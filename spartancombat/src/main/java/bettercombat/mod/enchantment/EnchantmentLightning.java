package bettercombat.mod.enchantment;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import bettercombat.mod.util.BetterCombatMod;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Reference;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;


@Mod.EventBusSubscriber(modid=Reference.MOD_ID)
public class EnchantmentLightning extends Enchantment
{
    public EnchantmentLightning()
    {
        super(Rarity.VERY_RARE, BetterCombatMod.THROWING_WEAPON, new EntityEquipmentSlot[]
        {
        		EntityEquipmentSlot.MAINHAND,
        		EntityEquipmentSlot.OFFHAND
        });
        this.setName("lightning");
        this.setRegistryName(Reference.MOD_ID+":lightning");
        BetterCombatMod.ENCHANTMENTS.add(this);
    }

    @Override
    public int getMaxLevel()
    {
        return ConfigurationHandler.lightningEnchantmentMaxLevel;
    }
    
    @Override
    public int getMinEnchantability(int level)
    {
        return 1 + ConfigurationHandler.lightningEnchantmentEnchantabilityPerLevel * level;
    }
    
    public static boolean canActuallySeeSky(Entity victim)
    {
    	try
    	{
	        for ( int i = 0; i < 127; i++ )
	        {
	        	if ( i > 16 )
	        	{
	        		i+=16;
	        	}
	        	
	            IBlockState iblockstate = victim.world.getBlockState(victim.getPosition().up(i));
	
	            boolean flag = false;
	            
	            if ( iblockstate.getBlock() instanceof BlockAir || iblockstate.getBlock() instanceof BlockLiquid )
	            {
	            	flag = true;
	            }
	            else
	            {
	            	if ( flag )
	            	{
	            		return false;
	            	}
	            }
	        }
        }
    	catch(Exception e)
    	{
        	return false;
    	}

        return true;
    }
    
    public static void doLightning( Entity attacker, Entity victim, int lvl)
    {
	    if ( lvl > 0 )
		{
			if ( canActuallySeeSky(victim) )
			{
				for ( int i = 0; i < lvl; i++ )
				{
					EntityLightningBolt bolt = new EntityLightningBolt(victim.world, victim.posX + (victim.world.rand.nextBoolean() ? victim.world.rand.nextInt(lvl)*0.5F : -victim.world.rand.nextInt(lvl)*0.5F), victim.posY, victim.posZ + (victim.world.rand.nextBoolean() ? victim.world.rand.nextInt(lvl)*0.5F : -victim.world.rand.nextInt(lvl)*0.5F), true);
					
					try
					{
				    	Field field = ObfuscationReflectionHelper.findField(bolt.getClass(),"lightningState");
				    	field.setInt(bolt, 1);
					}
					catch ( Exception e )
					{
						
					}
					victim.world.addWeatherEffect(bolt);
				}
								
				victim.setFire(1);
				
				float a = 0.8F + victim.world.rand.nextFloat() * 0.2F;
				float b = 0.8F + victim.world.rand.nextFloat() * 0.2F;
				
				victim.world.playSound((EntityPlayer)attacker, victim.posX, victim.posY, victim.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 1.0F, a);
				victim.world.playSound((EntityPlayer)attacker, victim.posX, victim.posY, victim.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.HOSTILE, 1.0F, b);
				
				if ( attacker instanceof EntityPlayer )
				{
					attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 1.0F, a);
					attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.HOSTILE, 1.0F, b);
				}
				
				float r = 0.4F+lvl*0.4F;
				
                List<EntityLivingBase> livingList = victim.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(victim.getPosition()).grow(r, r, r));
                
                for ( EntityLivingBase elb : livingList )
                {                	
                	if ( victim != elb )
                	{
                		elb.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 13, 1, true, false));
                		elb.hurtResistantTime = 0;
        		        elb.attackEntityFrom(DamageSource.causeIndirectMagicDamage(attacker, attacker), victim.isWet() ? (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel)*ConfigurationHandler.lightningEnchantmentWetModifier : (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel));
                		// elb.attackEntityFrom(DamageSource.LIGHTNING_BOLT, victim.isWet() ? (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel)*ConfigurationHandler.lightningEnchantmentWetModifier : (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel) );
                		elb.hurtResistantTime = 0;
                	}
                }

                if ( victim instanceof EntityLivingBase ) ((EntityLivingBase)victim).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 13, 1, true, false));
				victim.hurtResistantTime = 0;
				if ( attacker != null )
				victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(attacker, attacker), victim.isWet() ? (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel)*ConfigurationHandler.lightningEnchantmentWetModifier : (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel));
				else victim.attackEntityFrom(DamageSource.LIGHTNING_BOLT.setMagicDamage(), victim.isWet() ? (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel)*ConfigurationHandler.lightningEnchantmentWetModifier : (ConfigurationHandler.lightningEnchantmentBaseDamage + lvl*ConfigurationHandler.lightningEnchantmentDamagePerLevel) );
				victim.hurtResistantTime = 0;
			}
		}
    }
    
}