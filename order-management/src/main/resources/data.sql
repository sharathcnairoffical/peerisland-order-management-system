MERGE INTO roles (role_id, role_name) KEY(role_id) VALUES (1, 'CUSTOMER');
MERGE INTO roles (role_id, role_name) KEY(role_id) VALUES (2, 'ADMIN');

MERGE INTO customer (
    full_name,
    email,
    mobile_number,
    country_code,
    password_hash,
    customer_unique_id,
    role_id,
    status,
    add_by,
    mod_by,
    add_date,
    mod_date
) KEY(email) VALUES (
    'Sharath Nair',
    'sharath.nair@peerisland.com',
    '9999999999',
    '+91',
    '$2a$10$leDBRPzU7whL1MB2a4RkD.mGA4Pxl1B4fYm0XeBHwhSwT07nSQdJK',
    'CUS-SHARATH-NAIR',
    1,
    'ACTIVE',
    'SYSTEM',
    'SYSTEM',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
