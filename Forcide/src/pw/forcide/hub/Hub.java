package pw.forcide.hub;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import pw.forcide.hub.commands.LeaveQueueCommand;
import pw.forcide.hub.commands.PauseQueueCommand;
import pw.forcide.hub.commands.SetLimitCommand;
import pw.forcide.hub.commands.chat.ChatCommand;
import pw.forcide.hub.listeners.ChatListener;
import pw.forcide.hub.listeners.EnderButtListener;
import pw.forcide.hub.listeners.JoinListener;
import pw.forcide.hub.listeners.JumpListener;
import pw.forcide.hub.listeners.PlayerVisibilityListener;
import pw.forcide.hub.listeners.SelectorTask;
import pw.forcide.hub.listeners.ServerListener;
import pw.forcide.hub.listeners.ServerSelectorListener;
import pw.forcide.hub.queue.QueueManager;
import pw.forcide.hub.scoreboard.ScoreboardHandler;
import pw.forcide.hub.scoreboard.tab.HubTabAdapter;
import pw.forcide.hub.scoreboard.tab.TabHandler;
import pw.forcide.hub.utils.ServerUtils;

public class Hub extends JavaPlugin {
	
    public static Hub instance;
    private ScoreboardHandler scoreboardHandler;
    private SecurityManager securityManager;
    private QueueManager queueManager;
    public DBCollection deathbansHcf;
    public DBCollection deathbansKits;
    public DBCollection lives;
    public DB databaseHcf;
    public DB databaseKits;
    public MongoClient client;
    private Permission perms;
    private Chat chat;
    
    public void onEnable() {
        Hub.instance = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        this.saveDefaultConfig();
        final File f = new File(this.getDataFolder(), "lang.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.getConfig().getBoolean("SupportOasisDatabase")) {
            this.setupDataBase();
        }
        this.scoreboardHandler = new ScoreboardHandler();
        this.queueManager = new QueueManager();
        this.setUpPermissions();
        this.setUpChat();
        this.getCommand("leavequeue").setExecutor((CommandExecutor)new LeaveQueueCommand());
        this.getCommand("pausequeue").setExecutor((CommandExecutor)new PauseQueueCommand());
        this.getCommand("setlimit").setExecutor((CommandExecutor)new SetLimitCommand());
        this.getCommand("chat").setExecutor((CommandExecutor)new ChatCommand());
        Bukkit.getPluginManager().registerEvents((Listener)new ChatListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ServerSelectorListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ServerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new EnderButtListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerVisibilityListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new JoinListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new JumpListener(), (Plugin)this);
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, getInstance().getConfig().getString("MessagingChannel"));
        Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)this, getInstance().getConfig().getString("MessagingChannel"), (PluginMessageListener)new ServerUtils());
        ServerUtils.startPlayerCountTask();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().setCollidesWithEntities(false);
        }
        this.disableEverything();
        new SelectorTask().runTaskTimer((Plugin)this, 20L, 20L);
        new TabHandler(this, new HubTabAdapter());
    }
    
    public void onDisable() {
        Hub.instance = null;
    }
    
    public void setupDataBase() {
        try {
            this.client = new MongoClient("localhost", 27017);
            Bukkit.getConsoleSender().sendMessage("§a§lForcideHub has connected to MongoDB.");
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c§lForcideHub failed to connect to MongoDB.");
            return;
        }
        this.databaseKits = this.client.getDB("kits");
        this.deathbansKits = this.databaseKits.getCollection("deathbans");
        this.databaseHcf = this.client.getDB("hcf");
        this.deathbansHcf = this.databaseHcf.getCollection("deathbans");
        this.lives = this.databaseHcf.getCollection("lives");
    }
    
    private boolean setUpPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        this.perms = (Permission)rsp.getProvider();
        return this.perms != null;
    }
    
    private boolean setUpChat() {
        final RegisteredServiceProvider<Chat> rsp = (RegisteredServiceProvider<Chat>)this.getServer().getServicesManager().getRegistration((Class)Chat.class);
        this.chat = (Chat)rsp.getProvider();
        return this.chat != null;
    }
    
    private void disableEverything() {
        final Server s = Bukkit.getServer();
        final World w = s.getWorld("world");
        s.setDefaultGameMode(GameMode.ADVENTURE);
        w.setTime(6000L);
        w.setGameRuleValue("doDaylightCycle", "false");
        w.setAmbientSpawnLimit(0);
        w.setAnimalSpawnLimit(0);
        w.setDifficulty(Difficulty.PEACEFUL);
        w.setWeatherDuration(0);
        w.setStorm(false);
        w.setPVP(false);
        w.setMonsterSpawnLimit(0);
        w.setKeepSpawnInMemory(true);
    }
    
    public static Hub getInstance() {
        return Hub.instance;
    }
    
    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }
    
    public SecurityManager getSecurityManager() {
        return this.securityManager;
    }
    
    public QueueManager getQueueManager() {
        return this.queueManager;
    }
    
    public Permission getPerms() {
        return this.perms;
    }
    
    public Chat getChat() {
        return this.chat;
    }
}
