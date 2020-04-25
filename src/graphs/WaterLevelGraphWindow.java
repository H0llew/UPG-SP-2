package graphs;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import utils.Window;

import javax.swing.*;

public class WaterLevelGraphWindow {

    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    public static JFrame create(String title, WaterLevelGraph graph) {
        JFrame jFrame = Window.createBasicWindow(200,200, title);

        jFrame.add(createChartPanel(graph.createLineChart()));

        data.registerGraph(graph);

        jFrame.setVisible(true);

        return jFrame;
    }

    public static JFrame create(String title, WaterLevelGraphA graph) {
        JFrame jFrame = Window.createBasicWindow(200,200, title);

        jFrame.add(createChartPanel(graph.createLineChart()));

        data.registerGraphA(graph);

        jFrame.setVisible(true);

        return jFrame;
    }

    private static ChartPanel createChartPanel(JFreeChart chart) {
        return new ChartPanel(chart);
    }
}
