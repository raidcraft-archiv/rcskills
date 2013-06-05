package de.raidcraft.skills.api.hero;

/**
* @author Silthus
*/
public enum Option {

    DEBUGGING("debug"),
    COMBAT_LOGGING("combatlog"),
    EXP_POOL_LINK("exp_pool_link"),
    PVP("pvp");

    private final String key;

    Option(String key) {

        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public void set(Hero hero, String value) {

        hero.getOptions().set(this, value);
    }

    public void set(Hero hero, boolean value) {

        hero.getOptions().set(this, value);
    }

    public String get(Hero hero) {

        return hero.getOptions().get(this);
    }

    public boolean getBoolean(Hero hero) {

        return Boolean.parseBoolean(get(hero));
    }
}
