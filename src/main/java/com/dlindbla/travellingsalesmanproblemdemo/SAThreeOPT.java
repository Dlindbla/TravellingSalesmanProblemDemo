package com.dlindbla.travellingsalesmanproblemdemo;

import java.util.ArrayList;
import java.util.Arrays;

public class SAThreeOPT implements TSPable{


    /* Note to self, the distance matrix is needed so that we don't change the original point[], thereby
     *  invalidating the distanceMatrix at the same time
     */
    private Point[] points;
    private double[][] distanceMatrix;

    private int[] route;
    private double currentBestDistance = Double.MAX_VALUE; // Init best route to as large a number as possible

    // An arraylist containing all the used routes. Can be used later to render a graph showing the progress
    // of the algorithm
    private ArrayList<int[]> routeHistory = new ArrayList<>();

    public SAThreeOPT(Point[] points, double[][] distanceMatrix){
        this.points = points;
        this.distanceMatrix = distanceMatrix;
        this.route = new int[points.length];
        //generateSequentialIndices();
        generateGreedyIndices();
        System.out.println(Arrays.toString(route));
    }

    /**
     * Produces are route according to the order of the Points[]. Yields int[0,1,2...N]
     */
    public void generateSequentialIndices(){
        for(int i = 0; i < points.length; i++){
            route[i] = i;
        }
    }

    /**
     * Produces a route according to the greedy algorithm ( Nearest neighbour heuristic )
     */
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

    @Override
    public Point[] findPath(Point[] points) {
        boolean improving = true;
        while(improving){
            //System.out.println("looping");
            improving = false;
            for (int i = 0; i < route.length-1; i++) {
                for (int k = i+1; k < route.length; k++) {

                    int[] newRoute = twoOptSwap(i, k);

                    //System.out.println(Arrays.toString(route));

                    double newRouteDistance = calculateTotalDistance(newRoute);

                    if (newRouteDistance < currentBestDistance) {
                        currentBestDistance = newRouteDistance;
                        route = newRoute;
                        improving = true;
                    }
                }
            }
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

}
