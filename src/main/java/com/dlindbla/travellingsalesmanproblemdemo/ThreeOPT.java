package com.dlindbla.travellingsalesmanproblemdemo;

import java.util.ArrayList;

public class ThreeOPT implements TSPable{

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

    public ThreeOPT(Point[] points, double[][] distanceMatrix){
        this.points = points;
        this.distanceMatrix = distanceMatrix;
        this.route = new int[points.length];
        generateGreedyIndices();
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
        for(int i = 0; i < points.length; i++){
            route[i] = i;
        }
    }

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
            improving = false;

            for(int i = 0 ; i < points.length-2; i++){
                for(int j = i+1 ; j < points.length-1; j++){
                    for(int k = j+1 ; k < points.length; k++){

                        /*
                        *       8 cases for 3-OPT exist
                        *       0 - nothing changes
                        *   1,2,3 - Single 2-OPT swap, equivalent to 2-OPT
                        *   4,5,6 - Double 2-OPT swap
                        *       7 - Triple 2-OPT swap
                        * */

                        // 1,2,3
                        int[] case1 = swap(route,i,j);
                        int[] case2 = swap(route,j,k);
                        int[] case3 = swap(route,i,k);
                        // 4,5,6
                        // a' b' c
                        // a' b  c'
                        // a  b' c'
                        int[] case4 = swap(route,i,j);
                        case4 = swap(case4,j,k);

                        int[] case5 = swap(route,i,k);
                        case5 = swap(case5,i,j);

                        int[] case6 = swap(route,i,k);
                        case6 = swap(case6,j,k);

                        // 7
                        // a'b'c'
                        int[] case7 = swap(route,i,k);
                        case7 = swap(case7,i,j);
                        case7 = swap(case7,j,k);

                        ArrayList<int[]> cases = new ArrayList<>();
                        ArrayList<Double> distances = new ArrayList<>();

                        cases.add(case1);
                        cases.add(case2);
                        cases.add(case3);
                        cases.add(case4);
                        cases.add(case5);
                        cases.add(case6);
                        cases.add(case7);

                        distances.add(calculateTotalDistance(case1));
                        distances.add(calculateTotalDistance(case2));
                        distances.add(calculateTotalDistance(case3));
                        distances.add(calculateTotalDistance(case4));
                        distances.add(calculateTotalDistance(case5));
                        distances.add(calculateTotalDistance(case6));
                        distances.add(calculateTotalDistance(case7));

                        for(int a = 0; a < distances.size(); a++){
                            if(distances.get(a) < currentBestDistance){
                                currentBestDistance = distances.get(a);
                                route = cases.get(a);
                                improving = true;
                            }
                        }
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
    public Double[] distanceHistory() {
        return new Double[0];
    }

    public int[] swap(int[] route, int j, int k){
        int[] newRoute = new int[route.length];
        for(int i = 0 ; i < route.length; i++){newRoute[i] = route[i];}

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
