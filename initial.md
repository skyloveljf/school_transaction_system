У԰���ֽ���ϵͳ1. ��Ŀ���⣺У԰���ֽ���ϵͳ2. ����ϵͳ������Ŀ�ı���Ŀּ�ڹ���һ����У԰���ֽ���ϵͳ������Ϊѧ��Ӧ��ϵͳ�ܹ��γ̵�ʵ����Ŀ�������Ŀ����ΪУ԰�ڵ�ѧ���ṩһ����ݵ�ƽ̨����������������Ʒ����Ϊһ��ѧϰ��Ŀ��ϵͳ����������Ĺ��ܵ�ǰ���£����Ʋ���Ҫ�ĸ��Ӷȣ�ͬʱ�����ڹؼ��ܹ�ԭ���ʵ����չʾ���ؼ���������ϵͳ������������Ҫ����ջ�����ʹ�� Java �� Spring Boot ��ܣ�ǰ�˲��� Vue3�����ݳ־û�ѡ�� MyBatis �������Σ�DM�����ݿ⣬������ Redis ���л����Ż������⣬ϵͳ���������ⲿ���������������ı������ Deepseek API���ṩʵʱ������Ϣ�� Web �����Լ�һ������AI��������������ţ��԰�����Ϊ�������м�ʵ�֣���3. ���������Ҫ����Ŀ�ϸ���ѭ���¼����빦��Ҫ�󣺼���ջ�涨
�������ԣ� Java
���ݿ⣺ �������ݿ� (DM) (��ΪӲ��Ҫ��)
��Ҫ�����ܹ��� Spring MVC
���Ŀ��������� Vue3, Spring Boot
���ݿ�����㣺 MyBatis
�ӿڼ����� Deepseek API, Redis, Web Service (���磺����Ԥ��)
AI������ ����AI������ƺͿ�����������Ź��� (���磺������ƽ̨�����м�ʵ��)
���ܷ�Χ
ϵͳ����� 4-6�� ��ȷ�Ĺ���ģ�顣
�����������һ���漰 ���ӱ�ṹ������ҵ���������ҵ���ܡ�
4. ϵͳ�ܹ��߲�ܹ�ͼΪ��������չʾϵͳ����ɲ��ּ��佻����ʽ��һ���߲�ܹ�ͼ�Ǳز����ٵġ���ͼӦ�ܰ����������ϵͳ�Ĺ������̡�������˴�ʵ�ʻ��ƻ�ʹ�ù�������һ���ܹ�ͼ������Ϊ�������������������˵���˺���������佻����ϵ��+---------------------+      +---------------------------------+      +-------------------+
| Vue3 ǰ�� |----->| Spring Boot ��� (ҵ���߼�/API) |----->| MyBatis (���ݷ���)|
| (�û�����) |<-----| |<-----| |
+---------------------+      +---------------------------------+      +-------------------+
| ^ |
| | v
                                      v | +-------------------+
                              +-------------+ | | �������ݿ� (DM) |
| Redis (����)| | +-------------------+
                              +-------------+ |
                                      ^ |
| |
      +----------------------------------------------------------------------------------+
| �ⲿ���� (Deepseek API, ����API, AI Agent API) |
      +----------------------------------------------------------------------------------+
��ͼչʾ���û�ͨ��Vue3ǰ����ϵͳ��������������Spring Boot��ˡ���˴���ҵ���߼���ͨ��MyBatis��������ݿ�������ݽ���������Redis�������ݻ��棬�����ⲿAPI����Deepseek����������AI�����壩ͨ�����ṩ��ǿ���ܡ����ֲַ�ܹ�������ģ�����Ͷ����������������
ǰ�� (Vue3)�� �����û�����������չʾ�Լ����˷��������û�ͨ�����������ǰ��Ӧ�ã������Ʒ��������������ĵȲ�����
��� (Spring Boot/Spring MVC)�� ����ҵ���߼��������ġ���ͨ��RESTful API��ǰ���ṩ���񣬹������ݿ⽻���������ɸ����ⲿ����
���ݿ� (���� DM)�� ϵͳ�ĳ־û��洢�㣬����洢����Ӧ�����ݣ����û���Ϣ����Ʒ��Ϣ�����ۼ�¼�ȡ�
���ݷ��ʲ� (MyBatis)�� ��Ϊ�����ϵӳ�䣨ORM����ܣ�����Java���������ݿ��¼֮���ӳ�䣬ʹSQL������Ϊ��� 1�����������߸����ؿ���SQL��䡣
���� (Redis)�� ���ڴ洢Ƶ�����ʵ����ݣ���������Ʒ��������Ϣ�ȣ��Լ������ݿ⸺�أ�����ϵͳ��Ӧ�ٶȺ����� 3��
�ⲿAPI�� �ṩרҵ���Ĺ��ܣ�����ͨ��Deepseek API���������ı���������Ʒ�������ɣ���ͨ������API��ȡʵʱ�������ݡ�
�Ƽ���Ŀ�ṹ (Maven��ģ��)Ϊ�˸��õ���֯���롢�����ע�㣬��֧��δ�����ܵĶ��������Ͳ����Ƽ�����Maven��ģ����Ŀ�ṹ�����ֽṹ�����ʵ����Ŀ�б��㷺Ӧ�ã������ڱ��ִ���������ԺͿ�ά���ԣ�������ڰ���ǰ�˺ͺ�����󲿷ֵ�ϵͳ���� 5��campus-trade-system/
������ pom.xml             # ��POM������������Ŀ�������Ͳ��
������ backend/            # ���Spring BootӦ��ģ��
��   ������ pom.xml         # ���ģ���POM
��   ������ src/            # ���JavaԴ�������Դ�ļ�
������ frontend/           # ǰ��Vue3Ӧ��ģ��
    ������ pom.xml         # ǰ��ģ���POM (��ѡ, ��Ҫ����ͨ��Maven����npm����)
    ������ src/            # ǰ��VueԴ�������Դ�ļ�
