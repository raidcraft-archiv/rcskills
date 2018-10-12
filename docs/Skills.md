# Konfiguration von Skills

Alle Konfigurationen an Skills sollten wenn möglich ein neuen eigenen Dateien im `alias-configs` Ordner vorgenommen werden.

> Änderungen an den Default Configs im `skill-configs` Ordner haben globale Auswirkungen auf alle Skills, bei denen die Konfigwerte nicht explizit überschrieben haben.

- [Der erste Skill](#der-erste-skill)
- [Vererbungsstruktur](#vererbungsstruktur)
- [Basis Config](#basis-config)
- [Waffen Skills](#waffen-skills)
- [Skills mit Effekten](#skills-mit-effekten)
- [Skills in Quests](#skills-in-quests)

## Der erste Skill

Als Beispiel starten wir mit einem einfachen Feuerball, den unser Magier ab Level 5 erhalten soll.

Lege eine neue Datei im Ordner `alias-configs/klassen/magier/` mit dem Namen `feuerball.yml` an.

> **Wichtig: Jeder Skill muss einen eindeutigen Dateinamen besitzen, unabhängig von der Ordnerstruktur.**

> Tipp: Überlege dir eine sinnvolle Ordnerstruktur für deine Skills. So hast es später einfacher.

```yml
# Der Name wird dem Spieler angezeigt.
displayName: Feuerball
# Die Beschreibung steht in der Skill Liste.
description: 'Schiesst einen Feuerball auf den Gegner.'
# Der eindeutige Name des Basis-Skills. 
# Eine Liste mit Skills findest du entweder im skill-configs Ordner oder im Web.
skill: fireball
# Man kann Skills durch Benutzung im Level verbessern,
# um dadurch z.B. mehr Schaden zu verursachen.
levelable: false
# Verschiedene Standardwerte die ein Skill haben kann.
# Jeder Abschnitt kann mit der unten beschriebenen BASIS-CONFIG angepasst werden.
range:
  base: 30
castime:
  base: 2.0
damage:
  base: 87
  path-level-modifier: 13
resources:
  mana:
    mana-base-modifier: 0.01
```

Du kannst jetzt bereits mit `/rcsa addskill <Spieler> feuerball` einem Spieler den Skill geben und dann mit `/cast feuerball` benutzen.

> Skills manuell zu vergeben bietet sich z.B. für Permission-Skills an.

Editiere nun die `professions/klassen/magier.yml` Config und füge den `Feuerball` Skill unterhalb von `magisches-geschoss` ein.

> Tipp: Wie du Klassen Konfigurationen anlegst erfährst du in der [Klassen & Berufe](#Professions.md) Dokumentation.

```yml
displayName: Magier
...
skills:
  prefix-mage:
    level: 1
    hidden: true
  magisches-geschoss:
    level: 1
  feuerball:
    level: 5
...
```

Der Spieler erhält nun automatisch den Skill sobald er Level 5 erreicht. Du könntest hier nun noch einzeln Config Werte überschreiben. Details dazu gibt es im nächsten Kapitel: [Vererbungsstruktur](#Vererbungsstruktur).

## Vererbungsstruktur

Wie auch bei den [Effekten](Effects.md) gibt es bei den Skill Configs eine festgelegte Vererbungsstruktur innerhalb der einzelnen Config Dateien. Dabei greift immer der Wert der am tiefsten in der Struktur gesetzt ist.

```yml
# Hier befinden sich alle Default Configs.
# Werte in diesen Configs werden von allen anderen Configs überschrieben.
skill-configs/*
    # Hier können in einer beliebigen Ordner Struktur eigene Skill Aliase angelegt werden.
    # Alle definierten Config Einstellungen in den Alias Skills überschreiben 
    # die Werte aus den Default Configs.
|--> alias-configs/*
        # Auf letzter Ebene kann man die Config Werte von Skills
        # in den Konfigurationen der Klassen und Berufe überschreiben.
    |---> profession-inline-configs
```

> Beispiel: Man erstellt sich einen Alias Skill vom `Fireball` Basis Skill und legt dafür eine eigene Datei `alias-configs/feuerball.yml` an. Der Feuerball Skill hat eine Zauberzeit von 2s und (87 Schaden + 13 * Level der Klasse) verursacht. In der Klassen Config des Magiers legt man nun fest, dass der Magier den Feuerball ohne Zauberzeit, aber dafür mit einem Cooldown von 15s zaubern darf.

`alias-configs/feuerball.yml`

```yml
displayName: Feuerball
description: 'Schiesst einen Feuerball auf den Gegner.'
skill: fireball
levelable: false
range: # BASIS-CONFIG
  base: 30
castime: # BASIS-CONFIG
  base: 2.0
damage: # BASIS-CONFIG
  base: 87
  path-level-modifier: 13
resources:
  mana: # BASIS-CONFIG
    mana-base-modifier: 0.01
```

`professions/firemage.yml`

```yml
displayName: Feuermagier
description: Der Feuermagier vermag es, ganze Armeen in einem Flammenmeer zu vertilgen.
tag: FEU
max-level: 30
formula: secundary
skills:
  feuerball:
    level: 1
    casttime: # BASIS-CONFIG
      base: 0
health: # BASIS-CONFIG
  base: 20
  level-modifier: 0.0
color: GRAY
```

## Basis Config

In jeder Skill und [Effekt](Effects.md) Config gibt es Sektionen die immer mit den gleichen Basis Werten befüllt werden können. Zur Vereinfachung der Dartstellung wird in den Beispielen immer nur `BASIS-WERTE` genannt. Dann einfach hier nachsehen was man alles konfigurieren kann.

> Beispiele für solche Sektionen sind z.B. `damage`, `cooldown`, `casttime` und `range`.

```yml
# Ein konstanter Wert der sich nicht verändert
base: 10.5
# Das finale Ergebnis wird diesen Wert nie überschreiten.
cap: 100
# Das finale Ergebnis wird diesen Wert nie unterschreiten.
low: 15
# Addiert den kombinierten Schaden der ausgerüsteten Waffen.
weapon-damage: true/false
# Multipliziert diesen Wert mit dem Level des Helden oder des Mobs.
# In diesem Fall würden bei einem Spieler auf Level 23, 23 Punkte addiert werden.
level-modifier: 1.0
# Das gleiche wie der level-modifier, nur dass es das Level der Klasse/Beruf nimmt.
# Wenn der Skill dem Krieger zugewiesen ist
# würde es das Level der Krieger Spezialisierung mit -0.5 multiplieren.
# Man kann auch negative Werte verwenden, z.B. um den Cooldown zu verringern.
prof-level-modifier: -0.5
# Nimmt das summierte Level aller Spezialisierungen in einem Pfad 
# und multipliziert sie mit dem Wert.
# Ein Spieler mit der Berserker Spezialisierung auf Level 11
# hat bereits den Krieger auf Level 30, daher ist das Pfad Level 41.
path-level-modifier: 0.1
# Gilt nur wenn der Skill Levelbar ist.
skill-level-modifier: 0.0
# Man kann Ressourcen Modifikatoren von jeder beliebigen verfügbaren Ressource verwenden.
# Dazu einfach <resource> durch den Namen der Ressource ersetzen.
# Beispiele sind z.B.: energy, souls, health, mana
# Multipliziert den Grundwert (Wert vor Gegenständen und Buffs) der Ressource mit dem angegebenen Wert.
# z.B. mana-base-modifier: 0.15 würde Manakosten in Höhe von 15% des Grund-Manas verursachen.
<resource>-base-modifier: 0.15
# Wenn true nimmt den Maximal Wert der Ressource anstatt den aktuellen Wert.
<resource>-from-max: true/false
# Multipliziert den aktuellen/maximalen Wert der Ressource damit.
<resource>-modifier: 1.0
# Multipliziert diesen Wert mit dem Prozentwert der Ressource.
# Bei 50/100 Mana wäre das 5.0 * 0.5
<resource>-percent-modifier: 5.0
# Man kann attribute genau wie Ressourcen verwenden, mit dem Unterschied,
# dass man nur den aktuellen Wert verwenden kann.
<attribute>-attr-modifier: 5.0
# Man kann auch beliebige Skills oder Professions als Multiplikator verwenden, egal wo der Skill verwendet wird.
# Dazu einfach den eindeutigen Namen einsetzen.
<skillname>-skill-modifier: 0.0
<profname>-prof-modifier: 0.0
```

## Waffen Skills

[Der erste Skill](#der-erste-skill) den wir konfiguriert haben war ein einfacher Feuerball, der ohne Waffe gezaubert werden kann. Es gibt aber auch die Möglichkeit Skills zu konfigurieren die nur mit Waffen und sogar nur mit speziellen Waffen Typen ausgeführt werden können.

Im folgenden Beispiel werden wir den Standard Angriff `Wilder Schlag` des Kriegers konfigurieren.

Erstelle dafür eine Config `wilder-schlag.yml` unterhalb von `alias-configs/klassen/krieger/`.

> Tipp: Die Dateinamen von Skills können Bindestriche enthalten, dürfen aber keine Leerzeichen haben.

```yml
displayName: Heldenhafter Stoss
description: 'Du schlägst dein Ziel mit voller Wucht und stößt es dabei zurück.'
skill: strike
# Der Skill kann nur verwendet werden wenn eine der folgenden Waffen ausgerüstet ist.
weapons:
  - sword
  - axe
  - polearm
  - mace
cooldown: # BASIS-CONFIG
  base: 5
# Der Skill fügt Schaden basierend auf dem Schaden der Waffe und des Levels der Spezialisierun zu.
# Beispiel: Der Spieler ist Berserker Level 13 (Spezialisierung nach dem Krieger) und damit Krieger Level 30. Das macht ein gesamt path-level von 43.
# Außerdem trägt er eine Schwere Zweihand Axt die Schaden von 64-95 mit einer Geschwindigkeit von 2.5 verursacht.
# Der Schaden berechnet sich also wie folgt:
# 89 + (13 * 43) + RND(64,95)
damage: # BASIS-CONFIG
  base: 89
  path-level-modifier: 13
  weapon-damage: true
```

Wie vorher muss der Skill noch in der Config der Krieger [Klasse](Professions.md) hinzugefügt werden.

## Skills mit Effekten

Je nach Programmierung der Skills kann ein Skill auch [Effekte](Effects.md) auslösen. Effekte haben keine eigenen Config Dateien sondern werden direkt in der Skill Config mit konfiguriert.

Im folgenden konfigurieren wir den Heilungszauber `Erneuerung` des Priesters. Der Zauber soll das Ziel über 12s alle 2s heilen.

Erstelle dafür eine Config `erneuerung.yml` unterhalb von `alias-configs/klassen/priester/`.

> Tipp: Heileffekte nutzen den `damage` Abschnitt um die geheilten Leben zu berechnen.

> Tipp: Skills können mit Halten von <kbd>Shift</kbd> auf den Zauberer gewirkt werden.

```yml
displayName: Erneuerung
description: 'Erneuert die Lebensenergie von einem Gruppenmitglied oder dem Priester für 12s.'
skill: heal-over-time
range:
  base: 25
resources:
  mana:
    mana-base-modifier: 0.015
effects:
  # Der eindeutige Name des Effekts muss vom Entwickler des Skills dokumentiert werden.
  heal-over-time:
    # Dieser Name wird dem Spieler in der Seitenleiste angezeigt.
    displayName: 'Erneuerung'
    duration: # BASIS-CONFIG
      base: 12
    # Das Intervall wird in Sekunden angegeben.
    interval: # BASIS-CONFIG
      base: 2
    # In diesem Fall ist Schaden = Heilung
    damage: # BASIS-CONFIG
      base: 27
```

## Skills in Quests

Zusätzlich zu den Alias Configs gibt es auch die Möglichkeit direkt in Quests Skills zu definieren und zu nutzen. Dabei einfach eine normale Skill Config mit der Endung `.skill.yml` im Quest Ordner anlegen.
Skills die so erstellt werden können dann, z.B. mit der `!cast.override this.mein-skill` Action gezaubert werden.