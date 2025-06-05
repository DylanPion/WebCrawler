const mongoose = require("mongoose");
const dotenv = require("dotenv");
dotenv.config();

const MONGODB_URI = process.env.MONGODB_URI;

// Connect to MongoDB
const connectDB = async () => {
  try {
    await mongoose.connect(MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("✅ Connexion à MongoDB réussie");
  } catch (err) {
    console.error("❌ Erreur de connexion à MongoDB :", err);
    process.exit(1); // Stop the process if connection fails
  }
};

module.exports = connectDB;
