-- ============================================================================
-- Database Seed Data Script for OnlineBookstore (Idempotent & Multi-run Safe)
-- Database Engine: Microsoft SQL Server (T-SQL)
-- Date: 2026-09-18
-- All sample content is in 100% English.
-- ============================================================================

USE onlinebookstore;
GO

/*
-------------------------------------------------------------------------------
-- OPTIONAL RESET SECTION
-- Uncomment the block below if you want to wipe all seed data and reset IDs:
-------------------------------------------------------------------------------
DELETE FROM payments;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM cart;
DELETE FROM reviews;
DELETE FROM addresses;
DELETE FROM books;
DELETE FROM publishers;
DELETE FROM authors;
DELETE FROM categories;
DELETE FROM users;

DBCC CHECKIDENT ('users', RESEED, 0);
DBCC CHECKIDENT ('categories', RESEED, 0);
DBCC CHECKIDENT ('authors', RESEED, 0);
DBCC CHECKIDENT ('publishers', RESEED, 0);
DBCC CHECKIDENT ('books', RESEED, 0);
DBCC CHECKIDENT ('addresses', RESEED, 0);
DBCC CHECKIDENT ('cart', RESEED, 0);
DBCC CHECKIDENT ('cart_items', RESEED, 0);
DBCC CHECKIDENT ('orders', RESEED, 0);
DBCC CHECKIDENT ('order_items', RESEED, 0);
DBCC CHECKIDENT ('reviews', RESEED, 0);
DBCC CHECKIDENT ('payments', RESEED, 0);
GO
*/

-- ----------------------------------------------------------------------------
-- 1. USERS
-- Admin Account:
--   Username: admin
--   Email: admin@onlinebookstore.com
--   Password: Admin123!
--   Role: MANAGER
--
-- Customer Accounts (Password for all: Customer123!):
--   john_doe        (john.doe@example.com)
--   jane_smith      (jane.smith@example.com)
--   alice_johnson   (alice.johnson@example.com)
--   robert_brown    (robert.brown@example.com)
--
-- Note: Passwords are BCrypt hashed (cost factor 12).
-- ----------------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, email, password, full_name, role, is_active)
    VALUES ('admin', 'admin@onlinebookstore.com', '$2a$12$mCiBlWAYUrjkGAaLhZlC1ugpCJjF942rOdiwy209lKm2PrnEJX47O', N'System Administrator', 'MANAGER', 1);
END;

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'john_doe')
BEGIN
    INSERT INTO users (username, email, password, full_name, role, is_active)
    VALUES ('john_doe', 'john.doe@example.com', '$2a$12$GErEPMB8n48NN8O56krgoeZdPnSvMJr.dLin1QWpeR9wpwNs4aAnS', N'John Doe', 'CUSTOMER', 1);
END;

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'jane_smith')
BEGIN
    INSERT INTO users (username, email, password, full_name, role, is_active)
    VALUES ('jane_smith', 'jane.smith@example.com', '$2a$12$GErEPMB8n48NN8O56krgoeZdPnSvMJr.dLin1QWpeR9wpwNs4aAnS', N'Jane Smith', 'CUSTOMER', 1);
END;

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'alice_johnson')
BEGIN
    INSERT INTO users (username, email, password, full_name, role, is_active)
    VALUES ('alice_johnson', 'alice.johnson@example.com', '$2a$12$GErEPMB8n48NN8O56krgoeZdPnSvMJr.dLin1QWpeR9wpwNs4aAnS', N'Alice Johnson', 'CUSTOMER', 1);
END;

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'robert_brown')
BEGIN
    INSERT INTO users (username, email, password, full_name, role, is_active)
    VALUES ('robert_brown', 'robert.brown@example.com', '$2a$12$GErEPMB8n48NN8O56krgoeZdPnSvMJr.dLin1QWpeR9wpwNs4aAnS', N'Robert Brown', 'CUSTOMER', 1);
END;
GO

