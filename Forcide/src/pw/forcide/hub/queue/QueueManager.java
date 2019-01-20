package pw.forcide.hub.queue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.OfflinePlayer;
import java.util.Iterator;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;
import pw.forcide.hub.utils.ServerUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
import pw.forcide.hub.Hub;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;

public class QueueManager implements Listener
{
    private List<Queue> queues;
    
    public QueueManager() {
        this.queues = new ArrayList<Queue>();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Hub.getInstance());
        for (final String s : Hub.getInstance().getConfig().getStringList("Queue.Servers")) {
            this.queues.add(new Queue(s));
        }
        new BukkitRunnable() {
            public void run() {
                for (final Queue q : QueueManager.this.queues) {
                    final int playerCount = ServerUtils.getPlayerCount(q.getServer());
                    if (!q.isPaused() && !q.getPlayers().isEmpty() && playerCount < q.getLimit()) {
                        if (q.getPlayerAt(0).isOnline()) {
                            q.sendFirst();
                        }
                        if (q.getPlayerAt(0).isOnline()) {
                            continue;
                        }
                        final Player p = (Player)q.getPlayerAt(0);
                        if (q.getPlayers().contains(p)) {
                            q.getPlayers().remove(p);
                        }
                        if (!q.getTaskMap().containsKey(p)) {
                            continue;
                        }
                        q.getTaskMap().get(p).cancel();
                        q.getTaskMap().remove(p);
                    }
                }
            }
        }.runTaskTimer((Plugin)Hub.getInstance(), 20L, (long)Hub.getInstance().getConfig().getInt("Queue.SendDelay"));
    }
    
    public Queue getQueue(final OfflinePlayer p) {
        for (final Queue q : this.queues) {
            if (q.getPlayers().contains(p)) {
                return q;
            }
        }
        return null;
    }
    
    public Queue getQueue(final String s) {
        for (final Queue q : this.queues) {
            if (q.getServer().equalsIgnoreCase(s)) {
                return q;
            }
        }
        return null;
    }
    
    public String getQueueName(final OfflinePlayer p) {
        return this.getQueue(p).getServer();
    }
    
    public int getPriority(final OfflinePlayer p) {
        return Hub.getInstance().getConfig().getInt("Ranks." + Hub.getInstance().getPerms().getPrimaryGroup((String)null, p) + ".Priority");
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        for (final Queue q : this.queues) {
            if (q.getPlayers().contains(p)) {
                q.removeEntry((OfflinePlayer)p);
            }
        }
    }
}
