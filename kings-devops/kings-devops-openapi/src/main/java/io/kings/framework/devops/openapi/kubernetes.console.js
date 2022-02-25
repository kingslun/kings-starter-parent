let ws = new WebSocket(
    "ws://localhost:8080/v1/kubernetes/pod/console/local/default/demo-795985b5d4-mjndd");
ws.onopen = () => {
  console.info("opening")
}
ws.onclose = () => {
  console.warn("closed")
}
ws.onmessage = msg => {
  console.debug(msg.data)
}