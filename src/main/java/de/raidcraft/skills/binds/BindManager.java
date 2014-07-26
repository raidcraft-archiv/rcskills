package de.raidcraft.skills.binds;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.reference.Colors;
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
 * Manages and holds binds.
 */
public class BindManager {

    private final Hero hero;
    private Map<Material, Integer> iterators = new HashMap<>(5);
    private Map<Material, ArrayList<BindWrapper>> binds = new HashMap<>(5);

    public BindManager(Hero hero) {

        this.hero = hero;
    }

    /**
     * Bind a skill to the given item material.
     *
     * @param material The item material to bind to
     * @param skill    The skill to be bound
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

            addToList(material, new BindWrapper(skill, new CommandContext(arg)));
        } catch (CommandException e) {
            hero.sendMessage(Colors.Chat.ERROR + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private void addToList(Material material, BindWrapper bindWrapper) {

        ArrayList<BindWrapper> bindWrapperArrayList;

        if (binds.containsKey(material)) {

            bindWrapperArrayList = binds.get(material);
            bindWrapperArrayList.add(bindWrapper);
        } else {

            bindWrapperArrayList = new ArrayList<>();
            bindWrapperArrayList.add(bindWrapper);
            iterators.put(material, 0);
            binds.put(material, bindWrapperArrayList);
        }
    }

    /**
     * Unbinds all skills from the given item material.
     *
     * @param material The item material to unbind
     *
     * @return true if success, otherwise false
     */
    public boolean remove(@NonNull Material material) {

        if (!binds.containsKey(material)) {
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
        binds.remove(material);

        if (iterators.containsKey(material)) {
            iterators.remove(material);
        }

        return true;
    }

    /**
     * Reloads all binds from the database.
     */
    public void reload() {

        clear();
        load();
    }

    /**
     * Removes all bound skills from each item material.
     */
    public void clear() {

        clear(false);
    }

    /**
     * Load all binds from the database.
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

                addToList(material, new BindWrapper(skill, new CommandContext(result.getArgs())));

            } catch (UnknownSkillException | NullPointerException e) {
                RaidCraft.getDatabase(SkillsPlugin.class).delete(result);
            } catch (CommandException e) {
                // no operation
            }
        });
    }

    /**
     * Removes all bound skills from each item material.
     *
     * @param permanent true to delete all entries from the database
     */
    public void clear(boolean permanent) {

        binds.clear();

        if (permanent) {

            String sql = "DELETE FROM skills_bindings WHERE ownerId = :owner_id";
            SqlUpdate update = RaidCraft.getDatabase(SkillsPlugin.class).createSqlUpdate(sql);
            update.setParameter("ownerId", hero.getId());
            update.execute();
        }
    }

    /**
     * Returns true if the list contains no binds.
     *
     * @return true if the list contains no binds
     */
    public boolean isEmpty() {

        return binds.isEmpty();
    }

    /**
     * Returns true if a skill is bound to the given item material.
     *
     * @param material The item material
     *
     * @return true if a skill is attached to the specified item material
     */
    public boolean contains(Material material) {

        return !binds.isEmpty() && binds.containsKey(material);
    }

    /**
     * Retrieves the currently pointed wrapper of the skill bound to the given item material.
     *
     * @param material The item material
     *
     * @return the wrapper of the specified item material currently referenced by the internal pointer, or null if none
     */
    public BindWrapper getWrapper(Material material) {

        if (!binds.containsKey(material) || !iterators.containsKey(material)) {
            return null;
        }

        return binds.get(material).get(iterators.get(material));
    }

    /**
     * Moves the internal pointer of the given item material to its next attached skill.
     *
     * @param material The item material
     * @param forward  true to move forward, false to move backwards
     *
     * @return the wrapper currently referenced by the internal pointer, or null if none
     */
    public BindWrapper switchSkill(Material material, boolean forward) {

        if (binds.containsKey(material) && binds.get(material).size() > 1 && iterators.containsKey(material)) {

            if (forward) {

                iterators.put(material,
                        binds.get(material).listIterator(iterators.get(material)).nextIndex() < binds.get(material).size() - 1 ?
                                iterators.get(material) + 1 : 0
                );
            } else {

                iterators.put(material,
                        binds.get(material).listIterator(iterators.get(material)).hasPrevious() ?
                                binds.get(material).listIterator(iterators.get(material)).previousIndex() :
                                binds.get(material).size() - 1
                );
            }

            return binds.get(material).get(iterators.get(material));
        }

        return null;
    }

}
