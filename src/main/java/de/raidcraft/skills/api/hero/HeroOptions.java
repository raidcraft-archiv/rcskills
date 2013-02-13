package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroOption;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
class HeroOptions {

    private final Hero hero;
    private final Map<String, Boolean> options = new HashMap<>();

    protected HeroOptions(Hero hero) {

        this.hero = hero;
        // load all the options from the database
        for (THeroOption option : Ebean.find(THero.class, hero.getId()).getOptions()) {
            options.put(option.getOptionKey(), option.isOptionValue());
        }
    }

    public void set(Option option, boolean value) {

        options.put(option.getKey(), value);
    }

    public boolean isSet(Option option) {

        return options.get(option.getKey()) != null && options.get(option.getKey());
    }

    public void save() {

        for (Map.Entry<String, Boolean> entry : options.entrySet()) {

            THeroOption option = Ebean.find(THeroOption.class).where()
                    .eq("hero_id", hero.getId()).eq("option_key", entry.getKey()).findUnique();
            if (option == null) {
                option = new THeroOption();
                option.setHero(Ebean.find(THero.class, hero.getId()));
                option.setOptionKey(entry.getKey());
            }
            option.setOptionValue(entry.getValue());
            Database.save(option);
        }
    }
}
