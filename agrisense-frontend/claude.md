# AgriSense Frontend (React, JS) — Full Implementation Spec (Stage-by-Stage)

## 0) Objective
Build a responsive monitoring web app for AgriSense (IoT agriculture monitoring).
The app:
- lists sensors (IoT sensor inventory),
- shows sensor details,
- shows measurement data (temperature/humidity/pH style values via measurements API),
- lists alerts and filters by status,
- manages alert rules per sensor (create/update/delete),
- supports multiple screens and UI navigation (React Router),
- uses at least one separate `.css` file for styling,
- integrates with backend via async REST API calls,
- is shipped as a Docker container serving static frontend via a web server (Nginx).

Backend: Java Quarkus, hexagonal architecture. Backend is not graded, but integration is required.

---

## 1) Hard Requirements (Must-Have)
1. React + JavaScript (no TypeScript).
2. Must include and use at least one separate `.css` file.
3. Multiple views/screens + navigation (React Router or similar).
4. Async backend integration (fetch or axios) is mandatory.
5. Responsive UI usable on mobile + desktop.
6. All functionality reachable via UI (no manual URL edits needed).
7. Code organized into multiple components with clear folder structure.
8. Provide Docker container serving static frontend via a web server (Nginx recommended).
   - `docker build ...` and `docker run ...` must work in a single command each.

---

## 2) Decisions (Do Not Deviate Unless Necessary)
### Stack
- Build tool: Vite (React)
- Routing: React Router v6
- HTTP: Axios
- Styling: plain CSS (at least one external CSS file)
- Charts: OPTIONAL. Only add if asked later. Otherwise keep simple.

### UI Layout
- Desktop: left sidebar + content area
- Mobile: top bar + hamburger menu (drawer) or collapsible sidebar
- All routes must be linked in UI navigation.

---

## 3) Routes / Screens
All routes MUST be reachable via UI buttons/links.

- `/dashboard`
- `/sensors` (Sensors list)
- `/sensors/:id` (Sensor detail)
- `/measurements` (Measurements query + table)
- `/alerts` (Alerts list + filter)
- `/alerts/open` (Only OPEN alerts)
- `/settings` (API base URL test + UI preferences)
- `/profile` (simple page: “Local mode” or placeholder)

Unknown routes -> NotFound page with “Go to Dashboard”.

---

## 4) Backend API Contract (Given Endpoints)
**All endpoints use `application/json`.**
Some responses use wrappers (PagedResponse) and HATEOAS links.

### 4.1 Alerts
**Base Path:** `/api/alerts`

#### GET `/api/alerts`
Fetch paginated alerts.
Query params:
- `status` (optional): filter by `EAlertStatus`
- `page` (optional)
- `size` (optional)

Response: `200 OK` with paginated `AlertResponse` list (PagedResponse wrapper likely).

Frontend requirements:
- Alerts table/list with:
  - status
  - message/type (as available)
  - createdAt (as available)
  - sensorId or related entity (as available)
- Filter by status (dropdown).
- Pagination controls (Prev/Next) OR infinite “Load more”.

> NOTE: You did NOT provide a “resolve alert” endpoint. Therefore:
- No “Resolve” button is implemented unless backend has such endpoint.
- “Open Alerts” view is simply a filtered query using `status=OPEN` (or equivalent).

### 4.2 Alert Rules (Per Sensor)
**Base Path:** `/sensors/{sensorId}/rules`

#### POST `/sensors/{sensorId}/rules`
Create rule for sensor.
- Body: `CreateAlertRuleRequest`
- Response: `201 Created` `AlertRuleResponse`

#### GET `/sensors/{sensorId}/rules`
Get active rules for sensor.
- Response: `200 OK` list of `AlertRuleResponse`

#### PUT `/sensors/{sensorId}/rules/{ruleId}`
Update rule.
- Body: `CreateAlertRuleRequest`
- Response: `200 OK` `AlertRuleResponse`

#### DELETE `/sensors/{sensorId}/rules/{ruleId}`
Delete rule.
- Response: `204 No Content`

Frontend requirements:
- In Sensor Detail page: section “Alert Rules”
  - list rules
  - create rule (form modal or inline)
  - edit rule (prefill form)
  - delete rule (confirm dialog)

