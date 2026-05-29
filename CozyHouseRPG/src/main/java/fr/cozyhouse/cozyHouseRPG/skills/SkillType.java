package fr.cozyhouse.cozyHouseRPG.skills;

/**
 * Defines how a skill is activated and behaves
 */
public enum SkillType {
    /** Always active, provides constant benefits */
    PASSIVE,
    
    /** Manually activated by player action */
    ACTIVE,
    
    /** Can be toggled on and off */
    TOGGLE,
    
    /** Powerful ability with long cooldown */
    ULTIMATE,
    
    /** Automatically triggers under certain conditions */
    TRIGGERED
}
