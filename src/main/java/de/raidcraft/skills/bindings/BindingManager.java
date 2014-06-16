package de.raidcraft.skills.bindings;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.TBinding;
import de.raidcraft.skills.util.CollectionUtils;
import lombok.NonNull;
import org.bukkit.Material;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages and holds bindings.
 */
public class BindingManager {

    private final Hero hero;
    private Map<Material, Integer> iterators = new HashMap<>(5);
    private Map<Material, ArrayList<SkillAction>> bindings = new HashMap<>(5);

    public BindingManager(Hero hero) {

        this.hero = hero;
    }

    /**
     * Bind a skill on an item material.
     *
     * @param material The material of the item
     * @param skill    The skill to bind
     * @param arg      The argument to commit
     * @return true if success, otherwise false
     */
    public boolean add(@NonNull Material material, @NonNull Skill skill, String arg) {

        String item = material.toString();
        @NonNull String stSkill = skill.getName();
        @NonNull Integer heroId = hero.getId();
        arg = arg == null ? "" : arg;

        try {

            TBinding result = RaidCraft.getDatabase(SkillsPlugin.class)
                    .find(TBinding.class)
                    .where()
                    .eq("ownerId", heroId)
                    .eq("item", item)
                    .eq("skill", stSkill)
                    .findUnique();

            if (result != null)
                return false;

        } catch (PersistenceException e) {

            e.printStackTrace();
            return false;
        }

        TBinding binding = new TBinding();
        binding.setOwnerId(heroId);
        binding.setItem(item);
        binding.setSkill(stSkill);
        binding.setArgs(arg);

        RaidCraft.getDatabase(SkillsPlugin.class).save(binding);

        try {

            addToList(material, new SkillAction(skill, new CommandContext(arg)));
        } catch (CommandException e) {

            // no operation
        }

        return true;
    }

    /**
     * Unbind any skill from an item material.
     *
     * @param material The item material to unbind the skills from.
     * @return true if success, otherwise false
     */
    public boolean remove(@NonNull Material material) {

        if (!bindings.containsKey(material))
            return false;

        String item = material.toString();
        @NonNull Integer heroId = hero.getId();

        List<TBinding> result = RaidCraft.getDatabase(SkillsPlugin.class)
                .find(TBinding.class)
                .where()
                .eq("ownerId", heroId)
                .eq("item", item)
                .findList();

        if (result.isEmpty())
            return false;

        result.stream().forEach(r -> RaidCraft.getDatabase(SkillsPlugin.class).delete(r));
        bindings.remove(material);

        if (iterators.containsKey(material))
            iterators.remove(material);

        return true;
    }

    /**
     * Remove all bindings
     */
    public void clear() {

        clear(false);
    }

    /**
     * Remove all bindings
     *
     * @param permanent true to delete all entries from the database
     */
    public void clear(boolean permanent) {

        bindings.clear();

        if (permanent) {

            String sql = "DELETE FROM skills_bindings WHERE ownerId = :owner_id";
            SqlUpdate update = RaidCraft.getDatabase(SkillsPlugin.class).createSqlUpdate(sql);
            update.setParameter("ownerId", hero.getId());
            update.execute();
        }
    }

    /**
     * Reloads all bindings from the database.
     */
    public void reload() {

        clear();
        load();
    }

    /**
     * Load all bindings from the database.
     */
    public void load() {

        @NonNull Integer heroId = hero.getId();

        List<TBinding> results = RaidCraft.getDatabase(SkillsPlugin.class)
                .find(TBinding.class)
                .where()
                .eq("ownerId", heroId)
                .findList();

        if (CollectionUtils.isEmpty(results))
            return;

        results.stream().forEach(result -> {
            try {

                Skill skill = hero.getSkill(result.getSkill());
                Material material = Material.getMaterial(result.getItem());

                addToList(material, new SkillAction(skill, new CommandContext(result.getArgs())));

            } catch (UnknownSkillException | NullPointerException e) {
                RaidCraft.getDatabase(SkillsPlugin.class).delete(result);
            } catch (CommandException e) {
                // no operation
            }
        });
    }

    /**
     * Returns true if the list contains no bindings.
     *
     * @return true if the binding list contains no elements, else false
     */
    public boolean isEmpty() {
        return bindings.isEmpty();
    }

    /**
     * Returns the bound skill action which the specified item is mapped, or null if there is no mapping for the item.
     *
     * @param material The item material whose associated skill action is to be returned
     * @return The skill action to which the specified item material is mapped, or null if there is no mapping for the item material
     */
    public SkillAction getSkillAction(Material material) {

        if (!bindings.containsKey(material) || !iterators.containsKey(material))
            return null;

        return bindings.get(material).get(iterators.get(material));
    }

    /**
     * Switch the bound skill on the given item material.
     *
     * @param material The item material
     * @param forward  true to move forward, false to move backwards
     * @return the current selected action skill, otherwise null
     */
    public SkillAction switchSkill(Material material, boolean forward) {

        if (bindings.containsKey(material) && bindings.get(material).size() > 1 && iterators.containsKey(material)) {

            if (forward) {

                iterators.put(material,
                        bindings.get(material).listIterator(iterators.get(material)).nextIndex() < bindings.get(material).size() - 1 ?
                                iterators.get(material) + 1 : 0);
            } else {

                iterators.put(material,
                        bindings.get(material).listIterator(iterators.get(material)).hasPrevious() ?
                                bindings.get(material).listIterator(iterators.get(material)).previousIndex() :
                                bindings.get(material).size() - 1);
            }

            return bindings.get(material).get(iterators.get(material));
        }

        return null;
    }

    private void addToList(Material material, SkillAction skillAction) {
        ArrayList<SkillAction> skillActionList;

        if (bindings.containsKey(material)) {

            skillActionList = bindings.get(material);
            skillActionList.add(skillAction);
        } else {

            skillActionList = new ArrayList<>();
            skillActionList.add(skillAction);
            iterators.put(material, 0);
            bindings.put(material, skillActionList);
        }
    }

}
