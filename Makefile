BINARY_NAME=artventuriaapi
MAIN_PATH=./cmd/api
MAIN_FILE=${MAIN_PATH}/main.go
CONFIG_PATH=.config.dev.yaml

# Import path for injecting flags
FLAG_IMPORT_PATH=github.com/Artventuria/API/internal/flags

RM=rm -f

# Flags for compilation injection
FLAG_API_HOST = $(FLAG_INJECT) main.apiHost=$(API_URL)
FLAG_COMMIT = $(FLAG_INJECT) main.commit=$(COMMIT)
FLAG_DATE = $(FLAG_INJECT) main.date=$(BUILD_DATE)

## Entrypoint / executables
PATH_TO_GENERATE=./cmd/api

### Linux
BIN_DIR_LINUX = ./build/linux
ARCH_LINUX64 = amd64-linux
BIN_NAME_LINUX64 = $(BINARY_NAME)_$(ARCH_LINUX64)

### Windows
BIN_DIR_WIN = ./build/windows
ARCH_WIN32 = win32
ARCH_WIN64 = win64
BIN_NAME_WIN32 = $(BINARY_NAME)_$(ARCH_WIN32)
BIN_NAME_WIN64 = $(BINARY_NAME)_$(ARCH_WIN64)

### macOS
BIN_DIR_MACOS = ./build/macos
ARCH_MACOS64 = amd64-darwin
BIN_NAME_MACOS64 = $(BINARY_NAME)_$(ARCH_MACOS64)

### Build flags
.PHONY: DATE
FLAGS_DEF = -s -w
FLAG_INJECT = -X
VERSION=$(FLAG_INJECT) $(FLAG_IMPORT_PATH).Version=$(shell git describe --tags)
COMMIT=$(FLAG_INJECT) $(FLAG_IMPORT_PATH).Commit=$(shell git rev-parse --short HEAD)
DATE:=$(FLAG_INJECT) $(FLAG_IMPORT_PATH).Date=$(shell date +'%Y/%m/%d-%H:%M:%S')
ENV=$(FLAG_INJECT) $(FLAG_IMPORT_PATH).Env=PRODUCTION
MAIN_INJECTION=$(VERSION) $(COMMIT) $(DATE) $(ENV) $(API_HOST)

#OS = darwin linux windows
OS = linux

GOTEST=go test
COVERFLAG=-cover
VERBOSEFLAG=-v

.PHONY: all
all: help
.DEFAULT_GOAL := help

# Terminal color definitions
GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
WHITE  := $(shell tput -Txterm setaf 7)
CYAN   := $(shell tput -Txterm setaf 6)
RESET  := $(shell tput sgr0)

.PHONY: help
help:	## Show help
	@echo 'Usage:'
	@echo '  ${GREEN}make ${YELLOW}<target>${RESET}'
	@echo ''
	@echo 'Targets:'
	@grep -E '^[a-zA-Z_0-9-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "	${YELLOW}%-30s${CYAN}%s\n", $$1, $$2}'

.PHONY := run
run:  ## Run the application locally with development config
	go run ${MAIN_FILE} -config ${CONFIG_PATH}

.PHONY: clean_build
clean_build: ## Clean all build directories
	go clean
	rm -rf $(BIN_DIR_LINUX)
	rm -rf $(BIN_DIR_WIN)
	rm -rf $(BIN_DIR_MACOS)

########################################################################################################################
# Build for LINUX 64
########################################################################################################################
linux64: $(BIN_NAME_LINUX64) ## Build for Linux (64 bits)

