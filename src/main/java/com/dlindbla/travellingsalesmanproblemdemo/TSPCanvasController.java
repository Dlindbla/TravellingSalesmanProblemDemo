package com.dlindbla.travellingsalesmanproblemdemo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.*;

public class TSPCanvasController implements Initializable {


    private double width = 1000;
    private double height = 1000;
    private int pointsToGenerate = 50;

    private int distanceArrayWidth;
    private int distanceArrayHeight;

    private Point[] points;
    private double[][] distanceMatrix;

    private Random random;
    private double simulatedAnnealingCoolingFactor = 0.001;



    @FXML
    Canvas canvas;

    @FXML
    TextField nodeCountField;

    @FXML
    Button generateButton;
    @FXML
    Button timeStatsButton;

    @FXML
    Button runButton;
    @FXML
    Button twoOptButton;
    @FXML
    Button threeOptButton;
    @FXML
    Button saButton;
    @FXML
    Button acButton;
    @FXML
    Button bruteButton;
    @FXML
    Button greedyButton;
    @FXML
    Button statsButton;




    public TSPCanvasController(){
        this.random = new Random();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSeed(69_420_1337);
        generateRandomPoints(pointsToGenerate);
        drawPoints();

    }

    public void configureNodes(){
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        nodeCountField.setTextFormatter(new TextFormatter<String>(integerFilter));
    }


    public void setSeed(long seed){
        this.random.setSeed(seed);
    }

    @FXML
    public void solve(){
        //2-OPT algorithm
        TSPable twoOPT = new TwoOPT(points,distanceMatrix);
        Point[] path = twoOPT.findPath(points);
        double twoOPTDistance = calculateDistancePointArray(path);

        //3-OPT algorithm
        TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
        Point[] path2 = threeOPT.findPath(points);
        double threeOPTDistance = calculateDistancePointArray(path2);

        //simulated annealing
        double currentDistance = calculateDistancePointArray(points) ;
        TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,currentDistance,pointsToGenerate,simulatedAnnealingCoolingFactor);
        Point[] path3 = simulatedAnnealing.findPath(points);
        double simulatedAnnealingDistance = calculateDistancePointArray(path3);
        ArrayList<Point> pathPoints3 = new ArrayList<>(Arrays.asList(path3));
        generateLinesFromArrayList(pathPoints3);

