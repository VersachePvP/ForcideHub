package pw.forcide.hub.scoreboard;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Iterator;
import pw.forcide.hub.utils.TimeUtils;
import pw.forcide.hub.utils.DeathbanManager;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.utils.ServerUtils;
import java.util.Map;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import pw.forcide.hub.Hub;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class ScoreboardHandler implements Listener
{
    private HashMap<UUID, ScoreboardHelper> helperMap;
    private String title;
    
    public ScoreboardHandler() {
        this.helperMap = new HashMap<UUID, ScoreboardHelper>();
        this.title = ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getConfig().getString("Scoreboard.Title"));
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Hub.getInstance());
        this.register();
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player all = onlinePlayers[i];
            this.onJoin(all);
        }
    }
    
    public void register() {
        new BukkitRunnable() {
            public void run() {
                for (final Map.Entry<UUID, ScoreboardHelper> e : ScoreboardHandler.this.helperMap.entrySet()) {
                    final Player p = Bukkit.getPlayer((UUID)e.getKey());
                    if (p != null) {
                        final ScoreboardHelper h = e.getValue();
                        h.clear();
                        for (String s : Hub.getInstance().getConfig().getStringList("Scoreboard.Entries")) {
                            boolean b = false;
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
                            if (s.contains("%queueServer%") || s.contains("%position%") || s.contains("%total%") || s.contains("%empty%")) {
                                if (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) != null) {
                                    h.add(s.replace("%queueServer%", Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p)).replace("%position%", String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getPosition(p))).replace("%total%", String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getSize())).replace("%empty%", ""));
                                    b = true;
                                }
                                else {
                                    b = true;
                                }
                            }
                            if (s.contains("%deathban%")) {
                                if (DeathbanManager.isDeathbannedHCF(p.getUniqueId())) {
                                    h.add(s.replace("%deathban%", "§cDeathban§7: §f" + TimeUtils.format((int)DeathbanManager.getDeathbanHCF(p.getUniqueId()).getRemaining())));
                                    b = true;
                                }
                                else {
                                    b = true;
                                }
                            }
                            if (!b) {
                                h.add(ChatColor.translateAlternateColorCodes('&', s.replace("%globalPlayerCount%", String.valueOf(ServerUtils.getPlayerCount("all"))).replace("%rank%", Hub.getInstance().getConfig().getString("Ranks." + Hub.getInstance().getPerms().getPrimaryGroup(p) + ".Displayname"))));
                            }
                        }
                        h.update(p);
                    }
                }
            }
        }.runTaskTimer((Plugin)Hub.getInstance(), 20L, 20L);
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        this.onJoin(e.getPlayer());
    }
    
    public String format(final int i) {
        final int r = i * 1000;
        final int sec = r / 1000 % 60;
        final int min = r / 60000 % 60;
        final int h = r / 3600000 % 24;
        return String.valueOf((h > 0) ? new StringBuilder(String.valueOf(h)).append(":").toString() : "") + ((min < 10) ? ("0" + min) : min) + ":" + ((sec < 10) ? ("0" + sec) : sec);
    }
    
    public void onJoin(final Player p) {
        new BukkitRunnable() {
            public void run() {
                if (p != null) {
                    final Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
                    final ScoreboardHelper sh = new ScoreboardHelper(s, ScoreboardHandler.this.title);
                    ScoreboardHandler.this.helperMap.put(p.getUniqueId(), sh);
                }
            }
        }.runTaskLater((Plugin)Hub.getInstance(), 20L);
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
        this.helperMap.remove(e.getPlayer().getUniqueId());
    }
}
