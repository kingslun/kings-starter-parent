package infra

import (
	"os"

	"github.com/go-kratos/kratos/v2/log"
	"github.com/go-kratos/kratos/v2/middleware/tracing"
)

var logger = log.With(log.NewStdLogger(os.Stdout),
	"ts", log.DefaultTimestamp,
	"caller", log.DefaultCaller,
	"trace_id", tracing.TraceID(),
)
var loggers = map[string]log.Helper{}

func GetLogger(model string) log.Helper {
	if logger, ok := loggers[model]; ok {
		return logger
	}
	helper := log.NewHelper(log.With(logger, "module", model))
	loggers[model] = *helper
	return *helper
}
