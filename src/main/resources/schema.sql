drop table if exists users, items, bookings;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    is_available BIT NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);

