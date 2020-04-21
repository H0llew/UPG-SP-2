import waterflowsim.Cell;
import waterflowsim.Vector2D;

public class CalculateTerrainHeight {

    private Vector2D<Integer> landDimPix;
    private Cell[] landData;

    private double maxHeight;
    private double minHeight;

    public CalculateTerrainHeight(Vector2D<Integer> landDimPix, Cell[] landData) {
        this.landDimPix = landDimPix;
        this.landData = landData;
    }

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

    public double[] getTerrainLevels(int numOfLvls) {
        calculateMinMaxHeightBox();

        double[] levels = new double[numOfLvls];
        double step = maxHeight / numOfLvls;

        for (int i = 0; i < levels.length; i++) {
            levels[i] = i*step;
        }

        return levels;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public double getMinHeight() {
        return minHeight;
    }
}
