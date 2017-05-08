package io.woolford.database.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Cluster {

    private String clusterId;
    private String clusterName;
    private Float numMasters;
    private Float numSlaves;
    private BigDecimal usedStorage;
    private BigDecimal totalStorage;

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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
        return "Cluster{" +
                "clusterId='" + clusterId + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", numMasters=" + numMasters +
                ", numSlaves=" + numSlaves +
                ", usedStorage=" + usedStorage +
                ", totalStorage=" + totalStorage +
                '}';
    }

}
