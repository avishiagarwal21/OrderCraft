# OrderCraft backend

## Requirements

- Java 17 or newer
- MySQL

Create the database once in MySQL Workbench by opening and running `../database/create_database.sql`, or run:

```sql
CREATE DATABASE order_craft;
```

Set the database username and password in PowerShell, then start the app from this `backend` folder:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`. The wrapper downloads Maven the first time it runs. Hibernate creates or updates the tables from the entity classes when the application starts. The connection can be changed with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables.

Create an account from the Angular sign-up screen. The first account in a new database becomes Admin; the next accounts become Staff. Passwords are hashed before storage. The API returns an eight-hour bearer token at sign-up or login, and the Angular app sends it with later requests. Only Admin accounts can delete customers, products, and materials, or view the account list.

## Main API routes

| Method | Route | Purpose |
| --- | --- | --- |
| POST | `/api/auth/signup` | Create an account and sign in; the first account is Admin, later accounts are Staff |
| POST | `/api/auth/login` | Sign in with email and password |
| GET | `/api/auth/me` | Read the signed-in account |
| POST | `/api/auth/logout` | Revoke the current sign-in token |
| GET | `/api/auth/users` | List accounts (Admin only) |
| GET / POST / PUT / DELETE | `/api/customers` and `/api/customers/{id}` | Manage customers |
| GET / POST / PUT / DELETE | `/api/products` and `/api/products/{id}` | Manage finished products |
| GET / POST / PUT / DELETE | `/api/materials` and `/api/materials/{id}` | Manage raw materials |
| GET | `/api/bom/product/{productId}` | View a product BOM |
| POST | `/api/bom` | Add a material to a product BOM |
| GET / POST | `/api/orders` | List or create manufacturing orders |
| POST | `/api/orders/{id}/check-materials` | Check the BOM stock and begin production if available |
| PUT | `/api/orders/{id}/status` | Move production from `IN_PRODUCTION` to `READY` to `COMPLETED` |
| GET / POST | `/api/purchase-orders` | List or create purchase orders |
| POST | `/api/purchase-orders/{id}/receive` | Receive a purchase order and increase stock |
| GET | `/api/invoices` | List invoices |
| POST | `/api/invoices/order/{orderId}` | Create an invoice for a completed order |
| POST | `/api/invoices/{id}/mark-paid` | Record customer payment |

## Example request bodies

Sign-up:

```json
{"fullName":"Riya Patel","email":"riya@example.com","password":"samplePass123"}
```

Login:

```json
{"email":"riya@example.com","password":"samplePass123"}
```

Customer:

```json
{"name":"Asha Sharma","email":"asha@example.com","phone":"9876543210","address":"Pune"}
```

Product:

```json
{"name":"Wooden Chair","description":"Standard dining chair","unitPrice":1250.00,"stockQuantity":0}
```

Material:

```json
{"name":"Teak wood","unit":"kg","stockQuantity":40,"reorderLevel":10}
```

BOM item:

```json
{"productId":1,"materialId":1,"quantityPerProduct":2.5}
```

Manufacturing order:

```json
{"customerId":1,"productId":1,"quantity":4,"deliveryDate":"2026-11-15"}
```

Order status update:

```json
{"status":"READY"}
```

## Demonstration flow

Add a customer, a finished product, and the product's raw materials. Add BOM quantities, then create an order and check materials. If stock is short, create and receive a purchase order for the shortage and check again. When production starts, update the order to Ready and then Completed. Create an invoice and mark it Paid.
