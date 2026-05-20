# Movie Theater Seat Booking System

A full-stack prototype for the assignment: a 60-seat theater hall with row-based pricing and a booking flow that supports selecting up to 6 seats per transaction.

## Tech Stack

- Backend: Java 17, Spring Boot, Maven, JUnit, MockMvc
- Frontend: React, TypeScript, Vite, Vitest, Testing Library
- Storage: in-memory state for the prototype, resettable through `POST /initialize`

## Features

- Initializes 60 seats across 6 rows and 10 columns
- Shows Silver, Gold, and Platinum pricing tiers
- Books seats across different rows in one transaction
- Rejects already booked, duplicate, invalid, empty, or more-than-six seat selections
- Renders a visual seat grid with selected, available, booked, and tier-specific states
- Calculates the total price dynamically before purchase

## API

The backend runs on `http://localhost:8080`.

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/initialize` | Resets the theater to 60 available seats |
| `GET` | `/seats` | Returns all seats with status, row, column, tier, and price |
| `POST` | `/book` | Books selected seats for a user and returns total price |

Example booking request:

```json
{
  "userName": "Ada Lovelace",
  "seatIds": [15, 35]
}
```

## Run Locally

Start the backend:

```bash
cd backend
mvn spring-boot:run
```

Start the frontend in another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

If the backend is not on port `8080`, set the frontend API URL:

```bash
VITE_API_BASE_URL=http://localhost:8081 npm run dev
```

## Tests

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm test
npm run build
```

## Commit History

The implementation was committed in logical milestones:

1. `Build Spring Boot booking API`
2. `Build React seat booking UI`
3. `Document setup and verification`
