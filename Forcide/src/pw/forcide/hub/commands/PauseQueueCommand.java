package pw.forcide.hub.commands;

import pw.forcide.hub.queue.Queue;
import pw.forcide.hub.Hub;
import pw.forcide.hub.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class PauseQueueCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("hub.queue.pause")) {
            sender.sendMessage(Lang.getString("NO_PERMISSION"));
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /" + label + " <queue>");
            return true;
        }
        final String server = args[0];
        if (Hub.getInstance().getQueueManager().getQueue(server) == null) {
            sender.sendMessage(Lang.getString("INVALID_QUEUE"));
            return true;
        }
        final Queue q = Hub.getInstance().getQueueManager().getQueue(server);
        if (q.isPaused()) {
            sender.sendMessage(Lang.getString("QUEUE_UNPAUSED").replace("%server%", q.getServer()));
        }
        else {
            sender.sendMessage(Lang.getString("QUEUE_PAUSED").replace("%server%", q.getServer()));
        }
        q.setPaused(!q.isPaused());
        return true;
    }
}
