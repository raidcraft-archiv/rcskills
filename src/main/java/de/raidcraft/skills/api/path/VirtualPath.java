package de.raidcraft.skills.api.path;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class VirtualPath implements Path<Profession> {

    @Override
    public String getName() {

        return ProfessionManager.VIRTUAL_PROFESSION.toLowerCase();
    }

    @Override
    public String getFriendlyName() {

        return ProfessionManager.VIRTUAL_PROFESSION;
    }

    @Override
    public List<Profession> getParents(Hero hero) {

        ArrayList<Profession> professions = new ArrayList<>();
        professions.add(RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getVirtualProfession(hero));
        return professions;
    }

    @Override
    public List<String> getParents() {

        ArrayList<String> professions = new ArrayList<>();
        professions.add(ProfessionManager.VIRTUAL_PROFESSION);
        return professions;
    }
}
