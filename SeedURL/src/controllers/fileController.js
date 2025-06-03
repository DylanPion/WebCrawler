const express = require("express");
const fileService = require("../services/fileService");
const fs = require("fs");
const path = require("path");
const mime = require("mime");
const app = express();

exports.getHtmlFiles = (req, res) => {
  try {
    const relativePaths = fileService.listHtmlFiles();
    res.json(relativePaths);
  } catch (error) {
    console.error(error);
    res.status(500).send("Erreur lors de la lecture des fichiers");
  }
};

exports.downloadFile = (req, res) => {
  const relativePath = req.body.fileName;

  if (!relativePath || !relativePath.endsWith(".html")) {
    return res.status(400).send("Chemin de fichier invalide");
  }

  try {
    const filePath = fileService.getFilePath(relativePath);

    if (!filePath) {
      return res.status(404).send("Fichier non trouvé");
    }

    console.log("Ceci est l'URL de téléchargement", filePath);
    res.download(filePath);
  } catch (error) {
    console.error(error);
    res.status(500).send("Erreur lors du téléchargement du fichier");
  }
};
