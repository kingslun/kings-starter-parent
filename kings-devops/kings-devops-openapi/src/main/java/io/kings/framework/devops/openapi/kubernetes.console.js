// 初始化终端
import { Terminal } from 'xterm'
import 'xterm/dist/xterm.css'

let term = new Terminal()

// 将term挂砸到dom节点上
term.open(document.getElementById('app'))

term.write('Hello from \x1B[1;3;31mxterm.js\x1B[0m $ ')