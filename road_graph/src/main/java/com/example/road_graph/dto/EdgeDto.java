package com.example.road_graph.dto;

import com.example.road_graph.entities.Edge;
import com.example.road_graph.entities.Vertices;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class EdgeDto {
    private Long edgeId;
    private Vertices startVertex;
    private Vertices finishVertex;

    public EdgeDto(Edge e) {
        this.edgeId = e.getEdgeId();
        this.startVertex = e.getStartVertex();
        this.finishVertex = e.getFinishVertex();
    }
}
