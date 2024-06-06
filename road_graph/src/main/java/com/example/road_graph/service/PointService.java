package com.example.road_graph.service;

import com.example.road_graph.entities.Point;
import java.util.List;

public interface PointService {
    List<Point> findPointsInRectangle(double lat1, double lat2, double lon1, double lon2);
    List<Point> getAll();
}

