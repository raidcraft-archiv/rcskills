package de.raidcraft.skills.api.party;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleParty implements Party {

    private CharacterTemplate owner;
    private final Set<CharacterTemplate> members = new HashSet<>();
    private final Map<Hero, BukkitTask> invitedMembers = new HashMap<>();

    public SimpleParty(CharacterTemplate owner) {

        this.owner = owner;
        members.add(owner);
    }

    @Override
    public CharacterTemplate getOwner() {

        return owner;
    }

    @Override
    public boolean isOwner(CharacterTemplate character) {

        return character.equals(getOwner());
    }

    @Override
    public void setOwner(CharacterTemplate owner) {

        this.owner = owner;
        addMember(owner);
    }

    @Override
    public void sendMessage(String... msg) {

        for (CharacterTemplate member : members) {
            if (member instanceof Hero) {
                ((Hero) member).sendMessage(msg);
            }
        }
    }

    @Override
    public Set<CharacterTemplate> getMembers() {

        return members;
    }

    @Override
    public Set<Hero> getHeroes() {

        HashSet<Hero> heros = new HashSet<>();
        for (CharacterTemplate character : members) {
            if (character instanceof Hero) {
                heros.add((Hero) character);
            }
        }
        return heros;
    }

    @Override
    public void addMember(CharacterTemplate member) {

        members.add(member);
        member.joinParty(this);
        if (member instanceof Hero) {
            ((Hero) member).setPendingPartyInvite(null);
            BukkitTask bukkitTask = invitedMembers.remove(member);
            if (bukkitTask != null) bukkitTask.cancel();
        }
        for (Hero template : getHeroes()) {
            CharacterManager.refreshPlayerTag(template);
        }
    }

    @Override
    public void inviteMember(final Hero member) {

        member.sendMessage(ChatColor.YELLOW + getOwner().getName() + " hat dich in eine Gruppe eingeladen.",
                ChatColor.YELLOW + "Gebe " + ChatColor.GREEN + "/party accept" + ChatColor.YELLOW + " ein um die Einladung anzunehmen." +
                        " Gebe " + ChatColor.DARK_RED +"/party deny" + ChatColor.YELLOW + " ein um abzulehnen.");
        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                if (getOwner() instanceof Hero) {
                    ((Hero) getOwner()).sendMessage(ChatColor.RED + member.getName() + " hat die Gruppeneinladung nicht angenommen.");
                }
                member.sendMessage(ChatColor.RED + getOwner().getName() + " hat dich in eine Gruppe eingeladen während du AFK warst.");
                invitedMembers.remove(member);
                member.setPendingPartyInvite(null);
            }
        }, TimeUtil.secondsToTicks(plugin.getCommonConfig().invite_timeout));
        invitedMembers.put(member, task);
        member.setPendingPartyInvite(this);
    }

    @Override
    public boolean isInvited(Hero hero) {

        return invitedMembers.containsKey(hero);
    }

    @Override
    public void removeMember(CharacterTemplate member) {

        if (members.remove(member)) {
            member.leaveParty();
            if (member instanceof Hero) {
                sendMessage(ChatColor.YELLOW + member.getName() + " hat die Gruppe verlassen.");
            }
        }
        if (member instanceof Hero) {
            ((Hero) member).setPendingPartyInvite(null);
            if (invitedMembers.containsKey(member)) {
                invitedMembers.remove(member).cancel();
                if (getOwner() instanceof Hero) {
                    ((Hero) getOwner()).sendMessage(ChatColor.RED + member.getName() + " hat die Gruppeneinladung abgelehnt.");
                }
            }
        }
        if (getHeroes().size() < 2) {
            dispandParty();
        }
        CharacterManager.refreshPlayerTag(member);
    }

    @Override
    public void kickMember(Hero hero) {

        hero.sendMessage(ChatColor.RED + "Du wurdest von " + getOwner().getName() + " aus der Gruppe geworfen.");
        removeMember(hero);
        sendMessage(ChatColor.RED + hero.getName() + " wurde von " + getOwner().getName() + " aus der Gruppe geworfen.");
    }

    @Override
    public void dispandParty() {

        sendMessage(ChatColor.RED + "Die Gruppe wurde aufgelöst.");
        for (CharacterTemplate member : new HashSet<>(getMembers())) {
            removeMember(member);
        }
    }

    @Override
    public boolean isInGroup(CharacterTemplate member) {

        return members.contains(member);
    }

    @Override
    public <S> void heal(S source, int amount) {

        for (CharacterTemplate hero : getMembers()) {
            try {
                new HealAction<>(source, hero, amount).run();
            } catch (CombatException e) {
                if (source instanceof Hero) {
                    ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
    }
}
