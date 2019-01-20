package pw.forcide.hub.utils;

import java.util.HashMap;
import org.bukkit.Bukkit;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Deathban implements ConfigurationSerializable {
	
    private UUID uuid;
    private Location location;
    private String reason;
    private long expire;
    
    public Deathban(final Player p) {
        this.uuid = p.getUniqueId();
        this.location = null;
        this.reason = null;
        this.expire = 0L;
    }
    
    public Deathban(final UUID u) {
        this.uuid = u;
        this.location = null;
        this.reason = null;
        this.expire = 0L;
    }
    
    public Deathban(final Map<String, Object> map) {
        this.uuid = UUID.fromString((String) map.get("uuid"));
        final String[] loc = ((String) map.get("location")).split(",");
        this.location = new Location(Bukkit.getWorld(loc[0]), (double)Integer.valueOf(loc[1]), (double)Integer.valueOf(loc[2]), (double)Integer.valueOf(loc[3]));
        this.reason = (String) map.get("reason");
        this.expire = (long) map.get("expire");
    }
    
    public Deathban(final String sp) {
        final String[] s = sp.split("=");
        this.uuid = UUID.fromString(s[0]);
        this.location = LocationUtils.fromString(s[1]);
        this.reason = s[2];
        this.expire = Long.valueOf(s[3]);
    }
    
    public long getRemaining() {
        return (this.expire - System.currentTimeMillis()) / 1000L;
    }
    
    public boolean isActive() {
        return System.currentTimeMillis() < this.expire;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.uuid.toString()) + "=" + LocationUtils.toString(this.location) + "=" + this.reason.replace("=", " ") + "=" + this.expire;
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("expire", this.expire);
        map.put("uuid", this.uuid.toString());
        map.put("reason", this.reason);
        map.put("location", String.valueOf(this.location.getWorld().getName()) + "," + this.location.getBlockX() + "," + this.location.getBlockY() + "," + this.location.getBlockZ());
        return map;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public long getExpire() {
        return this.expire;
    }
    
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
    
    public void setLocation(final Location location) {
        this.location = location;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public void setExpire(final long expire) {
        this.expire = expire;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deathban)) {
            return false;
        }
        final Deathban other = (Deathban)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        Label_0065: {
            if (this$uuid == null) {
                if (other$uuid == null) {
                    break Label_0065;
                }
            }
            else if (this$uuid.equals(other$uuid)) {
                break Label_0065;
            }
            return false;
        }
        final Object this$location = this.getLocation();
        final Object other$location = other.getLocation();
        Label_0102: {
            if (this$location == null) {
                if (other$location == null) {
                    break Label_0102;
                }
            }
            else if (this$location.equals(other$location)) {
                break Label_0102;
            }
            return false;
        }
        final Object this$reason = this.getReason();
        final Object other$reason = other.getReason();
        if (this$reason == null) {
            if (other$reason == null) {
                return this.getExpire() == other.getExpire();
            }
        }
        else if (this$reason.equals(other$reason)) {
            return this.getExpire() == other.getExpire();
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof Deathban;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
        final Object $location = this.getLocation();
        result = result * 59 + (($location == null) ? 43 : $location.hashCode());
        final Object $reason = this.getReason();
        result = result * 59 + (($reason == null) ? 43 : $reason.hashCode());
        final long $expire = this.getExpire();
        result = result * 59 + (int)($expire ^ $expire >>> 32);
        return result;
    }
}
