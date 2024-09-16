module com.example.pqchatserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.json;
    requires static lombok;
    requires java.mail;


//    opens com.example.pqchatserver.Controllers to javafx.fxml;
    exports com.example.pqchatserver.Controllers;
    exports com.example.pqchatserver.Model;
//    exports com.example.pqchatserver.Views;
    opens com.example.pqchatserver.Controllers to javafx.fxml;
}