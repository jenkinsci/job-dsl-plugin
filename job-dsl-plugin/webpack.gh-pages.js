const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = merge(common, {
  mode: "production",
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "src/main/json/static/config.json" },
        { from: "target/update-center.json" },
        { from: "target/versions/*.json", to: "[name][ext]" },
      ],
    }),
  ],
  output: {
    path: path.join(__dirname, "target/gh-pages"),
  },
});
