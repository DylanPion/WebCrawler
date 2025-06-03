const WebUrl = require("../models/webUrl");

// Sauvegarde l'URL dans la base de données
const saveUrl = async (url) => {
  const webUrl = new WebUrl({ url });
  await webUrl.save();
};

module.exports = { saveUrl };
