package com.dlindbla.travellingsalesmanproblemdemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

import static java.lang.Math.*;

public class TSPCanvasController implements Initializable {


    private double width = 980;
    private double height = 980;
    private int pointsToGenerate = 10;

    private int distanceArrayWidth;
    private int distanceArrayHeight;

    private Point[] points;
    private double[][] distanceMatrix;

    private Random random;

    @FXML
    Canvas canvas;

    @FXML
    Button generateButton;

    @FXML
    Button runButton;
    @FXML
    Button twoOptButton;
    @FXML
    Button threeOptButton;
    @FXML
    Button saButton;
    @FXML
    Button bruteButton;




    public TSPCanvasController(){
        this.random = new Random();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSeed(69_420_1337);
        generateRandomPoints(pointsToGenerate);
        drawPoints();

    }

    public void setSeed(long seed){
        this.random.setSeed(seed);
    }

    @FXML
    public void solve(){

        /*
        //Bruteforce algorithm
        BruteForceAlgorithm bfa = new BruteForceAlgorithm();
        ArrayList<Point> solvedPath = bfa.findPath(points,distanceMatrix);
        generateLinesFromArrayList(solvedPath);
        */

        //2-OPT algorithm


        TSPable twoOPT = new TwoOPT(points,distanceMatrix);
        Point[] path = twoOPT.findPath(points);
        double twoOPTDistance = calculateDistancePointArray(path);
        ArrayList<Point> pathPoints = new ArrayList<>(Arrays.asList(path));
        //generateLinesFromArrayList(pathPoints);



        //3-OPT algorithm
        TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
        Point[] path2 = threeOPT.findPath(points);
        double threeOPTDistance = calculateDistancePointArray(path2);
        ArrayList<Point> pathPoints2 = new ArrayList<>(Arrays.asList(path));
        generateLinesFromArrayList(pathPoints);


        //Run both 2-, and 3-opt and print the performance



        //simulated annealing
        /*
        TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,300,0.000001);
        Point[] path3 = simulatedAnnealing.findPath(points);
        double simulatedAnnealingDistance = calculateDistancePointArray(path3);
        ArrayList<Point> pathPoints3 = new ArrayList<>(Arrays.asList(path3));
        generateLinesFromArrayList(pathPoints3);
        */

        //System.out.println("Distance of Simulated Annealing is : " + simulatedAnnealingDistance);
        System.out.println("Total distance for 2-OPT algorithm is : " + twoOPTDistance);
        System.out.println("Total distance for 3-OPT algorithm is : " + threeOPTDistance);

    }

