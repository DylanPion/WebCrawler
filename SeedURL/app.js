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

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});

// Close the Kafka connection when the server is stopped
process.on("SIGINT", async () => {
  await disconnectKafka();
  process.exit(0);
});
