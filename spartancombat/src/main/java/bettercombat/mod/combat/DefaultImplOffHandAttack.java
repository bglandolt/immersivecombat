package bettercombat.mod.combat;

import bettercombat.mod.client.handler.EventHandlersClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;

public class DefaultImplOffHandAttack implements IOffHandAttack
{
    private int offhandCooldown;

    @Override
    public int getOffhandCooldown()
    {
        return this.offhandCooldown;
    }

    @Override
    public void setOffhandCooldown(int amount)
    {
        this.offhandCooldown = amount;
    }

    @Override
    public void tick()
    {
        if ( this.offhandCooldown > 0 )
        {
            this.offhandCooldown--;
        }
        
        if ( EventHandlersClient.sameTimeSwing > 0 )
	    {
        	EventHandlersClient.sameTimeSwing--;
	    }
        
//        if ( EventHandlersClient.rightAttackQueued > 0 )
//	    {
//        	if ( EventHandlersClient.onMouseRightClick() )
//        	{
//        		EventHandlersClient.rightAttackQueued = 0;
//        	}
//        	else
//        	{
//            	EventHandlersClient.rightAttackQueued--;
//        	}
//	    }
//        
//        if ( EventHandlersClient.leftAttackQueued > 0 )
//	    {
//        	if ( EventHandlersClient.onMouseLeftClick() )
//        	{
//        		EventHandlersClient.leftAttackQueued = 0;
//        	}
//        	else
//        	{
//            	EventHandlersClient.leftAttackQueued--;
//        	}
//	    }
    }

    @Override
    public void swingOffHand(EntityPlayer player)
    {
    	player.swingProgressInt = -1;
        player.isSwingInProgress = true;
        player.swingingHand = EnumHand.OFF_HAND;
        if (player.world instanceof WorldServer) ((WorldServer)player.world).getEntityTracker().sendToTracking(player, new SPacketAnimation(player, 3));
    }
}