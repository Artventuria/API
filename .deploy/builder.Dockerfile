FROM goreleaser/goreleaser:v2.1.0
RUN apk add --no-cache git make build-base jq curl
