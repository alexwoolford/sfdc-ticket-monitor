package io.woolford.database.entity;

public class SfdcTableColumn {

    private String tableName;
    private String columnName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "SfdcTableColumn{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }

}
