package de.raidcraft.skills.task;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.tables.TLanguage;
import de.raidcraft.skills.tables.TProfession;
import de.raidcraft.skills.tables.TProfessionTranslation;
import de.raidcraft.skills.tables.TSkill;
import de.raidcraft.skills.tables.TSkillTranslation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginBase;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Timestamp;

/**
 * O_x Look at this mess. Just look at it! How did this happen?
 * <p>
 * Bitte so schnell wie möglich löschen und besser implementieren.
 */
public class LoadConfigsTask extends BukkitRunnable {

    private final PluginBase plugin;
    private TLanguage languageModel;

    public LoadConfigsTask(final PluginBase plugin) {

        this.plugin = plugin;
    }

    private TLanguage getLanguage() {

        if (this.languageModel == null) {
            this.languageModel = TLanguage.find.byId("de_DE");
            if (this.languageModel == null) {
                this.languageModel = new TLanguage();
                this.languageModel.setCode("de_DE");
                this.languageModel.setName("Deutsch");
                this.languageModel.save(SkillsPlugin.class);
            }
        }
        return this.languageModel;
    }

    @Override
    public void run() {

        this.loadGenericSkills();
        this.loadProfessionSkills("alias-configs/klassen");
        this.loadProfessionSkills("alias-configs/berufe");
    }

    private void loadProfessionSkills(final String root) {

        final File[] classDirectories = new File(this.plugin.getDataFolder(), root).listFiles();

        if ((classDirectories != null) && (classDirectories.length > 0)) {
            for (final File classDirectory : classDirectories) {
                if (!classDirectory.isDirectory()) {
                    continue;
                }

                TProfession profession = TProfession.find.byId(classDirectory.getName());

                if (profession == null) {
                    profession = new TProfession();
                    profession.setNameKey(classDirectory.getName());
                    profession.save(SkillsPlugin.class);
                }

                final File[] childFiles = classDirectory.listFiles();

                if ((childFiles != null) && (childFiles.length > 0)) {
                    for (final File childFile : childFiles) {
                        if (childFile.isDirectory()) {
                            TProfession childProfession = TProfession.find.byId(childFile.getName().replace(".yml", ""));

                            if (childProfession == null) {
                                childProfession = new TProfession();
                                childProfession.setNameKey(childFile.getName().replace(".yml", ""));
                                childProfession.setParent(profession);
                                childProfession.save(SkillsPlugin.class);
                            }

                            final File[] childChildsFiles = childFile.listFiles();

                            if ((childChildsFiles != null) && (childChildsFiles.length > 0)) {
                                for (final File childChildsFile : childChildsFiles) {
                                    if (childChildsFile.isDirectory() || childChildsFile.isHidden()) {
                                        continue;
                                    }
                                    this.skillFromFile(childChildsFile.getName().replace(".yml", ""), childChildsFile, childProfession);
                                }
                            }
                        } else {
                            if (childFile.isHidden()) {
                                continue;
                            }

                            this.skillFromFile(childFile.getName().replace(".yml", ""), childFile, profession);
                        }
                    }
                }
            }
        }
    }

    private void loadGenericSkills() {

        final File[] skillFiles = new File(this.plugin.getDataFolder(), "skill-configs/").listFiles();

        TProfession profession = TProfession.find.byId("generic");
        if (profession == null) {
            profession = new TProfession();
            profession.setNameKey("generic");
            profession.save(SkillsPlugin.class);

            final TProfessionTranslation pTranslation = new TProfessionTranslation();
            pTranslation.setProfession(profession);
            pTranslation.setLanguage(this.getLanguage());
            pTranslation.setName("Allgemein");
            pTranslation.save(SkillsPlugin.class);
        }

        if ((skillFiles != null) && (skillFiles.length > 0)) {
            for (final File skillFile : skillFiles) {

                if (skillFile.isDirectory() || skillFile.isHidden()) {
                    continue;
                }
                this.skillFromFile(skillFile.getName().replace(".yml", ""), skillFile, profession);
            }
        }
    }

    private TSkill skillFromFile(final String key, final File skillFile, TProfession profession) {

        final FileConfiguration config = YamlConfiguration.loadConfiguration(skillFile);

        if (config == null) {
            return null;
        }

        TSkill skill = TSkill.find.byId(key);

        if (skill == null) {
            skill = this.insertSkillData(new TSkill(), config);
            skill.setNameKey(key);
            skill.setProfession(profession);
            skill.setUpdtimestamp(new Timestamp(skillFile.lastModified()));
            skill.save(SkillsPlugin.class);

            final TSkillTranslation translation = this.insertSkillTranslationData(new TSkillTranslation(), config);
            translation.setSkill(skill);
            translation.setLanguage(this.getLanguage());
            translation.save(SkillsPlugin.class);

        } else if (this.fileChange(skillFile, skill)) {

            skill = this.insertSkillData(skill, config);
            skill.setUpdtimestamp(new Timestamp(skillFile.lastModified()));
            skill.save(SkillsPlugin.class);

            TSkillTranslation translation = TSkillTranslation.find.where()
                    .eq("skill_name_key", skill.getNameKey())
                    .eq("language_code", this.getLanguage().getCode())
                    .findUnique();

            if (translation == null) {
                translation = new TSkillTranslation();
            }

            translation = this.insertSkillTranslationData(translation, config);
            translation.save(SkillsPlugin.class);
        }

        return skill;
    }

    private TSkill insertSkillData(final TSkill skill, final FileConfiguration config) {

        if (config.contains("enabled")) {
            skill.setEnabled(config.getBoolean("enabled"));
        }

        if (config.contains("hidden")) {
            skill.setHidden(config.getBoolean("hidden"));
        }

        if (config.contains("max-level")) {
            skill.setMaxLevel(config.getInt("max-level"));
        }

        if (config.contains("level")) {
            skill.setReqLevel(config.getInt("level"));
        }

        if (config.contains("cooldown.base")) {
            skill.setCooldown(config.getInt("cooldown.base"));
        }

        if (config.contains("range.base")) {
            skill.setReach(config.getInt("range.base"));
        }

        return skill;
    }

    private TSkillTranslation insertSkillTranslationData(final TSkillTranslation translation, final FileConfiguration config) {

        translation.setLanguage(this.getLanguage());
        translation.setName(this.getString(config, "name"));
        translation.setDescription(this.getString(config, "description"));

        return translation;
    }

    private String getString(final FileConfiguration config, final String path) {

        return this.isEmpty(config.getString(path)) ? "Edit Me!" : config.getString(path);
    }


    private boolean fileChange(final File file, final TSkill skill) {

        return (skill.getUpdtimestamp() == null) || (file.lastModified() > skill.getUpdtimestamp().getTime());
    }

    private boolean isEmpty(final String s) {

        return (s == null) || s.isEmpty();
    }
}
