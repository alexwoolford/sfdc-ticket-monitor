package io.woolford.database.entity;

public class Account {

    private String accountId;
    private String accountName;
    private String customerRecordId2;

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

    public String getCustomerRecordId2() {
        return customerRecordId2;
    }

    public void setCustomerRecordId2(String customerRecordId2) {
        this.customerRecordId2 = customerRecordId2;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", customerRecordId2='" + customerRecordId2 + '\'' +
                '}';
    }

}
