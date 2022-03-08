package com.dlindbla.travellingsalesmanproblemdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Math.sqrt;

public class BruteForceAlgorithm{

    // Since this algorithm is known to be inefficient,
    // not much time was put in to making it either elegant or fast
    ArrayList<Point> optimalPath = new ArrayList<>();

    double shortest = Double.MAX_VALUE;
    double[][] distanceArray;


    public ArrayList<Point> findPath(Point[] a, double[][] b) {
        this.distanceArray = b;
        int[] indices = new int[a.length];
        ArrayList<Point> points = new ArrayList<>(Arrays.asList(a));
        ArrayList<Point> route = new ArrayList<Point>();
        bruteForce(route, points);

        return optimalPath;
    }

    public void bruteForce(ArrayList<Point> routeSoFarArrayList, ArrayList<Point> points){

        if(!points.isEmpty())
        {
            for(int i = 0; i<points.size(); i++)
            {
                Point justRemoved = (Point) points.remove(0);
                ArrayList<Point> newRoute = new ArrayList<>();
                newRoute.addAll(routeSoFarArrayList);
                newRoute.add(justRemoved);
                bruteForce(newRoute, points);
                points.add(justRemoved);//Item needs to be returned so that it doesn't mess up the higher order calls
            }
        }
        else
        {
            double totalDistance = calculateLength(routeSoFarArrayList);
            if(totalDistance < shortest){
                //system.out.println("new best tour found!");
                shortest = totalDistance;
                optimalPath = routeSoFarArrayList;
            }
        }
    }

    public double calculateLength(ArrayList<Point> points){
        double distance = 0;
        for(int i = 0; i < points.size()-1; i++){
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);
            double x1 = p1.getX();
            double x2 = p2.getX();
            double y1 = p1.getY();
            double y2 = p2.getY();
            distance += sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        }
        //Add the distance it takes to go back to the start point
        double x1 = points.get(0).getX();
        double x2 = points.get(points.size()-1).getX();
        double y1 = points.get(0).getY();
        double y2 = points.get(points.size()-1).getY();
        distance += sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        return distance;
    }
















}
