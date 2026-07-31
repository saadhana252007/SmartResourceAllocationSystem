const User = require("../models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const transporter = require("../config/email");

const generateToken = (user) => {

    return jwt.sign(

        {
            id: user._id,
            role: user.role
        },

        process.env.JWT_SECRET,

        {
            expiresIn: "1d"
        }

    );

};
const registerUser = async (req, res) => {

    try {

        const { name, email, password, role } = req.body;

        const existingUser = await User.findOne({ email });

        if (existingUser) {
            return res.status(400).json({
                success: false,
                message: "User already exists"
            });
        }

        if (password.length < 6) {

    return res.status(400).json({
    success: false,
    message: "Password must be at least 6 characters"
});

}

        const hashedPassword = await bcrypt.hash(password, 10);
        

        const user = await User.create({
          name,
          email,
          password: hashedPassword,
          role
        });

   const token = generateToken(user);

res.status(201).json({
    success: true,
    message: "User registered successfully",
    token,
    role: user.role,
    name: user.name,
    email: user.email,
    createdAt: user.createdAt
});

    } 
   catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const loginUser = async (req, res) => {

    try {

        const { email, password } = req.body;

        const user = await User.findOne({ email });

        if (!user) {
            return res.status(401).json({
                success: false,
                message: "Invalid credentials"
            });
        }

        const isMatch = await bcrypt.compare(
            password,
            user.password
        );

        if (!isMatch) {
            return res.status(401).json({
                success: false,
                message: "Invalid credentials"
            });
        }

        const token = generateToken(user);

        res.status(200).json({
            success: true,
            message: "Login successful",
            token,
            role: user.role,
            name: user.name,
            email: user.email,
            createdAt: user.createdAt
        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const getProfile = async (req, res) => {

    try {

        const user = await User.findById(req.user.id)
            .select("-password");

        if (!user) {

            return res.status(404).json({

                message: "User not found"

            });

        }

        res.status(200).json({

            success: true,

            user

        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const updateProfile = async (req, res) => {

    try {

        const { name, email } = req.body;

        const user = await User.findById(req.user.id);

        if (!user) {

            return res.status(404).json({
                success: false,
                message: "User not found"
            });

        }

                const existingUser = await User.findOne({
    email
});

if (
    existingUser &&
    existingUser._id.toString() !== user._id.toString()
) {

    return res.status(400).json({

        success: false,

        message: "Email already in use"

    });

}

        user.name = name;
        user.email = email;


        await user.save();

        res.status(200).json({

            success: true,

            message: "Profile updated successfully",

            user

        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const changePassword = async (req, res) => {

    try {

        const { currentPassword, newPassword } = req.body;

        const user = await User.findById(req.user.id);

        if (!user) {

            return res.status(404).json({
                success: false,
                message: "User not found"
            });

        }

        const isMatch = await bcrypt.compare(
            currentPassword,
            user.password
        );

        if (!isMatch) {

            return res.status(400).json({
                success: false,
                message: "Current password is incorrect"
            });

        }

        if(newPassword.length < 6){

    return res.status(400).json({
    success: false,
    message: "Password must be at least 6 characters"
});

}

        const hashedPassword = await bcrypt.hash(
            newPassword,
            10
        );

        user.password = hashedPassword;

        

        await user.save();

        res.status(200).json({

            success: true,

            message: "Password changed successfully"

        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const forgotPassword = async (req, res) => {

    try {

        const { email } = req.body;

        const user = await User.findOne({ email });

        if (!user) {

            return res.status(404).json({

                success: false,

                message: "User not found"

            });

        }

        const otp = Math.floor(

            100000 + Math.random() * 900000

        ).toString();

        user.resetOTP = otp;

        user.resetOTPExpiry = Date.now() + 5 * 60 * 1000;

        await user.save();

console.log("Skipping email sending for testing");

await Promise.race([

    transporter.sendMail({

        from: process.env.EMAIL_USER,

        to: email,

        subject: "Password Reset OTP",

        html: `
            <h2>Smart Resource Allocation System</h2>

            <p>Your OTP for password reset is:</p>

            <h1>${otp}</h1>

            <p>This OTP is valid for only 5 minutes.</p>
        `

    }),

    new Promise((_, reject) =>
        setTimeout(
            () => reject(new Error("Email timeout")),
            10000
        )
    )

]);

return res.status(200).json({

    success: true,

    message: "OTP sent successfully"

});

    } catch (error) {

    console.error("Forgot Password Error:", error);

    return res.status(500).json({

        success: false,

        message: error.message

    });

}

};
const verifyOTP = async (req, res) => {

    try {

        const { email, otp } = req.body;

        const user = await User.findOne({ email });

        if (!user) {

            return res.status(404).json({

                success: false,

                message: "User not found"

            });

        }

        if (user.resetOTP !== otp) {

            return res.status(400).json({

                success: false,

                message: "Invalid OTP"

            });

        }

        if (user.resetOTPExpiry < Date.now()) {

            return res.status(400).json({

                success: false,

                message: "OTP Expired"

            });

        }

        res.status(200).json({

            success: true,

            message: "OTP Verified"

        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};
const resetPassword = async (req, res) => {

    try {

        const { email, otp, newPassword } = req.body;

        const user = await User.findOne({ email });

        if (!user) {

            return res.status(404).json({

                success: false,

                message: "User not found"

            });

        }

        if (user.resetOTP !== otp) {

            return res.status(400).json({

                success: false,

                message: "Invalid OTP"

            });

        }

        if (user.resetOTPExpiry < Date.now()) {

            return res.status(400).json({

                success: false,

                message: "OTP Expired"

            });

        }

        if(newPassword.length < 6){

    return res.status(400).json({
    success: false,
    message: "Password must be at least 6 characters"
});

}

        user.password = await bcrypt.hash(newPassword, 10);

        user.resetOTP = null;
        user.resetOTPExpiry = null;

        await user.save();

        res.status(200).json({

            success: true,

            message: "Password Reset Successful"

        });

    } catch(error){

    console.error(error);

    return res.status(500).json({

        success: false,

        message: "Internal server error"

    });

}

};

module.exports = {
    registerUser,
    loginUser,
    getProfile,
    updateProfile,
    changePassword,
    forgotPassword,
    verifyOTP,
    resetPassword
};