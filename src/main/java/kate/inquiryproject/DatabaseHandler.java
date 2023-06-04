package kate.inquiryproject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DBurl = "jdbc:derby:database/forum;create=true";
    private static Connection connection = null;
    private static Statement statement = null;
    public static DatabaseHandler handler;

    public DatabaseHandler() {
        createConnection();
    }

    public static DatabaseHandler getHandler() {
        if (handler == null) {
            handler = new DatabaseHandler();
            return handler;
        } else {
            return handler;
        }
    }

    private void createConnection() {
        try {
            connection = DriverManager.getConnection(DBurl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeAction(String qu) {
        try {
            statement = connection.createStatement();
            statement.execute(qu);
            return true;
        } catch (SQLException e) {
            System.err.println(e);
        }
        return false;
    }

    public ResultSet executeQuery(String qu) {
        ResultSet resultset;
        try {
            statement = connection.createStatement();
            resultset = statement.executeQuery(qu);
            return resultset;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param tableName
     * @param columnName
     * @param primaryKey
     * @return an element (in string) given key & column
     */
    public static String getElement(String tableName, String columnName, String primaryKey) {
         String stm = "SELECT " + columnName + " FROM " + tableName + " WHERE ID = '" + primaryKey + "'";
            ResultSet resultset = DatabaseHandler.getHandler().executeQuery(stm);

        try {
            if (resultset.next()) {
                return resultset.getString(columnName);
            } else {
                return "no element found";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * read input file to table EXAMPLE in database
     * only accepted format: double column1, double column2
     * @param filePath path to CSV file
     */
    public void parseFile(String filePath) {
        File file;
        Reader reader;
        Iterable<CSVRecord> CSVRecord;
        List<String[]> records;

        try {
            //pass CSV file into ArrayList records
            file = new File(filePath);
            reader = new FileReader(file);
            CSVRecord = CSVFormat.DEFAULT.parse(reader);
            records = new ArrayList<>();
            for (CSVRecord record : CSVRecord) {
                records.add(record.values());
            }

            //iterate through arrays to add value into table
            int columnIndex = 0;
            for (String[] record : records) {

                    String stm = "INSERT INTO EXAMPLE \n"
                            + "VALUES (" + "'" + columnIndex +"',";
                    for (String value : record) {
                        stm = stm + Double.parseDouble(value) +",";
                    }

                    stm = stm.substring(0,stm.length()-2) + ")";
                    this.statement.execute(stm);

                columnIndex++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
