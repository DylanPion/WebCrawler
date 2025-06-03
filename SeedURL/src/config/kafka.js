// Configuration de Kafka
module.exports = {
  brokers: ["localhost:9092"],
  clientId: "SeedUrl",
  groupId: "crawler-group",
  topics: {
    url: "crawlers-url",
  },
};
