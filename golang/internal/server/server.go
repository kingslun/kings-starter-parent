package server

import (
	"context"

	appV1 "github.com/kings/golang/pkg/apps/v1"
	userV1 "github.com/kings/golang/pkg/users/v1"
)

type server struct {
}

var Server = &server{}

// GetApp
//
//	@Tags			应用管理
//	@Summary		单应用查询
//	@Description	AppName 是唯一标识符
//	@Param			obj	query		appV1.GetAppReq	false	"params"
//	@Success		200	{object}	appV1.GetAppResp
//	@Success		500	{object}	kratos.Error
//	@Router			/apps/{name} [get]
//	@Security		BearerToken
func (a *server) GetApp(context.Context, *appV1.GetAppReq) (*appV1.GetAppResp, error) {
	return &appV1.GetAppResp{
		Name: "kings",
		Repo: "https://github.com/kings",
		Lang: "golang",
	}, nil
}
func (a *server) GetUser(context.Context, *userV1.GetUserReq) (*userV1.GetUserResp, error) {
	return &userV1.GetUserResp{
		Name:  "kings",
		Phone: "15021261772",
	}, nil
}
