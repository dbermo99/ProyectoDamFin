DROP DATABASE IF EXISTS ProyectoFinal;
CREATE DATABASE ProyectoFinal;
USE ProyectoFinal;

CREATE TABLE Usuario
(
    id INTEGER NOT NULL,
    email VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    usuario VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    contrasenna VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    nombre VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    apellidos VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    privada BOOLEAN NOT NULL,
    foto VARCHAR(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE Publicacion
(
    id INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    texto VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
    foto VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE Solicitud
(
    id INTEGER NOT NULL,
    id_usuario1 INTEGER NOT NULL,
    id_usuario2 INTEGER NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE Amistad
(
    id INTEGER NOT NULL,
    id_usuario1 INTEGER NOT NULL,
    id_usuario2 INTEGER NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE Megusta
(
    id INTEGER NOT NULL,
    id_publicacion INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE Comentario
(
    id INTEGER NOT NULL,
    id_publicacion INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    comentario VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `Usuario`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `email` (`email`),
    ADD UNIQUE KEY `usuario` (`usuario`);
ALTER TABLE `Usuario`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `Publicacion`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `PERTENECE_A` FOREIGN KEY (`id_usuario`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Publicacion`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `Solicitud`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `ENVIADA_POR` FOREIGN KEY (`id_usuario1`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `RECIBIDA_POR` FOREIGN KEY (`id_usuario2`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Solicitud`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `Amistad`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `SOLICITADA_POR` FOREIGN KEY (`id_usuario1`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `ACEPTADA_POR` FOREIGN KEY (`id_usuario2`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Amistad`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `Megusta`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `PUBLICACION` FOREIGN KEY (`id_publicacion`) REFERENCES `Publicacion`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `PULSADO_POR` FOREIGN KEY (`id_usuario`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Megusta`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `Comentario`
    ADD PRIMARY KEY (`id`),
    ADD CONSTRAINT `PUBLICADO_EN` FOREIGN KEY (`id_publicacion`) REFERENCES `Publicacion`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `PUBLICADO_POR` FOREIGN KEY (`id_usuario`) REFERENCES `Usuario`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Comentario`
    MODIFY `id` INTEGER NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

INSERT INTO `usuario` (`id`, `email`, `usuario`, `contrasenna`, `nombre`, `apellidos`, `privada`, `foto`) VALUES
(1, 'david@gmail.com', 'davidbm99', 'david1234', 'David', 'Bermejo Moreno', 1, 'fotoPerfil.jpg'),
(2, 'pepe@gmail.com', 'pepe2000', 'pepe1234', 'Pepe', 'G G', 1, 'usuario2.png'),
(3, 'jesus@gmail.com', 'jesus1314', 'jesus1234', 'Jesús', 'García G', 0, 'usuario3.png'),
(4, 'lucia@gmail.com', 'lucia_gg', 'lucua1234', 'Lucía', 'g g', 0, 'usuario4.png'),
(5, 'amanda@gmail.com', 'amandadg', 'amanda', 'Amandaaa', 'D G', 1, 'usuario5.png');

INSERT INTO `amistad` (`id`, `id_usuario1`, `id_usuario2`) VALUES
(2, 5, 1),
(3, 1, 4),
(4, 1, 2);

INSERT INTO `publicacion` (`id`, `id_usuario`, `texto`, `foto`) VALUES
(1, 1, 'primeraa', 'publicacion1.png'),
(3, 2, 'ertgegr', 'publicacion3.png'),
(4, 3, 'ryhtujetr erg', 'publicacion4.png'),
(6, 1, 'Ñam ñam', 'publicacion6.png'),
(7, 1, 'pc', 'publicacion7.png');

INSERT INTO `solicitud` (`id`, `id_usuario1`, `id_usuario2`) VALUES
(3, 4, 1),
(5, 1, 3);

INSERT INTO `megusta` (`id`, `id_publicacion`, `id_usuario`) VALUES
(5, 3, 1),
(6, 7, 1);
