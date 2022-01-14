function transList(val) {
  let data = this.transList, empty = !data || !data.length,
      filtered = empty ? [] : data.filter(i => i.orgCode === val);
  return empty || !filtered.length ? '' : filtered[0].channelName;
}

function fun(arg) {
  if (arg.a) {
    console.log("aaa")
  } else if (arg.b) {
    console.log("bbb")
  } else {
    console.log("ccc")
  }
}

fun({a: true, b: true})