���ֽṹ����˺�ǰ�˴�����ȷ�ֿ���ʹ���Ŷӳ�Ա���Բ��й�����Ҳ����Ը�ģ����ж����Ĺ����Ͳ��ԡ�5. ����ջ����Ϊ��ȷ���������̵�˳�����к͸������ļ����ԣ��±��г�����Ŀ��ʹ�õ���Ҫ���������Ƽ��汾��ѡ���ȶ�������֧�����õİ汾�������ڼ��ٿ��������п��������ļ��������⡣
��������Ƽ��汾��עJava��������JDK 17 (����ݿγ�Ҫ�����)ȷ����Spring Boot�汾����Spring Boot��˿��3.x.xѡ�ý��µ��ȶ��汾Vue3ǰ�˿��3.x.x����ǰ�˿��MyBatisORM���3.5.x����SQLӳ����MyBatis Spring Boot Starter���ɿ�3.0.x 12��MyBatis��Spring Boot�ļ��ɴ���JDBC�������ݿ�����DmJdbcDriver18, �汾 8.1.x.x (��8.1.3.140 7)��ȷ�������ô������ݿ�汾���ݣ��ɴ�Maven����ֿ��ȡ 7Spring Data Redis���漯��(��Spring Boot�汾����)�ṩRedis�����ı��APIJedis/LettuceRedis�ͻ���(��Spring Data Redis����)Spring Data Redis�ײ������Ŀͻ���Maven��������3.6+��Ŀ��������������Node.jsǰ������ʱLTS �汾 (�� 18.x �� 20.x 5)ִ��Vue3ǰ�˹����Ϳ���������npm/yarnǰ�˰�������(��Node.js��װ)����ǰ����Ŀ����
6. �����������ǰ������
JDK�� ��װָ���汾 (�� JDK 17)��
Maven�� ��װָ���汾 (�� 3.6+)��
Node.js �� npm/yarn�� ��װָ���汾 (�� Node.js LTS 5)��
�������ݿ� (DM)��

���ݿγ�Ҫ���ٷ��ĵ���װ�����ô������ݿ⡣�����û��Դ������ݿⲻ̫��Ϥ�������ϸ�Ķ��䰲װ������ָ�ϡ�
�ؼ����裺Ϊ����Ŀ����һ��ר�õ����ݿ�ʵ����ģʽ��Schema������¼�����ݿ������URL���û��������룬�������û��õ���
ȷ���������ݿ��������������ͨ��������ʡ�


��� (Spring Boot) ����pom.xml �ؼ������ں��ģ��� pom.xml �ļ��У���Ҫ������º���������XML<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.3</version> </dependency>

    <dependency>
        <groupId>com.dameng</groupId>
        <artifactId>DmJdbcDriver18</artifactId> <version>8.1.3.140</version> </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
ȷ���������ȷ�Ĵ���JDBC����������DmJdbcDriver18 ����汾�� 8.1.3.140 �ǻ���������Ϣ��ʾ�� 7��ʵ��ʹ��ʱӦ�˶������Ĵ������ݿ�汾��ƥ���������application.properties (�� application.yml)�� src/main/resources Ŀ¼�´��� application.properties �ļ����������������ԣ�Properties# ����������
server.port=8080

# SpringӦ������
spring.application.name=campus-second-hand-trading

# �������ݿ� (DM) ����
# ���滻Ϊ����ʵ�����ݿ����ơ��û���������
# �������ݿ��Ĭ�϶˿�ͨ���� 5236
spring.datasource.url=jdbc:dm://localhost:5236/YOUR_DB_NAME # ���磺jdbc:dm://127.0.0.1:5236/TRADE_DB [8]
spring.datasource.username=YOUR_DM_USERNAME
spring.datasource.password=YOUR_DM_PASSWORD
spring.datasource.driver-class-name=dm.jdbc.driver.DmDriver # ����JDBC�������� [8]
# �����Ҫ��������Ӵ����ض����������ԣ����磺
# spring.datasource.hikari.connection-init-sql=SET CASE_SENSITIVE_LOGON FALSE

