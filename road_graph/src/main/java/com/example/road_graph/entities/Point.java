package com.example.road_graph.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "point_id")
    private Long pointId;
    private double latitude;
    private double longitude;
    @Column(name = "vehicle_number")
    private Integer vehicleNumber;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    private Integer speed;
    private Integer azimuth;
    private Integer run;
}


