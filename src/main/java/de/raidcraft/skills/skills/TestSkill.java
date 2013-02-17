package de.raidcraft.skills.skills;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.EffectUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "Test",
        desc = "Skill is for testing stuff..."
)
public class TestSkill extends AbstractSkill implements CommandTriggered {

    private int i = 0;
    private BukkitTask task;

    public TestSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void runCommand(CommandContext args) throws CombatException {

        final int radius = 10;
        final Location center = getHero().getBlockTarget();
        final List<Location> circle = EffectUtil.circle(center, radius, 1, true, false, 10);
        final World world = getHero().getPlayer().getWorld();
        final FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.BLUE).build();

        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
            @Override
            public void run() {

                if (i < circle.size()) {
                    EffectUtil.playFirework(world, circle.get(i), effect);
                    i++;
                } else {
                    Entity[] entities = LocationUtil.getNearbyEntities(center, radius);
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity) {
                            CharacterTemplate character = RaidCraft.getComponent(SkillsPlugin.class)
                                    .getCharacterManager().getCharacter((LivingEntity) entity);
                            try {
                                new MagicalAttack(getHero(), character, getTotalDamage()).run();
                                world.strikeLightningEffect(entity.getLocation());
                            } catch (CombatException e) {
                                getHero().sendMessage(ChatColor.RED + e.getMessage());
                            }
                        }
                    }
                    i = 0;
                    task.cancel();
                }
            }
        }, 1L, 1L);
    }
}
