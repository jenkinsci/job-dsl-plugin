const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = merge(common, {
  mode: "production",
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "src/main/json/embedded/config.json" },
      ],
    }),
  ],
  output: {
    path: path.join(__dirname, "src/main/webapp/api-viewer"),
  },
});
