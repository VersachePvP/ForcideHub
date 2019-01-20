package pw.forcide.hub.utils;

import java.util.UUID;
import com.mongodb.DBObject;
import pw.forcide.hub.Hub;
import com.mongodb.BasicDBObject;
import org.bukkit.OfflinePlayer;

public class LivesManager
{
    public static int getLives(final OfflinePlayer p) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)p.getUniqueId());
        DBObject i = Hub.getInstance().lives.findOne(r);
        if (i == null) {
            final DBObject obj = (DBObject)new BasicDBObject("uuid", (Object)p.getUniqueId());
            obj.put("lives", (Object)0);
            Hub.getInstance().lives.insert(new DBObject[] { obj });
            i = Hub.getInstance().lives.findOne(r);
        }
        return ((Number)i.get("lives")).intValue();
    }
    
    public static int getLives(final UUID u) {
        final DBObject r = (DBObject)new BasicDBObject("uuid", (Object)u);
        DBObject i = Hub.getInstance().lives.findOne(r);
        if (i == null) {
            final DBObject obj = (DBObject)new BasicDBObject("uuid", (Object)u);
            obj.put("lives", (Object)0);
            Hub.getInstance().lives.insert(new DBObject[] { obj });
            i = Hub.getInstance().lives.findOne(r);
        }
        return ((Number)i.get("lives")).intValue();
    }
    
    public static void setLives(final OfflinePlayer p, final int l) {
        final DBObject obj = (DBObject)new BasicDBObject("uuid", (Object)p.getUniqueId());
        final DBObject f = Hub.getInstance().lives.findOne(obj);
        final BasicDBObject set = new BasicDBObject("$set", (Object)f);
        set.append("$set", (Object)new BasicDBObject("lives", (Object)l));
        Hub.getInstance().lives.update(f, (DBObject)set);
    }
    
    public static void setLives(final UUID u, final int l) {
        final DBObject obj = (DBObject)new BasicDBObject("uuid", (Object)u);
        final DBObject f = Hub.getInstance().lives.findOne(obj);
        final BasicDBObject set = new BasicDBObject("$set", (Object)f);
        set.append("$set", (Object)new BasicDBObject("lives", (Object)l));
        Hub.getInstance().lives.update(f, (DBObject)set);
    }
}