-- ----------------------------------------------------------------------------
-- 2. CATEGORIES
-- ----------------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Fiction')
    INSERT INTO categories (name, description, is_active) VALUES (N'Fiction', N'Literary fiction, modern novels, and narrative storytelling.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Non-Fiction')
    INSERT INTO categories (name, description, is_active) VALUES (N'Non-Fiction', N'Biographies, memoirs, history, philosophy, and documentary literature.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Science & Technology')
    INSERT INTO categories (name, description, is_active) VALUES (N'Science & Technology', N'Computer science, software engineering, artificial intelligence, and physical sciences.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Business & Economics')
    INSERT INTO categories (name, description, is_active) VALUES (N'Business & Economics', N'Leadership, finance, entrepreneurship, investing, and management.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Mystery & Thriller')
    INSERT INTO categories (name, description, is_active) VALUES (N'Mystery & Thriller', N'Crime novels, psychological thrillers, suspense, and detective fiction.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Self-Help & Personal Growth')
    INSERT INTO categories (name, description, is_active) VALUES (N'Self-Help & Personal Growth', N'Habit formation, productivity, mindfulness, and personal development.', 1);

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = N'Fantasy & Sci-Fi')
    INSERT INTO categories (name, description, is_active) VALUES (N'Fantasy & Sci-Fi', N'Epic fantasy, magical realism, space exploration, and futuristic speculative fiction.', 1);
GO

