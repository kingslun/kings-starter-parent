package filter

import (
	"log"
	"net/http"

	khttp "github.com/go-kratos/kratos/v2/transport/http"
)

func AppendIPHeader() khttp.FilterFunc {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, req *http.Request) {
			w.Header().Set("X-Remote-Addr", req.Host)
			log.Printf("Incoming request from IP: %s", req.Host)
			next.ServeHTTP(w, req)
		})
	}
}
