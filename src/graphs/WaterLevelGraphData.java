package graphs;

import java.util.ArrayList;

public class WaterLevelGraphData {

    private static final WaterLevelGraphData INSTANCE = new WaterLevelGraphData();

    private final ArrayList<FrameData> data = new ArrayList<>();

    private ArrayList<WaterLevelGraph> activeGraphs = new ArrayList<>();
    private ArrayList<WaterLevelGraphA> activeGraphsA = new ArrayList<>();

    private WaterLevelGraphData() {
    }

    public void addFrame(FrameData frameData) {
        data.add(frameData);
    }

    public void registerGraph(WaterLevelGraph graph) {
        activeGraphs.add(graph);
    }

    public void registerGraphA(WaterLevelGraphA graph) {
        activeGraphsA.add(graph);
    }

    public void updateGraphs() {
        for (WaterLevelGraph graph : activeGraphs) {
            graph.updateDataset();
        }
        for (WaterLevelGraphA graph : activeGraphsA) {
            graph.addToDataset();
        }
    }

    public ArrayList<FrameData> getData() {
        return new ArrayList<>(data);
    }

    public static WaterLevelGraphData getInstance() {
        return INSTANCE;
    }
}
