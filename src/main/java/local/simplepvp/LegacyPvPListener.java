package local.simplepvp;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyPvPListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatTagManager combatTagManager;

    public LegacyPvPListener(JavaPlugin plugin, CombatTagManager combatTagManager) {
        this.plugin = plugin;
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyLegacyStats(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (plugin.getConfig().getBoolean("legacy-pvp.reset-hit-immunity", true)) {
            victim.setNoDamageTicks(0);
            victim.setMaximumNoDamageTicks(10);
        }

        if (event.getDamager() instanceof Player attacker) {
            applyLegacyStats(attacker);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!combatTagManager.isTagged(player)) {
            return;
        }
        if (player.hasPermission("simplepvp.bypass.combattag")) {
            return;
        }

        if (event.isFlying()) {
            event.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.sendMessage("§cYou cannot fly while combat tagged.");
        }
    }

    private void applyLegacyStats(Player player) {
        if (!plugin.getConfig().getBoolean("legacy-pvp.enabled", true)) {
            return;
        }

        double speed = plugin.getConfig().getDouble("legacy-pvp.attack-speed-base", 24.0);
        if (player.getAttribute(Attribute.ATTACK_SPEED) != null) {
            player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(speed);
        }
    }
}
