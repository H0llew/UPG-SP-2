package graphs;

import waterflowsim.Cell;
import waterflowsim.Vector2D;

/**
 * Prepravka uchovava potrebne informace k
 * vizualizaci vysky hladiny vody v zavislosti na case.
 *
 * @author Martin Jakubašek
 * @version 1.0.00
 * @since 23.4.2020
 */
public class FrameData {

    private final double[] waterLevels; // pole vysek vodnich hladin vsech bodu krajiny
    private final double time; // cas od zacatku simulace

    private final Vector2D<Integer> landDimPix;

    /**
     * Konstruktor vytvori zaznam (pole vysek vodnich hladin a casu od zacatku simulace)
     * jednoho prekresleni kroku simulace.
     *
     * @param landDimPix velikost krajiny v bodech
     * @param landData bunky krajiny
     * @param time cas, kdy se snimek vykreslil od zacatku simulace
     */
    public FrameData(Vector2D<Integer> landDimPix, Cell[] landData, double time) {
        this.time = time;
        this.waterLevels = new double[landDimPix.x * landDimPix.y];

        this.landDimPix = landDimPix;

        fillWaterLevels(landDimPix, landData);
    }

    /**
     * Naplni pole waterLevels hodnotami velikosti vodniho sloupce v bunkach
     *
     * @param landDimPix velikost krajiny v bodech
     * @param landData bunky krajiny
     */
    private void fillWaterLevels(Vector2D<Integer> landDimPix, Cell[] landData) {
        for (int y = 0; y < landDimPix.y; y++) {
            for (int x = 0; x < landDimPix.x; x++) {
                int formula = landDimPix.x * y + x;
                waterLevels[formula] = landData[formula].getWaterLevel();
            }
        }
    }

    // GET SET

    /**
     * Vrati kopii pole vodnich sloupcu bunek
     *
     * @return pole vodnich sloupcu bunek
     */
    public double[] getWaterLevels() {
        double[] copy = new double[waterLevels.length];
        System.arraycopy(waterLevels, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Vrati vysku vodniho sloupce v urcite bunce
     * @param index index bunky
     *
     * @return velikost vodniho sloubce v bunce
     */
    public double getWaterLevel(int index) {
        return waterLevels[index];
    }

    /**
     * Vrati cas zaznamu
     * @return cas od zacatku spusteni simulace
     */
    public double getTime() {
        return time;
    }

    public Vector2D<Integer> getLandDimPix() {
        return landDimPix;
    }
}
