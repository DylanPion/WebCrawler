const mongoose = require("mongoose");

const connectDB = async () => {
  try {
    await mongoose.connect("mongodb://localhost:27017/seedurl", {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("✅ Connexion à MongoDB réussie");
  } catch (err) {
    console.error("❌ Erreur de connexion à MongoDB :", err);
    process.exit(1); // Arrête le processus si échec
  }
};

module.exports = connectDB;
