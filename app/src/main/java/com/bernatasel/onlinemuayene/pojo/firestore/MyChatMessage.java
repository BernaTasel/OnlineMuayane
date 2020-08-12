package com.bernatasel.onlinemuayene.pojo.firestore;

public class MyChatMessage {
    private Long timestamp;
    private Boolean patientToDoctor;
    private String message;

    public MyChatMessage() {
    }

    public MyChatMessage(Long timestamp, Boolean patientToDoctor, String message) {
        this.timestamp = timestamp;
        this.patientToDoctor = patientToDoctor;
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getPatientToDoctor() {
        return patientToDoctor;
    }

    public void setPatientToDoctor(Boolean patientToDoctor) {
        this.patientToDoctor = patientToDoctor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyChatMessage{" +
                "timestamp=" + timestamp +
                ", patientToDoctor=" + patientToDoctor +
                ", message='" + message + '\'' +
                '}';
    }
}