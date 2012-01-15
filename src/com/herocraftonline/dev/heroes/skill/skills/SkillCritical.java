package com.herocraftonline.dev.heroes.skill.skills;


import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillCritical extends PassiveSkill {

    public SkillCritical(Heroes plugin) {
        super(plugin, "Critical");
        setDescription("Passive $1% chance to do $2 times damage.");
        setTypes(SkillType.COUNTER, SkillType.BUFF);
        
        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(this), Priority.Normal);
    }

    @Override
    public String getDescription(Hero hero) {
        double chance = (SkillConfigManager.getUseSetting(hero, this, Setting.CHANCE.node(), 0.2, false) +
                (SkillConfigManager.getUseSetting(hero, this, Setting.CHANCE_LEVEL.node(), 0.0, false) * hero.getLevel())) * 100;
        chance = chance > 0 ? chance : 0;
        double damageMod = (SkillConfigManager.getUseSetting(hero, this, "damage-multiplier", 0.2, false) +
                (SkillConfigManager.getUseSetting(hero, this, "damage-multiplier-increase", 0.0, false) * hero.getLevel()));
        damageMod = damageMod > 0 ? damageMod : 0;
        String description = getDescription().replace("$1", chance + "").replace("$2", damageMod + "");
        return description;
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.CHANCE.node(), 0.2);
        node.set(Setting.CHANCE_LEVEL.node(), 0);
        node.set("damage-multiplier", 2.0);
        node.set("damage-multiplier-increase", 0);
        return node;
    }
    
    public class SkillHeroListener extends HeroesEventListener {
        private Skill skill;
        public SkillHeroListener(Skill skill) {
            this.skill = skill;
        }
        
        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled() ||event.getCause() != DamageCause.ENTITY_ATTACK || !(event.getEntity() instanceof Player) ||
                    event.getDamage() == 0)
                return;
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                Hero hero = plugin.getHeroManager().getHero(player);

                if (hero.hasEffect("Critical")) {
                    double chance = (SkillConfigManager.getUseSetting(hero, skill, Setting.CHANCE.node(), 0.2, false) +
                            (SkillConfigManager.getUseSetting(hero, skill, Setting.CHANCE_LEVEL.node(), 0.0, false) * hero.getLevel())) * 100;
                    chance = chance > 0 ? chance : 0;
                    if (Math.random() <= chance) {
                        double damageMod = (SkillConfigManager.getUseSetting(hero, skill, "damage-multiplier", 0.2, false) +
                                (SkillConfigManager.getUseSetting(hero, skill, "damage-multiplier-increase", 0.0, false) * hero.getLevel()));
                        damageMod = damageMod > 0 ? damageMod : 0;
                        event.setDamage((int) (event.getDamage() * damageMod));
                    }
                }
            } else if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                    Hero hero = plugin.getHeroManager().getHero(player);

                    if (hero.hasEffect("Critical")) {
                        double chance = (SkillConfigManager.getUseSetting(hero, skill, Setting.CHANCE.node(), 0.2, false) +
                                (SkillConfigManager.getUseSetting(hero, skill, Setting.CHANCE_LEVEL.node(), 0.0, false) * hero.getLevel())) * 100;
                        chance = chance > 0 ? chance : 0;
                        if (Math.random() <= chance) {
                            double damageMod = (SkillConfigManager.getUseSetting(hero, skill, "damage-multiplier", 0.2, false) +
                                    (SkillConfigManager.getUseSetting(hero, skill, "damage-multiplier-increase", 0.0, false) * hero.getLevel()));
                            damageMod = damageMod > 0 ? damageMod : 0;
                            event.setDamage((int) (event.getDamage() * damageMod));
                            
                        }
                    }
                }
            }
        }
    }
}