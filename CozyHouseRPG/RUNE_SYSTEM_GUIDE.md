# 🔮 Système de Tessères

---

## 1. L'idée en deux phrases

Le joueur choisit **6 Tessères** et les grave sur **6 emplacements de son corps**.
Le jeu analyse la combinaison et révèle **3 compétences** (`Skill`), plus ou moins
puissantes selon la qualité du build.

---

## 2. Ce qui existe déjà — NE PAS RETOUCHER

| Fichier | Rôle dans le système Tessère |
|---|---|
| `skills/datas/TessereSlot.java` | Les 6 emplacements (HEAD, CHEST, etc.), déjà complet |
| `skills/tessere/TessereLoadout.java` | L'état de gravure du joueur — **c'est notre TessereGravure** |
| `skills/tessere/TessereRarity.java` | Rarité BRUT → ECLAT avec niveau requis |
| `skills/tessere/Tessere.java` | Objet d'affichage d'une Tessère, `buildItem()` déjà complet |
| `skills/tessere/TessereCalcul.java` | Calcul de résonance entre deux Tessères, déjà complet |
| `skills/tessere/Domaines.java` | Les 13 domaines du lore avec leur Material |
| `skills/Skill.java` | Interface que `TessereSkill` va implémenter |
| `skills/SkillType.java` | PASSIVE / ACTIVE / TOGGLE / ULTIMATE / TRIGGERED |
| `skills/SkillCategory.java` | COMBAT, MAGIC, STEALTH, etc. |
| `status/Status.java` | Interface pour les effets de statut |
| `status/StatusFlagEnum.java` | Flags : BUFF, STUN, PERIODIC, etc. |

**Dossiers vides à utiliser :**
- `skills/gui/` → GUI des Tessères
- `skills/logic/` → TessereService (toute la logique)

---

## 3. Architecture du code

```
Ce qu'on construit + ce qui existe déjà

DONNÉES STATIQUES
├── [EXISTE]  TessereSlot         — 6 emplacements du corps
├── [EXISTE]  TessereRarity       — BRUT, TAILLE, FACETTE, ECLAT
├── [EXISTE]  Domaines            — 13 domaines du lore
├── [CRÉER]   TessereType         — enum des 20 Tessères avec leurs données
│                                   (slotEfficiency, raceAffinity, calcul, rareté...)
└── [CRÉER]   TessereSkill        — enum des ~20 compétences, implémente Skill

ÉTAT DU JOUEUR
└── [EXISTE]  TessereLoadout      — les 6 Tessères gravées + les 3 Skill révélés
             (dans skills/tessere/)  + resonanceScore + efficaciteScore (à ajouter)

DISPLAY
└── [EXISTE + MODIFIER]  Tessere  — ajouter le champ TessereType pour savoir
                                     quelle Tessère un objet représente

LOGIQUE
└── [CRÉER]   TessereService      — dans skills/logic/

INTERFACE
├── [CRÉER]   TessereBodyGUI      — dans skills/gui/
├── [CRÉER]   TessereCatalogGUI   — dans skills/gui/
├── [CRÉER]   TessereGUIListener  — dans skills/gui/
└── [CRÉER]   TessereEffectListener — dans listeners/

FICHIERS EXISTANTS À MODIFIER
├── Tessere.java              → ajouter champ TessereType type
├── TessereLoadout.java       → ajouter champ double efficaciteScore +
│                               modifier revealSkills() pour le prendre
├── RPGPlayer.java            → remplacer List<Tessere> discoveryTessere
│                               par TessereLoadout tessereLoadout
├── PlayerDataManager.java    → ajouter 9 clés PDC pour sauvegarder la gravure
└── PluginContext.java        → ajouter TessereService tessereService
```

**Total : 4 fichiers à créer, 5 à modifier.**

---

## 4. Les concepts clés

### TessereType — les données statiques des 20 Tessères

Chaque Tessère a deux tableaux de données spécifiques à ce système :

- **`slotEfficiency[6]`** — à quel point cette Tessère est efficace dans chaque emplacement.  
  Indexé par `TessereSlot.ordinal()` : HEAD=0, CHEST=1, RIGHT_ARM=2, LEFT_ARM=3, RIGHT_LEG=4, LEFT_LEG=5.

- **`raceAffinity[8]`** — à quel point cette Tessère est adaptée à chaque race.  
  Indexé par `RaceType.ordinal()` : HUMAN=0, ELF=1, DWARF=2, ORC=3, GOBLIN=4, FAIRY=5, BEAST=6, FISH=7.

Valeurs : `1.0` = neutre · `≥1.3` = excellent · `≤0.7` = mauvais.

### TessereSkill implémente Skill

`TessereSkill` est un enum qui implémente l'interface `Skill` existante.
Les méthodes complexes (cooldown joueur, etc.) sont déléguées au service.

### TessereLoadout — ce qu'il fait déjà

```
TessereLoadout (existant) :
  Map<TessereSlot, Tessere>  equippedFragments  → les 6 Tessères gravées
  List<Skill>                activeSkills        → les 3 compétences révélées
  int                        resonanceScore      → score de résonance brut
  boolean                    revealed            → compétences révélées ?
  boolean                    dirty               → gravure modifiée ?

  equipFragment(slot, tessere)  → grave une Tessère
  unequipFragment(slot)         → retire une Tessère
  isComplete()                  → 6 slots remplis ?
  revealSkills(skills, resonance) → sauvegarde les compétences

À AJOUTER :
  double                     efficaciteScore     → score global 0.0-1.0
  (+ modifier revealSkills pour le prendre en paramètre)
```

### Score d'efficacité

```
Score = (Résonance × 40%) + (Efficacité slots × 30%) + (Affinité race × 30%)

Résonance (0.0 → 1.0)
  → On calcule toutes les paires de Tessères (15 paires pour 6 Tessères)
  → Via TessereCalcul.calculResonance() qui retourne Compatibility.number (0-5)
  → total / (paires × 5) = résonance normalisée

Efficacité slots (0.5 → 1.5)
  → Moyenne des slotEfficiency des 6 Tessères dans leurs slots respectifs

Affinité race (0.5 → 1.5)
  → Moyenne des raceAffinity des 6 Tessères pour la race du joueur

Score final normalisé sur 0.0 → 1.0 :
  score = resonance×0.40 + (effSlots/1.5)×0.30 + (affRace/1.5)×0.30

Effet sur les compétences :
  efficacite 0.0 → compétences à 50% de leur puissance de base
  efficacite 0.5 → puissance de base
  efficacite 1.0 → puissance ×1.5
  Formule : param × (0.5 + score)
```

### Les 20 Tessères disponibles

| Nom | Domaine | Meilleurs slots | Meilleures races |
|---|---|---|---|
| THERMIQUE | ENERGY | R_ARM, L_ARM | Nain, Orc |
| CINETIQUE | ENERGY | R_ARM, L_ARM | Orc, Bête |
| LUMINEUSE | ENERGY | HEAD | Fée, Elfe |
| SOLIDIFICATION | MATTER | CHEST | Nain, Orc |
| DENSITE | MATTER | R_LEG, L_LEG | Nain, Orc |
| TRANSLATION | SPACE | R_LEG, L_LEG | Gobelin, Poisson, Elfe |
| DIMENSION | SPACE | R_ARM, L_ARM | Fée, Gobelin |
| ACCELERATION | TIME | HEAD | Gobelin, Elfe |
| DUREE | TIME | HEAD, CHEST | Fée |
| VITALITE | LIFE | CHEST | Bête, Nain |
| CROISSANCE | LIFE | R_LEG, L_LEG | Bête |
| REGENERATION | LIFE | CHEST | Bête, Nain |
| CLARTE | AWARENESS | HEAD | Elfe, Fée |
| PERCEPTION | AWARENESS | HEAD | Elfe, Gobelin |
| EQUILIBRE | HARMONY | HEAD, CHEST | Humain |
| COHESION | HARMONY | HEAD, CHEST | Humain, Fée |
| INSTABILITE | CHAOS | R_ARM, L_ARM | Orc, Bête |
| EXTINCTION | EXISTENCE | R_ARM, L_ARM | Orc |
| OCCULTATION | EXISTENCE | HEAD, L_ARM | Gobelin |
| ASCENSION | TRANSCENDENCE | HEAD, CHEST | Fée |

