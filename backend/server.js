require("dotenv").config();

const express = require("express");
const cors = require("cors");

const connectDB = require("./config/db");

const authRoutes = require("./routes/authRoutes");
const resourceRoutes = require("./routes/resourceRoutes");
const reservationRoutes = require("./routes/reservationRoutes");
const allocationRoutes = require("./routes/allocationRoutes");
const uploadRoutes = require("./routes/uploadRoutes");
const analyticsRoutes = require("./routes/analyticsRoutes");
const adminProfileRoutes = require("./routes/adminProfileRoutes");

require("./scheduler/allocationScheduler");

connectDB();

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/resources", resourceRoutes);
app.use("/api/reservations", reservationRoutes);
app.use("/api/allocation", allocationRoutes);
app.use("/api/upload", uploadRoutes);
app.use("/api/analytics", analyticsRoutes);
app.use("/api/admin", adminProfileRoutes);

app.get("/", (req, res) => {
    res.send("Smart Resource Allocation API Running");
});

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});