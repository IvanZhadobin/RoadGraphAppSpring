package com.example.road_graph.service.serviceImpl;

import com.example.road_graph.entities.Point;
import com.example.road_graph.exception.ResourceNotFoundException;
import com.example.road_graph.repository.PointRepository;
import com.example.road_graph.service.PointService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PointServiceImpl implements PointService {
    PointRepository pointRepository;

    @Override
    public Point findById(Long id) {
        return pointRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Точка с данным id: " + id + " не найдена!"));
    }

    @Override
    public List<Point> findPointsInRectangle(double latMin, double lonMin, double latMax, double lonMax) {
        return pointRepository.findPointsInRectangle(latMin, latMax, lonMin, lonMax);
    }

    @Override
    public List<Point> findAll() {
        return pointRepository.findAll();
    }


}
