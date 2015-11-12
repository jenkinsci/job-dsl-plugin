# 1. Build your Jenkins image
We provide a Jenkins container with those dsl dependencies out of the box. In order to compile that image please run the below command

```bash
docker build --rm=true -t dsl_example:latest .
```

# 2. Run your Jenkins containers locally
Once you have build your local image then run the container locally

```bash
docker run --name dsl_example -p 8080:8080 -v `pwd`/fs:/var/jenkins_home dsl_example:latest
```

# 3. Run the seed job

[[the tutorial|Tutorial - Using the Jenkins Job DSL]] section 3.

# Requirements
- [Docker](https://docs.docker.com/installation/)

# Further reading
- https://registry.hub.docker.com/_/jenkins/
