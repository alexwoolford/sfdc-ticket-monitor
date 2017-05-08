package io.woolford.database.entity;

import java.math.BigDecimal;
import java.util.Date;

public class BundleEnriched {

    private String accountName;
    private String clusterName;
    private String bundleName;
    private Date bundleDate;
    private Float numMasters;
    private Float numSlaves;
    private BigDecimal usedStorage;
    private BigDecimal totalStorage;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public Date getBundleDate() {
        return bundleDate;
    }

    public void setBundleDate(Date bundleDate) {
        this.bundleDate = bundleDate;
    }

    public Float getNumMasters() {
        return numMasters;
    }

    public void setNumMasters(Float numMasters) {
        this.numMasters = numMasters;
    }

    public Float getNumSlaves() {
        return numSlaves;
    }

    public void setNumSlaves(Float numSlaves) {
        this.numSlaves = numSlaves;
    }

    public BigDecimal getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(BigDecimal usedStorage) {
        this.usedStorage = usedStorage;
    }

    public BigDecimal getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(BigDecimal totalStorage) {
        this.totalStorage = totalStorage;
    }

    @Override
    public String toString() {
        return "BundleEnriched{" +
                "accountName='" + accountName + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", bundleName='" + bundleName + '\'' +
                ", bundleDate=" + bundleDate +
                ", numMasters=" + numMasters +
                ", numSlaves=" + numSlaves +
                ", usedStorage=" + usedStorage +
                ", totalStorage=" + totalStorage +
                '}';
    }

}
