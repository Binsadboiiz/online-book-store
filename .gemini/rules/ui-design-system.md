# Jakarta EE Monochrome UI Design System Rules

This document governs the UI/UX architecture, component structure, styling guidelines, and language standards for the `OnlineBookstore` project. All future web pages and components MUST adhere strictly to these rules.

---

## 1. Jakarta EE Component & Page Architecture
- **Component Reusability (`<jsp:include>`)**:
  - All shared layout elements (Header, Footer, Head metadata) MUST be extracted into reusable JSP component includes under `WEB-INF/includes/`:
    - `WEB-INF/includes/head.jsp`: Common `<head>` meta tags, CSS link, page title parameter `${param.title}`.
    - `WEB-INF/includes/header.jsp`: Top navigation bar, brand logo, menu links, search trigger, and authentication actions. Accepts `${param.activePage}` for active link highlighting.
    - `WEB-INF/includes/footer.jsp`: Reusable footer with brand information and copyright.
- **Page Separation Directory (`/pages/`)**:
  - Do NOT put all UI logic or modals inside a single file.
  - Separate pages logically into dedicated JSP views:
    - `index.jsp`: Landing storefront and featured book showcase.
    - `pages/books.jsp`: Book Catalog with live search and category filters.
    - `pages/book-detail.jsp`: Standalone Book Detail view.
    - `pages/admin-books.jsp`: Inventory & Book Management panel (CRUD table).

---

## 2. Language Standard: 100% English
- All user-facing text, page titles, section headings, buttons, table headers, form labels, input placeholders, status badges, modal text, and notifications MUST be written strictly in **English**.
- Standard UI Vocabulary:
  - *Home*, *Catalog*, *Management*, *Sign In*, *Browse Catalog*, *Manage Inventory*
  - *List Price*, *Discount Price*, *In Stock*, *Out of Stock*, *Add to Cart*, *Details*, *Edit*, *Delete*
  - *ISBN Code*, *Publisher*, *Published Year*, *Page Count*, *Language*, *Stock Status*

---

## 3. Minimalist Monochrome Color Palette

| Token | Hex Value | Usage |
| :--- | :--- | :--- |
| `--bg-main` | `#fafafa` | Main page body background |
| `--bg-card` | `#ffffff` | Card, container, and modal background |
| `--text-primary` | `#09090b` | Headings, prices, body text |
| `--text-muted` | `#71717a` | Captions, metadata, author names |
| `--border-color` | `#e4e4e7` | 1px borders, dividers, input borders |
| `--border-hover` | `#09090b` | Input focus ring, hover state border |
| `--btn-bg-primary` | `#09090b` | Primary action buttons |
| `--btn-text-primary` | `#ffffff` | Primary button text |
| `--btn-hover-primary` | `#27272a` | Primary button hover fill |
| `--badge-bg` | `#f4f4f5` | Category tag fill |

---

## 4. Component Rules
- **Buttons**: Heights `40px` - `44px`, `border-radius: 6px`, `transition: all 0.2s ease-in-out`.
- **Inputs**: Crisp `1px solid #e4e4e7`, black outline focus ring (`0 0 0 1px #09090b`).
- **Cards**: Aspect ratio `3:4` cover placeholder, subtle hover elevation (`transform: translateY(-4px)`).
- **Tables**: Clean border-bottom dividers, uppercase header text (`font-size: 0.75rem`).
