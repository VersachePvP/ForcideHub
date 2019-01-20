package pw.forcide.hub.queue;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.Collections;
import org.bukkit.plugin.Plugin;
import pw.forcide.hub.Hub;
import java.util.Iterator;
import org.bukkit.ChatColor;
import pw.forcide.hub.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import java.util.List;

public class Queue
{
    private String server;
    private List<OfflinePlayer> list;
    private Map<OfflinePlayer, BukkitTask> taskMap;
    private boolean paused;
    private int limit;
    
    public Queue(final String server) {
        this.server = server;
        this.list = new ArrayList<OfflinePlayer>();
        this.taskMap = new HashMap<OfflinePlayer, BukkitTask>();
        this.paused = false;
        this.limit = 400;
        new BukkitRunnable() {
            public void run() {
                for (final OfflinePlayer o : Queue.this.list) {
                    if (o.isOnline()) {
                        final Player p = (Player)o;
                        for (final String s : Lang.getList("QUEUE_MESSAGE")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("%position%", String.valueOf(Queue.this.getPosition(p))).replace("%total%", String.valueOf(Queue.this.getSize())).replace("%server%", server)));
                        }
                    }
                    else {
                        Queue.this.list.remove(o);
                    }
                }
            }
        }.runTaskTimer((Plugin)Hub.getInstance(), 300L, 300L);
    }
    
    public void addEntry(final OfflinePlayer p) {
        if (this.list.contains(p)) {
            return;
        }
        if (Hub.getInstance().getQueueManager().getPriority(p) == 0) {
            final Player o = (Player)p;
            this.sendDirect(o);
            o.sendMessage(Lang.getString("QUEUE_SENT").replace("%server%", this.server));
            return;
        }
        this.list.add(p);
        for (final OfflinePlayer o2 : this.list) {
            final int pos = this.list.indexOf(o2);
            if (p != o2 && Hub.getInstance().getQueueManager().getPriority(p) < Hub.getInstance().getQueueManager().getPriority(o2)) {
                Collections.swap(this.list, pos, this.list.size() - 1);
            }
        }
    }
    
    public List<OfflinePlayer> getPlayers() {
        return this.list;
    }
    
    public void removeEntry(final OfflinePlayer p) {
        this.list.remove(p);
    }
    
    public int getSize() {
        return this.list.size();
    }
    
    public OfflinePlayer getPlayerAt(final int i) {
        return this.list.get(i);
    }
    
    public int getPosition(final Player p) {
        return this.list.indexOf(p) + 1;
    }
    
    public void sendFirst() {
        if (!this.list.isEmpty()) {
            final Player p = this.list.get(0).getPlayer();
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(this.server);
            p.sendPluginMessage((Plugin)Hub.getInstance(), Hub.getInstance().getConfig().getString("MessagingChannel"), out.toByteArray());
        }
    }
    
    public void sendDirect(final Player p) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(this.server);
        p.sendPluginMessage((Plugin)Hub.getInstance(), Hub.getInstance().getConfig().getString("MessagingChannel"), out.toByteArray());
    }
    
    public String getServer() {
        return this.server;
    }
    
    public List<OfflinePlayer> getList() {
        return this.list;
    }
    
    public Map<OfflinePlayer, BukkitTask> getTaskMap() {
        return this.taskMap;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public void setServer(final String server) {
        this.server = server;
    }
    
    public void setList(final List<OfflinePlayer> list) {
        this.list = list;
    }
    
    public void setTaskMap(final Map<OfflinePlayer, BukkitTask> taskMap) {
        this.taskMap = taskMap;
    }
    
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Queue)) {
            return false;
        }
        final Queue other = (Queue)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$server = this.getServer();
        final Object other$server = other.getServer();
        Label_0065: {
            if (this$server == null) {
                if (other$server == null) {
                    break Label_0065;
                }
            }
            else if (this$server.equals(other$server)) {
                break Label_0065;
            }
            return false;
        }
        final Object this$list = this.getList();
        final Object other$list = other.getList();
        Label_0102: {
            if (this$list == null) {
                if (other$list == null) {
                    break Label_0102;
                }
            }
            else if (this$list.equals(other$list)) {
                break Label_0102;
            }
            return false;
        }
        final Object this$taskMap = this.getTaskMap();
        final Object other$taskMap = other.getTaskMap();
        if (this$taskMap == null) {
            if (other$taskMap == null) {
                return this.isPaused() == other.isPaused() && this.getLimit() == other.getLimit();
            }
        }
        else if (this$taskMap.equals(other$taskMap)) {
            return this.isPaused() == other.isPaused() && this.getLimit() == other.getLimit();
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof Queue;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $server = this.getServer();
        result = result * 59 + (($server == null) ? 43 : $server.hashCode());
        final Object $list = this.getList();
        result = result * 59 + (($list == null) ? 43 : $list.hashCode());
        final Object $taskMap = this.getTaskMap();
        result = result * 59 + (($taskMap == null) ? 43 : $taskMap.hashCode());
        result = result * 59 + (this.isPaused() ? 79 : 97);
        result = result * 59 + this.getLimit();
        return result;
    }
    
    @Override
    public String toString() {
        return "Queue(server=" + this.getServer() + ", list=" + this.getList() + ", taskMap=" + this.getTaskMap() + ", paused=" + this.isPaused() + ", limit=" + this.getLimit() + ")";
    }
}
