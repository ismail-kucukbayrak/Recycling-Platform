----------------------------------------------------
-----------TABLES------------------------------------
----------------------------------------------------

--Admins Table
CREATE TABLE admins (
    phone    BIGINT PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    surname  VARCHAR(50) NOT NULL
);


--Neighborhood Residents Table
CREATE TABLE neighborhood_residents (
    phone    BIGINT PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    surname  VARCHAR(50) NOT NULL,
    "plastic(kg)" INTEGER DEFAULT 0,
    "glass(kg)" INTEGER DEFAULT 0,
    "electronic(kg)" INTEGER DEFAULT 0
);


--Collector Companies Table
CREATE TABLE collector_companies (
    phone    BIGINT PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    name     VARCHAR(50) NOT NULL
);


--Warehouse Waste Table
CREATE TABLE warehouse (
    id SERIAL PRIMARY KEY,
    waste_name VARCHAR(50) NOT NULL,
    "amount(kg)" INTEGER DEFAULT 0 CHECK ("amount(kg)" >= 0)
);


--SEQUENCE: Appointments id
CREATE SEQUENCE appointment_id_seq START 1 INCREMENT 1;

--Appointments Table
CREATE TABLE appointments (
    id INTEGER PRIMARY KEY DEFAULT nextval('appointment_id_seq'),
    phone BIGINT,
    "company_name" VARCHAR(50),
    waste_id INTEGER,
    "waste_name" VARCHAR(50),
    "amount(kg)" INTEGER NOT NULL,
    time TIMESTAMP NOT NULL,

    FOREIGN KEY (phone) REFERENCES collector_companies(phone) ON UPDATE CASCADE ON DELETE CASCADE,

    FOREIGN KEY (waste_id) REFERENCES warehouse(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

----------------------------------------------------
-----------OPERATIONS--------------------------------
----------------------------------------------------

--Warehouse Product Definitions
INSERT INTO warehouse (waste_name, "amount(kg)") VALUES
('plastic', 0),
('glass', 0),
('electronic', 0);


--FUNCTION: NEIGHBORHOOD RESIDENT LOGIN
CREATE OR REPLACE FUNCTION neighborhood_resident_login(p_phone BIGINT, p_password VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM neighborhood_residents WHERE phone = p_phone AND password = p_password);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: COLLECTOR COMPANY LOGIN
CREATE OR REPLACE FUNCTION collector_company_login(p_phone BIGINT, p_password VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM collector_companies WHERE phone = p_phone AND password = p_password);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: ADMIN LOGIN
CREATE OR REPLACE FUNCTION admin_login(p_phone BIGINT, p_password VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM admins WHERE phone = p_phone AND password = p_password);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Neighborhood Resident Registration
CREATE OR REPLACE FUNCTION register_neighborhood_resident(p_phone BIGINT, p_password VARCHAR, p_name VARCHAR, p_surname VARCHAR)
RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM neighborhood_residents WHERE phone = p_phone) THEN
        RAISE EXCEPTION 'A registration with this phone number already exists.';
    END IF;

    INSERT INTO neighborhood_residents (phone, password, name, surname, "plastic(kg)", "glass(kg)", "electronic(kg)")
    VALUES (p_phone, p_password, p_name, p_surname, 0, 0, 0);

    RAISE NOTICE 'Neighborhood resident registered successfully.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Collector Company Registration
CREATE OR REPLACE FUNCTION register_collector_company(p_phone BIGINT, p_password VARCHAR, p_name VARCHAR)
RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM collector_companies WHERE phone = p_phone) THEN
        RAISE EXCEPTION 'A registration with this phone number already exists.';
    END IF;

    INSERT INTO collector_companies (phone, password, name)
    VALUES (p_phone, p_password, p_name);

    RAISE NOTICE 'Collector company registered successfully.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Neighborhood Resident Add Waste
