package com.dlindbla.travellingsalesmanproblemdemo;

import java.util.Arrays;
import java.util.Random;

public class SimulatedAnnealing implements TSPable{

    private Point[] points;
    private double[][] distanceMatrix;
    private int size;

    private double coolingRate;
    private double startingTemperature;

    private int[] route;
    private double currentDistance = Double.MAX_VALUE;
    private double bestDistance = Double.MAX_VALUE;


    public SimulatedAnnealing(Point[] points, double[][] distanceMatrix, double startingTemperature, double coolingRate){
        this.points = points;
        this.size = points.length;
        this.distanceMatrix = distanceMatrix;
        this.coolingRate = coolingRate;
        this.startingTemperature = startingTemperature;
        generateSequentialIndices();
        currentDistance = calculateTotalDistance(route);
        bestDistance = currentDistance;
    }


    @Override
    public Point[] findPath(Point[] a) {
        double temperature = startingTemperature;
        Random random = new Random();
        int iterations = 0;


        while(temperature > 1){

            //generate two random points to use as two opt swaps
            int pos1 = random.nextInt(size);
            int pos2 = random.nextInt(size);

            //make sure they aren't the same value
            while(pos1==pos2){pos2 = random.nextInt(size);}

            if(pos1 > pos2){
                int temp = pos1;
                pos1 = pos2;
                pos2 = temp;
            }

            int[] newRoute = twoOptSwap(pos1,pos2);

            double newDistance = calculateTotalDistance(newRoute);

            if(newDistance < currentDistance){
                route = newRoute;
                currentDistance = newDistance;
            }
            // Stochastically decide whether to accept new route or not (if it is worse)
            else if(Math.exp((currentDistance - newDistance) / temperature) > Math.random()) {
                route = newRoute;
                currentDistance = newDistance;
                //System.out.println("new route chosen");
            }



            temperature *= (1-coolingRate);
            iterations++;
            //if(iterations % 100 == 0){System.out.println("Total iterations so far : " + iterations);}
        }

        Point[] returnPoints = new Point[points.length];
        for(int i = 0; i < points.length; i++){
            returnPoints[i] = points[route[i]];
        }
        return returnPoints;
    }

    @Override
    public double[] distanceHistory() {
        return new double[0];
    }

    public int[] twoOptSwap(int j, int k){
        //int[] newRoute = route;
        int[] newRoute = Arrays.copyOf(route, route.length);

        int d = (k-j+1)/2;
        for(int i = 0; i < d ; i++)
        {
            int temp = newRoute[j+i];
            newRoute[j+i] = newRoute[k-i];
            newRoute[k-i] = temp;
        }
        return newRoute;
    }

    public void generateGreedyIndices(){
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
        route = indices;
    }

    public void generateSequentialIndices(){
        int[] indices = new int[size];
        for(int i = 0; i < size; i++){
            indices[i] = i;
        }
        this.route = indices;
    }



    /**
     *
     * @param indices takes the indexes of the path through the graph (x1,x2...xn) and calculates the total distance
     * @return double representing the total euclidean distance
     */
    public double calculateTotalDistance(int[] indices){
        double distance = 0;
        for(int i = 0; i < indices.length - 1; i++){
            distance += distanceMatrix[ indices[ i ] ] [ indices[ i + 1 ] ];
        }
        distance += distanceMatrix[indices[0]][indices[indices.length-1]];
        return distance;
    }


}
