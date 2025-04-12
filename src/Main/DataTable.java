package Main;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DataTable {

    public enum Format {
        TIME,
        CSV,
        MEMORY
    }
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

    public void print(Format format) {
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
                switch (format) {
                    case TIME:
                        rowAsStrings[j+1] = formatTime(dataRow[j]);
                        break;
                    case MEMORY:
                        rowAsStrings[j+1] = formatMemory(dataRow[j]);
                        break;
                    case CSV: default:
                        rowAsStrings[j+1] =""+dataRow[j];
                        break;
                }
            }
            printRow(rowAsStrings, columnWidths);
        }
    }

    public void printCSV(long divideDataBy) {
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
                rowAsStrings[j+1] = ""+((double)dataRow[j]/divideDataBy);
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
    
    private static String formatMemory(long bytes) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (bytes >= GB)
            return String.format("%.1f GB",  (double) bytes / GB);
        else if (bytes >= MB)
            return String.format("%.1f MB",  (double) bytes / MB);
        else if (bytes >= KB)
            return String.format("%d KB",   bytes / KB);
        else
            return bytes + " B";
    }

    private static String formatTime(long nanoseconds) {
        final long MILLI = 1_000_000;
        final long SEC   = 1_000_000_000;
        DecimalFormat df = new DecimalFormat("#.#");
        if (nanoseconds >= SEC) {
            double seconds = (double) nanoseconds / SEC;
            return df.format(seconds) + " s";
        } else if (nanoseconds >= MILLI) {
            long milliseconds = Math.round((double)nanoseconds / MILLI);
            return df.format(milliseconds) + " ms";
        } else {
            double milliseconds = (double)nanoseconds / MILLI;
            return df.format(milliseconds) + " ms";
        }
    }


}
