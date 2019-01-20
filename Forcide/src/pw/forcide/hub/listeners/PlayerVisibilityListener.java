package pw.forcide.hub.listeners;

import java.util.concurrent.TimeUnit;
import pw.forcide.hub.utils.ItemBuilder;
import org.bukkit.Material;
import pw.forcide.hub.utils.Lang;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.utils.Cooldowns;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import pw.forcide.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import org.bukkit.event.Listener;

public class PlayerVisibilityListener implements Listener
{
    private List<UUID> list;
    
    public PlayerVisibilityListener() {
        this.list = new ArrayList<UUID>();
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        new BukkitRunnable() {
            public void run() {
                Player[] onlinePlayers;
                for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
                    final Player all = onlinePlayers[i];
                    p.hidePlayer(all);
                    if (PlayerVisibilityListener.this.list.contains(all.getUniqueId())) {
                        all.hidePlayer(p);
                    }
                }
            }
        }.runTaskLater((Plugin)Hub.getInstance(), 10L);
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
        if (this.list.contains(e.getPlayer().getUniqueId())) {
            this.list.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().getDisplayName().equals("§eToggle Player Visibility")) {
            if (Cooldowns.hasTimer((OfflinePlayer)p, "visibility")) {
                p.sendMessage(Lang.getString("COOLDOWN_MESSAGE").replace("%seconds%", String.valueOf(Cooldowns.getRemaining((OfflinePlayer)p, "visibility") / 1000L)));
                return;
            }
            if (e.getItem().getData().getData() == 10) {
                if (!this.list.contains(p.getUniqueId())) {
                    this.list.add(p.getUniqueId());
                }
                Player[] onlinePlayers;
                for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
                    final Player all = onlinePlayers[i];
                    if (p.canSee(all)) {
                        p.hidePlayer(all);
                    }
                }
                p.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability((short)8).setName("§eToggle Player Visibility").toItemStack());
                p.sendMessage(Lang.getString("PLAYERS_HIDDEN"));
                Cooldowns.addTimer((OfflinePlayer)p, "visibility", TimeUnit.SECONDS.toMillis(5L));
            }
            else if (e.getItem().getData().getData() == 8) {
                if (this.list.contains(p.getUniqueId())) {
                    this.list.remove(p.getUniqueId());
                }
                Player[] onlinePlayers2;
                for (int length2 = (onlinePlayers2 = Bukkit.getOnlinePlayers()).length, j = 0; j < length2; ++j) {
                    final Player all = onlinePlayers2[j];
                    if (!p.canSee(all)) {
                        p.showPlayer(all);
                    }
                }
                p.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability((short)10).setName("§eToggle Player Visibility").toItemStack());
                p.sendMessage(Lang.getString("PLAYERS_SHOWN"));
                Cooldowns.addTimer((OfflinePlayer)p, "visibility", TimeUnit.SECONDS.toMillis(5L));
            }
        }
    }
}
