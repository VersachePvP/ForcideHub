package pw.forcide.hub.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils
{
    public static Location fromString(final String s) {
        final String[] split = s.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
    
    public static String toString(final Location l) {
        return String.valueOf(l.getWorld().getName()) + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }
}
