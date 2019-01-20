package pw.forcide.hub.commands;

import pw.forcide.hub.utils.Lang;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.Hub;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class LeaveQueueCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§aBruh...");
            return true;
        }
        final Player p = (Player)sender;
        if (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) {
            sender.sendMessage(Lang.getString("NOT_IN_QUEUE"));
            return true;
        }
        p.sendMessage(Lang.getString("QUEUE_LEFT").replace("%server%", Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p)));
        Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).removeEntry((OfflinePlayer)p);
        return true;
    }
}
