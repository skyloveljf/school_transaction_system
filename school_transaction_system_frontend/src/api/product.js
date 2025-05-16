// src/api/products.js
export const mockProducts = [
  {
    id: 1,
    title: '可爱猫咪玩偶',
    description: '柔软毛绒，治愈系玩具，适合送礼。',
    image: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
    price: 39,
    owner: 'user1'
  },
  {
    id: 2,
    title: '小狗背包',
    description: '呆萌设计，轻便实用，深受学生喜爱。',
    image: 'https://cbu01.alicdn.com/img/ibank/2017/040/469/4086964040_756268226.jpg',
    price: 59,
    owner: 'user2'
  },
  {
    id: 3,
    title: '仓鼠水壶',
    description: '可爱造型，实用又好看，适合学生党。',
    image: 'https://th.bing.com/th/id/OIP.c_X-CKffYfKBanH1sq3igwHaHw?rs=1&pid=ImgDetMain',
    price: 29,
    owner: 'user3'
  }
];

export const mockComments = [
  {
    id: 1,
    productId: 1,
    userId: 101,
    username: '张三',
    content: '价格合适，感兴趣！',
    createdAt: '2023-05-01'
  },
  {
    id: 2,
    productId: 1,
    userId: 102,
    username: '李四',
    content: '图片不错，有货吗？',
    createdAt: '2023-05-02'
  },
  {
    id: 3,
    productId: 2,
    userId: 103,
    username: '王五',
    content: '能便宜点吗？',
    createdAt: '2023-05-03'
  }
];