-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 04-06-2025 a las 22:28:30
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `biblioteca_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `libros`
--

CREATE TABLE `libros` (
  `id` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `autor` varchar(255) NOT NULL,
  `isbn` varchar(20) NOT NULL,
  `genero` varchar(100) DEFAULT NULL,
  `anio_publicacion` int(11) DEFAULT NULL,
  `disponible` tinyint(1) DEFAULT 1,
  `numero_ejemplares` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `libros`
--

INSERT INTO `libros` (`id`, `titulo`, `autor`, `isbn`, `genero`, `anio_publicacion`, `disponible`, `numero_ejemplares`) VALUES
(1, 'El Principito', 'Antoine de Saint-Exupéry', '9788478887194', 'Novela Infantil - Juvenil', 1943, 1, 1),
(9, 'Viaje al centro de la tierra', 'Julio Verne', '9788427213548', 'Novela - Aventuras', 1864, 0, 0),
(10, 'Veinte Mil Leguas de Viaje Submarino', 'Julio Verne', '9788427213739', 'Novela - Aventuras', 1870, 1, 1),
(11, 'Los hijos del capitán Grant ', 'Julio Verne', '9788427213821', 'Novela - Aventuras', 1868, 1, 2),
(12, 'Miguel Strogoff', 'Julio Verne', '9788427213838', 'Novela - Aventuras', 1876, 1, 1),
(13, 'Dos años de vacaciones', 'Julio Verne', '9788427208858', 'Novela - Aventuras', 1888, 1, 2),
(14, 'La vuelta al mundo en 80 días', 'Julio Verne', '9788427211575', 'Novela - Aventuras', 1873, 1, 1),
(15, 'Cinco semanas en globo', 'Julio Verne', '9788427246751', 'Novela - Aventuras', 1863, 1, 1),
(16, 'Don Quijote de la Mancha', 'Miguel de Cervantes Saavedra', '9788466236645', 'Literatura Universal', 1605, 0, 0),
(17, 'El Lazarillo de Tormes', 'Anónimo', '9788497590570', 'Literatura Universal', 1553, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `id` int(11) NOT NULL,
  `libro_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `fecha_reserva` date NOT NULL,
  `fecha_devolucion_prevista` date NOT NULL,
  `fecha_devolucion_real` date DEFAULT NULL,
  `estado` varchar(50) NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`id`, `libro_id`, `usuario_id`, `fecha_reserva`, `fecha_devolucion_prevista`, `fecha_devolucion_real`, `estado`) VALUES
(8, 9, 3, '2025-06-04', '2025-06-18', NULL, 'CANCELADO'),
(9, 9, 4, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(10, 10, 5, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(11, 11, 5, '2025-06-04', '2025-06-18', '2025-06-21', 'RETRASO'),
(12, 12, 5, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(13, 13, 5, '2025-06-04', '2025-06-18', '2025-06-18', 'DEVUELTO'),
(14, 14, 5, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(15, 17, 7, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(16, 15, 6, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(17, 1, 8, '2025-06-04', '2025-06-18', NULL, 'PENDIENTE'),
(18, 16, 9, '2025-06-04', '2025-06-20', NULL, 'PENDIENTE'),
(19, 16, 10, '2025-06-04', '2025-06-20', NULL, 'PENDIENTE');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `apellidos` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `sancionado` tinyint(1) NOT NULL DEFAULT 0,
  `fecha_fin_sancion` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `apellidos`, `email`, `telefono`, `activo`, `sancionado`, `fecha_fin_sancion`) VALUES
(1, 'prueba1', 'prueba1', 'prueba1@prueba.com', '111-111111', 1, 1, '2025-06-06'),
(2, 'prueba2', 'prueba2', 'prueba2@prueba.com', '222-222222', 0, 0, NULL),
(3, 'Carla', 'Ruiz Palencia', 'carla@example.com', '333-333333', 1, 0, NULL),
(4, 'José', 'Pérez Gómez', 'jose@example.com', '444-444444', 1, 0, NULL),
(5, 'Juan', 'Valderrama Almería', 'juan@example.com', '555-555555', 1, 1, '2025-06-07'),
(6, 'Carmen', 'Sevilla López', 'carmen@example.com', '666-666666', 1, 0, NULL),
(7, 'Isabel', 'Allende Merchán', 'isabel@example.com', '777-777777', 1, 0, NULL),
(8, 'Carlos', 'Fernández Herrera', 'carlos@example.com', '888-888888', 1, 0, NULL),
(9, 'Laura', 'Alhambra de la Fuente', 'laura@example.com', '999-999999', 1, 0, NULL),
(10, 'miguel', 'Bueno Ventura', 'miguel@example.com', '123-456789', 1, 0, NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `libros`
--
ALTER TABLE `libros`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `isbn` (`isbn`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `libro_id` (`libro_id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `libros`
--
ALTER TABLE `libros`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`libro_id`) REFERENCES `libros` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
