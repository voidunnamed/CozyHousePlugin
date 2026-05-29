package fr.cozyhouse.cozyHouseRPG;

import fr.cozyhouse.cozyHouseRPG.commands.RaceCommand;
import fr.cozyhouse.cozyHouseRPG.commands.RaceTabCompleter;
import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.listeners.EntityDamageListener;
import fr.cozyhouse.cozyHouseRPG.listeners.EntityDeathListener;
import fr.cozyhouse.cozyHouseRPG.listeners.InventoryClickListener;
import fr.cozyhouse.cozyHouseRPG.listeners.PlayerInteractListener;
import fr.cozyhouse.cozyHouseRPG.listeners.PlayerJoinListener;
import fr.cozyhouse.cozyHouseRPG.listeners.PlayerQuitListener;
import fr.cozyhouse.cozyHouseRPG.listeners.PlayerRespawnListener;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principale du plugin CozyHouseRPG.
 *
 * Règle d'organisation des listeners : une classe par type d'événement Bukkit.
 * Chaque listener délègue la logique à ses sous-systèmes (services, GUI, etc.).
 *
 *   PlayerJoinListener      → PlayerJoinEvent
 *   PlayerQuitListener      → PlayerQuitEvent
 *   PlayerRespawnListener   → PlayerRespawnEvent
 *   EntityDeathListener     → EntityDeathEvent
 *   EntityDamageListener    → EntityDamageByEntityEvent + EntityDamageEvent (dégâts)
 *   InventoryClickListener  → InventoryClickEvent (race, tessères, futurs GUI)
 *
 * Pour ajouter un nouveau GUI ou système : ajouter la délégation dans le listener
 * correspondant à l'événement utilisé, sans créer de nouveau listener.
 */
public final class CozyHouseRPG extends JavaPlugin {

    @Getter
    private PluginContext context;

    @Override
    public void onEnable() {
        this.context = new PluginContext(this);
        registerListeners();
        registerCommands();
        getLogger().info("CozyHouseRPG enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (context != null) {
            context.shutdown();
        }
        getLogger().info("CozyHouseRPG disabled successfully!");
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        // Cycle de vie des joueurs
        pm.registerEvents(new PlayerJoinListener(context),    this);
        pm.registerEvents(new PlayerQuitListener(context),    this);
        pm.registerEvents(new PlayerRespawnListener(context), this);

        // Événements de combat
        pm.registerEvents(new EntityDeathListener(context),   this);
        pm.registerEvents(new EntityDamageListener(context),  this);

        // Inventaires (race, tessères, tout futur GUI)
        pm.registerEvents(new InventoryClickListener(context),  this);

        // Interactions monde (baguette arcanique, skill mode)
        pm.registerEvents(new PlayerInteractListener(context), this);
    }

    private void registerCommands() {
        PluginCommand cmd = getCommand("cozyhouserpg");
        if (cmd != null) {
            cmd.setExecutor(new RaceCommand(context));
            cmd.setTabCompleter(new RaceTabCompleter());
        } else {
            getLogger().severe("Failed to register command: cozyhouserpg");
        }
    }
}
