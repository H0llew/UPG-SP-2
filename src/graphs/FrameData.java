package graphs;

import waterflowsim.Cell;
import waterflowsim.Vector2D;

public class FrameData {

    private final double[] waterLevels;
    private final double time;

    public FrameData(Vector2D<Integer> landDimPix, Cell[] landData, double time) {
        this.time = time;
        this.waterLevels = new double[landDimPix.x * landDimPix.y];

        getWaterLevels(landDimPix, landData);
    }

    private void getWaterLevels(Vector2D<Integer> landDimPix, Cell[] landData) {
        for (int y = 0; y < landDimPix.y; y++) {
            for (int x = 0; x < landDimPix.x; x++) {
                int formula = landDimPix.x * y + x;
                waterLevels[formula] = landData[formula].getWaterLevel();
            }
        }
    }

    public double[] getWaterLevels() {
        double[] copy = new double[waterLevels.length];
        System.arraycopy(waterLevels, 0, copy, 0, copy.length);
        return copy;
    }

    public double getTime() {
        return time;
    }
}
