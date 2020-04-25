package terrain;

import waterflowsim.Cell;
import waterflowsim.Vector2D;

/**
 * Slouzi pro urceni, jak se rozdeli jednotlive levely krajiny (tj. urci minimalni vysku jednotlivych levelu)
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 25.4.2020
 */
public class CalculateTerrainHeight {

    // pocet vodu krajiny na vysku a sirku
    private Vector2D<Integer> landDimPix;
    // bunky krajiny
    private Cell[] landData;

    // maximalni vyska krajiny
    private double maxHeight;
    // minimalni vyska krajiny
    private double minHeight;

    /**
     * Vytvori novou instanci {@link CalculateTerrainHeight}
     *
     * @param landDimPix pocet bodu krajiny na vysku a sirku
     * @param landData bunky krajiny
     */
    public CalculateTerrainHeight(Vector2D<Integer> landDimPix, Cell[] landData) {
        this.landDimPix = landDimPix;
        this.landData = landData;
    }

    /**
     * Vypocita minimalni a maximalni vysku krajiny
     */
    private void calculateMinMaxHeightBox() {
        boolean isFirstLoop = true;

        for (int y = 0; y < landDimPix.y; y++) {
            for (int x = 0; x < landDimPix.x; x++) {

                Cell actualCell = landData[landDimPix.x * y + x];
                if (isFirstLoop) {

                    maxHeight = actualCell.getTerrainLevel();
                    minHeight = actualCell.getTerrainLevel();

                    isFirstLoop = false;
                }

                maxHeight = Math.max(maxHeight, actualCell.getTerrainLevel());
                minHeight = Math.min(minHeight, actualCell.getTerrainLevel());
            }
        }
    }

    /**
     * Vypocita pocet a rozmezi levelu krajiny
     *
     * @param numOfLvls pocet levelu
     *
     * @return pole hodnot minimalnich vysek v levelu
     */
    public double[] getTerrainLevels(int numOfLvls) {
        calculateMinMaxHeightBox();

        int maxPossible = (int) (Math.abs(Math.abs(maxHeight) - Math.abs(minHeight)));
        if (maxPossible < numOfLvls) {
            numOfLvls = maxPossible;
        }
        if (numOfLvls > 255) {
            numOfLvls = 255;
        }
        if (numOfLvls == 0) {
            numOfLvls = 1;
        }

        double[] levels = new double[numOfLvls];
        double step = maxHeight / numOfLvls;

        for (int i = 0; i < levels.length; i++) {
            levels[i] = i*step;
        }

        return levels;
    }

    /**
     * Vrati maximalni vysku krajiny
     *
     * @return maximalni vyska krajiny
     */
    public double getMaxHeight() {
        return maxHeight;
    }

    /**
     * Vratin minimalni vysku krajiny
     *
     * @return minimalni vyska krajiny
     */
    public double getMinHeight() {
        return minHeight;
    }
}
