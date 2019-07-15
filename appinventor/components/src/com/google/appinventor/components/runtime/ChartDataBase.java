package com.google.appinventor.components.runtime;

import android.util.Log;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.CsvUtil;
import com.google.appinventor.components.runtime.util.YailList;

import java.util.Arrays;

@SimpleObject
public abstract class ChartDataBase implements Component {
    protected Chart container;
    protected ChartDataModel chartDataModel;

    private String label;
    private int color;

    private YailList csvColumns;
    private CSVFile dataSource;

    /**
     * Creates a new Chart Data component.
     */
    protected ChartDataBase(Chart chartContainer) {
        this.container = chartContainer;
        chartContainer.addDataComponent(this);
        initChartData();
    }

    /**
     * Returns the data series color as an alpha-red-green-blue integer.
     *
     * @return  background RGB color with alpha
     */
    @SimpleProperty(
            category = PropertyCategory.APPEARANCE)
    public int Color() {
        return color;
    }

    /**
     * Specifies the data series color as an alpha-red-green-blue integer.
     *
     * @param argb  background RGB color with alpha
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR,
            defaultValue = Component.DEFAULT_VALUE_COLOR_BLACK)
    @SimpleProperty
    public void Color(int argb) {
        color = argb;
        chartDataModel.setColor(color);
        refreshChart();
    }

    /**
     * Returns the label text of the data series.
     *
     * @return  label text
     */
    @SimpleProperty(
            category = PropertyCategory.APPEARANCE)
    public String Label() {
        return label;
    }

    /**
     * Specifies the text for the data series label.
     *
     * @param text  label text
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
            defaultValue = "")
    @SimpleProperty
    public void Label(String text) {
        this.label = text;
        chartDataModel.setLabel(text);
        refreshChart();
    }

    /**
     * Specifies the elements of the entries that the Chart should have.
     * @param elements  Comma-separated values of Chart entries alternating between x and y values.
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description="To be done (non-functional for now)",  category = PropertyCategory.BEHAVIOR)
    public void ElementsFromPairs(String elements) {
        // Base case:  nothing to add
        if (elements.equals("")) {
            return;
        }

        chartDataModel.setElements(elements);
        refreshChart();
    }

    /**
     * Initializes the Chart Data object by setting
     * the default properties and initializing the
     * corresponding ChartDataModel object instance.
     */
    public void initChartData() {
        // Creates a ChartDataModel based on the current
        // Chart type being used.
        chartDataModel = container.createChartModel();

        // Set default values
        Color(Component.COLOR_BLACK);
        Label("");
    }

    /**
     * Adds elements to the Data component from a specified List of tuples.
     *
     * @param list  YailList of tuples.
     */
    @SimpleFunction(description = "Imports data from a list of entries" +
      "Data is not overwritten.")
    public void ImportFromList(YailList list) {
        chartDataModel.importFromList(list);
        refreshChart();
    }

    /**
     * Removes all the entries from the Data Series.
     */
    @SimpleFunction(description = "Clears all of the data.")
    public void Clear() {
        chartDataModel.clearEntries();
        refreshChart();
    }


    /**
     * Imports data from a CSV file component, with the specified column names.
     *
     * TODO: Support multi-dimension data components
     *
     * @param csvFile  CSV File component to import form
     * @param xValueColumn  x-value column name
     * @param yValueColumn  y-value column name
     */
    @SimpleFunction(description = "Imports data from the specified CSVFile component, given the names of the " +
            "X and Y value columns. Passing in empty text for any of the column parameters will result" +
            " in the usage of the default option of entry 1 having the value of 0, entry 2 having the value of" +
            " 1, and so forth.")
    public void ImportFromCSV(final CSVFile csvFile, String xValueColumn, String yValueColumn) {
        // Construct a YailList of columns from the specified parameters
        final YailList columns = YailList.makeList(Arrays.asList(xValueColumn, yValueColumn));

        // Import the data from the CSV file with the specified columns asynchronously
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                // Import from CSV file with the specified parameters
                chartDataModel.importFromCSV(csvFile, columns);

                // Update the UI after importing (must be done on UI
                // thread to avoid exceptions)
                container.$context().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshChart();
                    }
                });
            }
        });
    }

    /**
     * Sets the CSV columns to parse data from the CSV source.
     *
     * TODO: Hide property in case the Source is not a CSVFile.
     *
     * @param columns  CSV representation of the column names (e.g. A,B will
     *                 use A for the x values, and B for the y values)
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
    @SimpleProperty(description="Sets the columns of the CSV file to parse data from." +
            "The columns must be specified in a CSV format, e.g. A,B will will use " +
            "A for the x values, and B for the y values.",
            category = PropertyCategory.BEHAVIOR,
                userVisible = false)
    public void CsvColumns(String columns) {
        try {
            this.csvColumns = CsvUtil.fromCsvRow(columns);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the Data Source for the Chart data component. The data
     * is then automatically imported.
     *
     * TODO: Modify description to include more data sources
     * TODO: Support for more Data Sources (so not only limited to CSVFile)
     * @param dataSource  Data Source to use for the Chart data.
     */
    @SimpleProperty(category = PropertyCategory.BEHAVIOR,
            description = "Sets the Data Source for the Data component. Accepted types " +
                    "include CSVFiles.")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHART_DATA_SOURCE)
    public void Source(final CSVFile dataSource) {
        this.dataSource = dataSource;
        ImportFromCSV(dataSource,
                csvColumns.getString(0), // X column
                csvColumns.getString(1)); // Y column
    }

    /**
     * Refreshes the Chart view object.
     */
    protected void refreshChart() {
        container.refresh();
    }

    @Override
    public HandlesEventDispatching getDispatchDelegate() {
        return null;
    }
}
