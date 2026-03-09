package local.simplepvp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatTagListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatTagManager combatTagManager;

    public CombatTagListener(JavaPlugin plugin, CombatTagManager combatTagManager) {
        this.plugin = plugin;
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile projectile && projectile.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null || attacker.equals(victim)) {
            return;
        }

        combatTagManager.tag(attacker);
        combatTagManager.tag(victim);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatTagManager.isTagged(player)) {
            if (plugin.getConfig().getBoolean("combat.kill-on-logout", true)) {
                player.setHealth(0.0);
                BukkitCompat.broadcast("§c" + player.getName() + " combat logged and was killed.");
            }
        }
        combatTagManager.clear(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        combatTagManager.clear(event.getEntity());
    }
}
