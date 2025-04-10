package Main;

import java.util.ArrayList;

public class DataTable<T> {

    private final String tableName;
    private final String[] colHeaders;
    private final String[] rowHeaders;
    private ArrayList<T[]> dataRows;

    public DataTable(String tableName, String[] colHeaders, String[] rowHeaders) {
        this.tableName = tableName;
        this.colHeaders = colHeaders;
        this.rowHeaders = rowHeaders;
        this.dataRows = new ArrayList<>();
    }

    public void AddRow(T[] values) {
        dataRows.add(values);
    }

    public void print() {
        int[] columnWidths = new int[colHeaders.length];
        for (int i = 0; i < colHeaders.length; i++) {
            columnWidths[i] = colHeaders[i].length();
        }
        for (String rowHeader : rowHeaders) {
            columnWidths[0] = Math.max(columnWidths[0], rowHeader.length());
        }
        for (T[] row : dataRows) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].toString().length());
            }
        }


        // Print header
        printRow(colHeaders, columnWidths);

        // Print data rows
        for (int i = 0; i < dataRows.size(); i++) {
            String[] rowAsStrings = new String[dataRows.get(i).length];
            T[] row = dataRows.get(i);
            for (int j = 0; j < row.length; j++) {
                rowAsStrings[j] = row[j].toString();
            }
            System.out.print(rowHeaders[i]);
            printRow(rowAsStrings, columnWidths);
        }
    }

    private static void printRow(String[] row, int[] columnWidths) {
        for (int i = 0; i < row.length; i++) {
            System.out.printf("%-" + columnWidths[i] + "s ", row[i]);
        }
        System.out.println();
    }

    private static void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }

}
