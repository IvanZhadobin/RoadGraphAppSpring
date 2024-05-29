package com.example.road_graph.repository;

import com.example.road_graph.entities.Point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT p FROM Point p WHERE p.latitude BETWEEN :latMin AND :latMax AND p.longitude BETWEEN :lonMin AND :lonMax")
    List<Point> findPointsInRectangle(@Param("latMin") double latMin, @Param("latMax") double latMax,
                                      @Param("lonMin") double lonMin, @Param("lonMax") double lonMax);
}
