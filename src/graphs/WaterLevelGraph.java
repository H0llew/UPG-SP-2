package graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.plaf.IconUIResource;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Reprezentuje graf vodnich vysek, ktery lze nasledne pridad do okna
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 26.4.2020
 */
public class WaterLevelGraph {

    // uchovava odkaz na jedinou instanci tridy
    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    // dataset linearniho grafu
    private DefaultCategoryDataset dataset;
    // dataset XY grafu
    private XYSeriesCollection XYDataset;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    /**
     * Vytvori novou instanci {@link WaterLevelGraph}
     *
     * @param x  x-ova souradnice leveho horniho rohu obdelnika
     * @param y  y-ova souradnice leveho horniho rohu obdelnika
     * @param x1 x-ova souradnice praveho dolniho rohu obdelnika
     * @param y1 y-ova souradnice praveho dolniho rohu obdelnika
     */
    public WaterLevelGraph(int x, int y, int x1, int y1) {
        processInputTest(x, y, x1, y1);
        createDateset();
        createXYDataset();
    }

    private void processInputTest(int x, int y, int x1, int y1) {
        int placeHolder;
        // začínám na [1;1]
        //
        // jdu na [0;0], [0;1] a [1,0]
        if (x >= x1 && y >= y1) {
            // prohod x
            placeHolder = x;
            x = x1;
            x1 = placeHolder;
            // prohod y
            placeHolder = y;
            y = y1;
            y1 = placeHolder;
        }
        // jdu na [2;0]
        else if (x < x1 && y > y1) {
            // prohodíme y
            placeHolder = y;
            y = y1;
            y1 = placeHolder;
        }
        // jdu na [2;1], [2,2] a [1,2]
        else if (x <= x1 && y <= y1) {
            // nic nedělej
        }
        // jdu na [0;2]
        else if (x > x1 && y < y1) {
            // prohodíme x
            placeHolder = x;
            x = x1;
            x1 = placeHolder;
        } else {
            System.err.println("Chyba ve zpracování inputu");
        }

        startX = x;
        startY = y;
        endX = x1;
        endY = y1;
    }

    /**
     * Vytvori dataset grafu
     */
    private void createDateset() {
        List<FrameData> frameData = data.getData();

        dataset = new DefaultCategoryDataset();
        for (FrameData frame : frameData) {
            double value = getAvgValue(frame);
            dataset.addValue(value, "Prutok", "" + frame.getTime());
        }
    }

    private void createXYDataset() {
        List<FrameData> frameData = data.getData();

        XYDataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Prutok");
        for (FrameData frame : frameData) {
            double value = getAvgValue(frame);
            series.add(frame.getTime(), value);
        }
        XYDataset.addSeries(series);
    }

    /**
     * Prida novou polozku do datasetu (Prida posledni pridany zaznam do zaznamu v {@link WaterLevelGraphData})
     */
    public void addToDataset() {
        FrameData lastFrame = data.getData().get(data.getData().size() - 1);
        double value = getAvgValue(lastFrame);
        dataset.addValue(value, "Prutok", "" + lastFrame.getTime());

        XYDataset.getSeries(0).add(lastFrame.getTime(), value);
    }

    /**
     * Vrati prumernou hodnotu z oblasti v framedata (do prumeru se nepocitaji oblasti s 0 vodni hladinou)
     *
     * @param frameData {@link FrameData}
     * @return prumerna hodnota
     */
    private double getAvgValue(FrameData frameData) {
        int counter = 0;
        double sums = 0;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                double actual = frameData.getWaterLevel(frameData.getLandDimPix().x * y + x);
                if (actual > 0) {
                    sums += actual;
                    counter++;
                }
            }
        }

        if (counter == 0) {
            counter = 1;
        }

        return sums / counter;
    }

    /**
     * Vytvori novy linearni graf
     *
     * @return novy linearni graf vysek vodnich hladin/y
     */
    public JFreeChart createLineChart() {
        JFreeChart lineChart = ChartFactory.createLineChart("Vyska hladiny v case", "Cas(ms)", "Vyska hladiny(m)", dataset);

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.DARK_GRAY);

        return lineChart;
    }

    public JFreeChart createLineXYChart() {
        JFreeChart lineXYChart = ChartFactory.createXYLineChart("Vyska hladiny v case",
                "Cas(s)",
                "Vyska hladiny(m)",
                XYDataset,
                PlotOrientation.VERTICAL, true, true, false);

        XYPlot plot = lineXYChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(renderer);

        return lineXYChart;
    }

    public Point2D getStart() {
        return new Point2D.Double(startX, startY);
    }

    public Point2D getEnd() {
        return new Point2D.Double(endX, endY);
    }
}