-- ----------------------------------------------------------------------------
-- 3. AUTHORS
-- ----------------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'J.K. Rowling')
    INSERT INTO authors (name, bio) VALUES (N'J.K. Rowling', N'British author best known for creating the world-renowned Harry Potter fantasy series.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'George R.R. Martin')
    INSERT INTO authors (name, bio) VALUES (N'George R.R. Martin', N'American novelist and screenwriter, creator of the epic fantasy saga A Song of Ice and Fire.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'James Clear')
    INSERT INTO authors (name, bio) VALUES (N'James Clear', N'Author and global speaker focused on habit formation, decision making, and continuous self-improvement.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'Robert C. Martin')
    INSERT INTO authors (name, bio) VALUES (N'Robert C. Martin', N'Software engineer, instructor, and co-author of the Agile Manifesto, widely known as Uncle Bob.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'Walter Isaacson')
    INSERT INTO authors (name, bio) VALUES (N'Walter Isaacson', N'Acclaimed biographer, former CEO of CNN, and author of biographies on Steve Jobs, Albert Einstein, and Leonardo da Vinci.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'Agatha Christie')
    INSERT INTO authors (name, bio) VALUES (N'Agatha Christie', N'Legendary English mystery writer famous for creating detective icons Hercule Poirot and Miss Marple.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'Yuval Noah Harari')
    INSERT INTO authors (name, bio) VALUES (N'Yuval Noah Harari', N'Historian, philosopher, and bestselling author of Sapiens, Homo Deus, and 21 Lessons for the 21st Century.');

IF NOT EXISTS (SELECT 1 FROM authors WHERE name = N'Matt Haig')
    INSERT INTO authors (name, bio) VALUES (N'Matt Haig', N'Bestselling English author and journalist who writes fiction and non-fiction for both adults and children.');
GO

-- ----------------------------------------------------------------------------
-- 4. PUBLISHERS
-- ----------------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM publishers WHERE name = N'Penguin Random House')
    INSERT INTO publishers (name, address, phone) VALUES (N'Penguin Random House', N'1745 Broadway, New York, NY 10019, USA', '+1-212-782-9000');

IF NOT EXISTS (SELECT 1 FROM publishers WHERE name = N'HarperCollins Publishers')
    INSERT INTO publishers (name, address, phone) VALUES (N'HarperCollins Publishers', N'195 Broadway, New York, NY 10007, USA', '+1-212-207-7000');

IF NOT EXISTS (SELECT 1 FROM publishers WHERE name = N'Simon & Schuster')
    INSERT INTO publishers (name, address, phone) VALUES (N'Simon & Schuster', N'1230 Avenue of the Americas, New York, NY 10020, USA', '+1-212-698-7000');

IF NOT EXISTS (SELECT 1 FROM publishers WHERE name = N'Prentice Hall')
    INSERT INTO publishers (name, address, phone) VALUES (N'Prentice Hall', N'330 Hudson Street, New York, NY 10013, USA', '+1-800-848-9500');

IF NOT EXISTS (SELECT 1 FROM publishers WHERE name = N'Bloomsbury Publishing')
    INSERT INTO publishers (name, address, phone) VALUES (N'Bloomsbury Publishing', N'50 Bedford Square, London WC1B 3DP, UK', '+44-20-7631-5600');
GO

-- ----------------------------------------------------------------------------
-- 5. BOOKS (Dynamic FK Resolution)
-- ----------------------------------------------------------------------------
DECLARE @CatFiction INT = (SELECT id FROM categories WHERE name = N'Fiction');
DECLARE @CatNonFiction INT = (SELECT id FROM categories WHERE name = N'Non-Fiction');
DECLARE @CatSciTech INT = (SELECT id FROM categories WHERE name = N'Science & Technology');
DECLARE @CatBusiness INT = (SELECT id FROM categories WHERE name = N'Business & Economics');
DECLARE @CatMystery INT = (SELECT id FROM categories WHERE name = N'Mystery & Thriller');
DECLARE @CatSelfHelp INT = (SELECT id FROM categories WHERE name = N'Self-Help & Personal Growth');
DECLARE @CatFantasy INT = (SELECT id FROM categories WHERE name = N'Fantasy & Sci-Fi');

DECLARE @AuthRowling INT = (SELECT id FROM authors WHERE name = N'J.K. Rowling');
DECLARE @AuthMartin INT = (SELECT id FROM authors WHERE name = N'George R.R. Martin');
DECLARE @AuthClear INT = (SELECT id FROM authors WHERE name = N'James Clear');
DECLARE @AuthUncleBob INT = (SELECT id FROM authors WHERE name = N'Robert C. Martin');
DECLARE @AuthIsaacson INT = (SELECT id FROM authors WHERE name = N'Walter Isaacson');
DECLARE @AuthChristie INT = (SELECT id FROM authors WHERE name = N'Agatha Christie');
DECLARE @AuthHarari INT = (SELECT id FROM authors WHERE name = N'Yuval Noah Harari');
DECLARE @AuthHaig INT = (SELECT id FROM authors WHERE name = N'Matt Haig');

DECLARE @PubPenguin INT = (SELECT id FROM publishers WHERE name = N'Penguin Random House');
DECLARE @PubHarper INT = (SELECT id FROM publishers WHERE name = N'HarperCollins Publishers');
DECLARE @PubSimon INT = (SELECT id FROM publishers WHERE name = N'Simon & Schuster');
DECLARE @PubPrentice INT = (SELECT id FROM publishers WHERE name = N'Prentice Hall');
DECLARE @PubBloomsbury INT = (SELECT id FROM publishers WHERE name = N'Bloomsbury Publishing');

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780735211292')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'Atomic Habits', '9780735211292', @AuthClear, @CatSelfHelp, @PubPenguin, 27.00, 21.99, 50, N'An easy and proven way to build good habits and break bad ones. This groundbreaking book reveals practical strategies for everyday mastery.', 'https://images-na.ssl-images-amazon.com/images/I/81wgcld4wxL.jpg', 2018, 320, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780132350884')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'Clean Code: A Handbook of Agile Software Craftsmanship', '9780132350884', @AuthUncleBob, @CatSciTech, @PubPrentice, 45.00, 39.99, 30, N'Even bad code can function. But if code isn''t clean, it can bring a development organization to its knees. A timeless guide for every software professional.', 'https://images-na.ssl-images-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg', 2008, 464, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062316097')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'Sapiens: A Brief History of Humankind', '9780062316097', @AuthHarari, @CatNonFiction, @PubHarper, 35.00, 29.50, 40, N'Renowned historian Yuval Noah Harari spans sixty thousand years of human history, exploring how Homo sapiens came to dominate the Earth.', 'https://images-na.ssl-images-amazon.com/images/I/713jIoMO3UL.jpg', 2014, 448, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780590353427')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'Harry Potter and the Sorcerer''s Stone', '9780590353427', @AuthRowling, @CatFantasy, @PubBloomsbury, 24.99, 18.99, 100, N'Harry Potter has never even heard of Hogwarts when letters start dropping on the doormat at number four, Privet Drive. Thus begins an incredible magical adventure.', 'https://images-na.ssl-images-amazon.com/images/I/81iqZ2HHD-L.jpg', 1997, 309, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780553103540')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'A Game of Thrones', '9780553103540', @AuthMartin, @CatFantasy, @PubPenguin, 30.00, 24.99, 25, N'Winter is coming. As the cold weather approaches, noble families wage a ruthless war for control of the Iron Throne in Westeros.', 'https://images-na.ssl-images-amazon.com/images/I/91+1SUO2vVL.jpg', 1996, 694, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781451648539')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'Steve Jobs', '9781451648539', @AuthIsaacson, @CatNonFiction, @PubSimon, 32.50, 26.00, 20, N'Based on forty interviews with Steve Jobs conducted over two years, this definitive biography details the roller-coaster life and intense personality of the creative entrepreneur.', 'https://images-na.ssl-images-amazon.com/images/I/81VStYnDGrL.jpg', 2011, 656, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062073488')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'And Then There Were None', '9780062073488', @AuthChristie, @CatMystery, @PubHarper, 16.99, 13.99, 35, N'Ten strangers are lured to an isolated island mansion off the Devon coast. When one dies mysteriously, they realize a murderer is hiding among them.', 'https://images-na.ssl-images-amazon.com/images/I/81-0T0L0KzL.jpg', 1939, 272, N'English', 1);

IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780525559474')
    INSERT INTO books (title, isbn, author_id, category_id, publisher_id, price, discount_price, stock_quantity, description, cover_image, published_year, pages, language, is_active)
    VALUES (N'The Midnight Library', '9780525559474', @AuthHaig, @CatFiction, @PubPenguin, 26.00, 19.99, 45, N'Between life and death there is a library where every book offers a chance to try another life you could have lived. Nora Seed finds herself faced with this choice.', 'https://images-na.ssl-images-amazon.com/images/I/81J6APjwxlL.jpg', 2020, 304, N'English', 1);

-- =========================================================
-- 1. HARRY POTTER AND THE PHILOSOPHER'S STONE
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780747532699')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Harry Potter and the Philosopher''s Stone',
        '9780747532699',
        @AuthRowling,
        @CatFantasy,
        @PubBloomsbury,
        25.00, 19.99, 45,
        N'Harry Potter discovers the magical world and begins his first year at Hogwarts School of Witchcraft and Wizardry.',
        'https://images-na.ssl-images-amazon.com/images/I/81iqZ2HHD-L.jpg',
        1997, 223, N'English', 1
    );
END;


-- =========================================================
-- 2. HARRY POTTER AND THE CHAMBER OF SECRETS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780747538493')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Harry Potter and the Chamber of Secrets',
        '9780747538493',
        @AuthRowling,
        @CatFantasy,
        @PubBloomsbury,
        25.00, 20.49, 38,
        N'Harry returns to Hogwarts for another year and discovers a mysterious chamber hidden within the school.',
        'https://images-na.ssl-images-amazon.com/images/I/81S0LnPGqJL.jpg',
        1998, 251, N'English', 1
    );
END;


-- =========================================================
-- 3. A GAME OF THRONES
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780553593716')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'A Game of Thrones',
        '9780553593716',
        @AuthMartin,
        @CatFantasy,
        @PubPenguin,
        30.00, 24.99, 35,
        N'Noble families struggle for power while an ancient threat begins to rise in the frozen north.',
        'https://images-na.ssl-images-amazon.com/images/I/81WcnNQ-TBL.jpg',
        1996, 694, N'English', 1
    );
END;


-- =========================================================
-- 4. A CLASH OF KINGS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780553579901')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'A Clash of Kings',
        '9780553579901',
        @AuthMartin,
        @CatFantasy,
        @PubPenguin,
        32.00, 26.99, 28,
        N'The Seven Kingdoms descend into war as rival kings fight for the Iron Throne.',
        'https://images-na.ssl-images-amazon.com/images/I/91Z8D0Y2X-L.jpg',
        1998, 768, N'English', 1
    );
END;


-- =========================================================
-- 5. CLEAN CODE
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780132350884')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Clean Code',
        '9780132350884',
        @AuthUncleBob,
        @CatSciTech,
        @PubPrentice,
        45.00, 37.99, 25,
        N'A practical guide to writing readable, maintainable, and professional software code.',
        'https://images-na.ssl-images-amazon.com/images/I/41jEbK-jG-L.jpg',
        2008, 464, N'English', 1
    );
