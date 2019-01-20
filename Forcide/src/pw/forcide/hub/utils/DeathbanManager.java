package pw.forcide.hub.utils;

import org.bukkit.OfflinePlayer;
import com.mongodb.DBObject;
import pw.forcide.hub.Hub;
import com.mongodb.BasicDBObject;
import java.util.UUID;

public class DeathbanManager
{
    public static boolean isDeathbannedHCF(final UUID u) {
        return getDeathbanHCF(u) != null && getDeathbanHCF(u).isActive();
    }
    
    public static Deathban getDeathbanHCF(final UUID u) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)u);
        final DBObject f = Hub.getInstance().deathbansHcf.findOne(r);
        if (f == null) {
            return null;
        }
        final Deathban d = new Deathban(f.get("deathban").toString());
        return d;
    }
    
    public static void reviveHCF(final OfflinePlayer o) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)o.getUniqueId());
        final DBObject f = Hub.getInstance().deathbansHcf.findOne(r);
        final Deathban d = new Deathban(f.get("deathban").toString());
        if (d != null && d.isActive()) {
            Hub.getInstance().deathbansHcf.remove(f);
        }
    }
    
    public static boolean isDeathbannedKits(final UUID u) {
        return getDeathbanKits(u) != null && getDeathbanKits(u).isActive();
    }
    
    public static Deathban getDeathbanKits(final UUID u) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)u);
        final DBObject f = Hub.getInstance().deathbansKits.findOne(r);
        if (f == null) {
            return null;
        }
        final Deathban d = new Deathban(f.get("deathban").toString());
        return d;
    }
    
    public static void reviveKits(final OfflinePlayer o) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)o.getUniqueId());
        final DBObject f = Hub.getInstance().deathbansKits.findOne(r);
        final Deathban d = new Deathban(f.get("deathban").toString());
        if (d != null && d.isActive()) {
            Hub.getInstance().deathbansKits.remove(f);
        }
    }
}
