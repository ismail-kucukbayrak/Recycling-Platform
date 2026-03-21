----------------------------------------------------
-----------TABLOLAR---------------------------------
----------------------------------------------------

--Yetkililer Tablosu
CREATE TABLE yetkililer (
    telefon BIGINT PRIMARY KEY,
    sifre   VARCHAR(20) NOT NULL,
    isim    VARCHAR(50) NOT NULL,
    soyisim VARCHAR(50) NOT NULL
);


--Mahalle Sakinleri Tablosu
CREATE TABLE mahalle_sakinleri (
    telefon BIGINT PRIMARY KEY,
    sifre   VARCHAR(20) NOT NULL,
    isim    VARCHAR(50) NOT NULL,
    soyisim VARCHAR(50) NOT NULL,
    "karton(kg)" INTEGER DEFAULT 0,
    "cam(kg)" INTEGER DEFAULT 0,
    "elektronik(kg)" INTEGER DEFAULT 0
);


--Toplayıcı Firmalar Tablosu
CREATE TABLE toplayici_firmalar (
    telefon BIGINT PRIMARY KEY,
    sifre   VARCHAR(20) NOT NULL,
    isim    VARCHAR(50) NOT NULL
);


--Depodaki Atık Tablosu
CREATE TABLE depo (
    id SERIAL PRIMARY KEY,
    atik_ismi VARCHAR(50) NOT NULL,
    "miktar(kg)" INTEGER DEFAULT 0 CHECK ("miktar(kg)" >= 0)
);


--SEQUENCE: Randevular id
CREATE SEQUENCE randevu_id_seq START 1 INCREMENT 1;

--Randevu Tablosu
CREATE TABLE randevular (
    id INTEGER PRIMARY KEY DEFAULT nextval('randevu_id_seq'),
    telefon BIGINT,
    "firma ismi" VARCHAR(50),
    atik_id INTEGER,
    "atık ismi" VARCHAR(50),
    "miktar(kg)" INTEGER NOT NULL,
    zaman TIMESTAMP NOT NULL,

    FOREIGN KEY (telefon) REFERENCES toplayici_firmalar(telefon) ON UPDATE CASCADE ON DELETE CASCADE,

    FOREIGN KEY (atik_id) REFERENCES depo(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

----------------------------------------------------
-----------İŞLEMLER---------------------------------
----------------------------------------------------

--Depodaki ürün Tanımları
INSERT INTO depo (atik_ismi, "miktar(kg)") VALUES
('karton', 0),
('cam', 0),
('elektronik', 0);


--FUNCTION: MAHALLE SAKİNİ GİRİŞ
CREATE OR REPLACE FUNCTION mahalle_sakini_giris(p_telefon BIGINT, p_sifre VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM mahalle_sakinleri WHERE telefon = p_telefon AND sifre = p_sifre);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: TOPLAYICI FİRMA GİRİŞ
CREATE OR REPLACE FUNCTION toplayici_firma_giris(p_telefon BIGINT, p_sifre VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM toplayici_firmalar WHERE telefon = p_telefon AND sifre = p_sifre);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: YETKİLİ (ADMIN) GİRİŞ
CREATE OR REPLACE FUNCTION yetkili_giris(p_telefon BIGINT, p_sifre VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM yetkililer WHERE telefon = p_telefon AND sifre = p_sifre);
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Mahalle Sakini Kayıt
CREATE OR REPLACE FUNCTION mahalle_sakini_kayit_ol(p_telefon BIGINT, p_sifre VARCHAR, p_isim VARCHAR, p_soyisim VARCHAR)
RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM mahalle_sakinleri WHERE telefon = p_telefon) THEN
        RAISE EXCEPTION 'Bu telefon numarası ile kayıt zaten mevcut.';
    END IF;

    INSERT INTO mahalle_sakinleri (telefon, sifre, isim, soyisim, "karton(kg)", "cam(kg)", "elektronik(kg)")
    VALUES (p_telefon, p_sifre, p_isim, p_soyisim, 0, 0, 0);

    RAISE NOTICE 'Mahalle sakini başarıyla kaydedildi.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Toplayıcı Firma Kayıt
CREATE OR REPLACE FUNCTION toplayici_firma_kayit_ol(p_telefon BIGINT, p_sifre VARCHAR, p_isim VARCHAR)
RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM toplayici_firmalar WHERE telefon = p_telefon) THEN
        RAISE EXCEPTION 'Bu telefon numarası ile kayıt zaten mevcut.';
    END IF;

    INSERT INTO toplayici_firmalar (telefon, sifre, isim)
    VALUES (p_telefon, p_sifre, p_isim);

    RAISE NOTICE 'Toplayıcı firma başarıyla kaydedildi.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Mahalle Sakini Atık Ekleme
