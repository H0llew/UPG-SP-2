package graphs;

import java.util.ArrayList;

/**
 * Jedina instance tridy si uchovava veskere potrebne udaje k vykreleni grafu vodnich vysek,
 * a to pole {@link FrameData}, ktere si uchovava indormace z jednoho zaznamu dat krajiny.
 *
 * @author Martin Jakubasek
 * @version 1.00.000
 * @since 26.4.2020
 */
public class WaterLevelGraphData {

    // uchovava odkaz na jedinou instanci tridy
    private static final WaterLevelGraphData INSTANCE = new WaterLevelGraphData();

    /**
     * Urci kolik zaznamu se bude uchovavat
     */
    public static final int MAX_DATA_SIZE = 150;

    // pole zaznamu
    private final ArrayList<FrameData> data = new ArrayList<>();
    // pole registrovanych vodnich grafu (grafy zadajici o novy zaznam dat pri kazdem ziskani zaznamu dat)
    private ArrayList<WaterLevelGraph> activeGraphs = new ArrayList<>();

    // privatni konstruktor zabranuje vytvoreni nove instance tridy
    private WaterLevelGraphData() {
    }

    /**
     * Ulozi zaznam {@link FrameData} do pole zaznamu
     *
     * @param frameData {@link FrameData}
     */
    public void addFrame(FrameData frameData) {
        data.add(frameData);
        if (!reduceData())
            updateGraphs();
        else
            updateGraphsAfterReduction();
    }

    /**
     * Zaregistruje pozadovany graf a kazdy nasledujici zaznam mu posle nova data
     *
     * @param graph {@link WaterLevelGraph} graf pozadujici aktualizaci dat
     */
    public void registerGraph(WaterLevelGraph graph) {
        activeGraphs.add(graph);
    }

    /**
     * Odstrani pozadovany graf, tj. graf nebude dostavat nove hodnoty kazde zaznameni zaznamu
     *
     * @param graph Pozadovany graf {@link WaterLevelGraph}
     */
    public void unRegisterGraph(WaterLevelGraph graph) {
        activeGraphs.remove(graph);
    }

    /**
     * Aktualizuje vsechny zaregistrovane grafy, tak ze jim posle preda novy {@link FrameData} zaznam
     * z posledniho zaznamenani.
     */
    public void updateGraphs() {
        for (WaterLevelGraph graph : activeGraphs) {
            graph.addToDataset();
        }
    }

    /**
     * Updatuje data v grafech po redukci dat
     */
    private void updateGraphsAfterReduction() {
        for (WaterLevelGraph graph : activeGraphs) {
            graph.updateAfterReduction();
        }
    }

    /**
     * Zredukuje data
     *
     * @return true pokud doslo k redukci, false nikoliv
     */
    private boolean reduceData() {
        if (data.size() > MAX_DATA_SIZE) {
            int length = (data.size() - 1) / 2;
            for (int i = 1; i <= length; i++) {
                data.remove(i);
            }

            return true;
        }

        return false;
    }

    /**
     * Vrati pole zaznamenanych zaznamu {@link FrameData}
     *
     * @return pole zaznamu
     */
    public ArrayList<FrameData> getData() {
        return new ArrayList<>(data);
    }

    /**
     * Vrati odkaz na jedinou instanci tridy
     *
     * @return instance
     */
    public static WaterLevelGraphData getInstance() {
        return INSTANCE;
    }
}
