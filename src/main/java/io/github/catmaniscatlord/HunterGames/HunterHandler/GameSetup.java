package io.github.catmaniscatlord.HunterGames.HunterHandler;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.catmaniscatlord.HunterGames.*;

public class GameSetup {

    private JavaPlugin plugin;
    private CompassTarget compassTarget;
    private World world;

    
    public GameSetup(JavaPlugin plugin, CompassTarget compassTarget)
    {
        this.plugin = plugin;
        this.compassTarget = compassTarget;
    }

    public void run()
    {   
        setup();   

        GameEvents gameEvents = new GameEvents(compassTarget);
        
        plugin.getServer().getPluginManager().registerEvents(gameEvents, plugin);
        plugin.getCommand("start").setExecutor(new StartCommand(plugin, compassTarget, gameEvents));
        
    }
    
    private void setup()
    {
        if(Bukkit.getWorlds().size() == 0)
        {
            Bukkit.createWorld(new WorldCreator("world"));
        }
        world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();
        
        worldBorder.setSize(10);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setPVP(false);
        world.setGameRule(GameRule.SPAWN_RADIUS, 5);
    }
}
