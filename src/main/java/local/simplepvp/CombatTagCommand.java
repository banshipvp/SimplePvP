package local.simplepvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CombatTagCommand implements CommandExecutor {

    private final CombatTagManager combatTagManager;

    public CombatTagCommand(CombatTagManager combatTagManager) {
        this.combatTagManager = combatTagManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        long left = combatTagManager.getSecondsLeft(player);
        if (left <= 0) {
            player.sendMessage("§aYou are not combat tagged.");
            return true;
        }

        player.sendMessage("§cYou are combat tagged for §e" + left + "s§c.");
        return true;
    }
}
