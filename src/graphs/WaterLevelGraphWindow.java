package graphs;

import utils.Window;

import javax.swing.*;

public class WaterLevelGraphWindow {

    private static final WaterLevelGraphData data = WaterLevelGraphData.getInstance();

    public static JFrame create(String title) {
        JFrame jFrame = Window.createBasicWindow(200,200, title);

        return jFrame;
    }
}
