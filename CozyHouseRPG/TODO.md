# 📋 CozyHouseRPG — Analyse complète & TODO

> Généré le 26/02/2026 — Mis à jour le 27/02/2026 (corrections appliquées).
> ✅ = corrigé | 🔴/🟠/🟡/🟢 = à faire

---

## 🔴 Critique — Bugs & crashs potentiels

### ✅ 1. `BiomeChecker` — NullPointerException si le joueur n'a pas de race
Ajout d'un guard `if (rpgPlayer == null || !rpgPlayer.hasRace()) return;` dans `onBiomeChange()`.

### ✅ 2. `BiomeChecker` — NullPointerException si `getPlayer()` est vide
Remplacé le bloc if/null par un `orElse(null)` + guard propre.

### ✅ 3. `AttributeApplicator` — `value == 0` bloquait les resets intentionnels
Suppression de la condition `if (value == 0) return;`. Les valeurs zéro légitimes
(armure, knockback, oxygen bonus, fall damage…) sont maintenant correctement appliquées,
ce qui corrige notamment le no-fall-damage de la Fée et les changements de race.

### ✅ 4. `RaceCommand` — `exp set` ne déclenchait pas le level-up
Ajout de `setExperienceAndCheck(long)` dans `ExperienceService` (set + checkLevelUp).
`handleExp` utilise maintenant cette méthode au lieu du setter brut.

### ✅ 5. `RaceCommand` — `level give/set` ne plafonnait pas au niveau max
Le niveau est désormais cappé à `experienceService.getMaxLevel()`.
Un message est affiché à l'admin si le cap est atteint.

### ✅ 6. `RaceCommand` — `level give/set` n'appliquait pas les attributs
Appel de `context.getPlayerService().refreshAttributes(targetRpg)` après chaque
modification de niveau. L'XP est aussi remise à 0 pour cohérence.

---

## 🟠 Important — Fonctionnalités manquantes ou incomplètes

### ✅ 7. Persistence des données — non implémentée
**`PlayerRepository.saveAll()` / `loadAll()` sont vides.**
Toutes les données (race, niveau, XP) sont perdues au redémarrage du serveur.
Options : fichiers YAML/JSON par joueur, SQLite, MySQL.
Le `PlayerQuitListener` contient un `// TODO` à cet endroit.

### ✅ 8. `PlayerQuitListener` — inexistant
Créé. Nettoie le BiomeChecker et le PlayerRepository à chaque déconnexion.
Prêt à accueillir la sauvegarde une fois la persistence implémentée.

### 🟠 9. Système de compétences (Skills) — architecture sans implémentation
`Skill`, `SkillType`, `SkillCategory`, `InnateSkills` existent mais rien n'est concret :
- Pas de classe implémentant `Skill`
- Pas de `SkillRegistry` / `SkillFactory`
- Pas de `SkillGUI`
- `skillPoints` et `skillPointsPerLevel` dans `RPGPlayer` ne peuvent pas être dépensés

### 🟠 10. Système de statuts (Status/Buffs/Debuffs) — architecture sans implémentation
`BuffsEnum` et `DebuffsEnum` sont **vides**.
Aucun système n'applique ou ne retire des statuts aux joueurs en jeu.

### ✅ 11. `BiomeChecker` action bar — n'utilisait pas les helpers d'affichage
Refactorisé dans la méthode `updateActionBar()`.
Au niveau max : barre pleine dorée + "§6✦ MAX" + "§6MAX EXP".
Sinon : niveau coloré + barre verte/grise + "actuel/requis EXP".
La barre est aussi protégée contre un `required == 0` (division par zéro).

### 🟠 12. Attributs RPG jamais appliqués au joueur Bukkit
Plusieurs propriétés sont stockées mais n'ont aucun effet en jeu :

| Propriété | Impact attendu |
|---|---|
| `critChance` / `critDamage` | Système de critique (event de combat) |
| `dodgeChance` | Système d'esquive (event de dégâts) |
| `miningFatigueResistance` | Réduction fatigue minière |
| `poisonResistance` | Réduction dégâts de poison |
| `lootBonus` | Bonus drops de mobs |
| `tradeDiscount` | Réduction prix villageois |
| `canClimbWalls` | Capacité d'escalade |

