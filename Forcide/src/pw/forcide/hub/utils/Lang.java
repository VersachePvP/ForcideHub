package pw.forcide.hub.utils;

import java.util.Iterator;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import pw.forcide.hub.Hub;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public class Lang
{
    private static File f;
    private static FileConfiguration data;
    
    static {
        Lang.f = new File(Hub.getInstance().getDataFolder(), "lang.yml");
        Lang.data = (FileConfiguration)YamlConfiguration.loadConfiguration(Lang.f);
    }
    
    public static String getString(final String path) {
        if (!Lang.data.contains(path)) {
            return "Message not found!";
        }
        return ChatColor.translateAlternateColorCodes('&', Lang.data.getString(path));
    }
    
    public static List<String> getList(final String path) {
        if (!Lang.data.contains(path)) {
            return Arrays.asList("Message not found!");
        }
        return (List<String>)Lang.data.getStringList(path);
    }
    
    public static void sendList(final Player p, final String path) {
        if (!Lang.data.contains(path)) {
            p.sendMessage("Message not found!");
        }
        else {
            final List<String> list = (List<String>)Lang.data.getStringList(path);
            for (final String s : list) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }
}
