package bettercombat.mod.client.handler;

import java.util.List;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.ClientProxy;
import bettercombat.mod.client.gui.GuiCrosshairsBC;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketFastEquip;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.network.PacketMainhandAttack;
import bettercombat.mod.network.PacketOffhandAttack;
import bettercombat.mod.network.PacketShieldBash;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.ConfigurationHandler.CustomWeapon;
import bettercombat.mod.util.ConfigurationHandler.SpartanShield;
import bettercombat.mod.util.Helpers;
import bettercombat.mod.util.Reference;
import bettercombat.mod.util.Sounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MOD_ID)
public class EventHandlersClient
{
    private EventHandlersClient() {}

    public static final EventHandlersClient INSTANCE = new EventHandlersClient();

    private final GuiCrosshairsBC gc = new GuiCrosshairsBC();
    
    /* Countdown in ticks for when a mainhand or offhand attack is ready again */
    /* Used to prevent the player from right-clicking and left-clicking at the same time */
    public static int sameTimeSwing = 0;
    
    public static boolean canSwing()
    {
    	return sameTimeSwing < 1;
    }
    
    public static void startSwingTimer()
    {
    	sameTimeSwing = 3;
    }
    
//    public static int leftAttackQueued = 0;
//    public static int rightAttackQueued = 0;
    
    /* Countdown in ticks for when the offhand attack is ready */
    public static int offcd = 0;

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(KeyInputEvent event)
    {
        if ( ClientProxy.fastEquip.isPressed() && canSwing() ) 
        {
        	startSwingTimer();
        	PacketHandler.instance.sendToServer(new PacketFastEquip());
        }
    }
    
