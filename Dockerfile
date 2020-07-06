FROM gcr.io/distroless/java:11

WORKDIR /app
COPY target/lib /app/lib
COPY target/szpp-backend-runner.jar /app

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=90"

USER 65534

CMD ["szpp-backend-runner.jar"]
