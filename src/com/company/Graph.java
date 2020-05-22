package com.company;

import java.io.*;
import java.util.*;


public class Graph {
    int numberVertices;
    int[][] edgesValue;
    int[][] pathToDot;
    List<String> listToPrintDot = new LinkedList<>();
    int infinity = 99999;
    Graph(int numberCities){
        numberVertices = numberCities;
        edgesValue = new int[numberVertices][numberVertices];
        for (int i = 0; i < numberVertices; i++){
            for (int j = 0; j < numberVertices; j++){
                if(i != j){
                    edgesValue[i][j] = infinity;
                }else{
                    edgesValue[i][j] = 0;
                }

            }
        }
   }
    public void printTabOfEdges(){
        for (int i = 0; i < numberVertices; i++){
            for (int j = 0; j < numberVertices; j++){
                System.out.print(edgesValue[i][j] + " ");
            }
            System.out.println();
        }
    }
    static class Point{
        float x, y;
        Point(float x, float y){
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
    void floydWarshall(Map<String,Integer> cities) {
        int[][] returned = new int[numberVertices][numberVertices];
        int [][] path = new int[numberVertices][numberVertices];

        int i, j, k;

        for (i = 0; i < numberVertices; i++)
            for (j = 0; j < numberVertices; j++){
                returned[i][j] = edgesValue[i][j];
                if(i == j){
                    path[i][j] = 0;
                }else if(returned[i][j] != infinity){
                    path[i][j] = i;
                }else{
                    path[i][j] = -1;
                }
            }


        for (k = 0; k < numberVertices; k++) {
            for (i = 0; i < numberVertices; i++) {
                for (j = 0; j < numberVertices; j++) {
                    if(returned[i][k] != infinity && returned[k][j] != infinity && (returned[i][k] + returned[k][j] < returned[i][j]) ){
                        returned[i][j] = returned[i][k] + returned[k][j];
                        path[i][j] = path[k][j];
                    }
                }
                if(returned[i][i] < 0){
                    System.out.println("Negative weight cicle found!!!");
                    return;
                }
            }
        }

        printMatrix(returned);
        System.out.println();
        System.out.println();
        pathToDot = path;
        printPath(path,cities);
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
        if (value.equals(entry.getValue())) {
            return entry.getKey();
        }
    }
    return null;
}
    public void pathFunction(int[][] path, int i, int j, Map<String,Integer> cities){
        if(path[i][j] == i)
            return;
        pathFunction(path,i,path[i][j],cities);
        System.out.print(getKey(cities,path[i][j]) + ", ");
    }
    public void printPath(int[][] path,Map<String,Integer> cities){
        for (int i = 0; i < numberVertices; i++){
            for (int j = 0; j < numberVertices; j++){
                if( i != j && path[i][j] != -1){
                    System.out.print("Shortest Path from  " + getKey(cities,i) + " to  " + getKey(cities,j) + " is (" + getKey(cities,i) + " ");
                    pathFunction(path, i, j,cities);
                    System.out.println(getKey(cities,j) + ")");

                }
            }
        }
    }
    public void pathDot(int i , int j, Map<String,Integer> cities){
        if(pathToDot[i][j] == i)
            return;
        pathDot(i,pathToDot[i][j],cities);
        listToPrintDot.add(getKey(cities,pathToDot[i][j]));
    }

