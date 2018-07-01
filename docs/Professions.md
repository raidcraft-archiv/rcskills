# Spezialisierungen

Mit dem [RCSkills](../README.md) Plugin kann man mehrere [Skills](Skills.md) in Spezialisierungen, oder auch Klassen und Berufe genannt, gruppieren. Diese Spezialisierungen lassen sich dann wiederum in [Pfaden](Paths.md) zusammenfassen um so dem Spieler ein progressives Level Erlebnis zu bieten.

In der Spezialisierung werden auch die [Attribute](#attribute) und [Ressourcen](#ressourcen) einer Klasse oder eines Berufs festgelegt.

- [Die erste Klasse](#die-erste-klasse)
  - [Ressourcen](#ressourcen)
  - [Attribute](#attribute)

## Die erste Klasse

Im Beispiel werden wir einen Schurken mit einigen [Skills](Skills.md), Attributen und Ressourcen konfigurieren. Außerdem wird der Klasse eine [Level Formel](Erfahrungspunkte.md#levelsyml) zugewiesen.

Lege im Ordner `professions/klassen/` die Datei `schurke.yml` an.

> Tipp: Durch eine gute Ordnerstruktur lässt sich später ein Chaos vermeiden.

```yml
displayName: Schurke
description: Der Schurke versteht sich auf das Brauen von Giften und ist auf den heimlichen Angriff aus dem Schatten spezialisiert.
# Prefix im Chat
tag: SCH
# Der Schurke kann sich nach dem Erreichen des max-level auf eine der folgenden Klassen spezialisieren.
childs:
- assassin
- hunter
max-level: 30
# Eine in der levels.yml konfigurierte Level Formel
formula: primary
# Eine Liste der verfügbaren Skills und auf welchem Level die Skills freigeschaltet werden.
skills:
  prefix-rogue:
    level: 1
  schattenschlag:
    level: 1
  schleichen:
    level: 1
  pfeilbeschwoerung:
    level: 3
  todesstoss:
    level: 5
  gezielter-schuss:
    level: 7
  geschicklichkeit:
    level: 10
  schurke-entwaffnen:
    level: 20
  dunkler-stich:
    level: 25
  verkrueppelnder-schuss:
    level: 25
resources:
  # ... siehe Ressourcen
attributes:
  # ... siehe Attribute
health: # BASIS-CONFIG
  base: 280
  prof-level-modifier: 12.5
  path-level-modifier: 9.0
  stamina-attr-modifier: 10
# Eine Liste der erlaubten Rüstungsklassen und ab welchem Level die Klasse sie nutzen darf.
allowed-armor:
  CLOTH: 1
  LEATHER: 1
# Eine Liste der erlaubten Waffen und ab welchem Level die Klasse sie nutzen darf.
allowed-weapons:
  dagger: 1
  sword: 1
  mace: 1
  axe: 1
  bow: 1
# Die Farbe der Klasse im Chat.
color: GRAY
```

### Ressourcen

[Entwickler](Developer.md) können wie [Skills](Skills.md) auch zusätzliche Ressourcen programmieren die dann in Skills als Ressource verwendet werden können.

Skills selber können abhängig von ihrer Programmierung oder Config Einfluss auf Ressourcen nehmen und sie so erhöhen oder verringern. Ein klassisches Beispiel hierfür ist Mana, das beim Verwenden von Skills abnimmt, aber auch durch bestimmte Skills aufgefrischt werden kann.

```yml
resources:
  # Eindeutiger Name der Ressource.
  # Wird vom Entwickler bekannt gegeben.
  health:
    displayName: Leben
    # Die Regneration der Ressource außerhalb vom Kampf.
    # In diesem Beispiel wird das Leben des Spielers außerhalb vom Kampf alle 5s um 2.5% regeneriert.
    out-of-combat-regen:
      percent: true
      from-max: true
      base: 0.025
    interval:
      base: 5
  # In diesem Fall wird Ernergie jede 1/10s um 1 erhöht und kann maximal 100 erreichen, egal welches Level der Spieler hat.
  energy:
    displayName: Energie
    # Die Anzeige der Ressource im GUI.
    types:
    - experience
    default: 0
    max:
      base: 100
    min: 0
    in-combat-regen:
      base: 1
      percent: false
    out-of-combat-regen:
      base: 1
      percent: false
    interval:
      base: 0.1
```

Hier noch ein Beispiel für die Ressource `Mana` beim Magier.
Das Basis Mana des Magiers erhöht sich im Beispiel mit jedem Level. Zusätzlich erhält der Magier für jeden Punkt in dem [Attribut](#Attribute) Intelligenz 15 Manapunkte dazu.

Die Manaregeneration unterscheidet sich im und außerhalb des Kampfes. Zusätzlich hängt die Regeneration vom Willenskraft Wert ab.

```yml
mana:
  displayName: Mana
  types:
  - EXPERIENCE
  default: 0
  base: # BASIS-CONFIG
    base: 100
    path-level-modifier: 27.25
  max: # BASIS-CONFIG
    intellect-attr-modifier: 15
  min: 0
  in-combat-regen: # BASIS-CONFIG
    percent: false
    mana-base-modifier: 0.05
    spirit-attr-modifier: 0.1
  out-of-combat-regen: # BASIS-CONFIG
    percent: true
    base: 0.01
    mana-base-modifier: 0.05
    spirit-attr-modifier: 0.001
  interval: # BASIS-CONFIG
    base: 5
  # Gibt an wie lange es nach der Benutzung eines Skills dauert bis die Regeneration einsetzt.
  usage-delay: # BASIS-CONFIG
    base: 5
```

### Attribute

Alle Attribute müssen im Code definiert werden, da diese auch an vielen anderen Stellen, wie z.B. [Custom Items](https://git.faldoria.de/raidcraft/rc-items) verwendet werden.

> **Wichtig: Bei jeder Klasse sollten alle Attribute konfiguriert werden.**

```yml
attributes:
  strength:
    base-value: # BASIS-CONFIG
      base: 7
      path-level-modifier: 0.75
    # Gibt den Schadenstyp an den dieses Attribut beeinflusst.
    # In diesem Fall wird 1:1 der Wert des Attributs auf den physischen Schaden addiert.
    damage-modifiers:
      PHYSICAL: 1.0
  agility:
    base-value: # BASIS-CONFIG
      base: 17
      path-level-modifier: 1.5
    damage-modifiers:
      PHYSICAL: 2.0
      DEFAULT_ATTACK: 0.1
  stamina:
    base-value: # BASIS-CONFIG
      base: 14
      path-level-modifier: 1
  intellect:
    base-value: # BASIS-CONFIG
      base: 9
      path-level-modifier: 0.75
  spirit:
    base-value: # BASIS-CONFIG
      base: 5
      path-level-modifier: 0.5
```