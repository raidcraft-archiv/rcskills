package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    public static final String HEALTH_OBJECTIVE = "rcshp";

    private final Hero hero;
    private final Map<String, EffectDisplay> displayedEffects = new CaseInsensitiveMap<>();

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public void addEffect(Effect effect, final int duration) {

        if (!isValidEffect(effect)) {
            return;
        }
        final Score score = Scoreboards.getPlayerSidebarObjective(getHero()).getScore(getEffectScore(effect));
        EffectDisplay display = new EffectDisplay(effect, score, duration);
        displayedEffects.put(effect.getName(), display);
    }

    @Override
    public void renewEffect(Effect effect, int duration) {

        if (!isValidEffect(effect)) {
            return;
        }
        if (displayedEffects.containsKey(effect.getName())) {
            displayedEffects.get(effect.getName()).setRemainingDuration(duration);
        } else {
            addEffect(effect, duration);
        }
    }

    @Override
    public void removeEffect(Effect effect) {

        if (!isValidEffect(effect)) {
            return;
        }
        EffectDisplay display = displayedEffects.remove(effect.getName());
        if (display != null) {
            display.setRemainingDuration(0);
        }
    }

    private boolean isValidEffect(Effect effect) {

        return effect.isOfType(EffectType.HARMFUL)
                || effect.isOfType(EffectType.HELPFUL);
    }

    private OfflinePlayer getEffectScore(Effect effect) {

        ChatColor color = ChatColor.WHITE;
        if (effect.isOfType(EffectType.HELPFUL)) {
            color = ChatColor.GREEN;
        } else if (effect.isOfType(EffectType.HARMFUL)) {
            color = ChatColor.RED;
        }
        return Bukkit.getOfflinePlayer(color + effect.getFriendlyName());
    }

    @Override
    public void refresh() {

        if (!hero.isOnline()
                || hero.getPlayer().isDead()
                || hero.getHealth() < 1) {
            return;
        }

        for (EffectDisplay display : new ArrayList<>(displayedEffects.values())) {
            if (display.getRemainingDuration() < 1) {
                displayedEffects.remove(display.getEffect().getName());
            }
        }
        // lets update the scoreboard
        updateHealthDisplay();
        // make sure the food level is never at 20 to allow eating
        if (hero.getPlayer().getFoodLevel() > 19) {
            hero.getPlayer().setFoodLevel(19);
        }
    }

    private void updateHealthDisplay() {

        // update what others see
        Objective objective = getScoreboardHealthObjective();
        for (Player player : Bukkit.getOnlinePlayers()) {
            objective.getScore(player).setScore((int) player.getHealth());
        }
        // update what the player sees
        getHero().getPlayer().setLevel(getHero().getHealth());
    }

    private Objective getScoreboardHealthObjective() {

        // lets also set the scoreboard to display the health of this player to all online players
        Scoreboard scoreboard = Scoreboards.getScoreboard(hero);

        Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE, "health");
            objective.setDisplayName(ChatColor.RED + "❤");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return objective;
    }
}
