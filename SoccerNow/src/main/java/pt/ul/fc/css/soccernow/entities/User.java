package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    /**
     * Constructor for the User class.
     */
    public User() {}

    /**
     * Sets the user id.
     * @param userId The user id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Sets the name of the user.
     * @param name The name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the user id
     * @return The user id
     */
    public Long getUserId() {return this.userId;}

    /**
     * Get the name of the user
     * @return The name of the user
     */
    public String getName() {return this.name;}
}
