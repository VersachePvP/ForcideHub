package pw.forcide.hub.listeners;

import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import pw.forcide.hub.utils.Cooldowns;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import pw.forcide.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class ServerListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.setJoinMessage((String)null);
        final Location spawn = Bukkit.getWorld("world").getSpawnLocation().clone();
        float yaw = 0.0f;
        final String s = Hub.getInstance().getConfig().getString("SpawnFace");
        if (s.equalsIgnoreCase("NORTH")) {
            yaw = -180.0f;
        }
        else if (s.equalsIgnoreCase("SOUTH")) {
            yaw = 0.0f;
        }
        else if (s.equalsIgnoreCase("EAST")) {
            yaw = -90.0f;
        }
        else if (s.equalsIgnoreCase("WEST")) {
            yaw = 90.0f;
        }
        spawn.setYaw(yaw);
        spawn.add(0.5, 0.5, 0.5);
        e.getPlayer().teleport(spawn);
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        e.setQuitMessage((String)null);
        if (Cooldowns.hasTimer((OfflinePlayer)p, "join")) {
            Cooldowns.stopTimer((OfflinePlayer)p, "join");
        }
        if (Cooldowns.hasTimer((OfflinePlayer)p, "visibility")) {
            Cooldowns.stopTimer((OfflinePlayer)p, "visibility");
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            final Player p = (Player)e.getEntity();
            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        e.setCancelled(true);
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (p.getFoodLevel() != 20) {
                p.setFoodLevel(20);
            }
        }
    }
    
    @EventHandler
    public void onSpawn(final EntitySpawnEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onWeather(final WeatherChangeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onDrop(final PlayerDropItemEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlace(final BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (!e.getWhoClicked().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onTarget(final EntityTargetEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onTarget2(final EntityTargetLivingEntityEvent e) {
        e.setCancelled(true);
    }
}
