module.exports = {
    "env": {
        "browser": true,
        "es2022": true
    },
    "extends": "eslint:recommended",
    "overrides": [
    ],
    "parserOptions": {
        "ecmaVersion": "2022",
        "sourceType": "module"
    },
    "rules": {
        "curly": "error",
    },
    "globals": {
        "$": "readonly",
    },
}
