const mongoose = require("mongoose");

const bookingWindowSchema = new mongoose.Schema(
{
    category: {
        type: String,
        required: true
    },

    bookingDate: {
        type: Date,
        required: true
    },

    openTime: {
        type: Date,
        required: true
    },

    closeTime: {
        type: Date,
        required: true
    },

    isActive: {
        type: Boolean,
        default: true
    }
},
{
    timestamps: true
}
);

module.exports = mongoose.model(
    "BookingWindow",
    bookingWindowSchema
);