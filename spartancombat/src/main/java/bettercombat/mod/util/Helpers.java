/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package bettercombat.mod.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import bettercombat.mod.util.ConfigurationHandler.CustomWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

public final class Helpers
{
    private Helpers()
    {
    	
    }
    
    /* ============================================================================================================ */
    /* REACH */
    /* ============================================================================================================ */
    public static double getOffhandReach(double configWeaponReach, EntityPlayer player, ItemStack oh, ItemStack mh)
    {
        double reach = configWeaponReach + ConfigurationHandler.baseReachDistance;

        double multiply_base = 1.0D;

        double multiply = 1.0D;
        
        /* + ALL */
    	for ( AttributeModifier attribute : player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getModifiers() )
		{
    		//Helpers.message("getmodifiers" + attribute.getAmount());

    		switch ( attribute.getOperation() )
        	{
	    		case 0:
	    		{
	    			reach += attribute.getAmount();
	            	break;
	    		}
	    		case 1:
	    		{
	            	multiply_base += attribute.getAmount();
	            	break;
	    		}
	    		case 2:
	    		{
	            	multiply *= (1.0D+attribute.getAmount());
	            	break;
	    		}
        	}
		}
    	
        /* - MAINHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("reach") )
        	{
        		//Helpers.message("-key mh " + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			reach -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply /= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
        /* + OFFHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("reach") )
        	{
        		//Helpers.message("+key oh" + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			reach += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply *= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
    	/* - MAINHAND QUALITY TOOLS */
        for ( Map.Entry<String, AttributeModifier> modifier : mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("reach") )
        	{
        		//Helpers.message("keyreach " + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			reach -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply /= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
        /* + OFFHAND QUALITY TOOLS */
        if ( oh.hasTagCompound() && oh.getTagCompound().hasKey("Quality", 10) )
        {
            final NBTTagCompound tag = oh.getSubCompound("Quality");
            final NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for ( int j = 0; j < attributeList.tagCount(); ++j )
    		{
        		final AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(j));
        		final String attributeName = attributeList.getCompoundTagAt(j).getString("AttributeName");

        		if ( attributeName.contains("reach") )
        		{
            		//Helpers.message("tag " + modifier.getAmount());

        			switch ( modifier.getOperation() )
    	        	{
	    	        	case 0:
	            		{
	            			reach += modifier.getAmount();
	                    	break;
	            		}
	            		case 1:
	            		{
	                    	multiply_base += modifier.getAmount();
	                    	break;
	            		}
	            		case 2:
	            		{
	                    	multiply *= (1.0D+modifier.getAmount());
	                    	break;
	            		}
    	        	}
        		}
    		}
        }
        
        return reach *= multiply_base *= multiply;
    }
        
    /* ============================================================================================================ */
    /* COOLDOWN */
    /* ============================================================================================================ */
    public static int getOffhandCooldown(EntityPlayer player, ItemStack oh, ItemStack mh)
    {
        double speed = ConfigurationHandler.baseAttackSpeed;

        double multiply_base = 1.0D;

        double multiply = 1.0D;

        /* + ALL */
    	for ( AttributeModifier attribute : player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getModifiers() )
		{
    		//Helpers.message("+all " + attribute.getAmount());

    		switch ( attribute.getOperation() )
        	{
        		case 0:
        		{
        			speed += attribute.getAmount();
                	break;
        		}
        		case 1:
        		{
                	multiply_base += attribute.getAmount();
                	break;
        		}
        		case 2:
        		{
                	multiply *= (1.0D+attribute.getAmount());
                	break;
        		}
        	}
		}
    	
        /* - MAINHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("attackSpeed") )
        	{
        		//Helpers.message("-key mh " + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			speed -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply /= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
        /* + OFFHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("attackSpeed") )
        	{
        		//Helpers.message("+key oh" + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			speed += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply *= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
        /* - MAINHAND QUALITY TOOLS */
        if ( mh.hasTagCompound() && mh.getTagCompound().hasKey("Quality", 10) )
        {
            final NBTTagCompound tag = mh.getSubCompound("Quality");
            final NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for ( int j = 0; j < attributeList.tagCount(); ++j )
    		{
        		final AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(j));
        		final String attributeName = attributeList.getCompoundTagAt(j).getString("AttributeName");

        		if ( attributeName.contains("attackSpeed") )
        		{
            		//Helpers.message("-tag oh" + modifier.getAmount());

        			switch ( modifier.getOperation() )
    	        	{
			        	case 0:
		        		{
		        			speed -= modifier.getAmount();
		                	break;
		        		}
		        		case 1:
		        		{
		                	multiply_base -= modifier.getAmount();
		                	break;
		        		}
		        		case 2:
		        		{
		                	multiply /= (1.0D+modifier.getAmount());
		                	break;
		        		}
    	        	}
        		}
    		}
        }
        