---

## 5. Le code

---

### Modifier : TessereLoadout.java

Ajouter le champ `efficaciteScore` et modifier `revealSkills` pour le prendre.

```java
// Ajouter ce champ :
private double efficaciteScore = 0.0;

// Remplacer :
public void revealSkills(List<Skill> skills, int resonance) {

// Par :
public void revealSkills(List<Skill> skills, int resonance, double efficaciteScore) {
    this.activeSkills = new ArrayList<>(skills);
    this.resonanceScore = resonance;
    this.efficaciteScore = efficaciteScore;
    this.revealed = true;
    this.dirty = false;
}
```

---

### Modifier : Tessere.java

Ajouter le champ `type` pour savoir quelle Tessère cet objet représente,
et `rarity` pour lier à `TessereRarity` existant.

```java
// Dans la classe Tessere, ajouter deux champs via @Builder :
private final TessereType type;          // ← identifiant de la Tessère
private final TessereRarity rarity;      // ← BRUT, TAILLE, FACETTE, ECLAT

// Dans buildItem(), ajouter dans le lore (après la signature) :
lore.add(ChatColor.DARK_GRAY + "Rareté : " + rarity.getDisplayName());
```

---

### Créer : TessereType.java

```java
package fr.cozyhouse.cozyHouseRPG.skills.tessere;

import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.datas.Domaines;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereRarity;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.logic.TessereCalcul;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum TessereType {

  // format des tableaux :
  // slotEfficiency : [HEAD, CHEST, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG]
  // raceAffinity   : [HUMAN, ELF, DWARF, ORC, GOBLIN, FAIRY, BEAST, FISH]

  // ─── Domaine ÉNERGIE ───────────────────────────────────────────────────
  THERMIQUE(
          Domaines.ENERGY, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.ASCENDING, Domaines.ENERGY),
          Material.BLAZE_POWDER, ChatColor.RED,
          "Thermique",
          "L'essence de la chaleur. Se propage, consume, transforme.",
          "Mendel disait : le feu est une Tessère vivante — elle veut se propager.",
          new double[]{0.6, 0.8, 1.4, 1.3, 0.7, 0.7},
          new double[]{1.0, 0.8, 1.3, 1.3, 1.0, 0.7, 1.2, 0.6}
  ),
  CINETIQUE(
          Domaines.ENERGY, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.ENERGY),
          Material.AMETHYST_SHARD, ChatColor.LIGHT_PURPLE,
          "Cinétique",
          "La force du mouvement à l'état pur. Ce qui frappe, ce qui propulse.",
          "Chaque Orc est un fragment de Cinétique incarné.",
          new double[]{0.7, 0.9, 1.4, 1.3, 1.0, 0.9},
          new double[]{1.0, 0.9, 1.2, 1.4, 1.1, 0.7, 1.3, 0.8}
  ),
  LUMINEUSE(
          Domaines.ENERGY, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.ENERGY),
          Material.GLOWSTONE_DUST, ChatColor.YELLOW,
          "Lumineuse",
          "La lumière comme vecteur d'énergie. Révèle, amplifie, protège.",
          "Glenn appelait la Lumineuse la Tessère des mages fragiles. Il avait tort.",
          new double[]{1.3, 1.0, 0.9, 1.0, 0.7, 0.7},
          new double[]{1.0, 1.2, 0.8, 0.7, 0.9, 1.5, 1.0, 0.9}
  ),

  // ─── Domaine MATIÈRE ───────────────────────────────────────────────────
  SOLIDIFICATION(
          Domaines.MATTER, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.MATTER),
          Material.IRON_INGOT, ChatColor.GRAY,
          "Solidification",
          "La Matière qui se durcit, qui résiste. La Tessère des bâtisseurs.",
          "Les Nains ont gravé la Solidification avant d'apprendre à parler.",
          new double[]{0.6, 1.4, 1.0, 1.0, 0.9, 0.9},
          new double[]{1.0, 0.8, 1.4, 1.2, 0.8, 0.6, 1.1, 0.9}
  ),
  DENSITE(
          Domaines.MATTER, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.DESCENDING, Domaines.MATTER),
          Material.STONE, ChatColor.DARK_GRAY,
          "Densité",
          "Plus lourd que l'air, plus stable que la roche.",
          "Celui qui porte la Densité ne trébuche pas.",
          new double[]{0.6, 1.0, 0.8, 0.8, 1.3, 1.4},
          new double[]{1.0, 0.7, 1.3, 1.3, 0.7, 0.6, 1.1, 1.0}
  ),

  // ─── Domaine ESPACE ────────────────────────────────────────────────────
  TRANSLATION(
          Domaines.SPACE, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.SPACE),
          Material.ENDER_PEARL, ChatColor.AQUA,
          "Translation",
          "La position change. L'être reste. L'Espace ne fait que ça.",
          "Les Celestials voyageaient en Espace comme on marche dans un couloir.",
          new double[]{0.8, 0.7, 0.9, 0.9, 1.4, 1.4},
          new double[]{1.0, 1.3, 0.8, 0.9, 1.4, 1.2, 1.2, 1.3}
  ),
  DIMENSION(
          Domaines.SPACE, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.ASCENDING, Domaines.SPACE),
          Material.END_CRYSTAL, ChatColor.DARK_AQUA,
          "Dimension",
          "La taille de ce qui est. L'étendue d'une présence dans l'Espace.",
          "La Dimension, c'est la Tessère de la présence.",
          new double[]{1.0, 0.8, 1.2, 1.2, 0.9, 0.9},
          new double[]{1.0, 1.1, 0.9, 1.0, 1.2, 1.3, 0.9, 1.0}
  ),

  // ─── Domaine TEMPS ─────────────────────────────────────────────────────
  ACCELERATION(
          Domaines.TIME, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.ASCENDING, Domaines.TIME),
          Material.CLOCK, ChatColor.GOLD,
          "Accélération",
          "Le Temps qui presse. Tout va plus vite — y compris tes réflexes.",
          "Un mage du Temps ne court pas. Il arrive avant.",
          new double[]{1.3, 0.7, 1.0, 1.0, 1.2, 1.0},
          new double[]{1.0, 1.2, 0.9, 1.0, 1.4, 1.1, 1.1, 0.9}
  ),
  DUREE(
          Domaines.TIME, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.TIME),
          Material.DAYLIGHT_DETECTOR, ChatColor.WHITE,
          "Durée",
          "Prolonge. Étire. Maintient ce qui devrait s'effacer.",
          "La Durée est la Tessère des patients.",
          new double[]{1.1, 1.2, 0.8, 0.9, 0.9, 0.9},
          new double[]{1.1, 1.0, 1.0, 0.9, 1.0, 1.3, 0.9, 1.0}
  ),

  // ─── Domaine VIE ───────────────────────────────────────────────────────
  VITALITE(
          Domaines.LIFE, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
          Material.GLISTERING_MELON_SLICE, ChatColor.GREEN,
          "Vitalité",
          "L'élan de la vie. Ce qui bat, ce qui pulse, ce qui résiste.",
          "Mendel ne put jamais expliquer pourquoi certains ont plus de Vitalité que d'autres.",
          new double[]{0.8, 1.4, 0.9, 0.9, 1.0, 1.0},
          new double[]{1.0, 1.1, 1.2, 1.1, 0.9, 0.9, 1.3, 1.0}
  ),
  CROISSANCE(
          Domaines.LIFE, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
          Material.WHEAT_SEEDS, ChatColor.DARK_GREEN,
          "Croissance",
          "Ce qui était faible devient fort. La Tessère sans plafond.",
          "La Croissance est la seule Tessère qui ne connaît pas de plafond.",
          new double[]{0.9, 1.1, 0.9, 0.9, 1.2, 1.2},
          new double[]{1.0, 1.2, 1.0, 1.0, 1.0, 1.1, 1.3, 1.0}
  ),
  REGENERATION(
          Domaines.LIFE, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.LIFE),
          Material.GOLDEN_APPLE, ChatColor.GREEN,
          "Régénération",
          "La Vie qui se répare. La blessure qui se referme d'elle-même.",
          "Les Bêtes régénèrent sans le savoir. Les mages le font avec intention.",
          new double[]{0.9, 1.4, 0.7, 0.7, 1.0, 1.0},
          new double[]{1.0, 1.1, 1.1, 1.0, 0.9, 1.2, 1.3, 1.1}
  ),

  // ─── Domaine CONSCIENCE ────────────────────────────────────────────────
  CLARTE(
          Domaines.AWARENESS, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.AWARENESS),
          Material.SPYGLASS, ChatColor.WHITE,
          "Clarté",
          "La perception aiguisée. Voir ce que les autres ne voient pas.",
          "Glenn gravait toujours Clarté en premier sur ses apprentis.",
          new double[]{1.5, 1.0, 0.7, 0.7, 0.7, 0.7},
          new double[]{1.1, 1.4, 0.9, 0.7, 1.2, 1.3, 0.8, 1.0}
  ),
  PERCEPTION(
          Domaines.AWARENESS, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.AWARENESS),
          Material.WRITABLE_BOOK, ChatColor.AQUA,
          "Perception",
          "Les sens au-delà du normal. Sentir avant de voir.",
          "Un Elfe avec Perception gravée entend les pensées, presque.",
          new double[]{1.4, 0.9, 1.0, 1.0, 0.8, 0.8},
          new double[]{1.1, 1.3, 0.9, 0.8, 1.3, 1.2, 1.0, 1.0}
  ),

  // ─── Domaine HARMONIE ──────────────────────────────────────────────────
  EQUILIBRE(
          Domaines.HARMONY, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.NEUTRAL, TessereCalcul.Entropy.BALANCE, TessereCalcul.FLOW.STABLE, Domaines.HARMONY),
          Material.BEACON, ChatColor.WHITE,
          "Équilibre",
          "Ni trop fort, ni trop faible. La Tessère de ceux qui durent.",
          "Les Humains comprennent l'Équilibre instinctivement.",
          new double[]{1.1, 1.2, 1.0, 1.0, 1.0, 1.0},
          new double[]{1.4, 1.1, 1.0, 0.8, 1.0, 1.2, 0.9, 1.1}
  ),
  COHESION(
          Domaines.HARMONY, TessereRarity.BRUT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.STABLE, Domaines.HARMONY),
          Material.NAME_TAG, ChatColor.AQUA,
          "Cohésion",
          "Le lien entre les êtres. Ce qui unit les Tessères entre elles.",
          "La Cohésion n'existe que quand plusieurs la cherchent ensemble.",
          new double[]{1.2, 1.2, 0.9, 0.9, 0.9, 0.9},
          new double[]{1.3, 1.0, 1.0, 0.8, 1.0, 1.3, 0.9, 1.0}
  ),

  // ─── Domaine CHAOS ─────────────────────────────────────────────────────
  INSTABILITE(
          Domaines.CHAOS, TessereRarity.FACETTE,
          new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.CHAOS),
          Material.SCULK, ChatColor.DARK_RED,
          "Instabilité",
          "Le Chaos gravé dans la chair. Imprévisible. Dangereux.",
          "Les Orcs gravent l'Instabilité par bravade. Certains survivent.",
          new double[]{0.9, 0.9, 1.2, 1.3, 1.0, 1.0},
          new double[]{0.9, 0.8, 1.1, 1.4, 1.2, 0.7, 1.4, 0.9}
  ),

  // ─── Domaine EXISTENCE ─────────────────────────────────────────────────
  EXTINCTION(
          Domaines.EXISTENCE, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.EXISTENCE),
          Material.WITHER_ROSE, ChatColor.DARK_GRAY,
          "Extinction",
          "Ce qui diminue, s'efface, s'oublie progressivement.",
          "L'Extinction n'est pas la Mort. C'est pire — c'est l'oubli progressif.",
          new double[]{0.9, 0.8, 1.3, 1.3, 0.9, 0.9},
          new double[]{0.9, 0.8, 1.0, 1.3, 1.1, 0.7, 1.2, 0.9}
  ),
  OCCULTATION(
          Domaines.EXISTENCE, TessereRarity.TAILLE,
          new TessereCalcul(TessereCalcul.Charge.NEGATIVE, TessereCalcul.Entropy.CHAOS, TessereCalcul.FLOW.DESCENDING, Domaines.EXISTENCE),
          Material.INK_SAC, ChatColor.DARK_PURPLE,
          "Occultation",
          "L'absence de présence. Être là sans être perçu.",
          "Le meilleur assassin est celui qui n'existe pas.",
          new double[]{1.2, 0.7, 1.0, 1.2, 1.1, 1.1},
          new double[]{0.9, 1.0, 0.8, 0.9, 1.4, 0.9, 1.1, 1.0}
  ),

  // ─── Domaine TRANSCENDANCE ─────────────────────────────────────────────
  ASCENSION(
          Domaines.TRANSCENDANCE, TessereRarity.ECLAT,
          new TessereCalcul(TessereCalcul.Charge.POSITIVE, TessereCalcul.Entropy.ORDER, TessereCalcul.FLOW.ASCENDING, Domaines.TRANSCENDANCE),
          Material.NETHER_STAR, ChatColor.GOLD,
          "Ascension",
          "Le dépassement de soi. La Tessère que les Celestials gardaient pour eux.",
          "Très peu maîtrisent l'Ascension. Ceux qui y arrivent ne reviennent pas inchangés.",
          new double[]{1.3, 1.2, 1.0, 1.0, 1.1, 1.1},
          new double[]{1.1, 1.2, 1.0, 0.9, 1.0, 1.5, 1.0, 1.0}
  );

  // Note : Domaines.TRANSCENDANCE (sans E final) si c'est le nom dans ton enum.
  // Vérifie le nom exact dans Domaines.java et adapte si besoin.

  private final Domaines domaine;
  private final TessereRarity rarity;
  private final TessereCalcul calcul;
  private final Material iconMaterial;
  private final ChatColor color;
  private final String displayName;
  private final String description;
  private final String loreQuote;
  private final double[] slotEfficiency;
  private final double[] raceAffinity;

  /** Efficacité de cette Tessère dans un slot donné. */
  public double getSlotEfficiency(TessereSlot slot) {
    return slotEfficiency[slot.ordinal()];
  }

  /** Affinité de cette Tessère pour une race donnée. */
  public double getRaceAffinity(RaceType race) {
    return raceAffinity[race.ordinal()];
  }

  /**
   * Construit l'objet Tessere correspondant à ce TessereType.
   * Utilisé par TessereService pour peupler le catalogue GUI.
   */
  public Tessere buildTessere() {
    return Tessere.builder()
            .type(this)
            .rarity(this.rarity)
            .DisplayName(this.displayName)
            .Description(this.description)
            .LoreQuote(this.loreQuote)
            .color(this.color)
            .tessereCalcul(this.calcul)
            .iconMaterial(this.iconMaterial)
            .build();
  }

  /** Label coloré pour afficher l'efficacité dans le lore du GUI. */
  public static String labelEff(double v) {
    String pct = String.format("%.0f%%", v * 100);
    if (v >= 1.3) return ChatColor.GREEN + "✦ Excellent (" + pct + ")";
    if (v >= 1.0) return ChatColor.YELLOW + "◆ Bon (" + pct + ")";
    return ChatColor.RED + "▼ Faible (" + pct + ")";
  }
}
```