CREATE OR REPLACE FUNCTION mahalle_sakini_atik_ekle(p_telefon BIGINT, p_atik_turu VARCHAR, p_miktar INTEGER)
RETURNS VOID AS $$
BEGIN
    IF p_atik_turu = 'karton' THEN
        UPDATE mahalle_sakinleri SET "karton(kg)" = "karton(kg)" + p_miktar WHERE telefon = p_telefon;

    ELSIF p_atik_turu = 'cam' THEN
        UPDATE mahalle_sakinleri SET "cam(kg)" = "cam(kg)" + p_miktar WHERE telefon = p_telefon;

    ELSIF p_atik_turu = 'elektronik' THEN
        UPDATE mahalle_sakinleri SET "elektronik(kg)" = "elektronik(kg)" + p_miktar WHERE telefon = p_telefon;
    END IF;

    RAISE NOTICE 'Atık eklendi.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Mahalle Sakini Raporu
CREATE OR REPLACE FUNCTION mahalle_sakini_raporu(p_telefon BIGINT)
RETURNS TABLE (urun VARCHAR, miktar INTEGER) AS $$
BEGIN
    RETURN QUERY
    SELECT 'Karton'::VARCHAR, "karton(kg)" FROM mahalle_sakinleri WHERE telefon = p_telefon
    UNION ALL
    SELECT 'Cam'::VARCHAR, "cam(kg)" FROM mahalle_sakinleri WHERE telefon = p_telefon
    UNION ALL
    SELECT 'Elektronik'::VARCHAR, "elektronik(kg)" FROM mahalle_sakinleri WHERE telefon = p_telefon;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Randevu Oluştur
CREATE OR REPLACE FUNCTION randevu_olustur( p_telefon BIGINT, p_atik_id INTEGER, p_miktar INTEGER, p_zaman TIMESTAMP )
RETURNS VOID AS $$
DECLARE
    depo_miktar INTEGER;
    p_firma_ismi VARCHAR(50);
    p_atik_ismi VARCHAR(50);
BEGIN
    SELECT "miktar(kg)" INTO depo_miktar FROM depo WHERE id = p_atik_id;
    SELECT isim INTO p_firma_ismi FROM toplayici_firmalar WHERE telefon = p_telefon;
    SELECT atik_ismi INTO p_atik_ismi FROM depo WHERE id = p_atik_id;

    IF depo_miktar < p_miktar THEN
        RAISE EXCEPTION 'Depoda yeterli miktar yok. Mevcut: %kg, İstenen: %kg', depo_miktar, p_miktar;
    END IF;

    INSERT INTO randevular (telefon,"firma ismi", atik_id, "atık ismi", "miktar(kg)", zaman)
    VALUES (p_telefon, p_firma_ismi, p_atik_id, p_atik_ismi, p_miktar, p_zaman);

    RAISE NOTICE 'Randevu başarıyla oluşturuldu.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Deponun Mevcut Durumu
CREATE OR REPLACE FUNCTION depo_kayitlarini_getir()
RETURNS TABLE (atik_id INTEGER, atik_ismi VARCHAR, "miktar(kg)" INTEGER) AS $$
DECLARE
    depo_cursor CURSOR FOR SELECT d.id, d.atik_ismi, d."miktar(kg)" FROM depo d ORDER BY d.id;
    depo_rec RECORD;
BEGIN
    OPEN depo_cursor;

    LOOP
        FETCH depo_cursor INTO depo_rec;
        EXIT WHEN NOT FOUND;

        atik_id       := depo_rec.id;
        atik_ismi     := depo_rec.atik_ismi;
        "miktar(kg)"  := depo_rec."miktar(kg)";

        RETURN NEXT;
    END LOOP;

    CLOSE depo_cursor;
END;
$$ LANGUAGE plpgsql;


--VİEW: Bugün ve Sonrası Randevular
CREATE VIEW bugunun_randevulari AS
SELECT * 
FROM randevular 
WHERE date(zaman) >= CURRENT_DATE 
ORDER BY zaman;


