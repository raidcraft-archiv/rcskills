package de.raidcraft.skills.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class ProfessionUtil {

    private ProfessionUtil() {

    }

    public static Profession getProfessionFromArgs(Hero hero, String input) throws CommandException {

        input = input.toLowerCase();
        List<Profession> professions = new ArrayList<>();
        for (Profession profession : RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getAllProfessions(hero)) {
            if (profession.getName().contains(input) || profession.getProperties().getFriendlyName().toLowerCase().contains(input)) {
                professions.add(profession);
            }
        }

        if (professions.size() < 1) {
            throw new CommandException("Es gibt keinen Beruf/Klasse mit dem Namen: " + input);
        }

        if (professions.size() > 1) {
            throw new CommandException(
                    "Es gibt mehrere Berufe/Klassen mit dem Namen " + input + ":" + StringUtil.joinString(professions, ", ", 0));
        }

        return professions.get(0);
    }
}
