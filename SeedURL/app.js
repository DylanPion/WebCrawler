const express = require("express");
const connectDB = require("./src/config/db");
const { connectKafka, disconnectKafka } = require("./src/kafka/producer");
const router = require("./src/routes/route");

connectDB();
connectKafka();

const app = express();
const port = 3000;

app.use(express.json());

app.use("/", router);

// Démarrage du serveur
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});

// Fermeture de la connexion Kafka lors de l'arrêt du serveur
process.on("SIGINT", async () => {
  await disconnectKafka();
  process.exit(0);
});
