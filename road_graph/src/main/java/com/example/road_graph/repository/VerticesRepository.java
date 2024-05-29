package com.example.road_graph.repository;

import com.example.road_graph.entities.Vertices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VerticesRepository  extends JpaRepository<Vertices, Long> {
}

