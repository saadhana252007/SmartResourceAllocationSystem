const jwt = require("jsonwebtoken");

const protect = (req, res, next) => {

    let token;

    if (
        req.headers.authorization &&
        req.headers.authorization.startsWith("Bearer ")
    ) {

        try {

            if (!process.env.JWT_SECRET) {

                throw new Error(
                    "JWT_SECRET is not configured"
                );

            }

            token =
                req.headers.authorization.split(" ")[1];

            const decoded = jwt.verify(
                token,
                process.env.JWT_SECRET
            );

            req.user = decoded;

            return next();

        }

        catch (error) {

            return res.status(401).json({

                message:
                    "Invalid or expired token"

            });

        }

    }

    return res.status(401).json({

        message:
            "No token provided"

    });

};

module.exports = protect;