package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.character.CharacterTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class ThreatTable {

    private final CharacterTemplate holder;
    private final Map<CharacterTemplate, ThreatLevel> threatLevels = new HashMap<>();
    private final List<ThreatLevel> threatRanks = new ArrayList<>();

    public ThreatTable(CharacterTemplate holder) {

        this.holder = holder;
    }

    public CharacterTemplate getHolder() {

        return holder;
    }

    public ThreatLevel getThreatLevel(CharacterTemplate character) {

        if (!threatLevels.containsKey(character)) {
            ThreatLevel threatLevel = new ThreatLevel(character);
            threatLevels.put(character, threatLevel);
            threatRanks.add(threatLevel);
        }
        return threatLevels.get(character);
    }

    public ThreatLevel getHighestThreat() {

        return getThreat(0);
    }

    public ThreatLevel getThreat(int position) {

        if (threatRanks.size() < position + 1) {
            return null;
        }
        return threatRanks.get(position);
    }

    public void order() {

        Collections.sort(threatRanks);
    }

    public void reset() {

        threatLevels.clear();
        threatRanks.clear();
    }

    public class ThreatLevel implements Comparable<ThreatLevel> {

        private final CharacterTemplate target;
        private double threatLevel;

        public ThreatLevel(CharacterTemplate target) {

            this.target = target;
        }

        public CharacterTemplate getTarget() {

            return target;
        }

        public double getThreatLevel() {

            return threatLevel;
        }

        public void setThreatLevel(double threatLevel) {

            this.threatLevel = threatLevel;
            order();
        }

        public void increaseThreat(double amount) {

            setThreatLevel(getThreatLevel() + amount);
        }

        public void decreaseThreat(double amount) {

            setThreatLevel(getThreatLevel() - amount);
        }

        @Override
        public int compareTo(ThreatLevel o) {

            if (o.getThreatLevel() < getThreatLevel()) {
                return -1;
            }
            if (o.getThreatLevel() > getThreatLevel()) {
                return 1;
            }
            return 0;
        }
    }
}
