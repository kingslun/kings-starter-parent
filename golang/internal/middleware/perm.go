package middleware

import (
	"context"

	"github.com/go-kratos/kratos/v2/errors"
	"github.com/go-kratos/kratos/v2/middleware"
	"github.com/go-kratos/kratos/v2/transport"
	"github.com/kings/golang/internal/infra"
)

var logger = infra.GetLogger("Perm")

func Perm() middleware.Middleware {
	return func(handler middleware.Handler) middleware.Handler {
		return func(ctx context.Context, req interface{}) (interface{}, error) {
			tr, ok := transport.FromServerContext(ctx)
			if !ok {
				return nil, errors.InternalServer("transport", "missing transport")
			}
			logger.Infof("Incoming request: %s", tr.Operation())
			// 示例：从 header 中获取 token
			token := tr.RequestHeader().Get("Authorization")
			if !validateToken(token) {
				return nil, errors.Unauthorized("unauthorized", "invalid token")
			}
			return handler(ctx, req)
		}
	}
}
func validateToken(token string) bool {
	return token == "Bearer kings"
}
