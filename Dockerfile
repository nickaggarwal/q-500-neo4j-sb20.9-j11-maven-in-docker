FROM maven:3.5.2-jdk-8-alpine AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

EXPOSE 8080

RUN apk add --update openssl

# Build the app
RUN wget https://codejudge-starter-repo-artifacts.s3.ap-south-1.amazonaws.com/backend-project/springboot/maven/2.x/build.sh
RUN chmod 775 ./build.sh
RUN sh build.sh

# Add extra docker commands here (if any)...

# Run the app
RUN wget https://codejudge-starter-repo-artifacts.s3.ap-south-1.amazonaws.com/backend-project/springboot/maven/2.x/run.sh
RUN chmod 775 ./run.sh
CMD sh run.sh