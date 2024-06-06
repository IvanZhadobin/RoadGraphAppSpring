package com.example.road_graph.service;

import com.example.road_graph.dto.EdgeDto;
import com.example.road_graph.entities.Edge;

import java.util.List;

public interface EdgeService {
    List<Edge> getAll();
    Edge add(EdgeDto edgeDto);
}
