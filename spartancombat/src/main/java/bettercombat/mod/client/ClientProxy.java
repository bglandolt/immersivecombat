package bettercombat.mod.client;

import org.lwjgl.input.Keyboard;

import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.client.particle.EntitySweepAttack2FX;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.util.BetterCombatPotions;
import bettercombat.mod.util.CommonProxy;
import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy
{
	public static KeyBinding fastEquip = new KeyBinding("key.fastEquip.desc", Keyboard.KEY_X, "key.categories.misc");
	
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(EventHandlersClient.INSTANCE);
        PacketHandler.registerClientMessages();
        
        // http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-keybinding.html
    	ClientRegistry.registerKeyBinding(fastEquip);
    }

    @Override
    public void spawnSweep(EntityPlayer player)
    {
        double x = -MathHelper.sin(player.rotationYaw * 0.017453292F);
        double z = MathHelper.cos(player.rotationYaw * 0.017453292F);
        Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySweepAttack2FX(Minecraft.getMinecraft().getTextureManager(), player.world, player.posX + x, player.posY + player.height * 0.5D, player.posZ + z, 0.0D));
    }
}

//     private DamageSource damageSource = new DamageSource("").setDamageBypassesArmor();
