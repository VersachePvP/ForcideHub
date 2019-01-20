package pw.forcide.hub.scoreboard.tab;

import java.util.AbstractMap;
import org.bukkit.ChatColor;
import java.util.Map;
import org.bukkit.scoreboard.Team;
import java.util.Iterator;
import pw.forcide.hub.scoreboard.tab.utils.TabTemplate;
import org.bukkit.entity.Player;
import pw.forcide.hub.scoreboard.tab.utils.TabAdapter;
import java.util.Arrays;
import java.util.List;
import pw.forcide.hub.scoreboard.tab.utils.Tab;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TabTask extends BukkitRunnable
{
    private TabHandler handler;
    
    public TabTask(final TabHandler handler, final JavaPlugin plugin) {
        this.handler = handler;
        this.runTaskTimerAsynchronously((Plugin)plugin, 20L, 20L);
    }
    
    public void run() {
        final TabAdapter adapter = this.handler.getAdapter();
        if (adapter != null) {
            Player[] onlinePlayers;
            for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, k = 0; k < length; ++k) {
                final Player all = onlinePlayers[k];
                final Tab tab = this.handler.getTabByPlayer(all);
                if (tab != null) {
                    final TabTemplate t = adapter.getTemplate(all);
                    if (t == null || (t.getLeft().isEmpty() && t.getMiddle().isEmpty() && t.getRight().isEmpty())) {
                        for (final Tab.TabEntryPosition position : tab.getPositions()) {
                            final Team team = all.getScoreboard().getTeam(position.getKey());
                            if (team != null) {
                                if (team.getPrefix() != null && !team.getPrefix().isEmpty()) {
                                    team.setPrefix("");
                                }
                                if (team.getSuffix() == null || team.getSuffix().isEmpty()) {
                                    continue;
                                }
                                team.setSuffix("");
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < 20 - t.getLeft().size(); ++i) {
                            t.left("");
                        }
                        for (int i = 0; i < 20 - t.getMiddle().size(); ++i) {
                            t.middle("");
                        }
                        for (int i = 0; i < 20 - t.getRight().size(); ++i) {
                            t.right("");
                        }
                        final List<List<String>> rows = Arrays.asList(t.getLeft(), t.getMiddle(), t.getRight(), t.getFarRight());
                        for (int l = 0; l < rows.size(); ++l) {
                            for (int j = 0; j < rows.get(l).size(); ++j) {
                                final Team team2 = tab.getByLocation(l, j);
                                if (team2 != null) {
                                    final Map.Entry<String, String> prefixAndSuffix = this.getPrefixAndSuffix(rows.get(l).get(j));
                                    final String prefix = prefixAndSuffix.getKey();
                                    final String suffix = prefixAndSuffix.getValue();
                                    if (!team2.getPrefix().equals(prefix) || !team2.getSuffix().equals(suffix)) {
                                        team2.setPrefix(prefix);
                                        team2.setSuffix(suffix);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Map.Entry<String, String> getPrefixAndSuffix(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        String prefix;
        String suffix;
        if (text.length() > 16) {
            final int splitAt = (text.charAt(15) == '§') ? 15 : 16;
            prefix = text.substring(0, splitAt);
            final String suffixTemp = String.valueOf(ChatColor.getLastColors(prefix)) + text.substring(splitAt);
            suffix = suffixTemp.substring(0, Math.min(suffixTemp.length(), 16));
        }
        else {
            prefix = text;
            suffix = "";
        }
        return new AbstractMap.SimpleEntry<String, String>(prefix, suffix);
    }
}
