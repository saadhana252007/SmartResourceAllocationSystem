const Resource = require("../models/Resource");
const Reservation = require("../models/Reservation");

const moment = require("moment-timezone");

const createResource = async (req, res) => {

    try {

        const resource = await Resource.create({
            ...req.body,
            createdBy: req.user.id
        });

        res.status(201).json({
            success: true,
            resource});

    } catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};

const getAllResources = async (req, res) => {

    try {

        const resources = await Resource.find();

        res.status(200).json({
            success: true,
            resources});

    } catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};

const getResourcesByCategory = async (req, res) => {

    try {

        const category = req.params.category;

        const resources = await Resource.find({category: category});

        res.status(200).json({
            success:true,
            resources});

    } catch (error) {

    console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

}
};

const getRecommendedResources = async (req, res) => {

    try {

        const { category, participantCount } = req.query;

        const resources = await Resource.find({
            category,
            resourceType: "CAPACITY_BASED",
            capacity: { $gte: participantCount }
        });

        resources.sort(
            (a, b) => a.capacity - b.capacity
        );

        res.status(200).json({
            success: true,
            resources});

    } catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};

const getBookingStatus = async (req, res) => {
    console.log("******** getBookingStatus API HIT ********");

    try {


        const { resourceId, date } = req.query;

        const resource =
            await Resource.findById(resourceId);

        if (!resource) {

            return res.status(404).json({
                success: false,
                message: "Resource not found"
            });

        }

        const bookingDate = moment
    .tz(date, "YYYY-MM-DD", "Asia/Kolkata")
    .startOf("day");

const openTime = bookingDate
    .clone()
    .subtract(resource.bookingOpenBeforeHours, "hours");

const closeTime = openTime
    .clone()
    .add(resource.bookingWindowDurationHours, "hours");

const now = moment().tz("Asia/Kolkata");

console.log("==================================");
console.log("Received Date:", date);
console.log("Current Time:", now);
console.log("Timezone Offset:", now.getTimezoneOffset());
console.log("Booking Open Before:", resource.bookingOpenBeforeHours);
console.log("Booking Window Duration:", resource.bookingWindowDurationHours);
console.log("Booking Date:", bookingDate);
console.log("Open Time:", openTime);
console.log("Close Time:", closeTime);
console.log(
    "Hours Remaining:",
    (openTime.getTime() - now.getTime()) / (1000 * 60 * 60)
);
console.log("==================================");

if (now.isBefore(openTime)) {

    const hoursRemaining =
    Math.ceil(
        openTime.diff(now, "minutes") / 60
    );

    return res.status(200).json({
        status: "OPENS_SOON",
        hoursRemaining,
        message: "Booking window has not opened yet."
    });

}

        if (
    now.isSameOrAfter(openTime) &&
    now.isSameOrBefore(closeTime)
) {

            const requestsReceived =
                await Reservation.countDocuments({

                    requestedResource: resourceId,

                    date: bookingDate.toDate()

                });

            return res.status(200).json({

                status: "OPEN",

                requestsReceived,

                availableUnits:
                    resource.resourceType === "QUANTITY_BASED"
                        ? resource.availableUnits
                        : resource.capacity,

                message:
                    "Your request will be evaluated after the booking window closes."

            });

        }

        return res.status(200).json({

            status: "CLOSED",

            message:
                "Booking window has closed."

        });

    } catch (error) {

        console.error(error);

        return res.status(500).json({

            success: false,

            message: "Internal server error"

        });

    }

};const getMyResources = async (req, res) => {

    try {

        const resources =
            await Resource.find({
                createdBy: req.user.id
            });

        res.status(200).json({
            success: true,
            resources});

    } catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};

const updateResource = async (req, res) => {

    try {

        const resource =
            await Resource.findById(
                req.params.id
            );

        if (!resource) {

            return res.status(404).json({
                success: false,
                message: "Resource not found"
            });

        }

        if (
            resource.createdBy.toString()
            !== req.user.id
        ) {

            return res.status(403).json({
                success: false,
                message:
                    "You can update only your resources"
            });

        }

        const updatedResource =
            await Resource.findByIdAndUpdate(
                req.params.id,
                req.body,
                { new: true }
            );

        res.status(200).json({
            success: true, 
            updatedResource
    });

    } catch (error) {

    console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

}

};

const deleteResource = async (req, res) => {

    try {

        const resource = await Resource.findById(
            req.params.id
        );

        if (!resource) {

            return res.status(404).json({
                success: false,
                message: "Resource not found"
            });

        }

        if (
            resource.createdBy.toString() !== req.user.id
        ) {

            return res.status(403).json({
                success: false,
                message:
                    "You can delete only your resources"
            });

        }

        const today = new Date();

today.setHours(0, 0, 0, 0);


await Reservation.updateMany(
    {
        requestedResource: resource._id,
        date: { $gte: today },
        status: {
            $in: [
                "PENDING",
                "APPROVED",
                "WAITLISTED",
                "ALTERNATIVE_APPROVED"
            ]
        }
    },
    {
        $set: {
            status: "CANCELLED"
        }
    }
);


await Reservation.updateMany(
    {
        requestedResource: resource._id,
        date: { $lt: today }
    },
    {
        $set: {}
    }
);

        await Resource.findByIdAndDelete(
            resource._id
        );

        res.status(200).json({
            success: true,
            message:
                "Resource deleted successfully. Related reservations have been cancelled."

        });

    }

    catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};

const getResourceById = async (
    req,
    res
) => {

    try {

        const resource = await Resource.findById(req.params.id);

        if (!resource) {

            return res.status(404).json({
                success: false,
                message:
                    "Resource not found"
            });

        }

        res.status(200).json({
            success: true,
            resource
    });

    } catch (error) {

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};
const getMyResourcesByCategory = async (req,res)=>{

    try{

        const resources =

            await Resource.find({

                createdBy:req.user.id,

                category:req.params.category

            });

        res.status(200).json({
            success: true,
            resources});

    }

    catch(error){

        console.error(error);

return res.status(500).json({

    success: false,

    message: "Internal server error"

});

    }

};



module.exports = {
    createResource,
    getAllResources,
    getResourcesByCategory,
    getRecommendedResources,
    getBookingStatus,
    getMyResources,
    updateResource,
    deleteResource,
    getResourceById,
    getMyResourcesByCategory
};