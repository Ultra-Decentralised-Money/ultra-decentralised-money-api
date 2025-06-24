CREATE TABLE block_leader (
    block_hash TEXT PRIMARY KEY,
    vrf_key_hash TEXT NOT NULL,
    slot BIGINT NOT NULL
);

CREATE TABLE epoch_active_pool (
    epoch_number SMALLINT PRIMARY KEY,
    active_pool_count SMALLINT NOT NULL,
    updated_at_slot BIGINT NOT NULL
);

CREATE TABLE token_volume (
    tx_hash TEXT,
    address TEXT,
    unit TEXT,
    volume BIGINT NOT NULL,
    slot BIGINT NOT NULL,
    PRIMARY KEY (tx_hash, address, unit)
);
