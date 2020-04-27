import javax.swing.*;
import java.util.Hashtable;

/**
 * Trida vytvari metodou create() JPanel obsahujici vsechny potrebne prvky na kontrolu rychlosti simulace a
 * to konkretne:
 * -> zmenu rychlosti
 * -> pauzu
 *
 * @author Martin Jakubasek
 * @version 0.410.20
 * @since 4.10.2020
 */
public class SpeedControlsPanel {

    // zamezeni vytvareni nove instance
    private SpeedControlsPanel() {}

    /**
     * Vytvori JPanel obsahujici prvky potrebne pro ovladani rychlosti simulace
     *
     * @return JPanel kontrolujici rychlost simulace
     */
    public static JPanel createPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JSlider slider = getSimSpeedJS();
        root.add(slider);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        buttons.add(getSimStopJBTN());
        buttons.add(getSimSpeedResetJBTN(slider));

        root.add(buttons);

        return root;
    }

    /**
     * Vytvori slider, ktery slouzi pro manipulaci rychsloti simulace
     *
     * @return slider ovlivnujici rychlost simulace
     */
    private static JSlider getSimSpeedJS() {
        final int min = 1;
        final int max = 900;
        final int init = L01_SpusteniSimulatoru.DEFAULT_SIM_SPEED;

        JSlider simSpeed = new JSlider(JSlider.HORIZONTAL, min, max, init);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(100, new JLabel("Pomaleji"));
        labelTable.put(800, new JLabel("Rychleji"));

        simSpeed.setLabelTable(labelTable);
        simSpeed.setPaintLabels(true);

        simSpeed.addChangeListener(changeEvent -> L01_SpusteniSimulatoru.setSimSpeed(simSpeed.getValue()));

        return simSpeed;
    }

    /**
     * Tlacitko pauzy casu simulace
     *
     * @return tlacitko stop/play
     */
    private static JButton getSimStopJBTN() {
        JButton playStop = new JButton("\u23EF");

        playStop.addActionListener(actionEvent -> L01_SpusteniSimulatoru.setSimUpdate(!L01_SpusteniSimulatoru.isUpdatingSim()));

        return playStop;
    }

    /**
     * Tlacitko vrati rychlost na puvodni rychlost simulace
     *
     * @param speedControl jslider resetujici svoji value na vychozi rychlost
     * @return vrati tlacitko, ktere vrati rychlost na puvodni
     */
    private static JButton getSimSpeedResetJBTN(JSlider speedControl) {
        JButton resetSpeed = new JButton("výchozí rychlost");

        resetSpeed.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimSpeed(L01_SpusteniSimulatoru.DEFAULT_SIM_SPEED);
            speedControl.setValue(L01_SpusteniSimulatoru.DEFAULT_SIM_SPEED);
        });

        return resetSpeed;
    }
}
