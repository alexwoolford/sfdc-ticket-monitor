package io.woolford.database.entity;

public class Notification {

    private String caseNumber;
    private Boolean notificationSent;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "caseNumber='" + caseNumber + '\'' +
                ", notificationSent=" + notificationSent +
                '}';
    }

}
