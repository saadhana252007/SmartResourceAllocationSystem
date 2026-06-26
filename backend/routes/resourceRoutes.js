const express = require("express");

const router = express.Router();

const protect = require("../middleware/authMiddleware");
const adminOnly = require("../middleware/adminMiddleware");

const {
    createResource,
    getAllResources,
    getResourcesByCategory,
    getRecommendedResources,
    getBookingStatus,
    getMyResources,
    updateResource,
    deleteResource,
    getResourceById
} = require("../controllers/resourceController");

router.post(
    "/",
    protect,
    adminOnly,
    createResource
);

router.get("/", getAllResources);
router.get("/recommendations",getRecommendedResources);
router.get("/booking-status",getBookingStatus);
router.get(
    "/my-resources",
    protect,
    adminOnly,
    getMyResources
);
router.put(
    "/:id",
    protect,
    adminOnly,
    updateResource
);

router.delete(
    "/:id",
    protect,
    adminOnly,
    deleteResource
);
router.get("/category/:category", getResourcesByCategory);

router.get(
    "/:id",
    getResourceById
);

module.exports = router;
