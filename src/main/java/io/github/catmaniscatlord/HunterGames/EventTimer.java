package io.github.catmaniscatlord.HunterGames;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

// This 

public abstract class EventTimer {
    private HashMap<Player,Long> playerTimer = new HashMap<Player, Long>();

    protected PlayerEvent event;
    protected Player player;
    private int delay;

    public EventTimer(int delay)
    {
        this.delay = delay;
    }

    //inorder to access the event variable you must cast it to something else in this function
    abstract void run();

    abstract void delayMessage();

    // checks if it has been long enough for the code to run again 
    public boolean checkDelay()
    {
        while (true)
        {
            if(playerTimer.containsKey(player))
            {
                return timeDelay() > delay;
            } else
            {
                playerTimer.put(player, System.currentTimeMillis());
            }
        }
    }

    public long timeDelay()
    {
        return playerTimer.get(player) - System.currentTimeMillis();
    }

    public void handleEvent(PlayerEvent event)
    {
        this.event = event;
        this.player = event.getPlayer();
        if(checkDelay())
        {
            run();
        } else
        {
            delayMessage();
        }
    }

    public int getDelay() {
        return delay;
    }

    public PlayerEvent getEvent() {
        return event;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setEvent(PlayerEvent event) {
        this.event = event;
    }
}
