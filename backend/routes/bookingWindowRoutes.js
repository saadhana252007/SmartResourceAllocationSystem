const express = require("express");

const router = express.Router();

const {
    createBookingWindow,
    getAllBookingWindows
} = require("../controllers/bookingWindowController");

router.post("/", createBookingWindow);

router.get("/", getAllBookingWindows);

module.exports = router;