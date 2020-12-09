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

    abstract boolean conditions();

    //inorder to access the event variable you must cast it to something else in this function
    abstract void run();

    public void delayMessage()
    {
        //since the time delay returns a negative number we add
        String message = String.format("Please wait %.1f seconds before using that again", (float) (getDelay() + timeDelay())/1000);
        getPlayer().sendMessage(message);
    }

    // checks if it has been long enough for the code to run again 
    // we negate the time delay so it is positive.
    public boolean checkDelay()
    {
        if(playerTimer.containsKey(player))
        {
            return -timeDelay() > delay;
        } else
        {
            playerTimer.put(player, System.currentTimeMillis());   
            return true;
        }
    }

    // always returns a number less than or equal to 0
    public long timeDelay()
    {
        return playerTimer.get(player) - System.currentTimeMillis();
    }

    public void handleEvent(PlayerEvent event)
    {
        this.event = event;
        this.player = event.getPlayer();
        if(conditions())
        {
            if(checkDelay())
            {
                run();
                playerTimer.put(player, System.currentTimeMillis());
            } else
            {
                delayMessage();
            }
        }
    }

    public int getDelay() {
        return delay;
    }

    public PlayerEvent getEvent() {
        return event;
    }

    public Player getPlayer() {
        return player;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
