package terrain;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TerrainHeightDataPanel {

    private static TerrainHeightDataPanel INSTANCE = new TerrainHeightDataPanel();

    private JPanel root;

    public TerrainHeightDataPanel() {
    }

    public JPanel createPanel() {
        root = new JPanel();

        return root;
    }

    public void addChartPanel() {
        TerrainHeightData tHD = TerrainHeightData.getData();

        JPanel chartRoot = new JPanel();
        GridLayout gridLayout = new GridLayout(tHD.getTerrainLevels().length, 2);
        chartRoot.setLayout(gridLayout);

        for (int i = tHD.getTerrainLevels().length -1; i >= 0; i--) {
            double heightNumber = tHD.getTerrainLevels()[i];
            String heightText = "";

            if (i == tHD.getTerrainLevels().length - 1) {
                heightText = String.format("%.1fm+", heightNumber);
            }
            else {
                double heightPrevious = tHD.getTerrainLevels()[i+1];
                heightText = String.format("%.1fm-%.1fm", heightNumber, heightPrevious);
            }

            JPanel row = new JPanel();

            //row.add(new JLabel(heightText));
            //row.add(new JLabel("barva"));

            chartRoot.add(new JLabel(heightText));
            chartRoot.add(new Rectangle(tHD.getColors()[i]));
        }

        root.add(chartRoot);
    }

    public static TerrainHeightDataPanel getInstance() {
        return INSTANCE;
    }

    class Rectangle extends JComponent {
        private Color color;

        public Rectangle(Color color) {
            this.color = color;
            this.setPreferredSize(new Dimension(20, 20));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillRect(10,10,20,20);

        }
    }
}