        /* + OFFHAND QUALITY TOOLS */
        if ( oh.hasTagCompound() && oh.getTagCompound().hasKey("Quality", 10) )
        {
            final NBTTagCompound tag = oh.getSubCompound("Quality");
            final NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for ( int j = 0; j < attributeList.tagCount(); ++j )
    		{
        		final AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(j));
        		final String attributeName = attributeList.getCompoundTagAt(j).getString("AttributeName");

        		if ( attributeName.contains("attackSpeed") )
        		{
            		//Helpers.message("+tag oh " + modifier.getAmount());

        			switch ( modifier.getOperation() )
    	        	{
	    	        	case 0:
		        		{
		        			speed += modifier.getAmount();
		                	break;
		        		}
		        		case 1:
		        		{
		                	multiply_base += modifier.getAmount();
		                	break;
		        		}
		        		case 2:
		        		{
		                	multiply *= (1.0D+modifier.getAmount());
		                	break;
		        		}
    	        	}
        		}
    		}
        }
        
        return (int)((20.0D/MathHelper.clamp(speed * multiply_base * multiply, 0.1D, 20.0D))+0.5D);
    }
    
    /* ============================================================================================================ */
    /* ATTACK DAMAGE */
    /* ============================================================================================================ */
    public static double getOffhandAttackDamage(EntityPlayer player, ItemStack oh, ItemStack mh)
    {
        double attackDamage = ConfigurationHandler.baseAttackDamage;

        double multiply_base = 1.0D;

        double multiply = 1.0D;
        
        /* + ALL */
    	for ( AttributeModifier attribute : player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getModifiers() )
		{
    		//Helpers.message("getmodifiers" + attribute.getAmount());

    		switch ( attribute.getOperation() )
        	{
	    		case 0:
	    		{
	    			attackDamage += attribute.getAmount();
	            	break;
	    		}
	    		case 1:
	    		{
	            	multiply_base += attribute.getAmount();
	            	break;
	    		}
	    		case 2:
	    		{
	            	multiply *= (1.0D+attribute.getAmount());
	            	break;
	    		}
        	}
		}
    	
        /* - MAINHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("attackDamage") )
        	{
        		//Helpers.message("-key mh " + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			attackDamage -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base -= modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply /= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
        /* + OFFHAND */
        for ( Map.Entry<String, AttributeModifier> modifier : oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
        {
        	if ( modifier.getKey().contains("attackDamage") )
        	{
        		//Helpers.message("+key oh" + modifier.getValue().getAmount());

	        	switch ( modifier.getValue().getOperation() )
	        	{
		        	case 0:
	        		{
	        			attackDamage += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 1:
	        		{
	                	multiply_base += modifier.getValue().getAmount();
	                	break;
	        		}
	        		case 2:
	        		{
	                	multiply *= (1.0D+modifier.getValue().getAmount());
	                	break;
	        		}
	        	}
        	}
        }
        
    	/* - MAINHAND QUALITY TOOLS */
        if ( mh.hasTagCompound() && mh.getTagCompound().hasKey("Quality", 10) )
        {
            final NBTTagCompound tag = mh.getSubCompound("Quality");
            final NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for ( int j = 0; j < attributeList.tagCount(); ++j )
    		{
        		final AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(j));
        		final String attributeName = attributeList.getCompoundTagAt(j).getString("AttributeName");

        		if ( attributeName.contains("attackDamage") )
        		{
            		//Helpers.message("tag " + modifier.getAmount());

        			switch ( modifier.getOperation() )
    	        	{
	    	        	case 0:
	            		{
	            			attackDamage -= modifier.getAmount();
	                    	break;
	            		}
	            		case 1:
	            		{
	                    	multiply_base -= modifier.getAmount();
	                    	break;
	            		}
	            		case 2:
	            		{
	                    	multiply /= (1.0D+modifier.getAmount());
	                    	break;
	            		}
    	        	}
        		}
    		}
        }
        
        /* + OFFHAND QUALITY TOOLS */
        if ( oh.hasTagCompound() && oh.getTagCompound().hasKey("Quality", 10) )
        {
            final NBTTagCompound tag = oh.getSubCompound("Quality");
            final NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for ( int j = 0; j < attributeList.tagCount(); ++j )
    		{
        		final AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(j));
        		final String attributeName = attributeList.getCompoundTagAt(j).getString("AttributeName");

        		if ( attributeName.contains("attackDamage") )
        		{
            		//Helpers.message("tag " + modifier.getAmount());

        			switch ( modifier.getOperation() )
    	        	{
	    	        	case 0:
	            		{
	            			attackDamage += modifier.getAmount();
	                    	break;
	            		}
	            		case 1:
	            		{
	                    	multiply_base += modifier.getAmount();
	                    	break;
	            		}
	            		case 2:
	            		{
	                    	multiply *= (1.0D+modifier.getAmount());
	                    	break;
	            		}
    	        	}
        		}
    		}
        }
        
        return attackDamage *= multiply_base *= multiply;
    }
    
    
    /* ============================================================================================================ */
    /* ATTACK DAMAGE */
    /* ============================================================================================================ */
    public static double getMainhandAttackDamage(EntityPlayer player, ItemStack mh)
    {
        double attackDamage = ConfigurationHandler.baseAttackDamage;

        double multiply_base = 1.0D;

        double multiply = 1.0D;
        
        /* + ALL */
    	for ( AttributeModifier attribute : player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getModifiers() )
		{
    		//Helpers.message("getmodifiers" + attribute.getAmount());

    		switch ( attribute.getOperation() )
        	{
	    		case 0:
	    		{
	    			attackDamage += attribute.getAmount();
	            	break;
	    		}
	    		case 1:
	    		{
	            	multiply_base += attribute.getAmount();
	            	break;
	    		}
	    		case 2:
	    		{
	            	multiply *= (1.0D+attribute.getAmount());
	            	break;
	    		}
        	}
		}
    	
        return attackDamage *= multiply_base *= multiply;
    }
    
    
    
     // attackvictimWithCurrentItem
    
    
    
