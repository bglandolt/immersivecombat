package bettercombat.mod.handler;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.combat.IOffHandAttack;
import bettercombat.mod.enchantment.BetterCombatEnchantments;
import bettercombat.mod.enchantment.EnchantmentLightning;
import bettercombat.mod.util.BetterCombatAttributes;
import bettercombat.mod.util.BetterCombatPotions;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.ConfigurationHandler.SpartanBow;
import bettercombat.mod.util.Helpers;
import bettercombat.mod.util.Reference;
import bettercombat.mod.util.Sounds;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandlers
{
    public static final EventHandlers INSTANCE = new EventHandlers();

    public int offhandCooldown;

    @CapabilityInject(IOffHandAttack.class)
    public static final Capability<IOffHandAttack> OFFHAND_CAP = Helpers.getNull();
    
    @CapabilityInject(CapabilityOffhandCooldown.class)
    public static final Capability<CapabilityOffhandCooldown> TUTO_CAP = Helpers.getNull();

    private EventHandlers()
    {
    	
    }
	
    /*

    PlayerControllerMP
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity)
    {
        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketUseEntity(targetEntity));

        if (this.currentGameType != GameType.SPECTATOR)
        {
            playerIn.attackTargetEntityWithCurrentItem(targetEntity);
            playerIn.resetCooldown();
        }
    }
    
    */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void cancelAttackEntityEvent( AttackEntityEvent event)
    {
		event.setCanceled(true);
	}
    
	@SubscribeEvent
	public void projectileJoinWorld( EntityJoinWorldEvent event ) /* ArrowLooseEvent */
	{
		if ( event.getEntity() instanceof EntityArrow )
		{
//			try
//			{
//		    	Field field = ObfuscationReflectionHelper.findField(event.getEntity().getClass().getSuperclass(),"weapon");
//		    	ItemStack itemStack =  (ItemStack) field.get(event.getEntity());
//		    	itemStack.setCount(1);
//			}
//			catch ( Exception e )
//			{
//				
//			}
			
			EntityArrow arrow = ((EntityArrow)event.getEntity());
			
			if ( arrow.shootingEntity instanceof EntityPlayer )
			{
				EntityPlayer p = (EntityPlayer)arrow.shootingEntity;
				
				if ( p.isHandActive() )
				{
					ItemStack stack = p.getActiveHand() == EnumHand.MAIN_HAND ? p.getHeldItemMainhand() : p.getHeldItemOffhand();
					
					if ( !stack.isEmpty() )
			        {
			            NBTTagList nbttaglist = stack.getEnchantmentTagList();

			            for (int i = 0; i < nbttaglist.tagCount(); ++i)
			            {
			                int id = nbttaglist.getCompoundTagAt(i).getShort("id");
			                int lvl = nbttaglist.getCompoundTagAt(i).getShort("lvl");

			                if ( Enchantment.getEnchantmentID(BetterCombatEnchantments.LIGHTNING) == id )
			                {
			                	arrow.addTag("lightning~"+lvl);
			                	arrow.setGlowing(true);
			                }
			            }
			        }

				}
			}
		}
		else if (event.getEntity() instanceof EntityPlayer)
        {
		    EntityPlayer player = (EntityPlayer)event.getEntity();
       	    player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigurationHandler.baseAttackDamage);
       	    player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(ConfigurationHandler.baseReachDistance);
       	    player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(ConfigurationHandler.baseReachDistance);
        }
	}
	
	@SubscribeEvent
    public static void onEntityConstructing(final EntityEvent.EntityConstructing event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();
       	    player.getAttributeMap().registerAttribute(BetterCombatAttributes.CRIT_CHANCE).setBaseValue(ConfigurationHandler.randomCrits);
       	    player.getAttributeMap().registerAttribute(BetterCombatAttributes.CRIT_DAMAGE).setBaseValue(ConfigurationHandler.bonusCritDamage);
        }
    }
	
	@SubscribeEvent (priority = EventPriority.LOW, receiveCanceled = true)
	public void knockBack(LivingKnockBackEvent event)
	{
		if ( ConfigurationHandler.betterKnockback )
		{
			if ( !event.isCanceled() )
			{
				try
				{
					double strength = event.getStrength();
					double xRatio = event.getRatioX();
					double zRatio = event.getRatioZ();
					EntityLivingBase entityLivingBase = event.getEntityLiving();
					double knockbackResistance = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
					
			        if ( knockbackResistance < 1.0D )
			        {
			            double f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
			            strength = strength * ( 1.0D - knockbackResistance );
			            
			        	entityLivingBase.isAirBorne = true;
			        	
			            entityLivingBase.motionX = entityLivingBase.motionX / 2.0D * (1.0D + knockbackResistance);
			            entityLivingBase.motionZ = entityLivingBase.motionZ / 2.0D * (1.0D + knockbackResistance);
			            
			            entityLivingBase.motionX -= xRatio / f * strength;
			            entityLivingBase.motionZ -= zRatio / f * strength;
			
			            if (entityLivingBase.onGround)
			            {
			            	entityLivingBase.motionY = entityLivingBase.motionY / 2.0D * (1.0D + knockbackResistance);
			            	entityLivingBase.motionY += strength * ConfigurationHandler.knockUpStrength;
			
			                if (entityLivingBase.motionY > 0.4D)
			                {
			                	entityLivingBase.motionY = 0.4D;
			                }
			            }
			            entityLivingBase.velocityChanged = true;
			        }
					event.setCanceled(true);
				}
				catch (Exception e)
				{

				}
			}
		}
	}
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void potionAdded(PotionAddedEvent event)
    {	
    	if ( event.getEntityLiving() instanceof EntityCreature && ConfigurationHandler.nauseaAffectsMobs )
    	{
			EntityCreature el = (EntityCreature)(event.getEntityLiving());
							
			if ( el.height < 2.5F )
			{
				if ( el.isPotionActive(MobEffects.NAUSEA) || el.isPotionActive(MobEffects.BLINDNESS) )
				{
	                boolean flag = false;
	                
	                for ( EntityAITaskEntry task : el.tasks.taskEntries )
	                {
	                	if ( task.getClass().equals(EntityAINausea.class) )
	                	{
	                		flag = true;
	                	}
	                }
	                
	                if ( !flag )
	                {
	                	el.tasks.addTask(0, new EntityAINausea(el));
	                }
	                else if ( el.world.rand.nextBoolean() )
	                {
	                	el.setAttackTarget(null);
	                }
				}
			}
    	}
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void livingHurtLow(LivingHurtEvent event)
    {
    	if ( event.getEntityLiving() == null )
    	{
    		return;
    	}
        
        if ( event.getSource() == null )
        {
        	return;
        }
        
        if ( !(event.getSource().getTrueSource() instanceof EntityLivingBase) )
		{
            return;
		}
		
		/* Add a damage source to tipped arrows */
		if ( event.getSource().getTrueSource() == null && event.getSource().isMagicDamage() && event.getEntityLiving().getRevengeTarget() instanceof EntityPlayer )
	    {
	        event.getEntityLiving().hurtResistantTime = 0;
	        event.getEntityLiving().attackEntityFrom( DamageSource.causeIndirectMagicDamage(event.getEntityLiving().getRevengeTarget(), event.getEntityLiving().getRevengeTarget()), event.getAmount() );
	        event.setCanceled(true);
			return;
	    }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void livingHurtHigh(LivingHurtEvent event)
    {
    	if ( event.getEntityLiving() == null )
    	{
    		return;
    	}
        
        if ( event.getSource() == null )
        {
        	return;
        }
        
        if ( !(event.getSource().getTrueSource() instanceof EntityLivingBase) )
		{
            return;
		}
		
        EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
    	
        if ( attacker.isPotionActive(BetterCombatPotions.AETHEREALIZED) && !event.getSource().isMagicDamage() && !event.getSource().isFireDamage() && !event.getSource().isExplosion() )
	    {
        	PotionEffect potionEffect = attacker.getActivePotionEffect(BetterCombatPotions.AETHEREALIZED);
        	
        	if ( potionEffect != null )
        	{
	        	float magicDamage = event.getAmount()*potionEffect.getAmplifier()*ConfigurationHandler.alchemizedAmplifier;
		        event.setAmount(event.getAmount()-magicDamage);
		        event.getEntityLiving().hurtResistantTime = 0;
		        event.getEntityLiving().attackEntityFrom(DamageSource.causeIndirectMagicDamage(event.getEntityLiving().getRevengeTarget(), event.getEntityLiving().getRevengeTarget()), magicDamage);
		        playAetherealizedEffect(event.getEntityLiving(), potionEffect.getAmplifier());
        	}
	    }
        
        if ( event.getSource().damageType.equals("arrow") )
        {
        	event.getSource().setProjectile();
        }
        
		if ( ConfigurationHandler.silverArmorDamagesUndeadAttackers > 0.0F )
		{
			if ( attacker.isEntityUndead() && !(event.getEntityLiving().isEntityUndead()) )
			{
				int armorPieces = 0;
				for ( ItemStack piece : event.getEntityLiving().getEquipmentAndArmor() )
				{
					if ( !piece.isEmpty() && piece.getItem().getRegistryName().toString().contains("silver_metal") )
					{
						armorPieces++;
					}
				}
				if ( armorPieces > 0 )
				{
					attacker.hurtResistantTime = 0;
			        attacker.attackEntityFrom(DamageSource.causeIndirectMagicDamage(event.getEntityLiving(), event.getEntityLiving()), ConfigurationHandler.silverArmorDamagesUndeadAttackers*armorPieces);
					playSilverArmorEffect(attacker);
				}
			}
		}
    }

    @Nullable
    private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange)
    {
        BlockPos blockpos = new BlockPos(entityIn);
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
        BlockPos blockpos1 = null;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = i - horizontalRange; l <= i + horizontalRange; ++l)
        {
            for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1)
            {
                for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1)
                {
                    blockpos$mutableblockpos.setPos(l, i1, j1);
                    IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);

                    if (iblockstate.getMaterial() == Material.WATER)
                    {
                        float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));

                        if (f1 < f)
                        {
                            f = f1;
                            blockpos1 = new BlockPos(blockpos$mutableblockpos);
                        }
                    }
                }
            }
        }

        return blockpos1;
    }
    


    @SubscribeEvent(receiveCanceled = true)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
