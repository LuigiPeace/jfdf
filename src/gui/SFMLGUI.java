package gui;

import managers.EventManager;
import managers.IEventObserver;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.VideoMode;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SFMLGUI {

    private RenderWindow window = null;
    private EventManager eventManager = null;

    private List<Triplet<Vertex, Vertex, Color>> linesBuffer;
    private List<Quartet<String, Float, Float, Boolean>> textsBuffer;

    private Map<String, Integer> programArgs;

    private Font font;
    private Text text;

    public SFMLGUI() {
        linesBuffer = new ArrayList<>();
        textsBuffer = new ArrayList<>();

        programArgs = new HashMap<>();
    }

    public SFMLGUI(final String[] args) {
        this();
        int width, height;

        if (args == null || args.length < 2) {
            width = 1280;
            height = 720;

            System.err.println("[Warning] No program arguments! default: width(1280), height(720)");
        } else {
            width = Integer.valueOf(args[0]);
            height = Integer.valueOf(args[1]);
        }

        programArgs.put("width", width);
        programArgs.put("height", height);
    }

    /**
     * Create a sized window with caption title.
     * @param caption Window caption title.
     * @param width Window width in pixels.
     * @param height Window height in pixels.
     * @return new window pointer or null if one is already existing.
     */
    public RenderWindow createWindow(final String caption, final int width, final int height) {
        if (window != null)
            return window;

        window = new RenderWindow();
        window.create(new VideoMode(width, height), caption);
        window.setFramerateLimit(30);

        try {
            font = new Font();
            font.loadFromFile(Paths.get("fonts/Philosopher.ttf"));
        } catch (IOException e) {
            System.err.println("[Fatal] Cannot load font: Philosopher.ttf");
            return null;
        }

        text = new Text("", font, 20);

        return window;
    }

    /**
     * Create a sized window with caption title thanks to arguments of program injected through constructor
     * @param caption Window caption title.
     * @return new window pointer or null if one is already existing.
     */
    public RenderWindow createWindow(final String caption) {
        return createWindow(caption, programArgs.get("width"), programArgs.get("height"));
    }

    /**
     * Destroy current window if existing.
     */
    public void destroyWindow() {
        if (window == null)
            return ;

        window.close();
        window = null;
    }

    public EventManager createEventManager(IEventObserver observer) {
        eventManager = new EventManager(window, observer);

        return eventManager;
    }

    public void drawLine(final Vector2f begin, final Vector2f end, final Color color) {
        Triplet<Vertex, Vertex, Color> triplet = new Triplet<>(new Vertex(begin), new Vertex(end), color);
        linesBuffer.add(triplet);
    }

    public void drawText(final String text, final float x, final float y, final boolean center) {
        Quartet<String, Float, Float, Boolean> quartet = new Quartet<>(text, x, y, center);
        textsBuffer.add(quartet);
    }

    public boolean isRunning() {
        return window != null && window.isOpen();
    }

    /**
     * Clear screen with color and display all buffered draw calls.
     *
     * @param color
     */
    public void refresh(final Color color) {
        if (window == null)
            return ;

        window.clear(color);

        for (final Triplet<Vertex, Vertex, Color> lineData : linesBuffer)
            displayLine(lineData.getValue0(), lineData.getValue1(), lineData.getValue2());

        for (final Quartet<String, Float, Float, Boolean> textData : textsBuffer)
            displayText(textData.getValue0(), textData.getValue1(), textData.getValue2(), textData.getValue3());

        window.display();

        linesBuffer.clear();
        textsBuffer.clear();
    }

    private void displayLine(final Vertex begin, final Vertex end, final Color color) {
        Vertex[] lineArray = new Vertex[2];

        lineArray[0] = begin;
        lineArray[1] = end;
        window.draw(lineArray, PrimitiveType.LINES);
    }

    private void displayText(final String text, final float x, final float y, final boolean center) {
        this.text.setString(text);

        if (center) {
            FloatRect bound = this.text.getLocalBounds();
            this.text.setOrigin(bound.width / 2, bound.height / 2);
        }
        this.text.setPosition(x, y);
        window.draw(this.text);
    }
}