//       TextComponentString
//       AttackEntityEvent
//       EntityPlayer -> attackvictimWithCurrentItem
//	   player.sendMessage( new TextComponentString("pre: " + damage));
//	   net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, victim);
//       NBTTagList a = player.getHeldItemMainhand().getEnchantmentTagList();
//       for (int i = 0; i < a.tagCount(); ++i)
//	   {
//	       int j = a.getCompoundTagAt(i).getShort("id");
//	       int k = a.getCompoundTagAt(i).getShort("lvl");
//	
//	       if ( Enchantment.getEnchantmentByID(j) != null )
//	       {
//	           player.sendMessage( new TextComponentString( "j " + j + "k " + k + "   " + a.toString()));
//	       }
//	   }
	   
//    protected void updateArmSwingProgress()
//    {
//        int i = this.getArmSwingAnimationEnd();
//		  i = 6;
//
//        if (this.isSwingInProgress)
//        {
//            ++this.swingProgressInt;
//
//            if (this.swingProgressInt >= 6)
//            {
//                this.swingProgressInt = 0;
//                this.isSwingInProgress = false;
//            }
//        }
//        else
//        {
//            this.swingProgressInt = 0;
//        }
//
//        this.swingProgress = this.swingProgressInt / 6;
//    }
//    
//    public void resetActiveHand()
//    {
//        if (!this.world.isRemote)
//        {
//            this.dataManager.set(HAND_STATES, Byte.valueOf((byte)0));
//        }
//
//        this.activeItemStack = ItemStack.EMPTY;
//        this.activeItemStackUseCount = 0;
//    }
    
    
    
    
    // if main left click hand while off right click hand, then it resets off hand
    
    public static void playerAttackVictim( EntityPlayer player, Entity victim, boolean mainHand )
    {
    	if ( mainHand ) /* MAINHAND */
    	{
    		player.swingingHand = EnumHand.MAIN_HAND;

        	final ItemStack oh = player.getHeldItemOffhand();
        	final ItemStack mh = player.getHeldItemMainhand();
            player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
            
            /* ATTACK */
            try
            {
            	playerAttackVictimWithWeapon(player, victim, ConfigurationHandler.isItemAttackUsable(mh.getItem()), mh, mh, oh, true);
            }
            finally
            {
            	player.setHeldItem(EnumHand.OFF_HAND, oh);
            }
            
            if ( !mh.isEmpty() && victim instanceof EntityLivingBase )
            {
                ItemStack beforeHitCopy = mh.copy();
                mh.hitEntity((EntityLivingBase) victim, player);
                if ( mh.isEmpty() )
                {
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, EnumHand.MAIN_HAND);
                }
            }
    	}
    	else /* OFFHAND */
    	{
    		player.swingingHand = EnumHand.OFF_HAND;
    		
        	final ItemStack oh = player.getHeldItemOffhand();
        	final ItemStack mh = player.getHeldItemMainhand();
        	            
//	        if ( !mh.isEmpty() )
//	        {
//	            player.getAttributeMap().removeAttributeModifiers(mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
//	        }
//	        
//	        if ( !oh.isEmpty() )
//	        {
//            	player.getAttributeMap().applyAttributeModifiers(oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
//	        }

            player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
            player.setHeldItem(EnumHand.MAIN_HAND, oh);
            
            /* ATTACK */
            try
            {
            	playerAttackVictimWithWeapon(player, victim, true, mh, mh, oh, false);
            }
            finally
            {
            	player.setHeldItem(EnumHand.OFF_HAND, oh);
            	player.setHeldItem(EnumHand.MAIN_HAND, mh);
            	
//            	if ( !mh.isEmpty() )
//    	        {
//    	            player.getAttributeMap().removeAttributeModifiers(mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
//    	        }
//    	        
//    	        if ( !oh.isEmpty() )
//    	        {
//                	player.getAttributeMap().applyAttributeModifiers(oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
//    	        }
            	
            }
            
            if ( !oh.isEmpty() && victim instanceof EntityLivingBase )
            {
                ItemStack beforeHitCopy = oh.copy();
                oh.hitEntity((EntityLivingBase) victim, player);
                if ( oh.isEmpty() )
                {
                    player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
                    ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, EnumHand.OFF_HAND);
                }
            }
    	}
    }
    
	/* vanilla attacks are cancelled and this method is instead called with  betterCombatAttack = false */
    public static void playerAttackVictimWithWeapon(EntityPlayer player, Entity victim, boolean configWeapon, ItemStack weapon, ItemStack mh, ItemStack oh, boolean mainHand)
    {
        if ( player == null || player.swingingHand == null || victim == null || victim == player || !victim.isEntityAlive() || victim.hitByEntity(player) || player.isOnSameTeam(victim) || !victim.canBeAttackedWithItem() )
        {
        	return;
        }
    	
        /* INSTANCE VARIABLES */
        String weaponName = "";
        double damage = 0.0D;

		if ( mainHand )
		{
			damage = getMainhandAttackDamage(player, mh);
		}
		else
		{
			damage = getOffhandAttackDamage(player, oh, mh);
			damage *= ConfigurationHandler.offHandEfficiency;
		}
		
        if ( configWeapon )
        {
        	weaponName = weapon.getItem().getRegistryName().toString();
        }
        else
        {
        	damage -= ConfigurationHandler.fistAndNonWeaponReducedDamage;
        }
        
        boolean isCrit = false;
        int knockbackMod = EnchantmentHelper.getKnockbackModifier(player);
        int fireAspect = EnchantmentHelper.getFireAspectModifier(player);
		double extraCritChance = ConfigurationHandler.randomCrits;
    	try{extraCritChance = (float)(player.getEntityAttribute(BetterCombatAttributes.CRIT_CHANCE).getAttributeValue());}catch ( Exception e ){}
    	float extraCritDamage = ConfigurationHandler.bonusCritDamage;
        try{extraCritDamage = (float) player.getEntityAttribute(BetterCombatAttributes.CRIT_DAMAGE).getAttributeValue();}catch ( Exception e ){}
        double enchantmentModifier = 0.0D;
    	double armor = 0.0D;
    	double armorToughness = 0.0D;
        boolean hasMetalSweepSound = false;
    	int sweepAmount = weapon.getItem() instanceof ItemSword ? 0 : -1;
    	/* ================== */
    	
       	for ( CustomWeapon s : ConfigurationHandler.weapons )
    	{
    		if ( weaponName.contains(s.name) )
    		{
    			sweepAmount = s.sweepMod;
    			extraCritChance += s.critChanceMod;
    			extraCritDamage += s.critDamageMod;
    			break;
    		}
    	}
       	
       	if ( sweepAmount >= 0 || weaponName.contains("sword") )
       	{
       		hasMetalSweepSound = true;
       	}
       	
        float tgtHealth = 0.0F;
        float tgtMaxHealth = 0.0F;
        float tgtHealthPercent = 0.0F;
        
        if ( player.isSprinting() )
        {
            knockbackMod++;
        }

        if ( victim instanceof EntityLivingBase )
        {
        	try
        	{
				armor = ((EntityLivingBase)victim).getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
			}
        	catch ( Exception e )
        	{
        		
        	}
        	
			try
			{
				armorToughness = ((EntityLivingBase)victim).getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
			}
			catch ( Exception e )
			{
				
			}
			
			for ( PotionEffect effect : player.getActivePotionEffects() )
			{
				switch ( effect.getPotion().getRegistryName().toString() )
				{
					case "bettercombatmod:precision":
					{
						extraCritChance += (1+effect.getAmplifier())*ConfigurationHandler.critChancePotionAmplifier;
						break;
					}
					case "bettercombatmod:brutality":
					{
						extraCritDamage += (1+effect.getAmplifier())*ConfigurationHandler.critDamagePotionAmplifier;
						break;
					}
					case "wards:effect_knockback":
					{
						knockbackMod += (1+effect.getAmplifier());
						break;
					}
					case "wards:effect_sharpness":
					{
						enchantmentModifier += 0.5F + (1+effect.getAmplifier()) * 0.5F;
						break;
					}
					case "wards:effect_smite":
					{
						if ( ((EntityLivingBase)victim).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD )
						{
							enchantmentModifier += (1+effect.getAmplifier()) * 2.5F;
						}
						break;
					}
					case "wards:effect_arthropods":
					{
						if ( ((EntityLivingBase)victim).getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD )
						{
							enchantmentModifier += (1+effect.getAmplifier()) * 2.5F;
						}
						break;
					}
					case "wards:effect_fire_aspect":
					{
						fireAspect += (1+effect.getAmplifier());
						break;
					}
					case "wards:effect_sweeping":
					{
						sweepAmount += (1+effect.getAmplifier());
						break;
					}
					case "wards:effect_unbreaking":
					{									
						if ( !weapon.isEmpty() && weapon.isItemDamaged() )
						{
							if ( player.world.rand.nextInt((2+effect.getAmplifier())) > 0 )
							{
								weapon.setItemDamage(weapon.getItemDamage()-1);
							}
						}
						break;
					}
					default :
					{
						break;
					}
				}
			}

        	if ( ConfigurationHandler.autoCritOnSneakAttacks )
        	{	                	
                if ( victim instanceof IMob && victim instanceof EntityLiving )
                {
                	EntityLiving el = ((EntityLiving) victim);
                	
                	if ( el.getAttackTarget() != player && el.getRevengeTarget() != player )
                	{
                		isCrit = true;
                	}
                }
        	}
        	        	                    
            try
            {
            	tgtHealthPercent = tgtHealth/tgtMaxHealth;
            }
            catch ( Exception e )
            {
            	tgtHealthPercent = 0.0F;
            }

            if ( fireAspect > 0 )
            {
                victim.setFire(fireAspect*4);
            }
			
            enchantmentModifier = EnchantmentHelper.getModifierForCreature(weapon, ((EntityLivingBase)victim).getCreatureAttribute());
        }
        else
        {
            enchantmentModifier = EnchantmentHelper.getModifierForCreature(weapon, EnumCreatureAttribute.UNDEFINED);
        }
        
        /* CombatRules */
        if ( (armor > 0.0D || armorToughness > 0.0D) && ConfigurationHandler.warhammerArmorPierceAdjustments && weaponName.contains("warhammer_") )
        {
            double damageAfterArmor = damage * (1.0F - (MathHelper.clamp(armor - damage / (2.0F + armorToughness / 4.0F), armor * 0.2F, 20.0F)) / 25.0F);
            
            if ( damageAfterArmor < damage )
            {
                damage += damage-(damageAfterArmor*0.8F);
            }
        }
        
        if ( ConfigurationHandler.randomCrits > 0.0F )
        {
        	extraCritChance +=
    			(((player.fallDistance > 0.0D && !player.onGround) || player.isSprinting())
    			&& !player.isOnLadder()
    			&& !player.isInWater()
    			&& !player.isRiding()
    			&& !player.isPotionActive(MobEffects.BLINDNESS))
    			? ConfigurationHandler.jumpAndSprintCrits : 0.0D;
        	
        	float luckCritChance = (float)(player.getEntityAttribute(SharedMonsterAttributes.LUCK).getAttributeValue() * ConfigurationHandler.luckCritModifier);
        	
        	isCrit = player.getRNG().nextFloat() <= (extraCritChance + luckCritChance);
        }
        else
        {
            isCrit = ((player.fallDistance > 0.0F && !player.onGround))
            		&& !player.isOnLadder()
            		&& !player.isInWater()
            		&& !player.isRiding()
                    && !player.isPotionActive(MobEffects.BLINDNESS);
        }
        
        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, victim, false, isCrit ? extraCritDamage : 1.0F);
        
        if ( hitResult != null && isCrit )
        {
            damage *= extraCritDamage;
        }
        
        damage += enchantmentModifier;
                      
        if ( !ConfigurationHandler.moreSprint )
        {
            player.setSprinting(false);
        }

        if ( ConfigurationHandler.removeHurtResistantTime )
    	{
    		victim.hurtResistantTime = 0;
    	}
        
        boolean attacked = victim.attackEntityFrom(DamageSource.causePlayerDamage(player), (float)damage);

        //if ( ConfigurationHandler.inertiaOnAttack != 1.0F ) XXX
