# Fair Fight
## A Simple Anti-Combat Logging Mod
---
## About
Fair Fight is a multiplatform mod aimed to remove those pesky individuals who think combat logging will save them.
Now when they decide to perform their devious tricks, their player will remain on the server if they were engaged in
combat! This will allow the person who was attacking them to continue attacking them.

---

## Configuration
This mod has 2 gamerules. The first is `in_combat_ticks`. This gamerule allows you to set how many ticks after being attacked
the game should wait until the player is considered not in combat. By default, this is 300 ticks (15s).
The second is `combat_time_shown`. This gamerule toggles the combat time cooldown overlay.

---

# Supported Platforms
- Fabric/Quilt
- Forge
- NeoForge
- PaperMC/Spigot/Folia 
- Sponge

---

# Installation
## Fabric, Quilt, Forge, NeoForge
Simply put the mod in the mods folder
## Sponge
Simply put the plugin in the plugins folder
## Spigot/PaperMC
1. Install the [Ignite](https://github.com/vectrix-space/ignite) Mixin loader
2. Run the ignite jar alongside the paper/spigot jar
3. Put the mod in the mods folder and restart
## Folia
1. Install the [Ignite](https://github.com/vectrix-space/ignite) Mixin loader
2. Rename the Folia jar to "paper.jar". Alternatively, you can launch the game with the following JVM args: `-Dignite.locator=paper -Dignite.jar=./folia.jar`
3. Run the ignite jar alongside the folia jar
4. Put the mod in the mods folder and restart