    @FXML
    public void draw2opt(){
        TSPable twoOPT = new TwoOPT(points,distanceMatrix);
        Point[] path = twoOPT.findPath(points);
        double twoOPTDistance = calculateDistancePointArray(path);
        ArrayList<Point> pathPoints = new ArrayList<>(Arrays.asList(path));
        generateLinesFromArrayList(pathPoints);
    }
    @FXML
    public void draw3opt(){
        //3-OPT algorithm
        TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
        Point[] path = threeOPT.findPath(points);
        double threeOPTDistance = calculateDistancePointArray(path);
        ArrayList<Point> pathPoints = new ArrayList<>(Arrays.asList(path));
        generateLinesFromArrayList(pathPoints);

    }
    @FXML
    public void drawSA(){
        TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,300,0.000001);
        Point[] path3 = simulatedAnnealing.findPath(points);
        double simulatedAnnealingDistance = calculateDistancePointArray(path3);
        ArrayList<Point> pathPoints3 = new ArrayList<>(Arrays.asList(path3));
        generateLinesFromArrayList(pathPoints3);
    }

    @FXML
    public void drawBrute(){
        BruteForceAlgorithm bfa = new BruteForceAlgorithm();
        ArrayList<Point> solvedPath = bfa.findPath(points,distanceMatrix);
        generateLinesFromArrayList(solvedPath);
    }




    @FXML
    public void redrawPoints(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        generateRandomPoints(pointsToGenerate);
        drawPoints();

        int[] greedy = generateGreedyIndices();
        int[] seq = generateSequentialIndices();
        drawLines(seq);
    }

    public int[] generateSequentialIndices(){
        int[] indices = new int[points.length];
        for(int i = 0; i < points.length; i++){
            indices[i] = i;
        }
        return indices;
    }


    public int[] generateGreedyIndices(){
        int[] indices = new int[points.length];
        boolean[] visitedPlaces = new boolean[points.length];
        int visitedPoints = 1;
        //we start by visiting the first node
        indices[0] = 0;
        int currentIndex = 0;
        visitedPlaces[currentIndex] = true;
        while(visitedPoints < visitedPlaces.length){
            int nextIndex = -1;
            double currentShortestDistance = Double.MAX_VALUE;
            for(int i = 0; i < visitedPlaces.length; i++) {
                if(!visitedPlaces[i]){
                    double tempDistance = distanceMatrix[currentIndex][i];
                    if(tempDistance < currentShortestDistance){
                        currentShortestDistance = tempDistance;
                        nextIndex = i;
                    }
                }
            }
            visitedPlaces[nextIndex] = true;
            indices[visitedPoints] = nextIndex;
            currentIndex = nextIndex;
            visitedPoints++;

        }
        return indices;
    }




    //Function for generating a random amount of points at random locations and drawing them
    public void generateRandomPoints(int totalNumberOfPoints){
        points = new Point[totalNumberOfPoints];
        for(int i = 0 ; i < totalNumberOfPoints ; i++){
            float normalFloatX = random.nextFloat();
            float normalFloatY = random.nextFloat();
            double xCord = ( abs(normalFloatX) * width );
            double yCord = ( abs(normalFloatY) * height );
            //System.out.println(xCord);
            Point newPoint = new Point(xCord,yCord);
            points[i] = newPoint;
        }
        generateDistanceMatrix();
    }

    //Function for drawing points given as a set of X,Y cords
    public void drawPoints(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.FORESTGREEN);
        gc.setFill(Color.FORESTGREEN);
        for (Point point: points) {
            gc.fillOval(point.getX()-10, point.getY()-10, 20, 20);
        }
        Point firstPoint = points[0];
        gc.setFill(Color.RED);
        gc.fillOval(firstPoint.getX()-10,firstPoint.getY()-10,20,20);
        gc.setFill(Color.FORESTGREEN);
    }
    //Function for iterating through the points in a set order and drawing lines in that order (Passed by the sorter)
    public void drawLines(int[] indices){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for(int i = 0 ; i < indices.length-1; i++){
            Point point = points[ indices[i] ];
            Point nextPoint = points[ indices[i+1] ];
            double x1 = point.getX();
            double x2 = nextPoint.getX();
            double y1 = point.getY();
            double y2 = nextPoint.getY();
            gc.strokeLine(x1,y1,x2,y2);

            gc.setFill(Color.BLACK);
            gc.setFont(new Font("", 8));
            gc.fillText(String.valueOf(i), x1,y1);
            gc.setFill(Color.FORESTGREEN);
        }
        // Hard coded for now, draws line connecting fist and last item
        double x1 = points[indices[0]].getX();
        double x2 = points[indices[indices.length-1]].getX();
        double y1 = points[indices[0]].getY();
        double y2 = points[indices[indices.length-1]].getY();
        gc.strokeLine(x1,y1,x2,y2);
    }
    //Function for clearing everything

    public double calculateDistancePointArray(Point[] points){
        double distance = 0;
        for (int i = 0 ; i < points.length; i++){
            for(int j = 0 ; j < points.length; j++) {
                double x1 = points[i].getX();
                double x2 = points[j].getX();
                double y1 = points[i].getY();
                double y2 = points[j].getY();
                double dist = sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
                distance += dist;
            }
        }
        double x1 = points[0].getX();
        double x2 = points[points.length-1].getX();
        double y1 = points[0].getY();
        double y2 = points[points.length-1].getY();
        double dist = sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        distance += dist;
        return distance;
    }



    /** Generates a distance matrix of N*N doubles from a given Point-Class array
     * @Parma pointArray
     */
    public void generateDistanceMatrix(){
        int n = points.length;
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0 ; i < points.length; i++){
            for(int j = 0 ; j < points.length; j++) {
                double x1 = points[i].getX();
                double x2 = points[j].getX();
                double y1 = points[i].getY();
                double y2 = points[j].getY();
                double distance = sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
                distanceMatrix[i][j] = distance;
            }
        }
        this.distanceMatrix = distanceMatrix;
    }




    public void generateLinesFromArrayList(ArrayList<Point> points){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPoints();
        for(int i = 0; i < points.size() - 1 ; i++){
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);
            double x1 = p1.getX();
            double x2 = p2.getX();
            double y1 = p1.getY();
            double y2 = p2.getY();
            gc.strokeLine(x1,y1,x2,y2);
        }
        //hard coded, for your pleasure
        gc.strokeLine(points.get(0).getX(), points.get(0).getY(),points.get(points.size()-1).getX(), points.get(points.size()-1).getY());
    }

}
