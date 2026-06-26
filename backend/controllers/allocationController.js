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

        res.status(200).json({

            message:
                "Allocation engine executed successfully"

        });

    } catch (error) {

        res.status(500).json({

            message:
                error.message

        });

    }

};

module.exports = {

    runAllocationEngine

};