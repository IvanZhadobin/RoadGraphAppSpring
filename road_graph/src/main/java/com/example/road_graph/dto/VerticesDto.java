package com.example.road_graph.dto;

import com.example.road_graph.entities.Vertices;
import lombok.Data;

@Data
public class VerticesDto {
    private Long vertexId;
    private double latitude;
    private double longitude;
    private int weight;

    public VerticesDto(Vertices v) {
        this.vertexId = v.getVertexId();
        this.latitude = v.getLatitude();
        this.longitude = v.getLongitude();
        this.weight = v.getWeight();
    }
}
