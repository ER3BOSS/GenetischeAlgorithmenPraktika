import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;

class LineChart extends ApplicationFrame {
    LineChart(String applicationTitle, String chartTitle, DefaultCategoryDataset dataset) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Generations -->","Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                true,true,false
        );

        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));

        lineChart.getCategoryPlot().getRenderer().setSeriesPaint(2, Color.magenta);

        lineChart.getPlot().setBackgroundPaint( Color.WHITE );
        lineChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 460*2 , 267*2 ) );
        setContentPane( chartPanel );
    }
}
