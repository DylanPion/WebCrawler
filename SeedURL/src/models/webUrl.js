const mongoose = require("mongoose");

const webUrlSchema = new mongoose.Schema({
  url: {
    type: String,
    required: true,
    validate: {
      validator: function (v) {
        try {
          new URL(v);
          return true;
        } catch (e) {
          return false;
        }
      },
      message: (props) => `${props.value} n'est pas une URL valide!`,
    },
  },
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model("WebUrl", webUrlSchema);