CREATE OR REPLACE FUNCTION add_waste_for_neighborhood_resident(p_phone BIGINT, p_waste_type VARCHAR, p_amount INTEGER)
RETURNS VOID AS $$
BEGIN
    IF p_waste_type = 'plastic' THEN
        UPDATE neighborhood_residents SET "plastic(kg)" = "plastic(kg)" + p_amount WHERE phone = p_phone;

    ELSIF p_waste_type = 'glass' THEN
        UPDATE neighborhood_residents SET "glass(kg)" = "glass(kg)" + p_amount WHERE phone = p_phone;

    ELSIF p_waste_type = 'electronic' THEN
        UPDATE neighborhood_residents SET "electronic(kg)" = "electronic(kg)" + p_amount WHERE phone = p_phone;
    END IF;

    RAISE NOTICE 'Waste added.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Neighborhood Resident Report
CREATE OR REPLACE FUNCTION neighborhood_resident_report(p_phone BIGINT)
RETURNS TABLE (product VARCHAR, amount INTEGER) AS $$
BEGIN
    RETURN QUERY
    SELECT 'Plastic'::VARCHAR, "plastic(kg)" FROM neighborhood_residents WHERE phone = p_phone
    UNION ALL
    SELECT 'Glass'::VARCHAR, "glass(kg)" FROM neighborhood_residents WHERE phone = p_phone
    UNION ALL
    SELECT 'Electronic'::VARCHAR, "electronic(kg)" FROM neighborhood_residents WHERE phone = p_phone;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Create Appointment
CREATE OR REPLACE FUNCTION create_appointment( p_phone BIGINT, p_waste_id INTEGER, p_amount INTEGER, p_time TIMESTAMP )
RETURNS VOID AS $$
DECLARE
    warehouse_amount INTEGER;
    p_company_name VARCHAR(50);
    p_waste_name VARCHAR(50);
BEGIN
    SELECT "amount(kg)" INTO warehouse_amount FROM warehouse WHERE id = p_waste_id;
    SELECT name INTO p_company_name FROM collector_companies WHERE phone = p_phone;
    SELECT waste_name INTO p_waste_name FROM warehouse WHERE id = p_waste_id;

    IF warehouse_amount < p_amount THEN
        RAISE EXCEPTION 'Not enough amount in the warehouse. Available: %kg, Requested: %kg', warehouse_amount, p_amount;
    END IF;

    INSERT INTO appointments (phone,"company_name", waste_id, "waste_name", "amount(kg)", time)
    VALUES (p_phone, p_company_name, p_waste_id, p_waste_name, p_amount, p_time);

    RAISE NOTICE 'Appointment created successfully.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Current Warehouse Status
CREATE OR REPLACE FUNCTION get_warehouse_records()
RETURNS TABLE (waste_id INTEGER, waste_name VARCHAR, "amount(kg)" INTEGER) AS $$
DECLARE
    warehouse_cursor CURSOR FOR SELECT w.id, w.waste_name, w."amount(kg)" FROM warehouse w ORDER BY w.id;
    warehouse_rec RECORD;
BEGIN
    OPEN warehouse_cursor;

    LOOP
        FETCH warehouse_cursor INTO warehouse_rec;
        EXIT WHEN NOT FOUND;

        waste_id      := warehouse_rec.id;
        waste_name    := warehouse_rec.waste_name;
        "amount(kg)"  := warehouse_rec."amount(kg)";

        RETURN NEXT;
    END LOOP;

    CLOSE warehouse_cursor;
END;
$$ LANGUAGE plpgsql;


--VIEW: Today and Future Appointments
CREATE VIEW todays_appointments AS
SELECT *
FROM appointments
WHERE date(time) >= CURRENT_DATE
ORDER BY time;


