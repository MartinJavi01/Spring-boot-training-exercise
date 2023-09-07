# Proyecto tienda Springboot con microservicios
Small project that emulates a simple shopping web with product, orders and inventory systems. Secured with Spring security and JWT and with multiple microservices being connected with Feign and having an Eureka service as the core conection, managing all the calls to the APIs through an API-Gateway.

## Microservicios (Puertos y Dependencias Principales)
- **Eureka-Service (8761)**
  - Dependencias: ConfigClient, EurekaClient, DevTools.
- **Api-Gateway (8800)**
  - Dependencias: ConfigClient, Gateway, DevTools, Data JPA, JDBC, MySQL Driver, Lombok, Security, Spring Web Flux, JABX Bean, JWT.
- **Product-Service (dynamic port)**
  - Dependencias: ConfigClient, EurekaClient, Web, MongoDB, OpenFeign, DevTools, Lombok, SpringDoc.
- **Inventory-Service (dynamic port)**
  - Dependencias: ConfigClient, EurekaClient, Web, Data JPA, MySQL Driver, OpenFeign, DevTools, ModelMapper, SpringDoc.
- **Order-Service (dynamic port)**
  - Dependencias: ConfigClient, EurekaClient, Web, Data JPA, MySQL Driver, OpenFeign, DevTools, ModelMapper, SpringDoc.

## Rutas
### Product-Service
- **Retornar todos los productos**
  - `GET /products`
- **Retornar un producto por su ID**
  - `GET /products/{id}`
- **Retornar el precio de un producto por su ID**
  - `GET /products/{id}/price`
- **Agregar un producto**
  - `POST /products`
- **Agregar un producto por su ID**
  - `PUT /products/{id}`
- **Eliminar un producto por ID**
  - `DELETE /products/{id}`

### Inventory-Service
- **Retornar todos los inventarios**
  - `GET /inventory`
- **Retornar un inventario por skuCode**
  - `GET /inventory/{skuCode}`
- **Retornar un cantidad de un inventario por skuCode**
  - `GET /inventory/{skuCode}/quantity`
- **Agregar nuevo inventario**
  - `POST /inventory`
- **Agregar nuevo inventario por su skuCode**
  - `POST /inventory/new/{skuCode}`
- **Modificar cantidad de un inventario**
  - `PUT /inventory/{skuCode}`
- **Eliminar un inventario por su SkuCode**
  - `DELETE /inventory/{skuCode}`

### Order-Service
- **Retornar todos los pedidos**
  - `GET /orders`
- **Retornar un pedido por su ID**
  - `GET /orders/{id}`
- **Agregar un pedido por su ID**
  - `POST /orders/{id}`
- **Agregar nuevo Producto con el orderNumber y el skuCode**
  - `PUT /orders/{orderNumber}/add/{skuCode}`
- **Eliminar un Pedido por id**
  - `DELETE /orders/{id}`
- **Eliminar un Producto con el orderNumber y el skuCode**
  - `DELETE /orders/{orderNumber}/remove/{skuCode}`