//        {
//            player.motionX = 0;//ConfigurationHandler.inertiaOnAttack;
//            player.motionZ = 0;//ConfigurationHandler.inertiaOnAttack;
//            player.velocityChanged = true;
//        }
        
        if ( victim instanceof EntityLivingBase )
        {
           	if ( attacked )
            {
                if ( configWeapon && ConfigurationHandler.moreSweep )
                {
                	spawnSweepHit(player, victim);
                }
                
                if ( knockbackMod > 0 )
                {
              	 	player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);

              	 	if ( victim instanceof EntityLivingBase )
                    {
                        ((EntityLivingBase) victim).knockBack(player, knockbackMod * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                    }
                    else
                    {
                        victim.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F);
                    }
                }
                
//                if ( ConfigurationHandler.inertiaOnAttack != 1.0F )
//                {
//                    player.motionX *= ConfigurationHandler.inertiaOnAttack;
//                    player.motionZ *= ConfigurationHandler.inertiaOnAttack;
//                }
               	
            	NBTTagList nbttaglist = weapon.getEnchantmentTagList();
            	
               	/* SWEEPING Weapons with this property will inflict half attack damage (for level I) or full attack damage (for level II) to all targets that get hit by the sweep attack */
                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                    Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
                    int j = nbttagcompound.getShort("lvl");
                    if ( enchantment == Enchantments.SWEEPING )
                    {
                        if ( j > 0 )
                        {
                        	/* Level 1 Sweeping Edge = 1 (goes up to 3) */
                        	sweepAmount += j;
                        }
                    }
                }
	
        		if ( sweepAmount >= 0 )
                {
                    boolean swept = false;
                    double range = (2.0D+sweepAmount)/2.0D;
                    double distance = player.getDistance(victim);
                    
                    if ( distance < ConfigurationHandler.baseReachDistance )
                    {
                    	distance = ConfigurationHandler.baseReachDistance;
                    }

                    List<EntityLivingBase> sweepVictims = player.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(victim.posX-range,victim.posY-1.0D,victim.posZ-range,victim.posX+range,victim.posY+1.0D,victim.posZ+range));
                    
                    for ( EntityLivingBase sweepVictim : sweepVictims )
                    {
                    	if ( sweepVictim != player && sweepVictim != victim && sweepVictim.isEntityAlive() && player.getDistance(sweepVictim) <= distance && !player.isOnSameTeam(victim) )
                        {
                        	swept = true;
                        	double sweepDamage = ConfigurationHandler.baseAttackDamage;
                        	
                        	if ( sweepAmount > 0 )
                        	{
                        		sweepDamage = MathHelper.clamp(damage*(sweepAmount/4.0D), ConfigurationHandler.baseAttackDamage, damage);
                        	}

                            if ( ConfigurationHandler.removeHurtResistantTime )
                        	{
                            	sweepVictim.hurtResistantTime = 0;
                        	}
                            
                        	sweepVictim.attackEntityFrom(DamageSource.causePlayerDamage(player), (float)sweepDamage);
                        	
                        	sweepVictim.knockBack(player, (1+sweepAmount)/10.0F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                        }
                    }
                    
                    if ( swept )
                    {
                    	player.spawnSweepParticles();
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), ConfigurationHandler.weaponHitSoundVolume, 0.8F + player.world.rand.nextFloat()*0.4F);
                    }
                }
                
            	float volume = ConfigurationHandler.weaponHitSoundVolume*(1.2F-player.world.rand.nextFloat()*0.4F);
                float pitch = 0.8F+player.world.rand.nextFloat()*0.4F;
                
            	if ( isCrit )
                {
                    player.onCriticalHit(victim);
                    player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), volume, pitch);
                    if ( enchantmentModifier > 0.0F )
                    {
                        player.onEnchantmentCritical(victim);
                    }
                }
            	else
            	{
            		player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
            	}
            	
            	/* SHIELD */
                if ( victim instanceof EntityLivingBase )
                {
                	EntityLivingBase e = (EntityLivingBase)victim;
                	
                    ItemStack activeItem = e.isHandActive() ? e.getActiveItemStack() : ItemStack.EMPTY;
                    
                    boolean isBlocking = (activeItem.getItem() instanceof ItemShield);
                    
                    if ( isBlocking )
                    {
                        if ( ( weapon.getItem() instanceof ItemAxe || weapon.getItem().getRegistryName().toString().contains("battleaxe_") || weapon.getItem().getRegistryName().toString().contains("halberd_") ) )
                        {
                        	e.resetActiveHand();
                            if ( e instanceof EntityPlayer )
                            {
                            	((EntityPlayer)e).getCooldownTracker().setCooldown(activeItem.getItem(), 100);
                            }
                            player.world.setEntityState(e, (byte) 30);
                        }
                        else if ( isCrit && ConfigurationHandler.critsDisableShield > 0 )
                        {
                        	e.resetActiveHand();
                            if ( e instanceof EntityPlayer )
                            {
                            	((EntityPlayer)e).getCooldownTracker().setCooldown(activeItem.getItem(), ConfigurationHandler.critsDisableShield);
                            }
                            player.world.setEntityState(e, (byte) 30);
                        }
                    }
                }

                player.setLastAttackedEntity(victim);

                if ( victim instanceof EntityLivingBase )
                {
                    EnchantmentHelper.applyThornEnchantments((EntityLivingBase) victim, player);
                }

                EnchantmentHelper.applyArthropodEnchantments(player, victim);
                
                /* multipart entity */
                if ( victim instanceof MultiPartEntityPart )
                {
                    IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) victim).parent;
                    if( ientitymultipart instanceof EntityLivingBase )
                    {
                    	victim = (EntityLivingBase) ientitymultipart;
                    }
                }
                
                /* SOUNDS */
                if ( ConfigurationHandler.hitSound )
                {
                	/* wood */
                	boolean metal = !weaponName.isEmpty();
                	
                	if ( metal )
                	{
	                	for ( String s : ConfigurationHandler.nonMetalList )
	                	{
	                		if ( weaponName.contains(s) )
	                		{
	                			metal = false;
	                			break;
	                		}
	                	}
                	}

                	if ( metal )
                	{
                		/* sword */
                		if ( hasMetalSweepSound )
                		{          
                			armor += armorToughness;
                			if ( armor > 1 && !isCrit && damage <= tgtHealth && player.world.rand.nextFloat()*2.0F*damage <= (player.world.rand.nextDouble()+tgtHealthPercent)*armor )
                			{
                				/* ARMOR */
                				metalArmorHitSound(player, volume, pitch, true); // armor
                			}
                			else
                			{
                				metalSwordHitSound(player, volume, pitch);
                			}
                    	}
                		/* blunt */
                		else
                		{
                			armor += armorToughness;
                			if ( armor > 1 && !isCrit && damage <= tgtHealth && player.world.rand.nextFloat()*2.0F*damage <= (player.world.rand.nextDouble()+tgtHealthPercent)*armor )
                			{
                				/* ARMOR */
                				metalArmorHitSound(player, volume, pitch, false);
                			}
                			else
                			{
                				metalBluntHitSound(player, volume, pitch);
                			}
                		}
                	}
                	/* wood, stone, punching */
                	else
                	{
        				nonMetalHitSound(player, volume, pitch);
                	}
                }
                else /* VANILLA SOUNDS */
                {
                  	if ( isCrit )
                    {
                  	 	player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), volume, pitch);
                    }
                    else
                    {
                	    player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), volume, pitch);
                    }
                }
                
                /* DAMAGE PARTICLES */
                if ( ConfigurationHandler.damageParticles && player.world instanceof WorldServer )
                {	
                    if ( damage > 2.0F )
                    {
                        int k = (int) (damage * 0.5D);
                        ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, victim.posX, victim.posY + victim.height * 0.5F, victim.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }
                }
            }
            else
            {
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 0.8F + player.world.rand.nextFloat()*0.4F);
            }
            player.addExhaustion(0.1F);
        }
    }    
    
    public static final UUID STRENGTH_POTION_UUID = UUID.fromString("648d7064-6a60-4f59-8abe-c2c23a6dd7a9");
    public static final UUID WEAKNESS_POTION_UUID = UUID.fromString("22653b89-116e-49dc-9b6b-9971489b5be5");

    /* https://algorithms.tutorialhorizon.com/convert-integer-to-roman/ */
    public static String integerToRoman(int num)
    {
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder roman = new StringBuilder();

        for ( int i = 0; i < values.length; i++ )
        {
            while ( num >= values[i] )
            {
                num -= values[i];
                roman.append(romanLiterals[i]);
            }
        }
        
        return roman.toString();
    }
    
    public static void spawnSweepHit( EntityPlayer e, Entity target )
    {
        double d0 = (double)(-MathHelper.sin(e.rotationYaw * 0.017453292F));
        double d1 = (double)MathHelper.cos(e.rotationYaw * 0.017453292F);

        if (e.world instanceof WorldServer)
        {
            ((WorldServer)e.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, target.posX + d0 * 0.5D, e.posY + e.height * 0.5D, target.posZ + d1 * 0.5D, 0, d0, 0.0D, d1, 0.0D);
        }
    }
    
    /* ARMOR HIT */
    private static void metalArmorHitSound(EntityPlayer player, float volume, float pitch, boolean isSword )
    {

        if ( isSword )
        {
        	if ( player.world.rand.nextBoolean() )
        	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_RING0, player.getSoundCategory(), volume, pitch);
        	}
        	else
        	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_RING1, player.getSoundCategory(), volume, pitch);
        	}
        }
        else
        {
        	player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.ARMOR_HIT, player.getSoundCategory(), volume, pitch);
        }
	}
    
    /* METAL SWORD HIT */
    private static void metalSwordHitSound(EntityPlayer player, float volume, float pitch)
    {
    	switch ( player.world.rand.nextInt(4) )
	    {
	    	case 0:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_HIT0, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 1:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_HIT1, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 2:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_HIT2, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 3:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_HIT3, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	default :
	    	{
	    		break;
	    	}
	    }
	}
    
    /* METAL BLUNT HIT */
    private static void metalBluntHitSound(EntityPlayer player, float volume, float pitch)
    {
    	switch ( player.world.rand.nextInt(4) )
	    {
	    	case 0:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.BLUNT_HIT0, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 1:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.BLUNT_HIT1, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 2:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.BLUNT_HIT2, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 3:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.BLUNT_HIT3, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	default :
	    	{
	    		break;
	    	}
	    }
	}
    
    /* NONMETAL HIT */
    private static void nonMetalHitSound(EntityPlayer player, float volume, float pitch)
    {
    	switch ( player.world.rand.nextInt(6) )
	    {
	    	case 0:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT0, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 1:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT1, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 2:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT2, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 3:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT3, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 4:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT4, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
	    	case 5:
	    	{
                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT5, player.getSoundCategory(), volume, pitch);
	            break;
	    	}
//	    	case 6:
//	    	{
//                player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.NONMETAL_HIT6, player.getSoundCategory(), volume, pitch);
//	            break;
//	    	}
	    	default :
	    	{
	    		break;
	    	}
	    }
	}

    public static <T> void execNullable(@Nullable T obj, Consumer<T> onNonNull) {
        if( obj != null ) {
            onNonNull.accept(obj);
        }
    }

    public static <T, R> R execNullable(@Nullable T obj, Function<T, R> onNonNull, R orElse)
    {
        if( obj != null )
        {
            return onNonNull.apply(obj);
        }

        return orElse;
    }
    
