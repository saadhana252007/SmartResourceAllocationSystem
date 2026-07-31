const express = require("express");

const router = express.Router();

const {

    getAdminProfile,

    getAdminProfileSummary

} = require("../controllers/adminProfileController");

const authMiddleware =
    require("../middleware/authMiddleware");


const protect = require("../middleware/authMiddleware");
const adminOnly = require("../middleware/adminMiddleware");    



router.get(
    "/profile-summary",
    protect,
    adminOnly,
    getAdminProfileSummary
);




module.exports = router;