const express = require("express");
const protect = require("../middleware/authMiddleware");

const router = express.Router();

const {
    registerUser,
    loginUser,
    getProfile,
    updateProfile,
    changePassword,
    forgotPassword,
    verifyOTP,
    resetPassword
} = require("../controllers/authController");

router.post("/login", loginUser);
router.post("/register", registerUser);
router.get("/profile", protect, getProfile);
router.put("/profile",protect,updateProfile);
router.put("/change-password",protect,changePassword);
router.post("/forgot-password",forgotPassword);
router.post("/verify-otp",verifyOTP);
router.post("/reset-password",resetPassword);

module.exports = router;