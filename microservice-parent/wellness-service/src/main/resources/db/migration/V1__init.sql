-- Category Table
CREATE TABLE IF NOT EXISTS category (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255)
);

-- WellnessResource Table
CREATE TABLE IF NOT EXISTS wellness_resource (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    url VARCHAR(255),
    category_id VARCHAR(255),
    CONSTRAINT fk_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON DELETE SET NULL
);
