const cron = require("node-cron");

const Reservation = require("../models/Reservation");
const Resource = require("../models/Resource");
const moment = require("moment-timezone");

const {
    runAllocationWorkflow,
    rejectExpiredWaitlistedReservations
} = require("../services/allocationService");

function getBookingCloseTime(resource, bookingDate) {

    const bookingDay = moment
        .tz(bookingDate, "Asia/Kolkata")
        .startOf("day");

    return bookingDay
        .clone()
        .subtract(
            resource.bookingOpenBeforeHours,
            "hours"
        )
        .add(
            resource.bookingWindowDurationHours,
            "hours"
        );

}

cron.schedule("* * * * *", async () => {

    try {

        const start = Date.now();

console.log("================================");
console.log("Scheduler Started");

        console.log("Running Allocation Scheduler...");

        const now = moment().tz("Asia/Kolkata");

        const resources = await Resource.find().lean();

        for (const resource of resources) {

            try {

                console.log(`Checking ${resource.name}`);

                const reservations = await Reservation.find({

                    requestedResource: resource._id,

                    status: "PENDING",

                    allocationProcessed: false

                }).populate("requestedResource");

                if (reservations.length === 0)
                    continue;

const groupedReservations = {};

for (const reservation of reservations) {

    const bookingDate = moment
    .tz(reservation.date, "Asia/Kolkata");

    const closeTime = getBookingCloseTime(
        resource,
        bookingDate
    );

   const key =
    resource.category + "_" +
    bookingDate.format("YYYY-MM-DD") + "_" +
    closeTime.valueOf();

    if (!groupedReservations[key]) {

        groupedReservations[key] = {

            bookingDate,

            closeTime

        };

    }

}

                for (const key of Object.keys(groupedReservations)) {
                    const bookingDate =
    groupedReservations[key].bookingDate;

const closeTime =
    groupedReservations[key].closeTime;


                    console.log(
                        `Booking Date : ${bookingDate}`
                    );

                    console.log(
                        `Booking Close : ${closeTime}`
                    );

                    console.log(
    "NOW:",
    now.format("YYYY-MM-DD HH:mm:ss")
);

console.log(
    "Booking Open Before:",
    resource.bookingOpenBeforeHours
);

console.log(
    "Booking Window Duration:",
    resource.bookingWindowDurationHours
);

console.log(
    "OPEN:",
    closeTime
        .clone()
        .subtract(
            resource.bookingWindowDurationHours,
            "hours"
        )
        .format("YYYY-MM-DD HH:mm:ss")
);

console.log(
    "CLOSE:",
    closeTime.format("YYYY-MM-DD HH:mm:ss")
);

                    if (now.isBefore(closeTime)) {

                        console.log(
                            "Booking window still open."
                        );

                        continue;

                    }

                   const allReservations =
await Reservation.find({

    resourceCategory: resource.category,

   date: bookingDate.toDate(),

    status: "PENDING",

    allocationProcessed: false

}).populate("requestedResource");

const reservationsToProcess =
allReservations.filter(reservation => {

    const reservationCloseTime =
        getBookingCloseTime(

            reservation.requestedResource,

            reservation.date

        );

    return (

       reservationCloseTime.valueOf() ===
closeTime.valueOf()

    );

});

if (reservationsToProcess.length === 0) {

    continue;

}

                    console.log(
                        `Running allocation for ${reservationsToProcess.length} reservation(s)`
                    );

                    console.log(
    reservationsToProcess.map(r => ({
        resource: r.requestedResource.name,
        user: r.user,
        purpose: r.purposeDescription
    }))
);

                    const categoryResources = await Resource.find({
    category: resource.category
}).lean();

await runAllocationWorkflow(
    reservationsToProcess,
    categoryResources
);

                    await Reservation.updateMany(

                        {

                            _id: {

                                $in: reservationsToProcess.map(

                                    reservation => reservation._id

                                )

                            }

                        },

                        {

                            $set: {

                                allocationProcessed: true

                            }

                        }

                    );

                   console.log(
    `Allocation completed for ${key}`
);

                }

            }

            catch (error) {

                console.error(

                    `Scheduler failed for ${resource.name}`,

                    error

                );

            }

        }
        await rejectExpiredWaitlistedReservations();
        console.log(
    "Scheduler Finished in",
    Date.now() - start,
    "ms"
);

console.log("================================");

    }

    catch (error) {

        console.error(

            "Scheduler Error:",

            error

        );

    }

});