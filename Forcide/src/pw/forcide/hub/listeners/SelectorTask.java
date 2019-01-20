package pw.forcide.hub.listeners;

import org.bukkit.inventory.Inventory;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SelectorTask extends BukkitRunnable
{
    public void run() {
        for (final Player p : ServerSelectorListener.invOpen) {
            if (p.getOpenInventory() == null) {
                ServerSelectorListener.invOpen.remove(p);
            }
            final Inventory i = p.getOpenInventory().getTopInventory();
            i.setContents(ServerSelectorListener.getServerSelector(p).getContents());
        }
    }
}
