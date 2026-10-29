# OrderCraft frontend

## Requirements

- Node.js 22.22.3 or newer on the 22.x line, or Node.js 24.15.0 or newer on the 24.x line
- The Spring Boot backend running at `http://localhost:8080`

## Start the Angular app

From this `frontend` folder, run:

```powershell
npm install
npm start
```

Then open `http://localhost:4200` in your browser. The app talks to the backend at `http://localhost:8080/api`.

Use the sign-up screen to create the first Admin account, or sign in with an existing account. After signing in, use the sidebar to manage customers, products, raw materials, BOM details, orders, purchase orders, and invoices. The dashboard shows order and stock counts. Prices use Indian rupees and dates use day/month/year format.
