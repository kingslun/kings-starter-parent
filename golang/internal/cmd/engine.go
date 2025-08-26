package main

import (
	"github.com/go-kratos/kratos/v2"
	"github.com/go-kratos/kratos/v2/middleware/recovery"
	"github.com/go-kratos/kratos/v2/middleware/selector"
	"github.com/go-kratos/kratos/v2/middleware/tracing"
	"github.com/go-kratos/kratos/v2/transport/http"
	"github.com/kings/golang/internal/filter"
	"github.com/kings/golang/internal/infra"
	"github.com/kings/golang/internal/middleware"
	"github.com/kings/golang/internal/server"
	appV1 "github.com/kings/golang/pkg/apps/v1"
	userV1 "github.com/kings/golang/pkg/users/v1"
	_ "github.com/swaggo/swag"
)

func main() {
	serve := http.NewServer(
		http.Address(":8080"),
		http.Filter(filter.AppendIPHeader(), filter.Timer()),
		http.Middleware(
			recovery.Recovery(),
			tracing.Server(),
			selector.Server(middleware.Perm()).Prefix("/users.v1.UserService/GetUser").Build(),
		),
	)
	userV1.RegisterUserServiceHTTPServer(serve, server.Server)
	appV1.RegisterAppServiceHTTPServer(serve, server.Server)
	app := kratos.New(
		kratos.Name("golang"),
		kratos.Server(
			serve,
		),
	)
	logger := infra.GetLogger("HttpMiddleware")
	defer logger.Info("Kratos app stopped.")
	logger.Info("Starting Kratos app...")
	if err := app.Run(); err != nil {
		logger.Error("Run server error: %v", err)
		panic(err)
	}
}
