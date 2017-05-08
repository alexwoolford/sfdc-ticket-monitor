package io.woolford.database.entity;

import java.util.Date;

public class Bundle {

    private String bundleName;
    private Date bundleDate;
    private String clusterId;

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

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public String toString() {
        return "Bundle{" +
                "bundleName='" + bundleName + '\'' +
                ", bundleDate=" + bundleDate +
                ", clusterId='" + clusterId + '\'' +
                '}';
    }
}
