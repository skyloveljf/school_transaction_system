campus-trade-system/
������ pom.xml             # ��POM������������Ŀ�������Ͳ��
������ backend/            # ���Spring BootӦ��ģ��
��   ������ pom.xml         # ���ģ���POM
��   ������ src/
��       ������ main/
��       ��   ������ java/
��       ��   ��   ������ com/example/yourproject/  # ����Ŀ�İ���·��
��       ��   ��       ������ CampusTradeSystemApplication.java # Spring Boot ������
��       ��   ��       ������ config/               # ������ (�� MybatisConfig, RedisConfig, WebConfig)
��       ��   ��       ������ controller/           # API ������
��       ��   ��       ������ dto/                  # ���ݴ������
��       ��   ��       ������ model/                # ���ݿ�ʵ��/����ģ��
��       ��   ��       ������ mapper/               # MyBatis Mapper �ӿ�
��       ��   ��       ������ service/              # ҵ���߼�����
��       ��   ��       ������ util/                 # ������
��       ��   ������ resources/
��       ��       ������ application.properties    # Spring Boot �����ļ�
��       ��       ������ mappers/                  # MyBatis XML Mapper �ļ�
��       ��       ������ static/                   # ��̬��Դ (δ���ɷ�ǰ�˹�������)
��       ������ test/
��           ������ java/
��               ������ com/example/yourproject/  # ���Դ������·��
������ frontend/           # ǰ��Vue3Ӧ��ģ�� (��ʼ��Ϊ�գ����������Vue��Ŀ�ṹ)
    ������ pom.xml         # ǰ��ģ���POM (��ѡ, ��Ҫ����ͨ��Maven����npm����)
    ������ (Vue3��Ŀ�ļ�����package.json, src/, public/ �ȣ���������)