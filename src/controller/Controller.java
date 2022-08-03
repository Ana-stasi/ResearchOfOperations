package controller;

import model.*;
import model.algorithm.AntColonyAlgorithm;
import model.algorithm.GeneticAlgorithm;
import model.algorithm.GreedyAlgorithm;
import model.service.InputDataService;
import model.service.PrepareDataService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import view.ChartsForm;
import view.MainForm;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static model.service.InputDataService.getMatrixFromFile;

public class Controller {
    private static MainForm mainForm = new MainForm();
    private static ChartsForm chartsForm = new ChartsForm();
    static JFrame mainFrame = new JFrame("Двокритеріальна задача комівояжера");
    static JFrame chartsFrame = new JFrame("Графіки");
    InputDataService inputDataService = new InputDataService();
    PrepareDataService prepareDataService = new PrepareDataService();

    String[] columnNames;
    String[][] distanceMatrixData;
    String[][] safetyMatrixData;
    int startCity;
    int iterations;
    int populationSize;
    int parentsAmount;
    float mutationChance;
    int antsAmount;
    float crowdLevel;
    float distanceGreedyLevel;
    float safetyGreedyLevel;
    float startPheromone;
    float evapormationPheromone;
    Route route1;
    Route route2;
    Route route3;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Controller::createAndShowGui);
    }

    private static void createAndShowGui() {
        mainFrame.setContentPane(mainForm.getMainPanel());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void manualInputListener() {
        try {
            int cities = mainForm.getCitiesAmount();
            distanceMatrixData = inputDataService.createEmptyMatrix(cities);
            safetyMatrixData = inputDataService.createEmptyMatrix(cities);
            columnNames = new String[cities];
            updateTables(distanceMatrixData, safetyMatrixData, columnNames);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Попередньо вкажіть кількість міст");
        }
    }

    public void randomGenerationListener() {

        try {
            int size = mainForm.getCitiesAmount();
            inputDataService.clearMatrix(distanceMatrixData);
            inputDataService.clearMatrix(safetyMatrixData);
            inputDataService.generateMatrix(distanceMatrixData, safetyMatrixData, size);
            updateTables(distanceMatrixData, safetyMatrixData, columnNames);
        } catch (NullPointerException | NumberFormatException exc) {
            JOptionPane.showMessageDialog(mainFrame, "Попередньо вкажіть та підтвердіть кількість міст");
        }
    }

    public void readFromFileListener() {
        try {
            distanceMatrixData = getMatrixFromFile(new File("Program/src/ant_distance_matrix.txt"));
            safetyMatrixData = getMatrixFromFile(new File("Program/src/ant_chance_matrix.txt"));
            columnNames = new String[distanceMatrixData.length];
            updateTables(distanceMatrixData, safetyMatrixData, columnNames);
        } catch (FileNotFoundException fileNotFoundException) {
            JOptionPane.showMessageDialog(mainFrame, "Файл матриці відстаней не було знайдено");
        }
    }

    private void updateTables(String[][] distanceMatrixData, String[][] safetyMatrixData, String[] columnNames) {
        TableModel distanceMatrixModel = new DefaultTableModel(prepareTableView(getMatrixCopy(distanceMatrixData)), columnNames);
        mainForm.getDistanceMatrix().setModel(distanceMatrixModel);

        TableModel safetyMatrixModel = new DefaultTableModel(prepareTableView(getMatrixCopy(safetyMatrixData)), columnNames);
        mainForm.getSafetyMatrix().setModel(safetyMatrixModel);
    }

    public void calculateListener() {
        try {
            List<Float[][]> matrixData = prepareDataService.FloydWarshall(distanceMatrixData, safetyMatrixData);
            Float[][] distanceMatrix = matrixData.get(0);
            Float[][] safetyMatrix = matrixData.get(1);
            getParameters();
            List<Route> routes = new ArrayList<>();


            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            AntColonyAlgorithm antColonyAlgorithm = new AntColonyAlgorithm();

            GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();

            route1 = greedyAlgorithm.findSolution(distanceMatrix, safetyMatrix, startCity);
            routes.add(route1);
            route2 = geneticAlgorithm.findSolution(distanceMatrix, safetyMatrix, iterations, startCity, populationSize,
                    parentsAmount, mutationChance);
            routes.add(route2);
            route3 = antColonyAlgorithm.findSolution(distanceMatrix, safetyMatrix, iterations, startCity, antsAmount,
                    crowdLevel, distanceGreedyLevel, safetyGreedyLevel, startPheromone, evapormationPheromone);
            routes.add(route3);

            String[] columns = new String[4];
            columns[0] = "Алгоритм";
            columns[1] = "Маршрут" ;
            columns[2] = "Довжина маршруту, км";
            columns[3] = "Надійність, %";
            String [][] data = new String[3][4];
            data[0][0] = "Жадібний ";
            data[1][0] = "Генетичний ";
            data[2][0] = "Мурашиний ";

            for (int i = 0; i < data.length; i++) {
               data[i][1] = routes.get(i).getCityRoute().toString();
               data[i][2] = String.valueOf(routes.get(i).getLength());
               data[i][3] = String.format("%.26f",routes.get(i).getSafety()*100.0);
            }
            TableModel resultModel = new DefaultTableModel(data, columns);
            mainForm.getResultTable().setModel(resultModel);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, "Спочатку оберіть вхідні дані");
        } catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog(mainFrame, "Усі поля з параметрами методів мають бути заповнені коректно");
        }
    }

    public void showChartsListener(){
        try {
            getParameters();
            int taskSizeFrom = mainForm.getFromValue();
            int taskSizeTo = mainForm.getToValue();
            int step = mainForm.getStepValue();
            int tasksAmount = mainForm.getTasksAmount();

            XYSeries greedyAlgorithmTimeSeries = new XYSeries("Жадібний алгоритм");
            XYSeries geneticAlgorithmTimeSeries = new XYSeries("Генетичний алгоритм");
            XYSeries antAlgorithmTimeSeries = new XYSeries("Мурашиний алгоритм");

            XYSeries geneticAlgorithmAccuracySeries = new XYSeries("Генетичний алгоритм");
            XYSeries antAlgorithmAccuracySeries = new XYSeries("Мурашиний алгоритм");

            for (int i = taskSizeFrom; i <= taskSizeTo; i += step) {
                double[] time = new double[3];
                Arrays.fill(time, 0);
                double[] relativeDeviation = new double[2];
                Arrays.fill(relativeDeviation, 0);
                for (int j = 0; j < tasksAmount; j++) {
                    List<Float[][]> matrixData = generateTask(i);
                    Float[][] distanceMrx = matrixData.get(0);
                    Float[][] safetyMrx = matrixData.get(1);

                    GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();

                    double greedyStart = System.currentTimeMillis();
                    Route route1 = greedyAlgorithm.findSolution(distanceMrx, safetyMrx, startCity);
                    //time[0] += System.currentTimeMillis() - greedyStart;
                    time[0] += greedyAlgorithm.getActionsAmount()*0.05;
                    double z1 = route1.getSafety()/route1.getLength();

                    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

                    double geneticStart = System.currentTimeMillis();
                    Route route2 = geneticAlgorithm.findSolution(distanceMrx, safetyMrx, iterations, startCity, populationSize, parentsAmount, mutationChance);
                    //time[1] += System.currentTimeMillis() - geneticStart;
                    time[1] += geneticAlgorithm.getActionsAmount()*0.05;
                    double z2 = route2.getSafety()/route2.getLength();


                    AntColonyAlgorithm antColonyAlgorithm = new AntColonyAlgorithm();

                    double antStart = System.currentTimeMillis();
                    Route route3 = antColonyAlgorithm.findSolution(distanceMrx, safetyMrx, iterations, startCity, antsAmount, crowdLevel, distanceGreedyLevel, safetyGreedyLevel, startPheromone, evapormationPheromone);
                    //time[2]  += System.currentTimeMillis() - antStart;
                    time[2]  += antColonyAlgorithm.getActionsAmount()*0.0005;
                    double z3 = route3.getSafety()/route3.getLength();

                    relativeDeviation[0] += ((z2 - z1)/z1)*100.0;
                    relativeDeviation[1] += ((z3 - z1)/z1)*100.0;

                }
                greedyAlgorithmTimeSeries.add(i,(time[0]/tasksAmount));
                geneticAlgorithmTimeSeries.add(i,(time[1]/tasksAmount));
                antAlgorithmTimeSeries.add(i,(time[2]/tasksAmount));

                geneticAlgorithmAccuracySeries.add(i,(relativeDeviation[0]/tasksAmount));
                antAlgorithmAccuracySeries.add(i,(relativeDeviation[1]/tasksAmount));
            }
            XYSeriesCollection datasetTime = new XYSeriesCollection();
            datasetTime.addSeries(greedyAlgorithmTimeSeries);
            datasetTime.addSeries(geneticAlgorithmTimeSeries);
            datasetTime.addSeries(antAlgorithmTimeSeries);

            XYSeriesCollection datasetAccuracy = new XYSeriesCollection();
            datasetAccuracy.addSeries(geneticAlgorithmAccuracySeries);
            datasetAccuracy.addSeries(antAlgorithmAccuracySeries);

            showCharts(datasetTime,datasetAccuracy);

        }catch (NumberFormatException e ){
            JOptionPane.showMessageDialog(mainFrame, "Усі поля з параметрами мають бути заповнені коректно");
        }
    }

    public void showCharts(XYSeriesCollection datasetTime, XYSeriesCollection datasetAccuracy){
        chartsFrame.setContentPane(chartsForm.getMainPanel());
        chartsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartsFrame.pack();
        chartsFrame.setVisible(true);
        mainFrame.setVisible(false);

        chartsForm.getFirstChartPanel().setLayout(new GridLayout());
        chartsForm.getFirstChartPanel().add(initializeChart("Вплив розмірності задачі на час роботи алгоритмів",
                 "Розмірність","Час роботи",datasetTime ));
        chartsForm.getSecondChartPanel().setLayout(new GridLayout());
        chartsForm.getSecondChartPanel().add(initializeChart("Вплив розмірності задачі на точність алгоритмів",
                 "Розмірність","Відхилення від розв'язків жадібного алгоритму, %",datasetAccuracy));
    }
    private List<Float[][]> generateTask(int size) {
        String[][] distanceMrx = new String[size][size];
        String[][] safetyMrx = new String[size][size];
        InputDataService inputDataService = new InputDataService();
        inputDataService.clearMatrix(distanceMrx);
        inputDataService.clearMatrix(safetyMrx);
        inputDataService.generateMatrix(distanceMrx, safetyMrx, size);
        return new PrepareDataService().FloydWarshall(distanceMrx, safetyMrx);
    }

    public ChartPanel initializeChart(String title, String xAxisLabel, String yAxisLabel, XYSeriesCollection dataset){
        JFreeChart chart = ChartFactory.createXYLineChart(title,xAxisLabel, yAxisLabel,dataset,
                PlotOrientation.VERTICAL, true, true, true);
        ChartPanel chartpanel = new ChartPanel(chart);
        chartpanel.setDomainZoomable(true);
        return chartpanel;
    }

    private void getParameters() throws NumberFormatException {
        startCity = mainForm.getStartCity();
        iterations = mainForm.getIterations();

        populationSize = mainForm.getPopulationSize();
        parentsAmount = mainForm.getParentsAmount();
        mutationChance = mainForm.getMutationChance();

        antsAmount = mainForm.getAntsAmount();
        crowdLevel = mainForm.getCrowdLevel();
        distanceGreedyLevel = mainForm.getDistanceGreedyLevel();
        safetyGreedyLevel = mainForm.getSafetyGreedyLevel();
        startPheromone = mainForm.getStartPheromoneAmount();
        evapormationPheromone = mainForm.getEvapormationPheromoneLevel();
    }
    private String[][] prepareTableView(String[][] matrix) {
        if (matrix != null) {
            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix.length; j++)
                    if (matrix[i][j].equals("9999"))
                        matrix[i][j] = "∞";
        }
        return matrix;
    }

    private String[][] getMatrixCopy(String[][] matrix) {
        String[][] matrixCopy = new String[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++)
            System.arraycopy(matrix[i], 0, matrixCopy[i], 0, matrix.length);
        return matrixCopy;
    }


    public void backButtonListener(){
        mainFrame.setVisible(true);
        chartsForm.getFirstChartPanel().removeAll();
        chartsForm.getSecondChartPanel().removeAll();
        chartsFrame.setVisible(false);
    }

    public void saveDataToFileListener(){
        try {
            inputDataService.writeResultsToFile(new File("Program/src/results.txt"), route1, route2, route3);
        } catch (FileNotFoundException fileNotFoundException) {
            JOptionPane.showMessageDialog(mainFrame, "Файл результатів не було знайдено");
        }
    }

}
