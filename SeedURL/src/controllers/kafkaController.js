const { isValidUrl } = require("../utils/urlValidator");
const { sendMessage } = require("../kafka/producer");

exports.sendMessage = async (req, res) => {
  if (!isValidUrl(url)) {
    return res.status(400).send("URL invalide");
  }

  try {
    await sendMessage(req.body.url);
    res.status(200).json({ message: "Message envoyé à Kafka" });
  } catch (error) {
    console.error("Erreur :", error);
    res.status(500).send("Erreur lors de l'envoi du message à Kafka");
  }
};

module.exports = { sendMessage };
