package com.google.appinventor.client.editor.simple.components;

import org.pepstock.charba.client.data.Data;
import org.pepstock.charba.client.data.Dataset;

import java.util.List;

/**
 * Chart Data Model base class.
 *
 * The Data Model is responsible for handling data operations
 * of a Chart, and represents the data of a single Data Series.
 * Individual styling and options of a single Data Series is also
 * a responsibility of the Data Model class.
 */
public abstract class MockChartDataModel<D extends Dataset> {
    protected D dataSeries;
    protected Data chartData;

    /**
     * Creates a new Mock Chart Model object instance, linking it with
     * the Data object of a specific Chart.
     *
     * @param chartData  Chart Data object to link to
     */
    protected MockChartDataModel(Data chartData) {
        this.chartData = chartData;
    }

    /**
     * Changes the Color of the Data Series.
     *
     * @param color  New Color value in &HAARRGGBB format.
     */
    public abstract void changeColor(String color);

    /**
     * Changes the label of the Data Series.
     *
     * @param text  New text value
     */
    public void changeLabel(String text) {
        dataSeries.setLabel(text);
    }

    /**
     * Adds the data series of this object to the Chart.
     */
    protected void addDataSeriesToChart() {
        // When adding the first Data Series, it should be set
        // to the Chart Data object itself rather then appended,
        // to register the first (new) DataSet List to the Chart data.
        // Subsequent adding of Data Series objects can simply be added
        // to the end of the List.
        if (chartData.getDatasets().size() == 0) {
            chartData.setDatasets(dataSeries);
        } else {
            chartData.getDatasets().add(dataSeries);
        }
    }

    /**
     * Removes the Data Series from the Chart.
     */
    public void removeDataSeriesFromChart() {
        chartData.getDatasets().remove(dataSeries);
    }

    /**
     * Converts an ARGB color to RGB hex format.
     *
     * @param color  &HAARRGGBB format color string
     * @return #RRGGBB format color string
     */
    protected String getHexColor(String color) {
        // The idea: Remove &H at the beginning, replace with # and reorder ARGB to RGB
        return "#" + color.substring(4);
    }

    /**
     * Sets the elements of the Data Series from a CSV-formatted String.
     *
     * @param elements String in CSV format
     */
    public void setElements(String elements) {
        // Split the entries by the comma
        // TODO: Possibly move this to the server side? (commas not escaped with this approach)
        String[] entries = elements.split(",");

        // Clear the current entries
        clearEntries();

        // Get the tuple size of the Data Model
        int tupleSize = getTupleSize();

        // The tuples are grouped together and then added to the
        // Data Model in this loop
        for (int i = 0; i < entries.length; i += tupleSize) {
            /* (i, i + 1, ..., i + tupleSize - 1) forms the tuple
               E.g.: i = 0, tupleSize = 2, (0, 1) indexes represent the tuple
               Therefore if the last index is greater than the entries length,
               the tuple group is invalid and the method should return. */
            if (i + tupleSize - 1 >= entries.length) {
                break;
            }

            // Create an array having the capacity to hold a tuple
            String[] tuple = new String[tupleSize];

            // Set the appropriate elements to the tuple
            for (int j = 0; j < tupleSize; ++j) {
                tuple[j] = entries[i + j];
            }

            // Add the tuple to the Data Model
            addEntryFromTuple(tuple);
        }

        // Perform the defined action after data import
        postDataImportAction();
    }

    /**
     * Sets the default elements for the Data Model.
     * To be called when no data is specified.
     */
    protected abstract void setDefaultElements();

    /**
     * Sets the default styling properties of the Data Series.
     */
    protected abstract void setDefaultStylingProperties();

    /**
     * Sets the elements of the Data Series from the specified CSV columns.
     *
     * @param columns List of columns to import from
     */
    public void setElementsFromCSVColumns(List<List<String>> columns) {
        // Clear the current entries
        clearEntries();

        // Initially, the row size of the columns has to be determined
        int rows = 0;

        for (int i = 0; i < columns.size(); ++i) {
            int columnSize = columns.get(i).size();

            // Non-empty column found, break here
            if (columnSize > 0) {
                rows = columnSize;
                break;
            }
        }

        // Iterate through all the rows (except the first, which is the columnNames)
        for (int i = 1; i < rows; ++i) {
            // Create an array that can hold all the tuple entries
            String[] tuple = new String[getTupleSize()];

            // Iterate through all the columns.
            // Column size is the same as the tuple size
            for (int j = 0; j < getTupleSize(); ++j) {
                List<String> column = columns.get(j);

                if (column.size() == 0) { // Empty column; Use default value
                    tuple[j] = getDefaultTupleEntry(i);
                } else { // Use the column's value
                    tuple[j] = column.get(i);
                }
            }

            // Add the constructed tuple as an entry to the Data Model
            addEntryFromTuple(tuple);
        }

        // Perform the defined action after data import
        postDataImportAction();
    }

    /**
     * Adds an entry to the Data Model from the specified tuple (List of Strings),
     * where each String in the list represents an entry of the tuple.
     * @param tuple  List of entries (Strings) forming the tuple
     */
    public abstract void addEntryFromTuple(String... tuple);

    /**
     * Returns the default entry to use for the specified index value.
     * To be used when data is not present, and a default value substitute
     * has to be used.
     *
     * @param index  index to create a default entry for
     * @return  default entry (String)
     */
    protected abstract String getDefaultTupleEntry(int index);

    /**
     * Clears all the entries in the Data Series.
     */
    public abstract void clearEntries();

    /**
     * Action performed after data importing.
     * To be called after each data importing method.
     *
     * The most common example of such action is setting
     * default elements in case no data is present.
     */
    protected abstract void postDataImportAction();

    /**
     * Returns the size of the tuples that this Data Series
     * accepts.
     *
     * @return  tuple size (integer)
     */
    protected abstract int getTupleSize();
}
