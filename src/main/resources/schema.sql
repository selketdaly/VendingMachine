CREATE TABLE product
(
    product_id         INT         NOT NULL,
    name               VARCHAR(50) NOT NULL,
    price              INT,
    quantity_available INT
);

CREATE TABLE coin
(
    coin_type VARCHAR(50) NOT NULL,
    quantity  INT
);