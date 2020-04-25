package graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import waterflowsim.Simulator;
import waterflowsim.Vector2D;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida slouzi pro vytvoreni instanci grafu vodnich hladin.
 * Dale zajistuje metody pro aktualizaci dat a uchovava reference na data, ktera aktualne pouziva.
 *
 * @author Martin Jakubašek
 * @version 1.00.00
 * @since 23.4.2020
 */
public class WaterLevelGraph {

    // reference na zpracovatele dat(managera)
    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    private DefaultCategoryDataset dataset; // dataset grafu
    private int pos;

    private int startX;
    private int startY;

    private int endX;
    private int endY;

    public WaterLevelGraph(int pos) {
        this.pos = pos;
        createDataset();
    }

    public WaterLevelGraph(int x, int y) {
        this(x, y, x, y);
    }

    public WaterLevelGraph(int x, int y, int x1, int y1) {
        this.startX = x;
        this.startY = y;
        this.endX = x1;
        this.endY = y1;

        int placeHolder;
        if (startX > endX || startY > endY) {
            placeHolder = startX;
            startX = endX;
            endX = placeHolder;

            placeHolder = startY;
            startY = endY;
            endY = placeHolder;
        }


    }

    private List<Double> getValues() {
        int sizeY = Simulator.getDimension().y;
        int sizeX = Simulator.getDimension().x;

        int i = 0;
        List<Double> values = new ArrayList<>();
        List<FrameData> framesData = data.getData();

        for (FrameData frame : framesData) {
            for (int y = startY; y <= endY; y++) {
                for (int x = startX; x <= endX; x++) {
                    values.add(frame.getWaterLevel(x*y + x));
                }
            }
        }

        return values;
    }

    private double getWaterLevel(List<Double> values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

    public JFreeChart createLineChart() {
        JFreeChart lineChart = ChartFactory.createLineChart("Vyska hladiny v case", "Cas(ms)", "Vyska hladiny(m)", dataset);

        return lineChart;
    }

    private void createDataset() {
        List<FrameData> framesData = data.getData();

        dataset = new DefaultCategoryDataset();
        for (FrameData frame : framesData) {
            dataset.addValue(frame.getWaterLevel(pos), "Prutok", "" + frame.getTime());
        }
    }

    public void updateDataset() {
        FrameData lastFrame = data.getData().get(data.getData().size() - 1);
        dataset.addValue(lastFrame.getWaterLevel(pos), "Prutok", "" + lastFrame.getTime());
    }

    /**
     * Vrati referenci na dataset grafu, ktery pouziva dana instance tridy.
     *
     * @return dataset grafu
     */
    public DefaultCategoryDataset getDataset() {
        return dataset;
    }
}
