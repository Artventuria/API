FROM golang:1.22.5-alpine
LABEL version="1.0" author="Amine Elalame" title="Artventuria API"
ENV env=production
RUN apk add --no-cache libc6-compat
RUN mkdir /usr/project
WORKDIR /usr/project
COPY build/linux/artventuriaapi_amd64-linux ./artventuriaapi_amd64-linux
COPY .deploy/.config-empty.yaml ./.config.yaml
EXPOSE 7000
CMD ["./artventuriaapi_amd64-linux"]
