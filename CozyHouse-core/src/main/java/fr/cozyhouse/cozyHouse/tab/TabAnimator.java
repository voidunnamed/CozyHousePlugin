package fr.cozyhouse.cozyHouse.tab;

import fr.cozyhouse.cozyHouse.CozyHouseCore;
import fr.cozyhouse.cozyHouse.gameplayer.GamePlayer;
import fr.cozyhouse.cozyHouse.gameplayer.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class TabAnimator {

    private final CozyHouseCore plugin;
    private final PlayerManager playerManager;
    private int tickCounter = 0;

    // Texte FIXE sur 3 lignes (toujours au même endroit)
    private static final String FIXED_LINE_1 = "§f§l          CozyHouseCore          ";
    private static final String FIXED_LINE_2 = "§7§o     ton havre de paix     ";


    private static final String[] HEADER_FRAMES = new String[4];

    static {
        // Frames 0-3 : Feuilles qui tombent (🍂 descendent autour du texte)
        HEADER_FRAMES[0] = "§6✦   ❄   ✦  " + FIXED_LINE_1 + "§6 ✦ ❄         " + "\n"
                + "§6        ❄   " + FIXED_LINE_2 + "§6      ❄   ✦  ";

        HEADER_FRAMES[1] = "§6 ❄      ✦ ✦" + FIXED_LINE_1 + "§6 ❄   ✦      " + "\n"
                + "§6❄      ❄✦  " + FIXED_LINE_2 + "§6 ✦ ❄        ";

        HEADER_FRAMES[2] = "§6✦   ❄  ✦   " + FIXED_LINE_1 + "§6   ❄  ✦     " + "\n"
                + "§6 ❄ ✦     ❄ " + FIXED_LINE_2 + "§6    ✦    ❄  ";

        HEADER_FRAMES[3] = "§6   ✦ ✦    ✦" + FIXED_LINE_1 + "§6✦        ❄  " + "\n"
                + "§6❄   ✦  ❄   " + FIXED_LINE_2 + "§6 ❄ ✦        ";
    }

    private static final String[] LATERAL_BAR_FRAMES = new String[43];

    static {
        int i = 0;
        LATERAL_BAR_FRAMES[0] = "§6═══════════════════════════════";
        LATERAL_BAR_FRAMES[1] = "§8═§6══════════════════════════════";
        LATERAL_BAR_FRAMES[2] = "§8══§6═════════════════════════════";
        LATERAL_BAR_FRAMES[3] = "§8═══§6════════════════════════════";
        LATERAL_BAR_FRAMES[4] = "§8════§6═══════════════════════════";
        LATERAL_BAR_FRAMES[5] = "§8═════§6══════════════════════════";
        LATERAL_BAR_FRAMES[6] = "§8══════§6═════════════════════════";
        LATERAL_BAR_FRAMES[7] = "§8═══════§6════════════════════════";
        LATERAL_BAR_FRAMES[8] = "§8════════§6═══════════════════════";
        LATERAL_BAR_FRAMES[9] = "§8═════════§6══════════════════════";
        LATERAL_BAR_FRAMES[10] = "§8══════════§6═════════════════════";
        LATERAL_BAR_FRAMES[11] = "§8═══════════§6════════════════════";
        LATERAL_BAR_FRAMES[12] = "§8════════════§6═══════════════════";
        LATERAL_BAR_FRAMES[13] = "§8═════════════§6══════════════════";
        LATERAL_BAR_FRAMES[14] = "§6══§8════════════§6═════════════════";
        LATERAL_BAR_FRAMES[15] = "§6═══§8════════════§6════════════════";
        LATERAL_BAR_FRAMES[16] = "§6════§8════════════§6═══════════════";
        LATERAL_BAR_FRAMES[17] = "§6═════§8════════════§6══════════════";
        LATERAL_BAR_FRAMES[18] = "§6══════§8════════════§6═════════════";
        LATERAL_BAR_FRAMES[19] = "§6═══════§8════════════§6════════════";
        LATERAL_BAR_FRAMES[20] = "§6════════§8════════════§6═══════════";
        LATERAL_BAR_FRAMES[21] = "§6═════════§8════════════§6══════════";
        LATERAL_BAR_FRAMES[22] = "§6══════════§8════════════§6═════════";
        LATERAL_BAR_FRAMES[23] = "§6═══════════§8════════════§6════════";
        LATERAL_BAR_FRAMES[24] = "§6════════════§8════════════§6═══════";
        LATERAL_BAR_FRAMES[25] = "§6═════════════§8════════════§6══════";
        LATERAL_BAR_FRAMES[26] = "§6══════════════§8════════════§6═════";
        LATERAL_BAR_FRAMES[27] = "§6═══════════════§8════════════§6════";
        LATERAL_BAR_FRAMES[28] = "§6════════════════§8════════════§6═══";
        LATERAL_BAR_FRAMES[29] = "§6═════════════════§8════════════§6══";
        LATERAL_BAR_FRAMES[30] = "§6══════════════════§8════════════§6═";
        LATERAL_BAR_FRAMES[31] = "§6═══════════════════§8════════════";
        LATERAL_BAR_FRAMES[32] = "§6════════════════════§8═══════════";
        LATERAL_BAR_FRAMES[33] = "§6═════════════════════§8══════════";
        LATERAL_BAR_FRAMES[34] = "§6══════════════════════§8═════════";
        LATERAL_BAR_FRAMES[35] = "§6═══════════════════════§8════════";
        LATERAL_BAR_FRAMES[36] = "§6════════════════════════§8═══════";
        LATERAL_BAR_FRAMES[37] = "§6═════════════════════════§8══════";
        LATERAL_BAR_FRAMES[38] = "§6══════════════════════════§8═════";
        LATERAL_BAR_FRAMES[39] = "§6═══════════════════════════§8════";
        LATERAL_BAR_FRAMES[40] = "§6════════════════════════════§8═══";
        LATERAL_BAR_FRAMES[41] = "§6═════════════════════════════§8══";
        LATERAL_BAR_FRAMES[42] = "§6══════════════════════════════§8═";
    }

    public TabAnimator() {
        this.plugin = CozyHouseCore.getInstance();
        this.playerManager = CozyHouseCore.getInstance().getPlayerManager();
    }

    public void startAnimation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tickCounter++;

                if (tickCounter % 4 != 0) return;

                int online = Bukkit.getOnlinePlayers().size();
                int max = Bukkit.getMaxPlayers();

                int frameLateralBar = (tickCounter / 5) % LATERAL_BAR_FRAMES.length;

                int frameHeaderIndex = (tickCounter / 20) % HEADER_FRAMES.length;
                String header = HEADER_FRAMES[frameHeaderIndex] + "\n" + LATERAL_BAR_FRAMES[frameLateralBar] + "\n";

                String footerBase = LATERAL_BAR_FRAMES[(LATERAL_BAR_FRAMES.length - 1) - frameLateralBar] + "\n" +
                        "§7Online : §f" + online + "§7 / " + max + "\n" +
                        "§7Your ping : ";

                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player p : players) {
                    GamePlayer gamePlayer = playerManager.getPlayer(p);
                    int ping = p.getPing();
                    String pingColor = getPingColor(ping);
                    String fullFooter = footerBase + pingColor + ping + " ms\n" +
                            "§4§lCozy§f§lCoins §7: §6§l" + gamePlayer.getMoney() + "\n" +
                            LATERAL_BAR_FRAMES[frameLateralBar];

                    p.setPlayerListHeaderFooter(header, fullFooter);

                    p.setPlayerListName("§f" + p.getName());
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

    public void updateForNewPlayer(Player player) {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        int frameLateralBar = (tickCounter / 20) % LATERAL_BAR_FRAMES.length;

        int frameIndex = (tickCounter / 20) % HEADER_FRAMES.length;
        String header = HEADER_FRAMES[frameIndex] + "\n" + LATERAL_BAR_FRAMES[frameLateralBar];

        String footerBase = "§8═══════════════════════════════\n" +
                "§7En ligne : §f" + online + "§7 / " + max + "\n" +
                "§7Ton ping : ";
        int ping = player.getPing();
        String pingColor = getPingColor(ping);
        String fullFooter = footerBase + pingColor + ping + " ms\n" +
                "§8═══════════════════════════════";

        player.setPlayerListHeaderFooter(header, fullFooter);
        player.setPlayerListName("§f" + player.getName());
    }

    private String getPingColor(int ping) {
        if (ping <= 50) return "§a";
        if (ping <= 100) return "§e";
        if (ping <= 200) return "§6";
        return "§c";
    }
}
