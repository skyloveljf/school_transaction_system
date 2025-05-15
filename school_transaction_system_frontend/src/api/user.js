// 模拟用户数据
const mockUserDB = {
  username: 'testuser',
  password: '123456'
}

// 模拟登录接口
export function loginApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const { username, password } = data
      if (username === mockUserDB.username && password === mockUserDB.password) {
        resolve({ message: '登录成功' })
      } else {
        reject({ response: { data: { message: '用户名或密码错误' } } })
      }
    }, 1000) // 模拟网络延迟
  })
}

// 模拟注册接口
export function registerApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const { username } = data
      if (username === mockUserDB.username) {
        reject({ response: { data: { message: '用户名已存在' } } })
      } else {
        // 模拟注册成功
        mockUserDB.username = data.username
        mockUserDB.password = data.password
        resolve({ message: '注册成功' })
      }
    }, 1000)
  })
}
