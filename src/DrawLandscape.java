import graphs.WaterLevelGraph;
import graphs.WaterLevelGraphWindow;
import terrain.TerrainHeightData;
import terrain.TerrainHeightDataPanel;
import waterflowsim.Cell;
import waterflowsim.Vector2D;
import waterflowsim.WaterSourceUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Trida slouzi pro slpneni prvniho zadani UPG seminarni prace
 * Vykresli do okna krajinu, upravenou podle zadani a sipky vodnich zdroju
 *
 * @author Martin Jakubasek
 * @version 1.0
 * @since 22.3.2020
 */
public class DrawLandscape extends JPanel {

    // Atributy prevzate ze Simulatoru
    private Point2D landDimPix; // velikost krajiny z Simulator.getDimension()
    private Cell[] landData; // data krajiny z Simularot.getData()
    private WaterSourceUpdater[] waterSources; // data vodnich zdroju z Simulator.GetWaterSources()

    // Atributy tridy
    private Color waterColor = Color.BLUE; // barva vody
    private Color arrowColor = Color.BLACK; // barva sipky
    private Color textColor = Color.BLACK; // barva textu

    private boolean isFirstDraw = true; // prvni kresba

    // Atributy pro manipulaci se zobrazenim
    private Point2D offset; // offset krajiny od horniho leveho rohu
    private double scale; // scale pomoci nehoz se zmensuje/zvetsuje obrazek, tak aby se vesel do okna
    private Point2D deltaScale; // scale pomoci nehoz se zmeni meritko krajiny

    private CalculateLandscape cl; // trida pro ziskani vypoctu nutnych pro spravnou vizualizaci

    // Atributy pro vizualizaci sipky
    private int arrowLength; // velikost tela sipky
    private int arrowThickness = 4; // tloustka hlavicky sipky
    private int TEXT_SIZE = 10; // velikost textu
    private final Point2D ARROW_OFFSET = new Point2D.Double(-10, -10); // offset, aby sipka nebyla primo na zdroji
    private final int ARROW_LENGTH_EXT = 5; // trosku roztahne sipku

    // 1.00.41020
    private Color terrainColor = Color.GREEN;

    /*
    private CalculateTerrainHeight cTH;
    private double[] levels;
     */
    private TerrainHeightData tHD = TerrainHeightData.getData();

    // rectangle reprezentuje okraje obrazku krajiny
    private Rectangle2D rectangle2D;

    // atributy pro zobrazeni inputu uzivatele a urceni souradnic v krajine, ktere byly vybrany
    // reprezentuje bod, kam uzivatel kliknul
    private Point2D pressedCoord;
    // nasledujici atributy slouzi ke spravnemu urceni souradnic krajiny pro graf
    private Point2D mouseStart;
    private Point2D mouseEnd;
    // slouzi pro spravne vykrsleni inputu
    private Point2D mouseStartReal;
    private Point2D mouseEndReal;

    // urcuje zda se ma vykreslit obdelnik inputu
    private boolean drawRect = false;
    // urcuje zda byl klik proveden na obrazku krajiny
    private boolean wasPressedInImage = false;

    private double startingWidth;
    private double startingHeight;

