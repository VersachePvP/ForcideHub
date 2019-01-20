package pw.forcide.hub.listeners;

import org.spigotmc.event.entity.EntityDismountEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Projectile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.Listener;

public class EnderButtListener implements Listener
{
    @EventHandler
    public void onLaunch(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            final Player p = (Player)e.getEntity().getShooter();
            if (e.getEntity() instanceof EnderPearl) {
                final Projectile pr = e.getEntity();
                if (pr.getType() == EntityType.ENDER_PEARL) {
                    if (p.getVehicle() != null) {
                        p.getVehicle().remove();
                        p.eject();
                    }
                    p.spigot().setCollidesWithEntities(false);
                    pr.setPassenger((Entity)p);
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ENDER_PEARL, 1) });
                    p.updateInventory();
                    p.setItemInHand(p.getItemInHand());
                }
            }
        }
    }
    
    @EventHandler
    public void onDismount(final EntityDismountEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (p != null && p.getVehicle() instanceof EnderPearl) {
                p.spigot().setCollidesWithEntities(true);
                p.eject();
                e.getDismounted().remove();
            }
        }
    }
}