END;


-- =========================================================
-- 6. THE CLEAN CODER
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780137081073')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Clean Coder',
        '9780137081073',
        @AuthUncleBob,
        @CatSciTech,
        @PubPrentice,
        42.00, 34.99, 30,
        N'A guide to professional behavior, discipline, communication, and craftsmanship for software developers.',
        'https://images-na.ssl-images-amazon.com/images/I/41Wj8lR8L-L.jpg',
        2011, 256, N'English', 1
    );
END;


-- =========================================================
-- 7. STEVE JOBS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781451648539')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Steve Jobs',
        '9781451648539',
        @AuthIsaacson,
        @CatBusiness,
        @PubSimon,
        35.00, 28.99, 32,
        N'A detailed biography exploring the life, career, personality, and innovations of Steve Jobs.',
        'https://images-na.ssl-images-amazon.com/images/I/81VStl0ZJ-L.jpg',
        2011, 656, N'English', 1
    );
END;


-- =========================================================
-- 8. LEONARDO DA VINCI
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781501139154')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Leonardo da Vinci',
        '9781501139154',
        @AuthIsaacson,
        @CatNonFiction,
        @PubSimon,
        32.00, 26.49, 20,
        N'A biography of Leonardo da Vinci exploring his art, science, inventions, and extraordinary curiosity.',
        'https://images-na.ssl-images-amazon.com/images/I/81R5F8LZ9-L.jpg',
        2017, 624, N'English', 1
    );