//    public static int getOffhandCooldown(EntityPlayer player, ItemStack oh, ItemStack mh)
//    {
//        /* https://minecraft.fandom.com/wiki/Attribute */
//
//    	/*
//        OPERATION 0 (ADD)
//        
//        add (amount +/-): Saved as operation 0. Adds all of the modifiers' amounts
//        to the current value of the attribute. For example, modifying an attribute
//        with {Amount:2,Operation:0} and {Amount:4,Operation:0} with a Base of 3
//        results in 9 (3 + 2 + 4 = 9)
//	    */
//	    double speed = ConfigurationHandler.baseAttackSpeed;
//
//	    /*
//	    	OPERATION 1 ()
//	    	
//	        multiply_base (amount % +/-, additive): Saved as operation 1. Multiplies the
//	        current value of the attribute by (1 + x), where x is the sum of the modifiers'
//	        amounts. For example, modifying an attribute with {Amount:2,Operation:1} and
//	        {Amount:4,Operation:1} with a Base of 3 results in 21 (3 * (1 + 2 + 4) = 21)
//	    */
//	    double multiply_base = 1.0D;
//	    
//	    /*
//	    	OPERATION 2 ()
//	    
//	        multiply (amount % +/-, multiplicative): Saved as operation 2. For every modifier,
//	        multiplies the current value of the attribute by (1 + x), where x is the amount of
//	        the particular modifier. Functions the same as Operation 1 if there is only a single
//	        modifier with operation 1 or 2. However, for multiple modifiers it multiplies the
//	        modifiers rather than adding them. For example, modifying an attribute with
//	        {Amount:2,Operation:2} and {Amount:4,Operation:2} with a Base of 3 results in 45
//	        (3 * (1 + 2) * (1 + 4) = 45)
//	    */
//	    double multiply = 1.0D;
//		Helpers.message("===");
//
//	    /* ADD ALL MODIFIERS */
//    	for ( AttributeModifier attribute : player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getModifiers() )
//		{
//    		Helpers.message("add " + attribute.getAmount());
//    		switch ( attribute.getOperation() )
//        	{
//        		case 0:
//        		{
//        			speed += attribute.getAmount();
//                	break;
//        		}
//        		case 1:
//        		{
//                	multiply *= (1.0D+attribute.getAmount());
//                	break;
//        		}
//        		case 2:
//        		{
//                	multiply_base += attribute.getAmount();
//                	break;
//        		}
//        	}
//		}
//        
//        /* ADD OFFHAND QUALITY TOOLS */
//        for ( Map.Entry<String, AttributeModifier> modifier : oh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
//        {
//    		Helpers.message("a " + modifier.getValue().getAmount());
//
//        	if ( modifier.getKey().contains("attackSpeed") )
//        	{
//        		Helpers.message("add " + modifier.getValue().getAmount());
//
//	        	switch ( modifier.getValue().getOperation() )
//	        	{
//	        		case 0:
//	        		{
//	        			speed += modifier.getValue().getAmount();
//	        			break;
//	        		}
//	        		case 1:
//	        		{
//	                	multiply *= (1.0D+modifier.getValue().getAmount());
//	                	break;
//	        		}
//	        		case 2:
//	        		{
//	                	multiply_base += modifier.getValue().getAmount();
//	                	break;
//	        		}
//	        	}
//        	}
//        }
//        
//        /* REMOVE MAINHAND QUALITY TOOLS */
//        for ( Map.Entry<String, AttributeModifier> modifier : mh.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
//        { 
//    		Helpers.message("r " + modifier.getValue().getAmount());
//
//        	if ( modifier.getKey().contains("attackSpeed") )
//        	{
//        		Helpers.message("remove " + modifier.getValue().getAmount());
//
//	        	switch ( modifier.getValue().getOperation() )
//	        	{
//	        		case 0:
//	        		{
//	        			speed -= modifier.getValue().getAmount();
//	        			break;
//	        		}
//	        		case 1:
//	        		{
//	                	multiply /= (1.0D+modifier.getValue().getAmount());
//	                	break;
//	        		}
//	        		case 2:
//	        		{
//	                	multiply_base -= modifier.getValue().getAmount();
//	                	break;
//	        		}
//	        	}
//        	}
//        }
//		Helpers.message("===");
//
//        return (int)((20.0D/MathHelper.clamp(speed * multiply_base * multiply, 0.1D, 20.0D))+0.5D);
//    }
//    final static UUID MAIN_HAND_ATTACK_SPEED = UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    public static int getOffhandCooldown(EntityPlayer player)
//    {
//    	double power = 1.0D;
//        double speed = 0.0D;
//        
//    	Multimap<String, AttributeModifier> modifiers = player.getHeldItemOffhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
//        
//    	for ( Map.Entry<String, AttributeModifier> modifier : modifiers.entries())
//        {
//            if ( modifier.getKey().contains("attackSpeed") )
//            {
//                speed = modifier.getValue().getAmount();
//            }
//        }
//    	
//    	if ( player.isPotionActive(MobEffects.MINING_FATIGUE) )
//    	{
//    		power += (0.1D*(player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()+1));
//    	}
//    	
//    	if ( player.isPotionActive(MobEffects.HASTE) )
//    	{
//    		power -= (0.1D*(player.getActivePotionEffect(MobEffects.HASTE).getAmplifier()+1));
//    	}
//    	
//    	try
//    	{
//        	String speedString = StringUtils.substringBetween(StringUtils.reverse(player.getHeldItemOffhand().getTagCompound().toString()), "\"deepSkcatta.cireneg\":emaNetubirttA,", ":tnuomA");
//        	double speedDouble = Double.valueOf(StringUtils.reverse(speedString));
//        	power -= speedDouble;
//    	}
//    	catch(Exception e)
//    	{
//    		
//    	}
//        
//        if ( speed >= -3.9D )
//        {
//        	return (int)((20.0D/(4.0D+speed)*power)+0.5D);
//        }
//        else
//        {
//        	return (int)(200*power);
//        }
//    }
    
