package core;

import gui.SFMLGUI;
import org.javatuples.Pair;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import java.util.List;

public class Terrain {

    private final int width;
    private final int height;

    private int[][] data;
    private Pair[][] projectionData;

    private final List<String> lines;

    private float projectionX = 0.6f;
    private float projectionY = 0.6f;

    private float scaleXY = 25.0f;
    private float scaleZ = 5.0f;

    public Terrain(final List<String> lines) {
        if (lines == null || lines.isEmpty() || lines.get(0).isEmpty()) {
            throw new RuntimeException();
        }
        this.width = lines.get(0).split(" ").length;
        this.height = lines.size();
        this.lines = lines;
    }

    /**
     * Create a 3D matrix from terrain's data.
     */
    public void buildData() {
        int x, y;

        y = 0;
        data = new int[height][width];
        for (final String line : lines) {
            x = 0;
            for (final String depth : line.split(" "))
                data[y][x++] = Integer.valueOf(depth);
            y++;
        }
    }

    /**
     * Create a 2D matrix from 3D matrix with isometric projection.
     */
    public void buildProjectionData() {
        int x;

        projectionData = new Pair[height][width];
        for (int y = 0; y < height; y++)
            for (x = 0; x < width; x++)
                projectionData[y][x] = projectData((float)x, (float)y, (float)data[y][x]);
    }

    /**
     * Create a 2D matrix from 3D matrix with choosen projection.
     * @param projectionXY X, Y projection coefficient
     * @param scaleXY X, Y scaling coefficient
     */
    public void buildProjectionData(final float projectionXY, final float scaleXY, final float scaleZ) {
        this.projectionX = projectionXY;
        this.projectionY = projectionXY;
        this.scaleXY = scaleXY;
        this.scaleZ = scaleZ;

        buildProjectionData();
    }

    public void draw(SFMLGUI gui, final int sample) {
        Vector2f a, b, c, d;
        int x;
        int step;

        step = (sample == 0) ? 1 : sample;

        for (int y = 0; y < height - sample; y += sample) {
            for (x = 0; x < width - sample; x += sample) {
                a = new Vector2f((float)projectionData[y][x].getValue0(), (float)projectionData[y][x].getValue1());
                b = new Vector2f((float)projectionData[y][x + step].getValue0(), (float)projectionData[y][x + step].getValue1());
                c = new Vector2f((float)projectionData[y + step][x + step].getValue0(), (float)projectionData[y + step][x + step].getValue1());
                d = new Vector2f((float)projectionData[y + step][x].getValue0(), (float)projectionData[y + step][x].getValue1());

                gui.drawLine(a, b, Color.WHITE);
                gui.drawLine(b, c, Color.WHITE);
                gui.drawLine(c, d, Color.WHITE);
                gui.drawLine(d, a, Color.WHITE);
            }
        }
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final int getMaximumSamplingRatio() {
        int min = width;

        min = (height < min) ? height : min;
        return min;
    }

    /**
     * Create a 2D point from 3D point with isometric projection.
     * @param x
     * @param y
     * @param z
     * @return
     */
    private Pair projectData(final float x, final float y, final float z) {
        float pX, pY;

        pX = projectionX * (x * scaleXY) - projectionY * (y * scaleXY);
        pX += 1280 / 2;

        pY = (z * scaleZ) + projectionX / 2f * (x * scaleXY) + projectionY / 2f * (y * scaleXY);
        pY = 720 - pY;

        return new Pair(pX, pY);
    }
}
