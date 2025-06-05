const express = require("express");
const router = express.Router();
const kafkaController = require("../controllers/kafkaController");
const fileController = require("../controllers/fileController");

// Route POST /send-url
router.post("/api/send-url", kafkaController.sendMessage);

// Router GET /api/files
router.get("/api/files", fileController.getHtmlFiles);

// Router POST /api/files/download
router.post("/api/files/download", fileController.downloadFile);

module.exports = router;
