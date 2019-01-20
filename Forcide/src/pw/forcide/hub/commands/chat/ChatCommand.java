package pw.forcide.hub.commands.chat;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import pw.forcide.hub.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class ChatCommand implements CommandExecutor, TabCompleter
{
    public static boolean muted;
    public static Map<UUID, Long> nextChat;
    public static int slowTime;
    
    static {
        ChatCommand.nextChat = new HashMap<UUID, Long>();
        ChatCommand.slowTime = 0;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("hub.commands.chat")) {
            sender.sendMessage(Lang.getString("NO_PERMISSION"));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                for (int i = 0; i < 105; ++i) {
                    Bukkit.broadcastMessage("");
                }
                Bukkit.broadcastMessage(Lang.getString("CHAT_CLEARED"));
                return true;
            }
            if (args[0].equalsIgnoreCase("mute")) {
                if (!ChatCommand.muted) {
                    ChatCommand.muted = true;
                    Bukkit.broadcastMessage(Lang.getString("CHAT_MUTE_ENABLED"));
                }
                else {
                    ChatCommand.muted = false;
                    Bukkit.broadcastMessage(Lang.getString("CHAT_MUTE_DISABLED"));
                }
            }
            else {
                if (args[0].equalsIgnoreCase("slow")) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " slow <time>");
                    return true;
                }
                this.showHelp(sender, label);
                return true;
            }
        }
        else {
            if (args.length != 2) {
                this.showHelp(sender, label);
                return true;
            }
            if (args[0].equalsIgnoreCase("slow")) {
                if (Ints.tryParse(args[1]) == null) {
                    sender.sendMessage(Lang.getString("INVALID_NUMBER"));
                    return true;
                }
                final int i = Integer.parseInt(args[1]);
                if (i == 0) {
                    Bukkit.broadcastMessage(Lang.getString("CHAT_SLOW_DISABLED"));
                    ChatCommand.slowTime = 0;
                    return true;
                }
                Bukkit.broadcastMessage(Lang.getString("CHAT_SLOW_ENABLED").replace("%seconds%", String.valueOf(i)));
                ChatCommand.slowTime = i;
            }
        }
        return false;
    }
    
    public void showHelp(final CommandSender p, final String label) {
        p.sendMessage(String.valueOf(ChatColor.BLUE.toString()) + ChatColor.BOLD + "Chat Help" + ChatColor.GRAY + ":");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " clear " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Clear the chat.");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " mute " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Mute or unmute the chat.");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " clear " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Slow down the chat.");
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("clear", "mute", "slow");
        }
        return Collections.emptyList();
    }
}
