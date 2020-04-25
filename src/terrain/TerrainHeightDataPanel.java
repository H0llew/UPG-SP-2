package terrain;

import javax.swing.*;
import java.awt.*;

/**
 * Slouzi pro vytvoreni panelu popisku vyskove mapy krajiny
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 25.4.2020
 */
public class TerrainHeightDataPanel {

    // reference na jedinou instanci
    private static TerrainHeightDataPanel INSTANCE = new TerrainHeightDataPanel();

    // root panel panelu
    private JPanel root;

    // privatni konstruktor zabrani vytvoreni nove instance tridy
    private TerrainHeightDataPanel() {
    }

    /**
     * Vytvori prazdny panel a vrati referenci na nej
     *
     * @return reference na root panel
     */
    public JPanel createPanel() {
        root = new JPanel();

        return root;
    }

    /**
     * Zaplni root panel hodnotami
     */
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

    /**
     * Vrati referenci na jedinou instanci tridy
     *
     * @return reference na instanci tridy
     */
    public static TerrainHeightDataPanel getInstance() {
        return INSTANCE;
    }

    // slouzi pro vykresleni obdelniku s barvou levelu
    class Rectangle extends JComponent {
        private Color color;

        public Rectangle(Color color) {
            this.color = color;
            this.setPreferredSize(new Dimension(20, 20));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.fillRect(0,0,20,20);

        }
    }
}
