# 🧭 Guide : Implémentation du Système d'EXP — CozyHouseRPG

## 📊 État actuel du code (ce qui existe déjà)

| Fichier | Ce qui est déjà là |
|---|---|
| `RPGPlayer.java` | Champs `level` (double) et `experience` (long) |
| `PlayerService.java` | `addExperience()`, `checkLevelUp()`, `calculateRequiredExperience()` |
| `PlayerRepository.java` | `saveAll()` / `loadAll()` marqués TODO |

Les méthodes existent donc, mais elles sont **déconnectées** du jeu : rien ne les appelle, rien ne sauvegarde, rien n'affiche quoi que ce soit au joueur.

---

## 🗺️ Vue d'ensemble des étapes

```
1. Finaliser la formule d'EXP et le level cap
2. Compléter PlayerService : level-up bonuses + messages
3. Créer les sources d'EXP (listeners d'événements Bukkit)
4. Créer un ExperienceService dédié (séparation des responsabilités)
5. Affichage : action bar / title / effets visuels
6. Commandes admin (/rpg exp give, /rpg level set)
7. Persistance (sauvegarde/chargement)
8. Intégration avec les Skills et la Race
9. Tests & équilibrage
```

---

## ÉTAPE 1 — Finaliser la formule d'EXP et le level cap

**Fichier concerné :** `PlayerService.java` → méthode `calculateRequiredExperience()`

La formule actuelle est :
```java
return (long) (100 * Math.pow(currentLevel + 1, 1.5));
```

### Ce que tu dois décider :
- **Level maximum** : ex. 100 ? Ajouter une constante `MAX_LEVEL = 100` dans `PlayerService` (ou dans une classe `ExpConfig`).
- **Formule** : la formule actuelle est correcte. Voici quelques alternatives courantes :
  - Linéaire : `100 * (level + 1)` → trop rapide en fin de jeu
  - Quadratique : `100 * level^2` → progression plus dure
  - Actuelle (1.5 exposant) : bon équilibre ✅

### À faire :
- Ajouter `private static final int MAX_LEVEL = 100;` dans `PlayerService`
- Dans `checkLevelUp()`, ajouter une condition pour stopper le level-up si `rpgPlayer.getLevel() >= MAX_LEVEL`
- Bloquer l'EXP si le joueur est déjà au level max

---

## ÉTAPE 2 — Compléter PlayerService : level-up bonuses + messages

**Fichier concerné :** `PlayerService.java` → méthode `checkLevelUp()`

### Actuellement dans `checkLevelUp()` :
```java
// TODO: Send level up message
// TODO: Grant level up bonuses
```

### Ce que tu dois implémenter :

**A) Message de level-up**
Dans le while de `checkLevelUp()`, après l'incrémentation du level :
```java
if (rpgPlayer.getBukkitPlayer() != null && rpgPlayer.getBukkitPlayer().isOnline()) {
    Player p = rpgPlayer.getBukkitPlayer();
    p.sendTitle("§6✦ LEVEL UP ✦", "§eNiveau " + (int) rpgPlayer.getLevel(), 10, 60, 20);
    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
}
```

**B) Bonus de stats au level-up**
Créer une méthode privée `applyLevelUpBonus(RPGPlayer rpgPlayer)` qui augmente les stats de base selon la race.
Exemple :
```java
// +0.5 HP par level pour tous
rpgPlayer.setMaxHealth(rpgPlayer.getMaxHealth() + 0.5);
rpgPlayer.setHealth(rpgPlayer.getMaxHealth()); // soin complet au level-up
// refreshAttributes() pour appliquer à Bukkit
refreshAttributes(rpgPlayer);
```
Tu peux personnaliser les bonus par race via un `switch` sur `rpgPlayer.getCurrentRace()`.

---

## ÉTAPE 3 — Créer les sources d'EXP (Event Listeners)

C'est l'étape la plus importante. Il faut créer un listener qui capte les actions du joueur et appelle `playerService.addExperience()`.

### Fichier à créer : `listeners/ExpListener.java`

**Événements Bukkit à écouter et EXP suggérée :**

| Événement Bukkit | Source d'EXP | EXP recommandée |
|---|---|---|
| `EntityDeathEvent` | Tuer un mob | 10–500 selon le type |
| `BlockBreakEvent` | Miner un bloc | 1–15 selon le bloc |
| `PlayerFishEvent` | Pêche réussie | 20–50 |
| `CraftItemEvent` | Craft d'un item | 5–30 |
| `EnchantItemEvent` | Enchantement | 50–100 |
| `PlayerTradeEvent` | Commerce villageois | 10–30 |
| `EntityTameEvent` | Apprivoiser un animal | 50 |
| `PlayerHarvestBlockEvent` | Récolte agricole | 5–20 |

