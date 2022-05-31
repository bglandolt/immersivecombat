package bettercombat.mod.enchantment;

import bettercombat.mod.util.BetterCombatMod;
import bettercombat.mod.util.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BetterCombatEnchantments
{
	public static final Enchantment LIGHTNING = new EnchantmentLightning();
	
	@SubscribeEvent
	public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
	{
		event.getRegistry().registerAll(BetterCombatMod.ENCHANTMENTS.toArray(new Enchantment[0]));
	}
}