END;


-- =========================================================
-- 9. MURDER ON THE ORIENT EXPRESS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062693662')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Murder on the Orient Express',
        '9780062693662',
        @AuthChristie,
        @CatMystery,
        @PubHarper,
        18.00, 14.99, 50,
        N'Hercule Poirot investigates a murder aboard the luxurious Orient Express where every passenger is a suspect.',
        'https://images-na.ssl-images-amazon.com/images/I/81Lx4W7LZ-L.jpg',
        1934, 274, N'English', 1
    );
END;


-- =========================================================
-- 10. AND THEN THERE WERE NONE
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062073488')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'And Then There Were None',
        '9780062073488',
        @AuthChristie,
        @CatMystery,
        @PubHarper,
        17.00, 13.99, 42,
        N'Ten strangers are invited to an isolated island where they begin dying one by one under mysterious circumstances.',
        'https://images-na.ssl-images-amazon.com/images/I/81pJ5K0J7-L.jpg',
        1939, 272, N'English', 1
    );
END;


-- =========================================================
-- 11. SAPIENS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062316097')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Sapiens',
        '9780062316097',
        @AuthHarari,
        @CatNonFiction,
        @PubHarper,
        30.00, 24.99, 40,
        N'A broad history of humankind examining how Homo sapiens developed societies, cultures, and civilizations.',
        'https://images-na.ssl-images-amazon.com/images/I/713jIoMO3UL.jpg',
        2015, 464, N'English', 1
    );
END;


-- =========================================================
-- 12. HOMO DEUS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780062464316')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Homo Deus',
        '9780062464316',
        @AuthHarari,
        @CatSciTech,
        @PubHarper,
        29.00, 23.99, 35,
        N'An exploration of humanity''s future and the technological forces that may transform society.',
        'https://images-na.ssl-images-amazon.com/images/I/713jIoMO3UL.jpg',
        2017, 464, N'English', 1
    );
END;


-- =========================================================
-- 13. THE MIDNIGHT LIBRARY
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780525559474')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Midnight Library',
        '9780525559474',
        @AuthHaig,
        @CatFiction,
        @PubPenguin,
        22.00, 17.99, 45,
        N'A woman discovers a mysterious library where every book offers a chance to experience a different version of her life.',
        'https://images-na.ssl-images-amazon.com/images/I/81J6APjwxlL.jpg',
        2020, 304, N'English', 1
    );
END;


-- =========================================================
-- 14. THE HUMANS
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780525559498')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Humans',
        '9780525559498',
        @AuthHaig,
        @CatFiction,
        @PubPenguin,
        20.00, 15.99, 30,
        N'An alien sent to Earth begins to discover the strange, complicated, and beautiful nature of human life.',
        'https://images-na.ssl-images-amazon.com/images/I/81X6K8L2-L.jpg',
        2013, 320, N'English', 1
    );
END;


-- =========================================================
-- 15. THE PSYCHOLOGY OF MONEY
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780857197689')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Psychology of Money',
        '9780857197689',
        @AuthHarari,
        @CatBusiness,
        @PubSimon,
        24.00, 19.99, 55,
        N'An exploration of how emotions, behavior, and personal experiences influence financial decisions.',
        'https://images-na.ssl-images-amazon.com/images/I/81Dkyb5X-L.jpg',
        2020, 256, N'English', 1
    );
