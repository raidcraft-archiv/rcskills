package de.raidcraft.skills.skills;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.EffectUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "Test",
        description = "Skill is for testing stuff..."
)
public class TestSkill extends AbstractSkill implements CommandTriggered {

    private EffectUtil.Particle particle;

    public TestSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void load(ConfigurationSection data) {

        particle = EffectUtil.Particle.valueOf(data.getString("type"));
    }

    @Override
    public void runCommand(CommandContext args) throws CombatException {

        EffectUtil.fakeParticles(particle, getBlockTarget(), 10);
    }
}
