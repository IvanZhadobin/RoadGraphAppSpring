package com.example.road_graph.service.serviceImpl;

import com.example.road_graph.dto.VerticesDto;
import com.example.road_graph.entities.Vertices;
import com.example.road_graph.exception.ResourceAlreadyExistsException;
import com.example.road_graph.exception.ResourceNotFoundException;
import com.example.road_graph.repository.VerticesRepository;
import com.example.road_graph.service.VerticesService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VerticesServiceImpl implements VerticesService {

    @Autowired
    VerticesRepository verticesRepository;

    @Override
    public List<Vertices> getAll() {
        return verticesRepository.findAll();
    }


    @Override
    public Vertices add(VerticesDto verticesDto) {

        Vertices vertices = new Vertices();

        initVerticesFromDto(vertices, verticesDto);

        return verticesRepository.save(vertices);
    }
    private void initVerticesFromDto(Vertices vertices, VerticesDto verticesDto) {
        vertices.setLatitude(verticesDto.getLatitude());
        vertices.setLongitude(verticesDto.getLongitude());
        vertices.setWeight(verticesDto.getWeight());
    }
}
