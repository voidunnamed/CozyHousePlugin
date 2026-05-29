# 🗡️ CozyHouseRPG — Design du Système de Compétences

> Document de conception — plusieurs propositions originales détaillées.
> Infrastructure existante prise en compte : `Skill`, `SkillType` (PASSIVE, ACTIVE, TOGGLE, ULTIMATE, TRIGGERED), `SkillCategory` (12 catégories), `InnateSkills`, `skillPoints` sur `RPGPlayer`.

---

## Sommaire

1. [Proposition A — Arbre de Runes Gravées](#proposition-a--arbre-de-runes-gravées-⭐-recommandée)
2. [Proposition B — Mémoire Ancestrale (absorption de mobs)](#proposition-b--mémoire-ancestrale)
3. [Proposition C — Constellation d'Attributs](#proposition-c--constellation-dattributs)
4. [Proposition D — Synergies & Combos enchaînés](#proposition-d--synergies--combos-enchaînés)
5. [Proposition E — Affinité Élémentaire évolutive](#proposition-e--affinité-élémentaire-évolutive)
6. [Comparatif et recommandations](#-comparatif-et-recommandations)

---

## Proposition A — Arbre de Runes Gravées ⭐ *(recommandée)*

### Concept

Chaque joueur possède un **corps** représenté par **6 emplacements de runes** (tête, torse, bras gauche, bras droit, jambes, pieds). Chaque emplacement accepte une **Rune** (= une compétence). Les runes **interagissent entre elles** selon leur position et leur type, créant des bonus de **résonance** si les bonnes combinaisons sont réunies.

Ce système est inspiré des tatouages magiques dans les MMORPGs coréens (Blade & Soul, Aion) mais avec une mécanique de **puzzle spatial** originale.

### Mécanique détaillée

#### Les Runes
Chaque Rune possède :
- Un **élément** : Feu, Froid, Foudre, Terre, Ombre, Lumière
- Un **type** : Attaque, Défense, Utilitaire, Aura
- Un **niveau** (1 à 5, upgradable avec des `skillPoints`)
- Un **coût en slots** : une rune puissante peut occuper 2 emplacements adjacents

```
Exemple de Rune : "Rune de la Lame Ardente"
  - Élément    : Feu
  - Type       : Attaque
  - Effet L1   : +5% dégâts
  - Effet L5   : +25% dégâts + 10% chance de brûler
  - Coût       : 1 emplacement
```

#### Les Emplacements et la Résonance
Les 6 emplacements forment un schéma corporel. Deux runes **adjacentes** du même élément créent une **Résonance** (+25% d'efficacité des deux runes). Deux runes **opposées** d'éléments contraires créent un **Conflit** (-10% d'efficacité).

```
Schéma des emplacements :
        [TÊTE]
   [BRAS G] [TORSE] [BRAS D]
        [JAMBES]
        [PIEDS]

Adjacences :
  TÊTE    ↔ TORSE, BRAS G, BRAS D
  TORSE   ↔ TÊTE, BRAS G, BRAS D, JAMBES
  JAMBES  ↔ TORSE, PIEDS
```

#### Les Runes Innées de Race
Chaque race commence avec une **Rune Innée verrouillée** dans un emplacement fixe (non retirable) :
- Humain → Torse : Rune de l'Équilibre (tous éléments +5%)
- Elfe → Bras D : Rune du Vent (vitesse attaque +15%)
- Nain → Jambes : Rune de Pierre (armure +20%)
- Orc → Bras G : Rune de Sang (dégâts +20%, -10% déf)
- etc.

### Structure Java

```
skills/
  rune/
    Rune.java              ← interface (extends Skill)
    RuneElement.java       ← enum : FEU, FROID, FOUDRE, TERRE, OMBRE, LUMIERE
    RuneSlot.java          ← enum : HEAD, CHEST, LEFT_ARM, RIGHT_ARM, LEGS, FEET
    RuneRegistry.java      ← charge toutes les runes depuis runes.yml
    RuneResonanceEngine.java ← calcule les bonus/malus de résonance
    impl/
      FireBladeRune.java   ← implémentation concrète
      ...
  RuneInventory.java       ← les 6 slots du joueur (stocké en PDC/YAML)
  RuneGUI.java             ← inventaire 6 slots avec visualisation du corps
```

### Persistance
Les 6 runes équipées + leurs niveaux sont de simples données primitives → tout en **PDC** (6x `STRING` pour le nom + 6x `INTEGER` pour le niveau).

### GUI
Un inventaire 9×3 avec les 6 emplacements positionnés comme le corps humain. Clic gauche sur un slot → ouvre le catalogue des runes disponibles. Clic droit → retire la rune.

### Avantages / Inconvénients
| ✅ Avantages | ❌ Inconvénients |
|---|---|
| Très original, puzzle-like | GUI complexe à faire |
| Forte identité visuelle | Équilibrage délicat |
| Compatible races (rune innée) | Beaucoup de runes à créer |
| Simple à persister | — |

---

## Proposition B — Mémoire Ancestrale

### Concept

Le joueur n'a **pas d'arbre de compétences prédéfini**. Il **absorbe** les compétences en tuant des mobs. Chaque type de mob peut laisser tomber un **Fragment de Mémoire** (item custom) que le joueur consomme pour apprendre une aptitude liée à ce mob. Plus le joueur tue un type de mob, plus il peut monter en grade cette compétence.

Inspiré de : Final Fantasy XIV (Blue Mage), Sekiro (déblocage d'aptitudes sur les boss).

### Mécanique détaillée

#### Fragments de Mémoire
Quand un mob meurt, il a une **chance** de laisser tomber un Fragment :
- Drop rate de base : 5%, modifié par `lootBonus`
- Le Fragment est un item avec NBT custom (nom du mob, tier)

```
Exemples de Fragments :
  Fragment de Zombie    → Compétence "Régénération Putride" (regen HP lente)
  Fragment d'Araignée   → Compétence "Escalade Furtive" (canClimbWalls temporaire)
  Fragment de Creeper   → Compétence "Surcharge Explosive" (AoE dégâts)
  Fragment d'Enderman   → Compétence "Pas de Vide" (téléportation courte)
  Fragment de Blaze      → Compétence "Aura Ardente" (dégâts feu auto)
```

#### Grade de Mémoire
Chaque compétence a 3 grades, débloqués en consommant plusieurs fragments :
- Grade I : 1 fragment → effet de base
- Grade II : 5 fragments → effet amélioré
- Grade III : 20 fragments → effet maximal + effet bonus unique

Le compteur de fragments par mob est stocké en PDC.

#### Limite de Mémoires Actives
Le joueur ne peut activer que **X mémoires simultanément** (X = `skillPoints / 10`, minimum 3). Le reste reste appris mais inactif. Il change ses mémoires actives via un GUI.

### Structure Java

```
skills/
  memory/
    Memory.java             ← interface (extends Skill)
    MemoryGrade.java        ← enum : GRADE_I, GRADE_II, GRADE_III
    MemoryRegistry.java     ← map EntityType → Memory
    MemoryFragment.java     ← helper pour créer/lire les items NBT
    MemoryDropListener.java ← onEntityDeath : chance de drop fragment
    MemoryConsumeListener.java ← onPlayerInteract : consomme le fragment
    MemoryGUI.java          ← voir/activer ses mémoires
```

### Persistance
Par joueur : Map `<String mobType, Integer fragmentCount>` + `List<String> activeMemories` → YAML (map arbitraire, impossible en PDC brut).

### Avantages / Inconvénients
| ✅ Avantages | ❌ Inconvénients |
|---|---|
| Très immersif et narratif | Beaucoup de listeners à gérer |
| Encourage l'exploration | Drop RNG potentiellement frustrant |
| Totalement unique | Difficile d'équilibrer la limite active |
| Chaque kill a un sens | — |

---

## Proposition C — Constellation d'Attributs

### Concept

Inspiré de : Path of Exile (Passive Skill Tree), mais simplifié et thématisé comme une **carte du ciel étoilé**. Le joueur navigue sur un graphe de nœuds. Chaque nœud est une petite amélioration de stat. Les nœuds sont regroupés en **constellations** (= thèmes), et débloquer tous les nœuds d'une constellation active un bonus de complétion.

### Mécanique détaillée

#### Structure du Graphe
Le graphe est défini dans un fichier `constellation.yml`. Chaque nœud a :
- Un ID
- Un type : `MINOR` (petit bonus, 1 pt), `MAJOR` (gros bonus, 3 pts), `KEYSTONE` (passive majeure, 5 pts)
- Des connexions vers d'autres nœuds
- Un coût en `skillPoints`

Le joueur commence à un **nœud de départ propre à sa race** et avance dans le graphe.

#### Constellations thématiques
```
La Grande Épée     → Force, dégâts, critique
Le Chasseur Agile  → Dextérité, esquive, vitesse
La Forteresse      → Constitution, armure, HP
Le Savant          → Intelligence, mana, XP bonus
Le Marchand        → Charisme, loot bonus, réduction coûts
```

#### Nœuds Keystone (choix exclusifs)
Des nœuds très puissants avec un **trade-off** fort :
```
"Berserker"    → +50% dégâts mais -30% armure
"Mort Vivant"  → Immunité au poison mais -20% soin reçu
"Vampirisme"   → Vol de vie sur coup mais pas de regen naturelle
```

### Structure Java

```
skills/
  constellation/
    Node.java               ← données d'un nœud (id, type, stat, valeur, connexions)
    NodeType.java           ← enum : MINOR, MAJOR, KEYSTONE
    Constellation.java      ← groupe de nœuds + bonus de complétion
    ConstellationGraph.java ← graphe complet chargé depuis constellation.yml
    ConstellationService.java ← logique d'achat de nœuds, validation de chemin
    ConstellationGUI.java   ← affichage paginé (carte par région du graphe)
```

### Persistance
`Set<Integer> unlockedNodeIds` → liste d'entiers, stockée comme `String` séparé par virgules en PDC. Simple et efficace.

### GUI
Le plus complexe de tous à afficher : grille de nœuds avec symboles (items dans inventaire), nœuds accessibles en vert, verrouillés en rouge, débloqués en gold. Navigation par pages/régions.

### Avantages / Inconvénients
| ✅ Avantages | ❌ Inconvénients |
|---|---|
| Énorme profondeur de build | GUI très difficile à afficher en inventaire Bukkit |
| Très familier des joueurs MMORPG | Long à équilibrer |
| Configurable en YAML | — |
| Keystones = choix forts, discussions de build | — |

---

## Proposition D — Synergies & Combos Enchaînés

### Concept

Système **combo-driven** : le joueur achète des **compétences individuelles** avec ses `skillPoints` (simple arbre linéaire par catégorie), mais la vraie profondeur vient des **Synergies** : certaines combinaisons de compétences actives déclenchent un **effet composite** automatique.

Inspiré de : League of Legends (synergies objets), TFT (synergies traits), mais adapté à un RPG Minecraft.

### Mécanique détaillée

#### Compétences de base
Chaque `SkillCategory` a **5 compétences** débloquables en séquence (prérequis = avoir la précédente) :
```
Catégorie COMBAT :
  Niv1 → Frappe Lourde        (+10% dégâts melee)
  Niv2 → Coup Critique        (+5% crit chance)
  Niv3 → Fente                (dash + attaque)
  Niv4 → Berserk              (buff temporaire force)
  Niv5 → Exécution            (instakill < 10% HP)
```

#### Synergies
Les synergies sont définies dans un fichier `synergies.yml`. Elles se déclenchent quand le joueur possède **exactement les compétences requises** :

```yaml
synergy_blade_and_shadow:
  name: "Lame des Ombres"
  requires:
    - combat.frappe_lourde
    - stealth.pas_feutres
  effect: "Les attaques depuis la furtivité ont +100% de dégâts"
  type: PASSIVE

synergy_berserker_alchemist:
  name: "Rage Alchimique"
  requires:
    - combat.berserk
    - alchemy.potion_force
  effect: "Berserk se déclenche automatiquement à <30% HP"
  type: TRIGGERED
```

#### Synergies Secrètes
Certaines synergies ne sont pas listées dans le GUI — le joueur les découvre en débloquant les bonnes compétences. Un message lui indique "✦ Nouvelle synergie découverte !".

### Structure Java

```
skills/
  tree/
    BaseSkill.java          ← implémente Skill, compétence simple
    SkillTree.java          ← arbre par SkillCategory, chargé depuis skills.yml
    SkillTreeGUI.java       ← GUI d'achat, progression visible
  synergy/
    Synergy.java            ← interface : requises, effet, type
    SynergyRegistry.java    ← charge synergies.yml, index par Set<SkillId>
    SynergyEngine.java      ← recalcule les synergies actives quand le joueur achète
    SynergyGUI.java         ← onglet séparé, liste les synergies découvertes
```

### Persistance
`Set<String> unlockedSkillIds` → String CSV en PDC. Les synergies sont recalculées au load, pas stockées.

### Avantages / Inconvénients
| ✅ Avantages | ❌ Inconvénients |
|---|---|
| Facile à comprendre au début | Peut devenir complexe à équilibrer |
| Profondeur cachée (synergies secrètes) | Nombreuses combinaisons à tester |
| 100% configurable en YAML | — |
| Système évolutif (ajout de synergies) | — |

---

## Proposition E — Affinité Élémentaire Évolutive

### Concept

Pas de points à dépenser, pas d'arbre à naviguer. Les compétences se **débloquent seules** selon comment le joueur joue. Le système observe son comportement et développe ses **Affinités** :
- Tu fais beaucoup de dégâts corps à corps → ton Affinité de Feu monte
- Tu minas beaucoup → ton Affinité de Terre monte
- Tu vis la nuit / dans des grottes → ton Affinité d'Ombre monte

Quand une Affinité atteint certains seuils, des compétences passives se débloquent automatiquement. Le joueur ne choisit rien, son style de jeu forge son personnage.

Inspiré de : Runescape (compétences montant avec la pratique), mais avec une couche thématique élémentaire.

### Mécanique détaillée

#### Les 6 Affinités
```
🔥 Feu     → combat offensif, mob kills
❄️ Froid   → défense, survie, résistances  
⚡ Foudre  → vitesse, agilité, esquive
🌍 Terre   → minage, farming, craft
🌊 Eau     → pêche, nage, biomes aquatiques
🌑 Ombre   → furtivité, nuit, grottes
```

Chaque affinité a un **score de 0 à 1000**. Les actions rapportent des points :
```
Tuer un mob au corps à corps      → +2 Feu
Recevoir des dégâts et survivre   → +3 Froid
Sprinter 50 blocs                 → +1 Foudre
Miner un bloc de minerai          → +2 Terre
Pêcher un poisson                 → +5 Eau
Rester dans le noir (0 lumière)   → +1/s Ombre
```

#### Compétences débloquées par seuil
Chaque affinité débloque des compétences à 100, 300, 600 et 1000 points :
```
Affinité Feu 100  → "Brûlure Résiduelle" (chance d'enflammer l'ennemi)
Affinité Feu 300  → "Colère du Guerrier" (buff force quand <50% HP)
Affinité Feu 600  → "Âme du Bûcher" (immunité à la brûlure)
Affinité Feu 1000 → "Avatar du Feu" (passive légendaire : les kills restituent 5 HP)
```

#### Affinité de Race
Chaque race démarre avec une affinité bonus :
```
Orc    → Feu   +200 (démarre déjà avec Brûlure Résiduelle)
Elfe   → Foudre +200
Nain   → Terre +200
Poisson → Eau  +400
etc.
```

#### Plafonnement et Spécialisation
Le total de points dans toutes les affinités est plafonné à **2000**. Si le joueur atteint le plafond, il doit "réinitialiser" une affinité (via commande ou item) pour en monter une autre. Cela crée un choix de **spécialisation**.

### Structure Java

```
skills/
  affinity/
    AffinityType.java       ← enum : FEU, FROID, FOUDRE, TERRE, EAU, OMBRE
    AffinityData.java       ← score actuel + compétences débloquées
    AffinityRegistry.java   ← charge les seuils et compétences depuis affinity.yml
    AffinityTracker.java    ← observer de tous les events (combat, mine, pêche...)
    AffinityService.java    ← addScore(), checkThresholds(), notifyUnlock()
    AffinityGUI.java        ← jauge visuelle par affinité + compétences débloquées
```

### Persistance
6 entiers (un par affinité) → **6 clés PDC** INTEGER. Les compétences débloquées sont recalculées au load depuis les scores, donc pas besoin de les stocker.

### Avantages / Inconvénients
| ✅ Avantages | ❌ Inconvénients |
|---|---|
| Zéro interface à apprendre | Moins de contrôle pour le joueur |
| Narrative parfaite (personnage = playstyle) | Risque de farming artificiel d'une affinité |
| Très original, rare dans les plugins | Équilibrage des gains/seuils très long |
| Persistance ultra simple | Peut sembler "invisible" si mal communiqué |

---

## 📊 Comparatif et Recommandations

| | A — Runes | B — Mémoire | C — Constellation | D — Synergies | E — Affinité |
|---|:---:|:---:|:---:|:---:|:---:|
| **Originalité** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Facilité d'implémentation** | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Facilité pour le joueur** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Profondeur de build** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Compatibilité races** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Persistance simple** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Rejouabilité** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |

### 🎯 Recommandation personnelle

Si tu veux **quelque chose de vraiment unique** qui se démarque de tous les autres plugins Minecraft → **Proposition A (Runes)** ou **Proposition E (Affinité)**.

- **A (Runes)** si tu veux un système avec un GUI fort, visible, et des choix actifs du joueur.
- **E (Affinité)** si tu veux quelque chose de narratif où le personnage se forge naturellement — aucun plugin populaire ne fait ça.
- **D (Synergies)** si tu veux la solution la plus maintenable et évolutive sur le long terme.

### 💡 Idée bonus : combiner A + E
**Runer l'Affinité** : les Affinités (système E) débloquent des **Runes spéciales** que le joueur peut ensuite équiper dans ses slots (système A). Les deux systèmes deviennent complémentaires : le playstyle débloque du contenu, les slots créent les choix tactiques.

---

## 🔧 Points techniques communs à tous les systèmes

### GUI
Tous les systèmes nécessitent au moins un inventaire custom. Il est conseillé de créer une classe abstraite `RPGGui` avec les helpers habituels (fond de verre, titres, pagination).

### Configuration YAML
Toutes les compétences/runes/nœuds devraient être **définies en YAML** et chargées au démarrage via un Registry. Cela permet de tout modifier sans recompiler.

### Intégration `skillPoints`
Le champ `skillPoints` sur `RPGPlayer` est déjà en place. Il alimente le coût d'achat quelle que soit la proposition choisie. `skillPointsPerLevel` (défaut = 3) contrôle la progression.

### Intégration `InnateSkills`
L'interface `InnateSkills` est déjà définie et relie des compétences à des races. Elle est prête à être implémentée dans les enums de race pour accorder les bonus de départ décrits dans chaque proposition.

### Tests
Prévoir dès le début une commande admin `/chrpg skill <give|reset> <joueur>` pour tester facilement sans grinder.