> **Note** : le `@RequiredArgsConstructor` de Lombok génère le constructeur depuis les champs `final`.
> Le problème est que les enums avec beaucoup de paramètres ne peuvent pas utiliser `@RequiredArgsConstructor`
> directement — Lombok ne supporte pas les enums pour ça. Il faut écrire le constructeur manuellement :
>
> ```java
> // Remplace @RequiredArgsConstructor par ce constructeur dans TessereType.java :
> TessereType(Domaines domaine, TessereRarity rarity, TessereCalcul calcul,
>             Material iconMaterial, ChatColor color,
>             String displayName, String description, String loreQuote,
>             double[] slotEfficiency, double[] raceAffinity) {
>     this.domaine = domaine;
>     this.rarity = rarity;
>     this.calcul = calcul;
>     this.iconMaterial = iconMaterial;
>     this.color = color;
>     this.displayName = displayName;
>     this.description = description;
>     this.loreQuote = loreQuote;
>     this.slotEfficiency = slotEfficiency;
>     this.raceAffinity = raceAffinity;
> }
> ```

---

### Créer : TessereSkill.java

`TessereSkill` est un enum qui implémente l'interface `Skill` existante.
Les méthodes liées au joueur (cooldown restant, etc.) retournent des valeurs
neutres — le service gère la logique réelle.

