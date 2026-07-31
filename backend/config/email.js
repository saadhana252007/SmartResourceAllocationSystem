const nodemailer = require("nodemailer");

console.log("EMAIL_USER:", process.env.EMAIL_USER);
console.log("EMAIL_PASS length:", process.env.EMAIL_PASS?.length);

const transporter = nodemailer.createTransport({

    host: "smtp.gmail.com",

    port: 465,

    secure: true,

    connectionTimeout: 10000,

    greetingTimeout: 10000,

    socketTimeout: 10000,

    auth: {

        user: process.env.EMAIL_USER,

        pass: process.env.EMAIL_PASS

    }

});

module.exports = transporter;