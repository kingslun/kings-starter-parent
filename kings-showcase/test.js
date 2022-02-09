function filterCodeValue(arr, oArr = [], code = 'code', value = 'value') {
  let res = {};
  arr.forEach(q => res[q[code]] = q[value])
  oArr.forEach(q => res[q[code]] = q[value])
  return res;
}

let arg0 = [{code: '0', value: '新保'}, {code: '1', value: '续保'}]
let arg1 = [{code: '2', value: '旧保'}, {code: '3', value: '臭保'}]
console.log(filterCodeValue(arg0, arg1))