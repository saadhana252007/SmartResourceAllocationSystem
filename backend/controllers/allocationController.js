const Reservation =
require("../models/Reservation");

const Resource =
require("../models/Resource");

const {
    runAllocationWorkflow
} = require(
    "../services/allocationService"
);

const runAllocationEngine =
async (
    req,
    res
) => {

    try {

        const bookingDate =
            req.params.date;

        const reservations =

            await Reservation.find({

                date:
                    bookingDate,

                status:
                    "PENDING"

            })

            .populate(
                "requestedResource"
            );

        const resources =

            await Resource.find();

        await runAllocationWorkflow(

            reservations,

            resources

        );

        return res.status(200).json({

            success: true,

            message:
                "Allocation engine executed successfully"

        });

    }

    catch (error) {

        console.error(error);

        return res.status(500).json({

            success: false,

            message:
                "Internal server error"

        });

    }

};

module.exports = {

    runAllocationEngine

};