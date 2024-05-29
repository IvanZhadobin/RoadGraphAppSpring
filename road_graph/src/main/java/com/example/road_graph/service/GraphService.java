package com.example.road_graph.service;

import com.example.road_graph.entities.Point;

import com.example.road_graph.entities.Vertices;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public interface GraphService {
    List<Vertices> detectRoadSegmentsFromDatabase();

    List<Vertices> detectRoadSegments(List<Point> points, double latMin, double latMax, double lonMin, double lonMax);
}
