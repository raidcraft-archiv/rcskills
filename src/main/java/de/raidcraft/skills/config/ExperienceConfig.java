package de.raidcraft.skills.config;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ExperienceConfig extends ConfigurationBase {

    public static final String CONFIG_NAME = "experience.yml";
    private static final String KILLING_KEY = "killing";
    private static final String FARMING_KEY = "farming";

    public final Map<EntityType, Integer> killing;
    public final Map<Material, Integer> farming;

    public ExperienceConfig(BasePlugin plugin) {

        super(plugin, CONFIG_NAME);

        this.killing = formatEntities(KILLING_KEY, loadExperience(KILLING_KEY));
        this.farming = formatMaterials(FARMING_KEY, loadExperience(FARMING_KEY));
    }

    private Map<Material, Integer> formatMaterials(String key, Map<String, Integer> map) {

        Map<Material, Integer> materials = new HashMap<>();

        if (map == null) {
            if (key.equals(FARMING_KEY)) {
                // set the defaults for farming items
                set(Material.CARROT.name(), 10);
                // TODO: set all farmable items
            }
        } else {
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

    private Map<EntityType, Integer> formatEntities(String key, Map<String, Integer> map) {

        Map<EntityType, Integer> entities = new HashMap<>();

        if (map == null) {
            if (key.equals(KILLING_KEY)) {
                for (EntityType type : EntityType.values()) {
                    if (LivingEntity.class.isAssignableFrom(type.getEntityClass())) {
                        set(type.name(), 10);
                    }
                }
            }
            save();
        } else {
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
            map.put(type, getInt(type));
        }
        return map;
    }
}
