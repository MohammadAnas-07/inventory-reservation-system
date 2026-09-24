-- 1. Products Table
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          sku VARCHAR(64) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price NUMERIC(12, 2) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Inventories Table
CREATE TABLE inventories (
                             id BIGSERIAL PRIMARY KEY,
                             product_id BIGINT NOT NULL UNIQUE,
                             total_quantity INT NOT NULL DEFAULT 0,
                             reserved_quantity INT NOT NULL DEFAULT 0,
                             version BIGINT NOT NULL DEFAULT 0,
                             created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_inventories_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
                             CONSTRAINT chk_quantity_valid CHECK (total_quantity >= 0 AND reserved_quantity >= 0),
                             CONSTRAINT chk_reserved_not_exceed_total CHECK (reserved_quantity <= total_quantity)
);

-- 3. Reservations Table
CREATE TABLE reservations (
                              id BIGSERIAL PRIMARY KEY,
                              status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
                              expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT chk_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'))
);

-- 4. Reservation Items Table
CREATE TABLE reservation_items (
                                   id BIGSERIAL PRIMARY KEY,
                                   reservation_id BIGINT NOT NULL,
                                   product_id BIGINT NOT NULL,
                                   quantity INT NOT NULL,
                                   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_items_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products (id),
                                   CONSTRAINT chk_item_quantity_positive CHECK (quantity > 0)
);

-- Indexes for lookup performance
CREATE INDEX idx_inventories_product_id ON inventories (product_id);
CREATE INDEX idx_reservations_status ON reservations (status);
CREATE INDEX idx_reservation_items_reservation_id ON reservation_items (reservation_id);