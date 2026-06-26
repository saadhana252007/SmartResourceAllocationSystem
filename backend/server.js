require("dotenv").config();

const express = require("express");
const cors = require("cors");

const authRoutes = require("./routes/authRoutes");
const resourceRoutes = require("./routes/resourceRoutes");
const reservationRoutes = require("./routes/reservationRoutes");
const bookingWindowRoutes = require("./routes/bookingWindowRoutes");
const allocationRoutes = require("./routes/allocationRoutes");

const connectDB = require("./config/db");

connectDB();

const app = express();
app.use(cors());
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/resources", resourceRoutes);
app.use("/api/reservations",reservationRoutes);
app.use("/api/booking-windows",bookingWindowRoutes);
app.use("/api/allocation",allocationRoutes);


app.get("/", (req, res) => {
    res.send("Smart Resource Allocation API Running");
});

const PORT = 5000;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});