import graphs.FrameData;
import graphs.WaterLevelGraphData;
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

    private static int scenario; // aktivni scenar

    private static JFrame landscapeWindow; // ulozi referenci na okno s krajinou

    private final static String WINDOW_TITLE = "WaterFlowSim - A19B0069P Martin Jakubašek";

    // zakladni rozmery okna s krajinou (hlavniho okna aplikace)
    private static final int WIDTH_LAND = 600;
    private static final int HEIGHT_LAND = 600;

    private static double updateInterval = 100; // inteval aktualizace krajiny v ms

    // atributy ovlivnujici rychlost simulace
    public static final int DEFAULT_SIM_SPEED = 200;
    private static double simSpeed = DEFAULT_SIM_SPEED / 1_000d;

    // aktualni cas (cas simulace) v sekundach od spusteni simulatoru
    private static double actualTime = 0;

    private static boolean updateSim = true; // true -> simulace se repaintuje -> false ne

    // odkaz na jedinou instanci tridy
    private static final WaterLevelGraphData WATER_LEVEL_DATA = WaterLevelGraphData.getInstance();

    /**
     * Hlavni metoda programu, spusti program, ktery vykresli krajinu a
     * pravidelne ji aktualizuje
     *
     * @param args pouzit pro vyber scenare (0-3)
     */
    public static void main(String[] args) {

        Simulator.runScenario(getActScenario(args));
        initWaterMap();

        setupAppLoop();
    }

    /**
     * Vrati aktivni scenar, ktery byl zadan v prikazove radce
     *
     * @param args args z prikazove radky
     * @return vrati aktivni scenar
     */
    private static int getActScenario(String[] args) {
        if (args.length == 0) {
            scenario = 0;
        } else {
            scenario = Integer.parseInt(args[0]);
        }
        return scenario;
    }

    /**
     * Inicializuje okno s krajinou
     **/
    private static void initWaterMap() {
        LandscapeWindow lw = new LandscapeWindow(WIDTH_LAND, HEIGHT_LAND,
                Simulator.getDimension(), Simulator.getData(),
                Simulator.getDelta(), Simulator.getWaterSources());
        landscapeWindow = lw.create();
        landscapeWindow.setTitle(WINDOW_TITLE);
        landscapeWindow.setVisible(true);
    }

    /**
     * Vytvori a spusti hlavni smycku aplikace
     */
    private static void setupAppLoop() {
        Timer updateLand = new Timer();
        updateLand.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (updateSim) {
                    update();
                    draw();
                }
                //draw();
            }
        }, 0, (int) updateInterval);
    }

    /**
     * Sekce pracujici s grafikou
     */
    private static void draw() {
        landscapeWindow.repaint();
    }

    /**
     * Vypocetni sekce
     */
    private static void update() {
        Simulator.nextStep(simSpeed);

        actualTime += simSpeed;
        WATER_LEVEL_DATA.addFrame(new FrameData(Simulator.getDimension(), Simulator.getData(), actualTime));
    }

    /**
     * Vrati rychlost simulace
     *
     * @return rychlost simulace
     */
    public static double getSimSpeed() {
        return simSpeed;
    }

    /**
     * Nastavi rychlost simulace
     *
     * @param value nova rychlost simulace v sekundach
     */
    public static void setSimSpeed(double value) {
        value /= 1_000;

        if (value > 1e-8 && value < 1d)
            simSpeed = value;
        else
            throw new RuntimeException("Failed to assign value! + \n + Simulation speed must be between (1e-8;1)");
    }

    /**
     * Urci zda se bude simulace aktualizovat
     *
     * @param status true -> bude se aktualizovat
     */
    public static void setSimUpdate(boolean status) {
        updateSim = status;
    }

    /**
     * Vrati zda se simulace aktualizuje
     *
     * @return aktualizu je se simulace?
     */
    public static boolean isUpdatingSim() {
        return updateSim;
    }
}
