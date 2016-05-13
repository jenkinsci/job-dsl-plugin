# Job DSL API Viewer

## Development Setup 
1. From the project root, run `./gradlew generateApiDoc` to create the JSON file.
2. From the project root, run `./gradlew :job-dsl-api-viewer:build` to build the project resources.
2. From the project root, run `./gradlew -t :job-dsl-api-viewer:concat` to watch for file changes.
3. Open `job-dsl-api-viewer/index.html` with the embedded web server of your IDE, e.g. click "Open in Browser" in the
   file's context menu in IntelliJ IDEA

## Publishing in gh-pages
1. From the project root, run `./gradlew :job-dsl-api-viewer:publishGhPages`
2. To publish to a fork, pass a githubUser param like `./gradlew -PgithubUser=sheehan :job-dsl-api-viewer:publishGhPages`