> IMPORTANT OPEN DETAIL:
We need the exact fields of `CreateAlertRuleRequest` and `AlertRuleResponse`.
If unknown, Antigravity must implement rule form with generic fields and placeholders, then you replace with correct fields.
But best: user provides sample JSON.

### 4.3 Measurements
**Base Path:** `/api/measurements`

#### POST `/api/measurements`
Create new measurement (ingest).
- Body: `CreateMeasurementRequest`:
  - `sensorId`
  - `value`
  - `unit` (optional, default "")
- Response: `202 Accepted` `{ "status": "Measurement processed successfully" }`

Frontend requirements:
- In Measurements page: “Add Measurement” mini form (sensorId, value, unit)
- After POST success, show toast/notice.

#### GET `/api/measurements`
Query measurements with filtering and pagination:
Query params:
- `fieldId` (optional)
- `from` (optional ISO 8601)
- `to` (optional ISO 8601)
- `page` (default 1)
- `size` (default 50)

Response: `200 OK` paginated `MeasurementResponse` list (PagedResponse wrapper likely).

Frontend requirements:
- Filters UI:
  - fieldId input
  - from datetime input
  - to datetime input
  - page size select
- Results table:
  - sensorId (if present)
  - value
  - unit
  - timestamp / createdAt (as present)
- Pagination controls.

> NOTE:
This API uses `fieldId` not `sensorId`. This might be domain-specific.
If you actually want “measurements per sensor”, we either:
- map sensor -> fieldId in backend domain, OR
- keep UI filter as `fieldId` exactly as API expects.

### 4.4 Sensors
**Base Path:** `/api/sensors`

#### POST `/api/sensors`
Create sensor (CreateSensorRequest)
Response: `201 Created` SensorResponse (with HATEOAS links)

#### GET `/api/sensors`
List all sensors
Response: `200 OK` list SensorResponse (with HATEOAS links)

#### GET `/api/sensors/{id}`
Get sensor by id
Response: `200 OK` SensorResponse

#### PUT `/api/sensors/{id}`
Update sensor
Response: `200 OK` SensorResponse (HATEOAS links)

#### DELETE `/api/sensors/{id}`
Delete sensor
Response: `204 No Content`

Frontend requirements:
- Sensors list page:
  - table/cards with sensor name/type/id/status (whatever exists)
  - search box
  - buttons: “Create”, “Edit”, “Delete”, “Detail”
- Sensor detail page:
  - sensor info (read-only panel)
  - quick actions: edit/delete
  - alert rules section (CRUD)
  - “Related measurements” link to Measurements page with prefilled filter (best-effort)

> NOTE:
You earlier mentioned “devices that can be turned on/off and tagged ACTIVE/MALFUNCTION”.
But the provided endpoints do NOT include a Device API. Only Sensors exist.
Therefore the UI will implement “Sensors management + rules + measurements + alerts”.
If you truly have separate “devices”, you must provide device endpoints. Otherwise we use “sensor == device” conceptually.

---

## 5) Data Handling Notes (PagedResponse + HATEOAS)
Backend uses:
- PagedResponse wrapper for paginated lists
- HATEOAS links for sensors

Frontend MUST:
- Implement a tolerant parser:
  - If response is an array -> treat as list
  - If response has `{ content: [...] }` -> use `content`
  - If response has `{ _embedded: { ... } }` -> extract arrays inside (best effort)
  - For pagination: try to read `page`, `size`, `totalPages`, `totalElements` if available
- Avoid hard-crashing on shape mismatch; show error state with raw message.

---

## 6) UX Requirements
- Loading state on every async call.
- Error state with Retry.
- Empty state messages.
- Confirm dialog on delete actions.
- Responsive layout:
  - tables turn into stacked cards on small screens or enable horizontal scroll.

---

