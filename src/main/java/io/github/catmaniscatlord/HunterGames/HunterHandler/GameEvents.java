package io.github.catmaniscatlord.HunterGames.HunterHandler;

import io.github.catmaniscatlord.HunterGames.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class GameEvents implements Listener {
    
    private CompassTarget compassTarget;
    private boolean canTakeDamage;

    public GameEvents(CompassTarget compassTarget)
    {
        this.compassTarget = compassTarget;
        this.canTakeDamage = false;
    }

    // When a player first joins set them into adventure mode and update the available players for the compass class
    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    { 
        Player p = e.getPlayer(); 
        if(!p.hasPlayedBefore())
        { 
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
            p.setGameMode(GameMode.ADVENTURE);
        }
        compassTarget.updatePlayers();
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e)
    {
        // if they can take damage set cancelled will be false
        e.setCancelled(!canTakeDamage);
    }

    // When a player dies set them into spectator mode and shrink the world border
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e)
    {
        Player p = e.getEntity();
        WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
        
        p.setGameMode(GameMode.SPECTATOR);
        
        
        int worldSize=10;
        
        for(Player i : Bukkit.getOnlinePlayers())
        {
            if(i.getGameMode().equals(GameMode.SURVIVAL))
            {
                worldSize += 350;
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "World border shrinking to a radius of " + worldSize/2);
        worldBorder.setSize(worldSize,20*300);
    }

    public Boolean getCanTakeDamage() {
        return canTakeDamage;
    }
    
    public void setCanTakeDamage(Boolean canTakeDamage) {
        this.canTakeDamage = canTakeDamage;
    }  
}
