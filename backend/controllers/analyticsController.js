const Resource = require("../models/Resource");
const Reservation = require("../models/Reservation");
const mongoose = require("mongoose");

const getAnalytics = async (req, res) => {

    try {

        const resources = await Resource.find({
            createdBy: req.user.id
        });
    

        const resourceIds = resources.map(
            resource => resource._id
        );

        const totalResources =
            resources.length;

        const totalReservations =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                }

            });

        const pending =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "PENDING"

            });

        const approved =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "APPROVED"

            });

        const alternativeApproved =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "ALTERNATIVE_APPROVED"

            });

        const waitlisted =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "WAITLISTED"

            });

        const rejected =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "REJECTED"

            });

        const cancelled =
            await Reservation.countDocuments({

                requestedResource: {
                    $in: resourceIds
                },

                status: "CANCELLED"

            });

        const reservationStatus = {

            PENDING: pending,

            APPROVED: approved,

            ALTERNATIVE_APPROVED: alternativeApproved,

            WAITLISTED: waitlisted,

            REJECTED: rejected,

            CANCELLED: cancelled

        };

        const categoryUsage =
            await Resource.aggregate([

                {

                    $match: {

                        createdBy: new mongoose.Types.ObjectId(req.user.id)

                    }

                },

                {

                    $group: {

                        _id: "$category",

                        count: {
                            $sum: 1
                        }

                    }

                }

            ]);

     

        const topResources =
            await Reservation.aggregate([

                {

                    $match: {

                        requestedResource: {

                            $in: resourceIds

                        },

                        status: {

                            $in: [

                                "APPROVED",

                                "ALTERNATIVE_APPROVED"

                            ]

                        }

                    }

                },

                {

                    $group: {

                        _id: "$requestedResource",

                        reservations: {

                            $sum: 1

                        }

                    }

                },

                {

                    $sort: {

                        reservations: -1

                    }

                },

                {

                    $limit: 5

                }

            ]);

        await Resource.populate(

            topResources,

            {

                path: "_id",

                select: "name category location"

            }

        );

        const reservationTrend =
            await Reservation.aggregate([

                {

                    $match: {

                        requestedResource: {

                            $in: resourceIds

                        }

                    }

                },

                {

                    $group: {

                        _id: "$date",

                        reservations: {

                            $sum: 1

                        }

                    }

                },

                {

                    $sort: {

                        _id: 1

                    }

                }

            ]);

        const utilization =
            await Reservation.aggregate([

                {

                    $match: {

                        requestedResource: {

                            $in: resourceIds

                        }

                    }

                },

                {

                    $group: {

                        _id: "$requestedResource",

                        reservations: {

                            $sum: 1

                        }

                    }

                }

            ]);

        await Resource.populate(

            utilization,

            {

                path: "_id",

                select: "name"

            }

        );

        const averageDuration =
            await Reservation.aggregate([

                {

                    $match: {

                        requestedResource: {

                            $in: resourceIds

                        }

                    }

                },

                {

                    $group: {

                        _id: null,

                        average: {

                            $avg: "$durationHours"

                        }

                    }

                }

            ]);

        const mostReservedResource =

            topResources.length > 0

                ? topResources[0]._id.name

                : "N/A";

        const sortedUtilization =
            [...utilization].sort(
                (a, b) => b.reservations - a.reservations
            );

        const highestUtilization =
            sortedUtilization.length > 0
            ? sortedUtilization[0]
            : null;

        const leastUtilization =
            sortedUtilization.length > 0
            ? sortedUtilization[
            sortedUtilization.length - 1
        ]
        : null;

        const peakDay =

            reservationTrend.length > 0

                ? [...reservationTrend].sort(

                    (a, b) =>

                        b.reservations - a.reservations

                )[0]

                : null;

        res.status(200).json({
            success: true,

            totalResources,

            totalReservations,

            pending,

            approved,

            alternativeApproved,

            waitlisted,

            rejected,

            cancelled,

            reservationStatus,

            categoryUsage,

            topResources,

            reservationTrend,

            utilization,

            insights: {

                mostReservedResource,

                highestUtilization,

                leastUtilization,

                peakReservationDay:
                    peakDay?._id,

                averageReservationDuration:
                    averageDuration[0]?.average || 0

            }

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

    getAnalytics

};