package graphs;

import java.util.ArrayList;
import java.util.Collections;

public class WaterLevelGraphData {

    private static final WaterLevelGraphData INSTANCE = new WaterLevelGraphData();

    private final ArrayList<FrameData> data;

    public WaterLevelGraphData() {
        data = new ArrayList<>();
    }

    public void addFrame(FrameData frameData) {
        data.add(frameData);
    }

    public ArrayList<FrameData> getData() {
        ArrayList<FrameData> copy = new ArrayList<>();
        Collections.copy(copy, data);
        return copy;
    }

    public static WaterLevelGraphData getInstance() {
        return INSTANCE;
    }
}