//        ISecondHurtTimer sht = event.getEntityLiving().getCapability(SECONDHURTTIMER_CAP, null);
//        
//        if ( sht != null && sht.getHurtTimerBCM() > 0 )
//        {
//            sht.tick();
//        }

        if ( event.getEntityLiving() instanceof EntityPlayer )
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IOffHandAttack oha = event.getEntityLiving().getCapability(OFFHAND_CAP, null);
            CapabilityOffhandCooldown cof = player.getCapability(TUTO_CAP, null);
            Helpers.execNullable(oha, IOffHandAttack::tick);

            if ( cof != null )
            {
                cof.tick();
                if ( this.offhandCooldown > 0 )
                {
                    cof.setOffhandCooldown(this.offhandCooldown);
                    if ( !player.world.isRemote )
                    {
                        cof.sync();
                    }
                    this.offhandCooldown = 0;
                }
            }
/*
          if( this.giveEnergy )
          {
              if( player.ticksSinceLastSwing == 0 )
            	{
                  player.ticksSinceLastSwing = this.energyToGive;
                  this.giveEnergy = false;
                  PacketHandler.instance.sendToServer(new PacketSendEnergy(this.energyToGive));
              }
          }
*/
        }
    }
    
    @SuppressWarnings("rawtypes")
	@SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent event)
    {
        if ( event.getGenericType() != Entity.class )
        {
            return;
        }
        
        if ( event.getObject() instanceof EntityPlayer )
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "TUTO_CAP"), new CapabilityOffhandCooldown((EntityPlayer) event.getObject()));
        }

        event.addCapability(new ResourceLocation(Reference.MOD_ID, "IOffHandAttack"), new ICapabilitySerializable()
        {
            IOffHandAttack inst = EventHandlers.OFFHAND_CAP.getDefaultInstance();

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return capability == EventHandlers.OFFHAND_CAP;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return capability == EventHandlers.OFFHAND_CAP ? EventHandlers.OFFHAND_CAP.cast(this.inst) : null;
            }

            @Override
            public NBTPrimitive serializeNBT() {
                return (NBTPrimitive) EventHandlers.OFFHAND_CAP.getStorage().writeNBT(EventHandlers.OFFHAND_CAP, this.inst, null);
            }

            @Override
            public void deserializeNBT(NBTBase nbt) {
                EventHandlers.OFFHAND_CAP.getStorage().readNBT(EventHandlers.OFFHAND_CAP, this.inst, null, nbt);
            }
        });