        System.out.println("Total distance for 2-OPT algorithm is    : " + twoOPTDistance);
        System.out.println("Total distance for 3-OPT algorithm is    : " + threeOPTDistance);
        System.out.println("Total distance of Simulated Annealing is : " + simulatedAnnealingDistance);
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
        //use the current distance of the generated path to use as a temperature for simulated annealing
        double currentDistance = calculateDistancePointArray(points);
        TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,currentDistance,pointsToGenerate,simulatedAnnealingCoolingFactor);
        Point[] path3 = simulatedAnnealing.findPath(points);
        double simulatedAnnealingDistance = calculateDistancePointArray(path3);
        System.out.println("Total distance of Simulated Annealing is : " + simulatedAnnealingDistance);
        ArrayList<Point> pathPoints3 = new ArrayList<>(Arrays.asList(path3));
        generateLinesFromArrayList(pathPoints3);

        /***
         * This part of the function will generate a CSV file of selected tour lengths
         * which can be used to graph the history of the tours:
         * It is implemented in the worst way possible for extra flavour
         */
        Double[] distanceHistory = simulatedAnnealing.distanceHistory();
        System.out.println("Total size of SA history array : " + distanceHistory.length);
        try {
            toCSV("Distance_History_SimulatedAnnealing.csv",distanceHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This is not a good function by any means and does not do what it is supposed to
    public <T> void toCSV(String fileName,T[] inputArray) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
        StringBuilder sb = new StringBuilder();
        for (T element : inputArray) {
            sb.append(element);
            sb.append("\n");
        }

        br.write(sb.toString());
        br.close();
    }

    @FXML
    public void drawAC(){
        TSPable antColony = new AntColonyOptimization(distanceMatrix);
        Point[] path = antColony.findPath(points);
        double antColonyDistance = calculateDistancePointArray(path);
        ArrayList<Point> pathPoints = new ArrayList<>(Arrays.asList(path));
        System.out.println("Total distance of Any Colony Optimization is : " + antColonyDistance);
        generateLinesFromArrayList(pathPoints);
    }

    @FXML
    public void drawBrute(){
        BruteForceAlgorithm bfa = new BruteForceAlgorithm();
        ArrayList<Point> solvedPath = bfa.findPath(points,distanceMatrix);
        generateLinesFromArrayList(solvedPath);
    }

    @FXML
    public void drawGreedy(){
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
        Point[] returnPoints = new Point[points.length];
        for(int i = 0; i < points.length; i++){
            returnPoints[i] = points[indices[i]];
        }

        double greedyDistance = calculateDistancePointArray(returnPoints);
        ArrayList<Point> pathPoints = new ArrayList<>(Arrays.asList(returnPoints));
        System.out.println("Total distance of Greedy Algorithm is : " + greedyDistance);
        generateLinesFromArrayList(pathPoints);
    }




    @FXML
    public void redrawPoints(){

        if(nodeCountField.getText() != ""){
           Integer nodesCount = Integer.valueOf(nodeCountField.getText());
           pointsToGenerate = nodesCount;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        generateRandomPoints(pointsToGenerate);
        drawPoints();

        int[] greedy = generateGreedyIndices();
        int[] seq = generateSequentialIndices();
        //drawLines(seq);
    }

    public int[] generateSequentialIndices(){
        int[] indices = new int[points.length];
        for(int i = 0; i < points.length; i++){
            indices[i] = i;
        }
        return indices;
    }

    public Point[] indicesToPoints(int[] indices){
        Point[] returnPoints = new Point[indices.length];
        for(int i = 0;i < indices.length;i++){
            returnPoints[i] = points[indices[i]];
        }
        return  returnPoints;
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


    /***
     * This will be a bit of a bloated method ( not to mention hard-coded )
     */
    public void generateStats(){

        int totalRuns = 10;

        double greedyTotal = 0;
        double twoOPTTotal = 0;
        double threeOPTTotal = 0;
        double simulatedAnnealingTotal = 0;
        double antColonyTotal = 0;


        for ( int i = 0 ; i < totalRuns ; i++){
            generateRandomPoints(pointsToGenerate);
            TSPable twoOPT = new TwoOPT(points,distanceMatrix);
            TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
            TSPable antColony = new AntColonyOptimization(distanceMatrix);
            double temperature = calculateDistancePointArray(indicesToPoints(generateGreedyIndices()));
            TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,temperature,pointsToGenerate,simulatedAnnealingCoolingFactor);
            twoOPTTotal += calculateDistancePointArray(twoOPT.findPath(points));
            threeOPTTotal += calculateDistancePointArray(threeOPT.findPath(points));
            antColonyTotal += calculateDistancePointArray(antColony.findPath(points));
            greedyTotal += calculateDistancePointArray(indicesToPoints(generateGreedyIndices()));
            simulatedAnnealingTotal += calculateDistancePointArray(simulatedAnnealing.findPath(points));
        }

        double greedyAverage = greedyTotal/totalRuns;
        double twoOPTAverage = twoOPTTotal/totalRuns;
        double threeOPTAverage = threeOPTTotal/totalRuns;
        double simulatedAnnealingAverage = simulatedAnnealingTotal/totalRuns;
        double antColonyAverage = antColonyTotal/totalRuns;


        System.out.println("Average Distance of greedy algorithm on "+totalRuns+" runs is : " + greedyAverage);
        System.out.println("Average Distance of twoOPT algorithm on "+totalRuns+" runs is : " + twoOPTAverage);
        System.out.println("Average Distance of threeOPT algorithm on "+totalRuns+" runs is : " + threeOPTAverage);
        System.out.println("Average Distance of simulated annealing algorithm on "+totalRuns+" runs is : " + simulatedAnnealingAverage);
        //System.out.println("Average Distance of ant colony algorithm on "+totalRuns+" runs is : " + antColonyAverage);

    }

    public void generateTimeStats(){
        int maxSize = pointsToGenerate;
        Long[] twoOPTTimes = new Long[maxSize];
        Long[] threeOPTTimes = new Long[maxSize];
        Long[] simulatedAnnealingTimes = new Long[maxSize];
        Long[] bruteForceTimes = new Long[maxSize];

        for(int i = 1; i < maxSize; i++){
            generateRandomPoints(i);
            BruteForceAlgorithm bruteForce = new BruteForceAlgorithm();
            double temperature = calculateDistancePointArray(indicesToPoints(generateGreedyIndices()));
            TSPable twoOPT = new TwoOPT(points,distanceMatrix);
            TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
            TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,temperature,pointsToGenerate,simulatedAnnealingCoolingFactor);

            long startTime = System.nanoTime();
            twoOPT.findPath(points);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) ;
            twoOPTTimes[i] = (duration);

            startTime = System.nanoTime();
            threeOPT.findPath(points);
            endTime = System.nanoTime();
            duration = (endTime   - startTime) ;
            threeOPTTimes[i] = (duration);

            startTime = System.nanoTime();
            simulatedAnnealing.findPath(points);
            endTime = System.nanoTime();
            duration = (endTime - startTime) ;
            simulatedAnnealingTimes[i] = (duration);

            startTime = System.nanoTime();
            bruteForce.findPath(points,distanceMatrix);
            endTime = System.nanoTime();
            duration = (endTime - startTime) ;
            bruteForceTimes[i] = (duration);


            System.out.println("Currently running iteration number : " + i);
        }
        //write a CSV file for each of the times for graphing in python
        try {
            toCSV("twoOPTTimes.csv",twoOPTTimes);
            toCSV("threeOPTTimes.csv",threeOPTTimes);
            toCSV("simulatedAnnealingTimes.csv",simulatedAnnealingTimes);
            toCSV("bruteforcetimes.csv",bruteForceTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void generateAvgDistanceData(){

        int maxPoints = 300;
        int iterations = 10;

        Double[] twoOPTDistances = new Double[maxPoints];
        Double[] threeOPTDistances = new Double[maxPoints];
        Double[] simulatedAnnealingDistances = new Double[maxPoints];
        Double[] greedyDistances = new Double[maxPoints];
        for(int i = 1 ; i < maxPoints; i++){
            System.out.println("Currently running iteration : " + i);
            Double tempTwoOpt = 0.0;
            Double tempThreeOpt = 0.0;
            Double tempSA = 0.0;
            Double tempGreedy = 0.0;

            for(int j = 0; j < iterations; j++){
                generateRandomPoints(i);
                double temperature = calculateDistancePointArray(indicesToPoints(generateGreedyIndices()));
                TSPable twoOPT = new TwoOPT(points,distanceMatrix);
                TSPable threeOPT = new ThreeOPT(points,distanceMatrix);
                TSPable simulatedAnnealing = new SimulatedAnnealing(points,distanceMatrix,temperature,i,simulatedAnnealingCoolingFactor);

                tempTwoOpt += calculateDistancePointArray(twoOPT.findPath(points));
                tempThreeOpt += calculateDistancePointArray(threeOPT.findPath(points));
                tempSA += calculateDistancePointArray(simulatedAnnealing.findPath(points));
                tempGreedy += calculateDistancePointArray(indicesToPoints(generateGreedyIndices()));

            }

            tempTwoOpt = tempTwoOpt / iterations;
            tempThreeOpt = tempThreeOpt / iterations;
            tempSA = tempSA / iterations;
            tempGreedy = tempGreedy / iterations;

            twoOPTDistances[i] = tempTwoOpt;
            threeOPTDistances[i] = tempThreeOpt;
            simulatedAnnealingDistances[i] = tempSA;
            greedyDistances[i] = tempGreedy;
        }

        try {
            toCSV("twoOPTDistances.csv",twoOPTDistances);
            toCSV("threeOPTDistances.csv",threeOPTDistances);
            toCSV("simulatedAnnealingDistances.csv",simulatedAnnealingDistances);
            toCSV("greedyDistances.csv",greedyDistances);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public double calculateDistancePointArray(Point[] points){
        double distance = 0;
        for(int i = 0 ; i < points.length-1; i++){
            double x1 = points[i].getX();
            double x2 = points[i+1].getX();
            double y1 = points[i].getY();
            double y2 = points[i+1].getY();
            double dist = sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            distance += dist;
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
