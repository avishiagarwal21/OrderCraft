# OrderCraft project guide

## What the application does

OrderCraft (project P_001 in the supplied project list) helps a small workshop follow a customer order from creation through bill-of-material planning, purchase orders, production, invoicing, and payment. The Angular screens send requests to a Spring Boot API, and the API stores records in MySQL.

Users sign in or create an account before opening the dashboard. The first account registered for a new database is Admin; later accounts are Staff. Admin can view the account list and delete master records. Passwords are stored as secure hashes, and sign-in tokens expire after eight hours.

## Main database records

| Record | What it stores |
| --- | --- |
| Customer | Customer contact details |
| Product | A finished item the workshop makes and its unit price |
| Raw material | Material name, unit, current stock, and reorder level |
| Bill of materials | The quantity of each raw material needed for one product |
| Customer order | Customer, product, quantity, dates, and production status |
| Purchase order | A supplier order for a raw material |
| Invoice | Amount due for a completed customer order and payment status |
| App user | Name, email, hashed password, and Admin or Staff role |
| Auth token | Sign-in token hash and expiry time |

The database relationships are straightforward: a customer can have many orders; a product can have many orders and BOM items; a raw material can appear in many BOM items and purchase orders; a completed order can have one invoice. Hibernate creates these tables from the Java entity classes when the backend first connects to MySQL.

## Order flow

1. Add a customer and a finished product.
2. Add the raw materials and their current stock.
3. Add BOM quantities for the product. For example, a chair might need 2.5 kg of wood and 8 screws.
4. Create an order for a chosen quantity. The order starts as `PENDING`.
5. Check materials. OrderCraft multiplies each BOM quantity by the order quantity and compares it with stock.
6. If material is short, the order moves to `MATERIALS_REQUIRED`. Create a purchase order and mark it received to add stock, then check the order again.
7. When stock is available, OrderCraft deducts the required amount and moves the order to `IN_PRODUCTION`.
8. Mark it `READY`, then `COMPLETED`. Create the invoice and record payment.

## Suggested college demonstration

Create the first account to get the Admin role. Use one product and one material so the calculation is easy to explain. For example, set the BOM to 2 kg of wood per stool, add 3 kg in stock, and create an order for 2 stools. The system should show a 1 kg shortage. Receive a purchase order for at least 1 kg, recheck the order, and continue through production and payment. A second account signs in as Staff and can use the order workflow without the Admin-only delete and account-list controls.

## Project layout

- `backend/` — Java entities, repositories, services, and REST controllers
- `frontend/` — Angular pages and API connection
- `docs/` — this walkthrough

The current build is a single-workshop demo with a basic Admin and Staff sign-in flow. It does not include online payment processing, GST calculation, or multi-workshop accounts.
