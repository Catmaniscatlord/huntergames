package io.github.catmaniscatlord.HunterGames;

import io.github.catmaniscatlord.HunterGames.HunterHandler.*;

import org.bukkit.plugin.java.JavaPlugin;

public final class HunterGames extends JavaPlugin {


    @Override
    public void onEnable() {

        CompassTarget compassTarget = new CompassTarget();
        GameSetup gameSetup = new GameSetup(this, compassTarget);

        gameSetup.run(); 
        
        // We pass in compassTarget here because certain events use this
        HunterEvents hunterEvents = new HunterEvents(compassTarget,this);

        getServer().getPluginManager().registerEvents(hunterEvents, this);

    }

    @Override
    public void onDisable()
    {
    }
}
