const mongoose = require("mongoose");

const resourceSchema = new mongoose.Schema(
{
    name: {
        type: String,
        required: true,
        trim: true
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
        type: String,
        trim: true
    },

    location: {
        type: String,
        trim: true
    },

    resourceType: {
        type: String,
        enum: ["CAPACITY_BASED", "QUANTITY_BASED"],
        required: true
    },

    capacity: {
    type: Number,
    default: 0,
    min: 0
},

availableUnits: {
    type: Number,
    default: 0,
    min: 0
},

bookingOpenBeforeHours: {
    type: Number,
    default: 72,
    min: 0
},

bookingWindowDurationHours: {
    type: Number,
    default: 24,
    min: 1
},

    workingStartTime: {
        type: String,
        required: true,
        default: "08:00",
        match: /^([01]\d|2[0-3]):([0-5]\d)$/
    },

    workingEndTime: {
        type: String,
        required: true,
        default: "20:00",
        match: /^([01]\d|2[0-3]):([0-5]\d)$/
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
resourceSchema.index({

    category: 1

});

resourceSchema.index({

    createdBy: 1

});

module.exports = mongoose.model("Resource", resourceSchema);