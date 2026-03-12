package local.simplepvp;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LegacyPvPListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatTagManager combatTagManager;
    private final Set<UUID> blockingPlayers = new HashSet<>();

    public LegacyPvPListener(JavaPlugin plugin, CombatTagManager combatTagManager) {
        this.plugin = plugin;
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyLegacyStats(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
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

        // 1.8-style sword blocking: 10% damage reduction when right-clicking with a sword
        if (plugin.getConfig().getBoolean("legacy-pvp.sword-blocking", true)
                && blockingPlayers.contains(victim.getUniqueId())) {
            event.setDamage(event.getDamage() * 0.9);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwordBlock(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        if (!plugin.getConfig().getBoolean("legacy-pvp.sword-blocking", true)) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (isSword(item.getType())) {
            UUID uid = player.getUniqueId();
            blockingPlayers.add(uid);
            // Blocking state lasts only 1 tick (50ms) — enough to absorb the hit
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> blockingPlayers.remove(uid), 1L);
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

    private boolean isSword(Material material) {
        return switch (material) {
            case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD,
                 DIAMOND_SWORD, NETHERITE_SWORD -> true;
            default -> false;
        };
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
