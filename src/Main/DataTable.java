package Main;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Represents a formatted data table for presenting benchmark results.
 * Supports different output formats such as human-readable time, memory units, or raw CSV data.
 */
public class DataTable {

    /**
     * Defines the output format for displaying data.
     */
    public enum Format {
        /** Format data as human-readable time (e.g., ms, s). */
        TIME,
        /** Output as raw comma-separated values. */
        CSV,
        /** Format data as memory units (e.g., KB, MB). */
        MEMORY
    }

    private final String tableName;
    private final String[] colHeaders;
    private final String[] rowHeaders;
    private final ArrayList<long[]> dataRows;

    /**
     * Constructs a new DataTable instance with specified headers.
     *
     * @param tableName  The name/title of the table.
     * @param colHeaders The labels for each column (including the top-left cell).
     * @param rowHeaders The labels for each row (excluding the header row).
     */
    public DataTable(String tableName, String[] colHeaders, String[] rowHeaders) {
        this.tableName = tableName;
        this.colHeaders = colHeaders;
        this.rowHeaders = rowHeaders;
        this.dataRows = new ArrayList<>();
    }

    /**
     * Adds a row of data values to the table.
     *
     * @param values The array of long values to add as a row.
     */
    public void AddRow(long[] values) {
        dataRows.add(values);
    }

    /**
     * Prints the table in the specified format (TIME, MEMORY, or CSV).
     *
     * @param format The desired output format.
     */
    public void print(Format format) {
        // find the column widths by finding the string of maximum length between the headers and the data
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

        // print the title
        System.out.println(tableName);
        printRow(colHeaders, columnWidths);

        // Convert the numerical data rows into a string array
        for (int i = 0; i < dataRows.size(); i++) {
            String[] rowAsStrings = new String[dataRows.get(i).length + 1];
            long[] dataRow = dataRows.get(i);
            rowAsStrings[0] = rowHeaders[i];
            for (int j = 0; j < dataRow.length; j++) {
                switch (format) {
                    case TIME:
                        rowAsStrings[j + 1] = formatTime(dataRow[j]);
                        break;
                    case MEMORY:
                        rowAsStrings[j + 1] = formatMemory(dataRow[j]);
                        break;
                    case CSV:
                    default:
                        rowAsStrings[j + 1] = "" + dataRow[j];
                        break;
                }
            }
            printRow(rowAsStrings, columnWidths);
        }
    }


    /**
     * Prints the data in CSV format, with numeric values scaled by the specified divisor.
     *
     * @param divideDataBy A value to divide each data point by, used for unit conversion (e.g., nanoseconds to milliseconds).
     */
    public void printCSV(long divideDataBy) {
        // place quotation marks around string values and then print them
        System.out.println("\"" + tableName + "\"");
        String[] formattedHeaders = new String[colHeaders.length];
        for (int i = 0; i < colHeaders.length; i++) {
            formattedHeaders[i] = "\"" + colHeaders[i] + "\"";
        }
        printRow(formattedHeaders);

        // convert the data into strings and the print them
        for (int i = 0; i < dataRows.size(); i++) {
            String[] rowAsStrings = new String[dataRows.get(i).length + 1];
            long[] dataRow = dataRows.get(i);
            rowAsStrings[0] = "\"" + rowHeaders[i] + "\"";
            for (int j = 0; j < dataRow.length; j++) {
                rowAsStrings[j + 1] = "" + ((double) dataRow[j] / divideDataBy);
            }
            printRow(rowAsStrings);
        }
    }

    /**
     * Prints a row of strings separated by commas (used for CSV output).
     *
     * @param row The row of strings to print.
     */
    private void printRow(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (String s : row) {
            sb.append(s).append(",");
        }
        System.out.println(sb.deleteCharAt(sb.length() - 1));
    }

    /**
     * Prints a formatted row of strings with padding based on column widths.
     *
     * @param row          The row of strings to print.
     * @param columnWidths The array of widths for each column.
     */
    private void printRow(String[] row, int[] columnWidths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s     ", row[i]));
        }
        System.out.println(sb);
    }

    /**
     * Formats a byte count into a readable memory string (e.g., KB, MB, GB).
     *
     * @param bytes The byte value to format.
     * @return The formatted string representing the memory size.
     */
    private static String formatMemory(long bytes) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (bytes >= GB)
            return String.format("%.1f GB", (double) bytes / GB);
        else if (bytes >= MB)
            return String.format("%.1f MB", (double) bytes / MB);
        else if (bytes >= KB)
            return String.format("%d KB", bytes / KB);
        else
            return bytes + " B";
    }

    /**
     * Converts a time duration in nanoseconds to a formatted string.
     *
     * @param nanoseconds The time in nanoseconds.
     * @return A string representation in seconds, milliseconds, or nanoseconds.
     */
    private static String formatTime(long nanoseconds) {
        final long MILLI = 1_000_000;
        final long SEC = 1_000_000_000;
        DecimalFormat df = new DecimalFormat("#.#");

        if (nanoseconds >= SEC) {
            double seconds = (double) nanoseconds / SEC;
            return df.format(seconds) + " s";
        } else if (nanoseconds >= MILLI) {
            long milliseconds = Math.round((double) nanoseconds / MILLI);
            return df.format(milliseconds) + " ms";
        } else {
            double milliseconds = (double) nanoseconds / MILLI;
            return df.format(milliseconds) + " ms";
        }
    }
}
