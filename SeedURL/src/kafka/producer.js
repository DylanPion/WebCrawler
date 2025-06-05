const { Kafka } = require("kafkajs");
const { brokers, clientId } = require("../config/kafka");

const kafka = new Kafka({
  clientId,
  brokers: brokers || process.env.KAFKA_BROKER,
});
const producer = kafka.producer();

// Connect to Kafka Producer
const connectKafka = async () => {
  try {
    await producer.connect();
    console.log("✅ Kafka Producer connected");
  } catch (error) {
    console.error("❌ Error connecting to Kafka Producer :", error);
    throw error;
  }
};

// Send a message to Kafka Producer
const sendMessage = async (message) => {
  try {
    await producer.send({
      topic: "crawlers-url",
      messages: [{ value: String(message.body.url) }],
    });
  } catch (error) {
    console.error("❌ Error sending message to Kafka :", error);
  }
};

// Disconnect from Kafka Producer
const disconnectKafka = async () => {
  try {
    await producer.disconnect();
  } catch (error) {
    console.error("❌ Error disconnecting from Kafka Producer :", error);
    throw error;
  }
};

module.exports = { connectKafka, disconnectKafka, sendMessage };