```java
package fr.cozyhouse.cozyHouseRPG.skills.tessere;

import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.SkillCategory;
import fr.cozyhouse.cozyHouseRPG.skills.SkillType;
import fr.cozyhouse.cozyHouseRPG.status.Status;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum TessereSkill implements Skill {

    // format : (displayName, SkillType, SkillCategory, ChatColor, Material icône, valeur de base, paramètres)

    // ─── Offensives ────────────────────────────────────────────────────────────
    MARQUE_ARDENTE    ("Marque Ardente",      SkillType.PASSIVE,   SkillCategory.COMBAT,     ChatColor.RED,          Material.BLAZE_ROD,
                       "Frappes enflamment la cible.", 0.30,
                       Map.of("chance", 0.30, "duree_ticks", 60.0)),

    IMPACT_CINETIQUE  ("Impact Cinétique",    SkillType.PASSIVE,   SkillCategory.COMBAT,     ChatColor.LIGHT_PURPLE, Material.AMETHYST_SHARD,
                       "Knockback amplifié sur chaque frappe.", 0.6,
                       Map.of("puissance", 0.6)),

    LAME_DU_VIDE      ("Lame du Vide",        SkillType.PASSIVE,   SkillCategory.STEALTH,    ChatColor.DARK_PURPLE,  Material.INK_SAC,
                       "+40% dégâts quand accroupi.", 0.40,
                       Map.of("bonus", 0.40)),

    DECLIN_FORCE      ("Déclin de Force",     SkillType.TRIGGERED, SkillCategory.COMBAT,     ChatColor.DARK_GRAY,    Material.WITHER_ROSE,
                       "Applique Faiblesse sur la cible à chaque frappe.", 0.0,
                       Map.of("duree_ticks", 80.0)),

    CHOC_DIMENSIONNEL ("Choc Dimensionnel",   SkillType.ACTIVE,    SkillCategory.MAGIC,      ChatColor.DARK_AQUA,    Material.END_CRYSTAL,
                       "AoE de dégâts autour de toi.", 4.0,
                       Map.of("rayon", 3.0, "degats", 4.0, "cooldown_ticks", 300.0)),

    // ─── Défensives ────────────────────────────────────────────────────────────
    PEAU_DE_PIERRE    ("Peau de Pierre",      SkillType.PASSIVE,   SkillCategory.DEFENSE,    ChatColor.GRAY,         Material.IRON_INGOT,
                       "Armure passive permanente.", 3.0,
                       Map.of("bonus_armure", 3.0)),

    ANCRAGE           ("Ancrage",             SkillType.PASSIVE,   SkillCategory.DEFENSE,    ChatColor.DARK_GRAY,    Material.STONE,
                       "Résistance au recul passive.", 0.2,
                       Map.of("bonus_kb", 0.2)),

    REGENERATION_HP   ("Régénération Vitale", SkillType.PASSIVE,   SkillCategory.DEFENSE,    ChatColor.GREEN,        Material.GOLDEN_APPLE,
                       "Regagne des HP lentement en combat.", 0.5,
                       Map.of("hp_par_sec", 0.5)),

    VITALITE_ELARGIE  ("Vitalité Élargie",    SkillType.PASSIVE,   SkillCategory.DEFENSE,    ChatColor.GREEN,        Material.GLISTERING_MELON_SLICE,
                       "HP max augmentés.", 4.0,
                       Map.of("bonus_hp", 4.0)),

    BOUCLIER_LUMINE   ("Bouclier de Lumière", SkillType.PASSIVE,   SkillCategory.MAGIC,      ChatColor.YELLOW,       Material.GLOWSTONE_DUST,
                       "Réduit les dégâts magiques.", 0.25,
                       Map.of("reduction", 0.25)),

    // ─── Mobilité ──────────────────────────────────────────────────────────────
    BOND_INSTANTANE   ("Bond Instantané",     SkillType.ACTIVE,    SkillCategory.EXPLORATION, ChatColor.AQUA,        Material.ENDER_PEARL,
                       "Dash vers l'avant.", 1.2,
                       Map.of("puissance", 1.2, "cooldown_ticks", 200.0)),

    REFLEXES_ACCRUS   ("Réflexes Accrus",     SkillType.PASSIVE,   SkillCategory.COMBAT,     ChatColor.GOLD,         Material.CLOCK,
                       "Vitesse d'attaque augmentée.", 0.3,
                       Map.of("bonus_attaque", 0.3)),

    // ─── Utilitaires ───────────────────────────────────────────────────────────
    VUE_ARCANIQUE     ("Vue Arcanique",       SkillType.PASSIVE,   SkillCategory.EXPLORATION, ChatColor.WHITE,       Material.SPYGLASS,
                       "Vision nocturne permanente.", 0.0,
                       Map.of()),

    INSTINCT_AFFINE   ("Instinct Affiné",     SkillType.PASSIVE,   SkillCategory.COMBAT,     ChatColor.AQUA,         Material.WRITABLE_BOOK,
                       "Chance de coup critique augmentée.", 0.08,
                       Map.of("bonus_crit", 0.08)),

    HARMONIE_INT      ("Harmonie Intérieure", SkillType.PASSIVE,   SkillCategory.MAGIC,      ChatColor.WHITE,        Material.BEACON,
                       "Légère amélioration de toutes les stats.", 1.0,
                       Map.of("bonus_stats", 1.0)),

    CROISSANCE_ACCL   ("Croissance Accélérée",SkillType.PASSIVE,   SkillCategory.EXPLORATION, ChatColor.DARK_GREEN,  Material.WHEAT_SEEDS,
                       "XP gagné augmenté.", 0.15,
                       Map.of("bonus_xp", 0.15)),

    DUREE_ETENDUE     ("Durée Étendue",       SkillType.PASSIVE,   SkillCategory.MAGIC,      ChatColor.WHITE,        Material.DAYLIGHT_DETECTOR,
                       "Effets positifs durent plus longtemps.", 1.5,
                       Map.of("multiplicateur", 1.5)),

    // ─── Spéciales ─────────────────────────────────────────────────────────────
    CHAOS_LATENT      ("Chaos Latent",        SkillType.TRIGGERED, SkillCategory.COMBAT,     ChatColor.DARK_RED,     Material.SCULK,
                       "Effet aléatoire à chaque frappe.", 0.20,
                       Map.of("chance", 0.20, "mult_min", 0.5, "mult_max", 2.5)),

    EVEIL_ARCANIQUE   ("Éveil Arcanique",     SkillType.PASSIVE,   SkillCategory.MAGIC,      ChatColor.GOLD,         Material.NETHER_STAR,
                       "Toutes les stats +10%.", 0.10,
                       Map.of("pct_boost", 0.10)),

    // ─── Fallbacks ─────────────────────────────────────────────────────────────
    FLUX_I  ("Flux Arcanique I",   SkillType.PASSIVE, SkillCategory.MAGIC, ChatColor.DARK_GRAY, Material.GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02)),
    FLUX_II ("Flux Arcanique II",  SkillType.PASSIVE, SkillCategory.MAGIC, ChatColor.DARK_GRAY, Material.GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02)),
    FLUX_III("Flux Arcanique III", SkillType.PASSIVE, SkillCategory.MAGIC, ChatColor.DARK_GRAY, Material.GRAY_DYE, "Légère amélioration.", 0.02, Map.of("bonus", 0.02));

    // ─── Champs ────────────────────────────────────────────────────────────────

    private final String displayName;
    private final SkillType type;
    private final SkillCategory category;
    private final ChatColor color;
    private final Material iconMaterial;
    private final String description;
    private final double baseValue;
    private final Map<String, Double> params;

    TessereSkill(String displayName, SkillType type, SkillCategory category,
                 ChatColor color, Material iconMaterial, String description,
                 double baseValue, Map<String, Double> params) {
        this.displayName  = displayName;
        this.type         = type;
        this.category     = category;
        this.color        = color;
        this.iconMaterial = iconMaterial;
        this.description  = description;
        this.baseValue    = baseValue;
        this.params       = params;
    }

    // ─── Méthodes utilitaires ──────────────────────────────────────────────────

    /**
     * Retourne un paramètre amplifié par le score d'efficacité de la gravure.
     * score 0.0 → ×0.5 · score 0.5 → ×1.0 · score 1.0 → ×1.5
     */
    public double param(String cle, double efficaciteScore) {
        return params.getOrDefault(cle, 0.0) * (0.5 + efficaciteScore);
    }

    /** Paramètre brut, sans modification par le score (pour min/max d'un range). */
    public double paramBrut(String cle) {
        return params.getOrDefault(cle, 0.0);
    }

    // ─── Implémentation de l'interface Skill ──────────────────────────────────

    @Override public int getId()                       { return ordinal(); }
    @Override public String getName()                  { return name().toLowerCase(); }
    @Override public String getDisplayName()           { return displayName; }
    @Override public String getDescription()           { return description; }
    @Override public SkillType getType()               { return type; }
    @Override public SkillCategory getCategory()       { return category; }
    @Override public boolean isUnlocked()              { return true; }
    @Override public boolean isEnabled()               { return true; }
    @Override public int getMaxLevel()                 { return 1; }
    @Override public int getCurrentLevel()             { return 1; }
    @Override public ChatColor getColor()              { return color; }
    @Override public long getCooldownTicks()           { return params.getOrDefault("cooldown_ticks", 0.0).longValue(); }
    @Override public long getRemainingCooldownTicks()  { return 0; } // géré par RPGPlayer.cooldowns
    @Override public double getValue()                 { return baseValue; }
    @Override public double getValuePerLevel()         { return 0; }
    @Override public double getBonusAtLevel(int level) { return baseValue; }
    @Override public List<Status> getGrantedStatuses() { return Collections.emptyList(); }
    @Override public List<String> getUnlockedAtLevels(){ return Collections.emptyList(); }
    @Override public boolean isPassive()               { return type == SkillType.PASSIVE; }
    @Override public boolean hasLevels()               { return false; }
    @Override public boolean isToggleable()            { return type == SkillType.TOGGLE; }
    @Override public boolean consumesItem()            { return false; }
    @Override public Material getConsumableItem()      { return null; }

    @Override
    public ItemStack getIcon() {
        ItemStack item = new ItemStack(iconMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(color + "" + org.bukkit.ChatColor.BOLD + displayName);
        meta.setLore(List.of(ChatColor.GRAY + description));
        item.setItemMeta(meta);
        return item;
    }
}
```

