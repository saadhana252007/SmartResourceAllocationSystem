const express =
require("express");

const router =
express.Router();

const {
    runAllocationEngine
} = require(
    "../controllers/allocationController"
);

router.post(
    "/run/:date",
    runAllocationEngine
);

module.exports =
router;