const express = require("express");
const fs = require("fs");
const path = require("path");
const mime = require("mime");
const app = express();

const BASE_DIR = path.join(__dirname, "../CrawlerFile");

app.use(express.json());

app.get("/api/files", (req, res) => {
  try {
    const directories = fs
      .readdirSync(BASE_DIR, { withFileTypes: true })
      .filter((dirent) => dirent.isDirectory());

    let htmlFiles = [];

    directories.forEach((dirent) => {
      const dirPath = path.join(BASE_DIR, dirent.name);
      const files = fs
        .readdirSync(dirPath)
        .filter((file) => file.endsWith(".html"))
        .map((file) => path.join(dirPath, file));
      htmlFiles = htmlFiles.concat(files);
    });

    // Mapper pour n'afficher que la partie après 'CrawlerFile/'
    const relativePaths = htmlFiles.map((filePath) =>
      path.relative(BASE_DIR, filePath)
    );

    res.json(relativePaths); // Envoie les chemins "propres" au client
  } catch (error) {
    console.error(error);
    res.status(500).send("Erreur lors de la lecture des fichiers");
  }
});

// POST /api/files/download - Téléchargement d'un fichier .html
app.post("/api/files/download", (req, res) => {
  const relativePath = req.body.fileName;
  const filePath = BASE_DIR + "/" + relativePath;
  console.log(filePath);

  if (!relativePath || !relativePath.endsWith(".html")) {
    return res.status(400).send("Chemin de fichier invalide");
  }

  if (!fs.existsSync(filePath)) {
    return res.status(404).send("Fichier non trouvé");
  }

  console.log("Ceci est l'URL de téléchargement", filePath);

  try {
    res.download(filePath);
  } catch (error) {
    console.error(error);
    res.status(500).send("Erreur lors du téléchargement du fichier");
  }
});

// Démarrage du serveur
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Serveur démarré sur http://localhost:${PORT}`);
});

// URL De test : https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country/https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country.html

// URL du Front : https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country/https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country.html

// URL du Back : https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country/https%3A%2F%2Fappleid.apple.com%2Fchoose-your-country.html
