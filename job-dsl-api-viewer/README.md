# Job DSL API Viewer

## Development Setup 
1. From the project root, run `./gradlew generateApiDoc` to create the JSON file.
2. From the project root, run `./gradlew gulp_build` to build the project resources.
3. From the `job-dsl-api-viewer` directory, run `gulp connect` to start a server. This will also watch for file changes.

## Publishing in gh-pages
1. From the project root, run `./gradlew :job-dsl-api-viewer:publishGhPages`
2. To publish to a fork, pass a githubUser param like `./gradlew -PgithubUser=sheehan :job-dsl-api-viewer:publishGhPages`