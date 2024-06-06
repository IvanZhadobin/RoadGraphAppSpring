package com.example.road_graph.controllers;

import com.example.road_graph.entities.Edge;
import com.example.road_graph.entities.Vertices;
import com.example.road_graph.entities.Point;
import com.example.road_graph.service.EdgeService;
import com.example.road_graph.service.GraphService;
import com.example.road_graph.service.PointService;
import com.example.road_graph.service.VerticesService;
import com.example.road_graph.service.serviceImpl.EdgeServiceImpl;
import com.example.road_graph.service.serviceImpl.GraphServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class GraphController {
    private final GraphService graphService;
    private final VerticesService verticesService;
    private final EdgeService edgeService;
    private final PointService pointService;

    @Autowired
    public GraphController(GraphServiceImpl graphService, VerticesService verticesService, EdgeServiceImpl edgeService, PointService pointService) {
        this.graphService = graphService;
        this.verticesService = verticesService;
        this.edgeService = edgeService;
        this.pointService = pointService;
    }

    @GetMapping("/buildGraph")
    public String buildGraph() {
        graphService.detectRoadSegmentsFromDatabase();
        return "Graph built successfully";
    }

    @GetMapping("/drawGraph")
    public ResponseEntity<byte[]> drawGraph() throws IOException {
        List<Point> points = pointService.getAll();
        List<Vertices> vertices = verticesService.getAll();
        List<Edge> edges = edgeService.getAll();

        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);


        double latMin = points.stream().mapToDouble(Point::getLatitude).min().orElse(0);
        double latMax = points.stream().mapToDouble(Point::getLatitude).max().orElse(0);
        double lonMin = points.stream().mapToDouble(Point::getLongitude).min().orElse(0);
        double lonMax = points.stream().mapToDouble(Point::getLongitude).max().orElse(0);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        // Отрисовка обработанного графа
        for (Edge edge : edges) {
            Vertices startVertex = edge.getStartVertex();
            Vertices endVertex = edge.getFinishVertex();

            int startX = (int) ((startVertex.getLongitude() - lonMin) / (lonMax - lonMin) * width);
            int startY = (int) ((startVertex.getLatitude() - latMin) / (latMax - latMin) * height);
            int endX = (int) ((endVertex.getLongitude() - lonMin) / (lonMax - lonMin) * width);
            int endY = (int) ((endVertex.getLatitude() - latMin) / (latMax - latMin) * height);

            g.drawLine(startX, startY, endX, endY);
        }

        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);

        return ResponseEntity.ok().headers(headers).body(imageBytes);
    }
}
