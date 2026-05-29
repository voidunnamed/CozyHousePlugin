package fr.cozyhouse.cozyHouseRPG.listeners;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.PlayerService;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.player.experience.MobsExperiences;
import fr.cozyhouse.cozyHouseRPG.player.experience.XpFloatingText;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EntityDeathListener implements Listener {

    private final PluginContext context;
    private final PlayerService playerService;

    public EntityDeathListener(PluginContext context) {
        this.context = context;
        this.playerService = context.getPlayerService();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamageSource().getCausingEntity();

        // On ignore si la victime est un joueur ou si le tueur n'est pas un joueur
        if (victim instanceof Player || !(damager instanceof Player killer)) {
            return;
        }

        RPGPlayer rpgPlayer = playerService.getPlayer(killer).orElse(null);
        if (rpgPlayer == null) return;

        for (MobsExperiences mob : MobsExperiences.values()) {
            if (mob.type.equals(victim.getType())) {
                long max = mob.max;
                long min = mob.min;
                Random rand = new Random();
                long xp = rand.nextLong(max - min) + min;
                long gained = rpgPlayer.getExperienceService().addExperience(xp);

                // Affiche le texte flottant "+X EXP" uniquement pour ce joueur
                if (gained > 0) {
                    XpFloatingText.spawn(
                            context.getPlugin(),
                            killer,
                            victim.getLocation(),
                            gained
                    );
                }
                break; // inutile de continuer une fois le mob trouvé
            }
        }
    }
}
