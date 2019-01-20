package pw.forcide.hub.commands;

import pw.forcide.hub.queue.Queue;
import net.minecraft.util.com.google.common.primitives.Ints;
import pw.forcide.hub.Hub;
import pw.forcide.hub.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SetLimitCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("hub.queue.setlimit")) {
            sender.sendMessage(Lang.getString("NO_PERMISSION"));
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /" + label + " <amount>");
            return true;
        }
        final String server = args[0];
        if (Hub.getInstance().getQueueManager().getQueue(server) == null) {
            sender.sendMessage(Lang.getString("INVALID_QUEUE"));
            return true;
        }
        final Queue q = Hub.getInstance().getQueueManager().getQueue(server);
        if (Ints.tryParse(args[1]) == null) {
            sender.sendMessage(Lang.getString("INVALID_NUMBER"));
            return true;
        }
        final int i = Ints.tryParse(args[1]);
        q.setLimit(i);
        sender.sendMessage(Lang.getString("LIMIT_SET").replace("%server%", q.getServer()).replace("%amount%", String.valueOf(i)));
        return true;
    }
}
