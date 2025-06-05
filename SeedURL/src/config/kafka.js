// Configuration de Kafka
module.exports = {
  brokers: [process.env.KAFKA_BROKER || "localhost:9092"],
  clientId: "SeedUrl",
  groupId: "crawler-group",
  topics: {
    url: "crawlers-url",
  },
};
