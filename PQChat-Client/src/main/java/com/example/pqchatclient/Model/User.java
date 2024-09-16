package com.example.pqchatclient.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private StringProperty id;
    private StringProperty fullName;
    private StringProperty avatarPath;

    public User(String id, String fullName, String avatarPath) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.avatarPath = new SimpleStringProperty(avatarPath);
    }
}
