package fr.cozyhouse.cozyHouseRPG.race;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for all game races
 * Immutable after initialization for optimal performance
 * Uses ConcurrentHashMap for thread-safety
 */
public final class RaceRegistry {

    private final PluginContext context;
    private final Map<RaceType, Race> races = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    public RaceRegistry(PluginContext context){
        this.context = context;
    }

    /**
     * Loads all races into the registry
     * Can only be called once
     *
     * @throws IllegalStateException if already initialized
     */
    public void loadAllRaces() {
        if (initialized) {
            throw new IllegalStateException("RaceRegistry already initialized");
        }

        RaceFactory factory = new RaceFactory(context);
        
        for (RaceType type : RaceType.values()) {
            Race race = factory.createRace(type);
            races.put(type, race);
        }

        initialized = true;
    }

    /**
     * Gets a race by type
     *
     * @param type the race type
     * @return the race instance
     * @throws IllegalStateException if not initialized
     * @throws IllegalArgumentException if race type not found
     */
    public Race getRace(RaceType type) {
        ensureInitialized();
        Race race = races.get(type);
        if (race == null) {
            throw new IllegalArgumentException("Race not found: " + type);
        }
        return race;
    }

    /**
     * Gets all available races
     *
     * @return unmodifiable collection of races
     */
    public Collection<Race> getAllRaces() {
        ensureInitialized();
        return Collections.unmodifiableCollection(races.values());
    }

    /**
     * Finds a race by display name (case-insensitive)
     *
     * @param displayName the display name to search
     * @return optional containing the race if found
     */
    public Optional<Race> findByDisplayName(String displayName) {
        ensureInitialized();
        return races.values().stream()
                .filter(race -> race.getDisplayName().equalsIgnoreCase(displayName))
                .findFirst();
    }

    /**
     * Checks if a race exists
     *
     * @param type the race type to check
     * @return true if the race exists
     */
    public boolean hasRace(RaceType type) {
        ensureInitialized();
        return races.containsKey(type);
    }

    /**
     * Gets the number of registered races
     *
     * @return race count
     */
    public int getRaceCount() {
        ensureInitialized();
        return races.size();
    }

    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("RaceRegistry not initialized");
        }
    }

}
