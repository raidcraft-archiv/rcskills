package de.raidcraft.skills.api.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    private final Hero hero;
    private final Map<String, RefreshingDisplay> refreshingDisplays = new CaseInsensitiveMap<>();
    private final Map<CharacterTemplate, HealthDisplay> healthDisplays = new HashMap<>();

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public void removeSidebarScore(String name) {

        if (!getHero().isOnline()) return;
        Score score = Scoreboards.getPlayerSidebarObjective(getHero()).getScore(name);
        score.getScoreboard().resetScores(score.getPlayer());
    }

    @Override
    public void updateSidebarScore(String name, int score) {

        if (!getHero().isOnline()) return;
        Scoreboards.getPlayerSidebarObjective(getHero()).getScore(name).setScore(score);
    }

    @Override
    public void addEffect(Effect effect, final int duration) {

        if (!isValidEffect(effect)) {
            return;
        }
        RefreshingEffectDisplay display = new RefreshingEffectDisplay(effect, this, duration);
        refreshingDisplays.put(effect.getName(), display);
    }

    @Override
    public void renewEffect(Effect effect, int duration) {

        if (!isValidEffect(effect)) {
            return;
        }
        if (refreshingDisplays.containsKey(effect.getName())) {
            refreshingDisplays.get(effect.getName()).setRemainingDuration(duration);
        } else {
            addEffect(effect, duration);
        }
    }

    @Override
    public void removeEffect(Effect effect) {

        if (!isValidEffect(effect)) {
            return;
        }
        RefreshingDisplay display = refreshingDisplays.remove(effect.getName());
        if (display != null) {
            display.setRemainingDuration(0);
        }
    }

    private boolean isValidEffect(Effect effect) {

        for (String type : RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().getStringList("sidebar-effect-types")) {
            EffectType effectType = EffectType.fromString(type);
            if (effectType != null && effect.isOfType(effectType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void refresh() {

        if (!hero.isOnline()
                || hero.getPlayer().isDead()
                || hero.getHealth() < 1) {
            return;
        }

        if (Option.SIDEBAR_PARTY_HP.isSet(getHero())) {
            // lets update the sidebar display with the party information
            Set<Map.Entry<CharacterTemplate, HealthDisplay>> entries = healthDisplays.entrySet();
            for (Map.Entry<CharacterTemplate, HealthDisplay> entry : entries) {
                if (!getHero().getParty().contains(entry.getKey())) {
                    entry.getValue().remove();
                    entries.remove(entry);
                }
            }
            // we need to add missing players to the party display
            for (CharacterTemplate partyMember : getHero().getParty().getHeroes()) {
                if (!partyMember.equals(getHero()) && !healthDisplays.containsKey(partyMember)) {
                    PartyHealthDisplay display = new PartyHealthDisplay(getHero(), partyMember);
                    display.refresh();
                    healthDisplays.put(partyMember, display);
                }
            }
        } else {
            // lets remove all old parties
            for (HealthDisplay display : healthDisplays.values()) {
                display.remove();
            }
            healthDisplays.clear();
        }

        for (RefreshingDisplay display : new ArrayList<>(refreshingDisplays.values())) {
            if (display instanceof RefreshingEffectDisplay && display.getRemainingDuration() < 1) {
                refreshingDisplays.remove(((RefreshingEffectDisplay) display).getEffect().getName());
            }
        }
        Scoreboards.getScoreboard(getHero()).getPlayerTeam(getHero().getPlayer());
        // lets update the scoreboard
        updateHealthDisplay();
        // make sure the food level is never at 20 to allow eating
        if (hero.getPlayer().getFoodLevel() > 19) {
            hero.getPlayer().setFoodLevel(19);
        }
    }

    private void updateHealthDisplay() {

        Scoreboards.updateHealthDisplays();
        // update what the player sees in his exp bar
        getHero().getPlayer().setLevel((int) getHero().getHealth());
    }
}