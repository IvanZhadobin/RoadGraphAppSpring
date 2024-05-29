package com.example.road_graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class RoadGraphApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(RoadGraphApplication.class, args);
	}

}