---

### Modifier : RPGPlayer.java

```java
// Remplacer :

import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
// ...
private List<Tessere> discoveryTessere;

// Par :
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
// ...
private final TessereLoadout tessereLoadout = new TessereLoadout();
```

---

### Créer : TessereService.java — dans skills/logic/

```java
package fr.cozyhouse.cozyHouseRPG.skills.logic;

import fr.cozyhouse.cozyHouseRPG.core.PluginContext;
import fr.cozyhouse.cozyHouseRPG.player.RPGPlayer;
import fr.cozyhouse.cozyHouseRPG.race.RaceType;
import fr.cozyhouse.cozyHouseRPG.skills.Skill;
import fr.cozyhouse.cozyHouseRPG.skills.SkillType;
import fr.cozyhouse.cozyHouseRPG.skills.datas.Domaines;
import fr.cozyhouse.cozyHouseRPG.skills.datas.TessereSlot;
import fr.cozyhouse.cozyHouseRPG.skills.display.Tessere;
import fr.cozyhouse.cozyHouseRPG.skills.playerstate.TessereLoadout;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TessereService {

  private final PluginContext context;

  public TessereService(PluginContext context) {
    this.context = context;
  }

  // ── Équipement ─────────────────────────────────────────────────────────────

  public void equiper(RPGPlayer rpg, TessereSlot slot, TessereType type) {
    Tessere tessere = type.buildTessere();
    rpg.getTessereLoadout().equipFragment(slot, tessere);
    double eff = type.getSlotEfficiency(slot);
    rpg.getBukkitPlayer().sendMessage("§7" + type.getDisplayName()
            + " → " + slot.getDisplayName() + " " + TessereType.labelEff(eff));
  }

  public void retirer(RPGPlayer rpg, TessereSlot slot) {
    rpg.getTessereLoadout().unequipFragment(slot);
  }

  // ── Révélation ─────────────────────────────────────────────────────────────

  public void revelerCompetences(RPGPlayer rpg) {
    TessereLoadout loadout = rpg.getTessereLoadout();

    if (!loadout.isDirty() && loadout.isRevealed()) {
      rpg.getBukkitPlayer().sendMessage("§7La gravure n'a pas changé.");
      return;
    }

    retirerEffetsPassifs(rpg);

    // Récupérer les TessereType équipés (via le champ type de chaque Tessere)
    List<TessereType> equipees = loadout.getEquippedFragmentList().stream()
            .map(Tessere::getType)
            .filter(Objects::nonNull)
            .toList();

    // Calcul du score global (0.0 → 1.0)
    double efficaciteScore = calculerScore(loadout, equipees, rpg.getCurrentRace());

    // Score de résonance brut (somme des compatibilités, 0 → 75 pour 6 Tessères)
    int resonanceBrut = calculerResonanceBrut(equipees);

    // Résolution des 3 compétences
    List<Skill> competences = new ArrayList<>(resoudreCompetences(equipees));

    // Sauvegarder dans le loadout
    loadout.revealSkills(competences, resonanceBrut, efficaciteScore);

    // Message
    Player p = rpg.getBukkitPlayer();
    p.sendMessage("§5§m══════════════════════════════");
    p.sendMessage("§5§l✦ Révélation Arcanique !");
    p.sendMessage(labelScore(efficaciteScore));
    for (int i = 0; i < competences.size(); i++) {
      Skill s = competences.get(i);
      p.sendMessage("§d" + (i + 1) + ". §f" + s.getDisplayName() + " §7— " + s.getDescription());
    }
    p.sendMessage("§5§m══════════════════════════════");

    appliquerEffetsPassifs(rpg);
  }

  // ── Calcul du score ────────────────────────────────────────────────────────

  private double calculerScore(TessereLoadout loadout, List<TessereType> equipees, RaceType race) {
    // Résonance normalisée (0.0 → 1.0)
    double resonance = equipees.size() < 2 ? 0.5
            : (double) calculerResonanceBrut(equipees) / ((equipees.size() * (equipees.size() - 1) / 2) * TessereCalcul.Compatibility.ALFA.number);

    // Efficacité des emplacements (0.5 → 1.5)
    double effSlots = loadout.getEquippedFragments().entrySet().stream()
            .filter(e -> e.getValue() != null && e.getValue().getType() != null)
            .mapToDouble(e -> e.getValue().getType().getSlotEfficiency(e.getKey()))
            .average().orElse(1.0);

    // Affinité de race (0.5 → 1.5)
    double affRace = (race != null && !equipees.isEmpty())
            ? equipees.stream().mapToDouble(t -> t.getRaceAffinity(race)).average().orElse(1.0)
            : 1.0;

    double score = (resonance * 0.40) + ((effSlots / 1.5) * 0.30) + ((affRace / 1.5) * 0.30);
    return Math.min(1.0, Math.max(0.0, score));
  }

  private int calculerResonanceBrut(List<TessereType> tesseres) {
    int total = 0;
    for (int i = 0; i < tesseres.size(); i++)
      for (int j = i + 1; j < tesseres.size(); j++)
        total += TessereCalcul.calculResonance(tesseres.get(i).getCalcul(), tesseres.get(j).getCalcul()).number;
    return total;
  }

  private String labelScore(double s) {
    if (s >= 0.85) return "§6⬡ Efficacité : §6§lParfaite";
    if (s >= 0.65) return "§a⬡ Efficacité : §aÉlevée";
    if (s >= 0.45) return "§e⬡ Efficacité : §eMoyenne";
    if (s >= 0.25) return "§c⬡ Efficacité : §cFaible";
    return "§4⬡ Efficacité : §4Chaotique";
  }

  // ── Résolution des compétences ─────────────────────────────────────────────

  private List<TessereSkill> resoudreCompetences(List<TessereType> eq) {
    Map<Domaines, Long> freq = eq.stream()
            .collect(Collectors.groupingBy(TessereType::getDomaine, Collectors.counting()));

    List<TessereSkill> res = new ArrayList<>();
    Set<TessereSkill> vus = new HashSet<>();

    // Règles dans l'ordre de priorité décroissante
    check(res, vus, eq.contains(TessereType.ASCENSION), TessereSkill.EVEIL_ARCANIQUE);
    check(res, vus, eq.contains(TessereType.OCCULTATION) && eq.contains(TessereType.CINETIQUE), TessereSkill.LAME_DU_VIDE);
    check(res, vus, eq.contains(TessereType.DIMENSION) && nb(freq, Domaines.SPACE) >= 2, TessereSkill.CHOC_DIMENSIONNEL);
    check(res, vus, eq.contains(TessereType.TRANSLATION), TessereSkill.BOND_INSTANTANE);
    check(res, vus, eq.contains(TessereType.INSTABILITE), TessereSkill.CHAOS_LATENT);
    check(res, vus, eq.contains(TessereType.LUMINEUSE) && nb(freq, Domaines.ENERGY) >= 2, TessereSkill.BOUCLIER_LUMINE);
    check(res, vus, nb(freq, Domaines.ENERGY) >= 2 && eq.contains(TessereType.THERMIQUE), TessereSkill.MARQUE_ARDENTE);
    check(res, vus, nb(freq, Domaines.ENERGY) >= 2 && eq.contains(TessereType.CINETIQUE), TessereSkill.IMPACT_CINETIQUE);
    check(res, vus, nb(freq, Domaines.MATTER) >= 2, TessereSkill.PEAU_DE_PIERRE);
    check(res, vus, nb(freq, Domaines.LIFE) >= 2 && eq.contains(TessereType.REGENERATION), TessereSkill.REGENERATION_HP);
    check(res, vus, nb(freq, Domaines.LIFE) >= 2, TessereSkill.VITALITE_ELARGIE);
    check(res, vus, nb(freq, Domaines.AWARENESS) >= 2, TessereSkill.VUE_ARCANIQUE);
    check(res, vus, nb(freq, Domaines.HARMONY) >= 2, TessereSkill.HARMONIE_INT);
    check(res, vus, nb(freq, Domaines.TIME) >= 2 && eq.contains(TessereType.ACCELERATION), TessereSkill.REFLEXES_ACCRUS);
    check(res, vus, nb(freq, Domaines.TIME) >= 2 && eq.contains(TessereType.DUREE), TessereSkill.DUREE_ETENDUE);
    check(res, vus, eq.contains(TessereType.THERMIQUE), TessereSkill.MARQUE_ARDENTE);
    check(res, vus, eq.contains(TessereType.CINETIQUE), TessereSkill.IMPACT_CINETIQUE);
    check(res, vus, eq.contains(TessereType.EXTINCTION), TessereSkill.DECLIN_FORCE);
    check(res, vus, nb(freq, Domaines.MATTER) >= 1, TessereSkill.ANCRAGE);
    check(res, vus, nb(freq, Domaines.LIFE) >= 1, TessereSkill.VITALITE_ELARGIE);
    check(res, vus, nb(freq, Domaines.AWARENESS) >= 1, TessereSkill.INSTINCT_AFFINE);
    check(res, vus, eq.contains(TessereType.CROISSANCE), TessereSkill.CROISSANCE_ACCL);
    check(res, vus, nb(freq, Domaines.HARMONY) >= 1, TessereSkill.HARMONIE_INT);

    TessereSkill[] fallbacks = {TessereSkill.FLUX_I, TessereSkill.FLUX_II, TessereSkill.FLUX_III};
    for (int i = 0; res.size() < 3; i++) res.add(fallbacks[i]);
    return res;
  }

  private void check(List<TessereSkill> res, Set<TessereSkill> vus, boolean cond, TessereSkill skill) {
    if (res.size() < 3 && cond && !vus.contains(skill)) {
      res.add(skill);
      vus.add(skill);
    }
  }

  private long nb(Map<Domaines, Long> freq, Domaines d) {
    return freq.getOrDefault(d, 0L);
  }

  // ── Effets passifs ─────────────────────────────────────────────────────────

  public void appliquerEffetsPassifs(RPGPlayer rpg) {
    TessereLoadout loadout = rpg.getTessereLoadout();
    if (!loadout.isRevealed()) return;
    double score = loadout.getEfficaciteScore();

    for (Skill skill : loadout.getActiveSkills()) {
      if (!(skill instanceof TessereSkill ts)) continue;
      if (ts.getType() != SkillType.PASSIVE) continue;
      switch (ts) {
        case PEAU_DE_PIERRE -> rpg.setArmor(rpg.getArmor() + (float) ts.param("bonus_armure", score));
        case VITALITE_ELARGIE -> rpg.setMaxHealth(rpg.getMaxHealth() + ts.param("bonus_hp", score));
        case REFLEXES_ACCRUS -> rpg.setAttackSpeed(rpg.getAttackSpeed() + (float) ts.param("bonus_attaque", score));
        case ANCRAGE -> rpg.setKnockbackResistance(rpg.getKnockbackResistance() + (float) ts.param("bonus_kb", score));
        case INSTINCT_AFFINE -> rpg.setCritChance(rpg.getCritChance() + (float) ts.param("bonus_crit", score));
        case CROISSANCE_ACCL -> rpg.setExpBonus(rpg.getExpBonus() + ts.param("bonus_xp", score));
        case HARMONIE_INT -> {
          double b = ts.param("bonus_stats", score);
          rpg.setStrength(rpg.getStrength() + b);
          rpg.setDexterity(rpg.getDexterity() + b);
          rpg.setConstitution(rpg.getConstitution() + b);
        }
        case EVEIL_ARCANIQUE -> {
          double pct = ts.param("pct_boost", score);
          rpg.setStrength(rpg.getStrength() * (1 + pct));
          rpg.setDexterity(rpg.getDexterity() * (1 + pct));
          rpg.setConstitution(rpg.getConstitution() * (1 + pct));
          rpg.setIntelligence(rpg.getIntelligence() * (1 + pct));
        }
        default -> {
        } // effets de combat → TessereEffectListener
      }
    }
    context.getPlayerService().refreshAttributes(rpg);
  }

  private void retirerEffetsPassifs(RPGPlayer rpg) {
    if (!rpg.getTessereLoadout().isRevealed()) return;
    context.getPlayerService().applyRaceStats(rpg);
  }
}
```

