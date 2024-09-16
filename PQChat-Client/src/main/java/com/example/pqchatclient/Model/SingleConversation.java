package com.example.pqchatclient.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SingleConversation {
    private final StringProperty sender;
    private final StringProperty receiver;
    private final StringProperty message;
    private final StringProperty timeCreated;

    public SingleConversation(String sender, String receiver, String message, String timeCreated) {
        this.sender = new SimpleStringProperty(this, "Sender", sender);
        this.receiver = new SimpleStringProperty(this, "Receiver", receiver);
        this.message = new SimpleStringProperty(this, "Message", message);
        this.timeCreated = new SimpleStringProperty(this, "Time Created", timeCreated);
    }

    public StringProperty senderProperty(){
        return sender;
    }

    public StringProperty receiverProperty(){
        return receiver;
    }
    public StringProperty messageProperty(){
        return message;
    }
    public StringProperty timeCreatedProperty(){
        return timeCreated;
    }
}
