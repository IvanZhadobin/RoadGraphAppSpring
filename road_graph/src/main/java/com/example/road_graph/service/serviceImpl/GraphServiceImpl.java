package com.example.road_graph.service.serviceImpl;

import com.example.road_graph.dto.EdgeDto;
import com.example.road_graph.dto.VerticesDto;
import com.example.road_graph.entities.Edge;
import com.example.road_graph.entities.Point;
import com.example.road_graph.entities.Vertices;
import com.example.road_graph.service.GraphService;
import com.example.road_graph.service.VerticesService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.*;

@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {
    @Autowired
    private VerticesService verticesService;
    @Autowired
    private final PointServiceImpl pointService;
    @Autowired
    private final EdgeServiceImpl edgeService;
    private static final double EARTH_RADIUS = 6371.0;


    public List<Vertices> detectRoadSegmentsFromDatabase() {
        List<Vertices> allVertices = new ArrayList<>();


        List<Point> allPoints = pointService.findAll();

        double latMin = allPoints.stream().mapToDouble(Point::getLatitude).min().orElse(Double.NaN);
        double latMax = allPoints.stream().mapToDouble(Point::getLatitude).max().orElse(Double.NaN);
        double lonMin = allPoints.stream().mapToDouble(Point::getLongitude).min().orElse(Double.NaN);
        double lonMax = allPoints.stream().mapToDouble(Point::getLongitude).max().orElse(Double.NaN);

        double latDiff = 1.0 / 111.0;
        double lonDiff = 1.0 / 70.0;

        double prevLatMin = latMin;

        for (double lat = latMin; lat <= latMax; lat += latDiff) {
            double currentLatMax = lat + latDiff;
            if (currentLatMax > latMax) {
                currentLatMax = latMax;
            }

            double prevLonMin = lonMin;
            for (double lon = lonMin; lon <= lonMax; lon += lonDiff) {
                double currentLonMax = lon + lonDiff;
                if (currentLonMax > lonMax) {
                    currentLonMax = lonMax;
                }

                // Получаем точки в текущем маленьком квадрате
                List<Point> pointsInSquare = pointService.findPointsInRectangle(prevLatMin, prevLonMin, currentLatMax, currentLonMax);

                // Производим обнаружение дорожных сегментов в текущем маленьком квадрате
                List<Vertices> verticesInSquare = detectRoadSegments(pointsInSquare, prevLatMin, prevLonMin, currentLatMax, currentLonMax);

                // Добавляем найденные вершины к общему списку
                allVertices.addAll(verticesInSquare);

                prevLonMin = currentLonMax;
            }
            prevLatMin = currentLatMax;
        }

        return allVertices;
    }

    @Override
    public List<Vertices> detectRoadSegments(List<Point> points, double latMin, double lonMin, double latMax, double lonMax) {
        List<Vertices> roadSegments;

        int[][] accumulatorSpace = createAccumulatorSpace();

        Map<Pair<Integer, Integer>, List<Integer>> coordinatesMap = new HashMap<>();

        accumulateVotes(points, accumulatorSpace, coordinatesMap, latMin, lonMin, latMax, lonMax);

        roadSegments = findRoadSegments(accumulatorSpace, points, coordinatesMap);

        return roadSegments;
    }

    private int[][] createAccumulatorSpace() {
        int[][] accumulatorSpace = new int[180][1000];
        for (int i = 0; i < 180; i++) {
            for (int j = 0; j < 1000; j++) {
                accumulatorSpace[i][j] = 0;
            }
        }
        return accumulatorSpace;
    }

    private void accumulateVotes(List<Point> points, int[][] accumulatorSpace, Map<Pair<Integer, Integer>, List<Integer>> coordinatesMap, double latMin, double lonMin, double latMax, double lonMax) {
        for (int index = 0; index < points.size(); index++) {
            Point point = points.get(index);

            if (index < points.size() - 1) {//проверка чтобы не выходить за границы index+1
                Point prevPoint = points.get(index + 1);
                int distanceInMeters = (int) Math.round(calculateDistance(point.getLatitude(), point.getLongitude(), prevPoint.getLatitude(), prevPoint.getLongitude()) * 1000);
                long timeDifference = Math.abs(prevPoint.getDateTime().toEpochSecond(ZoneOffset.UTC) - point.getDateTime().toEpochSecond(ZoneOffset.UTC));

                if (distanceInMeters > 300 || timeDifference > 60) {
                    continue; // Пропускаем текущую точку, если уд условию
                }
            }
            double latitude = point.getLatitude();
            double longitude = point.getLongitude();

            double theta = calculateTheta(latMin, lonMin, latitude, longitude, latMax, lonMax);

            double rho = calculateDistance(latMin, lonMin, latitude, longitude);
            int rhoIndex = (int) rho;

            if (rhoIndex >= 0 && rhoIndex < accumulatorSpace[0].length) {
                int thetaIndex = (int) theta;
                if (thetaIndex >= 0 && thetaIndex < accumulatorSpace.length) {
                    Pair<Integer, Integer> thetaRhoPair = Pair.of(thetaIndex, rhoIndex); //если в списке подряд идущие точки попадаются на один то средние от начальных точек
                    List<Integer> indexList = coordinatesMap.computeIfAbsent(thetaRhoPair, k -> new ArrayList<>());
                    indexList.add(index);
                    accumulatorSpace[thetaIndex][rhoIndex]++;
                }
            }
        }
    }


    private double calculateTheta(double latA, double lonA, double latD, double lonD, double latMax, double lonMax) {
        double deltaX_AC = lonMax - lonA;
        double deltaY_AC = 0;

        double deltaX_AD = lonD - lonA;
        double deltaY_AD = latD - latA;

        double angle_AC = Math.atan2(deltaY_AC, deltaX_AC);

        double angle_AD = Math.atan2(deltaY_AD, deltaX_AD);

        double theta = Math.toDegrees(angle_AD - angle_AC);
        if (theta < 0) {
            theta += 360;
        }
        return theta;
    }

    private double calculateDistance(double latA, double lonA, double latD, double lonD) {
        double latMin = Math.toRadians(latA);
        double lonMin = Math.toRadians(lonA);
        double latDot = Math.toRadians(latD);
        double lonDot = Math.toRadians(lonD);

        double deltaLon = lonDot - lonMin;
        double deltaLat = latDot - latMin;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(latMin) * Math.cos(latDot) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private List<Vertices> findRoadSegments(int[][] accumulatorSpace, List<Point> points, Map<Pair<Integer, Integer>, List<Integer>> coordinatesMap) {
        List<Vertices> verticesList = new ArrayList<>();
        int threshold = 0;
        Vertices previousEndVertex = null;  // добавим переменную для хранения предыдущей конечной вершины

        for (int theta = 0; theta < 180; theta++) {
            for (int rhoIndex = 0; rhoIndex < accumulatorSpace[0].length; rhoIndex++) {
                if (accumulatorSpace[theta][rhoIndex] > threshold) {
                    List<Integer> relevantIndices = coordinatesMap.get(Pair.of(theta, rhoIndex));
                    if (relevantIndices != null && !relevantIndices.isEmpty()) {
                        double totalStartLatitude = 0.0;
                        double totalStartLongitude = 0.0;
                        double totalEndLatitude = 0.0;
                        double totalEndLongitude = 0.0;
                        int countStart = 0;
                        int countEnd = 0;

                        for (Integer index : relevantIndices) {
                            if (index >= 0 && index < points.size()) {
                                Point startPoint = points.get(index);
                                totalStartLatitude += startPoint.getLatitude();
                                totalStartLongitude += startPoint.getLongitude();
                                countStart++;

                                if (index < points.size() - 1) {
                                    Point endPoint = points.get(index + 1);
                                    totalEndLatitude += endPoint.getLatitude();
                                    totalEndLongitude += endPoint.getLongitude();
                                    countEnd++;
                                }
                            }
                        }

                        if (countStart > 0 && countEnd > 0) {
                            double averageStartLatitude = totalStartLatitude / countStart;
                            double averageStartLongitude = totalStartLongitude / countStart;
                            double averageEndLatitude = totalEndLatitude / countEnd;
                            double averageEndLongitude = totalEndLongitude / countEnd;

                            VerticesDto startVertexDto = new VerticesDto(new Vertices());
                            startVertexDto.setLongitude(averageStartLongitude);
                            startVertexDto.setLatitude(averageStartLatitude);
                            startVertexDto.setWeight(accumulatorSpace[theta][rhoIndex]);

                            VerticesDto endVertexDto = new VerticesDto(new Vertices());
                            endVertexDto.setLongitude(averageEndLongitude);
                            endVertexDto.setLatitude(averageEndLatitude);
                            endVertexDto.setWeight(accumulatorSpace[theta][rhoIndex]);

                            Vertices startVertex = previousEndVertex != null ? previousEndVertex : verticesService.add(startVertexDto);//проверить
                            Vertices endVertex = verticesService.add(endVertexDto);
                            verticesList.add(endVertex);

                            EdgeDto edgeDto = new EdgeDto(new Edge());
                            edgeDto.setStartVertex(startVertex);
                            edgeDto.setFinishVertex(endVertex);

                            edgeService.add(edgeDto);

                            previousEndVertex = endVertex;  // обновляем предыдущую конечную вершину
                        }
                    }
                }
            }
        }
        return verticesList;
    }
}