package view;

import controller.Controller;

import javax.swing.*;

public class ChartsForm {
    private JPanel mainPanel;
    private JPanel firstChartPanel;
    private JPanel secondChartPanel;
    private JPanel chartsPanel;
    private JButton backButton;

    public ChartsForm(){
        Controller controller = new Controller();
        backButton.addActionListener(e -> controller.backButtonListener());
    }


    public JPanel getMainPanel() {return mainPanel;}

    public JPanel getFirstChartPanel() {return firstChartPanel;}

    public JPanel getSecondChartPanel() {return secondChartPanel;}

    public JPanel getChartsPanel() {return chartsPanel;}
}