### 🟠 13. `MobsExperiences` — tous les mobs donnent la même XP
Tous ont `min=10, max=20`. Aucune différenciation de difficulté.
Les boss donnent autant qu'un poulet. À rebalancer.

---

## 🟡 Qualité & cohérence — À corriger

### ✅ 14. `RaceFactory` — 5 races n'utilisent pas le `MessageManager`
ORC, GOBLIN, FAIRY, BEAST, FISH ont leurs noms/descriptions **hardcodés**.
HUMAN, ELF, DWARF passent correctement par le YAML via `messageManager`.

### ✅ 15. `RaceFactory` — constructeur en double
`@RequiredArgsConstructor` supprimé. Il ne reste que le constructeur manuel
qui initialise correctement `context` et `messageManager`.

### ✅ 16. `PlayerJoinListener` — GUI ouverte sans délai
Ouverture du GUI retardée d'1 tick via `runTaskLater(plugin, ..., 1L)`
pour éviter que Minecraft ne la ferme immédiatement au chargement du monde.

### ✅ 17. ~~`RaceCommand` — feedback `exp give` utilise les données brutes~~
Corrigé avec le fix #4 (refonte de `handleExp`) — `getXpDisplay()` utilisé partout.

---

## 🟢 Améliorations suggérées (non urgentes)

### 🟠 18. `PlayerDeathListener` manquant
Aucun listener sur la mort du joueur :
- Perte d'XP ou de niveau à la mort ?
- Reset de certains statuts ?
- Message de mort personnalisé avec la race ?

### ✅ 19. `PlayerRespawnListener` manquant
Créé. Ré-applique les attributs RPG 1 tick après le respawn (Minecraft les réinitialise
à la mort, ce qui ferait perdre santé max, vitesse, armure, etc.).

### 🟢 20. Service dédié pour l'action bar
La logique d'action bar est dans `BiomeChecker`. Un `ActionBarService` dédié
serait plus propre mais n'est pas urgent.

### 🟢 21. `RaceSelectionGUI` — confirmation avant changement de race
Aucun avertissement si un joueur change de race via `/chrpg changerace`.
Un GUI de confirmation ou un message serait pertinent.

### 🟢 22. `BiomeChecker` — multiplicateurs de dégâts et statuts de biome non appliqués
`Race` possède `biomeDamageMultipliers` et `biomeStatuses` mais `onBiomeChange`
n'applique que le multiplicateur de vitesse.

### 🟢 23. Effets permanents de race (`permanentEffects`) non appliqués
`Race` a une liste `permanentEffects` (PotionEffect) mais ni `AttributeApplicator`
ni `PlayerService` ne l'appliquent au joueur Bukkit.

### 🟢 24. `MobsExperiences` — mobs non souhaités dans la liste
`VILLAGER`, `WANDERING_TRADER`, `SNOW_GOLEM` donnent de l'XP RPG.

---

## 📊 Résumé des priorités restantes

| Priorité | Tâche |
|---|---|
| 🔴 P0 | **Persistence** (YAML/SQLite/MySQL) — données perdues au redémarrage |
| 🟠 P1 | Implémenter les listeners de combat (critique, esquive, résistances) |
| 🟠 P1 | Rebalancer `MobsExperiences` (boss >> hostiles >> neutres >> passifs) |
| 🟠 P1 | Implémenter le système de compétences (Skill concret, SkillGUI, dépense de points) |
| 🟠 P1 | Appliquer `permanentEffects` de la race au joueur |
| 🟠 P1 | `PlayerDeathListener` (perte XP ? reset statuts ? message de mort) |
| 🟡 P2 | Finir `BuffsEnum` / `DebuffsEnum` et implémenter l'application des statuts |
| 🟡 P2 | `RaceFactory` — uniformiser toutes les races via `messageManager` (ORC, GOBLIN, FAIRY, BEAST, FISH) |
| 🟢 P3 | GUI de confirmation avant changement de race |
| 🟢 P3 | `ActionBarService` dédié (sortir de `BiomeChecker`) |
| 🟢 P3 | Appliquer `biomeDamageMultipliers` et `biomeStatuses` dans `BiomeChecker` |
| 🟢 P3 | Filtrer les mobs indésirables dans `MobsExperiences` |
