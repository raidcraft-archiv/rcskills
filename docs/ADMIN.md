# RCSkills Admin Dokumentation

In dieser Dokumentation zum [RCSkills Plugin](../README.md) wird detailiert beschrieben, wie man folgendes konfiguriert:

- [Skills](SKILLS.md)
- [Effekte](#effekte)
- [Klassen](Professions.md#klassen)
- [Berufe](Professions.md#berufe)
- [Pfade](Pfade.md)
- [Erfahrungspunkte](Erfahrungspunkte.md)

## Getting Started

Im Root Ordner des RCSkills Plugins befinden sich Configs welche die grundlegenden Parameter für die Engine des Skill Plugins bestimmen. Alle diese Configs sollten wenn möglich einmal am Anfang konfiguriert werden. 

> Wenn Spieler bereits Level erhalten haben ist eine nachträgliche Anpassung der Basis Konfiguration sehr schwierig.

### Terminologie

Folgende Begrifflichkeiten spielen beim Konfigurieren von RCSkills immer wieder eine Rolle und sind daher hier kurz erklärt.

| Begriff | Bedeutung |
| ------- | --------- |
| Hero    | Der Spieler/Charakter |
| Skill   | Benutzbare und Passive Fähigkeiten. Können sowohl durch Klassen als auch einzeln zugewiesen werden. |
| Permission Skill | Skills können auch Permissions beinhalten. |
| Effect | Ein Effekt ist zeitlich begrenzt und wird von Skills auf Spieler und Mobs angewendet. |
| Profession | Eine Profession ist der Übergriff für Berufe und Klassen. |

## Konfiguration

### config.yml

In dieser Config werden viele grundlegende Einstellungen des Skill Plugins festgelegt. In der nachfolgenden Beispiel Config sind alle Einstellungen mit Kommentaren hinterlegt.

```yml
# Deaktiviert Skills die beim Laden einen Fehler werfen.
# In der Skill Config wird 'enabled: false' gesetzt und muss manuell wieder aktiviert werden.
disable-error-skills: false
# Definiert nach wie vielen Ticks Callbacks gelöscht werden.
# Ein Callback ist z.B. ein Pfeil der aufschlagen soll und dann explodiert.
callback-purge-ticks: 1200
# Dient der Anzeige der Herzen beim Spieler.
# Je größer die Zahl umso mehr Herzen werden im GUI angezeigt.
health-scale: 20.0
defaults:
  # Schaden von Feuer, Kakteen, Fall-Schaden, etc. wird in % gemessen.
  # Wenn Spieler große Mengen an Leben haben sollte das angeschaltet werden.
  environment-damage-in-percent: true
  # Wenn nichts in der Effekt Config überschrieben wird haben Effekte diese Priorität.
  effect-priority: 1.0
  # Globaler Cooldown für das Ausführen von Skills.
  # Verhindert das Spammen von Standard Attacken.
  global-cooldown: 1.5
  # Die Standard Gruppe die Spieler zugeteilt bekommen.
  permission-group: guest
  # Schaden beim Angriff mit den Fäusten
  fist-damage: 1.0
  # Knockback Chance in % wenn Skelette einen beschießen.
  skeleton-knockback: 0.75
  # Nach dieser Zeit in Sekunden werden Gruppen Einladungen automatisch abgelehnt.
  party-timeout: 30.0
  # Standard Angriffszeit mit Waffen und Fäusten.
  # Diese Zeit wird von Waffen und Ausrüstungen beeinflusst.
  # Die Zeit ist ähnlich dem Globalen Cooldown, nur für Angriffe.
  swing-time: 1.0
  # Bestimmt wie oft in Ticks das User Interface aktualisiert werden soll.
  userinterface-refresh-interval: 40
  # Die Zeit in Ticks nach der letzten Kampf Aktion bis der Kampf Status beendet wird.
  pvp-combat-timeout: 300.0
  # Entfernung in Blöcken in der Gruppen Mitglieder die selben EXP für getötete Mobs erhalten.
  party-exp-range: 100
  # Die Zeit in ticks die es dauert bis ein Spieler nach dem Abschalten des PvP Status nicht mehr angreifbar ist.
  pvp-toggle-delay: 300.0
  exp-bat-despawn-delay: 10.0
  character-invalidation-interval: 100
  # Wenn aktiviert bekommen Spieler keinen Hunger
  disable-vanilla-hunger: false
profession:
  # Währungskosten für den Wechsel der Klasse...
  change-cost: 100
  # ... multipliziert mit dem Level der Klasse.
  change-level-modifier: 25.0
interface:
  # Bestimmt wie oft in Ticks das User Interface aktualisiert werden soll.
  updateinterval: 20
hero:
  # Das maximale Helden-Level das ein Spieler erreichen kann
  max-level: 100
  level-treshhold: 10
  primary-path: class
  secundary-path: prof
paths:
  skill-configs: skill-configs/
  alias-configs: alias-configs/
  skill-jars: skills-and-effects/
  effect-configs: effect-configs/
  effect-jars: skills-and-effects/
  profession-configs: professions/
  abilities-jars: abilities/
# in diesen Welten sind Klassen und Berufe deaktiviert
# Permission Skills greifen aber weiterhin
ignored-worlds:
- freebuild
- lobby
- event
# Diese Pfade werden nicht auf max. Level gesetzt bei dem Befehl /rcs maxout
# virtual sollte hier immer stehen
excluded-max-out-professions:
- virtual
# Intervall in ticks in denen Logs in die Datenbank geschrieben werden.
log-interval: 60
# Deaktiviert alle Abilities die einen Fehler beim Laden haben.
disable-error-abilities: false
# Zeit in Sekunden bis ein Spieler nach dem Verlassen des Servers aus dem Cache gecleared wird.
hero-cache-timeout: 300
cache-offline-players: false
# Eine Liste aller Effekte die in der Seitenleiste angezeigt werden.
sidebar-effect-types:
- SYSTEM
- HARMFUL
- HELPFUL
- DAMAGING
- BUFF
- DEBUFF
```

### damages.yml

In dieser Datei wird der Grundschaden aller Monster und Umwelteinflüsse festgelegt.

> Wenn `defaults.environment-damage-in-percent: true` gesetzt wurde sind alle Werte unterhalb von `environmental-damage` Prozentwerte.