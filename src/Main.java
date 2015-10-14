import core.Terrain;
import gui.SFMLGUI;
import managers.EventManager;
import managers.IEventObserver;
import org.javatuples.Pair;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import parser.TerrainParser;

import java.util.*;

public class Main implements IEventObserver {

    private SFMLGUI gui;
    private RenderWindow window;
    private EventManager eventManager;

    private TerrainParser parser;
    private Map<String, Terrain> terrains;
    private String activeTerrain;

    private int terrainSampling;

    private Map<Event.Type, Runnable> eventsBinding;
    private Map<Keyboard.Key, Runnable> keyboardEventsBinding;

    private boolean isRunning;

    public Main(final String[] args) {
        if (args == null || args.length == 0)
            System.out.println("Usage: ./jfdf <width> <height> [*.terrain | *.math]");

        gui = new SFMLGUI(args);
        window = gui.createWindow("JFDF poirie_l #2018");

        parseTerrains(args);

        terrainSampling = 1;

        eventManager = gui.createEventManager(this);

        eventManager.registerEventType(Event.Type.CLOSED);
        eventManager.registerKeyboardEvent(Keyboard.Key.LEFT);
        eventManager.registerKeyboardEvent(Keyboard.Key.RIGHT);

        eventManager.registerKeyboardEvent(Keyboard.Key.UP);
        eventManager.registerKeyboardEvent(Keyboard.Key.DOWN);

        eventsBinding = new HashMap<>();
        eventsBinding.put(Event.Type.CLOSED, () -> {
            this.gui.destroyWindow();
            this.isRunning = false;
        });

        keyboardEventsBinding = new HashMap<>();
        keyboardEventsBinding.put(Keyboard.Key.LEFT, () -> {
            String previous = null;
            String last = null;

            for (final String terrainName : terrains.keySet()) {
                if (activeTerrain == terrainName)
                    break ;
                previous = terrainName;
            }

            for (final String terrainName : terrains.keySet())
                last = terrainName;

            activeTerrain = (previous == null) ? last : previous;
        });
        keyboardEventsBinding.put(Keyboard.Key.RIGHT, () -> {
            String first = null;
            String previous = null;
            String next = null;

            for (final String terrainName : terrains.keySet()) {
                if (first == null)
                    first = terrainName;

                if (previous != null) {
                    next = terrainName;
                    break;
                }

                if (activeTerrain == terrainName)
                    previous = terrainName;
            }

            activeTerrain = (next == null) ? first : next;
        });
        keyboardEventsBinding.put(Keyboard.Key.UP, () -> {
            if (this.terrainSampling < getActiveTerrain().getMaximumSamplingRatio())
                this.terrainSampling += 1;
        });
        keyboardEventsBinding.put(Keyboard.Key.DOWN, () -> {
            if (this.terrainSampling > 1)
                this.terrainSampling -= 1;
        });
    }

    private void parseTerrains(final String[] args) {
        if (args == null || args.length < 2)
            return;

        parser = new TerrainParser();
        terrains = new HashMap<>();

        for (int i = 2; i < args.length; i++) {
            parser.parse(args[i]);
            terrains.put(args[i], parser.build(window.getSize()));
        }

        activeTerrain = args[2];
    }

    public static void main(final String[] args) {
        Main core = new Main(args);

        core.run();
    }

    /**
     * Run core application
     */
    public void run() {
        isRunning = true;

        while (isRunning()) {
            eventManager.pollEvents();
            eventManager.handleEvents();
            eventManager.handleKeyboardEvents();

            draw();

            gui.refresh(Color.BLACK);
        }
    }

    private void draw() {
        Terrain terrain;

        if (activeTerrain == null || activeTerrain.isEmpty() ||
            !terrains.containsKey(activeTerrain)) {
            System.err.println("[Warning] No active terrain to display.");
            return;
        }

        gui.drawText("Tessellation factor: " + terrainSampling, 32, window.getSize().y - 72, false);
        gui.drawText("Name : " + activeTerrain, 32, window.getSize().y - 48, false);
        terrain = terrains.get(activeTerrain);
        if (terrain == null)
            return ;
        terrain.draw(gui, terrainSampling);
    }

    /**
     * Is the whole core running
     * @return true if core or gui is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    public final Terrain getActiveTerrain() {
        if (!terrains.containsKey(activeTerrain))
            return null;

        return terrains.get(activeTerrain);
    }

    @Override
    public void handleEvents(final Map<Event.Type, Event> events) {
        if (events.isEmpty() || eventsBinding.isEmpty())
            return ;

        for (final Event.Type eventType : events.keySet()) {
            Runnable action = eventsBinding.get(eventType);

            if (action != null)
                action.run();
        }
    }

    @Override
    public void handleKeyboardEvents(final List<KeyEvent> events) {
        if (events.isEmpty() || keyboardEventsBinding.isEmpty())
            return ;

        for (final KeyEvent event : events) {
            Runnable action = keyboardEventsBinding.get(event.key);

            if (action != null)
                action.run();
        }
    }
}
