package pw.forcide.hub.scoreboard.tab;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.utils.LivesManager;
import pw.forcide.hub.utils.ServerUtils;
import pw.forcide.hub.Hub;
import pw.forcide.hub.scoreboard.tab.utils.TabTemplate;
import org.bukkit.entity.Player;
import pw.forcide.hub.scoreboard.tab.utils.TabAdapter;

public class HubTabAdapter implements TabAdapter
{
    @Override
    public TabTemplate getTemplate(final Player p) {
        final TabTemplate t = new TabTemplate();
        for (String s : Hub.getInstance().getConfig().getStringList("Tab.Left")) {
            if (s.contains("%playerCount")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%playerCount" + a.toLowerCase() + "%", String.valueOf(ServerUtils.getPlayerCount(a)));
                }
            }
            if (s.contains("%limit")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%limit" + a.toLowerCase() + "%", String.valueOf(Hub.getInstance().getQueueManager().getQueue(a).getLimit()));
                }
            }
            if (s.contains("%status")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%status" + a.toLowerCase() + "%", ServerUtils.isOnline(a) ? (Hub.getInstance().getQueueManager().getQueue(a).isPaused() ? "§cMaintenance" : "§aOnline") : "§cOffline");
                }
            }
            if (Hub.getInstance().getConfig().getBoolean("SupportOasisDatabase")) {
                s = s.replace("%lives%", String.valueOf(LivesManager.getLives((OfflinePlayer)p)));
            }
            t.left(ChatColor.translateAlternateColorCodes('&', s.replace("%rank%", Hub.getInstance().getConfig().getString("Ranks." + Hub.getInstance().getPerms().getPrimaryGroup(p) + ".Displayname")).replace("%queue%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "§cNone" : ("§f" + Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p))).replace("%position%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getPosition(p))).replace("%total%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getSize())).replace("%globalPlayerCount%", String.valueOf(ServerUtils.getPlayerCount("all")))));
        }
        for (String s : Hub.getInstance().getConfig().getStringList("Tab.Middle")) {
            if (s.contains("%playerCount")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%playerCount" + a.toLowerCase() + "%", String.valueOf(ServerUtils.getPlayerCount(a)));
                }
            }
            if (s.contains("%limit")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%limit" + a.toLowerCase() + "%", String.valueOf(Hub.getInstance().getQueueManager().getQueue(a).getLimit()));
                }
            }
            if (s.contains("%status")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%status" + a.toLowerCase() + "%", ServerUtils.isOnline(a) ? (Hub.getInstance().getQueueManager().getQueue(a).isPaused() ? "§cMaintenance" : "§aOnline") : "§cOffline");
                }
            }
            if (Hub.getInstance().getConfig().getBoolean("SupportOasisDatabase")) {
                s = s.replace("%lives%", String.valueOf(LivesManager.getLives((OfflinePlayer)p)));
            }
            t.middle(ChatColor.translateAlternateColorCodes('&', s.replace("%rank%", Hub.getInstance().getConfig().getString("Ranks." + Hub.getInstance().getPerms().getPrimaryGroup(p) + ".Displayname")).replace("%queue%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "§cNone" : ("§f" + Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p))).replace("%position%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getPosition(p))).replace("%total%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getSize())).replace("%globalPlayerCount%", String.valueOf(ServerUtils.getPlayerCount("all")))));
        }
        for (String s : Hub.getInstance().getConfig().getStringList("Tab.Right")) {
            if (s.contains("%playerCount")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%playerCount" + a.toLowerCase() + "%", String.valueOf(ServerUtils.getPlayerCount(a)));
                }
            }
            if (s.contains("%limit")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%limit" + a.toLowerCase() + "%", String.valueOf(Hub.getInstance().getQueueManager().getQueue(a).getLimit()));
                }
            }
            if (s.contains("%status")) {
                for (final String a : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
                    s = s.replace("%status" + a.toLowerCase() + "%", ServerUtils.isOnline(a) ? (Hub.getInstance().getQueueManager().getQueue(a).isPaused() ? "§cMaintenance" : "§aOnline") : "§cOffline");
                }
            }
            if (Hub.getInstance().getConfig().getBoolean("SupportOasisDatabase")) {
                s = s.replace("%lives%", String.valueOf(LivesManager.getLives((OfflinePlayer)p)));
            }
            t.right(ChatColor.translateAlternateColorCodes('&', s.replace("%rank%", Hub.getInstance().getConfig().getString("Ranks." + Hub.getInstance().getPerms().getPrimaryGroup(p) + ".Displayname")).replace("%queue%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "§cNone" : ("§f" + Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p))).replace("%position%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getPosition(p))).replace("%total%", (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) == null) ? "0" : String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getSize())).replace("%globalPlayerCount%", String.valueOf(ServerUtils.getPlayerCount("all")))));
        }
        return t;
    }
}
