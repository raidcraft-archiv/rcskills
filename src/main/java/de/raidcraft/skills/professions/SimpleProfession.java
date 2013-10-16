package de.raidcraft.skills.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.logging.ExpLogger;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.ChatColor;

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
            economy.modify(getHero().getName(), exp * conversionRate, BalanceSource.SKILL, "Berufseinkommen");
        }

        ExpLogger.log(this, exp);
        getHero().getUserInterface().refresh();
    }

    @Override
    public void onExpLoss(int exp) {

        ExpLogger.log(this, -exp);
        getHero().getUserInterface().refresh();
    }

    @Override
    public void onLevelGain() {

        // lets update the max health
        getHero().setMaxHealth(getHero().getDefaultHealth());
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
