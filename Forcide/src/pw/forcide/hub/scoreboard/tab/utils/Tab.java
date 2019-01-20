package pw.forcide.hub.scoreboard.tab.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import pw.forcide.hub.scoreboard.tab.TabHandler;

public class Tab {
	
    private Scoreboard scoreboard;
    private Team elevatedTeam;
    private Map<TabEntryPosition, String> entries;
    
    public Tab(final Player p, final TabHandler t) {
        this.entries = new ConcurrentHashMap<TabEntryPosition, String>();
        final Scoreboard scoreboard = p.getScoreboard();
        this.scoreboard = scoreboard;
        this.elevatedTeam = this.scoreboard.registerNewTeam((String)this.getBlanks().get(this.getBlanks().size() - 1));
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player all = onlinePlayers[i];
            this.getElevatedTeam(all, t).addEntry(all.getName());
            final Tab tab = t.getTabByPlayer(all);
            if (tab != null) {
                tab.getElevatedTeam(p, t).addEntry(p.getName());
            }
            final PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)all).getHandle());
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)packet);
        }
        this.initialize(p);
    }
    
    public Team getElevatedTeam(final Player player, final TabHandler azazel) {
        if (player.hasMetadata("TabPrefix")) {
            final String prefix = ChatColor.getLastColors(player.getDisplayName().replace(new StringBuilder().append(ChatColor.RESET).toString(), ""));
            String name = String.valueOf(this.getBlanks().get(this.getBlanks().size() - 1)) + prefix;
            if (name.length() > 16) {
                name = name.substring(0, 15);
            }
            Team team = this.scoreboard.getTeam(name);
            if (team == null) {
                team = this.scoreboard.registerNewTeam(name);
                team.setPrefix(prefix);
            }
            return team;
        }
        return this.elevatedTeam;
    }
    
    public Set<TabEntryPosition> getPositions() {
        return this.entries.keySet();
    }
    
    public Team getByLocation(final int x, final int y) {
        for (final TabEntryPosition position : this.entries.keySet()) {
            if (position.getX() == x && position.getY() == y) {
                return this.scoreboard.getTeam(position.getKey());
            }
        }
        return null;
    }
    
    private void initialize(final Player player) {
        if (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
            for (int x = 0; x < 4; ++x) {
                for (int y = 0; y < 20; ++y) {
                    final String key = this.getNextBlank();
                    final TabEntryPosition position = new TabEntryPosition(x, y, key);
                    this.entries.put(position, key);
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(getPlayerPacket(this.entries.get(position)));
                    Team team = this.scoreboard.getTeam(position.getKey());
                    if (team == null) {
                        team = this.scoreboard.registerNewTeam(position.getKey());
                    }
                    team.addEntry((String)this.entries.get(position));
                }
            }
        }
        else {
            for (int i = 0; i < 60; ++i) {
                final int x2 = i % 3;
                final int y2 = i / 3;
                final String key2 = this.getNextBlank();
                final TabEntryPosition position2 = new TabEntryPosition(x2, y2, key2);
                this.entries.put(position2, key2);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(getPlayerPacket(this.entries.get(position2)));
                Team team2 = this.scoreboard.getTeam(position2.getKey());
                if (team2 == null) {
                    team2 = this.scoreboard.registerNewTeam(position2.getKey());
                }
                team2.addEntry((String)this.entries.get(position2));
            }
        }
    }
    
    private String getNextBlank() {
    Label_0092:
        for (final String blank : this.getBlanks()) {
            if (this.scoreboard.getTeam(blank) != null) {
                continue;
            }
            for (final String identifier : this.entries.values()) {
                if (identifier.equals(blank)) {
                    continue Label_0092;
                }
            }
            return blank;
        }
        return null;
    }
    
    public List<String> getBlanks() {
        final List<String> toReturn = new ArrayList<String>();
        ChatColor[] values;
        for (int length = (values = ChatColor.values()).length, j = 0; j < length; ++j) {
            final ChatColor color = values[j];
            for (int i = 0; i < 4; ++i) {
                final String identifier = StringUtils.repeat(new StringBuilder().append(color).toString(), 4 - i);
                toReturn.add(identifier);
            }
        }
        return toReturn;
    }
    
    private static Packet getPlayerPacket(final String name) {
        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        try {
            final Field action = PacketPlayOutPlayerInfo.class.getDeclaredField("action");
            final Field username = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
            final Field player = PacketPlayOutPlayerInfo.class.getDeclaredField("player");
            action.setAccessible(true);
            username.setAccessible(true);
            player.setAccessible(true);
            action.set(packet, 0);
            username.set(packet, name);
            player.set(packet, new GameProfile(UUID.randomUUID(), name));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return (Packet)packet;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public Team getElevatedTeam() {
        return this.elevatedTeam;
    }
    
    public void setElevatedTeam(final Team elevatedTeam) {
        this.elevatedTeam = elevatedTeam;
    }
    
    public static class TabEntryPosition
    {
        private int x;
        private int y;
        private String key;
        
        public TabEntryPosition(final int x, final int y, final String key) {
            this.x = x;
            this.y = y;
            this.key = key;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public String getKey() {
            return this.key;
        }
    }
}
