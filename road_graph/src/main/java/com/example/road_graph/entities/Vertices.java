package com.example.road_graph.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="vertices")
public class Vertices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "vertices_seq_gen", sequenceName = "vertices_seq", allocationSize = 1)
    @Column(name = "vertex_id")
    private Long vertexId;
    private double latitude;
    private double longitude;
    private int weight;
}
