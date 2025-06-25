package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;
import pt.ul.fc.css.soccernow.enums.CardType;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cardId;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;
    
    @ManyToOne
    @JoinColumn(name = "match_statistics_id")
    private MatchStatistics matchStatistics;
    
    @Column(name = "team")
    private int team;

    /**
     * Default constructor for Card.
     */
    public Card() {
    }

    /**
     * Retrieves the unique identifier of this card.
     * @return the card ID, or null if not persisted
     */
    public Long getCardId() {
        return cardId;
    }

    /**
     * Sets the unique identifier for this card.
     * @param cardId the ID to set for this card
     */
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    /**
     * Retrieves the player who received this card.
     * @return the Player entity who received the card
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player who received this card.
     * @param player the Player entity who received the card
     * @throws IllegalArgumentException if player is null
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Retrieves the match statistics where this card was recorded.
     * @return the MatchStatistics entity associated with this card
     */
    public MatchStatistics getMatchStatistics() {
        return matchStatistics;
    }


    /**
     * Sets the match statistics where this card was recorded.
     * @param matchStatistics the MatchStatistics entity to associate with this card
     */
    public void setMatchStatistics(MatchStatistics matchStatistics) {
        this.matchStatistics = matchStatistics;
    }

    /**
     * Retrieves the type of this card (YELLOW_CARD or RED_CARD).
     * @return the CardType enum value
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Sets the type of this card (YELLOW_CARD or RED_CARD).
     * @param cardType the CardType enum value to set
     * @throws IllegalArgumentException if cardType is null
     */
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
    
    
}
