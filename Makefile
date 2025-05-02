BINARY_NAME=artventuriaapi
DEV_CONFIG_PATH=src/main/resources/application-dev.properties
PROD_CONFIG_PATH=src/main/resources/application.properties

RM=rm -f

## Build directories
BUILD_DIR=target
BIN_DIR_LINUX=build/linux

### Linux
ARCH_LINUX64=amd64-linux
BIN_NAME_LINUX64=$(BINARY_NAME)_$(ARCH_LINUX64)

### Build info
VERSION=$(shell git describe --tags)
COMMIT=$(shell git rev-parse --short HEAD)
DATE=$(shell date +'%Y/%m/%d-%H:%M:%S')

### Maven settings
MVN=./mvnw
MVN_OPTS=-Drevision=$(VERSION) -Dcommit=$(COMMIT) -Dbuild.date=$(DATE)
MVN_PROFILE_DEV=-Pdev
MVN_PROFILE_PROD=-Pprod

.PHONY: all
all: help
.DEFAULT_GOAL := help

GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
WHITE  := $(shell tput -Txterm setaf 7)
CYAN   := $(shell tput -Txterm setaf 6)
RESET  := $(shell tput -Txterm sgr0)

.PHONY: help
help: ## Show this help
	@echo 'Usage:'
	@echo '  ${GREEN}make ${YELLOW}<target>${RESET}'
	@echo ''
	@echo 'Targets:'
	@grep -E '^[a-zA-Z_0-9-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\t${YELLOW}%-30s${CYAN}%s\n", $$1, $$2}'

.PHONY: run
run: ## Run the application in production mode
	$(MVN) spring-boot:run $(MVN_PROFILE_PROD) $(MVN_OPTS)

.PHONY: dev
dev: ## Run the application in development mode
	$(MVN) spring-boot:run $(MVN_PROFILE_DEV) $(MVN_OPTS)

.PHONY: clean
clean: ## Clean all build directories
	$(MVN) clean
	$(RM) -r $(BIN_DIR_LINUX)

.PHONY: build
build: ## Build for current OS
	$(MVN) package $(MVN_OPTS)

.PHONY: linux64
linux64: ## Build for linux (64 bits)
	@echo "Building for linux 64 bits"
	$(RM) $(BIN_DIR_LINUX)/*$(ARCH_LINUX64)*
	mkdir -p $(BIN_DIR_LINUX)
	$(MVN) package $(MVN_OPTS) -DskipTests
	cp $(BUILD_DIR)/*.jar $(BIN_DIR_LINUX)/$(BIN_NAME_LINUX64).jar

.PHONY: test
test: ## Execute all tests
	$(MVN) test

.PHONY: test_coverage
test_coverage: ## Execute all tests and show coverage
	$(MVN) verify

.PHONY: test_verbose
test_verbose: ## Execute all tests with verbose output
	$(MVN) test -X

.PHONY: deps
deps: ## Download dependencies
	$(MVN) dependency:resolve
	$(MVN) dependency:tree

.PHONY: lint
lint: ## Run linters
	$(MVN) checkstyle:check

.PHONY: megalint
megalint: ## Run MegaLinter with same config as CI
	docker run -v "$(shell pwd)":/tmp/lint oxsecurity/megalinter:v8

.PHONY: docs
docs: ## Generate API documentation
	$(MVN) javadoc:javadoc

.PHONY: openapi
openapi: ## Generate OpenAPI documentation
	$(MVN) spring-boot:run -Dspring-boot.run.arguments=--springdoc.api-docs.enabled=true,--springdoc.swagger-ui.path=/swagger-ui.html

.PHONY: migrate
migrate: ## Run all database migrations
	$(MVN) flyway:migrate $(MVN_OPTS)

.PHONY: migrate_clean
migrate_clean: ## Clean database and rerun all migrations
	$(MVN) flyway:clean $(MVN_OPTS)
	$(MVN) flyway:migrate $(MVN_OPTS)

.PHONY: migrate_info
migrate_info: ## Show migration information
	$(MVN) flyway:info $(MVN_OPTS)

.PHONY: migrate_validate
migrate_validate: ## Validate migrations
	$(MVN) flyway:validate $(MVN_OPTS)