---

### Persistance — ajout dans PlayerDataManager.java

**1. Ajouter dans le constructeur :**

```java
// 6 clés pour les slots
private final NamespacedKey KEY_TESS_HEAD      = key("tess_head");
private final NamespacedKey KEY_TESS_CHEST     = key("tess_chest");
private final NamespacedKey KEY_TESS_RIGHT_ARM = key("tess_right_arm");
private final NamespacedKey KEY_TESS_LEFT_ARM  = key("tess_left_arm");
private final NamespacedKey KEY_TESS_RIGHT_LEG = key("tess_right_leg");
private final NamespacedKey KEY_TESS_LEFT_LEG  = key("tess_left_leg");
// 3 clés pour l'état révélé
private final NamespacedKey KEY_TESS_SKILLS   = key("tess_skills");   // CSV : "MARQUE_ARDENTE,PEAU_DE_PIERRE,FLUX_I"
private final NamespacedKey KEY_TESS_SCORE    = key("tess_score");    // int = score × 1000
private final NamespacedKey KEY_TESS_REVEALED = key("tess_revealed"); // byte 1/0
```

**2. Ajouter en fin de `saveToPDC()` :**

```java
TessereLoadout loadout = rpg.getTessereLoadout();

// Map slot → clé PDC
Map<TessereSlot, NamespacedKey> cleParSlot = Map.of(
    TessereSlot.HEAD,      KEY_TESS_HEAD,
    TessereSlot.CHEST,     KEY_TESS_CHEST,
    TessereSlot.RIGHT_ARM, KEY_TESS_RIGHT_ARM,
    TessereSlot.LEFT_ARM,  KEY_TESS_LEFT_ARM,
    TessereSlot.RIGHT_LEG, KEY_TESS_RIGHT_LEG,
    TessereSlot.LEFT_LEG,  KEY_TESS_LEFT_LEG
);

cleParSlot.forEach((slot, cle) -> {
    Tessere t = loadout.getEquippedFragments().get(slot);
    if (t != null && t.getType() != null)
        pdc.set(cle, PersistentDataType.STRING, t.getType().name());
    else
        pdc.remove(cle);
});

if (loadout.isRevealed() && !loadout.getActiveSkills().isEmpty()) {
    String csv = loadout.getActiveSkills().stream()
            .filter(s -> s instanceof TessereSkill)
            .map(s -> ((TessereSkill) s).name())
            .collect(Collectors.joining(","));
    pdc.set(KEY_TESS_SKILLS,   PersistentDataType.STRING,  csv);
    pdc.set(KEY_TESS_SCORE,    PersistentDataType.INTEGER, (int)(loadout.getEfficaciteScore() * 1000));
    pdc.set(KEY_TESS_REVEALED, PersistentDataType.BYTE,    (byte) 1);
}
```

