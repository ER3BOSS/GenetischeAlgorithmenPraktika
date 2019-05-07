import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
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

        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(0, new BasicStroke(3.0f));
        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));
        lineChart.getCategoryPlot().getRenderer().setSeriesStroke(2, new BasicStroke(3.0f));

        lineChart.getCategoryPlot().getRenderer().setSeriesPaint(2, Color.magenta);



        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560*2 , 367*2 ) );
        setContentPane( chartPanel );
    }
}
