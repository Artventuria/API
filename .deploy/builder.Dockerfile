# builder.Dockerfile
FROM golang:1.22.5-alpine AS builder
RUN apk add --no-cache git make build-base jq curl
WORKDIR /usr/src/app
COPY . .
RUN make linux64
