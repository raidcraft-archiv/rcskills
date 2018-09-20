# Erfahrungspunkte

Ein Spieler kann in fast allem Level aufsteigen und erhält über viele Wege EXP. Eine Haupt Quelle für Erfahrungspunkte ist das Erledigen von [Quests](https://git.faldoria.de/tof/plugins/raidcraft/rcquests) und Töten von [Custom Mobs](https://git.faldoria.de/tof/plugins/raidcraft/rcmobs).

Mit diesen Erfahrungspunkten steigt der Spieler in seiner [Klasse](Professions.md) oder [Beruf](Professions.md) im Level auf. Zusätzlich dazu kann der Spieler seine [Skills](Skills.md) durch Benutzung nach und nach im Level steigern.

Es gibt zusätzlich einen übergreifenden Heldenrang der alle Erfahrungspunkte summiert und die gesamte Erfahrung des Spielers wiederspieglt. Der Heldenrang bringt an sich keine direkten Vorteile, kann aber z.B. zum Freischalten spezieller VIP Skills verwendet werden.

- [Level Typen](#level-typen)
- [experience.yml](#experienceyml)
- [levels.yml](#levelsyml)

## Level Typen

| Level | Beschreibung |
|:-----:| ------------ |
| Helden Level | Das Helden Level spiegelt die gesamte Erfahrung des Spielers wieder und bietet keine direkten Vorteile. Auch beim Wechseln von Klassen und Berufen bleibt das Helden Level erhalten und summiert weiter die Erfahrungspunkte auf. |
| Pfad Level | Dieses Level spiegelt das gesamte Level des aktuell Pfades wieder, inkl. Subklassen. Das Level kann z.B. als Schadens-Multiplikator verwendet werden. |
| Profession Level | Dieses Level spiegelt das Level der aktiven Profession (Klassen oder Beruf) wieder und wird für EXP und Schadens Modifikatoren verwendet. |
| Skill Level | Skills steigen mit ihrer Benutzung im Level und werden dadurch stärker. Das Leveln von Skills hängt vom Skill und der Config ab. Nicht jeder Skill kann im Level aufsteigen. |

## experience.yml

In dieser Datei ist festgelegt welche Standard Mobs und Blöcke wie viel Erfahrung geben. Wenn [Custom Mobs](https://git.faldoria.de/tof/plugins/raidcraft/rcmobs) genutzt werden sind die Erfahrungspunkte für Mobs deutlich höher und vom Level der Mobs abhängig.

Außerdem kann man mit `*-exp-rate` Einstellungen temporär oder dauerhaft die Levelgeschwindigkeit der Spieler erhöhen.

## levels.yml

In dieser Dateien können beliebig viele unabhängige EXP Formeln konfiguriert werden. Die Formeln können dann in den verschiedenen Skills und Professions verwendet werden.

Als Basis für die Berechnung der Level stehen folgende Formeln zur Verfügung. Dabei wird immer die Menge an benötigten EXP für das nächste Level errechnet.

| Type | Formel |
| ---- | :----: |
| linear | level * x |
| mcmmo | ( base + x ) * level |
| static | level |
| wow | ( -0.4 * level^2 ) + ( modifier * level^2 ) |

So könnte z.B. eine Beispiel Config aussehen.

```yml
professions:
  default:
    type: wow
    modifier: 100
  primary:
    type: wow
    modifier: 100
  secundary:
    type: wow
    modifier: 220
  beruf:
    type: wow
    modifier: 46.7
skills:
  default:
    type: mcmmo
    base: 200
    x: 225
heroes:
  default:
    type: wow
    modifier: 146.5
```
