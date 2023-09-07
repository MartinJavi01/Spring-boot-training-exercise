# Simple shopping web project
Small project that emulates a simple shopping web with product, orders and inventory systems. Secured with Spring security and JWT and with multiple microservices being connected with Feign and having an Eureka service as the core conection, managing all the calls to the APIs through an API-Gateway.

### Mongo db
The product service requires a MongoDB databse instance called "product_db" with a "Products" collection, furthermore a user is required, to create one open the terminal at the bottom of MongoDB Compass and enter the command `use product_db` and then add the user by entering the command `db.createUser({user: "product", pwd: "product", roles: ["readWrite"]})`.

### Mysql
The order and inventory services require both a database instace called "order_db" and "inventory_db" respectivelly, as well as a user. Username "order" and passowrd "order_123" for order_db and username "inventory" and password "inventory_123" for inventory_db.

## Project execution
Once all the database requirements have been met, the first service to be executed is the configuration-service, then the eureka-service, and the order for the rest of the service does not matter at all.

I recommend to use a API-caller application like Postman to try the project endpoints. Remember that most endpoints need an authorization header, and that the created users will always be inactive at first, only a admin user is allowed to change that status (It's recommended to create a admin user in user_db at first).

### Willing to change the configuration by your own?
In this case you will have to edit the files located at the "config-service" folder, the ones you will be looking for are "product-service.yml", "order-service.yml" and "inventory-service.yml".

## Microservices (Used ports and dependencies)
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

## Routes
### Product-Service
- **Retrieve all the products**
  - `GET /products`
- **Retrieve all the products by id**
  - `GET /products/{id}`
- **Retrieve a products price by its id**
  - `GET /products/{id}/price`
- **Add a product**
  - `POST /products`
- **Edit an existing product by its id**
  - `PUT /products/{id}`
- **Remome an existing product by its id**
  - `DELETE /products/{id}`

### Inventory-Service
- **Retrive all the inventory instances**
  - `GET /inventory`
- **Retrieve an inventory by its products skuCode**
  - `GET /inventory/{skuCode}`
- **Retrieve an inventory stock by its products skuCode**
  - `GET /inventory/{skuCode}/quantity`
- **Add new inventory instance from a product**
  - `POST /inventory/new/{skuCode}`
- **Modify a products inventory stock by its skuCode**
  - `PUT /inventory/{skuCode}`
- **Delete a inventory instance by its products skuCode**
  - `DELETE /inventory/{skuCode}`

### Order-Service
- **Retrieve all the orders**
  - `GET /orders`
- **Retrieve an order by its id**
  - `GET /orders/{skuCode}`
- **Add an order by a products skuCode**
  - `POST /orders/{id}`
- **Add new product to an existing order**
  - `PUT /orders/{orderNumber}/add/{skuCode}`
- **Delete an order by its id**
  - `DELETE /orders/{id}`
- **Delete a product from an existing order**
  - `DELETE /orders/{orderNumber}/remove/{skuCode}`
