module com.example.pqchatserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.mail;
    requires static lombok;
    requires org.xerial.sqlitejdbc;


    opens com.example.pqchatserver.Controllers to javafx.fxml;

    exports com.example.pqchatserver.Controllers;
    exports com.example.pqchatserver.Model;
    exports com.example.pqchatserver.Views;
    exports com.example.pqchatserver.Util;
}