$(BIN_NAME_LINUX64):
	@echo "Building Artventuria server for Linux 64 bits"
	$(RM) $(BIN_DIR_LINUX)/*$(ARCH_LINUX64)*
	env GO111MODULE=on GOOS=linux GOARCH=amd64 go build -v -o $(BIN_DIR_LINUX)/$(BIN_NAME_LINUX64) -ldflags='$(FLAGS_DEF) $(MAIN_INJECTION)' $(PATH_TO_GENERATE)

########################################################################################################################
# Build for WINDOWS
########################################################################################################################

windows32: $(BIN_NAME_WIN32) ## Build for Windows (32 bits)
windows64: $(BIN_NAME_WIN64) ## Build for Windows (64 bits)
.PHONY: windows
windows: $(BIN_NAME_WIN32) $(BIN_NAME_WIN64) ## Build for all Windows (32-64 bits)

$(BIN_NAME_WIN32):
	@echo "Building Artventuria server for Windows 32 bits"
	$(RM) $(BIN_DIR_WIN)/*$(ARCH_WIN32)*
	$(RM) $(PATH_TO_GENERATE)/*.syso
	env GOOS=windows GOARCH=386 go generate $(PATH_TO_GENERATE)
	env GO111MODULE=on GOOS=windows GOARCH=386 GO386=softfloat go build -v -o $(BIN_DIR_WIN)/$(BIN_NAME_WIN32).exe -ldflags='$(FLAGS_DEF) $(MAIN_INJECTION)' $(PATH_TO_GENERATE)
	$(RM) $(PATH_TO_GENERATE)/*.syso

$(BIN_NAME_WIN64):
	@echo "Building Artventuria server for Windows 64 bits"
	$(RM) $(BIN_DIR_WIN)/*$(ARCH_WIN64)*
	$(RM) $(PATH_TO_GENERATE)/*.syso
	env GOOS=windows GOARCH=amd64 go generate $(PATH_TO_GENERATE)
	env GO111MODULE=on GOOS=windows GOARCH=amd64 go build -v -o $(BIN_DIR_WIN)/$(BIN_NAME_WIN64).exe -ldflags='$(FLAGS_DEF) $(MAIN_INJECTION)' $(PATH_TO_GENERATE)
	$(RM) $(PATH_TO_GENERATE)/*.syso

########################################################################################################################
# Build for MACOS
########################################################################################################################
macos64: $(BIN_NAME_MACOS64) ## Build for macOS (64 bits)

$(BIN_NAME_MACOS64):
	@echo "Building Artventuria server for macOS 64 bits"
	$(RM) $(BIN_DIR_MACOS)/*$(ARCH_MACOS64)*
	env GO111MODULE=on GOOS=darwin GOARCH=amd64 go build -v -o $(BIN_DIR_MACOS)/$(BIN_NAME_MACOS64) -ldflags='$(FLAGS_DEF) $(MAIN_INJECTION)' $(PATH_TO_GENERATE)

# Tests and code coverage
.PHONY: test
test: ## Run all tests
	$(GOTEST)

.PHONY: test_cover
test_cover: ## Run all tests with coverage
	$(GOTEST) $(COVERFLAG)

.PHONY: test_verbose
test_verbose: ## Run all tests with verbose output
	$(GOTEST) $(VERBOSEFLAG)

.PHONY: test_cover_verbose
test_cover_verbose: ## Run all tests with verbose output and coverage
	$(GOTEST) $(COVERFLAG) $(VERBOSEFLAG)

# Linting and cleanup
.PHONY: lint ##
lint: mega_linter

.PHONY: mega_linter
mega_linter: ## Run MegaLinter for the project
	sudo npx mega-linter-runner

.PHONY: clean ##
clean: clean_mega_linter

.PHONY: clean_mega_linter
clean_mega_linter:  ## Remove all MegaLinter report files
	sudo rm -r megalinter-reports

# Swagger for API documentation
swag: ## Generate API documentation with Swagger
	swag init --parseDependency --parseInternal --parseDepth 2 -g ./internal/server/routes.go

open-swagger: ## Open API Swagger UI in the browser
	@echo "Opening Artventuria API Swagger UI in the browser"
	$(eval PORT := $(shell grep -Po '(?<=port: )\d+' ./$(CONFIG_PATH)))
	google-chrome-stable --new-window http://localhost:$(PORT)/swagger/index.html