    /* Cancels input when the attack binds are not mouse clicks, and sets them back to default */
//  @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
//	public void onKeyInputEvent(InputEvent event)
//	{
//		if ( Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed() )
//		{
//    		if ( onMouseLeftClick() )
//    		{
//    			event.setResult(Result.DENY);
//    		}
//			Minecraft.getMinecraft().gameSettings.keyBindAttack.setToDefault();
//		}
//      
//		if ( Minecraft.getMinecraft().gameSettings.keyBindUseItem.isPressed() )
//		{
//			if ( onMouseRightClick() )
//    		{
//    			event.setResult(Result.DENY);
//    		}
//			Minecraft.getMinecraft().gameSettings.keyBindUseItem.setToDefault();
//		}
//	}
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseEvent(MouseEvent event)
    {
		KeyBinding rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem; /* -1 */
		
		if ( event.isButtonstate() && event.getButton() == rightClick.getKeyCode() + 100 )
		{
			if ( onMouseRightClick() )
    		{
				event.setResult(Result.DENY);
    			event.setCanceled(true);
    		}
		}
		
		KeyBinding leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack; /* 0 */
		
    	if ( event.isButtonstate() && event.getButton() == leftClick.getKeyCode() + 100 )
		{
    		if ( onMouseLeftClick() )
    		{
    			event.setCanceled(true);
    		}
		}
    }
		
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void aonItemTooltip(ItemTooltipEvent event)
    {
    	for ( CustomWeapon s : ConfigurationHandler.weapons )
    	{
    		if ( event.getItemStack().getItem().getRegistryName().toString().contains(s.name) )
    		{
    			event.getToolTip().add( I18n.format("bettercombatmod.info.critChance.color", new Object[0]) + ((int)((ConfigurationHandler.randomCrits+s.critChanceMod)*100)) + "%" + I18n.format("bettercombatmod.info.critChance.text", new Object[0]) );
				event.getToolTip().add( "" );
    			
    			if ( ConfigurationHandler.addSpartanWeaponryTags )
    			{
	    			/* ====================================================================================== */
	    			/* TWO-HANDED */
	    			boolean hasTwoHandedTag = false;
	    			String twoHandedString = I18n.format("tooltip.spartanweaponry:two_handed", new Object[0]);
	    			
	    			try
	    			{
	    				twoHandedString = twoHandedString.substring(twoHandedString.indexOf(':')+1, twoHandedString.indexOf('%')-1);
	    			}
	    			catch (Exception e)
	    			{
	    				
	    			}
	    			
	    			/* REACH */
	    			boolean hasReachTag = false;
	    			String reachString = I18n.format("tooltip.spartanweaponry:reach", new Object[0]);
	    			
	    			try
	    			{
	    				reachString = reachString.substring(reachString.indexOf(':')+1, reachString.indexOf('%')-1);
	    			}
	    			catch (Exception e)
	    			{
	    				
	    			}
	    			
	    			/* SWEEP */
	    			boolean hasSweepTag = false;
	    			String sweepString = I18n.format("tooltip.spartanweaponry:sweep_damage", new Object[0]);
	    			
	    			try
	    			{
	    				sweepString = sweepString.substring(sweepString.indexOf(':')+1, sweepString.indexOf('%')-1);
	    			}
	    			catch (Exception e)
	    			{
	    				
	    			}
	    			/* ====================================================================================== */
	    			for ( String tag : event.getToolTip() )
	    	    	{
	    				if ( tag.contains(twoHandedString) )
	    				{
	    					hasTwoHandedTag = true;
	    				}
	    				else if ( tag.contains(reachString) )
	    				{
	    					hasReachTag = true;
	    				}
	    				else if ( tag.contains(sweepString) )
	    				{
	    					hasSweepTag = true;
	    				}
	    	    	}
	    			
	    			if ( s.fatigueMod >= 2 && !hasTwoHandedTag )
	    			{
	    				event.getToolTip().add( I18n.format("bettercombatmod.info.fatigue.color", new Object[0]) + I18n.format("bettercombatmod.info.fatigue.text", new Object[0]) + Helpers.integerToRoman(s.fatigueMod-1) );
	    			}

	    			if ( s.reachMod >= 1 && !hasReachTag )
	    			{
	    				event.getToolTip().add( I18n.format("bettercombatmod.info.reach.color", new Object[0]) + I18n.format("bettercombatmod.info.reach.text", new Object[0]) + Helpers.integerToRoman((int)Math.round(s.reachMod)));
	    			}
	    			
	    			if ( s.sweepMod >= 1 && !hasSweepTag)
	    			{
	    				event.getToolTip().add( I18n.format("bettercombatmod.info.sweep.color", new Object[0]) + I18n.format("bettercombatmod.info.sweep.text", new Object[0]) + Helpers.integerToRoman(s.sweepMod-1));
	    			}
    			}
    			
    			if ( s.critDamageMod > 0 )
    			{
    				event.getToolTip().add( I18n.format("bettercombatmod.info.critDamage.color", new Object[0]) + "+" + (int)(s.critDamageMod*100) + "%" + I18n.format("bettercombatmod.info.critDamage.text", new Object[0]) );
    			}
    			
        		return;
    		}
    	}
    	
    	try
    	{
	    	if ( ConfigurationHandler.randomCrits > 0.0F && ConfigurationHandler.isItemAttackUsable(event.getItemStack().getItem()) )
	    	{
				event.getToolTip().add( I18n.format("bettercombatmod.info.critChance.color", new Object[0]) + ((int)((ConfigurationHandler.randomCrits)*100)) + "%" + I18n.format("bettercombatmod.info.critChance.text", new Object[0]) );
				event.getToolTip().add( "" );
				return;
	    	}
    	}
    	catch ( Exception e )
    	{
    		
    	}
    }
    
