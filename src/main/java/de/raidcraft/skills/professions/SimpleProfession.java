package de.raidcraft.skills.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientManager;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.util.ConfigUtil;
import de.slikey.effectlib.effect.CylinderEffect;
import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Particle;

/**
 * Represents a profession instantiated for one {@link Hero}.
 * Each single {@link Hero} can have multiple {@link Profession}s at a time. The
 * {@link Hero} will hold a reference to all {@link Profession}s obtained by the hero.
 *
 * @author Silthus
 */
public class SimpleProfession extends AbstractProfession {


    public SimpleProfession(Hero hero, ProfessionProperties properties, Path<Profession> path, Profession parent, THeroProfession database) {

        super(hero, properties, path, parent, database);
    }

    @Override
    public void onExpGain(int exp) {

        // lets convert some exp into money
        double conversionRate = ConfigUtil.getTotalValue(this, getProperties().getExpMoneyConversionRate());
        Economy economy = RaidCraft.getEconomy();
        if (economy != null) {
            economy.modify(getHero().getPlayer().getUniqueId(), exp * conversionRate, BalanceSource.SKILL, "Berufseinkommen");
        }
        getHero().getUserInterface().refresh();
    }

    @Override
    public void onExpLoss(int exp) {

        getHero().getUserInterface().refresh();
    }

    @Override
    public void onLevelGain() {

        CylinderEffect effect = new CylinderEffect(AmbientManager.getEffectManager());
        effect.setEntity(getHero().getEntity());
        effect.particle = Particle.FLAME;
        effect.solid = true;
        effect.run();
        // lets update the max health
        getHero().recalculateHealth();
        getHero().sendMessage(ChatColor.GREEN + "Du bist ein Level aufgestiegen: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getTotalLevel());
        checkSkillsForUnlock();
        getHero().reset();
    }

    @Override
    public void onLevelLoss() {

        getHero().sendMessage(ChatColor.RED + "Du bist ein Level abgestiegen: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getTotalLevel());
        checkSkillsForUnlock();
        getHero().getUserInterface().refresh();
    }
}
