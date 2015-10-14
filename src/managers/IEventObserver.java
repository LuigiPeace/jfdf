package managers;

import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import java.util.List;
import java.util.Map;

public interface IEventObserver {

    void handleEvents(final Map<Event.Type, Event> events);
    void handleKeyboardEvents(final List<KeyEvent> events);
}
