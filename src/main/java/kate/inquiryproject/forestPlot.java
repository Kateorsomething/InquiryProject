package kate.inquiryproject;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

public class forestPlot extends ApplicationFrame {
    public static forestPlot forestPlot;
    private static Stage chartStage;

    public forestPlot() {
        super("title");
        setContentPane(createJPanel( ));
    }

    public static forestPlot getForestPlotInstance() {
        if (forestPlot == null) {
            forestPlot = new forestPlot();
            return forestPlot;
        } else {
            return forestPlot;
        }
    }

    /**
     * create and show chart stage if none exists, else refresh
     */
    public void refreshChart() {
        if (chartStage == null) {
            getForestPlotInstance().chartStage = new Stage();
            refreshChart();
            return;
        }

        forestPlot.updatePooled();

        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode);
        Pane pane = new Pane();
        pane.getChildren().add(swingNode);
        Scene scene = new Scene(pane);
        getForestPlotInstance().chartStage.setScene(scene);
        getForestPlotInstance().chartStage.setResizable(false);
        getForestPlotInstance().chartStage.setWidth(1100);
        getForestPlotInstance().chartStage.setHeight(780);
        getForestPlotInstance().chartStage.show();

    }

        /**
         * copied from https://stackoverflow.com/a/29271379
         */
        private void createAndSetSwingContent(final SwingNode swingNode) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JPanel panel = forestPlot.createJPanel();
                    swingNode.setContent(panel);
                }
            });
        }

        /**
         * convert JFreeChart object to JPanel object
         * @return
         */
        public static JPanel createJPanel() {
            JFreeChart chart = createChart(createDataset());
            JPanel panel = new ChartPanel( chart );
            panel.setMinimumSize(new Dimension(1080,720));
            return panel;
        }

        /**
         * @param dataset BoxAndWhiskerCategoryDataset object
         * @return JFreeChart object
         */
        private static JFreeChart createChart(BoxAndWhiskerCategoryDataset dataset) {
            JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                    "title",
                    "",
                    "p",
                    dataset,
                    false);
            chart.getCategoryPlot().setOrientation(PlotOrientation.HORIZONTAL);

            return chart;
        }

        /**
         * reads table EXAMPLE in database
         * @return BoxAndWhiskerCategoryDataset object
         */
        private static BoxAndWhiskerCategoryDataset createDataset() {
                DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
                DatabaseHandler databaseHandler = new DatabaseHandler();

                //retrieve data
                String qu = "SELECT * FROM EXAMPLE WHERE ID != 'pooled' ";
                ResultSet resultset = databaseHandler.executeQuery(qu);

                //add data to dataset
                try {
                    int columnIndex = 0;
                    List list = new ArrayList();

                    while (resultset.next()) {
                        double p = resultset.getDouble(2);
                        double n = resultset.getDouble(3);
                        double mean = p/n;
                        double ME = 1.959964*Math.sqrt((mean*(1-mean))/n);
                        double CIhigh = mean + ME;
                        double CIlow = mean - ME;

                        BoxAndWhiskerItem item = new BoxAndWhiskerItem(mean,mean,mean,mean,CIlow,CIhigh,CIlow,CIhigh,list);
                        dataset.add(item,0,columnIndex);

                        columnIndex++;
                     }

                    //retrieve pooled statistics
                    double pooledN = parseDouble(databaseHandler.getElement("EXAMPLE","N","pooled"));
                    double pooledP = parseDouble(databaseHandler.getElement("EXAMPLE","P","pooled"))/pooledN;
                    double pooledME = 1.959964*Math.sqrt((pooledP*(1-pooledP))/pooledN);
                    BoxAndWhiskerItem item = new BoxAndWhiskerItem(pooledP,pooledP,pooledP,pooledP,pooledP-pooledME,pooledP+pooledME,pooledP-pooledME,pooledP+pooledME,list);
                    dataset.add(item,1,columnIndex);

              } catch (SQLException e) {
                  throw new RuntimeException(e);
              }

                databaseHandler.getHandler().closeConnection();
                return dataset;
            }


    /**
     * update the pooled row in table EXAMPLE
      */
    public static void updatePooled() {
        DatabaseHandler databaseHandler = new DatabaseHandler();

        try {
            //retrieve data
            String qu = "SELECT * FROM EXAMPLE WHERE ID='pooled'";
            ResultSet pooledResultSet = databaseHandler.getHandler().executeQuery(qu);
            String qu2 = "SELECT * FROM EXAMPLE WHERE ID != 'pooled' ";
            ResultSet resultset = databaseHandler.executeQuery(qu2);
            double pooledP = 0;
            double pooledN = 0;
            while (pooledResultSet.next()) {
                pooledP = pooledResultSet.getDouble("P");
                pooledN = pooledResultSet.getDouble("N");
            }

            //iterate through resultset to calculate pooled statistics
            while (resultset.next()) {
                double p = resultset.getDouble(2);
                double n = resultset.getDouble(3);
                pooledP = pooledP + p;
                pooledN = pooledN + n;
            }

            //update pooled statistics
            String qu3 = "UPDATE EXAMPLE SET " +
                    "P = " + pooledP + "," +
                    "N = " + pooledN + " WHERE ID = 'pooled'";
           databaseHandler.getHandler().executeAction(qu3);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        databaseHandler.closeConnection();
    }
}

