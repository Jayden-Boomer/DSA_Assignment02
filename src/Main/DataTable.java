package Main;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DataTable {

    private final String tableName;
    private final String[] colHeaders;
    private final String[] rowHeaders;
    private ArrayList<long[]> dataRows;

    public DataTable(String tableName, String[] colHeaders, String[] rowHeaders) {
        this.tableName = tableName;
        this.colHeaders = colHeaders;
        this.rowHeaders = rowHeaders;
        this.dataRows = new ArrayList<>();
    }

    public void AddRow(long[] values) {
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
        for (long[] dataRow : dataRows) {
            for (int i = 0; i < dataRow.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], formatTime(dataRow[i]).length() + 3);
            }
        }


        // Print header
        System.out.println(tableName);
        printRow(colHeaders, columnWidths);

        // Print data rows
        for (int i = 0; i < dataRows.size(); i++) {
            String[] rowAsStrings = new String[dataRows.get(i).length+1];
            long[] dataRow = dataRows.get(i);
            rowAsStrings[0] = rowHeaders[i];
            for (int j = 0; j < dataRow.length; j++) {
                rowAsStrings[j+1] = formatTime(dataRow[j]);
            }
            printRow(rowAsStrings, columnWidths);
        }
    }

    public void printCSV() {
        System.out.println("\""+tableName+"\"");
        String[] formattedHeaders = new String[colHeaders.length];
        for (int i = 0; i < colHeaders.length; i++) {
            formattedHeaders[i] = "\"" + colHeaders[i] + "\"";
        }
        printRow(formattedHeaders);

        // Print data rows
        for (int i = 0; i < dataRows.size(); i++) {
            String[] rowAsStrings = new String[dataRows.get(i).length+1];
            long[] dataRow = dataRows.get(i);
            rowAsStrings[0] = "\""+rowHeaders[i]+"\"";
            for (int j = 0; j < dataRow.length; j++) {
                rowAsStrings[j+1] = ""+dataRow[j];
            }
            printRow(rowAsStrings);
        }
    }

    private void printRow(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(row[i]).append(",");
        }
        System.out.println(sb.deleteCharAt(sb.length()-1));
    }


    private void printRow(String[] row, int[] columnWidths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s     ", row[i]));
        }
        System.out.println(sb);
    }

    private void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }

    public static String formatTime(long nanoseconds) {
        DecimalFormat df = new DecimalFormat("#.#");
        if (nanoseconds >= 1_000_000_000) {
            double seconds = nanoseconds / 1_000_000_000.0;
            return df.format(seconds) + " s";
        } else if (nanoseconds >= 1_000_000) {
            long milliseconds = Math.round(nanoseconds / 1_000_000.0);
            return df.format(milliseconds) + " ms";
        } else {
            double milliseconds = nanoseconds / 1_000_000.0;
            return df.format(milliseconds) + " ms";
        }
    }


}
