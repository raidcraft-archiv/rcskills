package de.raidcraft.skills.bindings;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
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
    private Map<Material, ArrayList<BindingWrapper>> bindings = new HashMap<>(5);

    public BindingManager(Hero hero) {

        this.hero = hero;
    }

    /**
     * Bind a skill on an item material.
     *
     * @param material The material of the item
     * @param skill    The skill to bind
     * @param arg      The argument to commit
     *
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

            if (result != null) {
                return false;
            }

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

            addToList(material, new BindingWrapper(skill, new CommandContext(arg)));
        } catch (CommandException e) {

            // no operation
        }

        return true;
    }

    private void addToList(Material material, BindingWrapper bindingWrapper) {

        ArrayList<BindingWrapper> bindingWrapperArrayList;

        if (bindings.containsKey(material)) {

            bindingWrapperArrayList = bindings.get(material);
            bindingWrapperArrayList.add(bindingWrapper);
        } else {

            bindingWrapperArrayList = new ArrayList<>();
            bindingWrapperArrayList.add(bindingWrapper);
            iterators.put(material, 0);
            bindings.put(material, bindingWrapperArrayList);
        }
    }

    /**
     * Unbind any skill from an item material.
     *
     * @param material The item material to unbind the skills from.
     *
     * @return true if success, otherwise false
     */
    public boolean remove(@NonNull Material material) {

        if (!bindings.containsKey(material)) {
            return false;
        }

        String item = material.toString();
        @NonNull Integer heroId = hero.getId();

        List<TBinding> result = RaidCraft.getDatabase(SkillsPlugin.class)
                .find(TBinding.class)
                .where()
                .eq("ownerId", heroId)
                .eq("item", item)
                .findList();

        if (result.isEmpty()) {
            return false;
        }

        result.stream().forEach(r -> RaidCraft.getDatabase(SkillsPlugin.class).delete(r));
        bindings.remove(material);

        if (iterators.containsKey(material)) {
            iterators.remove(material);
        }

        return true;
    }

    /**
     * Reloads all bindings from the database.
     */
    public void reload() {

        clear();
        load();
    }

    /**
     * Remove all bindings
     */
    public void clear() {

        clear(false);
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

        if (CollectionUtils.isEmpty(results)) {
            return;
        }

        results.stream().forEach(result -> {
            try {

                Skill skill = hero.getSkill(result.getSkill());
                Material material = Material.getMaterial(result.getItem());

                addToList(material, new BindingWrapper(skill, new CommandContext(result.getArgs())));

            } catch (UnknownSkillException | NullPointerException e) {
                RaidCraft.getDatabase(SkillsPlugin.class).delete(result);
            } catch (CommandException e) {
                // no operation
            }
        });
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
     * Returns true if the list contains no bindings.
     *
     * @return true if the binding list contains no elements, otherwise false
     */
    public boolean isEmpty() {

        return bindings.isEmpty();
    }

    /**
     * Returns true if bindings know about the specified material.
     *
     * @param material The item material
     *
     * @return true if bindings contains the specified material, otherwise false
     */
    public boolean containsMaterial(Material material) {

        return !bindings.isEmpty() && bindings.containsKey(material);
    }

    /**
     * Returns the bound wrapper which the specified item is mapped, or null if there is no mapping for the item.
     *
     * @param material The item material whose associated skill action is to be returned
     *
     * @return The wrapper to which the specified item material is mapped, or null if there is no mapping for the item material
     */
    public BindingWrapper getBindingWrapper(Material material) {

        if (!bindings.containsKey(material) || !iterators.containsKey(material)) {
            return null;
        }

        return bindings.get(material).get(iterators.get(material));
    }

    /**
     * Switch the bound skill on the given item material.
     *
     * @param material The item material
     * @param forward  true to move forward, false to move backwards
     *
     * @return the current selected wrapper, otherwise null
     */
    public BindingWrapper switchSkill(Material material, boolean forward) {

        if (bindings.containsKey(material) && bindings.get(material).size() > 1 && iterators.containsKey(material)) {

            if (forward) {

                iterators.put(material,
                        bindings.get(material).listIterator(iterators.get(material)).nextIndex() < bindings.get(material).size() - 1 ?
                                iterators.get(material) + 1 : 0
                );
            } else {

                iterators.put(material,
                        bindings.get(material).listIterator(iterators.get(material)).hasPrevious() ?
                                bindings.get(material).listIterator(iterators.get(material)).previousIndex() :
                                bindings.get(material).size() - 1
                );
            }

            return bindings.get(material).get(iterators.get(material));
        }

        return null;
    }

}
