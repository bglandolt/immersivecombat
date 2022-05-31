package bettercombat.mod.util;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.handler.EventHandlers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION, guiFactory="bettercombat.mod.client.gui.GUIFactory", acceptedMinecraftVersions="[1.12.2]")
public class BetterCombatMod
{
    @SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    @Mod.Instance(Reference.MOD_ID)
    public static BetterCombatMod modInstance;

    public static Logger LOG = LogManager.getLogger(Reference.MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EventHandlers.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EventHandlersClient.INSTANCE);
        proxy.preInit(event);
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CapabilityOffhandCooldown.register();
        ConfigurationHandler.createInstLists();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
    public static final ArrayList<Enchantment> ENCHANTMENTS = new ArrayList<Enchantment>();
    
	public static final EnumEnchantmentType THROWING_WEAPON = addEnchantment("throwing", item -> item instanceof ItemBow || item.getClass().getSuperclass().getSimpleName().equals("ItemThrowingWeapon")); //ItemPickaxe.class::isInstance
	
    @Nonnull
    public static EnumEnchantmentType addEnchantment(String name, Predicate<Item> condition)
    {
      return EnumHelper.addEnchantmentType(name, condition::test);
    }
}