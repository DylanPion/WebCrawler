const fs = require("fs");
const path = require("path");

const dotenv = require("dotenv");
dotenv.config();

const BASE_DIR = path.join(__dirname, process.env.BASE_DIR);

// List all html files
exports.listHtmlFiles = () => {
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

  return htmlFiles.map((filePath) => path.relative(BASE_DIR, filePath));
};

// Get the file path
exports.getFilePath = (relativePath) => {
  const fullPath = path.join(BASE_DIR, relativePath);

  if (!fs.existsSync(fullPath)) {
    return null;
  }

  return fullPath;
};