END;


-- =========================================================
-- 16. RICH DAD POOR DAD
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781612681139')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Rich Dad Poor Dad',
        '9781612681139',
        @AuthClear,
        @CatBusiness,
        @PubSimon,
        21.00, 16.99, 60,
        N'A personal finance classic focused on financial education, investing, assets, and building long-term wealth.',
        'https://images-na.ssl-images-amazon.com/images/I/81bsw6fnUiL.jpg',
        1997, 336, N'English', 1
    );
END;


-- =========================================================
-- 17. THE HOBBIT
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780547928227')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Hobbit',
        '9780547928227',
        @AuthMartin,
        @CatFantasy,
        @PubPenguin,
        24.00, 19.49, 40,
        N'A reluctant hobbit joins a dangerous adventure involving dwarves, dragons, treasure, and an unexpected journey.',
        'https://images-na.ssl-images-amazon.com/images/I/91b0C2YNSrL.jpg',
        1937, 310, N'English', 1
    );
END;


-- =========================================================
-- 18. THE 7 HABITS OF HIGHLY EFFECTIVE PEOPLE
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781982137274')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The 7 Habits of Highly Effective People',
        '9781982137274',
        @AuthClear,
        @CatSelfHelp,
        @PubSimon,
        28.00, 22.99, 48,
        N'A practical framework for developing personal effectiveness, leadership, and strong relationships.',
        'https://images-na.ssl-images-amazon.com/images/I/71ZB18P3-L.jpg',
        1989, 464, N'English', 1
    );
END;


-- =========================================================
-- 19. THE LEAN STARTUP
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780307887894')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'The Lean Startup',
        '9780307887894',
        @AuthIsaacson,
        @CatBusiness,
        @PubPenguin,
        26.00, 20.99, 33,
        N'A methodology for building businesses through rapid experimentation, validated learning, and continuous improvement.',
        'https://images-na.ssl-images-amazon.com/images/I/81-QB7nDh4L.jpg',
        2011, 336, N'English', 1
    );
END;


-- =========================================================
-- 20. THINKING, FAST AND SLOW
-- =========================================================
IF NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780374533557')
BEGIN
    INSERT INTO books (
        title, isbn, author_id, category_id, publisher_id,
        price, discount_price, stock_quantity, description,
        cover_image, published_year, pages, language, is_active
    )
    VALUES (
        N'Thinking, Fast and Slow',
        '9780374533557',
        @AuthHarari,
        @CatSelfHelp,
        @PubPenguin,
        27.00, 21.49, 37,
        N'An exploration of the two systems that shape human thinking, judgment, decision-making, and behavior.',
        'https://images-na.ssl-images-amazon.com/images/I/71fX7K9L-L.jpg',
        2011, 499, N'English', 1
    );
END;
GO

-- ----------------------------------------------------------------------------
-- 6. ADDRESSES
-- ----------------------------------------------------------------------------
DECLARE @UserJohnId INT = (SELECT id FROM users WHERE username = 'john_doe');
DECLARE @UserJaneId INT = (SELECT id FROM users WHERE username = 'jane_smith');

IF @UserJohnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM addresses WHERE user_id = @UserJohnId AND address_line = N'123 Elm Street, Suite 4B')
    INSERT INTO addresses (user_id, recipient_name, phone, address_line, ward, district, city, is_default)
    VALUES (@UserJohnId, N'John Doe', '+1-555-0192', N'123 Elm Street, Suite 4B', N'Ward 1', N'Central District', N'New York', 1);

IF @UserJohnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM addresses WHERE user_id = @UserJohnId AND address_line = N'456 Innovation Way, Tech Park')
    INSERT INTO addresses (user_id, recipient_name, phone, address_line, ward, district, city, is_default)
    VALUES (@UserJohnId, N'John Doe (Office)', '+1-555-0193', N'456 Innovation Way, Tech Park', N'Ward 5', N'Downtown', N'New York', 0);

IF @UserJaneId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM addresses WHERE user_id = @UserJaneId AND address_line = N'789 Maple Avenue')
    INSERT INTO addresses (user_id, recipient_name, phone, address_line, ward, district, city, is_default)
    VALUES (@UserJaneId, N'Jane Smith', '+1-555-0284', N'789 Maple Avenue', N'Ward 3', N'Westside', N'San Francisco', 1);
GO

