const express = require("express");
const protect = require("../middleware/authMiddleware");

const router = express.Router();

const {
    registerUser,
    loginUser,
    getProfile
} = require("../controllers/authController");

router.post("/login", loginUser);
router.post("/register", registerUser);
router.get("/profile", protect, getProfile);

module.exports = router;