// Script de test node test.js pour envoyer un message au topic crawlers-url

const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "crawlers-url",
  brokers: ["localhost:9092"], // adapte si besoin
});

const producer = kafka.producer();

const run = async () => {
  await producer.connect();
  await producer.send({
    topic: "crawlers-url",
    messages: [{ value: "test depuis node" }],
  });
  await producer.disconnect();
};

run().catch(console.error);
