package kate.inquiryproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class main extends Application {
    public static main main;
    public Stage stage;


    @Override
    public void start(Stage stage) throws Exception {
        getMainInstance().stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("/kate/inquiryproject/firstLaunch.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static main getMainInstance() {
        if (main == null) {
            main = new main();
            return main;
        } else {
            return main;
        }
    }

    public void closeMainStage() {
        this.stage.close();
    }

    public static void main(String[] args) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        String stm = "CREATE TABLE EXAMPLE(" +
                "P DOUBLE," +
                "N DOUBLE)";
        databaseHandler.getHandler().executeAction(stm);

        launch();
        DatabaseHandler.getHandler().closeConnection();
    }


}