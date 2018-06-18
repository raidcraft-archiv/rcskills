# RCSkills

Das `RCSkills` Plugin bietet die Möglichkeit beliebige Klassen, Berufe und Skills zu konfigurieren und zu nutzen. Außerdem ist das Plugin die Grundlage für die Fähigkeiten von Custom Mobs.

## Getting Started

- [Latest stable download](https://ci.faldoria.de/view/RaidCraft/job/RCSkills/lastStableBuild/)
- [Issue Tracker](https://git.faldoria.de/raidcraft/rcskills/issues)
- [Developer Dokumentation](docs/DEVELOPER.md)
- [Admin Dokumentation](docs/ADMIN.md)

### Prerequisites

`RCSkills` benötigt folgende Plugins um zu funktionieren:

- [RaidCraft API](https://git.faldoria.de/raidcraft/raidcraft-api)
- [RCPermissions](https://git.faldoria.de/raidcraft/rcpermissions)
- [Effect Lib](https://github.com/Slikey/EffectLib)
- [Vault](https://github.com/MilkBowl/Vault/)
- [ProtocollLib](https://github.com/aadnk/ProtocolLib)

Zusätzlich zum Grundplugin werden Skills & Effekte benötigt. Sehr viele davon werden bereits im dazugehörigen [Skills & Effects](https://git.faldoria.de/raidcraft/skills-and-effects) Plugin bereitgestellst.

### Installation

Beim ersten Start des Plugins werden alle relevanten Configs mit Default Werten angelegt. Mehr zum Bearbeiten der Configs in der [Admin Dokumentation](docs/ADMIN.md#config.yml).

Nach dem ersten Start sollten das extra [`Skills & Effects`](https://git.faldoria.de/raidcraft/skills-and-effects) in den Ordner `skills-and-effects/` kopiert werden. Nach einem Neustart des Servers werden nun Default Configs für alle geladenen Skills und Effekte angelegt. Die Ordner der Configs können in der `config.yml` Datei festgelegt werden.

## Development

Das Skills Plugin kann durch die Entwicklung von weiteren Skills & Effekten sehr leicht erweitert werden. Details zur Entwicklung von Skills gibt es in der [Developer Dokumentation](docs/DEVELOPER.md).

## Authors

- [**Michael (Silthus) Reichenbach**](https://git.faldoria.de/Silthus)