### Structure minimale du listener :
```java
@EventHandler
public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity().getKiller() == null) return;
    Player killer = event.getEntity().getKiller();
    
    context.getPlayerService().getPlayer(killer).ifPresent(rpgPlayer -> {
        long exp = calculateMobExp(event.getEntity());
        context.getPlayerService().addExperience(rpgPlayer, exp);
    });
}

private long calculateMobExp(LivingEntity entity) {
    return switch (entity.getType()) {
        case ZOMBIE, SKELETON -> 10L;
        case CREEPER -> 25L;
        case ENDERMAN -> 75L;
        case ENDER_DRAGON -> 5000L;
        default -> 5L;
    };
}
```

### Enregistrement dans `CozyHouseRPG.java` :
Dans `registerListeners()`, ajouter :
```java
pm.registerEvents(new ExpListener(context), this);
```

---

## ÉTAPE 4 — Créer un ExperienceService dédié (recommandé)

**Pourquoi ?** `PlayerService` commence à être gros. Isoler la logique d'EXP dans `ExperienceService` respecte le principe de responsabilité unique.

### Fichier à créer : `player/ExperienceService.java`

Ce service prendrait en charge :
- `addExperience(RPGPlayer, long amount)` → avec le calcul du bonus de race
- `checkLevelUp(RPGPlayer)` → le while de level-up
- `calculateRequiredExperience(double level)` → la formule
- `applyLevelUpBonus(RPGPlayer)` → les bonus de stats

### Dans `PlayerService`, remplacer la logique d'exp par une délégation :
```java
private final ExperienceService experienceService;

public void addExperience(RPGPlayer rpgPlayer, long amount) {
    experienceService.addExperience(rpgPlayer, amount);
}
```

### Ajouter `ExperienceService` dans `PluginContext.java` :
```java
private final ExperienceService experienceService;
// dans le constructeur :
this.experienceService = new ExperienceService(this);
```

---

## ÉTAPE 5 — Affichage : Action Bar + Barre de progression

### A) Action Bar permanente (EXP en temps réel)
Créer un **task Bukkit** qui affiche l'EXP en action bar toutes les secondes.

Dans `CozyHouseRPG.onEnable()` :
```java
getServer().getScheduler().runTaskTimer(this, () -> {
    for (RPGPlayer rpgPlayer : context.getPlayerRepository().findAll()) {
        if (rpgPlayer.getBukkitPlayer() == null || !rpgPlayer.getBukkitPlayer().isOnline()) continue;
        
        long required = experienceService.calculateRequiredExperience(rpgPlayer.getLevel());
        long current = rpgPlayer.getExperience();
        int level = (int) rpgPlayer.getLevel();
        
        String bar = buildExpBar(current, required, 20); // méthode à créer
        rpgPlayer.getBukkitPlayer().spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            new TextComponent("§6Lvl " + level + " §7| " + bar + " §e" + current + "/" + required + " EXP")
        );
    }
}, 0L, 20L); // toutes les secondes
```

### B) Méthode `buildExpBar()` :
```java
private String buildExpBar(long current, long required, int barLength) {
    int filled = (int) ((double) current / required * barLength);
    return "§a" + "█".repeat(filled) + "§8" + "█".repeat(barLength - filled);
}
```

### C) Barre d'EXP Minecraft native (optionnel)
Tu peux utiliser `player.setLevel(level)` et `player.setExp((float) current / required)` pour afficher l'EXP dans la barre vanilla Minecraft. Simple mais moins personnalisé.

---

## ÉTAPE 6 — Commandes admin

### Dans `RaceCommand.java` ou une nouvelle classe `ExpCommand.java` :

| Commande | Action |
|---|---|
| `/rpg exp give <joueur> <montant>` | Donne de l'EXP à un joueur |
| `/rpg exp set <joueur> <montant>` | Définit l'EXP exacte |
| `/rpg level set <joueur> <level>` | Force un level |
| `/rpg stats <joueur>` | Affiche les stats RPG |

### Enregistrement dans `plugin.yml` :
Ajouter les sous-commandes sous `cozyhouserpg` si tu utilises une structure de commande arborescente.

---

## ÉTAPE 7 — Persistance (IMPORTANT)

Actuellement, `PlayerRepository.saveAll()` et `loadAll()` sont des méthodes vides (TODO). Sans persistance, **toute l'EXP est perdue à chaque redémarrage**.