--FUNCTION: Monthly Total Waste Report
CREATE OR REPLACE FUNCTION monthly_total_waste_report()
RETURNS TABLE ("total_plastic(kg)" BIGINT, "total_glass(kg)" BIGINT, "total_electronic(kg)" BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT SUM("plastic(kg)"), SUM("glass(kg)"), SUM("electronic(kg)")
    FROM neighborhood_residents
    HAVING SUM("plastic(kg)") >= 0 AND SUM("glass(kg)") >= 0 AND SUM("electronic(kg)") >= 0;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Residents Who Added Waste This Month
CREATE OR REPLACE FUNCTION neighborhood_residents_who_added_waste_this_month()
RETURNS TABLE (phone BIGINT, name VARCHAR, surname VARCHAR) AS $$
BEGIN
    RETURN QUERY

    SELECT r.phone, r.name, r.surname FROM neighborhood_residents r WHERE r."plastic(kg)" > 0

    UNION

    SELECT r.phone, r.name, r.surname FROM neighborhood_residents r WHERE r."glass(kg)" > 0

    UNION

    SELECT r.phone, r.name, r.surname FROM neighborhood_residents r WHERE r."electronic(kg)" > 0;

END;
$$ LANGUAGE plpgsql;


--INDEX: Neighborhood Resident Name
CREATE INDEX idx_neighborhood_residents_name
ON neighborhood_residents (name);


--FUNCTION: Find Neighborhood Resident Using Index
CREATE OR REPLACE FUNCTION get_neighborhood_resident_by_name (p_name VARCHAR)
RETURNS TABLE (phone BIGINT, name VARCHAR, surname VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT r.phone, r.name, r.surname FROM neighborhood_residents r WHERE r.name = p_name;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Reset Monthly Waste Records
CREATE OR REPLACE FUNCTION reset_monthly_waste()
RETURNS VOID AS $$
BEGIN
    UPDATE neighborhood_residents
    SET
        "plastic(kg)" = 0,
        "glass(kg)" = 0,
        "electronic(kg)" = 0;

    RAISE NOTICE 'Monthly waste amounts for neighborhood residents have been reset.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Delete Neighborhood Resident
CREATE OR REPLACE FUNCTION delete_neighborhood_resident (p_phone BIGINT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM neighborhood_residents WHERE phone = p_phone;

    RAISE NOTICE 'Neighborhood resident removed from the system.';
END;
$$ LANGUAGE plpgsql;


--TRIGGER FUNCTION: Update Warehouse When Waste Is Added
CREATE OR REPLACE FUNCTION trg_add_waste_to_warehouse()
RETURNS trigger AS $$
DECLARE
    diff_plastic INTEGER;
    diff_glass INTEGER;
    diff_electronic INTEGER;
BEGIN

    diff_plastic := NEW."plastic(kg)" - OLD."plastic(kg)";
    IF diff_plastic > 0 THEN
        UPDATE warehouse SET "amount(kg)" = "amount(kg)" + diff_plastic WHERE waste_name = 'plastic';
    END IF;

    diff_glass := NEW."glass(kg)" - OLD."glass(kg)";
    IF diff_glass > 0 THEN
        UPDATE warehouse SET "amount(kg)" = "amount(kg)" + diff_glass WHERE waste_name = 'glass';
    END IF;

    diff_electronic := NEW."electronic(kg)" - OLD."electronic(kg)";
    IF diff_electronic > 0 THEN
        UPDATE warehouse SET "amount(kg)" = "amount(kg)" + diff_electronic WHERE waste_name = 'electronic';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--TRIGGER
CREATE TRIGGER neighborhood_resident_waste_update
AFTER UPDATE OF "plastic(kg)", "glass(kg)", "electronic(kg)"
ON neighborhood_residents
FOR EACH ROW
EXECUTE FUNCTION trg_add_waste_to_warehouse();


--TRIGGER FUNCTION: Update Warehouse When Waste Is Removed
CREATE OR REPLACE FUNCTION trg_update_warehouse_on_appointment()
RETURNS trigger AS $$
BEGIN
    UPDATE warehouse SET "amount(kg)" = "amount(kg)" - NEW."amount(kg)" WHERE id = NEW.waste_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--TRIGGER
CREATE TRIGGER appointment_insert_warehouse_update
AFTER INSERT ON appointments
FOR EACH ROW
EXECUTE FUNCTION trg_update_warehouse_on_appointment();


--Admin Definitions
INSERT INTO admins VALUES (0,'0','admin','admin')
