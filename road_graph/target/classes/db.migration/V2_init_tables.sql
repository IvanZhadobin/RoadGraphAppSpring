CREATE TABLE points
(
    point_id       SERIAL8 PRIMARY KEY,
    vehicle_number INTEGER,
    date_time      timestamp,
    latitude       double precision,
    longitude      double precision,
    speed          INTEGER,
    azimuth        INTEGER,
    run            INTEGER
);

-- Создание таблицы "Вершины" (vertices)
CREATE TABLE vertices
(
    vertex_id SERIAL8 PRIMARY KEY,
    longitude DOUBLE PRECISION,
    latitude  DOUBLE PRECISION,
    weight    DOUBLE PRECISION
);

-- Создание таблицы "Ребра" (edges)
CREATE TABLE edges
(
    edge_id         SERIAL8 PRIMARY KEY,
    start_vertex_id BIGINT REFERENCES vertices (vertex_id),
    end_vertex_id   BIGINT REFERENCES vertices (vertex_id)
);


ALTER TABLE vertices
    ALTER COLUMN weight TYPE INT USING weight::INTEGER;


--очистка таблиц
DELETE
FROM points;



DELETE
FROM edges;
DELETE
FROM vertices;


--Удаление координат за пределами Воронежской области
DELETE FROM Points
WHERE latitude < 49.976765 OR latitude > 53.163891
   OR longitude < 36.551792 OR longitude > 41.242798;



--Удаление координат за пределами города воронеж.
DELETE FROM Points
WHERE latitude < 51.643908 OR latitude > 51.723510
   OR longitude < 39.049917 OR longitude > 39.316335;

ALTER SEQUENCE vertices_seq RESTART WITH 1;
ALTER SEQUENCE edges_seq RESTART WITH 1;


--копирование из csv файла
\COPY points (vehicle_number, date_time, latitude, longitude, speed, azimuth, run) FROM 'D:/gps.csv' DELIMITER ';' CSV HEADER;