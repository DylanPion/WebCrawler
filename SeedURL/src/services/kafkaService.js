const WebUrl = require("../models/webUrl");

// Save the URL in the database
const saveUrl = async (url) => {
  const webUrl = new WebUrl({ url });
  await webUrl.save();
};

module.exports = { saveUrl };
