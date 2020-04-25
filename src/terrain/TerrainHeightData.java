package terrain;

import waterflowsim.Cell;
import waterflowsim.Vector2D;

import java.awt.*;

/**
 * Jeji jedina instance slouzi pro ziskani vsech potrebnych informaci pro vykresleni vyskove mapy krajiny.
 * Tj. instance uchovava pole hodnot velikosti krajiny, ktere slouzi na rozkouskovani krajiny do nekolika "levelů",
 * kdy pocet levelu je mozne zmenit, ale jen do vyse maximalni vysky krajiny. Dale uchova pole barev (Color),
 * kdy jeden prvek prave nalezi jedne vyskove skupine (levelu). Barvy jsou nemenne a jsou generovany automaticky,
 * s tim ze nenizsi levely budou mit barvu do zelena a nejvyssi do hneda. Vizualizace barev zavisi na poctu levelu.
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 25.4.2020
 */
public class TerrainHeightData {

    // reference na jedinou instanci tridy
    private static final TerrainHeightData INSTANCE = new TerrainHeightData();

    // uchovava referenci na tridu, ktera se stara o veskere potrebne vypocty k vizualizaci vyskove mapy
    private CalculateTerrainHeight calculations;

    // pole dolnich hranic levelu vysek
    private double[] terrainLevels;
    // pole barev levelu vysek
    private Color[] colorLevels;

    // slouzi pro pripadnou rekalkulaci levelu vysek.
    // tj. pokud nedoslo k prvotnimu vypoctu calculateTerrainHeight vypise chybu
    private boolean isInitialized = false;

    // privatni konstruktor slouzi k zabraneni vytvoreni nove instance tridy
    private TerrainHeightData() {
    }

    /**
     * Provede vsechny nutne vypocty pro potrebne k vizualizaci vyskove mapy krajiny
     *
     * @param landDimPix pocet bodu krajiny na sirku a vysku
     * @param landData   vody krajiny
     * @param numOfLvls  pocet levelu (pokud je pocet vetsi nez celkova vyska krajiny, nastavi se na celkovou vysku krajiny)
     */
    public void calculateTerrainHeight(Vector2D<Integer> landDimPix, Cell[] landData, int numOfLvls) {
        calculations = new CalculateTerrainHeight(landDimPix, landData);
        calculations(numOfLvls);

        isInitialized = true;
    }

    /**
     * Prepocita data potrebna v vizualizaci vyskove mapy pro novy pocet levelu
     *
     * @param numOfLvls novy pocet levelu
     */
    public void recalculateTerrainHeight(int numOfLvls) {
        if (isInitialized) {
            calculations(numOfLvls);
        }
    }

    /**
     * Vypocita hodnoty pro pole vysek levelu a barev levelu
     *
     * @param numOfLvls pocet levelu
     */
    private void calculations(int numOfLvls) {
        terrainLevels = calculations.getTerrainLevels(numOfLvls);
        getColorLevels();
    }

    /**
     * Urci barvy pro jednotlive levely krajiny
     */
    private void getColorLevels() {
        this.colorLevels = new Color[terrainLevels.length];

        for (int i = 0; i < colorLevels.length; i++) {
            double step = 255f / colorLevels.length;
            Color actualColor = new Color((int) (i * step / 2), (int) (255 - i * step), (int) (i * step / 5));
            colorLevels[i] = actualColor;
        }
    }

    /**
     * Vrati pole barev levelu krajiny
     *
     * @return pole barev levelu krajiny
     */
    public Color[] getColors() {
        Color[] copy = new Color[colorLevels.length];
        System.arraycopy(colorLevels, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Vrati pole minimalnich vysek levelu krajiny
     *
     * @return pole minimalnich vysek levelu krajiny
     */
    public double[] getTerrainLevels() {
        double[] copy = new double[terrainLevels.length];
        System.arraycopy(terrainLevels, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Vrati odkaz na jedinou instanci tridy
     *
     * @return instance tridy
     */
    public static TerrainHeightData getData() {
        return INSTANCE;
    }
}
