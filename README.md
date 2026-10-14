# Item Magnet (Fabric, Minecraft 26.3)

Passively pulls every dropped item within **16 blocks** toward you.

- **F8** toggles the magnet on/off (rebindable in Options → Controls → Miscellaneous)
- `/magnet` does the same from chat
- Items you just threw have a 2 second pickup delay before they get pulled back

## Install
1. Install [Fabric Loader](https://fabricmc.net/use/) for 26.3 and [Fabric API](https://modrinth.com/mod/fabric-api).
2. Put `item-magnet-1.0.0.jar` into your `.minecraft/mods` folder.

## Build
Requires JDK 25.

```
./gradlew build
```

The jar ends up in `build/libs/`.

Radius and speed are the `RADIUS` / `MAX_SPEED` constants in
`src/main/java/com/xlifer/itemmagnet/ItemMagnet.java`.
