package local.simplepvp;

import org.bukkit.Bukkit;

public final class BukkitCompat {
    private BukkitCompat() {}

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }
}
