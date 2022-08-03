package model.service;

import model.Route;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class InputDataService {

    private static String INFINITY = "9999";

    public void clearMatrix(String[][] matrix){
        int size = matrix.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = "0";
            }
        }
    }

    public String[][] createEmptyMatrix(int citiesAmount) {
        String[][] matrixData = new String[citiesAmount][citiesAmount];
        for (int i = 0; i < citiesAmount; i++) {
            for (int j = 0; j < citiesAmount; j++) {
                matrixData[i][j] = "0";
            }
        }
        return matrixData;
    }

    public static String[][] getMatrixFromFile(File inputFile) throws FileNotFoundException {
            Scanner sizeScanner = new Scanner(inputFile);
            String[] temp = sizeScanner.nextLine().split(" ");
            sizeScanner.close();
            int inputMatrixSize = temp.length;

            Scanner scanner = new Scanner(inputFile);
            String[][] inputMatrix = new String[inputMatrixSize][inputMatrixSize];
            for (int i = 0; i < inputMatrixSize; i++) {
                String[] numbers = scanner.nextLine().split(" ");
                for (int j = 0; j < inputMatrixSize; j++) {
                    inputMatrix[i][j] = numbers[j];
                }
            }
            scanner.close();
            return inputMatrix;
    }

    public void generateMatrix(String[][] distanceData,String [][] safetyData,int size){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(i!=j){
                    if (new Random().nextInt(2) == 1) {
                        fillField(distanceData, i, j, 100);
                        fillField(safetyData, i, j, 1);
                    }
                    else if (distanceData[i][j].equals("0") && distanceData[j][i].equals("0")){
                        safetyData[i][j] = safetyData[j][i] = INFINITY;
                        distanceData[i][j] = distanceData[j][i]= INFINITY;
                    }
                }
                else {
                    safetyData[i][j] = INFINITY;
                    distanceData[i][j]= INFINITY;
                }
            }
        }
    }

    private void fillField(String [][] matrix, int i, int j, int bound) {
        double randomNumber = 0.01 + Math.random() * (bound);
        matrix[i][j] = String.valueOf(randomNumber);
        matrix[j][i] = String.valueOf(randomNumber);
    }

    public void writeResultsToFile(File resultFile, Route route1, Route route2, Route route3)
            throws FileNotFoundException{
        PrintWriter out = new PrintWriter(resultFile);

        out.println("Жадібний алгоритм: маршрут - " + route1.getCityRoute() + ", довжина маршруту -  "
                + route1.getLength() + " (км), надійність маршруту - " + String.format("%.26f",route1.getSafety()*100.0)+"%");
        out.println("Генетичний алгоритм: маршрут - " + route2.getCityRoute() + ", довжина маршруту -  "
                + route2.getLength() + " (км), надійність маршруту - " + String.format("%.26f",route2.getSafety()*100.0) +"%");
        out.println("Мурашиний алгоритм: маршрут - " + route3.getCityRoute() + ", довжина маршруту -  "
                + route3.getLength() + " (км), надійність маршруту - " + String.format("%.26f",route3.getSafety()*100.0)+"%");

        out.close();
    }

}


