const express = require("express");
const fileService = require("../services/fileService");
const fs = require("fs");
const path = require("path");
const mime = require("mime");
const app = express();

// Get all html files
exports.getHtmlFiles = (req, res) => {
  try {
    const relativePaths = fileService.listHtmlFiles();
    res.json(relativePaths);
  } catch (error) {
    console.error(error);
    res.status(500).send("Error reading files");
  }
};

// Download a html file
exports.downloadFile = (req, res) => {
  const relativePath = req.body.fileName;

  if (!relativePath || !relativePath.endsWith(".html")) {
    return res.status(400).send("Invalid file path");
  }

  try {
    const filePath = fileService.getFilePath(relativePath);

    if (!filePath) {
      return res.status(404).send("File not found");
    }

    console.log("This is the download URL", filePath);
    res.download(filePath);
  } catch (error) {
    console.error(error);
    res.status(500).send("Error downloading file");
  }
};
