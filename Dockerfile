FROM ghcr.io/graalvm/native-image-community:25-muslib AS builder
WORKDIR /build
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve -B
COPY src/ src/
RUN ./mvnw -Pnative native:compile -DskipTests -B

FROM alpine:3
LABEL org.opencontainers.image.title="smart-tv-spoof"
LABEL org.opencontainers.image.description="Spoof smart TV connectivity checks to allow apps to work with DNS-level ad blocking"
LABEL org.opencontainers.image.source="https://github.com/acet/smart-tv-spoof"
LABEL org.opencontainers.image.licenses="MIT"
LABEL org.opencontainers.image.authors="acet (https://github.com/acet)"
RUN adduser -D -h /app appuser
WORKDIR /app
COPY LICENSE .
COPY --from=builder /build/target/smart-tv-spoof .
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD wget -qO- http://localhost:8080/Public/network/files/check.xml || exit 1
ENTRYPOINT ["./smart-tv-spoof"]