const multer = require("multer");

const cloudinary = require("../config/cloudinary");

const {
    CloudinaryStorage
} = require("multer-storage-cloudinary");

const storage = new CloudinaryStorage({

    cloudinary,

    params: {

        folder: "smart_resource_allocation",

        allowed_formats: [

            "jpg",

            "jpeg",

            "png"

        ]

    }

});

module.exports = multer({

    storage,

    limits: {

        fileSize: 5 * 1024 * 1024

    }

});