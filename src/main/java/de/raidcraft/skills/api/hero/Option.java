package de.raidcraft.skills.api.hero;

/**
* @author Silthus
*/
public enum Option {

    DEBUGGING("debug"),
    COMBAT_LOGGING("combatlog");

    private final String key;

    Option(String key) {

        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public void set(Hero hero, boolean value) {

        hero.getOptions().set(this, value);
    }

    public boolean isSet(Hero hero) {

        return hero.getOptions().isSet(this);
    }
}