    /**
     * Nacte vsechna potrebna data
     *
     * @param landDimPix   dimenze krajiny
     * @param landData     cell v krajine
     * @param delta        delta
     * @param waterSources vodni zdroje
     */
    public DrawLandscape(Vector2D<Integer> landDimPix, Cell[] landData, Vector2D<Double> delta,
                         WaterSourceUpdater[] waterSources) {

        this.landDimPix = new Point2D.Double(landDimPix.x, landDimPix.y);
        this.landData = landData;

        cl = new CalculateLandscape(landDimPix, delta);
        deltaScale = cl.deltaScale;

        // pro sipky
        this.waterSources = waterSources;

        addMouseListeners();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        scale = cl.getScale(this.getWidth(), this.getHeight());

        Graphics2D g2D = (Graphics2D) g;

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(new Font("Arial", Font.PLAIN, (int) (TEXT_SIZE * scale)));

        if (isFirstDraw) {
            g2D.setFont(new Font("Arial", Font.PLAIN, (int) (TEXT_SIZE)));
            arrowLength = getArrowLength(g2D);
            cl.createArrowDimension(landDimPix, ARROW_OFFSET, arrowLength, waterSources);
            isFirstDraw = false;

            /*
            cTH = new CalculateTerrainHeight(new Vector2D<Integer>((int)landDimPix.getX(), (int)landDimPix.getY()), landData);
            levels = cTH.getTerrainLevels(5);
             */
            tHD.calculateTerrainHeight(new Vector2D<Integer>((int) landDimPix.getX(), (int) landDimPix.getY()), landData, 10);
            TerrainHeightDataPanel.getInstance().addChartPanel();
        }

        AffineTransform old = g2D.getTransform();

        int finalWidth = (int) ((landDimPix.getX() * deltaScale.getX()) * scale);
        int finalHeight = (int) ((landDimPix.getY() * deltaScale.getY()) * scale);

        if (cl.getMinCoordX() < 0) {
            g2D.translate(Math.abs(cl.getMinCoordX() * scale), 0);
            rectangle2D = new Rectangle2D.Double(Math.abs(cl.getMinCoordX() * scale), 0, finalWidth, finalHeight);
        }
        if (cl.getMinCoordY() < 0) {
            g2D.translate(0, Math.abs(cl.getMinCoordY() * scale));
            rectangle2D = new Rectangle2D.Double(0, Math.abs(cl.getMinCoordY() * scale), finalWidth, finalHeight);
        }

        //g2D.draw(rectangle2D);

        drawWaterLayer(g2D);
        drawWaterSources(g2D);

        g2D.setTransform(old);

        if (drawRect) {
            double width = Math.abs(-1 * mouseStartReal.getX() + mouseEndReal.getX());
            double height = Math.abs(-1 * mouseStartReal.getY() + mouseEndReal.getY());
            g2D.setColor(Color.RED);

            g2D.draw(new Rectangle2D.Double(mouseStartReal.getX(), mouseStartReal.getY(), width, height));
        }
    }

    /**
     * Vykresli rastrovy obrazek vody v krajine.
     * Obrazek bude roztazen, tak aby vyhovoval delte a
     * zaroven se vesel do okna a nedoslo ke zkresleni
     */
    private void drawWaterLayer(Graphics2D g2D) {

        /* Metoda pomoci metody getWaterImage vykresli na platno g2D
           rastrovy obrazek, a ten nasledne zvetsi/zmensi,
           tak aby se vesel do okna */

        BufferedImage landscapeImage = getLandscapeImage(); //obrázek krajiny

        //scale = cl.getScale(this.getWidth(), this.getHeight());
        ;

        int finalWidth = (int) ((landscapeImage.getWidth() * deltaScale.getX()) * scale);
        int finalHeight = (int) ((landscapeImage.getHeight() * deltaScale.getY()) * scale);

        //int finalWidth = (int) ((landscapeImage.getWidth()));
        //int finalHeight = (int) ((landscapeImage.getHeight()));

        g2D.drawImage(landscapeImage, 0, 0,
                finalWidth, finalHeight, null); //nakresli na plátno krajinu
        //rectangle2D = new Rectangle2D.Double(100, 100, finalWidth, finalHeight);
        //g2D.draw(rectangle2D);
    }

    // metody pro vykresleni krajiny

    /**
     * Vykresli na platno vodu v krajine z pole {@link waterflowsim.Cell} bunek
     * Jedna bunka == 1 pixel
     *
     * @param g2D {@link Graphics2D}
     */
    private void drawWater(Graphics2D g2D) {

        // vykresleni probeha pomoci 2 vnorenych loopu -> jeden pro y a druhy pro x (osy)
        for (int y = 0; y < landDimPix.getY(); y++) {
            for (int x = 0; x < landDimPix.getX(); x++) {
                if (!landData[((int) landDimPix.getX()) * y + x].isDry()) {
                    Line2D cellPoint = new Line2D.Double(x, y, x, y);
                    g2D.draw(cellPoint);
                }
            }
        }
    }

    /**
     * Vykresli na platno g2D teren v krajine z pole {@link waterflowsim.Cell} bunek
     * Jedna bunka == 1 pixel
     *
     * @param g2D {@link Graphics2D}
     */
    private void drawTerrain(Graphics2D g2D) {

        // vykresleni probeha pomoci 2 vnorenych loopu -> jeden pro y a druhy pro x (osy)
        for (int y = 0; y < landDimPix.getY(); y++) {
            for (int x = 0; x < landDimPix.getX(); x++) {

                Cell actualCell = landData[((int) landDimPix.getX()) * y + x];
                if (actualCell.isDry()) {
                    Line2D cellPoint = new Line2D.Double(x, y, x, y);

                    double actualHeight = actualCell.getTerrainLevel();
                    for (int i = tHD.getTerrainLevels().length - 1; i >= 0; i--) {
                        if (actualHeight >= tHD.getTerrainLevels()[i]) {
                            Color actualColor = tHD.getColors()[i];
                            g2D.setColor(actualColor);
                            break;
                        }
                    }


                    g2D.draw(cellPoint);
                }
            }
        }
    }

