package pw.forcide.hub.utils;

import com.google.common.io.ByteArrayDataInput;
import org.bukkit.entity.Player;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import com.google.common.io.ByteArrayDataOutput;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import com.google.common.io.ByteStreams;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import pw.forcide.hub.Hub;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ServerUtils implements PluginMessageListener
{
    public static Map<String, Integer> playerCounts;
    
    static {
        ServerUtils.playerCounts = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
    }
    
    public static void startPlayerCountTask() {
        final List<String> servers = (List<String>)Hub.getInstance().getConfig().getStringList("PlayerCountTracker.Servers");
        new BukkitRunnable() {
            public void run() {
                for (final String s : servers) {
                    final ByteArrayDataOutput globalOut = ByteStreams.newDataOutput();
                    globalOut.writeUTF("PlayerCount");
                    globalOut.writeUTF(s);
                    Bukkit.getServer().sendPluginMessage((Plugin)Hub.getInstance(), Hub.getInstance().getConfig().getString("MessagingChannel"), globalOut.toByteArray());
                }
            }
        }.runTaskTimer((Plugin)Hub.getInstance(), 20L, 20L);
    }
    
    public static boolean isOnline(final String server) {
        final int port = Hub.getInstance().getConfig().getInt("PlayerCountTracker.Ports." + server.toLowerCase());
        try {
            final SocketAddress a = new InetSocketAddress("localhost", port);
            final Socket s = new Socket();
            s.connect(a, 1000);
            s.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static int getPlayerCount(final String server) {
        return ServerUtils.playerCounts.containsKey(server) ? ServerUtils.playerCounts.get(server) : 0;
    }
    
    public void onPluginMessageReceived(final String channel, final Player p, final byte[] message) {
        if (!channel.equals(Hub.getInstance().getConfig().getString("MessagingChannel"))) {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String command = in.readUTF();
        if (!command.equals("PlayerCount")) {
            return;
        }
        final String server = in.readUTF();
        final int playerCount = in.readInt();
        ServerUtils.playerCounts.put(server, playerCount);
    }
}
