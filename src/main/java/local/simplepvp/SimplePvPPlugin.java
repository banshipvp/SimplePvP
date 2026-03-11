package local.simplepvp;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SimplePvPPlugin extends JavaPlugin {

    private CombatTagManager combatTagManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.combatTagManager = new CombatTagManager(this);

        Bukkit.getPluginManager().registerEvents(new CombatTagListener(this, combatTagManager), this);
        Bukkit.getPluginManager().registerEvents(new CommandBlockListener(this, combatTagManager), this);
        Bukkit.getPluginManager().registerEvents(new LegacyPvPListener(this, combatTagManager), this);

        PluginCommand cmd = getCommand("combattag");
        if (cmd != null) {
            cmd.setExecutor(new CombatTagCommand(combatTagManager));
        }

        getLogger().info("SimplePvP enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimplePvP disabled.");
    }

    public CombatTagManager getCombatTagManager() {
        return combatTagManager;
    }
}
