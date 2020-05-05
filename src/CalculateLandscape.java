import waterflowsim.Cell;
import waterflowsim.Vector2D;
import waterflowsim.WaterSourceUpdater;

import java.awt.geom.Point2D;

/**
 * Trida slouzi pro urceni rozmeru krajiny +
 * zajistuje vysledne rozmery krajiny a sipek v okne
 *
 * @author Martin Jakubasek
 * @version 1.0
 * @since 22.3.2020
 */
public class CalculateLandscape {

    private Point2D pixLandDim; // rozmery krajiny z Simulator.getDimensions()

    public Point2D trueLandDim; // skutecne rozmery krajiny tj. krajina s deltou
    public Point2D deltaScale; // meritko o kolik se musi zvetsit/zmensit krajina aby vyhovovala delte

    // Nasledujici atributy slouzi k uchovani min max borderu sipek
    private double minCoordX;
    private double minCoordY;
    private double maxCoordX;
    private double maxCoordY;

    /**
     * Konstruktor inicializuje vypocet skutecnych rozmeru krajiny a
     * potrebnou zmenu meritka pro spravne vykresleni krajiny
     *
     * @param landDim dimenze krajiny z Simulator.getDimensions()
     * @param delta delta z Simulator.getDelta()
     */
    public CalculateLandscape(Vector2D<Integer> landDim, Vector2D<Double> delta) {
        pixLandDim = new Point2D.Double(landDim.x, landDim.y);
        computeModelDimensions(landDim, delta);
    }

    /**
     * Konstruktor pro ziskani dimenzi (zajistuje aby se prvky vesli do okna)
     *
     * @param landDim dimenze krajiny
     * @param delta delta krajiny
     */
    private void computeModelDimensions(Vector2D<Integer> landDim, Vector2D<Double> delta) {
        double width = ((landDim.x - 1) * Math.abs(delta.x));
        double height = ((landDim.y - 1) * Math.abs(delta.y));

        trueLandDim = new Point2D.Double(width, height);
        deltaScale = getDeltaScale();
    }

    /**
     * Vypocita meritko, o ktere se musi zvetsit/zmensit krajina,
     * tak aby vyhovovala delte
     *
     * @return meritko krajiny
     */
    private Point2D getDeltaScale() {
        double scaleX = trueLandDim.getX() / pixLandDim.getX();
        double scaleY = trueLandDim.getY() / pixLandDim.getY();

        return new Point2D.Double(scaleX, scaleY);
    }

    /**
     * Vypocita meritko, tak aby se vsechny prvky vesli do okna
     *
     * @param width sirka aktualniho okna
     * @param height vyska aktualniho okna
     *
     * @return meritko
     */
    public double getScale(int width, int height) {
        double x = Math.max(trueLandDim.getX(), maxCoordX);
        double y = Math.max(trueLandDim.getY(), maxCoordY);

        double scaleX = width / (x + Math.abs(minCoordX));
        double scaleY = height / (y + Math.abs(minCoordY));

        return Math.min(scaleY, scaleX);
    }

    /**
     * Urci min - max box ve kterem se nachazeji sipky (slouzi pro to aby se sipky vesli do okna)
     *
     * @param pixLandDim dimenze krajiny
     * @param arrowOffset offset sipek od zdroje
     * @param arrowLength delka sipky
     * @param waterSources vodni zdroje
     */
    public void createArrowDimension(Point2D pixLandDim, Point2D arrowOffset, double arrowLength,
                                   WaterSourceUpdater[] waterSources) {

        maxCoordX = trueLandDim.getX();
        maxCoordY = trueLandDim.getY();

        for (WaterSourceUpdater source : waterSources) {
            double y = (int) (source.getIndex() / pixLandDim.getX());
            double x = (int) (source.getIndex() % pixLandDim.getX());

            x = ((x + arrowOffset.getX()) * deltaScale.getX());
            y = ((y + arrowOffset.getY()) * deltaScale.getY());

            // vlevo
            double borderX = x - (arrowLength * 1.2);
            if (borderX < 0) {
                minCoordX = Math.min(minCoordX, borderX);
            }
            // nahoře
            double borderY = y - (arrowLength * 1.2);
            if (borderY < 0) {
                minCoordY = Math.min(minCoordY, borderY);
            }
            // vpravo
            borderX = x + (arrowLength * 1.2);
            if (borderX > 0) {
                maxCoordX = Math.max(maxCoordX, borderX);
            }
            // dole
            borderY = y + (arrowLength * 1.2);
            if (borderY > 0) {
                maxCoordY = Math.max(maxCoordY, borderY);
            }
        }
    }

    /**
     * Vypocita aktualni koncovou pozici sipek v zadane scale
     * @param pixLandDim pixel souradnice krajiny
     * @param arrowLengths delka sipky
     * @param waterSources vodni zdroje
     * @param landData data krajiny
     * @param scale meritko
     * @return koncove pozice sipek
     */
    public Point2D[] getActualArrowDimensions(Point2D pixLandDim, double arrowLengths,
                                             WaterSourceUpdater[] waterSources, Cell[] landData, double scale) {
        Point2D[] points = new Point2D[waterSources.length];

        int count = 0;
        for (WaterSourceUpdater source : waterSources) {
            double y = (int) (source.getIndex() / pixLandDim.getX());
            double x = (int) (source.getIndex() % pixLandDim.getX());

            Point2D position = new Point2D.Double((x) * deltaScale.getX() * scale,
                    (y) * deltaScale.getY() * scale);
            Vector2D<Double> gradient = landData[source.getIndex()].getGradient();
            gradient = new Vector2D<>(-gradient.x, -gradient.y);

            double magnitude = Math.sqrt(gradient.x * gradient.x + gradient.y * gradient.y);
            Vector2D<Double> normalization = new Vector2D<>(gradient.x / magnitude, gradient.y / magnitude);

            double arrowLength = (arrowLengths * 1.25d) * scale;

            Vector2D<Double> headings = new Vector2D<>(normalization.x * arrowLength, normalization.y * arrowLength);
            Point2D pointB = new Point2D.Double(position.getX() + headings.x, position.getY() + headings.y);

            Point2D point2D = new Point2D.Double(pointB.getX(), pointB.getY());

            points[count] = new Point2D.Double(point2D.getX(), point2D.getY());
            count++;
        }

        return points;
    }

    /**
     * Vrati minimalni rozmery sipky ve smeru X
     *
     * @return minimalni rozmer ve smeru X
     */
    public double getMinCoordX() {
        return minCoordX;
    }

    /**
     * Vrati minimalni rozmery sipky ve smeru Y
     *
     * @return minimalni rozmer ve smeru Y
     */
    public double getMinCoordY() {
        return minCoordY;
    }
}
