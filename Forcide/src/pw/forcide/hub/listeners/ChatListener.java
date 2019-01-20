package pw.forcide.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pw.forcide.hub.Hub;
import pw.forcide.hub.utils.Lang;
import pw.forcide.hub.commands.chat.ChatCommand;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (ChatCommand.muted && !p.hasPermission("hub.chat.bypass")) {
            e.setCancelled(true);
            p.sendMessage(Lang.getString("CHAT_MUTED"));
            return;
        }
        if (ChatCommand.slowTime > 0 && !p.hasPermission("hub.chat.bypass")) {
            if (ChatCommand.nextChat != null && ChatCommand.nextChat.containsKey(p.getUniqueId()) && System.currentTimeMillis() < ChatCommand.nextChat.get(p.getUniqueId())) {
                e.setCancelled(true);
                p.sendMessage(Lang.getString("CHAT_SLOWED").replace("%seconds%", String.valueOf((ChatCommand.nextChat.get(p.getUniqueId()) - System.currentTimeMillis()) / 1000L)));
                return;
            }
            ChatCommand.nextChat.put(p.getUniqueId(), System.currentTimeMillis() + ChatCommand.slowTime * 1000);
        }
        e.setCancelled(true);
        final String color = p.hasPermission("hub.staff") ? ChatColor.valueOf(Hub.getInstance().getConfig().getString("ChatColors.Staff")).toString() : ChatColor.valueOf(Hub.getInstance().getConfig().getString("ChatColors.NonStaff")).toString();
        Bukkit.broadcastMessage(Lang.getString("CHAT_FORMAT").replace("%prefix%", ChatColor.translateAlternateColorCodes('&', Hub.getInstance().getChat().getPlayerPrefix(p))).replace("%player%", p.getName()).replace("%color%", color).replace("%message%", e.getMessage()));
    }
}
