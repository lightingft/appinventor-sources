package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.client.editor.simple.SimpleEditor;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.InlineHTML;
import org.pepstock.charba.client.data.Dataset;

import java.util.List;

public abstract class MockChartData extends MockVisibleComponent {
    // Temporary placeholder for the Chart Data image
    private InlineHTML labelWidget;

    protected MockChartModel chartModel;
    protected MockChart chart;

    protected List<Float> yValues;

    /**
     * Creates a new instance of a Mock Chart Data component.
     *
     * @param editor editor of source file the component belongs to
     * @param type  type string of the component
     * @param icon  icon of the component
     */
    MockChartData(SimpleEditor editor, String type, ImageResource icon) {
        super(editor, type, icon);

        labelWidget = new InlineHTML();
        labelWidget.setStylePrimaryName("ode-SimpleMockComponent");
        labelWidget.setText("LINE CHART DATA");
        initComponent(labelWidget);
    }

    /**
     * Adds the Mock Chart Data component to the specified Mock Chart component
     * @param chart  Chart Mock component to add the data to
     */
    public void addToChart(MockChart chart) {
        // Set widget to invisible
        labelWidget.setVisible(false);
        labelWidget.setWidth("0");
        labelWidget.setHeight("0");

        this.chart = chart;
        this.chartModel = chart.createChartModel();
        setDefaultData();

        this.chart.addDataSeries(chartModel.getDataSeries());
    }

    @Override
    protected boolean isPropertyVisible(String propertyName) {
        // Hide HEIGHT and WIDTH properties (not needed for Chart Data)
        if (propertyName.equals(PROPERTY_NAME_HEIGHT) ||
                propertyName.equals(PROPERTY_NAME_WIDTH)) {
            return false;
        }

        return super.isPropertyVisible(propertyName);
    }

    @Override
    protected void onSelectedChange(boolean selected) {
        super.onSelectedChange(selected);
        removeStyleDependentName("selected"); // Force remove styling
    }

    protected abstract void setDefaultData();

//    protected void initializeDataSetFromChart() {
//        int add = chart.chartWidget.getData().getDatasets().size();
//
//        dataSeries = chart.chartWidget.newDataset();
//
//        dataSeries.setData(1 + add, 2 + add, 3 + add, 4 + add);
//    }
}
