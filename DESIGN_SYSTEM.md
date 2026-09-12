# Jakarta EE Modular UI Design System Rules

This document governs the UI/UX architecture, component structure, modular asset organization (CSS & JS), and language standards for the `OnlineBookstore` project. All future web pages and components MUST adhere strictly to these rules.

---

## 1. Modular Asset Architecture (`web/css/` & `web/js/`)

### CSS Modular Structure (`web/css/`)
- Do NOT write monolithic CSS files. Separate styles by domain responsibility:
  - `css/global.css`: Base resets, `:root` design tokens, container grids, typography, button primitives, form fields, modal base primitives, footer.
  - `css/navbar.css`: Header layout, brand logo styling, navigation menu links, admin navbar variant, role badges.
  - `css/books.css`: Book card grid, book covers, pricing tags, category badges, standalone & modal book detail specs.
  - `css/admin.css`: Admin stats cards, inventory data tables, table rows, action buttons.

### JS Modular Structure (`web/js/`)
- Do NOT write monolithic JS files. Separate script logic by feature:
  - `js/core.js`: Role state management (`getUserRole`, `setUserRole`, `toggleUserRole`), RBAC page guard (`checkAdminAccessGuard`), formatters (`formatCurrency`, `escapeHtml`), modal helpers, demo data provider.
  - `js/books.js`: Customer catalog loading (`fetchBooks`, `renderBooks`, `createBookCardHTML`), live search listener, modal detail population (`openBookDetailModal`, `loadStandaloneBookDetail`).
  - `js/admin-books.js`: Admin inventory table rendering (`fetchAdminBookTable`, `renderAdminTable`), book creation/edit form submit (`handleAddBookSubmit`), book deletion (`deleteBookAdmin`).

---

## 2. Jakarta EE Component & Page Architecture
- **Component Reusability (`<jsp:include>`)**:
  - All shared layout elements MUST be extracted into reusable JSP components under `WEB-INF/includes/`:
    - `WEB-INF/includes/head.jsp`: Imports Bootstrap Icons and all modular CSS files (`global.css`, `navbar.css`, `books.css`, `admin.css`).
    - `WEB-INF/includes/customer-header.jsp`: Customer navbar layout.
    - `WEB-INF/includes/admin-header.jsp`: Admin navbar layout.
    - `WEB-INF/includes/footer.jsp`: Reusable footer.
- **Page Separation Directory (`/pages/`)**:
  - Customer pages: `/pages/customer/` (`home.jsp`, `books.jsp`, `book-detail.jsp`, `cart.jsp`).
  - Admin pages: `/pages/admin/` (`dashboard.jsp`, `books.jsp`, `users.jsp`).

---

## 3. Language Standard: 100% English
- All user-facing text, page titles, section headings, buttons, table headers, form labels, input placeholders, status badges, modal text, and notifications MUST be written strictly in **English**.
