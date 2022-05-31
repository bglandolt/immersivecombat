package bettercombat.mod.util;

import bettercombat.mod.capability.StorageOffHandAttack;
import bettercombat.mod.combat.DefaultImplOffHandAttack;
import bettercombat.mod.combat.IOffHandAttack;
import bettercombat.mod.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.registerMessages(Reference.MOD_ID);
        CapabilityManager.INSTANCE.register(IOffHandAttack.class, new StorageOffHandAttack(), DefaultImplOffHandAttack::new);
    }

    public void spawnSweep(EntityPlayer player) {}
}