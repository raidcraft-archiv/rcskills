package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroOption;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
class HeroOptions {

    private final Hero hero;
    private final Map<String, String> options = new HashMap<>();

    protected HeroOptions(Hero hero) {

        this.hero = hero;
        // load all the options from the database
        for (THeroOption option : RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, hero.getId()).getOptions()) {
            options.put(option.getOptionKey(), option.getOptionValue());
        }
    }

    public void set(Option option, String value) {

        options.put(option.getKey(), value);
    }

    public void set(Option option, boolean value) {

        set(option, Boolean.toString(value));
    }

    public String get(Option option) {

        return options.get(option.getKey());
    }

    public boolean isSet(Option option) {

        return options.containsKey(option.getKey());
    }

    public void save() {

        for (Map.Entry<String, String> entry : options.entrySet()) {

            THeroOption option = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroOption.class).where()
                    .eq("hero_id", hero.getId()).eq("option_key", entry.getKey()).findUnique();
            if (option == null) {
                option = new THeroOption();
                option.setHero(RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, hero.getId()));
                option.setOptionKey(entry.getKey());
            }
            option.setOptionValue(entry.getValue());
            RaidCraft.getDatabase(SkillsPlugin.class).save(option);
        }
    }
}
