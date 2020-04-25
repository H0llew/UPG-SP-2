package utils;

import javax.swing.*;

/**
 * Vytvori zakladni okno
 *
 * @author Martin Jakubasek
 * @version 1.0
 * @since 22.3.2020
 */
public class Window {

    private Window() {
    }

    /**
     * Vytvori nove prazdne okno;
     */
    public static JFrame createBasicWindow() {
        return createBasicWindow(0, 0, "new utils.Window");
    }

    /**
     * Vytvori nove prazdne okno o zadanych rozmerech
     *
     * @param x sirka okna v pixelech
     * @param y vyska okna v pixelech
     */
    public static JFrame createBasicWindow(int x, int y, String windowTitle) {
        JFrame jframe = new JFrame();

        jframe.setTitle(windowTitle);
        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return jframe;
    }
}
