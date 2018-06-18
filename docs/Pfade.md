# Pfade

In der `paths.yml` können mehrere Pfade und ihrere jeweiligen Klassen festgelegt werden. Somit ist es möglich, dass ein Spieler z.B. einen Beruf und eine Klasse hat.

Ein Spieler kann theoretisch beliebig viele Pfade begehen, allerdings wird es irgendwann schwierig die EXP aufzuteilen, da immer nur ein aktiver Pfad die Erfahrungspunkte erhält.

In den Configs der [Klassen](Professions.md#klassen) und [Berufs](Professions.md#berufe) lassen sich weitere Unterklassen und Berufe definieren, die der Spieler nach dem Erreichen des max. Levels auswählen kann. Diese müssen nicht in der Pfad Config aufgeführt werden.

## paths.yml

Anbei eine Beispiel Konfiguration für das klassische Beruf und Klassen Szenario:

```yml
# Eindeutiger beliebiger name für den Pfad
# Wird an anderen Stellen referenziert
class:
  # Der Name den der Spieler zu sehen bekommt
  name: Klassen
  # Die Priorität gibt teilweise an was priorisiert verwendet oder angezeigt wird, z.B. Attribute oder Effekte.
  priority: 10
  # Alle "Professions" (Klassen) die in diesem Pfad wählbar sind.
  parents:
  - warrior
  - rogue
  - mage
  - priest
  # Man kann einen Pfad automatisch im oder außerhalb des Kampfes auswählen.
  # Wenn das der Fall ist ändert sich das UI entsprechend dem Kampfstatus.
  # Im Kampf werden z.B. die EXP des Klassen Pfades angezeigt
  # und außerhalb vom Kampf die EXP des Berufs.
  select-in-combat: true
  select-out-of-combat: false
prof:
  name: Beruf
  priority: 5
  parents:
  - bergarbeiter
  - bauer
  - alchemist
  select-in-combat: false
  select-out-of-combat: true
```
