package io.woolford.database.entity;

public class Ticket {

    private String accountId;
    private String accountName;
    private String caseNumber;
    private String id;
    private String severity;
    private String currentStatusResolution;
    private String productComponent;
    private String problemStatementQuestion;
    private String description;
    private String contactId;
    private String contactName;
    private String priority;
    private String problemType;
    private String problemSubType;
    private String reason;
    private String status;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCurrentStatusResolution() {
        return currentStatusResolution;
    }

    public void setCurrentStatusResolution(String currentStatusResolution) {
        this.currentStatusResolution = currentStatusResolution;
    }

    public String getProductComponent() {
        return productComponent;
    }

    public void setProductComponent(String productComponent) {
        this.productComponent = productComponent;
    }

    public String getProblemStatementQuestion() {
        return problemStatementQuestion;
    }

    public void setProblemStatementQuestion(String problemStatementQuestion) {
        this.problemStatementQuestion = problemStatementQuestion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public String getProblemSubType() {
        return problemSubType;
    }

    public void setProblemSubType(String problemSubType) {
        this.problemSubType = problemSubType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "accountId='" + accountId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", caseNumber='" + caseNumber + '\'' +
                ", id='" + id + '\'' +
                ", severity='" + severity + '\'' +
                ", currentStatusResolution='" + currentStatusResolution + '\'' +
                ", productComponent='" + productComponent + '\'' +
                ", problemStatementQuestion='" + problemStatementQuestion + '\'' +
                ", description='" + description + '\'' +
                ", contactId='" + contactId + '\'' +
                ", contactName='" + contactName + '\'' +
                ", priority='" + priority + '\'' +
                ", problemType='" + problemType + '\'' +
                ", problemSubType='" + problemSubType + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