## 7) Code Structure (Mandatory)
src/
│
├── api/
│ ├── client.js
│ ├── alerts.js
│ ├── sensors.js
│ ├── rules.js
│ └── measurements.js
│
├── components/
│ │
│ ├── layout/
│ │ ├── AppLayout.jsx
│ │ ├── Sidebar.jsx
│ │ ├── Topbar.jsx
│ │ └── MobileNav.jsx
│ │
│ ├── common/
│ │ ├── Button.jsx
│ │ ├── Card.jsx
│ │ ├── Badge.jsx
│ │ ├── Input.jsx
│ │ ├── Select.jsx
│ │ ├── Modal.jsx
│ │ ├── Loader.jsx
│ │ ├── ErrorState.jsx
│ │ ├── EmptyState.jsx
│ │ └── Pagination.jsx
│ │
│ ├── alerts/
│ │ ├── AlertList.jsx
│ │ ├── AlertFilters.jsx
│ │ └── AlertRow.jsx
│ │
│ ├── sensors/
│ │ ├── SensorList.jsx
│ │ ├── SensorCard.jsx
│ │ ├── SensorForm.jsx
│ │ └── SensorHeader.jsx
│ │
│ ├── rules/
│ │ ├── RuleList.jsx
│ │ ├── RuleForm.jsx
│ │ └── RuleRow.jsx
│ │
│ └── measurements/
│ ├── MeasurementTable.jsx
│ ├── MeasurementFilters.jsx
│ └── MeasurementForm.jsx
│
├── pages/
│ ├── Dashboard.jsx
│ ├── Sensors.jsx
│ ├── SensorDetail.jsx
│ ├── Measurements.jsx
│ ├── Alerts.jsx
│ ├── OpenAlerts.jsx
│ ├── Settings.jsx
│ ├── Profile.jsx
│ └── NotFound.jsx
│
├── routes/
│ └── AppRouter.jsx
│
├── styles/
│ └── main.css
│
├── utils/
│ ├── format.js
│ └── apiShape.js
│
├── App.jsx
└── main.jsx


---

## 8) CSS Requirement (Mandatory)
Use `src/styles/main.css` and import it in `main.jsx`.

CSS must define:
- layout primitives (container, grid, sidebar)
- typography
- buttons/inputs
- badges (status colors)
- tables/cards
- responsive breakpoints

No UI library needed. Keep consistent spacing.

---

## 9) Docker Requirement (Mandatory)
Container must serve the static build via Nginx.

### Must support React Router refresh
Nginx config must route unknown paths to `/index.html`.

Single-command run:
- `docker build -t agrisense-frontend .`
- `docker run -p 8080:80 agrisense-frontend`

---

# 10) Stage-by-Stage Implementation Plan (Antigravity MUST Follow)

## Stage 1 — Project Bootstrap (Scaffold)
**Goal:** runnable React app with routing + CSS baseline.

Tasks:
1. Create Vite React project (JavaScript).
2. Install deps:
   - `react-router-dom`
   - `axios`
3. Create folder structure as specified.
4. Add `src/styles/main.css` and import in `main.jsx`.
5. Implement basic layout:
   - Sidebar/Topbar
   - Responsive behavior (sidebar collapses on mobile)
6. Implement router with all routes returning placeholder pages.
7. Add NotFound route.

Exit criteria:
- `npm run dev` works
- navigation links render
- CSS clearly applied from separate file

---

## Stage 2 — API Client Foundation
**Goal:** stable API layer with env-based base URL.

Tasks:
1. Add `.env.example` containing:
   - `VITE_API_BASE_URL=http://localhost:8080`
2. Create axios client in `api/client.js`:
   - baseURL from env
   - timeout
   - error normalization helper
3. Implement API modules:
   - `alerts.js`: `getAlerts(params)`
   - `sensors.js`: CRUD
   - `rules.js`: CRUD per sensor
   - `measurements.js`: `getMeasurements(params)`, `postMeasurement(body)`
4. Add “Test connection” button on Settings page:
   - ping any safe GET (e.g., `/api/sensors`) and show success/fail.

Exit criteria:
- Changing base URL in env changes target
- Failed calls show readable errors

---

## Stage 3 — Sensors CRUD Screens
**Goal:** Sensors page + Sensor detail + CRUD modals.

Tasks:
1. Sensors list (`/sensors`):
   - fetch GET `/api/sensors`
   - show list/table/cards
   - search filter
2. Create sensor modal:
   - POST `/api/sensors`
3. Edit sensor modal:
   - PUT `/api/sensors/{id}`
4. Delete sensor:
   - DELETE `/api/sensors/{id}` with confirm
5. Sensor detail (`/sensors/:id`):
   - GET `/api/sensors/{id}`
   - show sensor properties + actions

Exit criteria:
- All CRUD reachable from UI
- Works on mobile (usable)

---

## Stage 4 — Alert Rules (Per Sensor)
**Goal:** Rule management inside Sensor Detail.

Tasks:
1. In Sensor Detail, add “Alert Rules” section:
   - GET `/sensors/{sensorId}/rules`
2. Add create rule form -> POST
3. Add edit rule form -> PUT
4. Add delete rule -> DELETE
5. Form validation (minimum required fields).

Open requirement:
- You must obtain the exact schema of `CreateAlertRuleRequest`.
If missing, implement generic fields, then adjust quickly once schema is provided.

Exit criteria:
- Full rule CRUD from UI
- No manual URL hacking needed

---

## Stage 5 — Alerts Screens
**Goal:** Alerts list + Open alerts filter view.

Tasks:
1. `/alerts`: GET `/api/alerts` with:
   - status filter dropdown
   - pagination (page/size)
2. `/alerts/open`: same but forces `status=OPEN` (or appropriate enum value)
3. Use shared `AlertList` component.

Exit criteria:
- status filter works
- open alerts view works
- responsive list

---

## Stage 6 — Measurements Screens
**Goal:** Measurements query + ingest form.

Tasks:
1. `/measurements`:
   - filters: fieldId, from, to, page, size
   - GET `/api/measurements`
   - render table/cards
2. “Add Measurement” form:
   - POST `/api/measurements` with sensorId/value/unit
   - show success message
3. Optional convenience:
   - From Sensor Detail: a button “View measurements” navigates to `/measurements` and pre-fills fieldId or shows note if mapping unknown.

Exit criteria:
- query works
- post works
- errors handled

---

## Stage 7 — Dashboard
**Goal:** high-level monitoring overview.

Tasks:
1. KPI cards:
   - total sensors (from `/api/sensors`)
   - open alerts (from `/api/alerts?status=OPEN`)
2. Recent alerts list
3. Quick navigation buttons.

Exit criteria:
- dashboard useful and fast
- no broken states

---

## Stage 8 — Polish + Responsiveness + QA
**Goal:** make it grade-safe.

Tasks:
1. Ensure every feature is reachable from navigation.
2. Ensure mobile usability:
   - nav toggle
   - tables not unusable
3. Add consistent spacing, typography, badges.
4. Add loading/empty/error states everywhere.
5. Remove console spam; handle errors cleanly.

Exit criteria:
- passes all hard requirements
- no dead routes
- looks consistent

---

## Stage 9 — Dockerization (Final Delivery)
**Goal:** Docker image serves built frontend via Nginx and React Router works.

Tasks:
1. Add Dockerfile multi-stage:
   - Node build stage -> `npm ci` + `npm run build`
   - Nginx stage -> copy `/dist` to `/usr/share/nginx/html`
2. Add `nginx.conf` that routes SPA:
   - `try_files $uri /index.html;`
3. Verify:
   - `docker build -t agrisense-frontend .`
   - `docker run -p 8080:80 agrisense-frontend`
   - open `/dashboard`, `/sensors`, refresh deep routes (no 404)

Exit criteria:
- single-command run works
- deep link refresh works

---

# 11) Acceptance Checklist (Grading-Focused)
- [ ] React + JS
- [ ] Separate CSS file used
- [ ] Multiple screens + navigation
- [ ] Async API integration working
- [ ] Responsive (mobile + desktop usable)
- [ ] All features reachable via UI
- [ ] Multiple components + clean structure
- [ ] Docker container serves static build via Nginx
- [ ] SPA routing works in Docker (refresh deep route)

---

# 12) Remaining Open Inputs (Must Confirm Once)
Antigravity should confirm:
1. What are the exact enum values for alert status (`EAlertStatus`)? (e.g., OPEN, RESOLVED, ACTIVE, etc.)
2. What is the exact JSON shape of:
   - SensorResponse (fields)
   - AlertResponse (fields)
   - AlertRuleResponse + CreateAlertRuleRequest (fields)
   - MeasurementResponse (fields)
3. Is backend base URL `http://localhost:8080` or includes `/api` prefix?
   - In this spec, endpoints already include `/api/...` so base URL should be host-only, e.g. `http://localhost:8080`

END.