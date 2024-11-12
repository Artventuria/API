# Dockerfile
FROM alpine:3.18
LABEL version="0.0.1" author="Amine Elalame" title="Artventuria API"
ENV env=env
RUN apk add --no-cache libc6-compat
WORKDIR /usr/project
COPY --from=artventuria-builder /usr/src/app/build/linux/artventuriaapi_amd64-linux ./artventuriaapi_amd64-linux
COPY .deploy/.config-empty.yaml ./.config.yaml
EXPOSE 7000
CMD ["./artventuriaapi_amd64-linux"]
