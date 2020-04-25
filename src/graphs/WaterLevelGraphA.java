package graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.geom.Point2D;
import java.util.List;

public class WaterLevelGraphA {

    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    private DefaultCategoryDataset dataset;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public WaterLevelGraphA(int x, int y, int x1, int y1) {
       processInputTest(x,y,x1,y1);
       createDateset();
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
        }
        else {
            System.err.println("Chyba ve zpracování inputu");
        }

        startX = x;
        startY = y;
        endX = x1;
        endY = y1;
    }

    private void createDateset() {
        List<FrameData> frameData = data.getData();

        dataset = new DefaultCategoryDataset();
        for (FrameData frame : frameData) {
            double value = getAvgValue(frame);
            dataset.addValue(value, "Prutok", "" + frame.getTime());
        }
    }

    public void addToDataset() {
        FrameData lastFrame = data.getData().get(data.getData().size() - 1);
        double value = getAvgValue(lastFrame);
        dataset.addValue(value, "Prutok", "" + lastFrame.getTime());
    }

    private double getAvgValue(FrameData frameData) {
        int counter = 0;
        double sums = 0;

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                sums += frameData.getWaterLevel(frameData.getLandDimPix().x * y + x);
                counter++;
            }
        }

        if (counter == 0) {
            counter = 1;
        }

        return sums / counter;
    }

    public JFreeChart createLineChart() {
        JFreeChart lineChart = ChartFactory.createLineChart("Vyska hladiny v case", "Cas(ms)", "Vyska hladiny(m)", dataset);

        return lineChart;
    }

    public Point2D getStart()  {
        return new Point2D.Double(startX,startY);
    }

    public Point2D getEnd() {
        return new Point2D.Double(endX, endY);
    }
}
