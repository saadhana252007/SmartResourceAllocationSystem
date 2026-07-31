const mongoose = require("mongoose");

const bookingWindowSchema = new mongoose.Schema(
{
    category: {
    type: String,
    required: true,
    enum: [
        "Meeting Room",
        "Laboratory Equipment",
        "Projector",
        "Sports Facility",
        "Study Area"
    ]
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
    },
    allocationProcessed: {
    type: Boolean,
    default: false
},
},
{
    timestamps: true
}
);
bookingWindowSchema.index({

    bookingDate: 1,

    category: 1

});

module.exports = mongoose.model(
    "BookingWindow",
    bookingWindowSchema
);