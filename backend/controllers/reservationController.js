const Reservation = require("../models/Reservation");

const {promoteWaitlistedReservations} = require("../services/allocationService");

const createReservation = async (req, res) => {

    try {

        const reservation =
            await Reservation.create({
                ...req.body,
                user: req.user.id
        });
        res.status(201).json(reservation);

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getAllReservations = async (req, res) => {

    try {

        const reservations = await Reservation.find()
            .populate("user", "-password")
            .populate("requestedResource")
            .populate("allocatedResource");

        res.status(200).json(reservations);

    } catch (error) {

        res.status(500).json({
            message: error.message
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

            .populate("requestedResource")
            .populate("allocatedResource")
            .sort({
                createdAt: -1
            });

        res.status(200).json(
            reservations
        );

    } catch (error) {

        res.status(500).json({
            message: error.message
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
                message:
                    "Reservation not found"
            });

        }

        if (
            reservation.user.toString() !==
            req.user.id
        ) {

            return res.status(403).json({
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

            message:
                "Reservation cancelled successfully"

        });

    } catch (error) {

        res.status(500).json({
            message:
                error.message
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
                message:
                    "Reservation not found"
            });

        }

        res.status(200).json(
            reservation
        );

    } catch (error) {

        res.status(500).json({
            message:
                error.message
        });

    }

};

const getReservationsForMyResources =
async (
    req,
    res
) => {

    try {

        const reservations =
            await Reservation.find()

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

        const filteredReservations =
            reservations.filter(
                reservation =>

                    reservation
                    .requestedResource
                    ?.createdBy
                    ?.toString()

                    ===

                    req.user.id
            );

        res.status(200).json(
            filteredReservations
        );

    } catch (error) {

        res.status(500).json({
            message:
                error.message
        });

    }

};
const updateReservation = async (
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
                message:
                    "Reservation not found"
            });

        }

        if (
            reservation.user.toString()
            !== req.user.id
        ) {

            return res.status(403).json({
                message:
                    "You can update only your reservations"
            });

        }

        if (
            reservation.status !==
            "PENDING"
        ) {

            return res.status(400).json({
                message:
                    "Only pending reservations can be edited"
            });

        }

        const updatedReservation =
            await Reservation.findByIdAndUpdate(

                req.params.id,

                req.body,

                { new: true }

            );

        res.status(200).json(
            updatedReservation
        );

    } catch (error) {

        res.status(500).json({
            message:
                error.message
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