package pw.forcide.hub.listeners;

import com.google.common.io.ByteArrayDataOutput;
import pw.forcide.hub.utils.Deathban;
import org.bukkit.plugin.Plugin;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang.time.DurationFormatUtils;
import pw.forcide.hub.utils.LivesManager;
import pw.forcide.hub.utils.DeathbanManager;
import pw.forcide.hub.utils.Cooldowns;
import pw.forcide.hub.utils.Lang;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.utils.ServerUtils;
import pw.forcide.hub.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pw.forcide.hub.Hub;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.MapMaker;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;

public class ServerSelectorListener implements Listener
{
    public static Map<UUID, Long> toLogin;
    public static List<Player> invOpen;
    
    static {
        ServerSelectorListener.toLogin = new MapMaker().expireAfterWrite(10L, TimeUnit.SECONDS).makeMap();
        ServerSelectorListener.invOpen = new ArrayList<Player>();
    }
    
    public static Inventory getServerSelector(final Player p) {
        final Inventory i = Bukkit.createInventory((InventoryHolder)null, Hub.getInstance().getConfig().getInt("ServerSelector.Size"), ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getConfig().getString("ServerSelector.Title")));
        for (final String s : Hub.getInstance().getConfig().getConfigurationSection("ServerSelector").getConfigurationSection("Items").getKeys(false)) {
            if (!Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Permission").equals("") && !p.hasPermission(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Permission"))) {
                continue;
            }
            final ItemBuilder ib = new ItemBuilder(Material.getMaterial(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Material")));
            ib.setName(ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Displayname")));
            for (String l : Hub.getInstance().getConfig().getStringList("ServerSelector.Items." + s + ".Lore")) {
                l = l.replace("%status" + s.toLowerCase() + "%", ServerUtils.isOnline(s) ? (Hub.getInstance().getQueueManager().getQueue(s).isPaused() ? "§cMaintenance" : "§aOnline") : "§cOffline").replace("%playerCount" + s.toLowerCase() + "%", String.valueOf(ServerUtils.getPlayerCount(s)));
                if (Hub.getInstance().getQueueManager().getQueue(s) != null) {
                    l = l.replace("%limit" + s.toLowerCase() + "%", String.valueOf(Hub.getInstance().getQueueManager().getQueue(s).getLimit()));
                }
                ib.addLoreLine(ChatColor.translateAlternateColorCodes('&', l));
            }
            if (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) != null && Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p).equalsIgnoreCase(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Server"))) {
                for (final String l : Hub.getInstance().getConfig().getStringList("ServerSelector.QueueLore")) {
                    ib.addLoreLine(ChatColor.translateAlternateColorCodes('&', l.replace("%position%", String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getPosition(p))).replace("%total%", String.valueOf(Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).getSize()))).replace("%queueStatus%", Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p).isPaused() ? "§cPaused" : "§aOpen"));
                }
            }
            final ItemStack it = ib.toItemStack();
            i.setItem(Hub.getInstance().getConfig().getInt("ServerSelector.Items." + s + ".Slot"), it);
        }
        for (int in = 0; in < i.getSize(); ++in) {
            if (i.getItem(in) == null || i.getItem(in).getType() == Material.AIR) {
                final String m = Hub.getInstance().getConfig().getString("ServerSelector.FillMaterial");
                final String[] ms = m.split(":");
                final ItemBuilder ib2 = new ItemBuilder(Material.getMaterial((int)Integer.valueOf(ms[0])));
                if (ms.length == 2) {
                    ib2.setDurability(Short.valueOf(ms[1]));
                }
                ib2.setName(" ");
                i.setItem(in, ib2.toItemStack());
            }
        }
        return i;
    }
    
    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getItem() != null && e.getItem().getType() == Material.BOOK && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            p.closeInventory();
            p.openInventory(getServerSelector(p));
            ServerSelectorListener.invOpen.add(p);
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        final Player p = (Player)e.getPlayer();
        if (ServerSelectorListener.invOpen.contains(p)) {
            ServerSelectorListener.invOpen.remove(p);
        }
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (ServerSelectorListener.invOpen.contains(p)) {
            ServerSelectorListener.invOpen.remove(p);
        }
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getConfig().getString("ServerSelector.Title"))) && e.getCurrentItem() != null) {
            e.setCancelled(true);
            final Player p = (Player)e.getWhoClicked();
            for (final String s : Hub.getInstance().getConfig().getConfigurationSection("ServerSelector").getConfigurationSection("Items").getKeys(false)) {
                if (e.getCurrentItem().getType() == Material.getMaterial(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Material")) && e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Displayname")))) {
                    final String server = Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Server");
                    if (Hub.getInstance().getQueueManager().getQueue((OfflinePlayer)p) != null) {
                        p.sendMessage(Lang.getString("ALREADY_QUEUEING").replace("%server%", Hub.getInstance().getQueueManager().getQueueName((OfflinePlayer)p)));
                        p.closeInventory();
                        return;
                    }
                    if (Cooldowns.hasTimer((OfflinePlayer)p, "join")) {
                        p.sendMessage(Lang.getString("COOLDOWN_MESSAGE").replace("%seconds%", String.valueOf(Cooldowns.getRemaining((OfflinePlayer)p, "join") / 1000L)));
                        p.closeInventory();
                        return;
                    }
                    if (Hub.getInstance().getConfig().getBoolean("SupportOasisDatabase")) {
                        if (server.equalsIgnoreCase("hcf")) {
                            if (DeathbanManager.isDeathbannedHCF(p.getUniqueId())) {
                                final Deathban d = DeathbanManager.getDeathbanHCF(p.getUniqueId());
                                if (ServerSelectorListener.toLogin.containsKey(p.getUniqueId())) {
                                    ServerSelectorListener.toLogin.remove(p.getUniqueId());
                                    LivesManager.setLives((OfflinePlayer)p, LivesManager.getLives((OfflinePlayer)p) - 1);
                                    DeathbanManager.reviveHCF((OfflinePlayer)p);
                                    p.sendMessage("§aYou have used a life! You are now in the queue for HCF.");
                                    Hub.getInstance().getQueueManager().getQueue("hcf").addEntry((OfflinePlayer)p);
                                    Cooldowns.addTimer((OfflinePlayer)p, "join", TimeUnit.SECONDS.toMillis(3L));
                                    return;
                                }
                                p.sendMessage("§c§lYou are deathbanned for " + DurationFormatUtils.formatDurationWords(d.getRemaining() * 1000L, true, true) + "!");
                                if (LivesManager.getLives((OfflinePlayer)p) <= 0) {
                                    continue;
                                }
                                final int lives = LivesManager.getLives((OfflinePlayer)p);
                                p.sendMessage("§eYou have §a" + lives + ((lives != 1) ? " §elives." : " §elife."));
                                p.sendMessage("§eClick again to confirm usage of a life.");
                                ServerSelectorListener.toLogin.put(p.getUniqueId(), System.currentTimeMillis() + 30000L);
                            }
                            else {
                                p.sendMessage(Lang.getString("QUEUE_JOINED").replace("%server%", "HCF"));
                                Hub.getInstance().getQueueManager().getQueue("hcf").addEntry((OfflinePlayer)p);
                                Cooldowns.addTimer((OfflinePlayer)p, "join", TimeUnit.SECONDS.toMillis(3L));
                                p.closeInventory();
                            }
                        }
                        else {
                            if (!server.equalsIgnoreCase("kits")) {
                                continue;
                            }
                            if (DeathbanManager.isDeathbannedKits(p.getUniqueId()) && !p.hasPermission("hub.staff")) {
                                p.sendMessage("§c§lYou have died during EOTW! Check the forums for information about next season.");
                                p.closeInventory();
                                return;
                            }
                            p.sendMessage(Lang.getString("QUEUE_JOINED").replace("%server%", "Kits"));
                            Hub.getInstance().getQueueManager().getQueue("kits").addEntry((OfflinePlayer)p);
                            Cooldowns.addTimer((OfflinePlayer)p, "join", TimeUnit.SECONDS.toMillis(3L));
                            p.closeInventory();
                        }
                    }
                    else {
                        if (Hub.getInstance().getConfig().getBoolean("ServerSelector.Items." + s + ".Queue")) {
                            p.sendMessage(Lang.getString("QUEUE_JOINED").replace("%server%", s));
                            Hub.getInstance().getQueueManager().getQueue(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Server")).addEntry((OfflinePlayer)p);
                        }
                        else {
                            p.sendMessage(Lang.getString("CONNECTING").replace("%server%", s));
                            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Connect");
                            out.writeUTF(Hub.getInstance().getConfig().getString("ServerSelector.Items." + s + ".Server"));
                            p.sendPluginMessage((Plugin)Hub.getInstance(), Hub.getInstance().getConfig().getString("MessagingChannel"), out.toByteArray());
                        }
                        Cooldowns.addTimer((OfflinePlayer)p, "join", TimeUnit.SECONDS.toMillis(3L));
                        p.closeInventory();
                    }
                }
            }
        }
    }
}
