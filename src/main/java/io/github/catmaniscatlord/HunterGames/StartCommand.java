package io.github.catmaniscatlord.HunterGames;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor{
 
    private JavaPlugin plugin;
    private World world;
    private CompassTarget compassTarget;
    private double worldBorderSize;
    private HunterEvents hunterEvents;
    private BukkitTask countdown;
    
    public StartCommand(JavaPlugin plugin, CompassTarget compassTarget,HunterEvents hunterEvents)
    {
        this.plugin = plugin;
        this.world = Bukkit.getWorld("world");
        this.worldBorderSize = this.world.getWorldBorder().getSize();
        this.compassTarget = compassTarget;
        this.hunterEvents = hunterEvents;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
        // Command can only be done by the console
        if(!(sender instanceof Player))
        {
            for(Player p : Bukkit.getOnlinePlayers())
            {
                if(p.getGameMode() == GameMode.ADVENTURE)
                {
                    worldBorderSize += 250; 
                }
            }
            // This will pregenerate the amount of chunks needed using the fcp plugin
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String generateCommand = "fcp start " + worldBorderSize + " world";
            Bukkit.dispatchCommand(console, generateCommand);
            Bukkit.broadcastMessage("Pregenerating");
        
            // Will start the countdown in 1 minute
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    // Starts the countdown
                    Countdown();
        
                    // Update compass every 10 minutes
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            compassTarget.updatePlayerCompass();
                        }
                    }, 0, 20*600);
        
                    // Enables pvp after 5 minutes
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            world.setPVP(true);
                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "GRACE PERIOD OVER");
                        }    
                    }, 20 * 300); 
                }    
            }, 20*60);
        }
        return true;
    }

    private void Countdown(){
        countdown = Bukkit.getScheduler().runTaskTimer(plugin,new Runnable()
        {
            int i = 10;
            
            @Override
            public void run()
            {
                Bukkit.getOnlinePlayers().forEach((p) -> {
                    p.sendTitle(ChatColor.AQUA + "" + i, "", 5, 10, 5);
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
                });
                if(i-- <= 0)
                {
                    stopCountdown();
                }
            }   
        }, 0, 20);
    }

    private void stopCountdown()
    {
        // Once the countdown starts cancle the pregeneration
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String generateCommand = "fcp cancel";
        Bukkit.dispatchCommand(console, generateCommand);

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Bukkit.getScheduler().cancelTask(countdown.getTaskId());
        hunterEvents.setCanTakeDamage(true);
        //set the world border to 200 * num of players after the countdown finishes 
        world.getWorldBorder().setSize(worldBorderSize);
        Bukkit.getOnlinePlayers().forEach((p) -> p.setGameMode(GameMode.SURVIVAL));
    }
}
