const {
    defineConfig,
} = require("eslint/config");
const js = require("@eslint/js");

const globals = require("globals");

module.exports = defineConfig([
    js.configs.recommended,
    {
        plugins: {
            js,
        },
        //files: [ "src/main/js/**/*"],
        languageOptions: {
            globals: {
                ...globals.node,
                ...globals.browser,
                "$": "readonly",
            },

            "ecmaVersion": 2022,
            "sourceType": "module",
        },

        "extends": ["js/recommended"],

        "rules": {
            "curly": "error",
        },
    },
    {
        ignores: [
            "src/main/webapp",
            "target",
        ],
    },
]);
