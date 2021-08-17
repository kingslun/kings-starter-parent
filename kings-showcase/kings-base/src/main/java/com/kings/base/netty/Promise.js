//模拟的数据
const MOCK_DATA = [{id: 1, name: '张三', age: 18, phone: '10086'},
    {id: 2, name: '李四', age: 19, phone: '1008611'},
    {id: 3, name: '王五', age: 9, phone: '10010'},
    {id: 4, name: '赵六', age: 12, phone: '10020'},
    {id: 5, name: '胡七', age: 21, phone: '10030'}];
//模拟从后端 根据ID查询用户
let getById = id => {
    //必须存在切位json对象
    if (isNaN(id)) return null;
    let result = null;
    for (let i = 0; i < MOCK_DATA.length; i++) {
        if (MOCK_DATA[i].id === id) result = MOCK_DATA[i];
    }
    //当前时间毫秒值 模拟后台接口 阻塞一秒钟
    let start = new Date().getTime();
    for (let i = 0; ; i++) {
        let end = new Date().getTime();
        if (end - start > 1000) break;
    }
    return result;
};
//定义Promise对象 它是对一波又一波的异步函数管理的容器 例如:promise.then(a).then(b) 只有a执行完成才会执行b
let getJSON = id => {
    return new Promise(function (resolve, reject) {
        console.log("初始化Promise对象时就调用")
        //获取数据
        let user = getById(id)
        //模拟成功场景调用resolve
        if (user) {
            resolve(user);
        } else {
            //模拟失败场景调用reject
            reject(new Error("未查询到数据"));
        }
        console.log("你以为的异步函数执行后调用")
    });
}
// getJSON 返回的是一个promise对象 并且它最终执行resolve还是reject取决于getById(id)返回的是什么 换句话说取决于异步函数的结果
//因为getById(1)正常返回 因此走resolve
getJSON(1).then(result => console.log(result));//正常打印
//因为getById(8)不正常返回 因此走reject
getJSON(8).then(result => console.log(result))//报错
//因为getById(8)不正常返回 因此走reject 但由于处理了异常 因此走catch 并且报错信息error为promise reject时指定的new Error("未查询到数据")这个对象
getJSON(8).then(result => console.log(result)).catch(error => console.log(error));