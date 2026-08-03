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
    resourceCategory: {
    type: String,
    required: true
},

    date: {
        type: Date,
        required: true
    },

    startTime: {
        type: String,
        required: true,
        trim: true,
        match: /^([01]\d|2[0-3]):([0-5]\d)$/
    },

    durationHours: {
        type: Number,
        required: true,
        min: 1
    },

    allocationPreference: {
      type: String,
      enum: [
        "SPECIFIC_RESOURCE",
        "ALTERNATE_RESOURCE",
        "ALTERNATE_TIME"
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
      default: 0,
      min: 0
    },

    participantCount: {
        type: Number,
        default: 1,
        min: 1
    },

    quantityRequired: {
      type: Number,
      default: 0,
      min: 0
    },

    purposeDescription: {
    type: String,
    required: true,
    trim: true,
    maxlength: 500
},

purposeScore: {
    type: Number,
    default: 0,
    min: 0,
    max: 100
},

purposeScoreStatus: {

    type: String,

    enum: [

        "PENDING",

        "COMPLETED",

        "FAILED"

    ],

    default: "PENDING"

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
    },
    alternativeStartTime: {
        type: String,
        default: null,
        trim: true,
        match: /^([01]\d|2[0-3]):([0-5]\d)$/
    },
    allocationType: {

    type: String,

    enum: [

        "REQUESTED",

        "ALTERNATE_RESOURCE",

        "ALTERNATE_TIME"

    ],

    default: "REQUESTED"

    },
    allocationProcessed: {
    type: Boolean,
    default: false
},
allocatedAt: {
    type: Date,
    default: null
},
},
{
    timestamps: true
}
);
reservationSchema.index({

    date: 1,

    status: 1

});

reservationSchema.index({

    requestedResource: 1

});

reservationSchema.index({

    user: 1

});

module.exports = mongoose.model(
    "Reservation",
    reservationSchema
);