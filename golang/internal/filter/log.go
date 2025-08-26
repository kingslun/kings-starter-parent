package filter

import (
	"log"
	"net/http"
	"time"

	khttp "github.com/go-kratos/kratos/v2/transport/http"
)

func Timer() khttp.FilterFunc {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, req *http.Request) {
			start := time.Now()
			defer func() {
				log.Printf("Endpoint:%s Took:%dms", req.URL.Path, time.Since(start).Milliseconds())
			}()
			next.ServeHTTP(w, req)
		})
	}
}
