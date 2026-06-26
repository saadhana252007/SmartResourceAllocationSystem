const BookingWindow = require("../models/BookingWindow");

const createBookingWindow = async (req, res) => {

    try {

        const bookingWindow =
            await BookingWindow.create(req.body);

        res.status(201).json(bookingWindow);

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

const getAllBookingWindows = async (req, res) => {

    try {

        const bookingWindows =
            await BookingWindow.find();

        res.status(200).json(
            bookingWindows
        );

    } catch (error) {

        res.status(500).json({
            message: error.message
        });

    }

};

module.exports = {
    createBookingWindow,
    getAllBookingWindows
};