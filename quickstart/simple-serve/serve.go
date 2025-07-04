package main

import (
	"encoding/json"
	"fmt"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"log"
	"net/http"
)

// 定义一个 HTTP 请求计数器
var requestCounter = prometheus.NewCounterVec(
	prometheus.CounterOpts{
		Name: "http_requests_total",           // 指标名称
		Help: "Total number of HTTP requests", // 指标说明
	},
	[]string{"method", "endpoint", "params"}, // 标签维度
)

func init() {
	// 注册指标
	prometheus.MustRegister(requestCounter)
}

type Response[T any] struct {
	Code       string `json:"code"`
	ErrMessage string `json:"errMessage,omitempty"`
	Data       T      `json:"data"`
}

func OK[T any](d T) *Response[T] {
	return &Response[T]{Code: "OK", Data: d}
}
func Error(code, msg string) *Response[any] {
	return &Response[any]{Code: code, ErrMessage: msg}
}
func (r *Response[T]) RespondOK(w http.ResponseWriter) {
	w.Header().Set("Content-Type", "application/json")
	// 将响应数据转换为 JSON
	jsonData, err := json.Marshal(r)
	if err != nil {
		// 如果出错，返回错误信息
		http.Error(w, "Error generating JSON", http.StatusInternalServerError)
		return
	}
	// 设置 Content-Type 头
	w.Header().Set("Content-Type", "application/json")
	// 写入 JSON 数据到响应体
	if _, err = w.Write(jsonData); err != nil {
		log.Fatal("handle ok failure")
		return
	}
}

func (r *Response[T]) RespondError(w http.ResponseWriter) {
	w.WriteHeader(http.StatusInternalServerError)
	if _, err := w.Write([]byte("Resource not found")); err != nil {
		log.Fatal("handle error failure")
		return
	}
}
func BodyParams(r *http.Request) (string, error) {
	// 解析 JSON 请求体
	var req map[string]interface{}
	decoder := json.NewDecoder(r.Body)
	err := decoder.Decode(&req)
	if err != nil {
		return "", err
	}
	// 转换为 JSON 字符串
	jsonStr, err := json.Marshal(req)
	if err != nil {
		fmt.Println("Error converting map to JSON:", err)
		return "", err
	}
	return string(jsonStr), nil
}
func main() {
	// 设置 HTTP 路由
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		var params string
		switch r.Method {
		case "GET", "Delete":
			params = r.URL.RawQuery
		default:
			var err error
			params, err = BodyParams(r)
			if err != nil {
				Error("http.ServerIntervalError", "ParseBody err").RespondError(w)
				return
			}
		}
		method, uri, args := r.Method, r.URL.Path, params
		OK("hello prometheus").RespondOK(w)
		requestCounter.WithLabelValues(method, uri, args).Inc()
		log.Println("handler succeed...", method, uri, args)
	})

	// 暴露 /metrics 接口
	http.Handle("/metrics", promhttp.Handler())
	log.Println("Start serve succeed...")
	// 启动 HTTP 服务
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Start serve error...")
		return
	}
}