    public void displaySpecialRoad(String city, String city2, Map<String, Integer> mapCities, Map<String, Point> citiesGeographyPoints) {
        int indexFirstCity = mapCities.get(city), indexSecondCity = mapCities.get(city2);
        try{
            FileWriter fileWriter = new FileWriter("drawRoad.dot");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("graph G {");

                    if( indexFirstCity != indexSecondCity && pathToDot[indexFirstCity][indexSecondCity] != -1){
                        printWriter.println(getKey(mapCities,indexFirstCity) + " [pos=\"" + citiesGeographyPoints.get(getKey(mapCities,indexFirstCity)).getX() +
                                "," + citiesGeographyPoints.get(getKey(mapCities,indexFirstCity)).getY() +"!\"];");
                    }

            pathDot(indexFirstCity,indexSecondCity,mapCities);
                    if(listToPrintDot.size() > 0){
                        printWriter.println(getKey(mapCities,indexFirstCity) + "--" + listToPrintDot.get(0) +";");

                        int sizeCityToDraw = listToPrintDot.size(), secondElement = 1;
                        for (int i = 0; i < sizeCityToDraw ; i++){
                            printWriter.println(getKey(mapCities,mapCities.get(listToPrintDot.get(i))) + "[pos=\"" +
                                    citiesGeographyPoints.get(getKey(mapCities,mapCities.get(listToPrintDot.get(i)))).getX() +
                                    "," + citiesGeographyPoints.get(getKey(mapCities,mapCities.get(listToPrintDot.get(i)))).getY() +"!\"];");
                            if(secondElement < sizeCityToDraw){
                                printWriter.println(getKey(mapCities,mapCities.get(listToPrintDot.get(i))) + "--" + getKey(mapCities,mapCities.get(listToPrintDot.get(secondElement))) +";");
                                secondElement++;
                            }
                        }
                        printWriter.println(getKey(mapCities,mapCities.get(listToPrintDot.get(sizeCityToDraw-1))) + "--" + getKey(mapCities,indexSecondCity) + ";");
                    }else{
                        printWriter.println(getKey(mapCities,indexFirstCity) + "--" + getKey(mapCities,indexSecondCity) + ";");
                    }

            if( indexFirstCity != indexSecondCity && pathToDot[indexFirstCity][indexSecondCity] != -1){
                printWriter.println(getKey(mapCities,indexSecondCity) + " [pos=\"" + citiesGeographyPoints.get(getKey(mapCities,indexSecondCity)).getX() +
                        "," + citiesGeographyPoints.get(getKey(mapCities,indexSecondCity)).getY() +"!\"];");
            }
            printWriter.println("}");

            printWriter.close();
        }catch(IOException e){
            System.out.println("Error with: " + e.getMessage());
        }
    }
    public void printMatrix(int[][] A) {
        for (int i = 0; i < numberVertices; ++i) {
            for (int j = 0; j < numberVertices; ++j) {
                if (A[i][j] == infinity)
                    System.out.print("INF ");
                else
                    System.out.print(A[i][j] + "  ");
            }
            System.out.println();
        }
    }
    public void addEdge(int indexOfFirstVertex, int indexOfSecondVertex, int value){

            edgesValue[indexOfFirstVertex][indexOfSecondVertex] = value;
            edgesValue[indexOfSecondVertex][indexOfFirstVertex] = value;

    }
    public static void main(String[] args) {
        Map<String, Integer> mapCities = new HashMap<>();
        String line;
        String[] splited;
        try{
            BufferedReader br = new BufferedReader(new FileReader("czasy.txt"));

            int id = 0;
            while ((line = br.readLine()) != null){
                splited = line.split(" ");
                if(!mapCities.containsKey(splited[0])){
                    mapCities.put(splited[0],id);
                    id++;
                }
                if(!mapCities.containsKey(splited[1])){
                    mapCities.put(splited[1],id);
                    id++;
                }
            }
            br.close();
        }catch(IOException e){
            System.out.println("Error with: " + e.getMessage());
        }
        Graph graph = new Graph(mapCities.size());
        try{
            BufferedReader br = new BufferedReader(new FileReader("czasy.txt"));
            while ((line = br.readLine()) != null){
                splited = line.split(" ");
                graph.addEdge(mapCities.get(splited[0]),mapCities.get(splited[1]),Integer.parseInt(splited[2]));
            }
            br.close();
        }catch(IOException e){
            System.out.println("Error with: " + e.getMessage());
        }
        System.out.println(mapCities);
        graph.printTabOfEdges();
        System.out.println();
        graph.floydWarshall(mapCities);
        System.out.println();
        Map<String,Point> citiesGeographyPoints = new HashMap<>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader("positions.txt"));
            while((line = bufferedReader.readLine()) != null){
                splited = line.split(" ");
                Point tempPoint = new Point(Float.parseFloat(splited[1]),Float.parseFloat(splited[2]));
                citiesGeographyPoints.put(splited[0],tempPoint);
            }
        }catch (IOException | NumberFormatException e) {
            System.out.println("Error with: " + e.getMessage());
        }

        graph.displaySpecialRoad("Grudziądz","Przemyśl",mapCities,citiesGeographyPoints);
    }
}
