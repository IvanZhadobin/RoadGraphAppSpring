package com.example.road_graph.dto;

import com.example.road_graph.entities.Point;
import lombok.Data;
import org.geolatte.geom.Geometry;

import java.time.LocalDateTime;

@Data
public class PointDto {
    private Long pointId;
    private double latitude;
    private double longitude;
    private Integer vehicleNumber;
    private LocalDateTime dateTime;
    private Integer speed;
    private Integer azimuth;
    private Integer run;

    public PointDto(Point p) {
        this.pointId = p.getPointId();
        this.latitude = p.getLatitude();
        this.longitude = p.getLongitude();
        this.vehicleNumber = p.getVehicleNumber();
        this.dateTime = p.getDateTime();
        this.speed = p.getSpeed();
        this.azimuth = p.getAzimuth();
        this.run = p.getRun();
    }
}
