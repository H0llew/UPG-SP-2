import javafx.scene.control.Button;
import terrain.TerrainHeightDataPanel;
import utils.Window;
import waterflowsim.Cell;
import waterflowsim.Vector2D;
import waterflowsim.WaterSourceUpdater;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;

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

    private final static String PRINT_TEXT = "Tisk";
    private final static String SVG_TEXT = "Ulož do SVG";
    private final static String PNG_TEXT = "Ulož do PNG";

    private final static String WIDTH_TEXT = "šířka";
    private final static String HEIGHT_TEXT = "výška";
    private final static String INPUT_TITLE_TEXT = "Zadejte požadovanou výšku a šířku bitmapy";

    private final static String FILE_NAME_TEXT = "název";

    private final static String INPUT_ERROR_TEXT = "Prosím zadejte jen celá čísla.";
    private final static String INPUT_WARNING_TEXT = "Prosím zadejte celá čísla > 0";

    private final static String SUCC_SAVED_IMG_TEXT = "Obrázek úspěšně uložen";
    private final static String SUCC_FAILED_SAVE_IMG_TEXT = "Obrázek nebylo možné uložit";

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
    public JFrame create() {

        JFrame jFrame = Window.createBasicWindow(width, height, "new utils.Window");

        landscapeMap = new DrawLandscape(landDimPix, landData, delta, waterSources);
        landscapeMap.setPreferredSize(new Dimension(width, height));

        // 1.00.41020

        jFrame.setLayout(new BorderLayout());

        jFrame.add(landscapeMap, BorderLayout.CENTER);
        jFrame.add(SpeedControlsPanel.createPanel(), BorderLayout.SOUTH);

        TerrainHeightDataPanel tHDP = TerrainHeightDataPanel.getInstance();
        jFrame.add(tHDP.createPanel(), BorderLayout.EAST);

        jFrame.add(exportButtons(), BorderLayout.NORTH);

        jFrame.pack();

        return jFrame;
    }

    /**
     * Tlacitka pro tlacitka pro export krajiny (ulozeni do svg/bitmapy, tisk)
     *
     * @return tlacitka pro export
     */
    private JPanel exportButtons() {
        JButton print = new JButton(PRINT_TEXT);
        print.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimUpdate(false);
            printLandscape();
            L01_SpusteniSimulatoru.setSimUpdate(true);
        });

        JButton svg = new JButton(SVG_TEXT + " s vyberem slozky");
        svg.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimUpdate(false);
            saveSVGChooser(landscapeMap.getSvgLandscape());
            L01_SpusteniSimulatoru.setSimUpdate(true);
        });

        JButton svg2 = new JButton(SVG_TEXT);
        svg2.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimUpdate(false);
            saveSVG(landscapeMap.getSvgLandscape());
            L01_SpusteniSimulatoru.setSimUpdate(true);
        });

        JButton png = new JButton(PNG_TEXT);
        png.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimUpdate(false);
            saveBitmap();
            L01_SpusteniSimulatoru.setSimUpdate(true);
        });

        JButton png2 = new JButton(PNG_TEXT + " s vyberem slozky");
        png2.addActionListener(actionEvent -> {
            L01_SpusteniSimulatoru.setSimUpdate(false);
            saveBitmapChooser();
            L01_SpusteniSimulatoru.setSimUpdate(true);
        });

        JPanel root = new JPanel();
        GridLayout grid = new GridLayout(2,3);
        root.setLayout(grid);

        root.add(print);
        root.add(svg);
        root.add(svg2);
        root.add(new JLabel(" "));
        root.add(png2);
        root.add(png);

        return root;
    }

    // Tisk

    /**
     * Vytvori printer job a vytiskne pomoci nej krajiny (s vodnimi zdroji)
     */
    private void printLandscape() {
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            job.setPrintable(landscapeMap);
            try {
                job.print();
            } catch (PrinterException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Ukladani SVG

    /**
     * Vytvori svg soubor pomoci knihovny jfreesvg.
     * Na jmeno a ulozeni souboru pouzije JFileChooser
     * Informaci ze se soubor ulozil/neulozil vypise pomoci dialogu
     *
     * @param svg svg string
     */
    private void saveSVGChooser(String svg) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".svg", ".svg");
        fileChooser.setFileFilter(filter);
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                String path = file.getPath();
                if (!path.endsWith(".svg")) {
                    path += ".svg";
                }
                try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
                    bufferedWriter.write(svg);
                    bufferedWriter.flush();
                    JOptionPane.showMessageDialog(null, path, SUCC_SAVED_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(null, path, SUCC_FAILED_SAVE_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                    //e.printStackTrace();
                }
            }
        }
    }

    /**
     *  Ulozi svg pomoci jfreesvg
     *  Na nazev svg se zepta pomoci dialogoveho okna a ulozi jej do rootu
     *  Informaci ze se soubor ulozil/neulozil vypise pomoci dialogu
     *
     * @param svg svg string
     */
    private void saveSVG(String svg) {
        String name = getDialogNameInput();
        if (name != null) {
            if (!name.endsWith(".svg")) {
                name += ".svg";
            }
            File file = new File(name);
            if (!file.exists()) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                    bufferedWriter.write(svg);
                    bufferedWriter.flush();
                    JOptionPane.showMessageDialog(new JFrame(), "Ulozeno: " + file.getAbsolutePath());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, file.getAbsolutePath(), SUCC_FAILED_SAVE_IMG_TEXT, JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    // Ukladani bitmapy v png

    /**
     * Metoda ulozi bitmapu krajiny (format png) pomoci
     * dialogoveho okna, ktere se zepta na velikost a nazev bitmapy.
     * Pote se pokusi ulozit bitmapu do rootu aplikace
     * Pokud se ulozeni povede/nepovede oznami tuto informaci dialogem
     */
    private void saveBitmap() {
        String[] input = getInputDialogWithFileName();
        if (input != null) {
            int width = 0;
            int height = 0;
            String name;
            try {
                width = Integer.parseInt(input[0]);
                height = Integer.parseInt(input[1]);
                name = input[2];

                if (width > 0 && height > 0) {
                    // ulož
                    BufferedImage img = landscapeMap.getNonBorderImage(width, height);
                    String path = name;
                    if (!path.endsWith(".png")) {
                        path += ".png";
                    }
                    if (ImageIO.write(img, "png", new File(path))) {
                        JOptionPane.showMessageDialog(null, path, SUCC_SAVED_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, path, SUCC_FAILED_SAVE_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else {
                    // malá velikost
                    JOptionPane.showMessageDialog(new JFrame(),
                            INPUT_WARNING_TEXT,
                            "Input warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            catch (Exception e) {
                //e.printStackTrace();
                // chybný input
                JOptionPane.showMessageDialog(new JFrame(),
                        INPUT_ERROR_TEXT,
                        "Input error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Metoda ulozi bitmapu krajiny (format png) pomoci
     * dialogoveho okna, ktere se zepta na velikost bitmapy.
     * Pote se pokusi ulozit bitmapu do zvoleneho adresare pomoci JFileChooseru
     * Pokud se ulozeni povede/nepovede oznami tuto informaci dialogem
     */
    private void saveBitmapChooser() {
        String[] input = getDialogInput();
        if (input != null) {
            int width = 0;
            int height = 0;
            try {
                width = Integer.parseInt(input[0]);
                height = Integer.parseInt(input[1]);

                if (width > 0 && height > 0) {
                    // ulož
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("png", ".png");
                    chooser.setFileFilter(filter);
                    int response = chooser.showSaveDialog(null);
                    if (response == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        if (file != null) {
                            BufferedImage img = landscapeMap.getNonBorderImage(width, height);
                            String path = file.getPath();
                            if (!path.endsWith(".png")) {
                                path += ".png";
                            }
                            if (ImageIO.write(img, "png", new File(path))) {
                                JOptionPane.showMessageDialog(null, path, SUCC_SAVED_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                            }
                            else {
                                JOptionPane.showMessageDialog(null, path, SUCC_FAILED_SAVE_IMG_TEXT, JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
                else {
                    // malá velikost
                    JOptionPane.showMessageDialog(new JFrame(),
                            INPUT_WARNING_TEXT,
                            "Input warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            catch (Exception e) {
                //e.printStackTrace();
                // chybný input
                JOptionPane.showMessageDialog(new JFrame(),
                        INPUT_ERROR_TEXT,
                        "Input error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Ziska input z 1 textfieldu
     *
     * @return input z 1 textfieldu
     */
    private String getDialogNameInput() {
        JTextField name = new JTextField(5);

        JPanel optionsPanel = new JPanel();
        optionsPanel.add(new JLabel(FILE_NAME_TEXT + ":"));
        optionsPanel.add(name);

        int response = JOptionPane.showConfirmDialog(null, optionsPanel, INPUT_TITLE_TEXT, JOptionPane.OK_CANCEL_OPTION);
        if (response == JOptionPane.OK_OPTION) {
            return name.getText();
        }
        return null;
    }

    /**
     * Ziska input ze 2 textfieldu
     *
     * @return input ze 2 textfieldu, jinak null
     */
    private String[] getDialogInput() {
        JTextField width = new JTextField(5);
        JTextField height = new JTextField(5);

        JPanel optionsPanel = new JPanel();
        optionsPanel.add(new JLabel(WIDTH_TEXT + ":"));
        optionsPanel.add(width);
        optionsPanel.add(Box.createHorizontalStrut(8));
        optionsPanel.add(new JLabel(HEIGHT_TEXT + ":"));
        optionsPanel.add(height);

        int response = JOptionPane.showConfirmDialog(null, optionsPanel, INPUT_TITLE_TEXT, JOptionPane.OK_CANCEL_OPTION);
        if (response == JOptionPane.OK_OPTION) {
            return new String[] {width.getText(), height.getText()};
        }
        return null;
    }

    /**
     * Ziska input ze 3 textfieldu
     *
     * @return input ze 3 textfieldu, jinak null
     */
    private String[] getInputDialogWithFileName() {
        JTextField width = new JTextField(5);
        JTextField height = new JTextField(5);
        JTextField fileName = new JTextField(5);

        JPanel optionsPanel = new JPanel();
        optionsPanel.add(new JLabel(WIDTH_TEXT + ":"));
        optionsPanel.add(width);
        optionsPanel.add(Box.createHorizontalStrut(8));
        optionsPanel.add(new JLabel(HEIGHT_TEXT + ":"));
        optionsPanel.add(height);
        optionsPanel.add(Box.createHorizontalStrut(8));
        optionsPanel.add(new JLabel(FILE_NAME_TEXT + ":"));
        optionsPanel.add(fileName);

        int response = JOptionPane.showConfirmDialog(null, optionsPanel, INPUT_TITLE_TEXT, JOptionPane.OK_CANCEL_OPTION);
        if (response == JOptionPane.OK_OPTION) {
            return new String[] {width.getText(), height.getText(), fileName.getText()};
        }
        return null;
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
