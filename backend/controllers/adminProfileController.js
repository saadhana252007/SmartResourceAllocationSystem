const Resource = require("../models/Resource");
const Reservation = require("../models/Reservation");

const getAdminProfileSummary = async (req, res) => {

    try {

        const resources = await Resource.find({

            createdBy: req.user.id

        });

        const resourceIds = resources.map(

            resource => resource._id

        );

        const resourcesManaged =

            resources.length;

        const reservationsProcessed =

    await Reservation.countDocuments({

        requestedResource: {

            $in: resourceIds

        },

        status: {

            $in: [

                "APPROVED",

                "ALTERNATIVE_APPROVED",

                "REJECTED",

                "CANCELLED"

            ]

        }

    });

        const pendingRequests =

            await Reservation.countDocuments({

                requestedResource: {

                    $in: resourceIds

                },

                status: "PENDING"

            });

        const approvedReservations =

            await Reservation.countDocuments({

                requestedResource: {

                    $in: resourceIds

                },

                status: {

                    $in: [

                        "APPROVED",

                        "ALTERNATIVE_APPROVED"

                    ]

                }

            });

        const systemUtilization =

            reservationsProcessed === 0

                ? 0

                : Math.round(

                    approvedReservations

                    /

                    reservationsProcessed

                    *

                    100

                );

        res.status(200).json({

            success: true,

            resourcesManaged,

            reservationsProcessed,

            pendingRequests,

            systemUtilization

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

    getAdminProfileSummary

};