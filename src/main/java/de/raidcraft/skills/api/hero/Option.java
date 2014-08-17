package de.raidcraft.skills.api.hero;

/**
 * @author Silthus
 */
public enum Option {

    DEBUGGING("debug", false),
    COMBAT_LOGGING("combatlog", false),
    EXP_POOL_LINK("exp_pool_link", null),
    SIDEBAR_PARTY_HP("display_sidebar_party_hp", true),
    PVP("pvp", false);

    private final String key;
    private final Object defaultValue;

    Option(String key, Object defaultValue) {

        this.key = key;
        this.defaultValue = defaultValue;
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

    public boolean isSet(Hero hero) {

        return Boolean.parseBoolean(get(hero));
    }

    public String get(Hero hero) {

        String result = hero.getOptions().get(this);
        if (result == null || result.equals("")) {
            if(defaultValue == null) {
                return null;
            }
            result = defaultValue.toString();
        }
        return result;
    }
}
