create database onlinebookstore;
use onlinebookstore;
go


CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,

    email VARCHAR(100) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    full_name NVARCHAR(100) NOT NULL,

    role VARCHAR(20) NOT NULL
        DEFAULT 'CUSTOMER',

    is_active BIT NOT NULL
        DEFAULT 1,

    created_at DATETIME2 NOT NULL
        DEFAULT SYSDATETIME(),

    updated_at DATETIME2 NOT NULL
        DEFAULT SYSDATETIME(),

    CONSTRAINT chk_user_role
        CHECK (role IN ('CUSTOMER', 'MANAGER'))
);
