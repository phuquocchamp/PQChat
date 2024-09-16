package com.example.pqchatserver.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private StringProperty id;
    private StringProperty fullName;
    private StringProperty avatar;

    public User(String id, String fullName, String avatarPath) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.avatar = new SimpleStringProperty(avatarPath);
    }
}
