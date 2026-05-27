-- 1. ROLES
MERGE INTO roles (id, name) KEY(id) VALUES (1, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY(id) VALUES (2, 'ROLE_ARTIST');

-- 2. USUARIOS
-- Contraseñas cifradas con BCrypt:
-- 'admin123' -> $2a$10$5Ovs1H/hoT48uwRdiAMiL.gnZISqmhtXuou7eLvNem0OfyQSAUsQK
MERGE INTO users (username, password, email, role_id, public_profile, allow_downloads, comments_privacy, image_visibility, created_at)
    KEY(username) VALUES ('admin', '$2a$10$5Ovs1H/hoT48uwRdiAMiL.gnZISqmhtXuou7eLvNem0OfyQSAUsQK', 'admin@pixay.com', 1, true, true, 'PUBLIC', 'PUBLIC', current_timestamp);
MERGE INTO users (username, password, email, role_id, public_profile, allow_downloads, comments_privacy, image_visibility, created_at)
    KEY(username) VALUES ('juan', '$2a$10$5Ovs1H/hoT48uwRdiAMiL.gnZISqmhtXuou7eLvNem0OfyQSAUsQK', 'juan@pixay.com', 2, true, true, 'PUBLIC', 'PUBLIC', current_timestamp);
MERGE INTO users (username, password, email, role_id, public_profile, allow_downloads, comments_privacy, image_visibility, created_at)
    KEY(username) VALUES ('maria', '$2a$10$5Ovs1H/hoT48uwRdiAMiL.gnZISqmhtXuou7eLvNem0OfyQSAUsQK', 'maria@pixay.com', 2, true, true, 'PUBLIC', 'PUBLIC', current_timestamp);
MERGE INTO users (username, password, email, role_id, public_profile, allow_downloads, comments_privacy, image_visibility, created_at)
    KEY(username) VALUES ('julia', '$2a$10$5Ovs1H/hoT48uwRdiAMiL.gnZISqmhtXuou7eLvNem0OfyQSAUsQK', 'julia@correo.com', 2, true, true, 'PUBLIC', 'PUBLIC', current_timestamp);

-- 3. CATEGORÍAS (Asegúrate de que la entidad se llame Category y la tabla categories)
MERGE INTO categories (id, name) KEY(id) VALUES (1, 'Fotografía');
MERGE INTO categories (id, name) KEY(id) VALUES (2, 'Ilustración');

--  4. SUBCATEGORÍAS
-- FOTOGRAFÍA (category_id = 1)
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Arquitectura', 1);       --1
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Blanco y Negro', 1);     --2
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Cine', 1);               --3
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Fotografía Urbana', 1);  --4
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('IA', 1);                 --5
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Macro', 1);              --6
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Meme', 1);               --7
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Moda', 1);               --8
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Naturaleza', 1);         --9
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Paisaje', 1);            --10
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Retrato', 1);            --11
-- ILUSTRACIÓN (category_id = 2)
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Acuarela Digital', 2);   --12
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Arte Conceptual', 2);    --13
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Boceto', 2);             --14
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Bodegón', 2);            --15
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Caricatura', 2);         --16
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Cómic / Manga', 2);      --17
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Fan Art', 2);            --18
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('IA', 2);                 --19
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Ilustración Digital', 2);--20
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Minimalismo', 2);        --21
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Naturaleza', 2);         --22
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Paisaje', 2);            --23
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Pixel Art', 2);          --24
MERGE INTO subcategories (name, category_id) KEY(name, category_id) VALUES ('Retrato', 2);            --25

--IMÁGENES DE MUESTRA
-- USUARIO 1 (Admin)
INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Angel Beats', 1, 2, 17, FILE_READ('src/main/resources/static/inicio/angel-beats.jpg')); -- 17: Cómic/Manga

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Criss & Cross', 1, 2, 5, FILE_READ('src/main/resources/static/inicio/CrissCross.png')); -- 11: Retrato (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Criss&Cross', 1, 1, 11, FILE_READ('src/main/resources/static/inicio/CrissCross2.jpg')); -- 11: Retrato (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Darker than black', 1, 2, 17, FILE_READ('src/main/resources/static/inicio/darker-than-black.jpg')); -- 17: Cómic/Manga

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Escarabajo', 1, 2, 22, FILE_READ('src/main/resources/static/inicio/Escarabajo.png')); -- 22: Naturaleza (Ilus)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Garden for you', 1, 2, 22, FILE_READ('src/main/resources/static/inicio/garden4u.png')); -- 22: Naturaleza (Ilus)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Life is Strange', 1, 2, 18, FILE_READ('src/main/resources/static/inicio/lis.jpg')); -- 18: Fan Art

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Life is Strange: True Colors', 1, 2, 18, FILE_READ('src/main/resources/static/inicio/lis-true-colors.jpg')); -- 18: Fan Art

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Maga', 1, 1, 5, FILE_READ('src/main/resources/static/inicio/maga.PNG')); -- 5: IA (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Mariquita', 1, 1, 9, FILE_READ('src/main/resources/static/inicio/mariquita.jpg')); -- 9: Naturaleza (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Meme', 1, 1, 7, FILE_READ('src/main/resources/static/inicio/meme.jpeg')); -- 7: Meme (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Monstera', 1, 2, 14, FILE_READ('src/main/resources/static/inicio/monstera.jpg')); -- 14: Boceto

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Pelirroja', 1, 1, 11, FILE_READ('src/main/resources/static/inicio/Pelirroja.PNG')); -- 11: Retrato (Foto)

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Pixel art', 1, 2, 24, FILE_READ('src/main/resources/static/inicio/pixel-art.jpeg')); -- 24: Pixel Art

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Zoro Roronoa', 1, 2, 17, FILE_READ('src/main/resources/static/inicio/Zoro-Roronoa.png')); -- 17: Cómic/Manga

-- USUARIO 2 (Juan)
INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Bleach', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Bleach_gang.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Bleach Rukia', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/bleach_rukia_ichigo.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Ceruledge', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Ceruledge.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Dragonair', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Dragonair.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Hoenn', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Hoenn.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Vegeta', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Majin_Vegeta.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Mark Grayson', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/mark_grayson.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Power Ultrakill', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Power_Ultrakill.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Scizor', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Scizor.jpg'));

INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Totoro hora de aventuras', 2, 2, 17, FILE_READ('src/main/resources/static/inicio/Totoro_hora_aventuras.jpg'));

--USER 3 (Maria)
INSERT INTO users_images (title, user_id, category_id, subcategory_id, content)
VALUES ('Doris', 3, 1, 11, FILE_READ('src/main/resources/static/inicio/Doris.JPG'));
-- select title, c.name, s.name from users_images i
--                                       inner join categories c on i.category_id = c.id
--                                       inner join subcategories s on i.subcategory_id = s.id;
--
-- select c.name, s.name from subcategories s
--                                inner join categories c on c.id = s.category_id;
--
-- select * from categories