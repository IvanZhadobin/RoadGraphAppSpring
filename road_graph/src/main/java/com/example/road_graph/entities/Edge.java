package com.example.road_graph.entities;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Edges")
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "edge_seq_gen", sequenceName = "edge_seq", allocationSize = 1)
    @Column(name = "edge_id")
    private Long edgeId;

    @ManyToOne
    @JoinColumn(name = "start_vertex_id")
    private Vertices startVertex;

    @ManyToOne
    @JoinColumn(name = "end_vertex_id")
    private Vertices finishVertex;
}