**3. Ajouter en fin de `loadFromPDC()` :**

```java
TessereLoadout loadout = rpg.getTessereLoadout();

Map<TessereSlot, NamespacedKey> cleParSlot = Map.of(
    TessereSlot.HEAD,      KEY_TESS_HEAD,
    TessereSlot.CHEST,     KEY_TESS_CHEST,
    TessereSlot.RIGHT_ARM, KEY_TESS_RIGHT_ARM,
    TessereSlot.LEFT_ARM,  KEY_TESS_LEFT_ARM,
    TessereSlot.RIGHT_LEG, KEY_TESS_RIGHT_LEG,
    TessereSlot.LEFT_LEG,  KEY_TESS_LEFT_LEG
);

cleParSlot.forEach((slot, cle) -> {
    String nom = pdc.get(cle, PersistentDataType.STRING);
    if (nom == null) return;
    try {
        TessereType type = TessereType.valueOf(nom);
        loadout.getEquippedFragments().put(slot, type.buildTessere());
    } catch (IllegalArgumentException e) {
        log.warning("[RPG] TessereType inconnue : " + nom + " — ignorée.");
    }
});

if (orDefault(pdc, KEY_TESS_REVEALED, PersistentDataType.BYTE, (byte)0) == 1) {
    String csv   = pdc.get(KEY_TESS_SKILLS, PersistentDataType.STRING);
    int scoreBrut = orDefault(pdc, KEY_TESS_SCORE, PersistentDataType.INTEGER, 500);
    if (csv != null) {
        List<Skill> skills = Arrays.stream(csv.split(","))
            .map(s -> { try { return (Skill) TessereSkill.valueOf(s); } catch (Exception e) { return null; } })
            .filter(Objects::nonNull).toList();
        if (skills.size() == 3)
            loadout.revealSkills(skills, 0, scoreBrut / 1000.0);
    }
}
```

---

### Modifier : PluginContext.java

```java
// Ajouter le champ :
private final TessereService tessereService;

// Dans le constructeur, après raceRegistry.loadAllRaces() :
this.tessereService = new TessereService(this);
```

### Modifier : PlayerService.initializePlayer()

```java
// Après context.getPlayerDataManager().load(rpgPlayer, bukkitPlayer) :
if (rpgPlayer.getTessereLoadout().isRevealed()) {
    context.getTessereService().appliquerEffetsPassifs(rpgPlayer);
}
```

---

### GUI — dans skills/gui/

#### TessereBodyGUI.java

```java
package fr.cozyhouse.cozyHouseRPG.skills.display;

public class TessereBodyGUI {

  static final int SLOT_VALIDER = 40;
  private static final String TITRE = "§5§l✦ Gravure Arcane";
  private final PluginContext context;

  public TessereBodyGUI(PluginContext context) {
    this.context = context;
  }

  public void ouvrir(RPGPlayer rpg) {
    Inventory inv = Bukkit.createInventory(null, 45, TITRE);

    // Fond violet
    ItemStack verre = makeItem(Material.PURPLE_STAINED_GLASS_PANE, " ");
    for (int i = 0; i < 45; i++) inv.setItem(i, verre);

    // 6 emplacements
    for (TessereSlot slot : TessereSlot.values())
      inv.setItem(slot.getGuiSlotIndex(), itemSlot(slot, rpg));

    // Bouton Valider
    inv.setItem(SLOT_VALIDER, itemValider(rpg));

    // 3 compétences révélées en haut (slots 2, 4, 6)
    TessereLoadout loadout = rpg.getTessereLoadout();
    if (loadout.isRevealed()) {
      int[] pos = {2, 4, 6};
      List<Skill> comp = loadout.getActiveSkills();
      for (int i = 0; i < comp.size(); i++)
        inv.setItem(pos[i], comp.get(i).getIcon());
    }

    rpg.getBukkitPlayer().openInventory(inv);
  }

  private ItemStack itemSlot(TessereSlot slot, RPGPlayer rpg) {
    Tessere equipee = rpg.getTessereLoadout().getEquippedFragments().get(slot);

    if (equipee == null)
      return makeItem(Material.PURPLE_STAINED_GLASS,
              "§7[ " + slot.getDisplayName() + " ]",
              "§8" + slot.getNodeLabel(),
              "§8Clic gauche → Choisir",
              "§8Clic droit → Retirer");

    // Tessère équipée : utilise buildItem() existant de Tessere
    ItemStack item = equipee.buildItem(true);
    // Ajouter efficacité/affinité dans le lore
    if (equipee.getType() != null) {
      ItemMeta meta = item.getItemMeta();
      List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
      lore.add("");
      lore.add("§7Efficacité slot : " + TessereType.labelEff(equipee.getType().getSlotEfficiency(slot)));
      if (rpg.hasRace())
        lore.add("§7Affinité " + rpg.getCurrentRace().getDisplayName() + " : "
                + TessereType.labelEff(equipee.getType().getRaceAffinity(rpg.getCurrentRace())));
      lore.add("§8Clic droit → Retirer");
      meta.setLore(lore);
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack itemValider(RPGPlayer rpg) {
    TessereLoadout loadout = rpg.getTessereLoadout();
    if (!loadout.isComplete())
      return makeItem(Material.RED_CONCRETE, "§c§l✗ Gravure incomplète",
              "§7" + loadout.filledSlotCount() + "/6 emplacements remplis");
    if (loadout.isDirty() || !loadout.isRevealed())
      return makeItem(Material.LIME_CONCRETE, "§a§l✦ Valider la Gravure",
              "§7Cliquer pour révéler les 3 compétences.");
    List<String> lore = new ArrayList<>();
    lore.add("§7Score : " + String.format("%.0f%%", loadout.getEfficaciteScore() * 100));
    loadout.getActiveSkills().forEach(s -> lore.add("§d▸ " + s.getDisplayName()));
    return makeItem(Material.GOLD_BLOCK, "§6§l✦ Gravure Active", lore.toArray(new String[0]));
  }

  private ItemStack makeItem(Material mat, String nom, String... loreLines) {
    ItemStack item = new ItemStack(mat);
    ItemMeta meta = item.getItemMeta();
    if (meta == null) return item;
    meta.setDisplayName(nom);
    meta.setLore(Arrays.asList(loreLines));
    item.setItemMeta(meta);
    return item;
  }
}
```

#### TessereCatalogGUI.java

