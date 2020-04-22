import waterflowsim.Simulator;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hlavni trida programu.
 * Zajistuje spusteni simulace + aktualizaci simulace + vykresleni
 *
 * @author Martin Jakubasek
 * @version 1.0
 * @since 22.3.2020
 */
public class L01_SpusteniSimulatoru {

    // uklada okno s krajinou
    private static JFrame landscapeWindow;

    private final static String windowTitle = "WaterFlowSim - A19B0069P Martin Jakubašek";

    //zakladni rozmery okna s krajinou
    private static final int WIDTH_LAND = 600;
    private static final int HEIGHT_LAND = 600;

    private static double updateInterval = 100; // inteval aktualizace krajiny v ms

    private static int scenario; // scenar ktery bezi

    // 1.00.41020

    public static final int DEFAULT_SIM_SPEED = 200;
    private static double simSpeed = DEFAULT_SIM_SPEED / 1_000d;

    private static boolean updateSim = true;

    /**
     * Hlavni metoda programu, spusti program, ktery vykresli krajinu a
     * pravidelne ji aktualizuje
     *
     * @param args pouzit pro vyber scenare (0-3)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            scenario = 0;
        } else {
            scenario = Integer.parseInt(args[0]);
        }
        Simulator.runScenario(0);

        initWaterMap();

		/*
        Timer updateLand = new Timer();
        updateLand.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Simulator.nextStep(updateInterval/1_000);
                landscapeWindow.repaint();
            }
        }, 0, (int) updateInterval);
		 */
        // 1.00.41020

        Timer updateLand = new Timer();
        updateLand.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (updateSim) {
                    {
                        Simulator.nextStep(simSpeed);
                        landscapeWindow.repaint();
                    }
                }
            }
        }, 0, (int) updateInterval);

    }

    /**
     * Inicializuje okno s krajinou
     **/
    private static void initWaterMap() {
        LandscapeWindow lw = new LandscapeWindow(WIDTH_LAND, HEIGHT_LAND,
                Simulator.getDimension(), Simulator.getData(),
                Simulator.getDelta(), Simulator.getWaterSources());
        landscapeWindow = lw.create();
        landscapeWindow.setTitle(windowTitle);
        landscapeWindow.setVisible(true);
    }

    // 1.00.41020

    public static double getSimSpeed() {
        return simSpeed;
    }

    public static void setSimSpeed(double value) {
        value /= 1_000;

        if (value > 1e-8 && value < 1d)
            simSpeed = value;
        else
            throw new RuntimeException("Failed to assign value! + \n + Simulation speed must be between (1e-8;1)");
    }

    public static void setSimUpdate(boolean status) {
        updateSim = status;
    }

    public static boolean isUpdatingSim() {
        return updateSim;
    }
}
