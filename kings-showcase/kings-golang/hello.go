package main

import (
	"fmt"
	"html"
)

func main() {
	fmt.Println("hello world!")
	html.EscapeString("hello golang!")
}