```java
package fr.cozyhouse.cozyHouseRPG.skills.display;

public class TessereCatalogGUI {

  // Clé PDC stockée dans l'item pour identifier le TessereType au clic
  static final NamespacedKey CLE_TYPE = new NamespacedKey("cozyhouserpg", "tessere_type");
  private final PluginContext context;

  public TessereCatalogGUI(PluginContext context) {
    this.context = context;
  }

  public void ouvrir(RPGPlayer rpg, TessereSlot slotCible) {
    Inventory inv = Bukkit.createInventory(null, 54, "§5§l✦ Tessère — " + slotCible.getDisplayName());

    int i = 0;
    for (TessereType type : TessereType.values()) {
      // buildTessere() crée le Tessere, puis on appelle buildItem(false) + on ajoute efficacité/affinité
      Tessere tessere = type.buildTessere();
      ItemStack item = tessere.buildItem(false);

      // Ajouter efficacité slot + affinité race dans le lore
      ItemMeta meta = item.getItemMeta();
      List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
      lore.add("");
      lore.add("§7Efficacité ici : " + TessereType.labelEff(type.getSlotEfficiency(slotCible)));
      if (rpg.hasRace())
        lore.add("§7Affinité " + rpg.getCurrentRace().getDisplayName() + " : "
                + TessereType.labelEff(type.getRaceAffinity(rpg.getCurrentRace())));

      // Stocker le nom de l'enum dans le PDC de l'item
      meta.getPersistentDataContainer().set(CLE_TYPE, PersistentDataType.STRING, type.name());
      meta.setLore(lore);
      item.setItemMeta(meta);
      inv.setItem(i++, item);
    }

    rpg.getBukkitPlayer().openInventory(inv);
  }

  public static TessereType lireType(ItemStack item) {
    if (item == null || !item.hasItemMeta()) return null;
    String nom = item.getItemMeta().getPersistentDataContainer().get(CLE_TYPE, PersistentDataType.STRING);
    if (nom == null) return null;
    try {
      return TessereType.valueOf(nom);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
```

#### TessereGUIListener.java

```java
package fr.cozyhouse.cozyHouseRPG.skills.display;

@RequiredArgsConstructor
public class TessereGUIListener implements Listener {

  private final PluginContext context;

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) return;
    event.setCancelled(true);
    String titre = event.getView().getTitle();
    RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
    if (rpg == null) return;

    if (titre.equals("§5§l✦ Gravure Arcane"))
      clicCorps(event, rpg);
    else if (titre.startsWith("§5§l✦ Tessère — "))
      clicCatalogue(event, rpg, titre);
  }

  private void clicCorps(InventoryClickEvent event, RPGPlayer rpg) {
    int slot = event.getSlot();

    if (slot == TessereBodyGUI.SLOT_VALIDER) {
      if (rpg.getTessereLoadout().isComplete()) {
        context.getTessereService().revelerCompetences(rpg);
        new TessereBodyGUI(context).ouvrir(rpg);
      }
      return;
    }

    for (TessereSlot ts : TessereSlot.values()) {
      if (slot != ts.getGuiSlotIndex()) continue;
      if (event.isLeftClick())
        new TessereCatalogGUI(context).ouvrir(rpg, ts);
      else if (event.isRightClick()) {
        context.getTessereService().retirer(rpg, ts);
        new TessereBodyGUI(context).ouvrir(rpg);
      }
      return;
    }
  }

  private void clicCatalogue(InventoryClickEvent event, RPGPlayer rpg, String titre) {
    String nomSlot = titre.replace("§5§l✦ Tessère — ", "");
    TessereSlot slotCible = Arrays.stream(TessereSlot.values())
            .filter(s -> s.getDisplayName().equals(nomSlot))
            .findFirst().orElse(null);
    if (slotCible == null) return;

    TessereType type = TessereCatalogGUI.lireType(event.getCurrentItem());
    if (type == null) return;

    context.getTessereService().equiper(rpg, slotCible, type);
    new TessereBodyGUI(context).ouvrir(rpg);
  }
}
```

---

### TessereEffectListener.java — dans listeners/

```java
package fr.cozyhouse.cozyHouseRPG.listeners;

@RequiredArgsConstructor
public class TessereEffectListener implements Listener {

    private final PluginContext context;

    @EventHandler
    public void onAttaque(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null || !rpg.getTessereLoadout().isRevealed()) return;

        double score = rpg.getTessereLoadout().getEfficaciteScore();

        for (Skill skill : rpg.getTessereLoadout().getActiveSkills()) {
            if (!(skill instanceof TessereSkill ts)) continue;
            switch (ts) {
                case MARQUE_ARDENTE -> {
                    if (Math.random() < ts.param("chance", score))
                        event.getEntity().setFireTicks((int) ts.param("duree_ticks", score));
                }
                case IMPACT_CINETIQUE -> {
                    if (event.getEntity() instanceof LivingEntity cible)
                        cible.setVelocity(player.getLocation().getDirection()
                            .multiply(ts.param("puissance", score)).setY(0.2));
                }
                case LAME_DU_VIDE -> {
                    if (player.isSneaking())
                        event.setDamage(event.getDamage() * (1 + ts.param("bonus", score)));
                }
                case DECLIN_FORCE -> {
                    if (event.getEntity() instanceof LivingEntity cible)
                        cible.addPotionEffect(new PotionEffect(
                            PotionEffectType.WEAKNESS, (int) ts.param("duree_ticks", score), 0));
                }
                case CHAOS_LATENT -> {
                    if (Math.random() < ts.param("chance", score)) {
                        double mult = ts.paramBrut("mult_min")
                            + Math.random() * (ts.paramBrut("mult_max") - ts.paramBrut("mult_min"));
                        event.setDamage(event.getDamage() * mult);
                        player.sendMessage(mult > 1 ? "§4⬡ Chaos : Surge !" : "§4⬡ Chaos : Dissipation !");
                    }
                }
                default -> {}
            }
        }
    }

    @EventHandler
    public void onDegats(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        RPGPlayer rpg = context.getPlayerService().getPlayer(player).orElse(null);
        if (rpg == null || !rpg.getTessereLoadout().isRevealed()) return;

        double score = rpg.getTessereLoadout().getEfficaciteScore();
        for (Skill skill : rpg.getTessereLoadout().getActiveSkills())
            if (skill instanceof TessereSkill ts && ts == TessereSkill.BOUCLIER_LUMINE
                    && event.getCause() == EntityDamageEvent.DamageCause.MAGIC)
                event.setDamage(event.getDamage() * (1 - ts.param("reduction", score)));
    }
}
```

---

## 6. Récapitulatif des fichiers

### À créer (4 fichiers)

| Fichier | Emplacement |
|---|---|
| `TessereType.java` | `skills/tessere/` |
| `TessereSkill.java` | `skills/tessere/` |
| `TessereService.java` | `skills/logic/` |
| `TessereBodyGUI.java` | `skills/gui/` |
| `TessereCatalogGUI.java` | `skills/gui/` |
| `TessereGUIListener.java` | `skills/gui/` |
| `TessereEffectListener.java` | `listeners/` |

### À modifier (5 fichiers)

| Fichier | Modification |
|---|---|
| `skills/tessere/Tessere.java` | Ajouter champs `TessereType type` et `TessereRarity rarity` |
| `skills/tessere/TessereLoadout.java` | Ajouter `double efficaciteScore` + modifier `revealSkills()` |
| `player/RPGPlayer.java` | Remplacer `List<Tessere> discoveryTessere` par `TessereLoadout tessereLoadout` |
| `player/PlayerDataManager.java` | 9 nouvelles clés PDC + blocs save/load |
| `core/PluginContext.java` | Ajouter `TessereService tessereService` |

---

## 7. Ordre de build

```
Phase 1 — Données (pas de Bukkit, pas de logique)
  → TessereType.java
  → TessereSkill.java (vérifie que ça compile avec l'interface Skill)
  → Modifier Tessere.java (ajouter type + rarity)
  → Modifier TessereLoadout.java (ajouter efficaciteScore)
  → Modifier RPGPlayer.java

Phase 2 — Logique
  → TessereService.java
  → Modifier PluginContext.java
  → TEST : commande debug pour graver 6 Tessères manuellement, vérifier le score en console

Phase 3 — Persistance
  → Modifier PlayerDataManager.java
  → Modifier PlayerService.initializePlayer()
  → TEST : graver, déco/reco, vérifier que la gravure persiste

Phase 4 — Interface
  → TessereBodyGUI.java, TessereCatalogGUI.java, TessereGUIListener.java
  → Enregistrer TessereGUIListener dans CozyHouseRPG.onEnable()
  → Ajouter /chrpg tesseres dans RaceCommand

Phase 5 — Effets
  → TessereEffectListener.java
  → Enregistrer dans CozyHouseRPG.onEnable()
  → TEST : MARQUE_ARDENTE (feu au hit), PEAU_DE_PIERRE (armure visible)
```
