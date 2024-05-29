package com.example.road_graph.service;

import com.example.road_graph.dto.EdgeDto;
import com.example.road_graph.entities.Edge;

import java.util.List;

public interface EdgeService { // метод для сбора точек
    Edge findById(Long id);
    List<Edge> findAll();
    Edge add(EdgeDto edgeDto);


}
