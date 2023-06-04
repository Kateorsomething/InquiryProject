package kate.inquiryproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kate.inquiryproject.DatabaseHandler;
import kate.inquiryproject.forestPlot;
import kate.inquiryproject.main;

import java.io.IOException;


public class EnterData {
    @FXML
    public TextField sampleP;
    @FXML
    public TextField sampleN;
    @FXML
    public Text alert;
    private static int dataPointIndex;
    public static EnterData enterData;
    private static Stage stage;

    public static EnterData getEnterDataInstance() {
        if (enterData == null) {
            enterData = new EnterData();
            return enterData;
        } else {
            return enterData;
        }
    }

    /**
     * enter data into database & refresh chart
     */
    public void enterData() {
        //check for invalid input
        double p = 0;
        int n = 0;
        try {
            p = Double.parseDouble(sampleP.getText());
            n = Integer.parseInt(sampleN.getText());
        } catch (NumberFormatException e) {
            alert.setStyle("-fx-opacity: 100;");
            sampleP.clear();
            sampleN.clear();
            return;
        }
        if (n == 0 | p>n) {
            alert.setStyle("-fx-opacity: 100;");
            sampleP.clear();
            sampleN.clear();
            return;
        }

        //process input
        DatabaseHandler databaseHandler = new DatabaseHandler();

        String stm = "INSERT INTO EXAMPLE VALUES (" +
                "'" + this.dataPointIndex + "'," +
                p + "," +
                n + ")";
        databaseHandler.getHandler().executeAction(stm);

        forestPlot.getForestPlotInstance().refreshChart();
        getEnterDataInstance().stage.requestFocus();
        alert.setStyle("-fx-opacity: 0;");
        sampleN.clear();
        sampleP.clear();
        this.dataPointIndex++;
    }

    /**
     * display ui window used to input data
     */
    public void showUiWindow() {
        try {
            Stage stage = new Stage();
            this.stage = stage;
            FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("/kate/inquiryproject/enterData.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
