const { Kafka } = require("kafkajs");
const { brokers, clientId } = require("../config/kafka");

const kafka = new Kafka({ clientId, brokers });
const producer = kafka.producer();

// Connexion au producteur Kafka
const connectKafka = async () => {
  try {
    await producer.connect();
    console.log("✅ Connexion au producteur Kafka réussie");
  } catch (error) {
    console.error(
      "❌ Erreur lors de la connexion au producteur Kafka :",
      error
    );
    throw error;
  }
};

// Envoi d'un message au producteur Kafka
const sendMessage = async (message) => {
  try {
    await producer.send({
      topic: "crawlers-url",
      messages: [{ value: String(message.body.url) }],
    });
  } catch (error) {
    console.error("❌ Erreur lors de l'envoi du message Kafka :", error);
  }
};

// Déconnexion du producteur Kafka
const disconnectKafka = async () => {
  try {
    await producer.disconnect();
  } catch (error) {
    console.error(
      "❌ Erreur lors de la déconnexion du producteur Kafka :",
      error
    );
    throw error;
  }
};

module.exports = { connectKafka, disconnectKafka, sendMessage };
