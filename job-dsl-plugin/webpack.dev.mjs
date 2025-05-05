import { merge } from 'webpack-merge';
import common from './webpack.common.mjs';
import path from 'path';
import CopyPlugin from 'copy-webpack-plugin';

export default merge(common, {
  mode: "development",
  devtool: "inline-source-map",
  devServer: {
    static: "target/dev-api-viewer",
  },
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
    path: path.join(import.meta.dirname, "target/dev-api-viewer"),
  },
});
