package com.google.appinventor.components.runtime;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;
import java.util.List;

public class PieChartView extends ChartView<PieChart, PieData> {
  private RelativeLayout rootView;
  private List<PieChart> pieCharts = new ArrayList<PieChart>();
  private Activity activity;

  public PieChartView(Activity context) {
    // Instantiate the Root View layout and the Root Chart
    rootView = new RelativeLayout(context);
    chart = new PieChart(context);

    // Store the activity variable locally
    this.activity = context;

    // Initialize default settings for the Root Chart
    initializeDefaultSettings();
  }

  @Override
  protected void initializeDefaultSettings() {
    super.initializeDefaultSettings();

    // The Legend must be drawn inside to prevent inner rings from
    // being misaligned on the main Pie Chart. The Legend is then aligned
    // LEFT to cause less overlap with the Pie Chart slices.
    chart.getLegend().setDrawInside(true);
    chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
  }

  @Override
  public View getView() {
    // Returns the underlying root RelativeLayout view
    // which stores all the Pie Chart rings
    return rootView;
  }

  @Override
  public ChartDataModel createChartModel() {
    PieChart pieChart = createPieChartRing();

    // Return a new Pie Chart Data model
    return new PieChartDataModel(pieChart, new PieData());
  }

  /**
   * Creates, initializes & attaches a new Pie Chart ring to add to the
   * Pie Chart root view.
   *
   * To be called upon creating a new PieChartDataModel.
   * @return  created Pie Chart instance
   */
  private PieChart createPieChartRing() {
    PieChart pieChart;

    if (pieCharts.isEmpty()) { // No Pie Charts have been added yet (root Pie Chart)
      pieChart = chart; // Set Pie Chart to root Pie Chart
    } else { // Inner Pie Chart
      pieChart = new PieChart(activity); // Create a new Pie Chart
      pieChart.getDescription().setEnabled(false); // Hide description
      pieChart.getLegend().setEnabled(false); // Hide legend
    };

    // Set the corresponding properties of the Pie Chart view
    // to the newly created Pie Chart
    setPieChartProperties(pieChart);

    // Create RelativeLayout params with MATCH_PARENT height and width and
    // CENTER_IN_PARENT property set to true. A future method call will
    // adjust all the necessary widths & heights.
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
        (ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    pieChart.setLayoutParams(params);

    pieCharts.add(pieChart); // Add new Pie Chart (ring) to the Pie Charts List
    rootView.addView(pieChart); // Add new Pie Chart (ring) to the root View

    // Return the newly constructed Pie Chart
    return pieChart;
  }

  /**
   * Sets the mutually defined styling proeprties to the specified Pie
   * Chart. This method allows having consistency between all the
   * instantiated Pie Chart rings.
   * @param chart  Pie Chart to apply styling settings to
   */
  private void setPieChartProperties(PieChart chart) {
    chart.setHoleRadius(0);
    chart.setTransparentCircleRadius(0);
  }

  @Override
  public synchronized void Refresh() {
    // Refresh each Pie Chart (ring) individually
    for (final PieChart pieChart : pieCharts) {
      // Notify the Data component of data changes (needs to be called
      // when Datasets get changed directly)
      pieChart.getData().notifyDataChanged();

      // Notify the Chart of Data changes (needs to be called
      // when Data objects get changed directly)
      pieChart.notifyDataSetChanged();

      // Invalidate the Chart on the UI thread (via the Handler)
      // The invalidate method should only be invoked on the UI thread
      // to prevent exceptions.
      uiHandler.post(new Runnable() {
        @Override
        public void run() {
          pieChart.invalidate();
        }
      });
    }
  }

  /**
   * Resizes all the inner Pie Charts according to the
   * total count of Pie Charts.
   */
  public void resizePieRings() {
    // Store width and height of last Pie Chart (since getHeight() and
    // getWidth() will not return the needed result instantly)
    int lastWidth = 0;
    int lastHeight = 0;

    for (int i = 0; i < pieCharts.size(); ++i) {
      PieChart pieChart = pieCharts.get(i);

      // Pie Chart non-last: expand radius
      if (i != pieCharts.size() - 1) {
        // TODO: Find generalized formula for radius factor
        pieChart.setTransparentCircleRadius(80);
        pieChart.setHoleRadius(80);
      }

      // FP on i != 0 always false
      if (i != 0) { // Inner Chart
        // Get the RelativeLayout parameters of the Pie Chart
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)(pieChart.getLayoutParams());

        // Compute new width & height
        // TODO: Find generalized formula for reduction factor
        lastWidth = (int)(lastWidth * 0.8f);
        lastHeight = (int)(lastHeight * 0.8f);

        // Set width & height of the Pie Chart, and update the Layout parameters
        // of the Chart
        params.width = lastWidth;
        params.height = lastHeight;
        pieChart.setLayoutParams(params);
      } else { // Root Chart
        // Set last height & width
        lastHeight = pieChart.getHeight();
        lastWidth = pieChart.getWidth();
      }

      // Refresh Chart to register changes
      pieChart.invalidate();
    }
  }
}
