# Event-Driven Design Implementation

## Overview

This document describes the implementation of event-driven design for race mode changes in the atamamozi_d plugin, addressing the TODO comment in `Race.java:69`.

## Problem Statement

The original `Race.setMode()` method contained a TODO comment:
```java
// TODO: イベント駆動設計へ移行を検討
```

The method was designed to change race modes but had no implementation, making the `race_Mode` field immutable in practice while the game logic required mode changes.

## Solution: Event-Driven Architecture

### Design Principles

1. **Separation of Concerns**: Mode changes are separate from their side effects
2. **Loose Coupling**: Components react to mode changes via events, not direct calls
3. **Extensibility**: New behaviors can be added by registering additional event handlers
4. **Observability**: All mode changes are logged and can be monitored

### Implementation Details

#### 1. RaceModeChangeEvent Class

Created: `src/main/java/waterpunch/atamamozi_d/plugin/race/events/RaceModeChangeEvent.java`

This custom Bukkit event fires whenever a race changes mode (e.g., WAIT → RUN, RUN → GOAL).

**Key Features:**
- Extends Bukkit's `Event` class
- Not cancellable (mode changes are final once initiated)
- Contains race reference, old mode, and new mode
- Follows Bukkit event API conventions

**Properties:**
- `race`: The Race object whose mode is changing
- `oldMode`: Previous Race_Mode state
- `newMode`: New Race_Mode state

#### 2. Updated Race.setMode() Method

Modified: `src/main/java/waterpunch/atamamozi_d/plugin/race/Race.java`

The `setMode()` method now:
1. Validates the new mode (null check, duplicate check)
2. Updates the internal state
3. Fires a `RaceModeChangeEvent`

**Benefits:**
- No direct side effects in the setter
- Side effects are handled by event listeners
- Easy to add new behaviors without modifying Race class

#### 3. Event Handler in Event.java

Modified: `src/main/java/waterpunch/atamamozi_d/plugin/event/Event.java`

Added `onRaceModeChange()` event handler that:
- Logs all mode changes for debugging
- Can be extended to handle specific transitions (e.g., WAIT→RUN)

## Benefits of Event-Driven Design

### 1. Maintainability
- Clear separation between state changes and their consequences
- Easy to understand what happens when a race mode changes
- Changes to behavior don't require modifying the Race class

### 2. Testability
- Mode changes can be tested independently of side effects
- Event handlers can be tested in isolation
- Mock events can be used for unit testing

### 3. Extensibility
- New plugins can register listeners for `RaceModeChangeEvent`
- Custom behaviors can be added without modifying core code
- Multiple handlers can react to the same event

### 4. Debugging
- All mode changes are logged automatically
- Event flow is visible in logs
- Easier to trace race state transitions

## Usage Examples

### Basic Mode Change
```java
// In game logic
race.setMode(Race_Mode.RUN);
// RaceModeChangeEvent is fired automatically
// All registered listeners are notified
```

### Listening for Mode Changes
```java
@EventHandler
public void onRaceModeChange(RaceModeChangeEvent event) {
    Race race = event.getRace();
    Race_Mode oldMode = event.getOldMode();
    Race_Mode newMode = event.getNewMode();
    
    // Handle specific transitions
    if (oldMode == Race_Mode.WAIT && newMode == Race_Mode.RUN) {
        // Race is starting - play sound, send messages, etc.
        notifyPlayers(race);
        startTimers(race);
    }
}
```

### Adding Custom Behavior
```java
// In a separate plugin or module
@EventHandler
public void onRaceStart(RaceModeChangeEvent event) {
    if (event.getNewMode() == Race_Mode.RUN) {
        // Custom plugin logic
        announceRaceStart(event.getRace());
        recordStatistics(event.getRace());
    }
}
```

## Migration Notes

### Changes Made
1. Changed `race_Mode` from `final` to mutable field
2. Implemented `setMode()` with event firing
3. Added `RaceModeChangeEvent` class
4. Added event handler in `Event.java`

### Backward Compatibility
- All existing `race.setMode()` calls continue to work
- No changes required to calling code
- Side effects can be migrated gradually to event handlers

### Future Improvements
1. **Additional Events**: Create events for participant join/leave, checkpoint pass, etc.
2. **Event Cancellation**: Consider making certain events cancellable (e.g., pre-mode-change events)
3. **Event Priority**: Use Bukkit's event priority system for ordered handling
4. **Async Events**: Some heavy operations could use async events

## Code Structure

```
src/main/java/waterpunch/atamamozi_d/plugin/
├── race/
│   ├── Race.java                    # Updated setMode() method
│   ├── enums/
│   │   └── Race_Mode.java          # Enum defining race states
│   └── events/
│       └── RaceModeChangeEvent.java # New event class
└── event/
    └── Event.java                   # Updated with event handler
```

## Testing Recommendations

1. **Unit Tests**: Test `Race.setMode()` with mock event manager
2. **Integration Tests**: Verify events are fired and handled correctly
3. **Regression Tests**: Ensure existing race functionality still works
4. **Performance Tests**: Verify event overhead is negligible

## Conclusion

The event-driven design successfully addresses the TODO by:
- Implementing a clean, extensible architecture
- Maintaining backward compatibility
- Improving code maintainability and testability
- Following Bukkit plugin best practices

This implementation serves as a foundation for further event-driven refactoring of the race system.
