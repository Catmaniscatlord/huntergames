package io.github.catmaniscatlord.HunterGames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.block.Action;

public class HunterEvents implements Listener{
    
    // The EventTimer class prevents a player from triggering an action for x milliseconds 
    private EventTimer compassTimer = new CompassTimer(10*1000);
    private final class CompassTimer extends EventTimer {
        // This is the material that will be used
        Material sacraficeItem = Material.IRON_INGOT;
        
		private CompassTimer(int delay) {
			super(delay);
		}

		@Override 
        public boolean conditions()
        {
            // We have to take variables out of the creation of this object in order to use them in our code
            PlayerInteractEvent e = (PlayerInteractEvent) getEvent();
            Player p = getPlayer();

            //I include the e.get hand part because player interact passes in both events for interact main and offhand, which triggers this twice
            if(e.getHand() == EquipmentSlot.HAND && (p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) || p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)))
            {
                // checks if the player right clicked with the compass, 
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    return true;
                }
            }
            return false;
        }

		@Override
        public void run()
        {
            // We have to take variables out of the creation of this object in order to use them in our code
            Player p = getPlayer();
            
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

    private EventTimer clockYeeter = new ClockYeeter(60 * 1000);
    private final class ClockYeeter extends EventTimer
    {
        private Material sacraficeItem = Material.DIAMOND;
        
        private PlayerInteractEvent e;
        private Player p;
        
        public ClockYeeter(int delay)
        {
            super(delay);
        }

        @Override
        public boolean conditions() 
        {
            e = (PlayerInteractEvent) getEvent();
            p = getPlayer();

            if(e.getHand() == EquipmentSlot.HAND && (p.getInventory().getItemInMainHand().getType().equals(Material.CLOCK) || p.getInventory().getItemInOffHand().getType().equals(Material.CLOCK)))
            {
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    if(compassTarget.distanceToNearestPlayer(p) < 100)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void run() 
        {
            boolean sacraficedItem = false;
            for(int i = 0; i < p.getInventory().getSize(); i++)
            {
                ItemStack item = p.getInventory().getItem(i);
                if(item != null && item.getType().equals(sacraficeItem))
                {
                    int amount = item.getAmount() - 1;
                    item.setAmount(amount);
                    p.getInventory().setItem(i, amount > 0 ? item : null);
                    p.updateInventory();
                    sacraficedItem = true;
                    break;
                } 
            }
            
            if(!sacraficedItem)
            {
                p.sendMessage(ChatColor.DARK_AQUA + "You don't have any diamonds you broke ass bitch");
                return;
            }           

            for(int i = 0; i < p.getInventory().getSize(); i++)
            {
                ItemStack item = p.getInventory().getItem(i);
                if(item != null && item.getType().equals(Material.CLOCK))
                {
                    int amount = item.getAmount() - 1;
                    item.setAmount(amount);
                    p.getInventory().setItem(i, amount > 0 ? item : null);
                    p.updateInventory();
                    break;
                } 
            }
            
            Player target = compassTarget.locateNearestPlayer(p);
            Vector targetVeloctiy = target.getVelocity();
            
            target.sendTitle("YEET IN 2 SECONDS","",0,20,10);
            target.playSound(target.getLocation(), Sound.BLOCK_PUMPKIN_CARVE , SoundCategory.MASTER, 1, 1);

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable()
            {
                @Override
                public void run() {
                    targetVeloctiy.add(yeetVector(5));
                    target.setVelocity(targetVeloctiy);                    
                }    
            }, 40);



            p.sendTitle("", "YEET", 10, 40, 20);
        }

        private Vector yeetVector(double distance)
        {
            Random random = new Random();
            Double x, y, z;
            double theata, phi;

            theata = random.nextDouble()*2*Math.PI;
            phi = random.nextDouble()*2*Math.PI;

            x = distance * Math.cos(theata) * Math.cos(phi);
            y = Math.abs(distance * Math.sin(phi));            
            z = distance * Math.cos(theata) * Math.sin(phi);
            return new Vector(x, y, z);
        }

    };

    private Plugin plugin; 
	private CompassTarget compassTarget;

    public HunterEvents(CompassTarget compassTarget,Plugin plugin)
    {
        this.compassTarget = compassTarget;
        this.plugin = plugin; 
    }
    // When a player leaves update the compass
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e)
    {
        compassTarget.updatePlayers();
    }
    
    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e)
    {  
        compassTimer.handleEvent(e);
        clockYeeter.handleEvent(e);
    }
    
}