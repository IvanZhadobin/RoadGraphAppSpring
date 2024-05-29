package com.example.road_graph.controllers;

import com.example.road_graph.entities.Edge;
import com.example.road_graph.entities.Vertices;
import com.example.road_graph.service.EdgeService;
import com.example.road_graph.service.GraphService;
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

    @Autowired
    public GraphController(GraphServiceImpl graphService, VerticesService verticesService, EdgeServiceImpl edgeService) {
        this.graphService = graphService;
        this.verticesService = verticesService;
        this.edgeService = edgeService;
    }

    @GetMapping("/buildGraph")
    public String buildGraph() {
        // Шаг 1: Вызов метода detectRoadSegmentsFromDatabase
        graphService.detectRoadSegmentsFromDatabase();
        return "Graph built successfully";
    }

    @GetMapping("/draw")
    public ResponseEntity<byte[]> drawGraph() throws IOException {
        // Шаг 2: Получение всех вершин из таблицы vertices
        List<Vertices> vertices = verticesService.findAll();


        // Задание размера изображения и масштабирования координат
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLUE);

        // Шаг 3: Преобразование координат в пиксели и отрисовка вершин
        double latMin = vertices.stream().mapToDouble(Vertices::getLatitude).min().orElse(0);
        double latMax = vertices.stream().mapToDouble(Vertices::getLatitude).max().orElse(0);
        double lonMin = vertices.stream().mapToDouble(Vertices::getLongitude).min().orElse(0);
        double lonMax = vertices.stream().mapToDouble(Vertices::getLongitude).max().orElse(0);

        for (Vertices vertex : vertices) {
            int x = (int) ((vertex.getLongitude() - lonMin) / (lonMax - lonMin) * width);
            int y = (int) ((vertex.getLatitude() - latMin) / (latMax - latMin) * height);
            g.fillOval(x, y, 1, 2);
        }

        // Шаг 4: Получение всех ребер из таблицы edges и отрисовка линий
        List<Edge> edges = edgeService.findAll();
        g.setColor(Color.RED);
        for (Edge edge : edges) {
            Vertices startVertex = edge.getStartVertex();
            Vertices endVertex = edge.getFinishVertex();

            int startX = (int) ((startVertex.getLongitude() - lonMin) / (lonMax - lonMin) * width);
            int startY = (int) ((startVertex.getLatitude() - latMin) / (latMax - latMin) * height);
            int endX = (int) ((endVertex.getLongitude() - lonMin) / (lonMax - lonMin) * width);
            int endY = (int) ((endVertex.getLatitude() - latMin) / (latMax - latMin) * height);

            g.drawLine(startX, startY, endX, endY);
        }

        // Завершение работы с изображением и возврат его в виде массива байтов
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
    }
}