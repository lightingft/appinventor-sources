package com.google.appinventor.client.editor.simple.components;

import org.pepstock.charba.client.AbstractChart;
import org.pepstock.charba.client.data.Data;
import org.pepstock.charba.client.data.Dataset;

public abstract class MockChartModel<D extends Dataset> {
    protected D dataSeries;
    protected Data chartData;

    /**
     * Creates a new Mock Chart Model object instance, linking it with
     * the Data object of a specific Chart.
     *
     * @param chartData  Chart Data object to link to
     */
    protected MockChartModel(Data chartData) {
        this.chartData = chartData;
    }

    /**
     * Adds an (x,y) based entry to the Data Series.
     *
     * @param x  x value
     * @param y  y value
     */
    public abstract void addEntry(float x, float y);

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
     * Converts an ARGB color to RGBA hex format.
     *
     * @param color  &HAARRGGBB format color string
     * @return #RRGGBBAA format color string
     */
    protected String getHexColor(String color) {
        // The idea: Remove &H at the beginning, replace with # and reorder ARGB to RGBA
        return "#" + color.substring(4) + color.substring(2, 4);
    }
}
