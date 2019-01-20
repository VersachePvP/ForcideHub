package pw.forcide.hub.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import pw.forcide.hub.Hub;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.Listener;

public class JumpListener implements Listener
{
    @EventHandler
    public void onFlightToggle(final PlayerToggleFlightEvent e) {
        final Player p = e.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE && Hub.getInstance().getConfig().getBoolean("DoubleJumpEnabled")) {
            e.setCancelled(true);
            p.setFlying(false);
            p.setAllowFlight(false);
            p.setVelocity(e.getPlayer().getLocation().getDirection().multiply(2).setY(1));
            p.playSound(e.getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, 1.0f, 1.0f);
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (e.getTo().getY() != e.getFrom().getY() && p.getGameMode() != GameMode.CREATIVE && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && Hub.getInstance().getConfig().getBoolean("DoubleJumpEnabled")) {
            p.setAllowFlight(true);
        }
    }
}