-- ----------------------------------------------------------------------------
-- 7. CART & CART ITEMS
-- ----------------------------------------------------------------------------
DECLARE @UserJohnId INT = (SELECT id FROM users WHERE username = 'john_doe');
DECLARE @UserJaneId INT = (SELECT id FROM users WHERE username = 'jane_smith');

IF @UserJohnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cart WHERE user_id = @UserJohnId)
    INSERT INTO cart (user_id) VALUES (@UserJohnId);

IF @UserJaneId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cart WHERE user_id = @UserJaneId)
    INSERT INTO cart (user_id) VALUES (@UserJaneId);

DECLARE @CartJohnId INT = (SELECT id FROM cart WHERE user_id = @UserJohnId);
DECLARE @CartJaneId INT = (SELECT id FROM cart WHERE user_id = @UserJaneId);

DECLARE @BookAtomicId INT = (SELECT id FROM books WHERE isbn = '9780735211292');
DECLARE @BookCleanCodeId INT = (SELECT id FROM books WHERE isbn = '9780132350884');
DECLARE @BookHarryPotterId INT = (SELECT id FROM books WHERE isbn = '9780590353427');

IF @CartJohnId IS NOT NULL AND @BookAtomicId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id = @CartJohnId AND book_id = @BookAtomicId)
    INSERT INTO cart_items (cart_id, book_id, quantity) VALUES (@CartJohnId, @BookAtomicId, 1);

IF @CartJohnId IS NOT NULL AND @BookCleanCodeId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id = @CartJohnId AND book_id = @BookCleanCodeId)
    INSERT INTO cart_items (cart_id, book_id, quantity) VALUES (@CartJohnId, @BookCleanCodeId, 2);

IF @CartJaneId IS NOT NULL AND @BookHarryPotterId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM cart_items WHERE cart_id = @CartJaneId AND book_id = @BookHarryPotterId)
    INSERT INTO cart_items (cart_id, book_id, quantity) VALUES (@CartJaneId, @BookHarryPotterId, 1);
GO

-- ----------------------------------------------------------------------------
-- 8. ORDERS & ORDER ITEMS
-- ----------------------------------------------------------------------------
DECLARE @UserJohnId INT = (SELECT id FROM users WHERE username = 'john_doe');
DECLARE @UserJaneId INT = (SELECT id FROM users WHERE username = 'jane_smith');

IF @UserJohnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM orders WHERE order_code = 'ORD-20260918-001')
    INSERT INTO orders (order_code, user_id, recipient_name, recipient_phone, shipping_address, note, total_amount, shipping_fee, final_amount, status, payment_method, payment_status, created_at, updated_at)
    VALUES ('ORD-20260918-001', @UserJohnId, N'John Doe', '+1-555-0192', N'123 Elm Street, Suite 4B, Ward 1, Central District, New York', N'Please leave parcel at the front door.', 61.98, 5.00, 66.98, 'DELIVERED', 'CREDIT_CARD', 'PAID', DATEADD(day, -5, SYSDATETIME()), DATEADD(day, -3, SYSDATETIME()));

IF @UserJaneId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM orders WHERE order_code = 'ORD-20260918-002')
    INSERT INTO orders (order_code, user_id, recipient_name, recipient_phone, shipping_address, note, total_amount, shipping_fee, final_amount, status, payment_method, payment_status, created_at, updated_at)
    VALUES ('ORD-20260918-002', @UserJaneId, N'Jane Smith', '+1-555-0284', N'789 Maple Avenue, Ward 3, Westside, San Francisco', N'Call before arrival.', 29.50, 0.00, 29.50, 'PROCESSING', 'VNPAY', 'PAID', DATEADD(day, -1, SYSDATETIME()), SYSDATETIME());

DECLARE @Order1Id INT = (SELECT id FROM orders WHERE order_code = 'ORD-20260918-001');
DECLARE @Order2Id INT = (SELECT id FROM orders WHERE order_code = 'ORD-20260918-002');

DECLARE @BookAtomicId INT = (SELECT id FROM books WHERE isbn = '9780735211292');
DECLARE @BookCleanCodeId INT = (SELECT id FROM books WHERE isbn = '9780132350884');
DECLARE @BookSapiensId INT = (SELECT id FROM books WHERE isbn = '9780062316097');

