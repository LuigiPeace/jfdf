package managers;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private RenderWindow window;
    private IEventObserver observer;

    private List<Event.Type> eventsBinding;
    private List<Keyboard.Key> keyboardEventsBinding;

    private Map<Event.Type, Event> events;
    private List<KeyEvent> keyboardEvents;

    public EventManager(RenderWindow window, IEventObserver observer) {
        this.window = window;
        this.observer = observer;

        this.eventsBinding = new ArrayList<>();
        this.keyboardEventsBinding = new ArrayList<>();
        this.events = new HashMap<>();
        this.keyboardEvents = new ArrayList<>();
    }

    /**
     * Register an Event.Type to catch when fired.
     * @param eventType to catch.
     */
    public void registerEventType(final Event.Type eventType) {
        if (eventsBinding.contains(eventType))
            return ;

        eventsBinding.add(eventType);
    }

    public void registerKeyboardEvent(final Keyboard.Key key) {
        if (keyboardEventsBinding.contains(key))
            return ;

        keyboardEventsBinding.add(key);
    }

    /**
     * Retrieve registered events indexed by Event.Type
     */
    public void pollEvents() {
        KeyEvent keyEvent;

        events.clear();
        keyboardEvents.clear();

        for (Event event : window.pollEvents()) {
            if (eventsBinding.contains(event.type))
                events.put(event.type, event);

            keyEvent = event.asKeyEvent();
            if (keyEvent != null && event.type.equals(Event.Type.KEY_RELEASED) && keyboardEventsBinding.contains(keyEvent.key))
                keyboardEvents.add(keyEvent);
        }
    }

    public void handleEvents() {
        observer.handleEvents(events);
    }

    public void handleKeyboardEvents() {
        observer.handleKeyboardEvents(keyboardEvents);
    }
}
