package local.simplepvp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatTagManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> combatTags = new ConcurrentHashMap<>();

    public CombatTagManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void tag(Player player) {
        int tagSeconds = Math.max(1, plugin.getConfig().getInt("combat.tag-seconds", 10));
        combatTags.put(player.getUniqueId(), System.currentTimeMillis() + tagSeconds * 1000L);
    }

    public boolean isTagged(Player player) {
        Long expires = combatTags.get(player.getUniqueId());
        if (expires == null) {
            return false;
        }
        if (expires <= System.currentTimeMillis()) {
            combatTags.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public long getSecondsLeft(Player player) {
        Long expires = combatTags.get(player.getUniqueId());
        if (expires == null) {
            return 0;
        }
        long delta = expires - System.currentTimeMillis();
        if (delta <= 0) {
            combatTags.remove(player.getUniqueId());
            return 0;
        }
        return (long) Math.ceil(delta / 1000.0);
    }

    public void clear(Player player) {
        combatTags.remove(player.getUniqueId());
    }
}
