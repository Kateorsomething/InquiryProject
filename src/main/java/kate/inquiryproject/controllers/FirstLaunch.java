package kate.inquiryproject.controllers;

import kate.inquiryproject.DatabaseHandler;
import kate.inquiryproject.forestPlot;
import kate.inquiryproject.main;

import javax.swing.*;


public class FirstLaunch {

    /**
     * create & show a blank chart
     */
    public void createNewChart() {
        DatabaseHandler databaseHandler = new DatabaseHandler();

        String stm = "DELETE FROM EXAMPLE";
        databaseHandler.getHandler().executeAction(stm);
        String stm2 = "INSERT INTO EXAMPLE VALUES ('pooled',0,0)";
        databaseHandler.getHandler().executeAction(stm2);

        forestPlot.getForestPlotInstance().refreshChart();
        EnterData.getEnterDataInstance().showUiWindow();
        main.getMainInstance().closeMainStage();

        databaseHandler.getHandler().closeConnection();
    }

    /**
     * load the previous chart
     */
    public void loadSavedChart() {
        DatabaseHandler databaseHandler = new DatabaseHandler();


        forestPlot.getForestPlotInstance().refreshChart();
        EnterData.getEnterDataInstance().showUiWindow();
        main.getMainInstance().closeMainStage();

        databaseHandler.getHandler().closeConnection();

    }

    /**
     * read & load a selected file with format
     * double column1 (success cases), double column2 (sample size)
     */
    public void uploadFile() {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        String stm = "DELETE FROM EXAMPLE";
        databaseHandler.getHandler().executeAction(stm);
        String stm2 = "INSERT INTO EXAMPLE VALUES ('pooled',0,0)";
        databaseHandler.getHandler().executeAction(stm2);

        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            databaseHandler.getHandler().parseFile(fileChooser.getSelectedFile().getAbsolutePath());
        }

        forestPlot.getForestPlotInstance().refreshChart();
        EnterData.getEnterDataInstance().showUiWindow();
        main.getMainInstance().closeMainStage();

        databaseHandler.getHandler().closeConnection();
    }
}
