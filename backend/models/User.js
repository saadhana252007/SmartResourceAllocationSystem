const mongoose = require("mongoose");

const userSchema = new mongoose.Schema(
{
    name: {
        type: String,
        required: true,
        trim: true
    },

    email: {
        type: String,
        required: true,
        unique: true,
        trim: true,
        lowercase: true,
        match: /^\S+@\S+\.\S+$/
},

    password: {
        type: String,
        required: true
    },
    resetOTP: {
        type: String,
        default: null
    },

    resetOTPExpiry: {
        type: Date,
        default: null
    },

    role: {
        type: String,
        enum: ["USER", "ADMIN"],
        default: "USER"
    }
},
{
    timestamps: true
}
);

module.exports = mongoose.model("User", userSchema);