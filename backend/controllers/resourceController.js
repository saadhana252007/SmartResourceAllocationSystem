const Resource = require("../models/Resource");
const Reservation = require("../models/Reservation");


const createResource = async (req, res) => {

    try {

        const resource = await Resource.create({
            ...req.body,
            createdBy: req.user.id
        });

        res.status(201).json(resource);

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getAllResources = async (req, res) => {

    try {

        const resources = await Resource.find();

        res.status(200).json(resources);

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getResourcesByCategory = async (req, res) => {

    try {

        const category = req.params.category;

        const resources = await Resource.find({
            category: category
        });

        res.status(200).json(resources);

    } catch (error) {

        res.status(500).json({
            message: error.message
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

        res.status(200).json(resources);

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getBookingStatus = async (req, res) => {

    try {

        const { resourceId, date } = req.query;

        const resource =
            await Resource.findById(resourceId);

        if (!resource) {
            return res.status(404).json({
                message: "Resource not found"
            });
        }

        const bookingDate = new Date(date);

        bookingDate.setHours(
            0, 1, 0, 0
        );

        const openTime =
            new Date(bookingDate);

        openTime.setHours(
            openTime.getHours()
            - resource.bookingOpenBeforeHours
        );

        const closeTime =
            new Date(openTime);

        closeTime.setHours(
            closeTime.getHours()
            + resource.bookingWindowDurationHours
        );

        const now = new Date();

        if (now < openTime) {

            const hoursRemaining =
                Math.ceil(
                    (openTime - now)
                    / (1000 * 60 * 60)
                );

            return res.status(200).json({
                status: "OPENS_SOON",
                hoursRemaining,
                message:"Booking window has not opened yet."
            });

        }

        if (
            now >= openTime &&
            now <= closeTime
        ) {

            const requestsReceived =
            await Reservation.countDocuments({
                requestedResource: resourceId,
                date: bookingDate
            });

            return res.status(200).json({
                status: "OPEN",

                requestsReceived,

            availableUnits:resource.resourceType ==="QUANTITY_BASED"? resource.availableUnits: resource.capacity,

            message:"Your request will be evaluated after the booking window closes."
            });

        }

       return res.status(200).json({
            status: "CLOSED",
            message:"Booking window has closed."
        });

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getMyResources = async (req, res) => {

    try {

        const resources =
            await Resource.find({
                createdBy: req.user.id
            });

        res.status(200).json(resources);

    } catch (error) {

        res.status(500).json({
            message: error.message
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
                message: "Resource not found"
            });

        }

        if (
            resource.createdBy.toString()
            !== req.user.id
        ) {

            return res.status(403).json({
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

        res.status(200).json(
            updatedResource
        );

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const deleteResource = async (req, res) => {

    try {

        const resource =
            await Resource.findById(
                req.params.id
            );

        if (!resource) {

            return res.status(404).json({
                message:
                    "Resource not found"
            });

        }

        if (
            resource.createdBy.toString()
            !== req.user.id
        ) {

            return res.status(403).json({
                message:
                    "You can delete only your resources"
            });

        }

        await Resource.findByIdAndDelete(
            req.params.id
        );

        res.status(200).json({
            message:
                "Resource deleted successfully"
        });

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getResourceById = async (
    req,
    res
) => {

    try {

        const resource =
            await Resource.findById(
                req.params.id
            );

        if (!resource) {

            return res.status(404).json({
                message:
                    "Resource not found"
            });

        }

        res.status(200).json(
            resource
        );

    } catch (error) {

        res.status(500).json({
            message:
                error.message
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
    getResourceById
};