    // metody pro vykresleni sipek

    /**
     * Pro vsechny vodni zdroje v krajine vykresli sipku a label ve smeru toku
     *
     * @param g2D {@link Graphics2D}
     */
    private void drawWaterSources(Graphics2D g2D) {

        //projede loopem vsechny water source a vykresli sipku + popisek
        for (WaterSourceUpdater waterSource : waterSources) {
            int y = (int) (waterSource.getIndex() / landDimPix.getX());
            int x = (int) (waterSource.getIndex() % landDimPix.getX());

            Point2D position = new Point2D.Double((x + ARROW_OFFSET.getX()) * deltaScale.getX() * scale,
                    (y + ARROW_OFFSET.getY()) * deltaScale.getY() * scale);
            Vector2D<Double> gradient = landData[waterSource.getIndex()].getGradient();
            gradient = new Vector2D<>(-gradient.x, -gradient.y);

            drawWaterFlowLabel(position, gradient, waterSource.getName(), g2D);
        }
    }

    /**
     * Vykresli sipku v zadanem bode a smerovem vektoru s popiskem name
     *
     * @param position pocatek
     * @param dirFlow  smerovy vektor
     * @param name     jmeno vodniho zdroje
     * @param g2D      {@link Graphics2D}
     */
    private void drawWaterFlowLabel(Point2D position, Vector2D<Double> dirFlow, String name, Graphics2D g2D) {
        drawArrow(position, dirFlow, g2D);
        drawSourceLabel(dirFlow, position, name, g2D);
    }

    /**
     * Vykresli sipku pomoci zadaneho pocatecniho bodu a smeroveho vektoru
     *
     * @param startPos  pocatek sipky
     * @param direction smerovy vektor sipky
     * @param g2D       {@link Graphics2D}
     */
    private void drawArrow(Point2D startPos, Vector2D<Double> direction, Graphics2D g2D) {

        // nejprve je potreba zajistit aby vektor mel velikost 1
        double magnitude = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        Vector2D<Double> normalization = new Vector2D<>(direction.x / magnitude, direction.y / magnitude);

        double arrowLength = this.arrowLength * scale;

        // tvorba "tela" sipky -> sipka pujde z bodu A do bodu B (bod B zjisten pomoci smeroveho vektoru +
        // konstanty urcujici velikost sipky
        Vector2D<Double> headings = new Vector2D<>(normalization.x * arrowLength, normalization.y * arrowLength);
        // smerovy vektor

        // za bod A se povazuje startPos - konstanta offset -> aby sipka byla nad zdrojem a ne primo na nem
        Point2D pointA = new Point2D.Double(startPos.getX(), startPos.getY());
        // bod B
        Point2D pointB = new Point2D.Double(startPos.getX() + headings.x, startPos.getY() + headings.y);

        Line2D body = new Line2D.Double(pointA, pointB); // usecka tela sipky

        //tvorba "hlavicky" sipky
        Vector2D<Double> perpNormalization = new Vector2D<>(-normalization.y, normalization.x);

        Point2D pointC = new Point2D.Double(pointB.getX() + (perpNormalization.x * arrowThickness * 2), //bod C
                pointB.getY() + (perpNormalization.y * arrowThickness * 2));

        Point2D pointD = new Point2D.Double(pointB.getX() - (perpNormalization.x * arrowThickness * 2), //bod D
                pointB.getY() - (perpNormalization.y * arrowThickness * 2));

        Point2D pointE = new Point2D.Double(pointB.getX() + headings.x / 4, //bod E
                pointB.getY() + headings.y / 4);

        Path2D head = new Path2D.Double();
        head.moveTo(pointC.getX(), pointC.getY());
        head.lineTo(pointD.getX(), pointD.getY());
        head.lineTo(pointE.getX(), pointE.getY());
        head.lineTo(pointC.getX(), pointC.getY());

        g2D.setColor(arrowColor);
        Stroke old = g2D.getStroke();
        g2D.setStroke(new BasicStroke(arrowThickness));
        g2D.draw(body);
        g2D.setStroke(old);
        g2D.fill(head);
    }