//        event.addCapability(new ResourceLocation(Reference.MOD_ID, "ISecondHurtTimer"), new ICapabilitySerializable()
//        {
//            ISecondHurtTimer inst = EventHandlers.SECONDHURTTIMER_CAP.getDefaultInstance();
//
//            @Override
//            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
//                return capability == EventHandlers.SECONDHURTTIMER_CAP;
//            }
//
//            @Override
//            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
//                return capability == EventHandlers.SECONDHURTTIMER_CAP ? EventHandlers.SECONDHURTTIMER_CAP.cast(this.inst) : null;
//            }
//
//            @Override
//            public NBTPrimitive serializeNBT() {
//                return (NBTPrimitive) EventHandlers.SECONDHURTTIMER_CAP.getStorage().writeNBT(EventHandlers.SECONDHURTTIMER_CAP, this.inst, null);
//            }
//
//            @Override
//            public void deserializeNBT(NBTBase nbt) {
//                EventHandlers.SECONDHURTTIMER_CAP.getStorage().readNBT(EventHandlers.SECONDHURTTIMER_CAP, this.inst, null, nbt);
//            }
//        });
    }
    
    @SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		World world = event.getWorld();
		
		if ( world == null || entity == null )
		{
			return;
		}
		
		if ( entity instanceof EntityArrow )
		{
			EntityArrow arrow = (EntityArrow)entity;
			
			if ( arrow.shootingEntity instanceof EntityLivingBase )
			{
				EntityLivingBase p = (EntityLivingBase) arrow.shootingEntity;
				try
				{
					Item item = p.getHeldItem(p.getActiveHand()).getItem();
					
					/* BOWS */
					if ( item instanceof ItemBow )
					{
						String name = item.getRegistryName().toString();

						for ( SpartanBow spartanBow : ConfigurationHandler.bows )
						{
							if ( name.equals(spartanBow.bow) )
							{
								if ( spartanBow.velocity != 1.0D )
								{
									arrow.motionX*=spartanBow.velocity;
									arrow.motionY*=spartanBow.velocity;
									arrow.motionZ*=spartanBow.velocity;
									arrow.velocityChanged = true;
								}
								
								if ( spartanBow.damage != 1.0D )
								{
									double damage = arrow.getDamage()*spartanBow.damage;
									if ( damage < 0.0D )
									{
										damage = 0.0D;
									}
									arrow.setDamage(damage);
								}
								
								if ( name.contains("silver") )
								{
									arrow.addTag("silver");
								}
								
								return;
							}
						}
					}
					
					/* CROSSBOWS */
					else if ( item.getClass().getSimpleName().equals("ItemCrossbow") )
					{
						String name = item.getRegistryName().toString();

						for ( SpartanBow spartanBow : ConfigurationHandler.bows )
						{
							if ( name.equals(spartanBow.bow) )
							{
								if ( spartanBow.velocity != 1.0D )
								{
									arrow.motionX*=spartanBow.velocity;
									arrow.motionY*=spartanBow.velocity;
									arrow.motionZ*=spartanBow.velocity;
									arrow.velocityChanged = true;
								}
								
								try
								{
							    	Field field = ObfuscationReflectionHelper.findField(event.getEntity().getClass(),"baseDamage");
							    	
							    	if ( field == null )
							    	{
								    	field = ObfuscationReflectionHelper.findField(event.getEntity().getClass().getSuperclass(),"baseDamage");
							    	}
							    	
							    	Float baseDamage = (Float)field.get(event.getEntity());
							    	
							    	if ( spartanBow.damage != 1.0D )
							    	{
								    	baseDamage = (float)(baseDamage * spartanBow.damage);
							    	}
							    }
								catch ( Exception e )
								{
									
								}
								
								if ( name.contains("silver") )
								{
									arrow.addTag("silver");
								}
								
								return;
							}
						}
					}
				}
				catch ( Exception e )
				{
					
				}
			}
		}
	}

    @SubscribeEvent(priority = EventPriority.LOWEST)
   	public void arrowImpact(ProjectileImpactEvent event)
   	{
		if ( event == null )
   		{
   			return;
   		}
		
		/* NO FIRE WHEN BLOCKING PROJECTILES */
		if ( ConfigurationHandler.blockFireProjectiles && event.getEntity() != null && event.getEntity().isBurning() && event.getRayTraceResult().entityHit instanceof EntityLivingBase )
		{
			EntityLivingBase victim = (EntityLivingBase)(event.getRayTraceResult().entityHit);

			if ( victim.isActiveItemStackBlocking() )
	        {
	            Vec3d vec3d = event.getEntity().getPositionVector();

            	if (vec3d != null)
                {
                    Vec3d vec3d1 = victim.getLook(1.0F);
                    Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(victim.posX, victim.posY, victim.posZ)).normalize();
                    vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);

                    if (vec3d2.dotProduct(vec3d1) < 0.0D)
                    {
                        event.getEntity().extinguish();
                    }
                }
	        }
		}
		
   		if ( event.getEntity() instanceof EntityArrow ) 
   		{
			EntityArrow arrow = (EntityArrow)(event.getEntity());
			
	   		if ( event.getRayTraceResult().entityHit instanceof EntityLivingBase )
	   		{
  				EntityLivingBase victim = (EntityLivingBase)(event.getRayTraceResult().entityHit);
  				
   	    		for ( String t : arrow.getTags() )
   	    		{
   	    			if ( t.equals("silver") )
   	    			{
   	    				if ( ConfigurationHandler.rangedSilverDamageMultiplier != 1.0F && victim.isEntityUndead() )
   	    				{
	   	   	    			arrow.setDamage(arrow.getDamage()*ConfigurationHandler.rangedSilverDamageMultiplier);
	   						this.playSilverArrowEffect(arrow);
   	    				}
   	    			}
   	    			else if ( t.contains("lightning~") )
   	    			{
   	    				try
   	    				{
	   	    				Integer i = Integer.valueOf(t.substring(10)); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
	   	    				EnchantmentLightning.doLightning(arrow.shootingEntity, victim, i);
	   	    				arrow.setDead();
   	    				}
   	    				catch ( Exception error )
   	    				{
   	    					
   	    				}
   	    			}
   	    			else if ( t.contains("instant_harming~") )
   	    			{
                        try
   	    				{
	   	    				Integer i = Integer.valueOf(t.substring(16)); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
	   	    				
	   	    				if ( victim.isEntityUndead() )
	   	    		        {
	   	    					if ( ConfigurationHandler.healingArrowNoDamage )
	   	    					{
   	    		                    arrow.setDamage(0.0D);
   	    		                }
	   	    					victim.heal(i*3.5F);
	   	    					playHealEffect(victim);
	   	    		        }
	   	    		        else
	   	    		        {
	   	    		        	victim.hurtResistantTime = 0;
    		                    if ( arrow.shootingEntity == null )
    		                    {
    		                    	victim.attackEntityFrom(DamageSource.MAGIC, i*4.5F);
    		                    }
    		                    else
    		                    {
   		    		                victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(arrow.shootingEntity, arrow.shootingEntity), i*4.5F);
    		                    }
	   	    		        }
   	    				}
   	    				catch ( Exception error )
   	    				{
   	    					
   	    				}
   	    			}
   	    			else if ( t.contains("instant_healing~") )
   	    			{
   	    				try
   	    				{
	   	    				Integer i = Integer.valueOf(t.substring(16)); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
	   	    				
	   	    				if ( victim.isEntityUndead() )
	   	    		        {
	   	    		        	victim.hurtResistantTime = 0;
    		                    if ( arrow.shootingEntity == null )
    		                    {
    		                    	victim.attackEntityFrom(DamageSource.MAGIC, i*4.5F);
    		                    }
    		                    else
    		                    {
   		    		                victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(arrow.shootingEntity, arrow.shootingEntity), i*4.5F);
    		                    }
	   	    		        }
	   	    		        else
	   	    		        {
	   	    		        	if ( ConfigurationHandler.healingArrowNoDamage )
	   	    					{
   	    		                    arrow.setDamage(0.0D);
   	    		                }
	   	    					victim.heal(i*3.5F);
	   	    					playHealEffect(victim);
	   	    		        }
   	    				}
   	    				catch ( Exception error )
   	    				{
   	    					
   	    				}
   	    			}
   	    		}
   	    		
   	    		/* TIPPED ARROWS */
   	    		if ( arrow instanceof EntityTippedArrow && ConfigurationHandler.tippedArrowFix )
   				{
   					EntityTippedArrow tippedArrow = (EntityTippedArrow) arrow;
   					try
   					{
   						Field field = ObfuscationReflectionHelper.findField(tippedArrow.getClass(),"potion");
   				    	PotionType potion =  (PotionType) field.get(tippedArrow);
   				    	   	                    
   	                    if ( potion == PotionTypes.HEALING || potion == PotionTypes.STRONG_HEALING )
   	                    {
   	                    	try
   	   	    				{
   		   	    				Integer i = Integer.valueOf(potion.getEffects().get(0).getAmplifier()); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
   	   	                    	field.set(tippedArrow, PotionTypes.EMPTY);
   		   	    				if ( victim.isEntityUndead() )
   		   	    		        {
   		   	    		        	victim.hurtResistantTime = 0;
   	    		                    if ( arrow.shootingEntity == null )
   	    		                    {
   	    		                    	victim.attackEntityFrom(DamageSource.MAGIC, i*4.5F);
   	    		                    }
   	    		                    else
   	    		                    {
   	   		    		                victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(arrow.shootingEntity, arrow.shootingEntity), i*4.5F);
   	    		                    }
   		   	    		        }
   		   	    		        else
   		   	    		        {
   		   	    		        	if ( ConfigurationHandler.healingArrowNoDamage )
   		   	    					{
   	   	    		                    arrow.setDamage(0.0D);
   	   	    		                }
   		   	    					victim.heal(i*3.5F);
   		   	    					playHealEffect(victim);
   		   	    		        }
   	   	    				}
   	   	    				catch ( Exception error )
   	   	    				{
   	   	    					
   	   	    				}
   	                    }
   	                    else if ( potion == PotionTypes.HARMING || potion == PotionTypes.STRONG_HARMING )
   	                    {
   	                    	try
   	   	    				{
   		   	    				Integer i = Integer.valueOf(potion.getEffects().get(0).getAmplifier()); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
   	   	                    	field.set(tippedArrow, PotionTypes.EMPTY);
   		   	    				// System.out.println("iiiiiiiiii" + i);
   		   	    				if ( victim.isEntityUndead() )
   		   	    		        {
   		   	    		        	victim.hurtResistantTime = 0;
   	    		                    if ( arrow.shootingEntity == null )
   	    		                    {
   	    		                    	victim.attackEntityFrom(DamageSource.MAGIC, i*4.5F);
   	    		                    }
   	    		                    else
   	    		                    {
   	   		    		                victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(arrow.shootingEntity, arrow.shootingEntity), i*4.5F);
   	    		                    }
   		   	    		        }
   		   	    		        else
   		   	    		        {
   		   	    		        	if ( ConfigurationHandler.healingArrowNoDamage )
   		   	    					{
   	   	    		                    arrow.setDamage(0.0D);
   	   	    		                }
   		   	    					victim.heal(i*3.5F);
   		   	    					playHealEffect(victim);
   		   	    		        }
   	   	    				}
   	   	    				catch ( Exception error )
   	   	    				{
   	   	    					
   	   	    				}
   	                    }
   	                    else if ( potion == PotionTypes.REGENERATION || potion == PotionTypes.LONG_REGENERATION || potion == PotionTypes.STRONG_REGENERATION )
	                    {
	                    	try
	   	    				{
		   	    				if ( victim.isEntityUndead() )
		   	    		        {
		   	    					
		   	    		        }
		   	    		        else
		   	    		        {
		   	    		        	if ( ConfigurationHandler.healingArrowNoDamage )
		   	    					{
	   	    		                    arrow.setDamage(0.0D);
	   	    		                }
		   	    		        }
	   	    				}
	   	    				catch ( Exception error )
	   	    				{
	   	    					
	   	    				}
	                    }
   					}
   					catch ( Exception e )
   					{
   						
   					}
   				}
   	    		
   	   	    	if ( ConfigurationHandler.dragonboneBowWitherDamage > 0.0F )
   	   			{
   	   				if ( arrow.getClass().getSimpleName().equals("EntityDragonArrow") )
   	   				{
   						victim.hurtResistantTime = 0;
   						victim.attackEntityFrom(DamageSource.WITHER, ConfigurationHandler.dragonboneBowWitherDamage);
	   					this.playDragonEffect(arrow);
	   					arrow.setDead();
   	   				}
   	   			}
   				
   	   	    	if ( ConfigurationHandler.playArrowHitSound )
   	   	    	{
	   				if ( arrow.shootingEntity instanceof EntityPlayer )
	   				{
		   				Entity player = (EntityPlayer)arrow.shootingEntity;
		   				
		   				if ( arrow.getClass().toString().equals("EntityBoomerang") && player != victim )
		   				{
		   					victim.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.CRITICAL_STRIKE, player.getSoundCategory(), ConfigurationHandler.bowThudSoundVolume, 0.9F + player.world.rand.nextFloat()*0.2F);
		   				}
		   				else
		   				{
			   				if ( arrow.getIsCritical() )
		   					{
		   						player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.CRITICAL_STRIKE, player.getSoundCategory(), ConfigurationHandler.bowThudSoundVolume, 0.9F + player.world.rand.nextFloat()*0.2F);
		   					}
		   					player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_SLASH, player.getSoundCategory(), ConfigurationHandler.bowStrikeSoundVolume, 0.9F + player.world.rand.nextFloat()*0.2F);
		   				}
		   			}
   	   	    	}
   			}
	   		else
	   		{
	   			for ( String t : arrow.getTags() )
   	    		{
	   				if ( t.contains("lightning~") )
	   		    	{
	   					try
	   					{
		   					Integer i = Integer.valueOf(t.substring(10)); // Integer i = Integer.valueOf(t.substring(t.indexOf('~')+1));
		   					EnchantmentLightning.doLightning(arrow.shootingEntity, arrow, i);
		   					arrow.setDead();
	   					}
	   					catch ( Exception error )
	   					{
	   						
	   					}
	   		    	}
   	    		}
	   		}
   		}
   	}
       
   public void playDragonEffect( Entity e )
   {
       if ( e.world instanceof WorldServer )
   	   {
   	       for ( int i = 12; i > 0; i-- )
   	       {
		       ((WorldServer)e.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, e.posX+e.world.rand.nextGaussian()*0.12D, e.posY+e.world.rand.nextGaussian()*0.18D, e.posZ+e.world.rand.nextGaussian()*0.12D, 1, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextDouble()*0.08D, new int[0]);
   		   }
   	       for ( int i = 4; i > 0; i-- )
	       {
		       ((WorldServer)e.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, e.posX+e.world.rand.nextGaussian()*0.12D, e.posY+e.world.rand.nextGaussian()*0.18D, e.posZ+e.world.rand.nextGaussian()*0.12D, 1, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextDouble()*0.08D, new int[0]);
		   }
   	   }
   }
   
   public void playSilverArrowEffect( Entity e )
   {
       if ( e.world instanceof WorldServer )
   	   {
   	       for ( int i = 12; i > 0; i-- )
   	       {
		       ((WorldServer)e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+e.world.rand.nextGaussian()*e.width, e.posY+e.world.rand.nextGaussian()*e.height, e.posZ+e.world.rand.nextGaussian()*e.width, 1, e.world.rand.nextGaussian()*0.01D, e.world.rand.nextGaussian()*0.01D, e.world.rand.nextGaussian()*0.01D, e.world.rand.nextDouble()*0.02D, new int[0]);
   	       }
   	   }
   }
   
   public static void playSilverArmorEffect( Entity e )
   {
       if ( e.world instanceof WorldServer )
   	   {
   	       for ( int i = 12; i > 0; i-- )
   	       {
		       ((WorldServer)e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+e.world.rand.nextGaussian()*e.width, e.posY+e.world.rand.nextGaussian()*e.height, e.posZ+e.world.rand.nextGaussian()*e.width, 1, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextGaussian()*0.06D, e.world.rand.nextDouble()*0.06D, new int[0]);
   	       }
   	   }
   }
   
   public static void playHealEffect( Entity e )
   {
       if ( e.world instanceof WorldServer )
   	   {
    	   double d0 = e.world.rand.nextGaussian() * 0.02D;
    	   double d1 = e.world.rand.nextGaussian() * 0.02D;
    	   double d2 = e.world.rand.nextGaussian() * 0.02D;
    	   ((WorldServer)e.world).spawnParticle(EnumParticleTypes.HEART, e.posX + (double)(e.world.rand.nextFloat() * e.width * 2.0F) - (double)e.width, e.posY + 0.5D + (double)(e.world.rand.nextFloat() * e.height), e.posZ + (double)(e.world.rand.nextFloat() * e.width * 2.0F) - (double)e.width, d0, d1, d2);
   	   }
   }

   public static void playAetherealizedEffect( Entity e, int amount )
   {
	   if ( ConfigurationHandler.aetherealizedDamageParticles && e.world instanceof WorldServer )
	   {
		   double h = e.height/2.0;
           if ( amount > 1 )
           {
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX,		 e.posY + h, e.posZ+1.5, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+0.75,	 e.posY + h, e.posZ+1.28, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+1.28,	 e.posY + h, e.posZ+0.75, amount, 0.0D, 0.0D, 0.01D, 0.0D);            ((WorldServer) e.world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, e.posX+1.4,	 e.posY + h, e.posZ+1.4, amount, 0.0D, 0.0D, 0.0D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+1.5,	 e.posY + h, e.posZ, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+1.28,	 e.posY + h, e.posZ-0.75, amount,0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX+0.75,	 e.posY + h, e.posZ-1.28, amount, 0.0D, 0.0D, 0.01D, 0.0D);            ((WorldServer) e.world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, e.posX+1.4,	 e.posY + h, e.posZ-1.4, amount, 0.0D, 0.0D, 0.0D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX,		 e.posY + h, e.posZ-1.5, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX-0.75,	 e.posY + h, e.posZ-1.28, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX-1.28,	 e.posY + h, e.posZ-0.75, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX-1.5,	 e.posY + h, e.posZ, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX-1.28,	 e.posY + h, e.posZ+0.75, amount, 0.0D, 0.0D, 0.01D, 0.0D);
               ((WorldServer) e.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, e.posX-0.75,  e.posY + h, e.posZ+1.28, amount, 0.0D, 0.0D, 0.01D, 0.0D);
           }
       }
   }
       
}