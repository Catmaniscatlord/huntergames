package io.github.catmaniscatlord.HunterGames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import org.bukkit.event.block.Action;

public class HunterEvents implements Listener{

    private CompassTarget compassTarget;
    private Boolean canTakeDamage;

    public HunterEvents(CompassTarget compassTarget)
    {
        this.canTakeDamage = false;
        this.compassTarget = compassTarget;

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

    // When a player leaves update the compass
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e)
    {
        compassTarget.updatePlayers();
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
                worldSize += 250;
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "World border shrinking to a radius of " + worldSize/2);
        worldBorder.setSize(worldSize,20*300);
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e)
    {
        // if they can take damage set cancelled will be false
        e.setCancelled(!canTakeDamage);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        // The EventTimer class prevents a player from triggering an action for x milliseconds 
        EventTimer compassTimer = new EventTimer(10*1000)
        {    
            // This is the material that will be used
            Material sacraficeItem = Material.IRON_INGOT;
            
            // We have to take variables out of the creation of this object in order to use them in our code
            PlayerInteractEvent e = (PlayerInteractEvent) this.event;
            Player p = this.player;
            
            @Override
            public void run()
            {
                //I include the e.get hand part because player interact passes in both events for interact main and offhand, which triggers this twice
                if(e.getHand() == EquipmentSlot.HAND && (p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) || p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)))
                {
                    // checks if the player right click with the compass, 
                    // and if it has been at least 10 seconds since it has last been triggered
                    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    {
                        for(int i = 0; i < p.getInventory().getSize(); i++)
                        {
                            ItemStack item = p.getInventory().getItem(i);
                            if(item != null && item.getType().equals(sacraficeItem))
                            {
                                int amount = item.getAmount() - 1;
                                item.setAmount(amount);
                                p.getInventory().setItem(i, amount > 0 ? item : null);
                                p.updateInventory();
                                compassTarget.updatePlayerCompass(p);
                                break;
                            }
                        }
                    }
                }
            }

            @Override 
            public void delayMessage()
            {
                String message = String.format("Please wait %.1f seconds before using that again", (float) - this.timeDelay()/1000);
                p.sendMessage(message);
            }
        };

        compassTimer.handleEvent(e);
    }

    public Boolean getCanTakeDamage() {
        return canTakeDamage;
    }
    
    public void setCanTakeDamage(Boolean canTakeDamage) {
        this.canTakeDamage = canTakeDamage;
    }   
}