# MyBatis ����
# ָ��MyBatis Mapper XML�ļ���λ�� [1]
mybatis.mapper-locations=classpath:mappers/*.xml
# �����շ�����ת�������ݿ��»��������Զ�ӳ�䵽Java�շ���������
mybatis.configuration.map-underscore-to-camel-case=true
# ָ��ʵ�������ɨ�����������Mapper XML��ֱ��ʹ������
# mybatis.type-aliases-package=com.example.yourproject.model # �����������Ŀ���ṹ����

# Redis ����
spring.data.redis.host=localhost
spring.data.redis.port=6379
# ���Redis���������룬ȡ��ע�Ͳ�����:
# spring.data.redis.password=YOUR_REDIS_PASSWORD

# �ⲿAPI��Կ (��Ҫ������������Ӧʹ�ø���ȫ�ķ�ʽ������Կ���绷����������������)
deepseek.api.key=YOUR_DEEPSEEK_API_KEY
weather.api.key=YOUR_WEATHER_API_KEY
alibaba.agent.api.key=YOUR_ALIBABA_AGENT_API_KEY
alibaba.agent.api.endpoint=YOUR_ALIBABA_AGENT_ENDPOINT
�������ļ���Spring BootӦ�õĺ��ģ���ȷ�������ݿ�������Ϣ������Ҫ�����ڴ������ݿ⣬driver-class-name �� url ��ʽ���׼ȷ 8��API��Կ�Ĺ����ڿ����׶ο���ֱ��д�������ļ����������������У�ǿ�ҽ�����û���������Vault��Spring Cloud Config�ȸ���ȫ�ķ�ʽ��ǰ�� (Vue3) ����
���� frontend Ŀ¼��
ִ�� npm install (�� yarn install) ����ǰ����Ŀ�������������
����API����URL����Vue3��Ŀ�У�ͨ������һ�������ļ����� .env.development, .env.production ��һ��ר�ŵ� src/config/api.js �ļ�����ָ�����API�ĵ�ַ������ָ��Spring Boot��˷������� VUE_APP_API_BASE_URL=http://localhost:8080/api��
�������ݿ� Schema ��������Ǻ���ʵ���� CREATE TABLE SQL��䣬��ʹ�ô������ݿ������ִ�С���������ѿ��ǵ��������ݿ���������ͼ����� 9��USERS (�û���)�洢�û���Ϣ��������֤����Ȩ��SQLCREATE TABLE USERS (
    user_id INT IDENTITY(1,1) PRIMARY KEY,    -- �û�ID���������� (DM֧��IDENTITY)
    username VARCHAR(50) NOT NULL UNIQUE,     -- �û�����Ψһ�Ҳ���Ϊ��
    password_hash VARCHAR(255) NOT NULL,      -- �洢��ϣ������룬ȷ����ȫ
    email VARCHAR(100) NOT NULL UNIQUE,       -- �������䣬Ψһ�Ҳ���Ϊ��
    registration_date TIMESTAMP DEFAULT SYSTIMESTAMP, -- ע�����ڣ�Ĭ��Ϊ��ǰʱ�� (DM֧��SYSTIMESTAMP)
    last_login_date TIMESTAMP                 -- ����¼����
);
-- ���ô�����������: INT, VARCHAR, TIMESTAMP [9, 10]
CATEGORIES (��Ʒ�����)������֯�͹�����Ʒ���ࡣSQLCREATE TABLE CATEGORIES (
    category_id INT IDENTITY(1,1) PRIMARY KEY, -- ����ID����������
    category_name VARCHAR(100) NOT NULL UNIQUE, -- �������ƣ�Ψһ�Ҳ���Ϊ��
    description CLOB                            -- ����������ʹ��CLOB�洢�ϳ��ı� [9]
);
PRODUCTS (��Ʒ�� - ����)�洢������Ʒ����ϸ��Ϣ�����ǽ��׵ĺ��ġ�SQLCREATE TABLE PRODUCTS (
    product_id INT IDENTITY(1,1) PRIMARY KEY,  -- ��ƷID����������
    user_id INT NOT NULL,                     -- ������ID (�������USERS��)
    category_id INT NOT NULL,                 -- ��Ʒ����ID (�������CATEGORIES��)
    title VARCHAR(200) NOT NULL,              -- ��Ʒ����
    description CLOB,                         -- ��Ʒ������ʹ��CLOB�洢��ϸ����
    price DECIMAL(10,2) NOT NULL,             -- ��Ʒ�۸�ʹ��DECIMAL��֤���� [9]
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'SOLD', 'PENDING')), -- ��Ʒ״̬
    post_date TIMESTAMP DEFAULT SYSTIMESTAMP,   -- ��������
    views INT DEFAULT 0,                      -- �������
    CONSTRAINT FK_PRODUCT_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT FK_PRODUCT_CATEGORY FOREIGN KEY (category_id) REFERENCES CATEGORIES(category_id)
);
-- �������ݿ��DDL����﷨���׼SQL���� [11, 12]��
-- DECIMAL �� CLOB �Ǵ���֧�ֵĺ����������� [9, 10]��
PRODUCT_BIDS (��Ʒ���۱� - �ӱ�)�˱�����ʵ����Ʒ���۹��ܣ������㡰���ӱ�ṹ����ҵ���������Ҫ��PRODUCTS ��������PRODUCT_BIDS ���Ǵӱ�ͨ�� product_id ҵ�����������SQLCREATE TABLE PRODUCT_BIDS (
    bid_id INT IDENTITY(1,1) PRIMARY KEY,       -- ����ID����������
    product_id INT NOT NULL,                  -- ��������ƷID (���)
    bidder_user_id INT NOT NULL,              -- �����û�ID (�������USERS��)
    bid_amount DECIMAL(10,2) NOT NULL,          -- ���۽��
    bid_time TIMESTAMP DEFAULT SYSTIMESTAMP,    -- ����ʱ��
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')), -- ����״̬
    CONSTRAINT FK_BID_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id) ON DELETE CASCADE, -- ҵ���������Ʒ��ɾ��������س��ۼ�¼Ҳ����ɾ��
    CONSTRAINT FK_BID_USER FOREIGN KEY (bidder_user_id) REFERENCES USERS(user_id)
);
PRODUCT_BIDS ��ͨ�� product_id �ֶ��� PRODUCTS ��������ȷ�����ӹ�ϵ��ON DELETE CASCADE Լ��ȷ���˵�һ����Ʒ��ɾ��ʱ��������صĳ��ۼ�¼Ҳ�ᱻ�Զ����������һ�ֳ���������������ά����ʽ����Щ��ṹ�����˶��ֽ���ϵͳ�Ļ�����USERS ������û���CATEGORIES �����Ʒ���з��࣬PRODUCTS ��洢��Ʒ��Ϣ���� PRODUCT_BIDS ����ʵ���˺��ĵľ��۹��ܣ�����������Ŀ�����ӱ�ṹ��Ҫ��7. ����ģ�� (4-6��ʾ��)�����ǽ����ϵͳ����ģ�飬�����˺�������������ָ���ļ�����

�û���֤�����

������ �ṩ�û�ע�ᡢ��¼���ǳ����ܣ��Լ��û��鿴�͸��¸������ϵĽӿڡ�
��ر� USERS
�ؼ�API�˵㣺

POST /api/auth/register���û�ע�ᣬ�ύ�û��������롢�������Ϣ��
POST /api/auth/login���û���¼����֤ƾ�ݲ�����token��
POST /api/auth/logout���û��ǳ���ʹtokenʧЧ��
GET /api/users/me����ȡ��ǰ��¼�û��ĸ������ϡ�
PUT /api/users/me�����µ�ǰ��¼�û��ĸ������ϡ�





��Ʒ���������

������ �û�����������з����Ķ�����Ʒ��������ɸѡ����ͨ���ؼ���������Ʒ��
��ر� PRODUCTS, CATEGORIES, USERS (����չʾ������Ϣ)��
�ؼ�API�˵㣺

GET /api/products����ȡ��Ʒ�б�֧�ַ�ҳ��������/�ؼ��ʹ��ˡ�
GET /api/products/{productId}����ȡ������Ʒ����ϸ��Ϣ��
GET /api/categories����ȡ������Ʒ�����б��˽ӿ��ʺ�ʹ��Redis���� 3����





��������Ʒ

������ ��֤�û����Խ��Լ��Ķ�����Ʒ������ƽ̨�Ͻ���������
��ر� PRODUCTS
�ؼ�API�˵㣺

POST /api/products����������Ʒ����Ҫ�û���֤�������������Ʒ���⡢�������۸񡢷������Ϣ��





��Ʒ����ϵͳ (��ʾ���ӱ���)

������ �û����ԶԸ���Ȥ����Ʒ���г��ۡ����ҿ��Բ鿴����Ʒ�ĵ�ǰ���г��ۡ��˹���ֱ�������� PRODUCTS (����) �� PRODUCT_BIDS (�ӱ�) ֮��Ĺ�����
��ر� PRODUCTS (����), PRODUCT_BIDS (�ӱ�), USERS��
�ؼ�API�˵㣺

POST /api/products/{productId}/bids����ָ����Ʒ���г��ۣ���Ҫ�û���֤��
GET /api/products/{productId}/bids���鿴ָ����Ʒ�����г��ۼ�¼ (ͨ�������һ����Ա�ɼ�)��
GET /api/users/me/bids���鿴��ǰ�û���������г��ۼ�¼��





������Ʒ���� (����Deepseek API��AI������)

������ �û��ڷ�������Ʒʱ��ϵͳ������Deepseek API�Զ�������Ʒ�����ݸ壬�������Ʒ����ͳ��������Ƽ���ر�ǩ/���ࡣ�˹��ܽ�����Deepseek API����ͨ����˷���ģ��һ���򵥵ġ�AI�����塱�������̡�
��ر� PRODUCTS (��Ҫ������ description �ֶλ�������ǩ�ֶ�)��
�ⲿ���� Deepseek API, (�򻯵�) ������AI���������
�ؼ�API�˵㣺

���Լ��ɵ� POST /api/products �����У�����Ʒ����ǰ���á�
���ṩһ�������ĸ����ӿڣ�POST /api/ai/generate-description?title={productTitle}&initialDesc={initialDescription}


�˹���������ʹ��Deepseek API 13 ��AI��������ŵ�Ҫ��ͨ����˷���Э����Deepseek API�ĵ��ã����ܰ����Խ���ĳ���������ʽ������������Ϊһ�ּ򻯵ġ������塱����ģʽ��



У԰����չʾ (����Web Service)

������ ��ϵͳ�Ǳ��̻��ض�ҳ����ʾ��ǰУ԰�����������Ϣ��
�ⲿ���� ����Web Service (���� Tomorrow.io 15)��
�ؼ�API�˵㣺

GET /api/weather/campus����˴Ӵ˽ӿڻ�ȡ�ⲿ����API���ݣ�����������Redis���л��� 3���Լ��ٶ��ⲿAPI������Ƶ�ʲ������Ӧ�ٶȡ�


�˹��������˼����ⲿWeb�����Ҫ�󣬲��ܽ��Redis���漼�������Ż���


8. ��˿��� (Spring Boot)8.1. ���ݿ�� (MyBatis & ����)���ݳ־û�����ϵͳ�Ļ�ʯ��MyBatis�ṩ������SQL����������

Mapper�ӿڣ� ����Java�ӿڣ��䷽����ӦSQL������ʹ�� @Mapper ע���ǽӿ��Ա�Spring Bootɨ�裬��ʹ�� @Select, @Insert, @Update, @Delete ��ע�ⶨ��򵥵�SQL��� 1��
Java// �ļ�·��: src/main/java/com/example/yourproject/mapper/ProductMapper.java
package com.example.yourproject.mapper;

import com.example.yourproject.model.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM PRODUCTS WHERE product_id = #{productId}")
    Product findById(@Param("productId") int productId);

    @Insert("INSERT INTO PRODUCTS (user_id, category_id, title, description, price, status, post_date) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, #{description}, #{price}, #{status}, SYSTIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "productId", keyColumn="product_id") // ȷ��������������
    int insert(Product product);

    @Update("UPDATE PRODUCTS SET title = #{title}, description = #{description}, price = #{price}, " +
            "category_id = #{categoryId}, status = #{status} WHERE product_id = #{productId}")
    int update(Product product);

    @Delete("DELETE FROM PRODUCTS WHERE product_id = #{productId}")
    int deleteById(@Param("productId") int productId);

    @Select("SELECT * FROM PRODUCTS ORDER BY post_date DESC") // ʾ������ȡ������Ʒ
    List<Product> findAll();

    // ���ڻ�ȡ��Ʒ�������г��ۼ�¼ (���Ӳ�ѯ)������ʵ����XML�ж���
    Product findProductWithBids(@Param("productId") int productId);
}



XML Mappers (mappers/*.xml)�� ���ڸ��ӵ�SQL��ѯ���������漰������ӡ���̬SQL�����ӱ�����ӳ�䣨��һ�Զ༯��ӳ�䣩ʱ��ʹ��XML�ļ���Ϊ���ʺ�ǿ�� 1��
XML<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.yourproject.mapper.ProductMapper"> <resultMap id="ProductWithBidsResultMap" type="com.example.yourproject.model.Product">
        <id property="productId" column="p_product_id"/> <result property="userId" column="p_user_id"/>
        <result property="categoryId" column="p_category_id"/>
        <result property="title" column="p_title"/>
        <result property="description" column="p_description"/>
        <result property="price" column="p_price"/>
        <result property="status" column="p_status"/>
        <result property="postDate" column="p_post_date"/>
        <result property="views" column="p_views"/>
        <association property="seller" javaType="com.example.yourproject.model.User">
            <id property="userId" column="u_user_id"/>
            <result property="username" column="u_username"/>
            <result property="email" column="u_email"/>
        </association>
        <collection property="bids" ofType="com.example.yourproject.model.ProductBid">
            <id property="bidId" column="b_bid_id"/>
            <result property="productId" column="b_product_id"/> <result property="bidderUserId" column="b_bidder_user_id"/>
            <result property="bidAmount" column="b_bid_amount"/>
            <result property="bidTime" column="b_bid_time"/>
            <result property="status" column="b_status_bid"/> <association property="bidder" javaType="com.example.yourproject.model.User">
                <id property="userId" column="bidder_user_id_fk"/>
                <result property="username" column="bidder_username"/>
            </association>
        </collection>
    </resultMap>

    <select id="findProductWithBids" resultMap="ProductWithBidsResultMap">
        SELECT
            p.product_id AS p_product_id, p.user_id AS p_user_id, p.category_id AS p_category_id,
            p.title AS p_title, p.description AS p_description, p.price AS p_price,
            p.status AS p_status, p.post_date AS p_post_date, p.views AS p_views,
            u.user_id AS u_user_id, u.username AS u_username, u.email AS u_email,
            b.bid_id AS b_bid_id, b.product_id AS b_product_id, b.bidder_user_id AS b_bidder_user_id,
            b.bid_amount AS b_bid_amount, b.bid_time AS b_bid_time, b.status AS b_status_bid,
            bidder_u.user_id AS bidder_user_id_fk, bidder_u.username AS bidder_username
        FROM
            PRODUCTS p
        JOIN
            USERS u ON p.user_id = u.user_id
        LEFT JOIN
            PRODUCT_BIDS b ON p.product_id = b.product_id
        LEFT JOIN
            USERS bidder_u ON b.bidder_user_id = bidder_u.user_id
        WHERE
            p.product_id = #{productId}
        ORDER BY 
            b.bid_time DESC NULLS LAST -- ʾ�����򣬽����µĳ��۷���ǰ�棬û�г��۵���ƷҲ����ȷ��ʾ
    </select>
</mapper>

���XMLӳ����ʾ�����ͨ�� <resultMap> �� <collection> ��ǩ������һ����Ʒ����������������صĳ��ۼ�¼���ӣ������ַ�ʽ���ڴ���һ�Զ���Զ��ϵ�ǳ���Ч���ܹ�һ���Ի�ȡ�������ݣ�������N+1��ѯ���⡣


Service�㣺 ��װҵ���߼�����ʹ�� @Transactional ע���������ȷ�����ݲ�����һ���ԡ�Service�����Mapper�ӿ������ݿ⽻����

8.2. API�˵� (Spring MVC / REST Controllers)ʹ�� @RestController ����������࣬ͨ�� @GetMapping, @PostMapping, @PutMapping, @DeleteMapping ��ע��ӳ��HTTP���󵽾���Ĵ����� 1��Ϊ�˹淶�������Ӧ�����ݽṹ����������Ч������У�飬�Ƽ�ʹ�����ݴ������DTOs��������� @Valid ע�����У�顣Java// �ļ�·��: src/main/java/com/example/yourproject/controller/ProductController.java
package com.example.yourproject.controller;

import com.example.yourproject.model.Product; // ������������ʵ����
import com.example.yourproject.dto.ProductCreateDTO; // �����������ڴ�����Ʒ��DTO
import com.example.yourproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import javax.validation.Valid; // ���ʹ�� Jakarta EE 9+ (Spring Boot 3+), ʹ�� jakarta.validation.Valid

import jakarta.validation.Valid; // For Spring Boot 3+
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable int productId) {
        Product product = productService.getProductWithBids(productId); // ���û�ȡ��Ʒ�����۵ķ���
        if (product!= null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        // ʵ��Ӧ���У�ProductCreateDTO ��ת��Ϊ Product ʵ��
        // Product productToCreate = convertDtoToEntity(productCreateDTO);
        // ������Ҫ���õ�ǰ��¼�û�Ϊ������
        // productToCreate.setUserId(getCurrentUserId());
        // Product createdProduct = productService.createProduct(productToCreate);
        // return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        // �˴�Ϊ��ʾ��
        Product mockProduct = new Product(); // ������ת���������û�ID
        mockProduct.setTitle(productCreateDTO.getTitle());
        //... ������������
        Product savedProduct = productService.createProduct(mockProduct); // ����service����������ת���ͱ���
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // �������¡�ɾ���ȶ˵�...
}
8.3. �ⲿAPI����ͨ�÷����Ƽ�ʹ��Spring����ṩ�� RestClient �������ⲿREST��������Spring Boot 3.x�����Ƽ�������ʽHTTP�ͻ��� 16����API�����߼���װ��ר�ŵ�Service���С�Java// �ļ�·��: src/main/java/com/example/yourproject/service/ExternalWeatherService.java
package com.example.yourproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cache.annotation.Cacheable; // ���ڻ���

@Service
public class ExternalWeatherService {

    private final RestClient restClient;
    private final String apiKey;
    private final String weatherApiBaseUrl = "https://api.tomorrow.io/v4"; // �ο� [15]

    // ���캯��ע�� RestClient.Builder �� apiKey
    public ExternalWeatherService(RestClient.Builder builder, @Value("${weather.api.key}") String apiKey) {
        this.restClient = builder.baseUrl(weatherApiBaseUrl).build();
        this.apiKey = apiKey;
    }

    @Cacheable("campusWeather") // ���������ݻ���������������Ϊ "campusWeather"
    public String getCampusWeather(String location) { // location �����Ǿ�γ�Ȼ����
        try {
            // ʾ��������Tomorrow.io API��ȡʵʱ����
            // GET /weather/realtime?location=toronto&apikey=XXX [15]
            String response = restClient.get()
                               .uri("/weather/realtime?location={location}&apikey={apiKey}", location, this.apiKey)
                               .retrieve()
                               .body(String.class); // ���践�ص���JSON�ַ���
            // ʵ��Ӧ���У�����Ӧ�ý�JSON�ַ�������Ϊ�����Java����
            return response;
        } catch (Exception e) {
            // ����API�����쳣�������¼��־������Ĭ��ֵ�������Ϣ
            System.err.println("Error fetching weather data: " + e.getMessage());
            return "{\"error\": \"Unable to fetch weather data\"}";
        }
    }
}
���� ExternalWeatherService ��ʾ�����ʹ�� RestClient ��������API����ʹ�� @Cacheable ע��Խ�����л��棬��������ܲ����ٶ��ⲿAPI��������Deepseek API ���ɴ���һ��������������Deepseek API������������Ʒ�������������ɡ�
��֤�� ����ͷ����� Authorization: Bearer YOUR_DEEPSEEK_API_KEY 18��
�˵㣺 POST https://api.deepseek.com/chat/completions 18��
�����壺 JSON��ʽ������ģ�����ƣ��� deepseek-chat������Ϣ�б� 18��
Ӧ�ó����� �Զ�������Ʒ�������Ż�SEO�ؼ��ʡ������ʴ�� 13��
Java// �ļ�·��: src/main/java/com/example/yourproject/service/DeepseekService.java
package com.example.yourproject.service;

import com.example.yourproject.dto.DeepseekChatRequest; // �Զ�������DTO
import com.example.yourproject.dto.DeepseekChatResponse; // �Զ�����ӦDTO
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;

@Service
public class DeepseekService {

    private final RestClient restClient;
    private final String apiKey;
    private final String deepseekApiBaseUrl = "https://api.deepseek.com"; // �ο� [19]

    public DeepseekService(RestClient.Builder builder, @Value("${deepseek.api.key}") String apiKey) {
        this.restClient = builder.baseUrl(deepseekApiBaseUrl).build();
        this.apiKey = apiKey;
    }

    public String generateProductDescription(String productTitle) {
        DeepseekChatRequest.Message systemMessage = new DeepseekChatRequest.Message("system", "����һ�������û�Ϊ������Ʒ׫д���������������֡�");
        DeepseekChatRequest.Message userMessage = new DeepseekChatRequest.Message("user", "��Ϊ�ҵ���Ʒ��" + productTitle + "������һ��50�����ҵ�������");
        
        DeepseekChatRequest requestPayload = new DeepseekChatRequest(
            "deepseek-chat", // �� "deepseek-reasoner" [18]
            List.of(systemMessage, userMessage),
            false // stream = false
        );

        try {
            DeepseekChatResponse response = restClient.post()
               .uri("/chat/completions")
               .header("Authorization", "Bearer " + this.apiKey)
               .contentType(MediaType.APPLICATION_JSON)
               .body(requestPayload)
               .retrieve()
               .body(DeepseekChatResponse.class);

            if (response!= null && response.getChoices()!= null &&!response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "�޷�����������";
        } catch (Exception e) {
            System.err.println("Error calling Deepseek API: " + e.getMessage());
            return "����AI����ʱ����";
        }
    }
}
// DeepseekChatRequest �� DeepseekChatResponse DTOs ��Ҫ���ж�����ƥ��API��JSON�ṹ
// ���� DeepseekChatRequest:
// public record DeepseekChatRequest(String model, List<Message> messages, boolean stream) {
//     public record Message(String role, String content) {}
// }
// ���� DeepseekChatResponse:
// public record DeepseekChatResponse(String id, String object, long created, String model, List<Choice> choices, Usage usage) {
//     public record Choice(int index, Message message, String finish_reason) {}
//     public record Message(String role, String content) {}
//     public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {}
// }
Redis ����
��Spring Boot���������һ��ר�ŵ������������ @EnableCaching ע�������û��湦�� 3��
����Ҫ��������Service������ʹ�� @Cacheable ע�⡣������������ʱ��Spring���ȼ�黺�����Ƿ���ڶ�Ӧ���Ľ�������������ֱ�ӷ��ػ���ֵ������ִ�з�������������뻺�� 3��
ʹ�� @CachePut �����ڲ�Ӱ�췽��ִ�е�����¸��»��档
ʹ�� @CacheEvict ���Դӻ������Ƴ����ݣ����統���ݸ��»�ɾ��ʱ��
Java// ʾ������CategoryService�л�������б�
// �ļ�·��: src/main/java/com/example/yourproject/service/CategoryService.java
package com.example.yourproject.service;

import com.example.yourproject.model.Category;
import com.example.yourproject.mapper.CategoryMapper; // ������CategoryMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper; // ע��Mapper

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Cacheable("categories") // ���������Ϊ "categories"
    public List<Category> getAllCategories() {
        System.out.println("Fetching categories from database..."); // ������ʾ�����Ƿ���Ч
        return categoryMapper.findAll(); // ����CategoryMapper��findAll����
    }
}

// ����Ӧ����������������û���
// import org.springframework.cache.annotation.EnableCaching;
// @SpringBootApplication
// @EnableCaching // ���û���
// public class CampusTradeSystemApplication {... }
8.4. AI��������� (�򻯰氢����ʾ��)�����û�����һ����С���Ҫ�󣬴˴��ġ���������š�����Ϊ��˷�����õ�����������ƽ̨���簢����ģ�ͷ��������ModelScope����������FC�����AIģ�ͣ��ϵ�AIӦ��/ģ�͡�Ŀ�����������ƶ�AI����ļ��ɡ�
��� ��������������ſ����漰���AI������Эͬ�������Զ��滮�������� 20�����ڱ�ѧ����Ŀ�����ǽ��˼򻯣�Spring Boot��˵�һ��������Ϊ�������ߡ�������һ���ڰ������ϲ���ġ��߱��ض�AI���ܵ�ģ�ͣ����磬ͨ��Model Studio�����Qwenϵ��ģ�� 20����
����ʾ������������Ʒ��ǩ/�����Ƽ���

�û��ڷ�����Ʒʱ�������ͻ���������
��˷��񣨳䵱�����������򡰵����ߡ�������Щ�ı���Ϣ���͸������ڰ������ϵ�AI����/ģ�͡���ģ�Ϳ��ܾ����ض���ʾ��Prompt�����̻�΢������ִ�б�ǩ��ȡ���������
�������ϵ�AIģ�ʹ����ı��������Ƽ��ı�ǩ�б����Ʒ���ཨ�顣
��˷��������Щ���飬����������Ʒ������������չʾ���û���ѡ��


������ģ�ͷ�����Model Studio / DashScope API����

ѧ�������ڰ�����Model Studio��ѡ����ʵ�Ԥѵ��ģ�ͣ���Qwenϵ�������ı��������ɣ���ͨ���򵥵����ú�Prompt��ƣ����䲿��Ϊһ���ɵ��õ�API���� 20��ƽ̨ͨ�����ṩAPI�ĵ��õ�ַ����֤��ʽ����API Key����
��Ȼ20�еġ��ճ̹������֡�ʾ��չʾ�������������Э����������Ŀ���Ծ۽��ڵ��õ������ƻ���AI������


Spring Boot Service ʵ�֣�

����һ��Service�࣬ʹ�� RestClient ���ò����ڰ������ϵ�AIģ�͵�API�˵㡣
�������д�����Ʒ��Ϣ������⡢��������
�����AIģ�ͷ��ص�JSON��Ӧ����ȡ��ǩ�������Ϣ��


Java// �ļ�·��: src/main/java/com/example/yourproject/service/AlibabaAiAgentService.java
package com.example.yourproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// ������������ӦDTO
// record AiTaggingRequest(String title, String description) {}
// record AiTaggingResponse(List<String> tags, String suggestedCategory) {}

@Service
public class AlibabaAiAgentService {

    private final RestClient restClient;
    private final String apiKey; // �����Ʒ����API Key
    private final String apiEndpoint; // �����Ʒ����API Endpoint

    public AlibabaAiAgentService(RestClient.Builder builder,
                                 @Value("${alibaba.agent.api.key}") String apiKey,
                                 @Value("${alibaba.agent.api.endpoint}") String apiEndpoint) {
        this.restClient = builder.baseUrl(apiEndpoint).build(); // Endpoint���ܰ�������·��
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint; // ��������־�����
    }

    public /* AiTaggingResponse */ String getSmartTagsAndCategory(String productTitle, String productDescription) {
        // ���������壬����ṹȡ�������ڰ������ϲ����AI�����������ĸ�ʽ
        // AiTaggingRequest requestPayload = new AiTaggingRequest(productTitle, productDescription);
        String requestPayloadString = String.format("{\"model\": \"qwen-plus\", \"input\":{\"prompt\":\"��Ϊ������Ʒ��Ϣ����3-5�����ʵı�ǩ��һ���Ƽ����ࣺ����-%s������-%s\"}, \"parameters\":{}}", productTitle, productDescription);


        try {
            String response = restClient.post()
                //.uri("/predict") // URI·��ȡ����������ķ���
               .header("Authorization", "Bearer " + this.apiKey) // ������������Ҫ�����֤��ʽ
               .contentType(MediaType.APPLICATION_JSON)
               .body(requestPayloadString) // ����������
               .retrieve()
               .body(String.class); // ���践��JSON�ַ����������ɽ���ΪAiTaggingResponse DTO

            // ʵ��Ӧ���У�����response�ַ���ΪAiTaggingResponse����
            // ObjectMapper objectMapper = new ObjectMapper();
            // return objectMapper.readValue(response, AiTaggingResponse.class);
            return response; // �򻯷���
        } catch (Exception e) {
            System.err.println("Error calling Alibaba AI Agent: " + e.getMessage());
            // return new AiTaggingResponse(Collections.emptyList(), "Error");
            return "{\"error\": \"���ð�����AI����ʧ��\"}";
        }
    }
}
���ּ򻯵ķ�ʽ�����˼���AI�������ض���ƽ̨�������ƣ���Ҫ��ͬʱ���������븴�ӵĶ���������ſ�ܣ����ʺϿγ���Ŀ��9. ǰ�˿��� (Vue3)ǰ�˸����û�������û����顣
����ṹ������

�������������������ṹ�����磺

src/views/�����ҳ�漶��� (�� Home.vue, Login.vue, ProductDetail.vue)��
src/components/����ſɸ��õ�UI��� (�� ProductCard.vue, Navbar.vue, SearchBar.vue)��




״̬���� (��ѡ�����Ƽ�)��

�����еȸ��Ӷȵ�Ӧ�ã��Ƽ�ʹ��Pinia��ΪVue3��״̬����⡣���ܰ������й���Ӧ�õĹ���״̬��ʹ״̬�����Ԥ�������ڵ��ԡ�


API����ģ�飺

����һ������JavaScript/TypeScriptģ�� (���� src/services/api.js �� src/services/productService.js) ����װ�Ժ��API�ĵ��á�ͨ��ʹ�� axios �������ԭ���� fetch API��
���������Խ�API�����߼���UI������룬��ߴ���Ŀ�ά���Ժ͸����ԡ�

JavaScript// ʾ��: src/services/productService.js (ʹ��axios)
import axios from 'axios';

const API_BASE_URL = process.env.VUE_APP_API_BASE_URL |


| 'http://localhost:8080/api';const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        // Authorization: `Bearer ${localStorage.getItem('user-token')}` // �����Ҫtoken��֤
    }
});

// ��������������������ǰ����token
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('user-token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});


export default {
    getProducts() {
        return apiClient.get('/products');
    },
    getProductById(productId) {
        return apiClient.get(`/products/${productId}`);
    },
    createProduct(productData) {
        return apiClient.post('/products', productData);
    },
    //... ����API����
    getWeather() {
        return apiClient.get('/weather/campus');
    },
    generateDescription(title, initialDesc) {
        return apiClient.post(`/ai/generate-description?title=<span class="math-inline">\{encodeURIComponent\(title\)\}&initialDesc\=</span>{encodeURIComponent(initialDesc)}`);
    }
};
```

����չʾ��

��Vue�����ģ���У�ʹ�� {{ }} (Mustache��ֵ) �����ݣ�ʹ�� v-for ָ����Ⱦ�б�ʹ�� v-if/v-else ����������Ⱦ��


�û����������

ʹ�� v-model ָ��ʵ�ֱ�����Ԫ�غ����״̬֮���˫�����ݰ󶨡�
���пͻ��˱�У�飬�����û����飬�����ύ�����ǰ���г������ݼ�顣


10. ����Ӧ�ú�� (Spring Boot)
ȷ���������ݿ���������������� application.properties �е����ݿ�������Ϣ��ȷ����
ȷ��Redis���������� (���������Redis)��
��������Ŀ�� backend ģ��Ŀ¼��
ͨ��Maven���У�
Bashcd backend
mvn spring-boot:run

���ߣ��ȴ����JAR�ļ���Ȼ�����У�
Bashmvn package
java -jar target/backend-0.0.1-SNAPSHOT.jar # JAR�ļ�����������Ŀ���ö���

6 ���й���ͨ�� mvn spring-boot:run ����Ӧ�õ�ʾ����
ǰ�� (Vue3)
��������Ŀ�� frontend ģ��Ŀ¼��
��װ���� (�����δ��װ)��
Bashcd frontend
npm install # ���� yarn install


����������������
Bashnpm run serve # ���� yarn serve

��ͨ������ http://localhost:8081 (�����ƶ˿�) ����ǰ�˿����������������������ع��ܡ�
����Ӧ��
���ǰ��ͨ���������������� (���� npm run serve)����ͨ���� http://localhost:8081 (������Vue CLIָ���Ķ˿�) ����ǰ�ˡ�ǰ��Ӧ�û�ͨ�����õ�API����URL (�� http://localhost:8080/api) ����ͨ�š�
���ɲ��𷽰� (�Ƽ����ڼ򻯲���)��
���Խ�ǰ�˹�����ľ�̬�ļ���dist Ŀ¼�е����ݣ����ɵ�Spring BootӦ���У���Spring Bootͳһ�ṩ������ͨ���漰��

�� vue.config.js (ǰ����Ŀ��Ŀ¼) ������ outputDir��ʹ��ָ������Ŀ�ľ�̬��ԴĿ¼������ target/classes/static ��һ���м�Ŀ¼��
JavaScript// vue.config.js
module.exports = {
  outputDir: '../backend/src/main/resources/static/dist_frontend', // ʾ��·��
  assetsDir: 'static' // ��̬��Դ��Ŀ¼
};


����Maven�� maven-resources-plugin (�ں�� pom.xml) �� frontend-maven-plugin (�ڸ� pom.xml ��ǰ�� pom.xml) ���ڹ������ʱ����ǰ�˹����������������︴�Ƶ�Spring Boot�ľ�̬��ԴĿ¼�¡�
�ο� 6 ��������ͨ�� maven-resources-plugin �� frontend/target/dist (���� vue.config.js ����� target/dist) ���Ƶ� backend/target/classes/static/��
������ô˼��ɷ��������������Ӧ�ú󣬿���ֱ��ͨ�� http://localhost:8080 ��������Ӧ�á����ַ�ʽ����ѧ����Ŀ���ԣ����˲������̣��γ�һ����һ�Ŀɲ���Ԫ��


11. ��һ�����������ʵ�� (��ѡ)
��ȫ�ԣ�

����Spring Security�����û���֤����Ȩ����
����洢���ʹ��ǿ��ϣ�㷨����BCryptPasswordEncoder����
����API�ӿڵİ�ȫ�ԣ����ֹCSRF��XSS�����ȡ�


������

��Spring Boot������ȫ���쳣������ (@ControllerAdvice, @ExceptionHandler)��ΪAPI�ṩͳһ���淶�Ĵ�����Ӧ��ʽ��


��־��¼��

���ú��ʵ���־��ܣ���Logback��Spring BootĬ�ϼ��ɣ�����־���𣬼�¼�ؼ������ʹ�����Ϣ�����ڵ��Ժ�����׷�١�


��Ԫ�����뼯�ɲ��ԣ�

Ϊ���Service��Controller��д��Ԫ���� (JUnit, Mockito)��
Ϊǰ�������д��Ԫ���� (Vue Test Utils, Jest)��
���Ǳ�дAPI���ɲ��ԡ�


API�ĵ���

ʹ��Swagger/OpenAPI (ͨ��Springdoc-openapi�ȿ�) Ϊ���API�Զ����ɽ���ʽ�ĵ�������ǰ�˵��ú��Ŷ�Э����


12. �����ų���ʾ
�������ݿ��������⣺

��ϸ��� application.properties �е� spring.datasource.url, username, password, driver-class-name �Ƿ���ȷ��
ȷ�ϴ������ݿ���������������Ҽ�����IP�Ͷ˿�������һ�¡�
������ǽ�����Ƿ�����Ӧ�÷��������ʴ������ݿ�˿ڡ�
ȷ��ʹ�õĴ���JDBC�����汾�����ݿ�汾���ݡ�


MyBatis Mapper���⣺

XML Mapper�ļ��е� namespace �Ƿ���Mapper�ӿڵ���ȫ�޶���ƥ�䡣
application.properties �е� mybatis.mapper-locations·���Ƿ���ȷָ����XML�ļ���
Mapper����������SQL�еĲ���ռλ�� (#{paramName}) �Ƿ�ƥ�䣬ע�� @Param ע���ʹ�á�
���������ݿ��SQL������MyBatis�б�д��SQL�Ƿ���ݣ����磬���ں�������ҳ�﷨�ȣ���


API��Կ�������⣺

ȷ���ⲿAPI����Կ����ȷ������ application.properties �л�ͨ�������������롣
���API��Կ�Ƿ�����Ӧ��Ȩ�ޣ��Լ��Ƿ񳬳���ʹ����


CORS (������Դ����) ���⣺

����ڿ���ʱǰ�˺ͺ�˷ֱ������ڲ�ͬ�˿ڣ����磬Vue������������8081��Spring Boot��8080�������������ֹ����������Ҫ��Spring Boot������CORS��������Controller��򷽷���ʹ�� @CrossOrigin ע�⣬������ȫ��CORS���ԡ�


Vue3����/�������⣺

���Node.js��npm/yarn�İ汾�Ƿ������ĿҪ��
ȷ��ǰ�˴��������õĺ��API�˵�URL��ȷ����
�鿴����������߹��ߵĿ���̨������ѡ�����ȡ��ϸ�Ĵ�����Ϣ��


