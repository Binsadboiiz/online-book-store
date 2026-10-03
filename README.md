# Enterprise Online Bookstore System

A multi-module Enterprise Java application for online book retail, built on Jakarta EE 10 / EJB 3.2, Jakarta Faces (JSF 3.0) with PrimeFaces 14, and Microsoft SQL Server. The architecture strictly enforces clear separation of concerns across enterprise layers (EAR, EJB, WAR) and implements robust Role-Based Access Control (RBAC).

---

## Architecture Overview

The system follows an enterprise multi-module Java EE / Jakarta EE architecture packaged into an Enterprise Archive (`OnlineBookstore.ear`):

```
OnlineBookstore (EAR)
|-- OnlineBookstore-ejb (Enterprise Java Beans - Business Logic & Persistence)
|   |-- Entities (JPA / Jakarta Persistence)
|   |-- Repositories / DAOs (Data Access Layer)
|   `-- Services (Stateless Session Beans - Transactional Business Logic)
|
`-- OnlineBookstore-war (Web Tier - Presentation Layer & APIs)
    |-- JSF Backing Beans (Jakarta Faces Managed Beans)
    |-- Security Filters & PhaseListeners (RBAC & Authorization)
    |-- REST Endpoints / Swagger Integration
    `-- Facelets Templates & Web Assets (HTML5, CSS3, JS, PrimeFaces)
```

### Layer Responsibilities

1. **Enterprise JavaBeans (EJB Tier)**:
   - Handles transactional business operations via `@Stateless` session beans.
   - Encapsulates Object-Relational Mapping (ORM) using Jakarta Persistence API (JPA) against Microsoft SQL Server.
   - Encrypts credential payloads using BCrypt before persisting to storage.
   - Provides DTO projections to decouple presentation logic from internal JPA entity states.

2. **Web & Presentation Tier (WAR Tier)**:
   - Utilizes the Jakarta Faces (JSF) component framework backed by PrimeFaces 14.
   - Implements AJAX-driven dynamic rendering (`<f:ajax>`) for modular state updates (cart badge, book modal preview, stock urgency).
   - Enforces two-tier authorization protection: `AdminAuthorizationFilter` (HTTP Servlet level) and `AdminSecurityPhaseListener` (JSF Lifecycle level).

3. **Persistence & Database Tier**:
   - Microsoft SQL Server with relational integrity enforcement.
   - Indexed foreign keys, catalog taxonomy, author relations, and order transaction statuses.

---

## Core System Features

### Customer Storefront
- **Catalog Navigation & Search**: Multi-criteria book filtering by category, author, price range, and keyword search.
- **Top 12 Best-Sellers Showcase**: Dynamic query-driven grid displaying top-performing inventory in symmetrical 4-column layouts.
- **Interactive Cover Quick Actions**: Instant AJAX modal preview (`Quick View`) and single-click cart addition.
- **Stock Urgency Indicators**: Real-time visual progress indicators highlighting fast-selling inventory.
- **Cart & Order Management**: Session-persistent shopping cart, checkout workflow, shipping address management, and real-time order tracking.
- **Author Spotlight & Curated Collections**: Dedicated author profiles, curated genre collections, and verified customer reviews.

### Administration Portal
- **Dashboard & Analytics**: High-level metrics for store inventory, order volume, revenue aggregation, and low-stock alerts.
- **Book & Catalog Management**: CRUD operations for books, cover image paths, category assignments, and pricing structures.
- **Order Fulfillment Pipeline**: Order management workflow, status transitions (Pending, Processing, Shipped, Delivered, Cancelled).
- **User & Role Governance**: Account role management (`ADMIN` vs `CUSTOMER`), status suspension, and authorization auditing.

---

## Technology Stack & Dependencies

### Core Stack
- **Java Platform**: Java SE 17 / Jakarta EE 10 (EJB 3.2, JPA 3.0, JSF 3.0)
- **Application Server**: Payara Server 6.x / GlassFish Server 7.x
- **Database Engine**: Microsoft SQL Server 2019+
- **Build System**: Apache Ant / NetBeans Enterprise Project Configuration

### Dependencies (`/lib`)
- `primefaces-14.0.8-jakarta.jar`: Enterprise Component Library for JSF
- `mssql-jdbc-13.4.0.jre11.jar`: Microsoft JDBC Driver for SQL Server
- `jBCrypt-0.4.3.jar`: Password hashing library implementation
- `jjwt-api-0.13.0.jar`, `jjwt-impl-0.13.0.jar`, `jjwt-jackson-0.13.0.jar`: JSON Web Token authentication utilities

---

## Security Architecture

Security is implemented using a defense-in-depth model:

1. **Password Security**: User passwords are non-reversibly salted and hashed using BCrypt (`BCrypt.hashpw`) prior to persistence.
2. **URL-Level Access Control (`AdminAuthorizationFilter`)**:
   - Intercepts all incoming HTTP requests targeting `/pages/admin/*`.
   - Validates session state and asserts `ADMIN` role privilege.
   - Directs unauthorized requests to HTTP 403 Forbidden page or authentication prompt.
3. **JSF Lifecycle Guard (`AdminSecurityPhaseListener`)**:
   - Hooks into the RESTORE_VIEW phase of the JSF lifecycle.
   - Blocks execution of JSF postbacks or AJAX requests attempting to invoke admin-scoped backing beans without authorization.

---

## Database Setup & Initialization

### 1. Database Creation
Execute the following SQL command in Microsoft SQL Server Management Studio (SSMS) or via sqlcmd:

```sql
CREATE DATABASE OnlineBookstore;
GO
USE OnlineBookstore;
GO
```

### 2. Schema Migration & Data Seeding
Run the SQL scripts located in the `/data` directory in sequential order:

1. `/data/schema/2026_08_30_Users.sql` - Database schema for user accounts and security roles.
2. `/data/schema/2026_09_07_Books.sql` - Database schema for categories, authors, and books.
3. `/data/schema/2026_09_15_address.sql` - Database schema for user shipping addresses.
4. `/data/seed.sql` - Initial seed data for system roles and category taxonomy.
5. `/data/2026_09_18_seed_data.sql` - Seed dataset for catalog inventory, authors, and sample order history.

---

## Building and Local Deployment

### Prerequisites
- JDK 17 or higher installed and configured in system PATH.
- Payara Server 6.x or GlassFish 7.x running with a configured JDBC DataSource (`jdbc/OnlineBookstoreDS`) targeting Microsoft SQL Server.

### Deployment Instructions
1. Open the project root directory in NetBeans IDE or configure Apache Ant environment variables.
2. Verify persistence settings in `OnlineBookstore-ejb/src/conf/persistence.xml`.
3. Execute the Ant build process:
   ```bash
   ant clean EAR-compile dist
   ```
4. Deploy the generated `dist/OnlineBookstore.ear` to Payara or GlassFish server.
5. Access system endpoints:
   - Customer Storefront: `http://localhost:8080/OnlineBookstore-war/pages/customer/home.xhtml`
   - Admin Portal: `http://localhost:8080/OnlineBookstore-war/pages/admin/dashboard.xhtml`

---

## Directory Structure

```
OnlineBookstore/
├── data/                       # Database DDL schema files and SQL seed scripts
├── lib/                        # Project JAR dependencies (JDBC, BCrypt, PrimeFaces, JJWT)
├── OnlineBookstore-ejb/        # Enterprise Java Beans Module (Domain & Business Logic)
│   └── src/java/com/onlinebookstore/
│       ├── book/               # Domain entities & EJBs for books, authors, categories
│       ├── order/              # Domain entities & EJBs for shopping cart & orders
│       ├── review/             # Domain entities & EJBs for book reviews & ratings
│       └── user/               # Domain entities & EJBs for user accounts & auth
├── OnlineBookstore-war/        # Web Application Module (Presentation Tier & Controllers)
│   ├── src/java/com/onlinebookstore/
│   │   ├── admin/              # JSF Managed Beans for administrative operations
│   │   ├── auth/               # Managed Beans, Filters, and PhaseListeners for auth
│   │   ├── book/               # Managed Beans for store catalog & book details
│   │   └── cart/               # Managed Beans for shopping cart state
│   └── web/
│       ├── css/                # Stylesheets for layout & component styling
│       ├── js/                 # Client-side interactivity scripts
│       ├── pages/
│       │   ├── admin/          # Protected JSF views for administration
│       │   └── customer/       # Public & customer store JSF views
│       └── WEB-INF/            # Facelets templates, includes, and web.xml manifest
└── README.md
```

---

## Design System & Interactivity Guidelines

- **Color System**: Minimalist monochrome aesthetic (`#09090b` dark accents, `#ffffff` card surfaces, `#27272a` boundary strokes).
- **Typography**: Clean, sans-serif font hierarchy optimized for readability across devices.
- **Interactivity Engine**: 2D micro-elevation transforms (`translateY(-5px)`) combined with specular gloss sheen sweeps. 3D tilt/rotation effects are disabled per design guidelines to maintain high visual clarity.