    /**
     * Vykresli text pro sipku
     *
     * @param headings smerovy vektor
     * @param position pocatek
     * @param name     jmeno
     * @param g2D      {@link Graphics2D}
     */
    private void drawSourceLabel(Vector2D<Double> headings, Point2D position, String name, Graphics2D g2D) {
        // normalizace smeroveho vektoru
        double magnitude = Math.sqrt(headings.x * headings.x + headings.y * headings.y);
        Vector2D<Double> normalization = new Vector2D<>(headings.x / magnitude, headings.y / magnitude);

        // zjistovani uhlu
        double angle = Math.toDegrees(Math.atan2(normalization.y, normalization.x));
        Point2D pos;

        FontMetrics fm = g2D.getFontMetrics(); // kvuli uprave textu na sipce
        double textOffset = (arrowLength * scale - fm.stringWidth(name));

        if ((angle > 90 && angle < 270) || (angle < -90 && angle > -270)) {
            pos = new Point2D.Double(position.getX() + (normalization.x * ((arrowLength * scale) - textOffset / 2)),
                    position.getY() + (normalization.y * ((arrowLength * scale) - textOffset / 2)));
            angle += 180; //kvůli relativně správnému zovrázení textu
        } else {
            pos = new Point2D.Double(position.getX() + (normalization.x * textOffset / 2.0),
                    position.getY() + (normalization.y * textOffset / 2.0));
        }

        AffineTransform old = g2D.getTransform();
        g2D.setColor(textColor);

        g2D.translate(pos.getX(), pos.getY());

        g2D.rotate(Math.toRadians(angle));
        g2D.translate(0, -arrowThickness);
        g2D.drawString(name, 0, 0);

        g2D.setTransform(old);
    }

    // get set private

