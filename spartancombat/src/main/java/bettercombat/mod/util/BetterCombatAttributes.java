package bettercombat.mod.util;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class BetterCombatAttributes
{
    public static final IAttribute CRIT_CHANCE;
    public static final IAttribute CRIT_DAMAGE;
    
    static
    {
    	CRIT_CHANCE = (IAttribute)new RangedAttribute((IAttribute)null, "bettercombatmod.critChance", ConfigurationHandler.randomCrits, 0.0D, 1.0D);
    	CRIT_DAMAGE = (IAttribute)new RangedAttribute((IAttribute)null, "bettercombatmod.critDamage", ConfigurationHandler.bonusCritDamage, 0.0D, 256.0D);
    }
}