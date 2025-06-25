package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "stadium")
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stadiumId;

    @Column(name = "stadium_name", nullable = false)
    private String stadiumName;

    /**
     * Default constructor for Stadium.
     */
    public Stadium() {}

    /**
     * Retrieves the stadium's unique identifier.
     * @return the stadium ID, or null if not persisted
     */
    public Long getStadiumId() {
        return stadiumId;
    }


    /**
     * Sets the stadium's unique identifier.
     * @param stadiumId the ID to set for this stadium
     */
    public void setStadiumId(Long stadiumId) {
        this.stadiumId = stadiumId;
    }

    /**
     * Retrieves the name of the stadium.
     * @return the stadium name (never null)
     */
    public String getStadiumName() {
        return stadiumName;
    }

    /**
     * Sets the name of the stadium.
     * @param stadiumName the name to set (must not be null)
     */
    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }
}
