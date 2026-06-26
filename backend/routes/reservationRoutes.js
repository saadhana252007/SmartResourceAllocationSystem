const express = require("express");

const router = express.Router();


const protect = require("../middleware/authMiddleware");
const {
    createReservation,
    getAllReservations,
    getMyReservations,
    cancelReservation,
    getReservationById,
    getReservationsForMyResources,
    updateReservation
} = require("../controllers/reservationController");

router.post(
    "/",
    protect,
    createReservation
);

router.put(
    "/cancel/:id",
    protect,
    cancelReservation
);

router.get("/", getAllReservations);

router.get(
    "/my-reservations",
    protect,
    getMyReservations
);
router.get(
    "/resource-owner",
    protect,
    getReservationsForMyResources
);
router.put(
    "/:id",
    protect,
    updateReservation
);
router.get(
    "/:id",
    protect,
    getReservationById
);
module.exports = router;