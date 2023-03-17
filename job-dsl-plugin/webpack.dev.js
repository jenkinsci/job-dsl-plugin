const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = merge(common, {
  mode: "development",
  devtool: "inline-source-map",
  devServer: {
    static: "./src/main/webapp/api-viewer",
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "../job-dsl-core/target/classes/javaposse/jobdsl/dsl/dsl.json" },
        { from: "src/main/json/development/config.json" },
        { from: "target/update-center.json" },
      ],
    }),
  ],
});
