import { merge } from 'webpack-merge';
import common from './webpack.common.mjs';
import path from 'path';
import CopyPlugin from 'copy-webpack-plugin';

export default merge(common, {
  mode: "production",
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "src/main/json/embedded/config.json" },
      ],
    }),
  ],
  output: {
    path: path.join(import.meta.dirname, "src/main/webapp/api-viewer"),
  },
});
