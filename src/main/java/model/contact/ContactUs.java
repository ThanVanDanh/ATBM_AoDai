package model.contact;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContactUs extends AId implements Serializable {
    @ColumnName("full_name")
    private String fullName;
    private String email;
    @ColumnName("message_body")
    private String messageBody;
    @ColumnName("received_at")
    private LocalDateTime receivedAt;
    @ColumnName("status_message")
    private String statusMessage;

    public ContactUs( String fullName, String email, String messageBody) {
        this.fullName = fullName;
        this.email = email;
        this.messageBody = messageBody;

    }
    public ContactUs(int id, String fullName, String email, String messageBody, LocalDateTime receivedAt, String statusMessage) {
        this.setId(id);
        this.fullName = fullName;
        this.email = email;
        this.messageBody = messageBody;
        this.receivedAt = receivedAt;
        this.statusMessage = statusMessage;
    }


    public ContactUs() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getFormattedDate() {
        if (this.receivedAt == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return this.receivedAt.format(formatter);
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
