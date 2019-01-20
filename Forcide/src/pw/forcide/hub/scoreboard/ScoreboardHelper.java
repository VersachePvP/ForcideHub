package pw.forcide.hub.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import java.util.List;

public class ScoreboardHelper
{
    private List<Entry> list;
    private Scoreboard board;
    private Objective o;
    private String tag;
    private int lastSentCount;
    
    public ScoreboardHelper(final Scoreboard board, final String title) {
        this.list = new ArrayList<Entry>();
        this.lastSentCount = -1;
        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.board = board;
        (this.o = this.getOrCreateObjective(this.tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public List<Entry> getEntries() {
        return this.list;
    }
    
    public void add(String s) {
        s = ChatColor.translateAlternateColorCodes('&', s);
        Entry text = null;
        if (s.length() <= 15) {
            text = new Entry(s, "");
        }
        else {
            String first = s.substring(0, 16);
            String second = s.substring(16, s.length());
            if (first.endsWith(String.valueOf('§'))) {
                first = first.substring(0, first.length() - 1);
                second = String.valueOf('§') + second;
            }
            final String lastColors = ChatColor.getLastColors(first);
            second = String.valueOf(lastColors) + second;
            text = new Entry(first, StringUtils.left(second, 16));
        }
        this.list.add(text);
    }
    
    public void clear() {
        this.list.clear();
    }
    
    public void remove(final int i) {
        final String name = this.getNameForIndex(i);
        this.board.resetScores(name);
        final Team t = this.getOrCreateTeam(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14))) + i, i);
        t.unregister();
    }
    
    public void update(final Player p) {
        p.setScoreboard(this.board);
        for (int sentCount = 0; sentCount < this.list.size(); ++sentCount) {
            final Team i = this.getOrCreateTeam(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14))) + sentCount, sentCount);
            final Entry str = this.list.get(this.list.size() - sentCount - 1);
            i.setPrefix(str.getLeft());
            i.setSuffix(str.getRight());
            this.o.getScore(this.getNameForIndex(sentCount)).setScore(sentCount + 1);
        }
        if (this.lastSentCount != -1) {
            for (int sentCount = this.list.size(), j = 0; j < this.lastSentCount - sentCount; ++j) {
                this.remove(sentCount + j);
            }
        }
        this.lastSentCount = this.list.size();
    }
    
    public Team getOrCreateTeam(final String team, final int i) {
        Team t = this.board.getTeam(team);
        if (t == null) {
            t = this.board.registerNewTeam(team);
            t.addEntry(this.getNameForIndex(i));
        }
        return t;
    }
    
    public Objective getOrCreateObjective(final String o) {
        Objective value = this.board.getObjective("oasis");
        if (value == null) {
            value = this.board.registerNewObjective("oasis", "dummy");
        }
        value.setDisplayName(o);
        return value;
    }
    
    public String getNameForIndex(final int i) {
        return String.valueOf(ChatColor.values()[i].toString()) + ChatColor.RESET;
    }
    
    private class Entry
    {
        private String left;
        private String right;
        
        public Entry(final String left, final String right) {
            this.left = left;
            this.right = right;
        }
        
        public String getLeft() {
            return this.left;
        }
        
        public String getRight() {
            return this.right;
        }
    }
}
