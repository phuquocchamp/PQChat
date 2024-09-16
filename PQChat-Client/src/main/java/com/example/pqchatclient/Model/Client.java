package com.example.pqchatclient.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {
    private final StringProperty clientID;
    private final StringProperty accountID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty imagePath;

    public Client(String clientID, String accountID, String firstName, String lastName,  String imagePath) {
        this.clientID = new SimpleStringProperty(this,"Client ID", clientID);
        this.accountID = new SimpleStringProperty(this, "Account ID", accountID);
        this.firstName = new SimpleStringProperty(this,"First Name", firstName);
        this.lastName = new SimpleStringProperty(this,"Last Name", lastName);
        this.imagePath = new SimpleStringProperty(this,"Image Path", imagePath);
    }
    public StringProperty clientIDProperty() {
        return this.clientID;
    }
    public StringProperty accountIDProperty(){return accountID;}
    public StringProperty firstNameProperty() {
        return this.firstName;
    }
    public StringProperty lastNameProperty() {
        return this.lastName;
    }
    public StringProperty imagePathProperty() {
        return this.imagePath;
    }
}
