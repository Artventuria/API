// Package main
package main

import (
	"fmt"
	"time"
)

func main() {
	for {
		fmt.Println("Hello, World!")
		time.Sleep(30 * time.Second)
	}
}
