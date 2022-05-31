package bettercombat.mod.network;

import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMainhandAttack implements IMessage
{
    private int entityId;
    private int fatigue = 0;
    
    public PacketMainhandAttack()
    {
    }

    public PacketMainhandAttack(int f)
    {
        this.fatigue = f;
    }

    public PacketMainhandAttack(int f, int parEntityId)
    {
        this.entityId = parEntityId;
        this.fatigue = f;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = ByteBufUtils.readVarInt(buf, 4);
        this.fatigue = ByteBufUtils.readVarInt(buf, 1);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.entityId, 4);
        ByteBufUtils.writeVarInt(buf, this.fatigue, 1);
    }

    public static class Handler
            implements IMessageHandler<PacketMainhandAttack, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketMainhandAttack message, final MessageContext ctx) 
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private static void handle(PacketMainhandAttack message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Entity theEntity = player.world.getEntityByID(message.entityId);
            
            if ( message.fatigue > 0 )
        	{
        		player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 50, message.fatigue-1, true, false));
        	}

            if ( ConfigurationHandler.inertiaOnAttack != 1.0F && player.onGround )
            {
                player.motionX *= ConfigurationHandler.inertiaOnAttack;
                player.motionZ *= ConfigurationHandler.inertiaOnAttack;
                player.velocityChanged = true;
            }
            
            if ( theEntity != null )
            {
                if ( player.interactionManager.getGameType() == GameType.SPECTATOR )
                {
                    player.setSpectatingEntity(theEntity);
                } 
                else
                {
                    Helpers.playerAttackVictim(player, theEntity, true);
                }
            }
        }
    }
}