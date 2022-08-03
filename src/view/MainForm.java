package view;

import controller.Controller;

import javax.swing.*;

public class MainForm {
    private JPanel mainPanel;
    private JButton generateButton;
    private JButton readFromFileButton;
    private JLabel inputDataLabel;
    private JTextField citiesAmountField;
    private JButton applyCitiesAmount;
    private JLabel citiesAmountLabel;
    private JTable distanceMatrix;
    private JTable safetyMatrix;
    private JLabel parametersLabel;
    private JTextField iterationsAmountField;
    private JTextField startCityField;
    private JLabel iterationsAmountLabel;
    private JLabel startCityLabel;
    private JLabel genethicAlgorytmLabel;
    private JTextField mutationChanceField;
    private JTextField populationSizeField;
    private JTextField parentsAmountField;
    private JLabel mutationChanceLabel;
    private JLabel populationSizeLabel;
    private JLabel parentsAmountLabel;
    private JLabel antColonyAlgorithmLabel;
    private JTextField antsAmountField;
    private JTextField crowdLevelField;
    private JTextField distanceGreedyLevelField;
    private JTextField evapormationPheromoneLevelField;
    private JLabel antsAmountLabel;
    private JLabel crowdLevelLabel;
    private JLabel distanceGreedyLevelLabel;
    private JLabel evapormationPheromoneLevelLabel;
    private JButton calculateButton;
    private JPanel inputDataPanel;
    private JTextField safetyGreedyLevelField;
    private JLabel safetyGreedyLevelLabel;
    private JTextField startingPheromoneAmountField;
    private JLabel startingPheromoneAmountLabel;
    private JPanel parameterPanel;
    private JPanel experimentsPanel;
    private JPanel resultsPanel;
    private JTextField tasksAmountTextField;
    private JTextField toTextField;
    private JTextField fromTextField;
    private JTextField stepTextField;
    private JLabel taskSizeLabel;
    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel stepLabel;
    private JLabel tasksAmountLabel;
    private JButton chartsButton;
    private JLabel resultsLabel;
    private JButton saveToFileButton;
    private JTable resultTable;
    private JLabel experimentLabel;
    private JPanel fieldPanel;
    private JScrollPane scroll_table;
    static JFrame frame = new JFrame("Двокритеріальна задача комівояжера");

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getInputDataPanel(){
        return inputDataPanel;
    }

    public JTable getDistanceMatrix() {
        return distanceMatrix;
    }

    public JTable getSafetyMatrix() {
        return safetyMatrix;
    }

    public MainForm() {
        Controller controller = new Controller();
        applyCitiesAmount.addActionListener(e -> controller.manualInputListener());
        generateButton.addActionListener(e -> controller.randomGenerationListener());
        readFromFileButton.addActionListener(e -> controller.readFromFileListener());
        calculateButton.addActionListener(e -> controller.calculateListener());
        chartsButton.addActionListener(e -> controller.showChartsListener());
        saveToFileButton.addActionListener(e -> controller.saveDataToFileListener());
    }

   public int getCitiesAmount() throws NumberFormatException{
       return  Integer.parseInt(citiesAmountField.getText());
   }

   public int getStartCity() throws NumberFormatException{
        return Integer.parseInt(startCityField.getText());
   }

   public int getIterations()throws NumberFormatException{
        return Integer.parseInt(iterationsAmountField.getText());
   }
   public int getPopulationSize()throws NumberFormatException{
        return Integer.parseInt(populationSizeField.getText());
   }
    public int getParentsAmount()throws NumberFormatException{
        return Integer.parseInt(parentsAmountField.getText());
    }

    public float getMutationChance()throws NumberFormatException{
        return Float.parseFloat(mutationChanceField.getText());
    }

    public int getAntsAmount()throws NumberFormatException{
        return Integer.parseInt(antsAmountField.getText());
    }

    public float getCrowdLevel()throws NumberFormatException{
        return Float.parseFloat(crowdLevelField.getText());
    }

    public float getDistanceGreedyLevel()throws NumberFormatException{
        return Float.parseFloat(distanceGreedyLevelField.getText());
    }

    public float getSafetyGreedyLevel()throws NumberFormatException{
        return Float.parseFloat(safetyGreedyLevelField.getText());
    }

    public float getStartPheromoneAmount()throws NumberFormatException{
        return Float.parseFloat(startingPheromoneAmountField.getText());
    }

    public float getEvapormationPheromoneLevel()throws NumberFormatException{
        return Float.parseFloat(evapormationPheromoneLevelField.getText());
    }

    public int getToValue() throws NumberFormatException{
        return Integer.parseInt(toTextField.getText());
    }

    public int getFromValue() throws NumberFormatException{
        return Integer.parseInt(fromTextField.getText());
    }

    public int getStepValue() throws NumberFormatException{
        return Integer.parseInt(stepTextField.getText());
    }

    public int getTasksAmount() throws NumberFormatException{
        return Integer.parseInt(tasksAmountTextField.getText());
    }

    public JTable getResultTable() {
        return resultTable;
    }



}
