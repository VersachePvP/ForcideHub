package pw.forcide.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import pw.forcide.hub.utils.Lang;
import org.bukkit.inventory.ItemStack;
import pw.forcide.hub.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class JoinListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (!p.isOp()) {
            p.getInventory().clear();
        }
        p.spigot().setCollidesWithEntities(false);
        p.getInventory().setItem(0, new ItemBuilder(Material.BOOK).setName("§6Server Selector").addLoreLine("§eSelect which server to play.").toItemStack());
        p.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL, 1));
        p.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK, 1).setDurability((short)8).setName("§eToggle Player Visibility").toItemStack());
        Lang.sendList(p, "WELCOME_MESSAGE");
    }
}