--FUNCTION: Aylık Toplam Atık Kaydı
CREATE OR REPLACE FUNCTION aylik_toplam_atik_raporu()
RETURNS TABLE ("toplam_karton(kg)" BIGINT, "toplam_cam(kg)" BIGINT, "toplam_elektronik(kg)" BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT SUM("karton(kg)"), SUM("cam(kg)"), SUM("elektronik(kg)")
    FROM mahalle_sakinleri
    HAVING SUM("karton(kg)") >= 0 AND SUM("cam(kg)") >= 0 AND SUM("elektronik(kg)") >= 0;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Bu Ay Atık Ekleyenler
CREATE OR REPLACE FUNCTION bu_ay_atik_ekleyen_mahalle_sakinleri()
RETURNS TABLE (telefon BIGINT, isim VARCHAR, soyisim VARCHAR) AS $$  
BEGIN
    RETURN QUERY

    SELECT m.telefon, m.isim, m.soyisim FROM mahalle_sakinleri m WHERE m."karton(kg)" > 0

    UNION

    SELECT m.telefon, m.isim, m.soyisim FROM mahalle_sakinleri m WHERE m."cam(kg)" > 0

    UNION

    SELECT m.telefon, m.isim, m.soyisim FROM mahalle_sakinleri m WHERE m."elektronik(kg)" > 0;

END;
$$ LANGUAGE plpgsql;


--INDEX: Mahalle Sakini İsmi
CREATE INDEX idx_mahalle_sakinleri_isim
ON mahalle_sakinleri (isim);


--FUNCTION: Index Kullanarak Mahalle Sakini Bulma
CREATE OR REPLACE FUNCTION mahalle_sakini_isme_gore_getir (p_isim VARCHAR)
RETURNS TABLE (telefon BIGINT, isim VARCHAR, soyisim VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT m.telefon, m.isim, m.soyisim FROM mahalle_sakinleri m WHERE m.isim = p_isim;
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Aylık Atık Kaydı Sıfırlama
CREATE OR REPLACE FUNCTION aylik_atiklari_sifirla()
RETURNS VOID AS $$
BEGIN
    UPDATE mahalle_sakinleri
    SET
        "karton(kg)" = 0,
        "cam(kg)" = 0,
        "elektronik(kg)" = 0;

    RAISE NOTICE 'Mahalle sakinlerinin aylık atık miktarları sıfırlandı.';
END;
$$ LANGUAGE plpgsql;


--FUNCTION: Mahalle Sakini Sil
CREATE OR REPLACE FUNCTION mahalle_sakini_sil (p_telefon BIGINT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM mahalle_sakinleri WHERE telefon = p_telefon;

    RAISE NOTICE 'Mahalle sakini sistemden silindi.';
END;
$$ LANGUAGE plpgsql;


--TRIGGER FUNCTION: Atık Eklenirse Depoyu GÜncelle
CREATE OR REPLACE FUNCTION trg_atiklari_depoya_ekle()
RETURNS trigger AS $$
DECLARE
    fark_karton INTEGER;
    fark_cam INTEGER;
    fark_elektronik INTEGER;
BEGIN

    fark_karton := NEW."karton(kg)" - OLD."karton(kg)";
    IF fark_karton > 0 THEN
        UPDATE depo SET "miktar(kg)" = "miktar(kg)" + fark_karton WHERE atik_ismi = 'karton';
    END IF;

    fark_cam := NEW."cam(kg)" - OLD."cam(kg)";
    IF fark_cam > 0 THEN
        UPDATE depo SET "miktar(kg)" = "miktar(kg)" + fark_cam WHERE atik_ismi = 'cam';
    END IF;

    fark_elektronik := NEW."elektronik(kg)" - OLD."elektronik(kg)";
    IF fark_elektronik > 0 THEN
        UPDATE depo SET "miktar(kg)" = "miktar(kg)" + fark_elektronik WHERE atik_ismi = 'elektronik';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--TRIGGER
CREATE TRIGGER mahalle_sakini_atik_update
AFTER UPDATE OF "karton(kg)", "cam(kg)", "elektronik(kg)"
ON mahalle_sakinleri
FOR EACH ROW
EXECUTE FUNCTION trg_atiklari_depoya_ekle();


--TRIGGER FUNCTON: Atık Çıkarılırsa Depoyu GÜncelle
CREATE OR REPLACE FUNCTION trg_randevu_depoyu_guncelle()
RETURNS trigger AS $$
BEGIN
    UPDATE depo SET "miktar(kg)" = "miktar(kg)" - NEW."miktar(kg)" WHERE id = NEW.atik_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--TRIGGER
CREATE TRIGGER randevu_insert_depo_update
AFTER INSERT ON randevular
FOR EACH ROW
EXECUTE FUNCTION trg_randevu_depoyu_guncelle();


--Yetkili Tanımları
INSERT INTO yetkililer VALUES (0,'0','admin','admin')
