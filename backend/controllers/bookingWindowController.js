const BookingWindow = require("../models/BookingWindow");

const createBookingWindow = async (req, res) => {

    try {

        const bookingWindow =
            await BookingWindow.create(req.body);

        return res.status(201).json({

    success: true,

    bookingWindow

});

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

        return res.status(200).json({

    success: true,

    bookingWindows

});

    } catch (error) {

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};

module.exports = {
    createBookingWindow,
    getAllBookingWindows
};