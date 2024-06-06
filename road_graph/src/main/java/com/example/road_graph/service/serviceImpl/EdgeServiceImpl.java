package com.example.road_graph.service.serviceImpl;

import com.example.road_graph.dto.EdgeDto;
import com.example.road_graph.dto.VerticesDto;
import com.example.road_graph.entities.Edge;
import com.example.road_graph.entities.Vertices;
import com.example.road_graph.exception.ResourceNotFoundException;
import com.example.road_graph.repository.EdgeRepository;
import com.example.road_graph.service.EdgeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EdgeServiceImpl implements EdgeService {

    EdgeRepository edgeRepository;

    @Override
    public List<Edge> getAll() {
        return edgeRepository.findAll();
    }

    @Override
    public Edge add(EdgeDto edgeDto) {

        Edge edge = new Edge();

        initEdgeFromDto(edge, edgeDto);
        return edgeRepository.save(edge);
    }

    private void initEdgeFromDto(Edge edge, EdgeDto edgeDto) {
        edge.setStartVertex(edgeDto.getStartVertex());
        edge.setFinishVertex(edgeDto.getFinishVertex());
    }
}
