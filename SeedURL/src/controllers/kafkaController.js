const { isValidUrl } = require("../utils/urlValidator");
const { sendMessage } = require("../kafka/producer");

// Send a message to Kafka
exports.sendMessage = async (req, res) => {
  if (!isValidUrl(url)) {
    return res.status(400).send("Invalid URL");
  }

  try {
    await sendMessage(req.body.url);
    res.status(200).json({ message: "Message sent to Kafka" });
  } catch (error) {
    console.error("Error :", error);
    res.status(500).send("Error sending message to Kafka");
  }
};

module.exports = { sendMessage };
