const mongoose = require("mongoose");

const reservationSchema = new mongoose.Schema(
{
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },

    requestedResource: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Resource",
        required: true
    },

    date: {
        type: Date,
        required: true
    },

    startTime: {
        type: String,
        required: true
    },

    durationHours: {
        type: Number,
        required: true
    },

    allocationPreference: {
      type: String,
      enum: [
        "SPECIFIC_RESOURCE",
        "ALTERNATE_RESOURCE",
        "ALTERNATE_TIME",
        "ALTERNATE_RESOURCE_AND_TIME"
      ],
      required: true
    },
    allocatedResource: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Resource",
      default: null
    },
    
    score: {
      type: Number,
      default: 0
    },

    participantCount: {
        type: Number,
        default: 1
    },

    quantityRequired: {
      type: Number,
      default: 0
    },

    purpose: {
      type: String,
      enum: [
        "Academic",
        "Research",
        "Project Work",
        "Club Activity",
        "Personal"
      ],
      required: true
    },

    status: {
        type: String,
        enum: [
            "PENDING",
            "APPROVED",
            "WAITLISTED",
            "REJECTED",
            "ALTERNATIVE_APPROVED",
            "CANCELLED"
        ],
        default: "PENDING"
    }
},
{
    timestamps: true
}
);

module.exports = mongoose.model(
    "Reservation",
    reservationSchema
);