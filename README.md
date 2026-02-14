# Payment Gateway Service (PGS) — Frontend Developer Guide

> **Base URL:** `http://localhost:8080`  
> **API Version:** `v1`  
> **Auth Method:** API Key via `X-API-KEY` header  
> **Format:** All requests and responses are `application/json`

---

## Table of Contents

1. [Overview & User Flow](#1-overview--user-flow)
2. [Environments & Base URLs](#2-environments--base-urls)
3. [Authentication](#3-authentication)
4. [API Endpoints Reference](#4-api-endpoints-reference)
    - [Create Merchant](#41-create-merchant)
    - [Create Payment](#42-create-payment)
    - [Payment Callback (Gateway → Server)](#43-payment-callback-gateway--server)
5. [Enums & Allowed Values](#5-enums--allowed-values)
6. [Error Handling](#6-error-handling)
7. [Full Example: Step-by-Step Flow](#7-full-example-step-by-step-flow)
8. [Swagger / API Docs](#8-swagger--api-docs)
9. [Pages & UI Checklist](#9-pages--ui-checklist)

---

## 1. Overview & User Flow

PGS is a **Payment Gateway Service** that sits between a merchant's website and payment processors (ZarinPal, IDPay, PayPing). The frontend interacts with two main flows:

```
┌─────────────────────────────────────────────────────────┐
│                    MERCHANT ONBOARDING                  │
│                                                         │
│  Frontend ──POST /api/v1/merchants──► Backend           │
│           ◄── { id, name, apiKey } ──                   │
│                                                         │
│  ⚠️  Save the apiKey — it is shown ONCE                  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    PAYMENT CREATION                     │
│                                                         │
│  Frontend ──POST /api/v1/payments──► Backend            │
│           Header: X-API-KEY: <apiKey>                   │
│           Body: { amount, referenceNumber, gateway }    │
│           ◄── { paymentId, amount, status, gateway,     │
│                 referenceNumber }                        │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    CALLBACK (Server-to-Server)          │
│                                                         │
│  Payment Gateway ──POST /api/v1/payments/callback──►    │
│  Backend  (this is NOT called by the frontend)          │
│  The frontend should only READ payment status,          │
│  not trigger callbacks.                                 │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Environments & Base URLs

| Environment | Base URL                    |
|-------------|-----------------------------|
| Local Dev   | `http://localhost:8080`     |
| Swagger UI  | `http://localhost:8080/swagger-ui/index.html` |
| API Docs    | `http://localhost:8080/v3/api-docs`           |

---

## 3. Authentication

**The `/merchants` and `/payments/callback` endpoints are public (no auth required).**

**The `POST /payments` endpoint requires a merchant API key sent as a custom HTTP header:**

```
X-API-KEY: sk_abc123...
```

- The API key is returned when a merchant is created.
- It starts with `sk_` followed by a 32-character UUID string (no hyphens).
- Example: `sk_a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4`

> **Important:** There is no login/session system. The API key IS the identity. Store it securely (e.g., environment variable or secure storage — never in plain `localStorage` for production).

---

## 4. API Endpoints Reference

---

### 4.1 Create Merchant

Creates a new merchant account and returns an API key for future payment requests.

**`POST /api/v1/merchants`**

#### Headers

| Header         | Value              |
|----------------|--------------------|
| `Content-Type` | `application/json` |

#### Request Body

```json
{
  "name": "My Shop",
  "callbackUrl": "https://myshop.com/payment/callback"
}
```

| Field         | Type   | Required | Rules                                              |
|---------------|--------|----------|----------------------------------------------------|
| `name`        | string | ✅       | 3–100 characters                                   |
| `callbackUrl` | string | ✅       | Must be a valid `http://` or `https://` URL        |

#### Response — `200 OK`

```json
{
  "id": 1,
  "name": "My Shop",
  "apiKey": "sk_a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4"
}
```

| Field    | Type   | Description                                           |
|----------|--------|-------------------------------------------------------|
| `id`     | number | Merchant's database ID                                |
| `name`   | string | Merchant name                                         |
| `apiKey` | string | **Secret key — display once, ask user to copy it**    |

#### Example (fetch)

```javascript
const response = await fetch('http://localhost:8080/api/v1/merchants', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'My Shop',
    callbackUrl: 'https://myshop.com/payment/callback'
  })
});

const data = await response.json();
// data.apiKey → save this! It is only shown once.
```

---

### 4.2 Create Payment

Creates a new pending payment for a merchant. The merchant is identified by the API key in the header — **do not send merchantId in the body**.

**`POST /api/v1/payments`**

#### Headers

| Header         | Value                          |
|----------------|--------------------------------|
| `Content-Type` | `application/json`             |
| `X-API-KEY`    | `sk_a1b2c3...` (merchant key)  |

#### Request Body

```json
{
  "amount": 150000.00,
  "referenceNumber": "ORDER-2024-001",
  "description": "Order #2024-001 — 2x T-Shirt",
  "gateway": "ZARINPAL"
}
```

| Field             | Type    | Required | Rules                                                          |
|-------------------|---------|----------|----------------------------------------------------------------|
| `amount`          | decimal | ✅       | Must be > `0.01`. Max 16 digits before decimal, 2 after        |
| `referenceNumber` | string  | ✅       | Max 100 chars. **Must be globally unique** per payment         |
| `description`     | string  | ❌       | Max 255 chars                                                  |
| `gateway`         | string  | ✅       | One of: `ZARINPAL`, `IDPAY`, `PAYPING`                        |

#### Response — `201 Created`

```json
{
  "paymentId": 42,
  "amount": 150000.00,
  "status": "PENDING",
  "gateway": "ZARINPAL",
  "referenceNumber": "ORDER-2024-001"
}
```

| Field             | Type    | Description                        |
|-------------------|---------|------------------------------------|
| `paymentId`       | number  | Unique payment ID                  |
| `amount`          | decimal | Payment amount                     |
| `status`          | string  | Initial status — always `PENDING`  |
| `gateway`         | string  | Selected payment gateway           |
| `referenceNumber` | string  | Your order/reference number        |

#### Example (fetch)

```javascript
const response = await fetch('http://localhost:8080/api/v1/payments', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-API-KEY': 'sk_a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4'
  },
  body: JSON.stringify({
    amount: 150000,
    referenceNumber: 'ORDER-2024-001',
    description: 'Order #2024-001',
    gateway: 'ZARINPAL'
  })
});

if (response.status === 409) {
  // Duplicate referenceNumber — already submitted
}

const payment = await response.json();
// payment.paymentId → track this payment
```

---

### 4.3 Payment Callback (Gateway → Server)

> **⚠️ This endpoint is NOT called by the frontend.**
>
> It is called by the payment gateway (ZarinPal, IDPay, etc.) after a payment completes. It is documented here so you understand the data flow and can build a status-polling or webhook display UI.

**`POST /api/v1/payments/callback`**

This endpoint:
- Validates the request comes from a whitelisted IP
- Validates the HMAC-SHA256 signature in `X-SIGNATURE` header
- Prevents replay attacks via nonce tracking
- Updates the payment status to `SUCCESS` or `FAILED`

---

## 5. Enums & Allowed Values

### `PaymentGateway`

| Value      | Description          |
|------------|----------------------|
| `ZARINPAL` | ZarinPal gateway     |
| `IDPAY`    | IDPay gateway        |
| `PAYPING`  | PayPing gateway      |

### `PaymentStatus`

| Value       | Is Final? | Description                            |
|-------------|-----------|----------------------------------------|
| `INITIATED` | No        | Payment object created, not submitted  |
| `PENDING`   | No        | Submitted to gateway, awaiting result  |
| `SUCCESS`   | Yes ✅    | Payment confirmed                      |
| `FAILED`    | Yes ✅    | Payment failed                         |
| `CANCELED`  | Yes ✅    | Payment canceled                       |

> "Is Final" means the status will **never change** once set. You can safely stop polling when you see `SUCCESS`, `FAILED`, or `CANCELED`.

---

## 6. Error Handling

All errors return a consistent JSON structure:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "name: Merchant name must be between 3 and 100 characters",
  "path": "/api/v1/merchants",
  "timestamp": "2024-01-15 10:30:00"
}
```

### HTTP Status Codes

| Status | When it happens                                                          |
|--------|--------------------------------------------------------------------------|
| `400`  | Validation failed — missing field, wrong format, value out of range      |
| `403`  | Callback signature invalid, IP not allowed, or replay attack detected    |
| `404`  | Merchant not found (wrong API key) or payment not found                  |
| `409`  | `referenceNumber` already exists — duplicate payment                     |
| `422`  | Business rule violation (generic)                                        |
| `500`  | Unexpected server error                                                  |

### Recommended Frontend Error Handling

```javascript
async function createPayment(apiKey, payload) {
  const res = await fetch('/api/v1/payments', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-API-KEY': apiKey
    },
    body: JSON.stringify(payload)
  });

  if (!res.ok) {
    const err = await res.json();
    switch (res.status) {
      case 400: throw new Error(`Validation error: ${err.message}`);
      case 404: throw new Error('Invalid API key — merchant not found');
      case 409: throw new Error('This reference number was already used');
      default:  throw new Error(err.message || 'Unexpected error');
    }
  }

  return res.json();
}
```

### Validation Rules Quick Reference

| Field             | Endpoint          | Rule                                                |
|-------------------|-------------------|-----------------------------------------------------|
| `name`            | POST /merchants   | Required, 3–100 chars                               |
| `callbackUrl`     | POST /merchants   | Required, must start with `http://` or `https://`   |
| `amount`          | POST /payments    | Required, > 0.01, max 16 integer digits, 2 decimal  |
| `referenceNumber` | POST /payments    | Required, max 100 chars, must be unique             |
| `description`     | POST /payments    | Optional, max 255 chars                             |
| `gateway`         | POST /payments    | Required, must be a valid `PaymentGateway` enum     |
| `X-API-KEY`       | POST /payments    | Required header, must match an existing merchant    |

---

## 7. Full Example: Step-by-Step Flow

This is the complete happy path from zero to a created payment.

### Step 1 — Register a Merchant

```http
POST /api/v1/merchants
Content-Type: application/json

{
  "name": "Awesome Store",
  "callbackUrl": "https://awesomestore.com/ipg/callback"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Awesome Store",
  "apiKey": "sk_9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c"
}
```

> 💾 Store `apiKey`. This is the only time it is shown.

---

### Step 2 — Create a Payment

```http
POST /api/v1/payments
Content-Type: application/json
X-API-KEY: sk_9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c

{
  "amount": 250000,
  "referenceNumber": "ORD-88812",
  "description": "Laptop Stand",
  "gateway": "IDPAY"
}
```

**Response `201 Created`:**
```json
{
  "paymentId": 7,
  "amount": 250000.00,
  "status": "PENDING",
  "gateway": "IDPAY",
  "referenceNumber": "ORD-88812"
}
```

---

### Step 3 — Redirect User to Gateway

After creating the payment, redirect the user to the payment gateway using the `referenceNumber` and `gateway` details you stored. The gateway will eventually call back to the server's `/api/v1/payments/callback` endpoint automatically.

---

### Step 4 — Show Payment Result

Poll or wait for a webhook event, then display the final payment status (`SUCCESS`, `FAILED`, or `CANCELED`) to the user.

---

## 8. Swagger / API Docs

The live interactive API documentation is available while the backend is running:

| Resource       | URL                                               |
|----------------|---------------------------------------------------|
| Swagger UI     | `http://localhost:8080/swagger-ui/index.html`     |
| OpenAPI JSON   | `http://localhost:8080/v3/api-docs`               |

Use Swagger UI to test endpoints directly in the browser without writing any code.

---

## 9. Pages & UI Checklist

Based on this API, here are the pages the frontend needs to build:

### Page 1 — Merchant Registration

- Form with `name` (text input) and `callbackUrl` (URL input)
- On success: display the `apiKey` prominently with a **"Copy" button**
- Show a clear one-time warning: *"This key will not be shown again"*
- Validate `callbackUrl` format on the client before submitting

### Page 2 — Create Payment

- Requires the merchant's `apiKey` (can be pre-filled if stored from registration)
- Form fields: `amount` (number), `referenceNumber` (text), `description` (text, optional), `gateway` (dropdown)
- Dropdown options for `gateway`: `ZARINPAL`, `IDPAY`, `PAYPING`
- On success (`201`): show `paymentId`, `status`, `referenceNumber`
- On `409 Conflict`: show "This reference number already exists"
- On `404`: show "Invalid API key"

### Page 3 — Payment Status Display

- Show `paymentId`, `referenceNumber`, `status`, `amount`, `gateway`
- Color-code status badges:
    - `PENDING` → yellow / orange
    - `SUCCESS` → green
    - `FAILED` → red
    - `CANCELED` → grey

### Global — Error Display

- All API errors return `{ status, error, message, path, timestamp }`
- Show `message` to the user in a toast or inline error
- Never expose raw stack traces or internal error details

---

## Quick Reference Card

```
┌────────────────────────────────────────────────────────────────┐
│  ENDPOINT                   METHOD  AUTH          STATUS       │
├────────────────────────────────────────────────────────────────┤
│  /api/v1/merchants          POST    None          200 OK       │
│  /api/v1/payments           POST    X-API-KEY     201 Created  │
│  /api/v1/payments/callback  POST    HMAC Sig      200 OK       │
│  /swagger-ui/**             GET     None          —            │
│  /v3/api-docs/**            GET     None          —            │
└────────────────────────────────────────────────────────────────┘
```

---

*README generated for PGS v1.0 — Spring Boot 3 / Java 17*