IF @Order1Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = @Order1Id AND book_title = N'Atomic Habits')
    INSERT INTO order_items (order_id, book_id, book_title, price, quantity, subtotal)
    VALUES (@Order1Id, @BookAtomicId, N'Atomic Habits', 21.99, 1, 21.99);

IF @Order1Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = @Order1Id AND book_title LIKE N'Clean Code%')
    INSERT INTO order_items (order_id, book_id, book_title, price, quantity, subtotal)
    VALUES (@Order1Id, @BookCleanCodeId, N'Clean Code: A Handbook of Agile Software Craftsmanship', 39.99, 1, 39.99);

IF @Order2Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = @Order2Id AND book_title LIKE N'Sapiens%')
    INSERT INTO order_items (order_id, book_id, book_title, price, quantity, subtotal)
    VALUES (@Order2Id, @BookSapiensId, N'Sapiens: A Brief History of Humankind', 29.50, 1, 29.50);
GO

-- ----------------------------------------------------------------------------
-- 9. REVIEWS
-- ----------------------------------------------------------------------------
DECLARE @UserJohnId INT = (SELECT id FROM users WHERE username = 'john_doe');
DECLARE @UserJaneId INT = (SELECT id FROM users WHERE username = 'jane_smith');

DECLARE @BookAtomicId INT = (SELECT id FROM books WHERE isbn = '9780735211292');
DECLARE @BookCleanCodeId INT = (SELECT id FROM books WHERE isbn = '9780132350884');
DECLARE @BookSapiensId INT = (SELECT id FROM books WHERE isbn = '9780062316097');
DECLARE @BookHarryPotterId INT = (SELECT id FROM books WHERE isbn = '9780590353427');

IF @UserJohnId IS NOT NULL AND @BookAtomicId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM reviews WHERE user_id = @UserJohnId AND book_id = @BookAtomicId)
    INSERT INTO reviews (book_id, user_id, rating, comment, is_approved)
    VALUES (@BookAtomicId, @UserJohnId, 5, N'Life-changing book! The framework provided by James Clear is extremely practical and easy to implement daily.', 1);

IF @UserJohnId IS NOT NULL AND @BookCleanCodeId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM reviews WHERE user_id = @UserJohnId AND book_id = @BookCleanCodeId)
    INSERT INTO reviews (book_id, user_id, rating, comment, is_approved)
    VALUES (@BookCleanCodeId, @UserJohnId, 5, N'A must-read for every professional developer. Clean Code transformed how I write and structure code.', 1);

IF @UserJaneId IS NOT NULL AND @BookSapiensId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM reviews WHERE user_id = @UserJaneId AND book_id = @BookSapiensId)
    INSERT INTO reviews (book_id, user_id, rating, comment, is_approved)
    VALUES (@BookSapiensId, @UserJaneId, 4, N'Fascinating overview of human history. Harari brings fresh perspectives to standard historical narratives.', 1);

IF @UserJaneId IS NOT NULL AND @BookHarryPotterId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM reviews WHERE user_id = @UserJaneId AND book_id = @BookHarryPotterId)
    INSERT INTO reviews (book_id, user_id, rating, comment, is_approved)
    VALUES (@BookHarryPotterId, @UserJaneId, 5, N'A timeless magical classic! Rereading it after years brought back so much warmth and nostalgia.', 1);
GO

-- ----------------------------------------------------------------------------
-- 10. PAYMENTS
-- ----------------------------------------------------------------------------
DECLARE @Order1Id INT = (SELECT id FROM orders WHERE order_code = 'ORD-20260918-001');
DECLARE @Order2Id INT = (SELECT id FROM orders WHERE order_code = 'ORD-20260918-002');

IF @Order1Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code = 'TXN-99881122')
    INSERT INTO payments (order_id, transaction_code, payment_method, amount, status, payment_time)
    VALUES (@Order1Id, 'TXN-99881122', 'CREDIT_CARD', 66.98, 'SUCCESS', DATEADD(day, -5, SYSDATETIME()));

IF @Order2Id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code = 'VNPAY-20260917-88')
    INSERT INTO payments (order_id, transaction_code, payment_method, amount, status, payment_time)
    VALUES (@Order2Id, 'VNPAY-20260917-88', 'VNPAY', 29.50, 'SUCCESS', DATEADD(day, -1, SYSDATETIME()));
GO

-- ============================================================================
-- End of Seed Data Script
-- ============================================================================
