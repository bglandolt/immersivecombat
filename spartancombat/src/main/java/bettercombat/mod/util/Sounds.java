package bettercombat.mod.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class Sounds
{
    public static final SoundEvent SWORD_SLASH = registerSound("player.swordslash");
    public static final SoundEvent CRITICAL_STRIKE = registerSound("player.criticalstrike");
    
    public static final SoundEvent SWORD_SWEEP_LEFT0 = registerSound("player.sword_sweep_left0");
    public static final SoundEvent SWORD_SWEEP_LEFT1 = registerSound("player.sword_sweep_left1");
    public static final SoundEvent SWORD_SWEEP_LEFT2 = registerSound("player.sword_sweep_left2");
    public static final SoundEvent SWORD_SWEEP_LEFT3 = registerSound("player.sword_sweep_left3");
    public static final SoundEvent SWORD_SWEEP_LEFT4 = registerSound("player.sword_sweep_left4");
    public static final SoundEvent SWORD_SWEEP_LEFT5 = registerSound("player.sword_sweep_left5");
    public static final SoundEvent SWORD_SWEEP_LEFT6 = registerSound("player.sword_sweep_left6");
    
    public static final SoundEvent SWORD_SWEEP_RIGHT0 = registerSound("player.sword_sweep_right0");
    public static final SoundEvent SWORD_SWEEP_RIGHT1 = registerSound("player.sword_sweep_right1");
    public static final SoundEvent SWORD_SWEEP_RIGHT2 = registerSound("player.sword_sweep_right2");
    public static final SoundEvent SWORD_SWEEP_RIGHT3 = registerSound("player.sword_sweep_right3");
    public static final SoundEvent SWORD_SWEEP_RIGHT4 = registerSound("player.sword_sweep_right4");
    public static final SoundEvent SWORD_SWEEP_RIGHT5 = registerSound("player.sword_sweep_right5");
    public static final SoundEvent SWORD_SWEEP_RIGHT6 = registerSound("player.sword_sweep_right6");
    
    public static final SoundEvent ARMOR_HIT = registerSound("player.armor_hit");

    public static final SoundEvent SWORD_RING0 = registerSound("player.sword_ring0");
    public static final SoundEvent SWORD_RING1 = registerSound("player.sword_ring1");
    
    public static final SoundEvent SWORD_HIT0 = registerSound("player.sword_hit0");
    public static final SoundEvent SWORD_HIT1 = registerSound("player.sword_hit1");
    public static final SoundEvent SWORD_HIT2 = registerSound("player.sword_hit2");
    public static final SoundEvent SWORD_HIT3 = registerSound("player.sword_hit3");
    
//    public static final SoundEvent SWORD_HIT4 = registerSound("player.sword_hit4");
//    public static final SoundEvent SWORD_HIT5 = registerSound("player.sword_hit5");
//    public static final SoundEvent SWORD_HIT6 = registerSound("player.sword_hit6");
//    public static final SoundEvent SWORD_HIT7 = registerSound("player.sword_hit7");
    
//    public static final SoundEvent BLUNT_PING0 = registerSound("player.blunt_ping0");
//    public static final SoundEvent BLUNT_PING1 = registerSound("player.blunt_ping1");
    
    public static final SoundEvent BLUNT_HIT0 = registerSound("player.blunt_hit0");
    public static final SoundEvent BLUNT_HIT1 = registerSound("player.blunt_hit1");
    public static final SoundEvent BLUNT_HIT2 = registerSound("player.blunt_hit2");
    public static final SoundEvent BLUNT_HIT3 = registerSound("player.blunt_hit3");
    
//    public static final SoundEvent BLUNT_HIT4 = registerSound("player.blunt_hit4");
//    public static final SoundEvent BLUNT_HIT5 = registerSound("player.blunt_hit5");
//    public static final SoundEvent BLUNT_HIT6 = registerSound("player.blunt_hit6");
//    public static final SoundEvent BLUNT_HIT7 = registerSound("player.blunt_hit7");
    
    public static final SoundEvent NONMETAL_HIT0 = registerSound("player.nonmetal_hit0");
    public static final SoundEvent NONMETAL_HIT1 = registerSound("player.nonmetal_hit1");
    public static final SoundEvent NONMETAL_HIT2 = registerSound("player.nonmetal_hit2");
    public static final SoundEvent NONMETAL_HIT3 = registerSound("player.nonmetal_hit3");
    public static final SoundEvent NONMETAL_HIT4 = registerSound("player.nonmetal_hit4");
    public static final SoundEvent NONMETAL_HIT5 = registerSound("player.nonmetal_hit5");
    // public static final SoundEvent NONMETAL_HIT6 = registerSound("player.nonmetal_hit6");
    
    public static final SoundEvent HEAVY_SWING_LEFT0 = registerSound("player.heavy_swing_left0");
    public static final SoundEvent HEAVY_SWING_LEFT1 = registerSound("player.heavy_swing_left1");
    public static final SoundEvent HEAVY_SWING_LEFT2 = registerSound("player.heavy_swing_left2");

    public static final SoundEvent NORMAL_SWING_LEFT0 = registerSound("player.normal_swing_left0");
    public static final SoundEvent NORMAL_SWING_LEFT1 = registerSound("player.normal_swing_left1");
    public static final SoundEvent NORMAL_SWING_LEFT2 = registerSound("player.normal_swing_left2");
    public static final SoundEvent NORMAL_SWING_LEFT3 = registerSound("player.normal_swing_left3");
    public static final SoundEvent NORMAL_SWING_LEFT4 = registerSound("player.normal_swing_left4");
    
    public static final SoundEvent QUICK_SWING_LEFT0 = registerSound("player.quick_swing_left0");
    public static final SoundEvent QUICK_SWING_LEFT1 = registerSound("player.quick_swing_left1");
    
    public static final SoundEvent HEAVY_SWING_RIGHT0 = registerSound("player.heavy_swing_right0");
    public static final SoundEvent HEAVY_SWING_RIGHT1 = registerSound("player.heavy_swing_right1");
    public static final SoundEvent HEAVY_SWING_RIGHT2 = registerSound("player.heavy_swing_right2");

    public static final SoundEvent NORMAL_SWING_RIGHT0 = registerSound("player.normal_swing_right0");
    public static final SoundEvent NORMAL_SWING_RIGHT1 = registerSound("player.normal_swing_right1");
    public static final SoundEvent NORMAL_SWING_RIGHT2 = registerSound("player.normal_swing_right2");
    public static final SoundEvent NORMAL_SWING_RIGHT3 = registerSound("player.normal_swing_right3");
    public static final SoundEvent NORMAL_SWING_RIGHT4 = registerSound("player.normal_swing_right4");
    
    public static final SoundEvent QUICK_SWING_RIGHT0 = registerSound("player.quick_swing_right0");
    public static final SoundEvent QUICK_SWING_RIGHT1 = registerSound("player.quick_swing_right1");
    
    private static SoundEvent registerSound(String soundName)
    {
        ResourceLocation soundID = new ResourceLocation(Reference.MOD_ID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().registerAll
        (
        		SWORD_SLASH,
        		CRITICAL_STRIKE,
        		
        		SWORD_SWEEP_LEFT0,
        		SWORD_SWEEP_LEFT1,
        		SWORD_SWEEP_LEFT2,
        		SWORD_SWEEP_LEFT3,
        		SWORD_SWEEP_LEFT4,
        		SWORD_SWEEP_LEFT5,
        		SWORD_SWEEP_LEFT6,
        		
        		SWORD_SWEEP_RIGHT0,
        		SWORD_SWEEP_RIGHT1,
        		SWORD_SWEEP_RIGHT2,
        		SWORD_SWEEP_RIGHT3,
        		SWORD_SWEEP_RIGHT4,
        		SWORD_SWEEP_RIGHT5,
        		SWORD_SWEEP_RIGHT6,
        		
//        		SWORD_PING0,
//        		SWORD_PING1,
        		
        		SWORD_HIT0,
        		SWORD_HIT1,
        		SWORD_HIT2,
        		SWORD_HIT3,
//        		SWORD_HIT4,
//        		SWORD_HIT5,
//        		SWORD_HIT6,
//        		SWORD_HIT7,
//        		
//        		BLUNT_PING0,
//        		BLUNT_PING1,
        		
        		BLUNT_HIT0,
        		BLUNT_HIT1,
        		BLUNT_HIT2,
        		BLUNT_HIT3,
//        		BLUNT_HIT4,
//        		BLUNT_HIT5,
//        		BLUNT_HIT6,
//        		BLUNT_HIT7,
        		
        		NONMETAL_HIT0,
        		NONMETAL_HIT1,
        		NONMETAL_HIT2,
        		NONMETAL_HIT3,
        		NONMETAL_HIT4,
        		NONMETAL_HIT5,
//        		NONMETAL_HIT6,
        		
        	    HEAVY_SWING_LEFT0,
        	    HEAVY_SWING_LEFT1,
        	    HEAVY_SWING_LEFT2,

        	    NORMAL_SWING_LEFT0,
        	    NORMAL_SWING_LEFT1,
        	    NORMAL_SWING_LEFT2,
        	    NORMAL_SWING_LEFT3,
        	    NORMAL_SWING_LEFT4,
        	    
        	    QUICK_SWING_LEFT0,
        	    QUICK_SWING_LEFT1,
        	    
        	    HEAVY_SWING_RIGHT0,
        	    HEAVY_SWING_RIGHT1,
        	    HEAVY_SWING_RIGHT2,

        	    NORMAL_SWING_RIGHT0,
        	    NORMAL_SWING_RIGHT1,
        	    NORMAL_SWING_RIGHT2,
        	    NORMAL_SWING_RIGHT3,
        	    NORMAL_SWING_RIGHT4,
        	    
        	    QUICK_SWING_RIGHT0,
        	    QUICK_SWING_RIGHT1
        );
    }
}