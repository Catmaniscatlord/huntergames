package io.github.catmaniscatlord.HunterGames;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CompassTarget{
    
    ArrayList<Player> players;
    public CompassTarget()
    {
        this.players = new ArrayList<Player>(Bukkit.getOnlinePlayers()); 
    }
    
    public void updatePlayerCompass()
    {
        players.forEach((p) -> updatePlayerCompass(p));
    }

    public void updatePlayerCompass(Player p)
    {
        Player targetPlayer = locateNearestPlayer(p);
        if(targetPlayer != null)
        {
            p.setCompassTarget(targetPlayer.getLocation());
            String title = String.format("%.2f m",distanceToPlayer(p, targetPlayer));
            p.sendTitle("", title,10,70,20);
        }
    }

    public Player locateNearestPlayer(Player source)
    {
        double shortestDistance = -1;
        Location sourcePlayer = source.getLocation();
        // sets closest player to the source as it starts
        Player closestPlayer = source;
        for(Player p : players)
        {
            //we onyl grab survival so those specating arent calculated
            if(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))
            {
                if(!p.equals(source) && players.size() > 1)
                {

                    if(shortestDistance == -1)
                    {
                        shortestDistance = sourcePlayer.distanceSquared(p.getLocation());
                        closestPlayer = p;
                    }
                    else if(sourcePlayer.distanceSquared(p.getLocation()) < shortestDistance)
                    {
                        shortestDistance = sourcePlayer.distanceSquared(p.getLocation());
                        closestPlayer = p;
                    }
                }
            }
        }
        return closestPlayer;
    }

    public double distanceToPlayer(Player source, Player target)
    {
        return source.getLocation().distance(target.getLocation());
    } 
    
    public double distanceToNearestPlayer(Player source)
    {
        return distanceToPlayer(source, locateNearestPlayer(source));
    }

    public void setPlayers(Collection<? extends Player> players) {
        this.players = new ArrayList<Player>(players);
    }

    public void updatePlayers()
    {
        setPlayers(Bukkit.getOnlinePlayers());
    }

}
