import waterflowsim.Cell;
import waterflowsim.Vector2D;
import waterflowsim.WaterSourceUpdater;

import javax.swing.*;
import java.awt.*;

/**
 * Trida slouzi pro vytvareni noveho okna se simulaci toku vody v krajine
 *
 * @author Martin Jakubašek
 * @version 1.0
 * @since 22.3.2020
 */
public class LandscapeWindow {

    // Atributy pro vysku a sirku okna
    int width;
    int height;

    // Atributy ze Simulatoru
    private Vector2D<Integer> landDimPix;
    private Cell[] landData;
    private Vector2D<Double> delta;
    private WaterSourceUpdater[] waterSources;

    // Atribut classy ktera vykresluje krajinu
    private DrawLandscape landscapeMap;

    /**
     * Konstuktor okna s krajinou
     *
     * @param width        sirka okna
     * @param height       vyska okna
     * @param landDimPix   dimenze krajiny z Simulator.getDimensions()
     * @param landData     data krajiny z Simulator.getData()
     * @param delta        delta krajiny z Simulator.getDelta()
     * @param waterSources vodni zdroje z Simulator.getWaterSources
     */
    public LandscapeWindow(int width, int height,
                           Vector2D<Integer> landDimPix, Cell[] landData,
                           Vector2D<Double> delta, WaterSourceUpdater[] waterSources) {

        this.landDimPix = landDimPix;
        this.landData = landData;
        this.delta = delta;
        this.waterSources = waterSources;

        this.width = width;
        this.height = height;
    }

    /**
     * vytvori JFrame s krajinou
     **/
    public JFrame create() { ;
        JFrame jFrame = Window.createBasicWindow(width, height, "new Window");

        landscapeMap = new DrawLandscape(landDimPix, landData, delta, waterSources);
        landscapeMap.setPreferredSize(new Dimension(width, height));
        jFrame.add(landscapeMap);

        jFrame.pack();

        return jFrame;
    }

    /**
     * Vrati panel s krajinou
     *
     * @return panel krajiny
     */
    public DrawLandscape getLandscapeMap() {
        return landscapeMap;
    }
}
