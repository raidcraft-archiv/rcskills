package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ExperienceConfig extends ConfigurationBase<SkillsPlugin> {

    public static final String CONFIG_NAME = "experience.yml";
    private static final String ENTITIES = "entities";
    private static final String BLOCKS = "blocks";
    private static final String CRAFTING = "crafting";

    private final Map<EntityType, Integer> entities = new HashMap<>();
    private final Map<Material, Integer> blocks = new HashMap<>();
    private final Map<Material, Integer> crafting = new HashMap<>();

    public ExperienceConfig(SkillsPlugin plugin) {

        super(plugin, CONFIG_NAME);
    }

    @Override
    public void load() {

        super.load();
        entities.putAll(formatEntities(loadExperience(ENTITIES)));
        blocks.putAll(formatItems(loadExperience(BLOCKS)));
        crafting.putAll(formatItems(loadExperience(CRAFTING)));
    }

    private Map<EntityType, Integer> formatEntities(Map<String, Integer> map) {

        Map<EntityType, Integer> entities = new HashMap<>();
        if (map != null) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                EntityType type = EntityType.fromName(entry.getKey());
                if (type == null) {
                    getPlugin().getLogger().warning("Wrong Entity Name in experience config: " + entry.getKey());
                } else {
                    entities.put(type, entry.getValue());
                }
            }
        }
        return entities;
    }

    private Map<String, Integer> loadExperience(String key) {

        if (!contains(key)) return null;

        Map<String, Integer> map = new HashMap<>();
        for (String type : getConfigurationSection(key).getKeys(false)) {
            map.put(type, getInt(key + "." + type));
        }
        return map;
    }

    private Map<Material, Integer> formatItems(Map<String, Integer> map) {

        Map<Material, Integer> materials = new HashMap<>();
        if (map != null) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Material type = Material.matchMaterial(entry.getKey());
                if (type == null) {
                    getPlugin().getLogger().warning("Wrong Material Name in experience config: " + entry.getKey());
                } else {
                    materials.put(type, entry.getValue());
                }
            }
        }
        return materials;
    }

    public int getBlockExperienceFor(Material material) {

        if (blocks.containsKey(material)) {
            return blocks.get(material);
        }
        return 0;
    }

    public int getEntityExperienceFor(EntityType entityType) {

        if (entities.containsKey(entityType)) {
            return entities.get(entityType);
        }
        return 0;
    }

    public int getCraftingExperienceFor(Material material) {

        if (crafting.containsKey(material)) {
            return crafting.get(material);
        }
        return 0;
    }

    public double getExpRate() {

        return getDouble("exp-rate", 1.0);
    }

    public double getSkillProfessionExpRate() {

        return getDouble("skill-prof-exp-rate", 1.0);
    }

    public double getProfessionHeroExpRate() {

        return getDouble("prof-hero-exp-rate", 1.0);
    }
}
