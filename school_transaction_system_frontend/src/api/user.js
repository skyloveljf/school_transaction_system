// 模拟用户数据库
const mockUserDB = [
  { username: 'admin', password: 'admin123', role: 'admin' },
  { username: 'user', password: 'user123', role: 'user' }
]


// 模拟登录接口
export function loginApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = mockUserDB.find(
        u => u.username === data.username && u.password === data.password
      )
      if (user) {
        resolve({
          message: '登录成功',
          data: {
            username: user.username,
            role: user.role
          }
        })
      } else {
        reject({
          response: {
            data: {
              message: '用户名或密码错误'
            }
          }
        })
      }
    }, 1000)
  })
}


// 模拟注册接口
export function registerApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const existingUser = mockUserDB.find(u => u.username === data.username)
      if (existingUser) {
        reject({
          response: {
            data: {
              message: '用户名已存在'
            }
          }
        })
      } else {
        mockUserDB.push({
          username: data.username,
          password: data.password,
          role: 'user' // 注册默认角色
        })
        resolve({ message: '注册成功' })
      }
    }, 1000)
  })
}

