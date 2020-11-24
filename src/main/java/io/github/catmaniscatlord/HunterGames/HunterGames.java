package io.github.catmaniscatlord.HunterGames;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;

public final class HunterGames extends JavaPlugin {

    private World world;

    @Override
    public void onEnable() {

        CompassTarget compassTarget = new CompassTarget();

        // Sets up the world for this minigame
        setup();

        // We pass in compassTarget here because certain events use this
        HunterEvents hunterEvents = new HunterEvents(compassTarget);

        getServer().getPluginManager().registerEvents(hunterEvents, this);

        // We pass in compassTarget and hunterEvents here because the start command
        // modifies how certain events behave
        // it also updates the compassTarget every 10 minutes.
        getCommand("start").setExecutor(new StartCommand(this, compassTarget, hunterEvents));


        //once the server closes delete all world files : this allows for easier resets.
        Bukkit.getWorlds().forEach((w) -> w.getWorldFolder().deleteOnExit());
    }

    @Override
    public void onDisable()
    {
        
    }

    private void setup()
    {
        Bukkit.createWorld(new WorldCreator("world"));
        
        world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();
        
        worldBorder.setSize(10);
        world.setPVP(false);
        world.setGameRule(GameRule.SPAWN_RADIUS, 5);
    }
}
