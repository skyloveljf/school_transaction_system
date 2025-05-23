// 模拟用户数据库
const mockUserDB = [
  {
    id: 1, // 添加 id
    username: 'admin',
    password: 'admin123', // 在实际应用中，密码不应明文存储
    role: 'admin',
    email: 'admin@example.com',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    registrationDate: '2023-01-15',
    lastLogin: '2025-05-18',
  },
  {
    id: 2, // 添加 id
    username: 'user',
    password: 'user123',
    role: 'user',
    email: 'user@example.com',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    registrationDate: '2023-02-20',
    lastLogin: '2025-05-17',
  },
  { // 为数据统计模块增加一些用户数据
    id: 3,
    username: 'AliceSmith',
    password: 'password123',
    role: 'user',
    email: 'alice.smith@example.com',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    registrationDate: '2023-02-20',
    lastLogin: '2025-05-17',
  },
  {
    id: 4,
    username: 'BobJohnson',
    password: 'password123',
    role: 'user',
    email: 'bob.johnson@example.com',
    avatar: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
    registrationDate: '2023-03-10',
    lastLogin: '2025-05-19',
  }
];

// 导出 AdminStats.vue 需要的 mockUsers
// 通常，用于统计的用户列表和用于登录验证的数据库可能是不同的，或者至少结构上会更丰富
// 这里我们直接将 mockUserDB 作为 mockUsers 导出，并确保它有 AdminStats.vue 需要的结构
export const mockUsers = mockUserDB;


// 模拟登录接口
export function loginApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = mockUserDB.find(
        u => u.username === data.username && u.password === data.password
      );
      if (user) {
        resolve({
          message: '登录成功',
          data: {
            username: user.username,
            role: user.role,
            id: user.id, // 登录成功后也返回id
            avatar: user.avatar // 和头像
          }
        });
      } else {
        reject({
          response: {
            data: {
              message: '用户名或密码错误'
            }
          }
        });
      }
    }, 500); // 减少延迟以便测试
  });
}


// 模拟注册接口
export function registerApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const existingUser = mockUserDB.find(u => u.username === data.username);
      if (existingUser) {
        reject({
          response: {
            data: {
              message: '用户名已存在'
            }
          }
        });
      } else {
        const newUser = {
          id: mockUserDB.length > 0 ? Math.max(...mockUserDB.map(u => u.id)) + 1 : 1, // 简单生成id
          username: data.username,
          password: data.password, // 实际应用中应加密
          role: 'user', // 注册默认角色
          email: data.email || `${data.username}@example.com`, // 假设邮箱
          avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', // 默认头像
          registrationDate: new Date().toISOString().split('T')[0],
          lastLogin: new Date().toISOString().split('T')[0],
        };
        mockUserDB.push(newUser);
        resolve({ message: '注册成功', data: { username: newUser.username, role: newUser.role, id: newUser.id } });
      }
    }, 500);
  });
}

