## Running & Trying the API

The OpenAPI contract at `kotlin/src/main/resources/openapi/time-deposit-api.yaml` is the
source of truth for both endpoints below -- the server-side routing is generated from it at
build time.

### Run it

```bash
cd kotlin
docker compose up --build
```

This starts a Postgres container (seeded by `src/main/resources/schema.sql`) and the app on
`http://localhost:8080`. Stop with `docker compose down`.

### Trigger the endpoints

```bash
# GET /v1/time-deposits -- every time deposit, each with its own withdrawals
curl http://localhost:8080/v1/time-deposits

# POST /v1/time-deposits/balance-updates -- apply this period's interest to every deposit
curl -i -X POST http://localhost:8080/v1/time-deposits/balance-updates
```

The GET response is a JSON array of `{ id, planType, balance, days, withdrawals: [{ amount, date }] }`.
The POST returns `204 No Content` on success.