										    public static boolean onMouseLeftClick()
										    {
										    	/* LEFT CLICK - MAIN ATTACK */
										        Minecraft mc = Minecraft.getMinecraft();
										        EntityPlayer player = mc.player;
										        
										        if ( player == null || player.isSpectator() || !player.isEntityAlive() || player.getHealth() <= 0.0F )
										        {
										        	return true;
										        }
										        
										        final ItemStack mh = player.getHeldItemMainhand();
										        final ItemStack oh = player.getHeldItemOffhand();
										        
										        double reach = ConfigurationHandler.baseReachDistance;
										        
										        /* If the player has active item */
										        if ( !player.getActiveItemStack().isEmpty() )
										        {
										        	/* Shield bashing */
										        	if ( player.isActiveItemStackBlocking() )
										        	{
										        		EntityPlayerSP entityPlayerSP = mc.player;
										        		ItemStack shieldStack = ItemStack.EMPTY;
										        		EnumHand shieldHand = null;
										        		
										        	    if ( mh.getItem() instanceof ItemShield )
										        	    {
										        	        shieldStack = mh;
										        	        shieldHand = EnumHand.MAIN_HAND;
										        	    }
										        	    else if ( oh.getItem() instanceof ItemShield )
										        	    {
										        	        shieldStack = oh;
										        	        shieldHand = EnumHand.OFF_HAND;
										        	    }
										        	    
										        	    if ( shieldStack.isEmpty() || shieldHand == null || entityPlayerSP.getCooldownTracker().hasCooldown(shieldStack.getItem()) )
										        	    {
										        	    	return true;
										        	    }
										        	    
												    	String shieldString = player.getHeldItem(shieldHand).getItem().getRegistryName().toString();
										
												        RayTraceResult result = getMouseOverExtended(reach);
												        
												        if ( result != null )
										        	    {
										        	        int entId = -1;
										        	        boolean attackEntity = true;
										        	        
										        	        if ( result.entityHit != null && result.entityHit != entityPlayerSP )
										        	        {
										        	        	entId = result.entityHit.getEntityId();
										        	        }
										        	        
										        	        if ( entId == -1 )
										        	        {
										        	        	entId = 0;
										        	        	attackEntity = false;
										        	        }
										        	        
										        	        entityPlayerSP.swingArm(shieldHand);
										        	        
										        	        /* short = 32767 (inclusive) */
										        	        /* max dmg 326 */
										        	        
												        	int damage = 100;
												        	int knockback = 100;
												        	int cooldown = 30;
												        															        	
												     		for ( SpartanShield shield : ConfigurationHandler.shields )
												     		{
												     			if ( shieldString.equals(shield.shield) )
												     			{
												     				damage = (int)(shield.damage*100);
												     				knockback = (int)(shield.knockback*100);
												     				cooldown = shield.cooldown;
												     				break;
												     			}
												     		}
												     		
											                if ( shouldAttack(result.entityHit, player) )
											                {
											                	PacketHandler.instance.sendToServer(new PacketShieldBash(shieldHand, entId, attackEntity, damage, knockback, cooldown));
											                }
											                
											                startSwingTimer();
										        	    }
										        	}
										        	return true;
										        }
										        
										    	int fatigue = 0;
										    	double multiply_base = 1.0D;
										        double multiply = 1.0D;
										        
										        // player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
										
										        for ( AttributeModifier attribute : player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getModifiers() )
												{
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
										        
										        // player.setHeldItem(EnumHand.OFF_HAND, oh);
										    	
										    	/* MAIN_HAND fatigue */
										    	String mainString = mh.getItem().getRegistryName().toString();
										    	
										 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
										 		{
										 			if ( mainString.contains(weapon.name) )
										 			{
										 				fatigue = weapon.fatigueMod;
										 				reach += weapon.reachMod;
										 				break;
										 			}
										 		}
										 		
										        reach *= multiply_base * multiply;
										        												    	
										    	/* OFF_HAND fatigue */
										    	if ( oh.isEmpty() )
										    	{
											    	fatigue = 0;
										    	}
										    	else
										    	{
										    		String offString = oh.getItem().getRegistryName().toString();
										    		
										     		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
										     		{
										     			if ( offString.contains(weapon.name) )
										     			{
										     				fatigue += weapon.fatigueMod;
										     				break;
										     			}
										     		}
										        }
										    	
										    	boolean configWeapon = ConfigurationHandler.isItemAttackUsable(mh.getItem());
										    	boolean attackReady = !(!canSwing() || player.getCooledAttackStrength(-ConfigurationHandler.addedSwingTickCooldown) < 1.0F || ( offcd - 3 <= Helpers.execNullable(player.getCapability(EventHandlers.TUTO_CAP, null), CapabilityOffhandCooldown::getOffhandCooldown, 1) && offcd > 3 ));
										        
										    	RayTraceResult mov = getMouseOverExtended(reach);
										        
										        /* NULL */
										        if ( mov == null )
										        {
										        	// mainHandMissSwing(player);
										        	return true;
										        }
										        else if ( mov.entityHit == null )
										        {
										        	/* BLOCK */
											        if ( mov.typeOfHit == RayTraceResult.Type.BLOCK )
											    	{
												        BlockPos pos = mov.getBlockPos();
												        
														if ( pos != null )
														{
															Block block = player.world.getBlockState(pos).getBlock();
															
															if ( block instanceof BlockAir )
															{
																/* MISS! */
															}
															else
															{
																/* GRASS! */
																if ( block instanceof IShearable && !(mh.getItem() instanceof ItemHoe) )
																{
																	if ( configWeapon )
																	{
																		if ( attackReady )
																		{
																			try
																			{
																				if ( player.world.getBlockState(pos.up()).getBlock() instanceof IShearable )
																	        	{
																		        	player.world.destroyBlock(pos.up(), true);
																		        	player.world.setBlockToAir(pos.up());
																		        	FMLClientHandler.instance().getServer().getEntityWorld().setBlockToAir(pos.up());
																		        	FMLClientHandler.instance().getServer().getEntityWorld().destroyBlock(pos.up(), true);
																	        	}
																	        	player.world.destroyBlock(pos, true);
																	        	player.world.setBlockToAir(pos);
																	        	FMLClientHandler.instance().getServer().getEntityWorld().setBlockToAir(pos);
																	        	FMLClientHandler.instance().getServer().getEntityWorld().destroyBlock(pos, true);

																	        	spawnSweepHit(player, pos.getX(), pos.getZ());
																		        mov = getMouseOverExtended(reach);
																			}
																			catch ( Exception e )
																			{
																				return true;
																			}
																		}
																		else
																		{
																			return true;
																		}
																	}
																	else
																	{
															        	// mainHandMissSwing(player);
																		return false;
																	}
																}
																else
																{
																	/* MINING! */
														        	// mainHandMissSwing(player);
																	return false;
																}
															}
														}
														else
														{
															mainHandMissSwing(player);
															return true;
														}
											    	}
											        else /* MISS */
											        {
												        if ( player.isRiding() && player.getRidingEntity() instanceof IMob )
												        {
												        	mov.entityHit = player.getRidingEntity();
												        }
												        else if ( player.isBeingRidden() )
												        {
													        for ( Entity passenger : player.getPassengers() )
													        {
													        	mov.entityHit = passenger;
													        	break;
													        }
												        }
											        }
										        }
										
										    	float volume = ConfigurationHandler.weaponSwingSoundVolume*(0.8F+player.world.rand.nextFloat()*0.4F);
										    	float pitch = mainSwingPitch(player);
										    	
										        offcd = Helpers.getOffhandCooldown(player, oh, mh);
										        
										        // Helpers.message("ocd   : " + offcd);

										        /* CONFIG WEAPON */
										        if ( configWeapon )
										        {
										        	if ( !attackReady )
													{
										        		return true;
													}
										        	else
										        	{
										        		if ( ConfigurationHandler.swingSoundsOnWeapons )
												        {
												        	boolean metal = true;
												        	
												        	for ( String s : ConfigurationHandler.nonMetalList )
												        	{
												        		if ( mainString.contains(s) )
												        		{
												        			metal = false;
												        			break;
												        		}
												        	}
												        	
												        	if ( metal && ( mh.getItem() instanceof ItemSword || mainString.contains("sword") ) )
												        	{
												        		swordSweepRightSounds(player, volume, pitch);
												        	}
												        	else
												        	{
												        		bluntSwingRightSounds(player, volume, pitch);
												        	}
												        }
										        	}
										        }
										    	/* FIST */
										        else
										        {
										        	if ( !attackReady )
													{
										        		return true;
													}
										        	else
										        	{
											        	if ( ConfigurationHandler.swingSoundsOnNonWeapons )
											        	{
											        		bluntSwingRightSounds(player, volume*0.7F, pitch);
											        	}
										        	}
										        	
										        }
										        
										        if ( mov.entityHit != null && mov.entityHit != player )
										        {
										            if ( shouldAttack(mov.entityHit, player) )
										            {
										            	PacketHandler.instance.sendToServer(new PacketMainhandAttack(fatigue, mov.entityHit.getEntityId()));
										            }
										            else
										            {
											        	PacketHandler.instance.sendToServer(new PacketMainhandAttack(fatigue));
										            }
										        }
										        else
										        {
										        	PacketHandler.instance.sendToServer(new PacketMainhandAttack(fatigue));
										        }
										        
										        player.setHeldItem(EnumHand.OFF_HAND, oh);
										        
										        /* Resets the players offhand animation so it doesn't instantly snap back into default position */
										        if ( player.swingingHand == EnumHand.OFF_HAND && (player.isSwingInProgress || player.swingProgressInt >= getArmSwingAnimationEnd(player) / 2 || player.swingProgressInt < 0) )
										        {
										        	Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress(EnumHand.OFF_HAND);
										        }
										        
										        player.resetCooldown();
										        player.swingProgressInt = -1;
										        player.isSwingInProgress = true;
										        player.swingingHand = EnumHand.MAIN_HAND;
									            if (player.world instanceof WorldServer) ((WorldServer)player.world).getEntityTracker().sendToTracking(player, new SPacketAnimation(player, 0));
										        
									            startSwingTimer();
										        
										        return true;
										    }

										    
										    
	private static void mainHandMissSwing(EntityPlayer player)
	{
		if ( ConfigurationHandler.swingSoundsOnWeapons && player.getCooledAttackStrength(-ConfigurationHandler.addedSwingTickCooldown) > 0.5F )
        {
        	float volume = ConfigurationHandler.weaponSwingSoundVolume*(0.8F+player.world.rand.nextFloat()*0.4F);
        	float pitch = mainSwingPitch(player);
    		bluntSwingRightSounds(player, volume*0.7F, pitch);
        }
		player.swingProgressInt = -1;
        player.isSwingInProgress = true;
        player.swingingHand = EnumHand.MAIN_HAND;
        player.resetCooldown();												
	}

	public static float mainSwingPitch(EntityPlayer player)
	{
		return (float)MathHelper.clamp(0.5D+6.5D/player.getCooldownPeriod(),0.8D,1.3D);
	}
	
	public static float offSwingPitch(EntityPlayer player, ItemStack oh, ItemStack mh)
	{
		return (float)MathHelper.clamp(0.5D+6.5D/offcd,0.8D,1.3D);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public static boolean onMouseRightClick()
    {
    	/* RIGHT CLICK - OFF ATTACK */

    	if ( !ConfigurationHandler.enableOffhandAttack )
    	{
    		return false;
    	}
    	
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if ( player == null || player.isSpectator() || !player.isEntityAlive() || player.getHealth() <= 0.0F )
        {
    		return false;
        }
        
        if ( player.isSneaking() && ConfigurationHandler.sneakingDisablesOffhand )
        {
    		return false;
        }
        
    	final ItemStack oh = player.getHeldItemOffhand();
        
        if ( oh.isEmpty() )
        {
    		return false;
        }
        
        if ( !player.getActiveItemStack().isEmpty() )
        {
    		return false;
        }

    	final ItemStack mh = player.getHeldItemMainhand();

        if ( mh.getItemUseAction() != EnumAction.NONE || oh.getItemUseAction() != EnumAction.NONE )
        {
        	/* Add a delay to blocking */
        	if ( !canSwing() && oh.getItemUseAction() == EnumAction.BLOCK )
        	{
        		return true;
        	}
    		return false;
        }
                
        if ( !ConfigurationHandler.isItemAttackUsable(oh.getItem()) )
        {
    		return false;
        }
        
        if ( !canSwing() || Helpers.execNullable(player.getCapability(EventHandlers.TUTO_CAP, null), CapabilityOffhandCooldown::getOffhandCooldown, 1) > 0 || player.getCooledAttackStrength(-ConfigurationHandler.addedSwingTickCooldown)*player.getCooldownPeriod() < 3 )
        {
    		return false;
        }

        offcd =  Helpers.getOffhandCooldown(player, oh, mh);

    	int fatigue = 0;
        double reach = 0;

		String offString = oh.getItem().getRegistryName().toString();
		
    	/* Get the fatigue & reach for having a weapon in the main hand */
 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
 		{
 			if ( offString.contains(weapon.name) )
 			{
 				fatigue = weapon.fatigueMod;
 				reach += weapon.reachMod;
 				break;
 			}
 		}

 		reach = Helpers.getOffhandReach(reach, player, oh, mh);
    	        
        // player.sendMessage( new TextComponentString( "reach: " + reach ) );

    	/* Get the fatigue for having a weapon in the main hand */
        if ( mh.isEmpty() )
    	{
	    	fatigue = 0;
    	}
        else
        {
	    	String mainString = mh.getItem().getRegistryName().toString();
	    	
	 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
	 		{
	 			if ( mainString.contains(weapon.name) )
	 			{
	 				fatigue += weapon.fatigueMod;
	 				break;
	 			}
	 		}
        }
    					
        RayTraceResult mov = getMouseOverExtended(reach);

        /* NULL */
        if ( mov == null )
        {
        	// return true;
        }
        else if ( mov.entityHit == null )
        {
        	/* BLOCK */
	        if ( mov.typeOfHit == RayTraceResult.Type.BLOCK )
	    	{
		        BlockPos pos = mov.getBlockPos();
		        
				if ( pos != null )
				{
					Block block = player.world.getBlockState(pos).getBlock();
					
					if ( block instanceof BlockAir )
					{
						/* MISS! */
					}
					else
					{
						if ( block instanceof IShearable && !(oh.getItem() instanceof ItemHoe) )
						{
							/* GRASS! */
							try
							{
								if ( player.world.getBlockState(pos.up()).getBlock() instanceof IShearable )
					        	{
						        	player.world.destroyBlock(pos.up(), true);
						        	player.world.setBlockToAir(pos.up());
						        	FMLClientHandler.instance().getServer().getEntityWorld().setBlockToAir(pos.up());
						        	FMLClientHandler.instance().getServer().getEntityWorld().destroyBlock(pos.up(), true);
					        	}
					        	player.world.destroyBlock(pos, true);
					        	player.world.setBlockToAir(pos);
					        	FMLClientHandler.instance().getServer().getEntityWorld().setBlockToAir(pos);
					        	FMLClientHandler.instance().getServer().getEntityWorld().destroyBlock(pos, true);
					        	spawnSweepHit(player, pos.getX(), pos.getZ());
						        mov = getMouseOverExtended(reach);
							}
							catch ( Exception e )
							{
								return false;
							}
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					// return true;
				}
	    	}
	        else /* MISS */
	        {
		        if ( player.isRiding() && player.getRidingEntity() instanceof IMob )
		        {
		        	mov.entityHit = player.getRidingEntity();
		        }
		        else if ( player.isBeingRidden() )
		        {
			        for ( Entity passenger : player.getPassengers() )
			        {
			        	mov.entityHit = passenger;
			        	break;
			        }
		        }
	        }
        }
    	
        startSwingTimer();
               		
        EventHandlers.INSTANCE.offhandCooldown = offcd;
    	
		if ( ConfigurationHandler.swingSoundsOnWeapons )
        {
        	float volume = ConfigurationHandler.weaponSwingSoundVolume*(0.8F+player.world.rand.nextFloat()*0.4F);
        	float pitch = offSwingPitch(player, oh, mh);

        	boolean metal = true;
        	for ( String s : ConfigurationHandler.nonMetalList )
        	{
        		if ( offString.contains(s) )
        		{
        			metal = false;
        			break;
        		}
        	}
        	
        	if ( metal && ( oh.getItem() instanceof ItemSword || offString.contains("sword") ) )
        	{
        		swordSweepLeftSounds(player, volume, pitch);
        	}
        	else
        	{
        		bluntSwingLeftSounds(player, volume, pitch);
        	}
        }
		
        player.getCapability(EventHandlers.OFFHAND_CAP, null).swingOffHand(player);
		        
        if ( mov != null && mov.entityHit != null && mov.entityHit != player )
        {
            if ( shouldAttack(mov.entityHit, player) )
            {
                PacketHandler.instance.sendToServer(new PacketOffhandAttack(fatigue, mov.entityHit.getEntityId()));
            }
            else
            {
            	PacketHandler.instance.sendToServer(new PacketOffhandAttack(fatigue));
            }
        }
        else
        {
        	PacketHandler.instance.sendToServer(new PacketOffhandAttack(fatigue));
        }
        
		return true;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    public static void spawnSweepHit( EntityPlayer e, int x, int z )
    {
        double d0 = (double)(-MathHelper.sin(e.rotationYaw * 0.017453292F));
        double d1 = (double)MathHelper.cos(e.rotationYaw * 0.017453292F);

        e.world.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, x + d0 * 0.5D, e.posY + e.height * 0.5D, z + d1 * 0.5D, d0, 0.0D, d1);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
	
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event)
    {
        switch( event.getType() )
        {
            case CROSSHAIRS:
            {
                boolean cancelled = event.isCanceled();
                event.setCanceled(true);
                if( !cancelled )
                {
                    this.gc.renderAttackIndicator(0.5F, new ScaledResolution(Minecraft.getMinecraft()));
                    MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, event.getType()));
                }
                break;
            }
            default:
            {
            	break;
            }
        }
    }

    private static boolean shouldAttack(Entity entHit, EntityPlayer player)
    {
        if ( entHit == null )
        {
            return false;
        }

        if ( entHit instanceof EntityPlayerMP )
        {
            return Helpers.execNullable(entHit.getServer(), MinecraftServer::isPVPEnabled, false);
        }

        return ConfigurationHandler.isEntityAttackable(entHit) && !(entHit instanceof IEntityOwnable && ((IEntityOwnable) entHit).getOwner() == player);
    }

    public static RayTraceResult getMouseOverExtended(double dist)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.getRenderViewEntity();
        
        if ( player == null )
        {
            return null;
        }

        float w = player.width/2.0F;
        AxisAlignedBB viewBB = new AxisAlignedBB(player.posX - w, player.posY, player.posZ - w, player.posX + w, player.posY + player.height, player.posZ + w);
        // AxisAlignedBB viewBB = new AxisAlignedBB(player.posX - 0.5D, player.posY, player.posZ - 0.5D, player.posX + 0.5D, player.posY + 1.5D, player.posZ + 0.5D);
        
        if ( mc.world != null )
        {
            RayTraceResult traceResult = player.rayTrace(dist, 0.0F);
            final Vec3d pos = player.getPositionEyes(0.0F).addVector(0.0D, -Helpers.execNullable(player.getRidingEntity(), Entity::getMountedYOffset, 0.0D), 0.0D);
            final Vec3d lookVec = player.getLook(0.0F);
            final Vec3d lookTarget = pos.addVector(lookVec.x * dist, lookVec.y * dist, lookVec.z * dist);
            final float growth = 1.0F;
            final List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(player, viewBB.expand(lookVec.x * dist, lookVec.y * dist, lookVec.z * dist).grow(growth, growth, growth));
            final double calcdist = traceResult != null ? traceResult.hitVec.distanceTo(pos) : dist;

            double newDist = calcdist;
            Entity pointed = null;

            for ( Entity entity : list )
            {
                if ( entity.canBeCollidedWith() )
                {
                    float borderSize = entity.getCollisionBorderSize();
                    AxisAlignedBB aabb;
                    
                    float waw = entity.width/2.0F * ConfigurationHandler.extraAttackWidth;
                        
                    aabb = new AxisAlignedBB(entity.posX - waw, entity.posY, entity.posZ - waw,   entity.posX + waw, entity.posY + entity.height * ConfigurationHandler.extraAttackHeight, entity.posZ + waw);

                    aabb.grow(borderSize, borderSize, borderSize);
                    RayTraceResult mop0 = aabb.calculateIntercept(pos, lookTarget);
                    if ( aabb.contains(pos) && entity != player.getRidingEntity() )
                    {
                        if ( newDist >= -0.000001D )
                        {
                            pointed = entity;
                            newDist = 0.0D;
                        }
                    }
                    else if ( mop0 != null )
                    {
                        double hitDist = pos.distanceTo(mop0.hitVec);
                        if ( hitDist < newDist || (newDist >= -0.000001D && newDist <= 0.000001D) )
                        {
                            pointed = entity;
                            newDist = hitDist;
                        }
                    }
                }
            }

            if ( pointed != null && (newDist < calcdist || traceResult == null) )
            {
                return new RayTraceResult(pointed);
            }

            return traceResult;
        }

        return null;
    }
    
    private static int getArmSwingAnimationEnd( EntityPlayer player )
    {
        if (player.isPotionActive(MobEffects.HASTE))
        {
            return 6 - (1 + player.getActivePotionEffect(MobEffects.HASTE).getAmplifier());
        }
        else
        {
            return player.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
        }
    }
    
    public static void swordSweepLeftSounds( EntityPlayer player, float volume, float pitch )
    {
    	switch ( player.world.rand.nextInt(7) )
        {
        	case 0:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT0, volume, pitch);
                break;
        	}
        	case 1:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT1, volume, pitch);
                break;
        	}
        	case 2:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT2, volume, pitch);
                break;
        	}
        	case 3:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT3, volume, pitch);
                break;
        	}
        	case 4:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT4, volume, pitch);
                break;
        	}
        	case 5:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT5, volume, pitch);
                break;
        	}
        	case 6:
        	{
                player.playSound(Sounds.SWORD_SWEEP_LEFT6, volume, pitch);
                break;
        	}
        	default :
        	{
        		break;
        	}
        }
    }
    
    public static void swordSweepRightSounds( EntityPlayer player, float volume, float pitch )
    {
    	switch ( player.world.rand.nextInt(7) )
        {
        	case 0:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT0, volume, pitch);
                break;
        	}
        	case 1:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT1, volume, pitch);
                break;
        	}
        	case 2:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT2, volume, pitch);
                break;
        	}
        	case 3:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT3, volume, pitch);
                break;
        	}
        	case 4:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT4, volume, pitch);
                break;
        	}
        	case 5:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT5, volume, pitch);
                break;
        	}
        	case 6:
        	{
                player.playSound(Sounds.SWORD_SWEEP_RIGHT6, volume, pitch);
                break;
        	}
        	default :
        	{
        		break;
        	}
        }
    }
    
    public static void bluntSwingLeftSounds( EntityPlayer player, float volume, float pitch )
    {
    	if ( pitch >= 1.15F )
    	{
    		pitch -= 0.15F;
    		switch ( player.world.rand.nextInt(2) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.QUICK_SWING_LEFT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.QUICK_SWING_LEFT1, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    	else if ( pitch <= 0.825F )
    	{
    		pitch += 0.175F;
    		switch ( player.world.rand.nextInt(3) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.HEAVY_SWING_LEFT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.HEAVY_SWING_LEFT1, volume, pitch);
                    break;
            	}
            	case 2:
            	{
                    player.playSound(Sounds.HEAVY_SWING_LEFT2, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    	else
    	{
    		switch ( player.world.rand.nextInt(5) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.NORMAL_SWING_LEFT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.NORMAL_SWING_LEFT1, volume, pitch);
                    break;
            	}
            	case 2:
            	{
                    player.playSound(Sounds.NORMAL_SWING_LEFT2, volume, pitch);
                    break;
            	}
            	case 3:
            	{
                    player.playSound(Sounds.NORMAL_SWING_LEFT3, volume, pitch);
                    break;
            	}
            	case 4:
            	{
                    player.playSound(Sounds.NORMAL_SWING_LEFT4, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    }
    
    public static void bluntSwingRightSounds( EntityPlayer player, float volume, float pitch )
    {
    	if ( pitch >= 1.15F )
    	{
    		pitch -= 0.15F;
    		switch ( player.world.rand.nextInt(2) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.QUICK_SWING_RIGHT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.QUICK_SWING_RIGHT1, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    	else if ( pitch <= 0.85F )
    	{
    		pitch += 0.15F;
    		switch ( player.world.rand.nextInt(3) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.HEAVY_SWING_RIGHT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.HEAVY_SWING_RIGHT1, volume, pitch);
                    break;
            	}
            	case 2:
            	{
                    player.playSound(Sounds.HEAVY_SWING_RIGHT2, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    	else
    	{
    		switch ( player.world.rand.nextInt(5) )
            {
            	case 0:
            	{
                    player.playSound(Sounds.NORMAL_SWING_RIGHT0, volume, pitch);
                    break;
            	}
            	case 1:
            	{
                    player.playSound(Sounds.NORMAL_SWING_RIGHT1, volume, pitch);
                    break;
            	}
            	case 2:
            	{
                    player.playSound(Sounds.NORMAL_SWING_RIGHT2, volume, pitch);
                    break;
            	}
            	case 3:
            	{
                    player.playSound(Sounds.NORMAL_SWING_RIGHT3, volume, pitch);
                    break;
            	}
            	case 4:
            	{
                    player.playSound(Sounds.NORMAL_SWING_RIGHT4, volume, pitch);
                    break;
            	}
            	default :
            	{
            		break;
            	}
            }
    	}
    }
}
