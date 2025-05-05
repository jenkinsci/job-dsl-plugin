import webpack from 'webpack';
import CssMinimizerPlugin from 'css-minimizer-webpack-plugin';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';

const exported = {
  entry: {
    app: "./src/main/js/app.js",
  },

  plugins: [
    new HtmlWebpackPlugin({
      title: "Jenkins Job DSL Plugin",
      template: "./src/main/html/index.html",
    }),
    new MiniCssExtractPlugin({
      filename: "[name].[contenthash].css",
      chunkFilename: "[id].[contenthash].css",
    }),
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
    }),
  ],

  module: {
    rules: [
      {
        test: /\.hbs$/i,
        loader: "handlebars-loader",
      },
      {
        test: /\.css$/i,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
        ],
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif)$/i,
        type: "asset/resource",
      },
      {
        test: /\.less$/i,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          "less-loader",
        ],
      },
      {
        test: /\.(woff|woff2|eot|ttf|otf)$/i,
        type: "asset/resource",
      },
    ],
  },

  optimization: {
    minimizer: [
      new CssMinimizerPlugin(),
    ],
  },

  output: {
    assetModuleFilename: "[name].[hash][ext][query]",
    clean: true,
    filename: "[name].[contenthash].js",
  }
};

export default exported;

export const {
  entry,
  plugins,
  module,
  optimization,
  output
} = exported;