//    public static float getOffhandDamage(EntityPlayer player)
//    {
//        float attack = 1.0F;
//        
//        for ( Map.Entry<String, AttributeModifier> modifier : player.getHeldItemOffhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
//        {
//            if ( modifier.getKey().contains("attackDamage") )
//            {
//                attack += (float) modifier.getValue().getAmount();
//            }
//        }
//        
//        /* get all modifiers such as strength, sinful, and main hand attack damage */
//        for ( AttributeModifier modifier : player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getModifiers() )
//        {
//        	  attack += (float) modifier.getAmount();
//        }
//        
//        /* remove main hand attack damage */
//        for ( Map.Entry<String, AttributeModifier> modifier : player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries() )
//        {
//            if ( modifier.getKey().contains("attackDamage") )
//            {
//            	attack -= (float) modifier.getValue().getAmount();
//            }
//        }
//        
//        return ( attack ) * ConfigurationHandler.offHandEfficiency;
//    }

    public static int getOffhandFireAspect(EntityPlayer player) 
    {
        NBTTagList tagList = player.getHeldItemOffhand().getEnchantmentTagList();

        for ( int i = 0; i < tagList.tagCount(); i++ )
        {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            
            if ( tag.getInteger("id") == Enchantment.getEnchantmentID(Enchantments.FIRE_ASPECT) )
            {
                return tag.getInteger("lvl");
            }
        }

        return 0;
    }

    public static int getOffhandKnockback(EntityPlayer player)
    {
        NBTTagList tagList = player.getHeldItemOffhand().getEnchantmentTagList();

        for ( int i = 0; i < tagList.tagCount(); i++ )
        {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            
            if ( tag.getInteger("id") == Enchantment.getEnchantmentID(Enchantments.KNOCKBACK) )
            {
                return tag.getInteger("lvl");
            }
        }

        return 0;
    }
    
    public static void message(Object o)
    {
    	try
    	{
	    	Minecraft mc = Minecraft.getMinecraft();
	        EntityPlayer player = mc.player;
	        player.sendMessage(new TextComponentString(""+o));
        }
    	catch(Exception e)
    	{
        	
    	}
    }

    /**
     * This returns a null value for those final variables that have their values injected during runtime.
     * Prevents IDEs from warning the user of potential NullPointerExceptions on code using those variables.
     * @param <T> any type
     * @return null
     */
    @Nonnull
    public static <T> T getNull()
    {
        return null;
    }
}
