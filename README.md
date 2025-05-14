# school_transaction_system
school_transaction_system/
├── frontend/                  # 前端 Vue 项目
│   ├── public/
│   ├── src/
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 通用组件
│   │   ├── views/             # 页面视图
│   │   ├── router/            # Vue Router 配置
│   │   ├── store/             # Pinia 或 Vuex 状态管理
│   │   ├── api/               # 与后端交互接口封装
│   │   ├── utils/             # 工具函数
│   │   └── App.vue
│   ├── .env.development
│   ├── vite.config.ts
│   └── package.json
│
├── backend/                   # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourorg/schooltransaction/
│   │   │   │   ├── controller/     # 控制器
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   ├── mapper/         # MyBatis 接口
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── dto/            # 接收参数封装类
│   │   │   │   ├── vo/             # 响应数据封装类
│   │   │   │   ├── config/         # 配置类（JWT、安全、跨域等）
│   │   │   │   └── utils/          # 工具类
│   │   └── resources/
│   │       ├── application.yml     # Spring Boot 配置
│   │       ├── mapper/             # Mapper XML
│   │       └── static/             # 文件上传目录
│   ├── pom.xml
│   └── .gitignore
│
├── docs/                      # 项目文档（数据库ER图、接口文档等）
│   ├── er_diagram.png
│   ├── api_spec.md
│   └── collaboration_guide.md
│
└── README.md
