# OrderCraft

OrderCraft is a small manufacturing order management application for a college project. It uses Spring Boot and Java for the backend, Angular for the web screens, and MySQL for data storage.

## Features

- Customer and finished-product records
- Email and password sign-up with Admin and Staff roles
- Raw-material stock levels and reorder warnings
- Product bill of materials (BOM)
- Manufacturing orders with material availability checks
- Purchase orders that add stock when marked received
- Production progress from in production to ready to completed
- Invoices and payment tracking

## Run locally

1. Install Java 17 or newer, Node.js, and MySQL. The backend includes Maven Wrapper, so a separate Maven install is not required.
2. Create the database by running `database/create_database.sql` in MySQL Workbench.
3. Start the backend by following `backend/README.md`.
4. In another terminal, start the Angular app by following `frontend/README.md`.
5. Open `http://localhost:4200` in your browser.

## Folder guide

- `backend/` — Spring Boot API and database access
- `frontend/` — Angular screens
- `../sources/` — synced project references; leave these files untouched

The first screen is a straightforward admin dashboard. The project is intentionally kept small so its tables and workflow are easy to explain.

See [docs/PROJECT_GUIDE.md](docs/PROJECT_GUIDE.md) for the database relationships and a suggested college demonstration flow.
