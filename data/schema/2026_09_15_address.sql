use onlinebookstore;
go

create table addresses (
    id bigint identity(1,1) primary key,
    user_id int not null,
    recipient_name nvarchar(100) not null,
    phone varchar(20) not null,
    address_line nvarchar(255) not null,
    ward nvarchar(100) not null,
    district nvarchar(100) not null,
    city nvarchar(100) not null,
    is_default bit not null default 0,
    created_at datetime2 not null default getdate(),
    updated_at datetime2 not null default getdate(),
    constraint Fk_addresses_users FOREIGN KEY (user_id) REFERENCES users(id) on delete cascade
);