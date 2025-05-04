import { defineConfig } from 'eslint/config';
import js from '@eslint/js';
import globals from 'globals';

export default defineConfig([
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