### Option A : Fichiers YAML (simple, sans dépendance)
Dans `saveAll()`, pour chaque `RPGPlayer`, écrire un fichier `plugins/CozyHouseRPG/players/{uuid}.yml` avec :
```yaml
uuid: "..."
level: 5
experience: 1250
currentRace: "HUMAN"
```
Utiliser `YamlConfiguration` de l'API Bukkit. Charger dans `loadAll()` au `onEnable`.

### Option B : SQLite (robuste, recommandé pour un serveur)
Utiliser `java.sql.Connection` avec SQLite (pas de dépendance externe si tu utilises le driver bundlé).
Table `rpg_players` avec colonnes : `uuid`, `level`, `experience`, `race`.

### Quand sauvegarder :
- À chaque level-up (appel à `playerRepository.save(rpgPlayer)`)
- Quand le joueur quitte (`PlayerQuitEvent` → `playerService.removePlayer()` doit sauvegarder avant de retirer)
- Toutes les 5 minutes via un task Bukkit (sécurité en cas de crash)

---

## ÉTAPE 8 — Intégration avec la Race et les Skills

### Race :
- `expBonus` est déjà dans `RPGPlayer` et utilisé dans `addExperience()` ✅
- Vérifier que `setPlayerRace()` réinitialise correctement l'EXP/level (ou non, selon le design : est-ce qu'un changement de race reset le level ?)

### Skills :
- Certains skills peuvent modifier le gain d'EXP → dans `addExperience()`, vérifier les skills actifs du joueur et appliquer un multiplicateur supplémentaire
- Des skills peuvent se débloquer à certains levels → dans `checkLevelUp()`, appeler une méthode `unlockSkillsForLevel(rpgPlayer, newLevel)`

---

## ÉTAPE 9 — Tests & Équilibrage

### Checklist de test :
- [ ] Un joueur qui tue un zombie gagne de l'EXP
- [ ] L'action bar affiche correctement EXP / EXP requise
- [ ] Le level-up se déclenche et affiche le titre
- [ ] Les stats augmentent bien au level-up
- [ ] La reconnexion charge bien le level et l'EXP sauvegardés
- [ ] Un joueur au level max ne peut plus gagner d'EXP (ou stagne)
- [ ] La commande admin `/rpg exp give` fonctionne
- [ ] Le bonus d'EXP de la race est bien appliqué

### Outil d'équilibrage :
Calculer le temps théorique pour atteindre le level max avec la formule actuelle :
- Level 1 → 2 : 189 EXP
- Level 10 → 11 : ~3317 EXP
- Level 50 → 51 : ~36k EXP
- Level 99 → 100 : ~100k EXP

Ajuste l'exposant (1.5) et le multiplicateur (100) selon le rythme de jeu voulu.

---

## 📁 Résumé des fichiers à créer / modifier

| Action | Fichier |
|---|---|
| ✏️ Modifier | `PlayerService.java` — compléter `checkLevelUp()`, ajouter `MAX_LEVEL` |
| 🆕 Créer | `player/ExperienceService.java` — logique EXP isolée |
| 🆕 Créer | `listeners/ExpListener.java` — sources d'EXP (mobs, blocs...) |
| ✏️ Modifier | `CozyHouseRPG.java` — enregistrer ExpListener + task action bar |
| ✏️ Modifier | `PluginContext.java` — ajouter ExperienceService |
| ✏️ Modifier | `PlayerRepository.java` — implémenter saveAll() / loadAll() |
| 🆕 Créer | `commands/ExpCommand.java` (optionnel, ou ajouter à RaceCommand) |
| ✏️ Modifier | `plugin.yml` — enregistrer les nouvelles commandes |

---

## 🔰 Ordre d'implémentation conseillé

1. **Étape 1** : Finalise la formule + MAX_LEVEL → 15 min
2. **Étape 2** : Messages + bonus level-up dans `checkLevelUp()` → 30 min
3. **Étape 3** : `ExpListener.java` avec 2-3 événements (mob kills + blocs) → 45 min
4. **Étape 5B** : Barre Minecraft native (`setLevel` / `setExp`) pour voir visuellement → 10 min
5. **Étape 7A** : Persistance YAML → 1h
6. **Étape 4** : Refacto `ExperienceService` → 30 min
7. **Étape 5A** : Action bar avancée → 30 min
8. **Étape 6** : Commandes admin → 45 min
9. **Étape 8** : Intégration Skills → selon complexité
10. **Étape 9** : Tests → ongoing
