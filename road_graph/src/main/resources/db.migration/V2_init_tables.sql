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


DELETE
FROM edges;
DELETE
FROM vertices;



DELETE
FROM points
where latitude =0;

ALTER TABLE vertices
    ALTER COLUMN weight TYPE INT USING weight::INTEGER;


DELETE FROM Points
WHERE latitude NOT BETWEEN 49.0 AND 52.0
   OR longitude NOT BETWEEN 37.0 AND 41.0;

CREATE SEQUENCE edges_seq START WITH 1 INCREMENT BY 1;

ALTER SEQUENCE vertices_seq RESTART WITH 1;
ALTER SEQUENCE edges_seq RESTART WITH 1;

