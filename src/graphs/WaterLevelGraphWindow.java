package graphs;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import utils.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Slouzi pro vytvoreni noveho okna obsahujici graf vodnich vysek ziskaneho pomoci tridy {@link WaterLevelGraph}
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 26.4.2020
 */
public class WaterLevelGraphWindow {

    // reference na jedinou instanci tridy
    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    /**
     * Vytvori {@link JFrame} obsahujici {@link JFreeChart} s vizualizci grafu vysky/ek vodni hladiny ziskaneho
     * z {@link WaterLevelGraph}
     * @param title titulek okna s grafem
     * @param graph {@link WaterLevelGraph} graf vysky/ek vodni hladiny
     *
     * @return {@link JFrame} s grafem vodnich vysek hladiny
     */
    public static JFrame create(String title, WaterLevelGraph graph) {
        int width = 500;
        int height = 400;

        JFrame jFrame = Window.createBasicWindow(width,height, title);

        ChartPanel panel = createChartPanel(graph.createLineXYChart());
        panel.setPreferredSize(new Dimension(width, height));
        jFrame.add(panel);

        setOnCloseOperations(jFrame);
        jFrame.setVisible(true);

        data.registerGraph(graph);

        jFrame.pack();

        return jFrame;
    }

    /**
     * Nastavi {@link JFrame} operaci pri zavreni okna
     * @param jFrame {@link JFrame} jframe
     */
    private static void setOnCloseOperations(JFrame jFrame) {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        };

        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(windowListener);
    }

    /**
     * Vytvori ChartPanel s JFreeChart chart a vrati takto vytvoreni ChartPanel
     *
     * @param chart {@link JFreeChart} chart
     *
     * @return {@link ChartPanel} chartPanel s chart grafem
     */
    private static ChartPanel createChartPanel(JFreeChart chart) {
        return new ChartPanel(chart);
    }
}
