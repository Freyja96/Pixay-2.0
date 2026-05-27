CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
email VARCHAR(100) NOT NULL,
role_id BIGINT NOT NULL,
profile_picture LONGBLOB,
description VARCHAR(255),
public_profile BOOLEAN NOT NULL DEFAULT TRUE,
allow_downloads BOOLEAN NOT NULL DEFAULT TRUE,
comments_privacy VARCHAR(255) NOT NULL DEFAULT 'PUBLIC',
image_visibility VARCHAR(20) DEFAULT 'PUBLIC',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

FOREIGN KEY (role_id) REFERENCES roles(id)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS image_visibility VARCHAR(20) DEFAULT 'PUBLIC';

CREATE TABLE IF NOT EXISTS follows (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       follower_id BIGINT NOT NULL,
                                       followed_id BIGINT NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (follower_id) REFERENCES users(id),
                                       FOREIGN KEY (followed_id) REFERENCES users(id),
                                       CHECK (follower_id <> followed_id)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS subcategories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    --category VARCHAR(50) NOT NULL, --mejor ponerlo por id
    category_id BIGINT NOT NULL,

    FOREIGN KEY (category_id) REFERENCES categories(id)
);
--tabla de imágenes!!
-- @Id
-- @GeneratedValue(strategy = GenerationType.IDENTITY)
-- private Long id;
--
-- private byte[] content; //archivo binario de UNA foto
--
-- @ManyToOne
-- @JoinColumn(name = "user_id")
-- private User user_id; //a quién pertenece
CREATE TABLE IF NOT EXISTS users_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    content LONGBLOB NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    subcategory_id BIGINT,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (subcategory_id) REFERENCES subcategories(id)
);

CREATE TABLE IF NOT EXISTS saved_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    image_id BIGINT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (image_id) REFERENCES users_images(id)
);

CREATE TABLE IF NOT EXISTS user_follows (
    follower_id BIGINT NOT NULL, -- El que da "seguir"
    following_id BIGINT NOT NULL, -- El que es seguido
    followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id),
    CONSTRAINT self_follow_check CHECK (follower_id <> following_id)
);

CREATE TABLE IF NOT EXISTS image_comments (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      content TEXT NOT NULL,
      user_id BIGINT NOT NULL,
      image_id BIGINT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      FOREIGN KEY (user_id) REFERENCES users(id),
      FOREIGN KEY (image_id) REFERENCES users_images(id)
);
CREATE TABLE IF NOT EXISTS image_reactions (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               user_id BIGINT NOT NULL,
                                               image_id BIGINT NOT NULL,
                                               reaction_type INT NOT NULL, -- código del emoji

                                               FOREIGN KEY (user_id) REFERENCES users(id),
                                               FOREIGN KEY (image_id) REFERENCES users_images(id),
                                               CONSTRAINT unique_user_image_reaction UNIQUE(user_id, image_id)
);