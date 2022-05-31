package bettercombat.mod.network;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.util.ConfigurationHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketShieldBash implements IMessage
{
    private int entityId;
    protected boolean attackEntity = false;
    protected EnumHand hand;
    protected int damage;
    protected int knockback;
    protected int cooldown;
  
    public PacketShieldBash(EnumHand enumHand, int entId, boolean atkEntity, int damage, int knockback, int cooldown )
    {
    	this.hand = enumHand;
    	this.entityId = entId;
    	this.attackEntity = atkEntity;
    	this.damage = damage;
    	this.knockback = knockback;
    	this.cooldown = cooldown;
    }
  
    public void fromBytes(ByteBuf buf)
    {
    	this.hand = EnumHand.values()[ByteBufUtils.readVarInt(buf, 1)];
    	this.entityId = ByteBufUtils.readVarInt(buf, 4);
    	this.damage = ByteBufUtils.readVarShort(buf);
    	this.knockback = ByteBufUtils.readVarShort(buf);
    	this.cooldown = ByteBufUtils.readVarShort(buf);
    	this.attackEntity = buf.readBoolean();
    }
  
    public void toBytes(ByteBuf buf)
    {
    	ByteBufUtils.writeVarInt(buf, this.hand.ordinal(), 1);
    	ByteBufUtils.writeVarInt(buf, this.entityId, 4);
        ByteBufUtils.writeVarShort(buf, this.damage);
        ByteBufUtils.writeVarShort(buf, this.knockback);
        ByteBufUtils.writeVarShort(buf, this.cooldown);
    	buf.writeBoolean(this.attackEntity);
    }
  
    public void handleClientSide(PacketShieldBash message, EntityPlayer player)
    {
    	
    }
    
    public PacketShieldBash()
    {
    	
    }

    public PacketShieldBash(int f, int parEntityId)
    {
        this.entityId = parEntityId;
    }

    public static class Handler implements IMessageHandler<PacketShieldBash, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketShieldBash message, final MessageContext ctx) 
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private static void handle(PacketShieldBash message, MessageContext ctx)
        {
        	EntityPlayerMP player = ctx.getServerHandler().player;

        	if ( message == null || player == null )
        	{
        		return;
        	}
        	
        	EnumHand shieldHand = message.hand;
        	Entity victim = player.world.getEntityByID(message.entityId);
        	
        	if ( player.isActiveItemStackBlocking() )
        	{
        		ItemStack shield = player.getHeldItem(shieldHand);
        		
        		if ( !shield.isEmpty() && !player.getCooldownTracker().hasCooldown(shield.getItem()) && shield.getItem() instanceof ItemShield )
        		{
        			if ( message.attackEntity && victim != null && victim instanceof EntityLivingBase )
        			{
        				int knockLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, shield);
        			    
        			    int mod = 0;
        			    
        			    try
        			    {
        			    	mod = player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier()*300;
        			    }
        			    catch ( Exception e )
        			    {
        			    	
        			    }
        			    
        			    try
        			    {
        			    	mod -= player.getActivePotionEffect(MobEffects.WEAKNESS).getAmplifier()*300;
        			    }
        			    catch ( Exception e )
        			    {
        			    	
        			    }
        			    
        			    float damage = ((float)(message.damage+mod)/100.0F);
        			    
        			    if ( ConfigurationHandler.shieldSilverDamageMultiplier != 1.0F && ((EntityLivingBase)victim).isEntityUndead() )
        			    {
        			    	damage *= ConfigurationHandler.shieldSilverDamageMultiplier;
        			    	EventHandlers.playSilverArmorEffect(victim);
        			    }
        			    
        			    if ( damage > 0 )
        			    {
	        			    victim.hurtResistantTime = 0;
	        			    ((EntityLivingBase)victim).knockBack((Entity)player, ((float)(message.knockback)/100.0F) + knockLvl, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
	        			    victim.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), damage); // XXX
	        			    shield.damageItem(5, (EntityLivingBase)player);
	        			    player.onCriticalHit(victim);
        			    }
        			    
        			    player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BLOCK, player.getSoundCategory(), 1.0F, 1.0F);
        			    
        		    }
        		    else
        		    {
        			    player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 0.5F, 0.01F);
        		    }
        			
              	    player.stopActiveHand();
              	    player.swingArm(shieldHand);
              	    player.getCooldownTracker().setCooldown(shield.getItem(), message.cooldown);
              	    
        	    } 
          	} 
        }
    }
}
//public class PacketShieldBash extends PacketBase<PacketShieldBash> {
//  protected int entityId;
//  
//  protected boolean attackEntity = false;
//  
//  protected EnumHand hand;
//  
//  public PacketShieldBash(EnumHand enumHand, int entId, boolean atkEntity) {
//    this.hand = enumHand;
//    this.entityId = entId;
//    this.attackEntity = atkEntity;
//  }
//  
//  public void fromBytes(ByteBuf buf) {
//    this.hand = EnumHand.values()[ByteBufUtils.readVarInt(buf, 1)];
//    this.entityId = ByteBufUtils.readVarInt(buf, 4);
//    this.attackEntity = buf.readBoolean();
//  }
//  
//  public void toBytes(ByteBuf buf) {
//    ByteBufUtils.writeVarInt(buf, this.hand.ordinal(), 1);
//    ByteBufUtils.writeVarInt(buf, this.entityId, 4);
//    buf.writeBoolean(this.attackEntity);
//  }
//  
//  public void handleClientSide(PacketShieldBash message, EntityPlayer player) {}
//  
//  public void handleServerSide(PacketShieldBash message, EntityPlayerMP player) {
//    boolean attackEntity = false;
//    if (message == null || player == null)
//      return; 
//    EnumHand shieldHand = message.hand;
//    int entId = message.entityId;
//    attackEntity = message.attackEntity;
//    Entity victim = player.world.getEntityByID(entId);
//    if (player.isActiveItemStackBlocking()) {
//      ItemStack shield = player.getHeldItem(shieldHand);
//      if (!shield.isEmpty() && !player.getCooldownTracker().hasCooldown(shield.getItem()) && shield.getItem() instanceof com.oblivioussp.spartanshields.item.ItemShieldBase) {
//        if (attackEntity && victim != null && victim instanceof EntityLivingBase) {
//          int knockLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, shield);
//          victim.hurtResistantTime = 0;
//          ((EntityLivingBase)victim).knockBack((Entity)player, 1.0F + knockLvl, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
//          victim.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 1.0F);
//          shield.damageItem(5, (EntityLivingBase)player);
//          player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BLOCK, player.getSoundCategory(), 1.0F, 1.0F);
//          player.onCriticalHit(victim);
//        } else {
//          player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 0.5F, 0.01F);
//        } 
//        player.stopActiveHand();
//        player.swingArm(shieldHand);
//        player.getCooldownTracker().setCooldown(shield.getItem(), ConfigHandler.cooldownShieldBash);
//      } 
//    } 
//  }
//  
//  public PacketShieldBash() {}
//}
//
//
//
//