    /**
     * Vytvori rastrovy obrazek krajiny
     *
     * @return rastrovy obrazek krajiny
     */
    private BufferedImage getLandscapeImage() {

        BufferedImage waterImage = getWaterImage();
        BufferedImage terrainImage = getTerrainImage();

        BufferedImage landscapeImage = new BufferedImage((int) landDimPix.getX(), (int) landDimPix.getY(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = landscapeImage.createGraphics();

        g2D.drawImage(waterImage, 0, 0,
                (int) landDimPix.getX(), (int) landDimPix.getY(),
                null);
        g2D.drawImage(terrainImage, 0, 0,
                (int) landDimPix.getX(), (int) landDimPix.getY(),
                null);

        return landscapeImage;
    }

    /**
     * Vytvori rastrovy obrazek vody v krajine
     *
     * @return rastrovy obrazek vody v krajine
     */
    private BufferedImage getWaterImage() {

        BufferedImage waterImage = new BufferedImage((int) landDimPix.getX(), (int) landDimPix.getY(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = waterImage.createGraphics();
        g2D.setColor(waterColor);

        drawWater(g2D);

        return waterImage;
    }

    /**
     * Vytvori rastrovy obrazek terenu krajiny
     *
     * @return rastrovy obrazek terenu krajiny
     */
    private BufferedImage getTerrainImage() {

        BufferedImage terrainImage = new BufferedImage((int) landDimPix.getX(), (int) landDimPix.getY(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = terrainImage.createGraphics();
        g2D.setColor(terrainColor);

        drawTerrain(g2D);

        return terrainImage;
    }

    /**
     * Vrati delku tela sipky, kde delka je urcena sirkou nejdelsiho textu
     *
     * @param g2D {@link Graphics2D}
     * @return delka sipky
     */
    private int getArrowLength(Graphics2D g2D) {

        int max = 0;
        FontMetrics fm = g2D.getFontMetrics();

        for (WaterSourceUpdater source : waterSources) {
            int arrowLength = fm.stringWidth(source.getName());
            max = Math.max(max, arrowLength);
        }

        return max + (2 * ARROW_LENGTH_EXT);
    }

    // Listeners methods

    /**
     * Prida mouse listenery slouzici pro spracovani inputu (tedy mouse released,pressed a mouse dragged)
     */
    private void addMouseListeners() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (rectangle2D.contains(mouseEvent.getPoint())) {
                    // System.out.println("Mouse pressed in bounds");
                    wasPressedInImage = true;

                    pressedCoord = new Point2D.Double(mouseEvent.getPoint().getX(), mouseEvent.getPoint().getY());
                    processInput((int) mouseEvent.getPoint().getX(), (int) mouseEvent.getPoint().getY(),
                            (int) mouseEvent.getPoint().getX(), (int) mouseEvent.getPoint().getY());

                    //System.out.println(mouseStartReal.toString());
                    //System.out.println(mouseEndReal.toString());

                    drawRect = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (wasPressedInImage) {
                    //System.out.println("Mouse released");
                    if (rectangle2D.contains(mouseEvent.getPoint())) {
                        mouseEndReal = new Point2D.Double(mouseEvent.getPoint().getX(), mouseEvent.getPoint().getY());
                        processInput((int) pressedCoord.getX(), (int) pressedCoord.getY(),
                                (int) mouseEvent.getPoint().getX(), (int) mouseEvent.getPoint().getY());
                    }

                    Point2D clickPoint = mouseEndReal;
                    double x = clickPoint.getX() - rectangle2D.getX();
                    double y = clickPoint.getY() - rectangle2D.getY();
                    clickPoint = new Point2D.Double(x, y);
                    clickPoint = new Point2D.Double(clickPoint.getX() / deltaScale.getX() / scale, clickPoint.getY() / deltaScale.getY() / scale);
                    mouseEnd = new Point2D.Double(clickPoint.getX(), clickPoint.getY());

                    clickPoint = mouseStartReal;
                    x = clickPoint.getX() - rectangle2D.getX();
                    y = clickPoint.getY() - rectangle2D.getY();
                    clickPoint = new Point2D.Double(x, y);
                    clickPoint = new Point2D.Double(clickPoint.getX() / deltaScale.getX() / scale, clickPoint.getY() / deltaScale.getY() / scale);
                    mouseStart = new Point2D.Double(clickPoint.getX(), clickPoint.getY());

                    //System.out.println("Zacinam na: " + mouseStart.toString() + "Jdu na: " + mouseEnd.toString());
                    //System.out.println("Ale v realnych se jedna o: " + mouseStartReal.toString() + "Jdu na: " + mouseEndReal.toString());

                    WaterLevelGraph waterLevelGraph = new WaterLevelGraph((int) mouseStart.getX(), (int) mouseStart.getY(), (int) mouseEnd.getX(), (int) mouseEnd.getY());
                    WaterLevelGraphWindow.create("Graf vodnich hladin", waterLevelGraph);
                }
                wasPressedInImage = false;
                drawRect = false;
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                super.mouseDragged(mouseEvent);
                if (wasPressedInImage && rectangle2D.contains(mouseEvent.getPoint())) {
                    processInput((int) pressedCoord.getX(), (int) pressedCoord.getY(),
                            (int) mouseEvent.getPoint().getX(), (int) mouseEvent.getPoint().getY());

                    //System.out.println(mouseStartReal.toString());
                    //System.out.println(mouseEndReal.toString());
                }
            }
        });
    }

    /**
     * Zpracuje input, tak aby nedochazelo k tomu ze souradnice v mouseEndReal byla vys nez souradnice v mouseStartReal
     *
     * @param x  x-ova souradnice bodu
     * @param y  y-ova souradnice bodu
     * @param x1 x-ova souradnice bodu
     * @param y1 y-ova souradnice bodu
     */
    private void processInput(int x, int y, int x1, int y1) {
        int placeHolder;
        // začínám na [1;1]
        //
        // jdu na [0;0], [0;1] a [1,0]
        if (x >= x1 && y >= y1) {
            // prohod x
            placeHolder = x;
            x = x1;
            x1 = placeHolder;
            // prohod y
            placeHolder = y;
            y = y1;
            y1 = placeHolder;
        }
        // jdu na [2;0]
        else if (x < x1 && y > y1) {
            // prohodíme y
            placeHolder = y;
            y = y1;
            y1 = placeHolder;
        }
        // jdu na [2;1], [2,2] a [1,2]
        else if (x <= x1 && y <= y1) {
            // nic nedělej
        }
        // jdu na [0;2]
        else if (x > x1 && y < y1) {
            // prohodíme x
            placeHolder = x;
            x = x1;
            x1 = placeHolder;
        } else {
            System.err.println("Chyba ve zpracování inputu");
        }

        mouseStartReal = new Point2D.Double(x, y);
        mouseEndReal = new Point2D.Double(x1, y1);
    }

    // get set public

    /**
     * Vrati barvu vody
     *
     * @return barva vody
     */
    public Color getWaterColor() {
        return waterColor;
    }

    /**
     * Nastava barvu vody
     *
     * @param waterColor nova barva vody
     */
    public void setWaterColor(Color waterColor) {
        this.waterColor = waterColor;
    }

    /**
     * Vrati barvu sipky
     *
     * @return barva sipky
     */
    public Color getArrowColor() {
        return arrowColor;
    }

    /**
     * Nastavi barvu sipky
     *
     * @param arrowColor barva sipky
     */
    public void setArrowColor(Color arrowColor) {
        this.arrowColor = arrowColor;
    }

    /**
     * Vrati barvu textu
     *
     * @return barva textu
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Nastavi barvu textu
     *
     * @param textColor barva textu
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}
