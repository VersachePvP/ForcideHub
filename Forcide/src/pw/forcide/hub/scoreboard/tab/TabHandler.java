package pw.forcide.hub.scoreboard.tab;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.concurrent.ConcurrentHashMap;
import pw.forcide.hub.scoreboard.tab.utils.TabAdapter;
import pw.forcide.hub.scoreboard.tab.utils.Tab;
import java.util.UUID;
import java.util.Map;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class TabHandler implements Listener
{
    private JavaPlugin plugin;
    private Map<UUID, Tab> tabs;
    private TabAdapter adapter;
    
    public TabHandler(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.tabs = new ConcurrentHashMap<UUID, Tab>();
        if (Bukkit.getMaxPlayers() < 60) {
            System.out.println("Server slots must be at least 60.");
        }
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player player = onlinePlayers[i];
            if (!this.tabs.containsKey(player.getUniqueId())) {
                this.tabs.put(player.getUniqueId(), new Tab(player, this));
            }
        }
        new TabTask(this, plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    public TabHandler(final JavaPlugin plugin, final TabAdapter a) {
        this(plugin);
        this.adapter = a;
    }
    
    public Tab getTabByPlayer(final Player p) {
        return this.tabs.get(p.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent e) {
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player all = onlinePlayers[i];
            final PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)e.getPlayer()).getHandle());
            ((CraftPlayer)all).getHandle().playerConnection.sendPacket((Packet)packet);
        }
        new BukkitRunnable() {
            public void run() {
                if (e.getPlayer().isOnline()) {
                    TabHandler.this.tabs.put(e.getPlayer().getUniqueId(), new Tab(e.getPlayer(), TabHandler.this));
                }
            }
        }.runTaskLater((Plugin)this.plugin, 40L);
    }
    
    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent e) {
        this.tabs.remove(e.getPlayer().getUniqueId());
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player other = onlinePlayers[i];
            final EntityPlayer entityPlayer = ((CraftPlayer)other).getHandle();
            if (entityPlayer.playerConnection.networkManager.getVersion() >= 47) {
                final Tab tab = this.getTabByPlayer(e.getPlayer());
                if (tab != null && tab.getElevatedTeam() != null) {
                    tab.getElevatedTeam().removeEntry(e.getPlayer().getName());
                }
            }
        }
    }
    
    public Map<UUID, Tab> getTabs() {
        return this.tabs;
    }
    
    public TabAdapter getAdapter() {
        return this.adapter;
    }
    
    public void setAdapter(final TabAdapter adapter) {
        this.adapter = adapter;
    }
}
