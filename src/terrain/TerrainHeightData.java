package terrain;

import waterflowsim.Cell;
import waterflowsim.Vector2D;

import java.awt.*;

public class TerrainHeightData {

    private static final TerrainHeightData INSTANCE = new TerrainHeightData();

    private CalculateTerrainHeight calculations;

    private double[] terrainLevels;
    private Color[] colorLevels;

    private boolean isInitialized = false;

    private TerrainHeightData() {}

    public void calculateTerrainHeight(Vector2D<Integer> landDimPix, Cell[] landData, int numOfLvls) {
        calculations = new CalculateTerrainHeight(landDimPix, landData);
        calculations(numOfLvls);

        isInitialized = true;
    }

    public void recalculateTerrainHeight(int numOfLvls) {
        if (isInitialized) {
            calculations(numOfLvls);
        }
    }

    private void calculations(int numOfLvls) {
        terrainLevels = calculations.getTerrainLevels(numOfLvls);
        getColorLevels();
    }

    private void getColorLevels() {
        this.colorLevels = new Color[terrainLevels.length];

        for (int i = 0; i < colorLevels.length; i++) {
            Color actualColor = new Color(0,(254 - i*30) % 255,0);
            colorLevels[i] = actualColor;
        }
    }

    public Color[] getColors() {
        Color[] copy = new Color[colorLevels.length];
        System.arraycopy(colorLevels, 0, copy, 0, copy.length);
        return copy;
    }

    public double[] getTerrainLevels() {
        double[] copy = new double[terrainLevels.length];
        System.arraycopy(terrainLevels, 0, copy, 0, copy.length);
        return copy;
    }

    public static TerrainHeightData getData() {
        return INSTANCE;
    }
}
