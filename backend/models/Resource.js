const mongoose = require("mongoose");

const resourceSchema = new mongoose.Schema(
{
    name: {
        type: String,
        required: true
    },

    category: {
        type: String,
        enum: [
        "Meeting Room",
        "Laboratory Equipment",
        "Projector",
        "Sports Facility",
        "Study Area"
        ],
        required: true
    },

    imageUrl: {
    type: String,
    default: ""
    },

    description: {
        type: String
    },

    location: {
        type: String
    },

    resourceType: {
        type: String,
        enum: ["CAPACITY_BASED", "QUANTITY_BASED"],
        required: true
    },

    capacity: {
        type: Number,
        default: 0
    },

    availableUnits: {
        type: Number,
        default: 0
    },
    bookingOpenBeforeHours: {
      type: Number,
      default: 72
    },

    bookingWindowDurationHours: {
      type: Number,
      default: 24
    },

    createdBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    }
},
{
    timestamps: true
}
);

module.exports = mongoose.model("Resource", resourceSchema);