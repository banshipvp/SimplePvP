package local.simplepvp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommandBlockListener implements Listener {

    private final CombatTagManager combatTagManager;
    private final Set<String> blocked;

    public CommandBlockListener(JavaPlugin plugin, CombatTagManager combatTagManager) {
        this.combatTagManager = combatTagManager;
        List<String> blockedList = plugin.getConfig().getStringList("combat.blocked-commands");
        this.blocked = new HashSet<>();
        for (String c : blockedList) {
            blocked.add(c.toLowerCase(Locale.ROOT));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!combatTagManager.isTagged(player)) {
            return;
        }
        if (player.hasPermission("simplepvp.bypass.combattag")) {
            return;
        }

        String msg = event.getMessage();
        if (msg == null || msg.length() < 2 || !msg.startsWith("/")) {
            return;
        }

        String base = msg.substring(1).split("\\s+")[0].toLowerCase(Locale.ROOT);
        if (blocked.contains(base)) {
            event.setCancelled(true);
            player.sendMessage("§cYou are combat tagged for §e" + combatTagManager.getSecondsLeft(player) + "s§c. Command blocked.");
        }
    }
}
