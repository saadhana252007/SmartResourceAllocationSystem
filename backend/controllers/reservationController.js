const Reservation = require("../models/Reservation");

const Resource = require("../models/Resource");

const {promoteWaitlistedReservations} = require("../services/allocationService");

const moment = require("moment-timezone");

const createReservation = async (req, res) => {

    try {

        const resource = await Resource.findById(
            req.body.requestedResource
        );

        if (!resource) {

            return res.status(404).json({
                success: false,
                message: "Resource not found"

            });

        }
        if (
    resource.resourceType === "QUANTITY_BASED" &&
    req.body.allocationPreference === "ALTERNATE_RESOURCE"
) {
    return res.status(400).json({
        success: false,
        message:
            "Alternate Resource is not allowed for quantity-based resources."
    });
}

        const bookingDate = moment
    .tz(req.body.date, "YYYY-MM-DD", "Asia/Kolkata")
    .startOf("day")
    .toDate();

        const openTime = new Date(bookingDate);

        openTime.setHours(
            openTime.getHours() -
            resource.bookingOpenBeforeHours
        );

        const closeTime = new Date(openTime);

        closeTime.setHours(
            closeTime.getHours() +
            resource.bookingWindowDurationHours
        );

        const now = new Date();

        if (now < openTime) {

            return res.status(400).json({
                success: false,
                message:
                    "Booking window has not opened yet."

            });

        }

        if (now > closeTime) {

            return res.status(400).json({
                success: false,
                message:
                    "Booking window has already closed."

            });

        }

        const startParts =
            req.body.startTime.split(":");

        const startMinutes =
            parseInt(startParts[0],10) * 60 +
            parseInt(startParts[1],10);

        const endMinutes =
            startMinutes +
            (req.body.durationHours * 60);


        const openingParts =
            resource.workingStartTime.split(":");

        const openingMinutes =
            parseInt(openingParts[0],10) * 60 +
            parseInt(openingParts[1],10);

        if (startMinutes < openingMinutes) {

            return res.status(400).json({
                success: false,
                message:
                `Resource opens at ${resource.workingStartTime}. Please choose a later start time.`

            });

        }


        const closingParts =
            resource.workingEndTime.split(":");

        const closingMinutes =
            parseInt(closingParts[0],10) * 60 +
            parseInt(closingParts[1],10);

        if (endMinutes > closingMinutes) {

            return res.status(400).json({
                success: false,
                message:
                `Resource closes at ${resource.workingEndTime}. Please choose a shorter duration or earlier start time.`

            });

        }

        if (

            resource.resourceType ===
            "CAPACITY_BASED" &&

            req.body.participantCount >
            resource.capacity

        ) {

            return res.status(400).json({
                success: false,
                message:
                `Participant count cannot exceed resource capacity (${resource.capacity}).`

            });

        }


        if (

            resource.resourceType ===
            "QUANTITY_BASED" &&

            req.body.quantityRequired >
            resource.availableUnits

        ) {

            return res.status(400).json({
                success: false,
                message:
                `Only ${resource.availableUnits} units are available.`

            });

        }

        const reservation =
            await Reservation.create({

                ...req.body,

                date: bookingDate,

                user: req.user.id,

                resourceCategory:
                    resource.category

            });

        res.status(201).json({
            success: true,
            reservation
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

const getAllReservations = async (req, res) => {

    try {

        const reservations = await Reservation.find()
            .populate("user", "-password")
            .populate("requestedResource")
            .populate("allocatedResource");

        res.status(200).json({
            success: true,
            reservations});

    } catch (error) {

        console.error(error);

return res.status(500).json({
    success: false,
    message: "Internal server error"
});

    }

};

const getMyReservations = async (
    req,
    res
) => {

    try {

        const reservations =
            await Reservation.find({

                user: req.user.id

            })

            .populate(
                "user",
                "name email role"
            )
            .populate("requestedResource")
            .populate("allocatedResource")
            .sort({
                createdAt: -1
            });

        res.status(200).json({
            success: true,
            reservations
    });

    } catch (error) {

        console.error(error);

return res.status(500).json({
    success: false,
    message: "Internal server error"
});

    }

};

const cancelReservation = async (
    req,
    res
) => {

    try {


        const reservation =
            await Reservation.findById(
                req.params.id
            );

        if (!reservation) {

            return res.status(404).json({
                success: false,
                message:
                    "Reservation not found"
            });

        }

        if (
            reservation.user.toString() !==
            req.user.id
        ) {

            return res.status(403).json({
                success: false,
                message:"You can cancel only your reservations"
            });

        }

        const resourceId =
            reservation.allocatedResource ||
            reservation.requestedResource;

        const bookingDate =
            reservation.date;

        const unitsFreed =
            reservation.quantityRequired || 1;

        reservation.status =
            "CANCELLED";

        reservation.allocatedResource =
            null;
  

        await reservation.save();


        await promoteWaitlistedReservations(

            resourceId,

            bookingDate,

            unitsFreed

        );
       

        res.status(200).json({
            success: true,
            message:
                "Reservation cancelled successfully"

        });

    } catch (error) {

        console.error(error);

return res.status(500).json({
    success: false,
    message: "Internal server error"
});

    }

};

const getReservationById = async (
    req,
    res
) => {

    try {

        const reservation =
            await Reservation.findById(
                req.params.id
            )

            .populate(
                "requestedResource"
            )

            .populate(
                "allocatedResource"
            )

            .populate(
                "user",
                "-password"
            );

        if (!reservation) {

            return res.status(404).json({
                success: false,
                message:
                    "Reservation not found"
            });

        }

        res.status(200).json({
            success: true,
            reservation
    });

    } catch (error) {

        console.error(error);

return res.status(500).json({
    success: false,
    message: "Internal server error"
});

    }

};

const getReservationsForMyResources = async (
    req,
    res
) => {

    try {

        const resources =
            await Resource.find({

                createdBy: req.user.id

            });

        const resourceIds =
            resources.map(
                resource => resource._id
            );

        const reservations =
            await Reservation.find({

                requestedResource: {
                    $in: resourceIds
                }

            })

            .populate(
                "requestedResource"
            )

            .populate(
                "allocatedResource"
            )

            .populate(
                "user",
                "-password"
            )

            .sort({
                createdAt: -1
            });

        res.status(200).json({
            success: true,
            reservations
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
const updateReservation = async (req, res) => {

    try {

        const reservation =
            await Reservation.findById(
                req.params.id
            );

        if (!reservation) {

            return res.status(404).json({
                success: false,
                message: "Reservation not found"
            });

        }

        if (
            reservation.user.toString() !==
            req.user.id
        ) {

            return res.status(403).json({
                success: false,
                message: "You can update only your reservations"
            });

        }

        if (
            reservation.status !== "PENDING"
        ) {

            return res.status(400).json({
                success: false,
                message: "Only pending reservations can be edited"
            });

        }

        const resource =
            await Resource.findById(
                req.body.requestedResource
            );

        if (!resource) {

            return res.status(404).json({
                success: false,
                message: "Resource not found"
            });

        }
        if (
    resource.resourceType === "QUANTITY_BASED" &&
    req.body.allocationPreference === "ALTERNATE_RESOURCE"
) {
    return res.status(400).json({
        success: false,
        message:
            "Alternate Resource is not allowed for quantity-based resources."
    });
}

        const startParts =
            req.body.startTime.split(":");

        const startMinutes =
            parseInt(startParts[0],10) * 60 +
            parseInt(startParts[1],10);

        const endMinutes =
            startMinutes +
            (req.body.durationHours * 60);

        const openingParts =
            resource.workingStartTime.split(":");

        const openingMinutes =
            parseInt(openingParts[0],10) * 60 +
            parseInt(openingParts[1],10);

        if (startMinutes < openingMinutes) {

            return res.status(400).json({
                success: false,
                message:
                `Resource opens at ${resource.workingStartTime}. Please choose a later start time.`

            });

        }


        const closingParts =
            resource.workingEndTime.split(":");

        const closingMinutes =
            parseInt(closingParts[0],10) * 60 +
            parseInt(closingParts[1],10);

        if (endMinutes > closingMinutes) {

            return res.status(400).json({
                success: false,
                message:
                `Resource closes at ${resource.workingEndTime}. Please choose a shorter duration or earlier start time.`

            });

        }


        if (

            resource.resourceType ===
            "CAPACITY_BASED" &&

            req.body.participantCount >
            resource.capacity

        ) {

            return res.status(400).json({
                success: false,
                message:
                `Participant count cannot exceed resource capacity (${resource.capacity}).`

            });

        }


        if (

            resource.resourceType ===
            "QUANTITY_BASED" &&

            req.body.quantityRequired >
            resource.availableUnits

        ) {

            return res.status(400).json({
                success: false,
                message:
                `Only ${resource.availableUnits} units are available.`

            });

        }
       const bookingDate = moment
    .tz(req.body.date, "YYYY-MM-DD", "Asia/Kolkata")
    .startOf("day")
    .toDate();

        const updatedReservation =
            await Reservation.findByIdAndUpdate(

    req.params.id,

    {
        ...req.body,
        date: bookingDate
    },

    {
        new: true,
        runValidators: true
    }

);

        res.status(200).json({
            success: true,
            reservation: updatedReservation
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
module.exports = {
    createReservation,
    getAllReservations,//for admin
    getMyReservations,//for user
    cancelReservation,
    getReservationById,
    getReservationsForMyResources,
    updateReservation
};