use onlinebookstore;
go

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'categories')
BEGIN
    CREATE TABLE categories (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE,
        description NVARCHAR(500) NULL,
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'authors')
BEGIN
    CREATE TABLE authors (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        bio NVARCHAR(MAX) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END;
GO


IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'publishers')
BEGIN
    CREATE TABLE publishers (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(150) NOT NULL,
        address NVARCHAR(255) NULL,
        phone VARCHAR(20) NULL
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'books')
BEGIN
    CREATE TABLE books (
        id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        isbn VARCHAR(20) NULL UNIQUE,
        author_id INT NULL,
        category_id INT NULL,
        publisher_id INT NULL,
        price DECIMAL(12, 2) NOT NULL,
        discount_price DECIMAL(12, 2) NULL,
        stock_quantity INT NOT NULL DEFAULT 0,
        description NVARCHAR(MAX) NULL,
        cover_image VARCHAR(500) NULL,
        published_year INT NULL,
        pages INT NULL,
        language NVARCHAR(50) NOT NULL DEFAULT 'English',
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT chk_book_price CHECK (price >= 0),
        CONSTRAINT chk_book_discount CHECK (discount_price IS NULL OR (discount_price >= 0 AND discount_price <= price)),
        CONSTRAINT chk_book_stock CHECK (stock_quantity >= 0),

        CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE SET NULL,
        CONSTRAINT fk_books_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
        CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'cart')
BEGIN
    CREATE TABLE cart (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL UNIQUE,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'cart_items')
BEGIN
    CREATE TABLE cart_items (
        id INT IDENTITY(1,1) PRIMARY KEY,
        cart_id INT NOT NULL,
        book_id INT NOT NULL,
        quantity INT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT chk_cart_item_qty CHECK (quantity > 0),
        CONSTRAINT uq_cart_book UNIQUE (cart_id, book_id),
        CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
        CONSTRAINT fk_cart_items_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
    );
END;
GO


IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'orders')
BEGIN
    CREATE TABLE orders (
        id INT IDENTITY(1,1) PRIMARY KEY,
        order_code VARCHAR(50) NOT NULL UNIQUE,
        user_id INT NOT NULL,
        recipient_name NVARCHAR(100) NOT NULL,
        recipient_phone VARCHAR(20) NOT NULL,
        shipping_address NVARCHAR(255) NOT NULL,
        note NVARCHAR(500) NULL,
        total_amount DECIMAL(12, 2) NOT NULL,
        shipping_fee DECIMAL(12, 2) NOT NULL DEFAULT 0,
        final_amount DECIMAL(12, 2) NOT NULL,
        status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
        payment_method VARCHAR(30) NOT NULL DEFAULT 'COD',
        payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT chk_order_total CHECK (total_amount >= 0),
        CONSTRAINT chk_order_final CHECK (final_amount >= 0),
        CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
        CONSTRAINT chk_payment_method CHECK (payment_method IN ('COD', 'VNPAY', 'MOMO', 'BANK_TRANSFER', 'CREDIT_CARD')),
        CONSTRAINT chk_payment_status CHECK (payment_status IN ('UNPAID', 'PAID', 'REFUNDED')),
        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
    );
END;
GO


IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'order_items')
BEGIN
    CREATE TABLE order_items (
        id INT IDENTITY(1,1) PRIMARY KEY,
        order_id INT NOT NULL,
        book_id INT NULL,
        book_title NVARCHAR(255) NOT NULL,
        price DECIMAL(12, 2) NOT NULL,
        quantity INT NOT NULL,
        subtotal DECIMAL(12, 2) NOT NULL,

        CONSTRAINT chk_order_item_price CHECK (price >= 0),
        CONSTRAINT chk_order_item_qty CHECK (quantity > 0),
        CONSTRAINT chk_order_item_subtotal CHECK (subtotal >= 0),
        CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        CONSTRAINT fk_order_items_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reviews')
BEGIN
    CREATE TABLE reviews (
        id INT IDENTITY(1,1) PRIMARY KEY,
        book_id INT NOT NULL,
        user_id INT NOT NULL,
        rating INT NOT NULL,
        comment NVARCHAR(MAX) NULL,
        is_approved BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT chk_review_rating CHECK (rating >= 1 AND rating <= 5),
        CONSTRAINT uq_user_book_review UNIQUE (user_id, book_id),
        CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
        CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
END;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'payments')
BEGIN
    CREATE TABLE payments (
        id INT IDENTITY(1,1) PRIMARY KEY,
        order_id INT NOT NULL,
        transaction_code VARCHAR(100) NULL,
        payment_method VARCHAR(30) NOT NULL,
        amount DECIMAL(12, 2) NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
        payment_time DATETIME2 NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

        CONSTRAINT chk_payment_status_type CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
        CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );
END;
GO

