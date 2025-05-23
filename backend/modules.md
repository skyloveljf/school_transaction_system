### ģ�飺��Ʒ���� (Product Module) - API �˵�

#### ���ܵ㣺��ȡ������Ʒ/������Ʒ (GetAllProducts)
*   **���Ĺ���**: `GET /api/products`�������ӿڣ���ȡ��Ʒ�б�֧�ֹؼ��֡�����ID���۸����䡢״̬���ˡ�Ĭ�ϲ�ѯAVAILABLE״̬��
*   **��Ҫ����**: 
    *   `ProductController.getAllProducts(...)`
    *   ���� `ProductService.searchProducts(...)` �� `ProductService.getAllProductsWithDetails(...)`��
    *   �� `Product` ʵ���б�תΪ `ProductResponseDto` �б�
*   **����**: `ProductResponseDto`, `ProductService`��
*   **Ǳ������/�Ż���**: 
    *   Controller�ж���Ч`status`�����Ĵ������鷵��400��
    *   ȷ��Service�㷽��ǩ����Controller����ƥ�䡣
*   **��������**: `ProductController` �ж�Ӧ������

#### ���ܵ㣺��ȡ������Ʒ���� (GetProductById)
*   **���Ĺ���**: `GET /api/products/{id}`�������ӿڣ�����ID��ȡ��Ʒ���飬�������������
*   **��Ҫ����**:
    *   `ProductController.getProductById(@PathVariable Long id)`
    *   ���� `ProductService.getProductById(id)` (Service�ڲ�Ӧ����Mapper���������)��
    *   ������Ʒδ�ҵ� (404)��
    *   �� `Product` ʵ��תΪ `ProductResponseDto`��
*   **����**: `ProductResponseDto`, `ProductService`��
*   **Ǳ������/�Ż���**: ȷ��Service������������߼���
*   **��������**: `ProductController` �ж�Ӧ������

#### ���ܵ㣺��������Ʒ (CreateProduct)
*   **���Ĺ���**: `POST /api/products`����Ҫ��֤����������Ʒ���Զ��������������δ�ṩ����
*   **��Ҫ����**:
    *   `ProductController.createProduct(@Valid @RequestBody ProductCreateRequest createRequest)`
    *   `ProductController.getCurrentUser()` ��ȡ��ǰ�û���
    *   ���� `ProductService.createProduct(ProductCreateRequest, User)`��
    *   Service�㴦�����У�顢AI��������������Ĭ��ֵ���־û���
    *   ����201 Created������Ʒ�� `ProductResponseDto`��
*   **����**: `ProductCreateRequest`, `ProductResponseDto`, `User` (model), `ProductService`, `UserService` (for `getCurrentUser`), `CategoryMapper`, `ProductMapper`, `DeepSeekService`.
*   **Ǳ������/�Ż���**:
    *   `ProductController.getCurrentUser()` �ľ���ʵ�������Spring Security���á�
    *   `ProductServiceImpl` �� `@CacheEvict` �� `key` ���ʽ������Ҫ������
    *   `ProductCreateRequest` ���Ƿ�Ӧ���� `stock` �ֶΣ�Service����δ����档
    *   ȷ�� `DeepSeekService` ��ȷע���ʹ�á�
*   **��������**: `ProductController` �ж�Ӧ����, `ProductService.createProduct` (�ӿ���ʵ��)��

#### ���ܵ㣺������Ʒ��Ϣ (UpdateProduct)
*   **���Ĺ���**: `PUT /api/products/{id}`����Ҫ��֤���������߲��ܸ��¡�����ָ����Ʒ��Ϣ��
*   **��Ҫ����**:
    *   `ProductController.updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest updateRequest)`
    *   `ProductController.getCurrentUser()` ��ȡ��ǰ�û���
    *   ���� `ProductService.updateProduct(Long, ProductUpdateRequest, User)`��
    *   Service�㴦����Ʒ������У�顢Ȩ��У�顢�·���IDУ�顢�ֶθ��¡��־û���
    *   ����200 OK�͸��º���Ʒ�� `ProductResponseDto`��
*   **����**: `ProductUpdateRequest`, `ProductResponseDto`, `User` (model), `ProductService`, `UserService`, `CategoryMapper`, `ProductMapper`.
*   **Ǳ������/�Ż���**:
    *   Controller�ж�Service�㷵��null���жϷ�֧���ܶ��ࣨ��ȫ���쳣�������ƣ���
    *   Service���ֶθ����߼���title, description, imageUrl, price����ϸ�ڣ����ַ�����nullֵ������ȷ�Ϸ���ҵ��Ԥ�ڡ�
    *   **��Ҫ��`ProductUpdateRequest` �� `ProductServiceImpl` ��Ŀǰȱ�ٶ���Ʒ��� (stock) �ĸ��´���**
*   **��������**: `ProductController` �ж�Ӧ����, `ProductService.updateProduct` (�ӿ���ʵ��)��

#### ���ܵ㣺ɾ����Ʒ (DeleteProduct)
*   **���Ĺ���**: `DELETE /api/products/{id}`����Ҫ��֤���������߲���ɾ����ɾ��ָ����Ʒ��
*   **��Ҫ����**:
    *   `ProductController.deleteProduct(@PathVariable Long id)`
    *   `ProductController.getCurrentUser()` ��ȡ��ǰ�û���
    *   ���� `ProductService.deleteProduct(Long, User)`��
    *   Service�㴦����Ʒ������У�顢Ȩ��У�顢ҵ�����У�飨�Ƿ���δ��ɶ��������־û�ɾ����
    *   ����204 No Content��
*   **����**: `User` (model), `ProductService`, `UserService`, `ProductMapper`, `OrderMapper`, `Order` (model), `OrderStatus` (enum).
*   **Ǳ������/�Ż���**:
    *   ȷ�� `OrderMapper.findByProductId` ����ʵ�ִ�������ȷ��
    *   ȷ�� `Order` ʵ�弰 `OrderStatus` ö��֧��ҵ�����У�顣
*   **��������**: `ProductController` �ж�Ӧ����, `ProductService.deleteProduct` (�ӿ���ʵ��)�� 