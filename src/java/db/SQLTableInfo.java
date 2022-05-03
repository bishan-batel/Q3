/*
 * Kishan Patel
 * Oct 22
 * This class is used to store metadata about a SQL table
 */
package db;

import java.util.Arrays;

/**
 * Immutable store for metadata about a SQL table
 */
public class SQLTableInfo {

    private final String fullColumns;
    private final String[] tableHeaders;
    private final String tableName;

    public SQLTableInfo(String tableName, String... tableHeadersWithTypes) {
        // panic if headers are empty
        if (tableHeadersWithTypes == null || tableHeadersWithTypes.length == 0) {
            throw new IllegalArgumentException("Table columns cannot be empty");
        }

        this.tableName = tableName;
        fullColumns = SQLDb.toStringSeperatedByCommas(tableHeadersWithTypes);

        // pulls header names into an array
        this.tableHeaders = Arrays
                .stream(tableHeadersWithTypes)
                .map(header -> header.split(" ")[0])
                .toArray(String[]::new);
    }

    public String getHeader(int i) {
        // subtracts 1 to keep in theme with SQL derby API
        return tableHeaders[i];
    }

    public int getColumnIndexFor(String header) {
        // linear searches
        for (int i = 0; i < getColumnCount(); i++) {
            if (header.equalsIgnoreCase(tableHeaders[i])) {
                return i;
            }
        }
        return -1;
    }

    public String getColumn(int i) {
        return tableHeaders[i];
    }

    public String getName() {
        return tableName;
    }

    public String getFullColumns() {
        return fullColumns;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    public int getColumnCount() {
        return tableHeaders.length;
    }

    public float getFloat(String[] row, String name) {
        return Float.parseFloat(getString(row, name));
    }

    public double getDouble(String[] row, String name) {
        return Double.parseDouble(getString(row, name));
    }

    public int getInt(String[] row, String name) {
        return Integer.parseInt(getString(row, name));
    }

    public String getString(String[] row, String name) {
        return row[getColumnIndexFor(name)];
    }
}
