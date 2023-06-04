module kate.inquiryproject {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.sql;
    requires jfreechart;
    requires jcommon;
    requires org.apache.commons.csv;

    opens kate.inquiryproject to javafx.fxml;
    exports kate.inquiryproject;
    exports kate.inquiryproject.controllers;
    opens kate.inquiryproject.controllers to javafx.fxml;
}