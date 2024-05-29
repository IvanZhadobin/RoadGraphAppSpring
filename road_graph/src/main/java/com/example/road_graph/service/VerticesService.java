package com.example.road_graph.service;

import com.example.road_graph.dto.VerticesDto;
import com.example.road_graph.entities.Vertices;

import java.util.List;

public interface VerticesService {
    Vertices findById(Long id);
    List<Vertices> findAll();

    Vertices add(VerticesDto vertices);

}
