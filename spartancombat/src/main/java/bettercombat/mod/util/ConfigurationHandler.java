package bettercombat.mod.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler
{
  public static Configuration config;
  public static boolean hitSound = true;
  public static boolean swingSoundsOnWeapons = true;
  public static boolean swingSoundsOnNonWeapons = true;
  public static boolean moreSprint = true;
  public static boolean moreSweep = true;
  public static boolean playArrowHitSound = true;
  public static boolean nauseaAffectsMobs = true;
  public static float offHandEfficiency = 0.5F;
  public static float extraAttackWidth = 1.2F;
  public static float extraAttackHeight = 1.2F;
  public static float randomCrits = 0.1F;
  public static float jumpAndSprintCrits = 0.1F;
  public static float luckCritModifier = 0.2F;
  public static float dragonboneBowWitherDamage = 12.0F;
  public static boolean warhammerArmorPierceAdjustments = true;
  public static float silverArmorDamagesUndeadAttackers = 1.5F;
  public static float bonusCritDamage = 1.5F;
  public static float baseReachDistance = 4.0F;
  public static boolean sneakingDisablesOffhand = true;
  public static boolean removeHurtResistantTime = true;
  public static boolean autoCritOnSneakAttacks = true;
  public static float rangedSilverDamageMultiplier = 1.5F;
  public static float shieldSilverDamageMultiplier = 1.5F;
  public static float baseAttackDamage = 1.0F;
  public static int critsDisableShield = 40;
  public static boolean wardsModCompatability = true;
  public static boolean damageParticles = true;
  public static float inertiaOnAttack = 0.6F;
  public static boolean aetherealizedDamageParticles = true;
  public static boolean tippedArrowFix = true;
  public static boolean healingArrowNoDamage = true;
  public static double baseAttackSpeed = 4.0D;
  public static double fistAndNonWeaponReducedDamage = 0.5F;
  public static boolean addSpartanWeaponryTags = true;
  public static boolean blockFireProjectiles = true;

  public static float addedFistTickCooldown = 2.5F;
  public static float addedSwingTickCooldown = 0.5F;
  
  public static boolean fastEquipHotbarOnly = false;

  public static float lightningEnchantmentDamagePerLevel = 2.0F;
  public static float lightningEnchantmentBaseDamage = 3.0F;
  public static float lightningEnchantmentWetModifier = 1.5F;
  public static int lightningEnchantmentMaxLevel = 5;
  public static int lightningEnchantmentEnchantabilityPerLevel = 10;
  public static float alchemizedAmplifier = 0.2F;
  public static float critChancePotionAmplifier = 0.1F;
  public static float critDamagePotionAmplifier = 0.1F;
  
  public static double strengthPotionMultiplier = 0.05F;
  public static double weaknessPotionMultiplier = 0.1F;
  
  public static boolean betterKnockback = true;
  public static float knockUpStrength = 0.5F;

  public static boolean enableOffhandAttack = true;
  
  public static float bowThudSoundVolume = 0.3F;
  public static float bowStrikeSoundVolume = 0.5F;
  
  public static float weaponHitSoundVolume = 1.0F;
  public static float weaponSwingSoundVolume = 1.0F;
  
  public static String[] nonMetalList =
  {
	  "wood",
	  "stone",
	  "bone",
	  "staff",
	  "caestus",
	  "club"
  };
  
  public static ArrayList<SpartanBow> bows = new ArrayList<SpartanBow>();

  private static String[] spartanBowList =
  {
	  "minecraft:bow                    ~ 1.00 ~ 1.00",
	  "spartanweaponry:longbow_wood     ~ 0.95 ~ 1.00",
      "spartanweaponry:crossbow_wood    ~ 1.05 ~ 0.95"
  };
  
  public static ArrayList<SpartanShield> shields = new ArrayList<SpartanShield>();

  private static String[] spartanShieldList =
  {
	  "minecraft:shield                     ~ 1.0 ~ 1.0 ~ 30",
	  "spartanshields:shield_basic_diamond  ~ 4.0 ~ 2.0 ~ 25",
	  "spartanshields:shield_basic_gold     ~ 1.0 ~ 1.0 ~ 10",
	  "spartanshields:shield_basic_iron     ~ 3.0 ~ 1.0 ~ 25",
	  "spartanshields:shield_basic_obsidian ~ 3.0 ~ 1.0 ~ 55",
	  "spartanshields:shield_basic_silver   ~ 3.0 ~ 1.0 ~ 25",
	  "spartanshields:shield_basic_stone    ~ 2.0 ~ 1.0 ~ 25",
	  "spartanshields:shield_basic_wood     ~ 1.0 ~ 1.0 ~ 25",
	  "spartanshields:shield_tower_diamond  ~ 4.0 ~ 2.5 ~ 30",
	  "spartanshields:shield_tower_gold     ~ 1.0 ~ 1.5 ~ 15",
	  "spartanshields:shield_tower_iron     ~ 3.0 ~ 1.5 ~ 30",
	  "spartanshields:shield_tower_obsidian ~ 3.0 ~ 1.5 ~ 60",
	  "spartanshields:shield_tower_silver   ~ 3.0 ~ 1.5 ~ 30",
	  "spartanshields:shield_tower_stone    ~ 2.0 ~ 1.5 ~ 30",
	  "spartanshields:shield_tower_wood     ~ 1.0 ~ 1.5 ~ 30"
  };
  
  public static ArrayList<CustomWeapon> weapons = new ArrayList<CustomWeapon>();

  // weaponName ~ fatigue ~ +extraReachDistance ~ +extraCritChance ~ +extraCritDamage ~ attackSweep
  private static String[] customAttributesWeaponsList =
  {
	  "pike_        ~ 3 ~ 2.0 ~ 0.0 ~ 0.0 ~ -1",
	  "glaive_      ~ 3 ~ 1.0 ~ 0.0 ~ 0.0 ~  2",
	  "halberd_     ~ 4 ~ 1.0 ~ 0.0 ~ 0.0 ~ -1",
	  "greatsword_  ~ 4 ~ 1.0 ~ 0.0 ~ 0.0 ~  3",
	  "spear_       ~ 0 ~ 1.0 ~ 0.0 ~ 0.0 ~ -1",
	  "lance_       ~ 0 ~ 1.0 ~ 0.0 ~ 0.0 ~ -1",
	  "staff        ~ 0 ~ 1.0 ~ 0.0 ~ 0.0 ~  2",
	  "katana_      ~ 3 ~ 0.0 ~ 0.0 ~ 0.0 ~  0",
	  "warhammer_   ~ 3 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1",
	  "battleaxe_   ~ 3 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1",
	  "longsword_   ~ 3 ~ 0.0 ~ 0.0 ~ 0.0 ~  0"
  };
    
  private static String[] itemClassWhitelist = 
  { 
		  "net.minecraft.item.ItemSword",
		  "net.minecraft.item.ItemAxe",
		  "net.minecraft.item.ItemSpade",
		  "net.minecraft.item.ItemPickaxe",
		  "net.minecraft.item.ItemHoe",
		  "com.oblivioussp.spartanweaponry.item.ItemWeaponBase",
		  "com.oblivioussp.spartanweaponry.item.ItemSwordBase",
		  "com.oblivioussp.spartanweaponry.item.ItemSaber",
		  "com.oblivioussp.spartanweaponry.item.ItemRapier",
		  "com.oblivioussp.spartanweaponry.item.ItemGlaive",
		  "com.oblivioussp.spartanweaponry.item.ItemSpear",
		  "com.oblivioussp.spartanweaponry.item.ItemPike",
		  "com.oblivioussp.spartanweaponry.item.ItemHammer",
		  "com.oblivioussp.spartanweaponry.item.ItemMace",
		  "com.oblivioussp.spartanweaponry.item.ItemWarhammer",
		  "com.oblivioussp.spartanweaponry.item.ItemBattleaxe",
		  "com.oblivioussp.spartanweaponry.item.ItemCaestus",
		  "com.oblivioussp.spartanweaponry.item.ItemClub",
		  "com.oblivioussp.spartanweaponry.item.ItemGreatsword",
		  "com.oblivioussp.spartanweaponry.item.ItemKatana",
		  "com.oblivioussp.spartanweaponry.item.ItemLance",
		  "com.oblivioussp.spartanweaponry.item.ItemLongsword",
		  "com.oblivioussp.spartanweaponry.item.ItemQuarterstaff",
		  "com.oblivioussp.spartanweaponry.item.ItemHalberd",
		  "com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon",
		  "slimeknights.tconstruct.library.tools.SwordCore",
		  "slimeknights.tconstruct.library.tools.AoeToolCore"
   	};
  
	public static class SpartanBow
	{
		public String bow = "";
		public double damage = 1.0D;
		public double velocity = 1.0D;
	}

	public static class CustomWeapon
	{
		public String name = "";
		public int fatigueMod = 0;
		public double reachMod = 0.0D;
		public double critChanceMod = 0.0D;
		public double critDamageMod = 0.0D;
		public int sweepMod = 0;
	}
	
	public static class SpartanShield
	{
		public String shield = "";
		public double damage = 1.0D;
		public double knockback = 1.0D;
		public int cooldown = 30;
	}
  
  public static void init(File configFile)
  {
    if (config == null)
    {
      config = new Configuration(configFile, Integer.toString(5));
      loadConfiguration();
    } 
  }
  
  private static final String WEAPON = "Weapon Tweaker";
  private static final String BOW = "Bow Tweaker";
  private static final String SHIELD = "Shield Tweaker";
  private static final String GENERAL = "General";
  private static final String IAF = "Ice & Fire";
  private static final String SOUND = "Sound";
  private static final String CRITICAL = "Critical Hits";
  private static final String LISTS = "White/Black Lists";
  private static final String ENCHANTMENT = "Enchantments";
  private static final String POTIONS = "Potions";

  private static void loadConfiguration()
  {
	baseReachDistance = config.getFloat("Base Reach Distance", GENERAL, 4.0F, 0.0F, 16.0F, "This base attack range (in vanilla multiplayer, the default range is 5.0F. In vanilla singleplayer, the default range is 4.0F).");
    baseAttackDamage = config.getFloat("Base Attack Damage", GENERAL, 1.0F, 0.0F, 256.0F, "Base attack damage for the player. Vanilla is 1.0F.");
    baseAttackSpeed = config.getFloat("Base Attack Speed", GENERAL, 4.0F, 0.0F, 256.0F, "Base attack speed for the player. Vanilla is 4.0F. Higher values mean faster attack swing recovery. 1.6F attack speed means attacks take 13 ticks to recover after an attack swing. 0.8F means 20 ticks to recover after an attack swing. 4.0F means 5 ticks to recover after an attack.");
    fistAndNonWeaponReducedDamage = config.getFloat("Reduce Fist & Non-Weapon Damage", GENERAL, 0.0F, 0.5F, 256.0F, "Reduce the damage dealt by fists and non-weapons by this amount. Set 0.0F to disable.");

	enableOffhandAttack = config.getBoolean("Offhand Attack", GENERAL, true, "Enables right-click to attack with offhand weapon");
	sneakingDisablesOffhand = config.getBoolean("Sneaking Disables Offhand Attack", GENERAL, true, "If set to true, attacking with your offhand is disabled while sneaking. The purpose of this is to add compatibility to mods such as CarryOn or Effortless Building where you need to use your right-click.");
    moreSprint = config.getBoolean("Attack & Sprint", GENERAL, true, "Attacking an enemy while sprinting will no longer interrupt your sprint.");
    moreSweep = config.getBoolean("More Attack Hit Particles", GENERAL, true, "Add sweep particles on attack (looks good, reccommend you keep this as true).");
    removeHurtResistantTime = config.getBoolean("Remove Hurt Resistant Time", GENERAL, true, "Remove hurt resistant time before an attack with a valid weapon to ensure it always deals the damage it should.");
    offHandEfficiency = config.getFloat("Offhand Efficiency", GENERAL, 0.5F, 0.0F, 256.0F, "The efficiency of an attack with offhanded weapon in percent (attack damage * efficiency).");
    extraAttackWidth = config.getFloat("Extra Attack Width", GENERAL, 1.2F, 0.0F, 64.0F, "How wide the hitbox will be extended for attacks (recommend keeping this between 1.0F and 1.2F). This setting is multiplicative, and setting it to 1.0 will leave the attack width unchanged.");
    extraAttackHeight = config.getFloat("Extra Attack Height", GENERAL, 1.2F, 0.0F, 64.0F, "How high the hitbox will be extended for attacks (recommend keeping this between 1.0F and 1.2F). This setting is multiplicative, and setting it to 1.0 will leave the attack height unchanged.");
    nauseaAffectsMobs = config.getBoolean("Nausea Affects Mobs", GENERAL, true, "Enable to have nausea & blindness affect mobs of medium/large height (3.0F) or below.");
    damageParticles = config.getBoolean("Damage Particles", GENERAL, true, "Enable to show heart damage particles (this is a vanilla feature, this option is here for those who wish to disable it).");
    warhammerArmorPierceAdjustments = config.getBoolean("Warhammer Armor Pierce", GENERAL, true, "Enable to have armor the piercing effect of warhammers actually work against mobs who have added armor attribute values. The armor attribute can be added to mobs through RoughMobsRevamped. The armor peircing amount has also been slightly increased to make the damage difference more noticable.");
	betterKnockback = config.getBoolean("Better Knockback", GENERAL, true,
			"Enable have the knockback_resistance attribute reduce the STRENGTH of knockback effects, rather than reducing the CHANCE to not be knocked back. (For example: by default, a knockback_resistance of 0.5 means a 50% chance to not be knocked back from an attack. However, if this setting is true, a knockback_resistance of 0.5 means the distance or effects of being knocked back are 50% less far or powerful.");			
	knockUpStrength = config.getFloat("knockUpStrength", GENERAL, 0.5f, 0.0f, 2.0f,
			"Multiply the motionY amount of knockback by this amount. Set to 0.5 by default to reduce the motionY by 50%. Does nothing if betterKnockback is disabled.");
	inertiaOnAttack = config.getFloat("Intertia on Attack", GENERAL, 0.6F, 0.0F, 2.0F, "Multiplies the player speed by this amount on successful attack. Set to 1.0F to disable.");
	blockFireProjectiles = config.getBoolean("Block Fire Projectiles", GENERAL, true, "Enable to have flaming prjectiles/arrows not set you on fire when you block them.");
	fastEquipHotbarOnly = config.getBoolean("Fast Equip Hotbar Only", GENERAL, false, "Set to true to have Fast Equip use only items in the hotbar instead of the entire inventory.");
	
	aetherealizedDamageParticles = config.getBoolean("Aetherealized Damage Particles", GENERAL, true, "Enable to have the Aetherealized potion create a ring of particles around the target when struck.");
	dragonboneBowWitherDamage = config.getFloat("Dragonbone Wither", IAF, 12.0F, 0.0F, 128.0F, "The dragonbone bow does the same damage as a normal bow (possibly due to the interactions of some other mods?) This setting adds wither damage to the bow to compensate for the lack of damage. Set to 0.0F to disable.");
    silverArmorDamagesUndeadAttackers = config.getFloat("Silver Armor Damage", IAF, 1.5F, 0.0F, 64.0F, "How much damage undead attackers will take against an entity wearing silver armor, per piece of silver armor. Silver weapons get a damage bonus, so why not add a special interaction with silver armor?");
    rangedSilverDamageMultiplier = config.getFloat("Ranged Silver Damage", IAF, 1.5F, 0.0F, 64.0F, "Multiplier for ranged silver weapons against undead. Set to 1.0F to disable.");
    shieldSilverDamageMultiplier = config.getFloat("Shield Silver Damage", IAF, 1.5F, 0.0F, 64.0F, "Multiplier for silver shield bash against undead. Set to 1.0F to disable.");
    addSpartanWeaponryTags = config.getBoolean("Add Spartan Weaponry Tags", GENERAL, true, "Enable to have Spartan Weaponry tags added to weapons that do not have the correct tags already. For example; if you make vanilla minecraft swords have fatigue III, then the Two-Handed I tag will be added to the weapon.");
    
    bowThudSoundVolume = config.getFloat("Bow Thud Sound Volume", SOUND, 0.3F, 0.0F, 1.0F, "The volume of the 'thud' sound that plays when you land a fully-charged hit.");
    bowStrikeSoundVolume = config.getFloat("Bow Strike Sound Volume", SOUND, 0.5F, 0.0F, 2.0F, "The volume of the 'strike' sound that plays when you land a hit.");
    weaponSwingSoundVolume = config.getFloat("Weapon Swing Sound Volume", SOUND, 1.0F, 0.0F, 2.0F, "The volume of the sound when you swing your weapon.");
    weaponHitSoundVolume = config.getFloat("Weapon Hit Sound Volume", SOUND, 1.0F, 0.0F, 2.0F, "The volume of the sound that plays when you land a weapon hit.");
    hitSound = config.getBoolean("Additional Hit Sound", SOUND, true, "Add an additional sound when striking a target.");
    swingSoundsOnWeapons = config.getBoolean("Additional Swing Sound On Weapons", SOUND, true, "Add an additional sound when you swing a weapon, the sound will pan left for offhand attacks, and pan right for mainhand attacks!");
    swingSoundsOnNonWeapons = config.getBoolean("Additional Swing Sound On Non-Weapons", SOUND, true, "Add an additional sound when you swing a non-weapon (such as your fists, a stick, or even a ball of clay), the sound will pan left for offhand attacks, and pan right for mainhand attacks!");
    playArrowHitSound = config.getBoolean("Arrow Impact Sound", SOUND, true, "Arrows will make an impact sound when they hit an entity, regardless of range.");
    nonMetalList = config.getStringList("Non Metal List", SOUND, nonMetalList, "Weapons that are considered non-metal for swinging and hitting sound purposes. If the weapon contains the string, such as 'wood' or 'stone' it will not make a metal sound.");

    luckCritModifier = config.getFloat("Luck Affects Critical Chance", CRITICAL, 0.2F, 0.0F, 1.0F, "LUCK attribute affects crit chance. Forumla = ( LUCK * luckCritModifier ). If luckCritModifier is 0.1F then 2 LUCK would give 20% crit chance.");
    autoCritOnSneakAttacks = config.getBoolean("Auto Critical On Sneak Attacks", CRITICAL, true, "Automatically crit a mob when you are not the attack target or revenge target of that mob.");
    jumpAndSprintCrits = config.getFloat("Additional Critical Chance", CRITICAL, 0.1F, 0.0F, 1.0F, "Adds additional critical strike chance when the player is falling like in vanilla or sprinting.");
    randomCrits = config.getFloat("Random Critical Chance", CRITICAL, 0.1F, 0.0F, 1.0F, "Adds random crit chance and replaces vanilla critical strikes.");
    bonusCritDamage = config.getFloat("Critical Damage Bonus", CRITICAL, 1.5F, 0.0F, 256.0F, "How much more damage crits do (multiplier). Default 1.5F, meaning crits do 50% extra damage (150% of total damage).");
    critsDisableShield = config.getInt("Critical Strikes Disable Shields", CRITICAL, 40, 0, 256, "Critical strikes with any weapon disable shields for X ticks, similar to how axes disable shields for 100 ticks. Set to 0 to disable this feature.");
    
    itemClassWhitelist = config.getStringList("Item Class Whitelist", LISTS, ICW_DEF, "Whitelisted item classes for attacking.");
    itemInstWhitelist = config.getStringList("Item Whitelist", LISTS, IIW_DEF, "Whitelisted items in the format \"modid:itemname\" for attacking.");
    entityBlacklist = config.getStringList("Entity Blacklist", LISTS, EB_DEF, "Blacklisted entity classes for attacking. You will not be able to attack any entity that extends this class! Please note that entities extending IEntityOwnable are by default blacklisted, when the entity is owned by the attacker.");
    
    lightningEnchantmentEnchantabilityPerLevel = config.getInt("Lightning Enchantment Enchantability Per Level", ENCHANTMENT, 10, 0, 256, "The enchantability per level that must be met for this enchantment to show up.");
    lightningEnchantmentMaxLevel = config.getInt("Lightning Enchantment Max Level", ENCHANTMENT, 5, 1, 256, "The max level of the lightning enchant. 5 means the max is Lightning V.");
    lightningEnchantmentWetModifier = config.getFloat("Lightning Enchantment Wet Modifier", ENCHANTMENT, 1.5F, 0.0F, 256.0F, "This number is multiplied by the total lightning damage when targets are wet. 1.5F means 50% more damage.");
    lightningEnchantmentBaseDamage = config.getFloat("Lightning Enchantment Base Damage", ENCHANTMENT, 3.0F, 0.0F, 256.0F, "Lightning enchantment base damage. A base of 3 and 2 per level means Lightning I deals 5 damage.");
    lightningEnchantmentDamagePerLevel = config.getFloat("Lightning Enchantment Damage Per Level", ENCHANTMENT, 2.0F, 0.0F, 256.0F, "Lightning enchantment damage per level. A base of 3 and 2 per level means Lightning V deals 13 damage.");
    
	alchemizedAmplifier = config.getFloat("Aetherealized Potion Damage", POTIONS, 0.2F, 0.0F, 1.0F, "The percentage of your physical damage that is converted to magical by the Aetherealized potion, per level. 0.2F means 20% of your physical damage (per level) is converted to magic damage.");
	critChancePotionAmplifier = config.getFloat("Percision Potion Chance", POTIONS, 0.1F, 0.0F, 1.0F, "The additive percent increase to your critical strike chance that the Percision potion adds, per level. 0.1F means add 10% critical strike chance (per level) to your base critical strike chance.");
	critDamagePotionAmplifier = config.getFloat("Brutality Potion Damage", POTIONS, 0.1F, 0.0F, 1.0F, "The additive percent increase to your critical strike damage that the Brutality potion adds, per level. 0.1F means add 10% critical strike damage (per level) to your base critical strike damage.");
	tippedArrowFix = config.getBoolean("Tipped Arrow Fix", POTIONS, true, "Fixes the terrible feature that makes harming/healing tipped arrows useless: Arrows of Harming (and arrows of Healing when used against undead mobs) do not add a static amount of damage to the arrow. Instead, the arrow's damage is first calculated, then checked to see if it is below 12♥ × 6. If the arrow's damage is less than 12, the Harming effect of the arrow makes up the difference, to ensure the arrow does exactly 12♥ × 6. Therefore, an unenchanted bow cannot deal more than 12 damage using Harming (or Healing) arrows, as it can deal a maximum of 11♥ × 5.5 damage on level ground. However, if the arrow would deal more than 12 damage, the harming effect is entirely neutralized. This means that bows enchanted with Power I through Power III has a chance to not utilize the arrow at full charge, and any Power level above III never utilizes Arrows of Harming effectively at full charge when against unarmored mobs/players." );
    healingArrowNoDamage = config.getBoolean("Healing Arrow No Damage", POTIONS, true, "Enable to have tipped arrows that cause healing (including healing from harming arrows against undead) to deal no damage.");
	
    strengthPotionMultiplier = config.getFloat("Strength Potion Damage", POTIONS, 0.05F, 0.0F, 1.0F, "Changes the Strength potion to be a percentage damage increase instead of a flat increase. If set to 0.05F, then a Potion of Strength (+3 Attack Damage) will increase damage dealt by 15%. Set to 0.0F to disable.");
	weaknessPotionMultiplier = config.getFloat("Weakness Potion Damage", POTIONS, 0.1F, 0.0F, 1.0F, "Changes the Weakness potion to be a percentage damage decrease instead of a flat decrease. If set to 0.1F, then a Potion of Weakness (-4 Attack Damage) will decrease damage dealt by 40%. Set to 0.0F to disable.");
    
    spartanBowList = config.getStringList("modID:bowName ~ *arrowDamageMultiplier ~ *arrowVelocityMultiplier", BOW, spartanBowList,
      "Config list for tweaking the base damage and the velocity of all arrows shot from specific bows.\n\n"
    		
    + "The base damage of a flint arrow is 2. The final damage of a flint arrow averages ~9.\n"
    + "The velocity of a fully-charged arrow (meaning a critical, with a trail of particles) averages 3.\n\n"
    
    + "The formula the final damage of a flint arrow is   ->   (velocity * arrowBaseDamage) + rand.nextInt(velocity * arrowBaseDamage / 2) + 2   ->   (~3 * 2) + rand.nextInt(~3 * 2 / 2) + 2   ->   ~9 damage.\n"
    + "For example, if you multiplied the damage of arrows shot from a minecraft:bow by 1.5 (meaning the base damage of a flint arrow would now be 3 instead of 2) the final average of that fully-charged shot would be ~13 damage.\n\n"
    
    + "The following entry (spartanweaponry:longbow_wood ~ 2.0 ~ 1.0) would multiply the base damage of arrows shot by the longbow by 2, and have the velocity remain unchanged.\n "
    + "This would change the damage of a fully-charged shot of a flint arrow by a longbow from ~9 damage to ~16 damage.\n\n"
    
    + "The following entry (spartanweaponry:crossbow_wood ~ 0.8 ~ 1.25) would cut the base damage of crossbow by 20%, but increase the velocity of the arrows (bolts) shot by it by 25%.\n"
    + "This would keep the damage of a fully-charged shot of a bolt by a crossbow roughly the same, however, the velocity would be greatly increased.\n\n"
    
    + "The following entry (minecraft:bow ~ 1.0 ~ 1.0) would do nothing, as neither the damage or velocity is changed (multiplier of 1 would not change the value).\n\n"
    
    + "The format for the config is  ->  modID:bowName~arrowDamageMultiplier~arrowVelocityMultiplier.\n"
    + "The maximum velocity multiplier is 1.5, anything over this value will do nothing! The reason being is that the arrow travels too fast which leads to visual anomalies.\n"
    + "If the arrows look like they are swerving off to the side, it means the velocity is too high and you'll have to lower it.\n\n"
    + "Longbows from SpartanWeaponry already have a velocity multiplier of 1.2, try not to increase velocity much more.\n"
    + "Crossbows from SpartanWeaponry already have a velocity multiplier of 1.5, do not increase any further!\n\n"
    + "More info on bows can be found here   ->   https://minecraft.fandom.com/wiki/Bow#Weapon");

    for ( String s : spartanBowList )
	{
		try
		{
			s = s.replaceAll("\\s", "");
			String[] list = s.split("~");
			SpartanBow spartanBow = new SpartanBow();
			spartanBow.bow = list[0];
			spartanBow.damage = Double.parseDouble(list[1]);
			spartanBow.velocity = MathHelper.clamp(Double.parseDouble(list[2]), 0.0D, 1.5D);
			bows.add(spartanBow);
		}
		catch ( Exception e )
		{
			System.out.println( "WARNING, incorrect Bow Damage Tweaker: " + s );
		}
	}
    
    customAttributesWeaponsList = config.getStringList("weaponName ~ fatigue ~ +extraReachDistance ~ +extraCritChance ~ +extraCritDamage ~ attackSweep", WEAPON, customAttributesWeaponsList, 
	  "Add reach and fatigue values for non-spartanweaponry weapons (the correct values for Spartan Weaponry weapons are listed in the default config).\n"
	+ "Format for this config setting example   ->     weaponName ~ fatigue ~ +extraReachDistance ~ +extraCritChance ~ +extraCritDamage ~ attackSweep.\n"
	+ "The itemName field can be simply 'pike_' for example, so that all items containing the word 'pike_' will affected, such as 'pike_wooden' or 'pike_iron'\n\n"
	
	+ "Set attackSweep to -1 to remove sweep from a weapon. Set attackSweep to 0 to have default sword sweep, which deals base attack damage to sweep targets.\n"
	+ "For each level of sweep, it deals 25% of the main damage to sweep targets. Sweep 4 is 100% of main damage dealt to sweep targets.\n"
	+ "If you wanted to remove the default sweep from all minecraft swords, add the config line   _sword ~ 0 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1   "
	+ "OR you could add a line for each sword specifically, such as   minecraft:wooden_sword ~ 0 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1    "
	+ "minecraft:iron_sword ~ 0 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1   minecraft:diamond_sword ~ 0 ~ 0.0 ~ 0.0 ~ 0.0 ~ -1");
	
//	+ "\n\nAdding attributes such as crit damage to weapons will not show up in the item description! You will have "
//	+ "to add it yourself using a mod such as CraftTweaker. Say you added critical strike damage to daggers, you could "
//	+ "add lines such as  <spartanweaponry:dagger_iron>.addTooltip('§o+100% critical strike damage');  to a "
//	+ "CraftTweaker script to let players know daggers deal additional crit damage.");

    for ( String s : customAttributesWeaponsList )
	{
		try
		{
			s = s.replaceAll("\\s", "");
			String[] list = s.split("~");
			CustomWeapon customWeapon = new CustomWeapon();
			customWeapon.name = list[0];
			customWeapon.fatigueMod = MathHelper.clamp(Integer.parseInt(list[1]), 0, 255);
			customWeapon.reachMod = Double.parseDouble(list[2]);
			customWeapon.critChanceMod = Double.parseDouble(list[3]);
			customWeapon.critDamageMod = Double.parseDouble(list[4]);
			customWeapon.sweepMod = Integer.parseInt(list[5]);
			weapons.add(customWeapon);
		}
		catch ( Exception e )
		{
			System.out.println("WARNING, incorrect Weapon Attribute Tweaker: " + s );
		}
	}
    
    spartanShieldList = config.getStringList("modID:shieldName ~ damage ~ knockback ~ cooldown", SHIELD, spartanShieldList, "Config list for tweaking the damage, knockback amount, and cooldown on shield bashing.");

    for ( String s : spartanShieldList )
	{
		try
		{
			s = s.replaceAll("\\s", "");
			String[] list = s.split("~");
			SpartanShield spartanShield = new SpartanShield();
			spartanShield.shield = list[0];
			spartanShield.damage = MathHelper.clamp(Double.parseDouble(list[1]), 0.0D, 326.0D);
			spartanShield.knockback = MathHelper.clamp(Double.parseDouble(list[2]), 0.0D, 326.0D);
			spartanShield.cooldown = MathHelper.clamp(Integer.parseInt(list[3]), 0, 326);
			shields.add(spartanShield);
		}
		catch ( Exception e )
		{
			System.out.println( "WARNING, incorrect Shield Tweaker: " + s );
		}
	}
        
    if ( config.hasChanged() )
    {
    	config.save();
    }
    
  }
  
  
  
  
  
  
  
  
  public static void createInstLists()
  {
    @SuppressWarnings("rawtypes")
	ArrayList<Class> classList = new ArrayList<Class>();
    for (String className : itemClassWhitelist)
    {
      try
      {
        classList.add(Class.forName(className));
      }
      catch (ClassNotFoundException classNotFoundException)
      {
    	  
      }
    } 
    itemClassWhiteArray = (Class[])classList.toArray(new Class[0]);
    
    classList.clear();
    
    for (String className : entityBlacklist)
    {
      try
      {
        classList.add(Class.forName(className));
      }
      catch (ClassNotFoundException e)
      {
        System.out.println("No class found: " + className);
      } 
    } 
    entityBlackArray = (Class[])classList.toArray(new Class[0]);
    
    List<Item> itemList = new ArrayList<Item>();
    for (String itemName : itemInstWhitelist)
    {
    	Item itm = Item.REGISTRY.getObject(new ResourceLocation(itemName));
    	if (itm != null)
    	{
    		itemList.add(itm);
    	}
    } 
    itemInstWhiteArray = (Item[])itemList.toArray(new Item[0]);
  }
  
  /* config weapon */
  public static boolean isItemAttackUsable(Item item)
  {
	  if ( Arrays.stream(itemInstWhiteArray).anyMatch(blItem -> (blItem == item)) )
	  {
		  return true;
	  }
	  return Arrays.stream(itemClassWhiteArray).anyMatch(wlClass -> wlClass.isInstance(item));
  }

  
  public static boolean isEntityAttackable(Entity entity) { return Arrays.stream(entityBlackArray).noneMatch(eClass -> eClass.isInstance(entity)); }

  
  private static String[] itemInstWhitelist = new String[0];
  private static String[] entityBlacklist = { "net.minecraft.entity.passive.EntityHorse", "net.minecraft.entity.item.EntityArmorStand", "net.minecraft.entity.passive.EntityVillager", "net.torocraft.toroquest.entity.EntityGuard", "net.torocraft.toroquest.entity.EntityVillageLord", "net.torocraft.toroquest.entity.EntityToroNpc", "net.torocraft.toroquest.entity.EntityToroVillager" };
  
  private static final String[] ICW_DEF = (String[])Arrays.copyOf(itemClassWhitelist, itemClassWhitelist.length);
  private static final String[] IIW_DEF = (String[])Arrays.copyOf(itemInstWhitelist, itemInstWhitelist.length);
  private static final String[] EB_DEF = (String[])Arrays.copyOf(entityBlacklist, entityBlacklist.length);
  
  private static Class<?>[] itemClassWhiteArray;
  private static Item[] itemInstWhiteArray;
  private static Class<?>[] entityBlackArray;
  
//  @SubscribeEvent
//  public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
//    if ("bettercombatmod".equalsIgnoreCase(event.getModID())) {
//      loadConfiguration();
//      createInstLists();
//    } 
//  }
}