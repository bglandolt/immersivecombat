package bettercombat.mod.network;

import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.ConfigurationHandler.CustomWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFastEquip implements IMessage
{
    
    public PacketFastEquip()
    {
    }

    public PacketFastEquip(int f, int parEntityId)
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<PacketFastEquip, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketFastEquip message, final MessageContext ctx) 
        {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        /* [][][][][][][][()][!] */
        private static void handle(PacketFastEquip message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_ARMOR_EQUIP_IRON, player.getSoundCategory(), 1.0F, 1.0F);

            int mainWeaponFound = -1;
            boolean twoHanded = false;
            boolean offItemFound = false;
            ItemStack heldItem = player.inventory.getCurrentItem();
            ItemStack offItem = player.inventory.offHandInventory.get(0);
            
            /* No weapon in mainhand, look through hotbar */
            if ( !isWeapon(heldItem) )
            {
            	for ( int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
                {
                	ItemStack itemStack = player.inventory.mainInventory.get(i);
                	
                	if ( isWeapon(itemStack) )
                	{
                		mainWeaponFound = player.inventory.currentItem;
                		player.inventory.mainInventory.set(mainWeaponFound, itemStack);
                		player.inventory.mainInventory.set(i, heldItem);

                		String mainString = itemStack.getItem().getRegistryName().toString();
        		    	
        		 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
        		 		{
        		 			if ( mainString.contains(weapon.name) )
        		 			{
        		 				if ( weapon.fatigueMod > 0 )
        		 				{
        		 					twoHanded = true;
        		 				}
        		 				break;
        		 			}
        		 		}
                		break;
                	}
                }
            }
            /* There is a weapon in the mainhand */
            else
            {
            	String mainString = heldItem.getItem().getRegistryName().toString();
		    	
		 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
		 		{
		 			if ( mainString.contains(weapon.name) )
		 			{
		 				if ( weapon.fatigueMod > 0 )
		 				{
		 					twoHanded = true;
		 				}
		 				break;
		 			}
		 		}
		 		
        		mainWeaponFound = player.inventory.currentItem;
            }
            
            /* If there is not a weapon in the hotbar, look through the inventory */
            if ( mainWeaponFound < 0 && !ConfigurationHandler.fastEquipHotbarOnly )
            {
            	for ( int i = InventoryPlayer.getHotbarSize()-1; i < player.inventory.mainInventory.size(); i++)
                {
                	ItemStack itemStack = player.inventory.mainInventory.get(i);
                	
                	if ( isWeapon(itemStack) )
                	{
                		String mainString = itemStack.getItem().getRegistryName().toString();
        		    	
        		 		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
        		 		{
        		 			if ( mainString.contains(weapon.name) )
        		 			{
        		 				if ( weapon.fatigueMod > 0 )
        		 				{
        		 					twoHanded = true;
        		 				}
        		 				break;
        		 			}
        		 		}
        		 		
                		mainWeaponFound = player.inventory.currentItem;
                		player.inventory.mainInventory.set(mainWeaponFound, itemStack);
                		player.inventory.mainInventory.set(i, heldItem);
                		
                		break;
                	}
                }
            }
            
            
            /* The mainhand is twohanded, don't look for an offhand */
        	if ( twoHanded )
        	{
        		/* Try to move the offhand item to the inventory */
        		if ( player.addItemStackToInventory(offItem) )
        		{
        			player.inventory.offHandInventory.remove(0);
            		return;
        		}
        	}
        	
        	
            /* If there is no weapon or shield in the offhand */
        	if ( !isWeaponOrShield(offItem) )
            {
        		/* Look for a hotbar shield first */
            	for ( int i = 0; !offItemFound && i < InventoryPlayer.getHotbarSize(); i++ )
	            {
	            	if ( i != mainWeaponFound )
	            	{
	                	ItemStack itemStack = player.inventory.mainInventory.get(i);
	                	
	                	if ( isShield(itemStack) )
	                	{
	                		player.inventory.mainInventory.set(i, offItem);
	                		player.inventory.offHandInventory.set(0, itemStack);
	                		return;
	                	}
	            	}
	            }
            	
        		/* Look for a hotbar weapon second */
            	for ( int i = 0; !offItemFound && i < InventoryPlayer.getHotbarSize(); i++ )
	            {
	            	if ( i != mainWeaponFound )
	            	{
	                	ItemStack itemStack = player.inventory.mainInventory.get(i);
	                	
	                	if ( isWeapon(itemStack) )
	                	{
	                		offItemFound = true;
	                		
	                		String offString = itemStack.getItem().getRegistryName().toString();

	                		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
	        		 		{
	        		 			if ( offString.contains(weapon.name) )
	        		 			{
	        		 				if ( weapon.fatigueMod > 0 )
	        		 				{
	        		 					offItemFound = false;
	        		 				}
	        		 				break;
	        		 			}
	        		 		}

	                		if ( offItemFound )
	                		{
		                		player.inventory.mainInventory.set(i, offItem);
		                		player.inventory.offHandInventory.set(0, itemStack);
		                		return;
	                		}
	                	}
	            	}
	            }
            	
            	if ( !ConfigurationHandler.fastEquipHotbarOnly )
            	{
	            	/* Look for an inventory shield third */
	            	for ( int i = InventoryPlayer.getHotbarSize()-1; i < player.inventory.mainInventory.size(); i++)
		            {
		            	if ( i != mainWeaponFound )
		            	{
		                	ItemStack itemStack = player.inventory.mainInventory.get(i);
		                	
		                	if ( isShield(itemStack) )
		                	{
		                		player.inventory.mainInventory.set(i, offItem);
		                		player.inventory.offHandInventory.set(0, itemStack);
		                		return;
		                	}
		            	}
		            }
	            	
	        		/* Look for an inventory weapon fourth */
	            	for ( int i = InventoryPlayer.getHotbarSize()-1; i < player.inventory.mainInventory.size(); i++)
		            {
		            	if ( i != mainWeaponFound )
		            	{
		                	ItemStack itemStack = player.inventory.mainInventory.get(i);
		                	
		                	if ( isWeapon(itemStack) )
		                	{
		                		offItemFound = true;
		                		
		                		String offString = itemStack.getItem().getRegistryName().toString();
	
		                		for ( CustomWeapon weapon : ConfigurationHandler.weapons )
		        		 		{
		        		 			if ( offString.contains(weapon.name) )
		        		 			{
		        		 				if ( weapon.fatigueMod > 0 )
		        		 				{
		        		 					offItemFound = false;
		        		 				}
		        		 				break;
		        		 			}
		        		 		}
	
		                		if ( offItemFound )
		                		{
			                		player.inventory.mainInventory.set(i, offItem);
			                		player.inventory.offHandInventory.set(0, itemStack);
			                		return;
		                		}
		                	}
		            	}
		            }
            	}
            }
        }
        
        public static boolean isWeapon(ItemStack itemStack)
        {
        	return !itemStack.isEmpty() && ConfigurationHandler.isItemAttackUsable(itemStack.getItem()) && !(itemStack.getItem() instanceof ItemTool && !(itemStack.getItem() instanceof ItemAxe));
        }
        
        public static boolean isWeaponOrShield(ItemStack itemStack)
        {
        	return !itemStack.isEmpty() && (itemStack.getItem() instanceof ItemShield || ConfigurationHandler.isItemAttackUsable(itemStack.getItem())) && !(itemStack.getItem() instanceof ItemTool && !(itemStack.getItem() instanceof ItemAxe));
        }
        
        public static boolean isShield(ItemStack itemStack)
        {
        	return !itemStack.isEmpty() && (itemStack.getItem() instanceof ItemShield) && !(itemStack.getItem() instanceof ItemTool && !(itemStack.getItem() instanceof ItemAxe));
        }
        
//        public static boolean isTool(ItemStack itemStack)
//        {
//        	return !itemStack.isEmpty() && itemStack.getItem() instanceof ItemTool;
//        }
    }
}