package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class RefereeDto {

	private String name;
	
	private boolean hasCertificate;
	
	private List<Long> matches;

    /**
     * Constructs a new empty RefereeDto.
     */
	public RefereeDto() {}
	
    /**
     * Gets the referee's name.
     * @return the referee name, or null if not set
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the referee's name.
     * @param name the referee name to set
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Checks if the referee has a valid certificate.
     * @return true if certified, false otherwise
     */
	public boolean getHasCertificate() {
		return hasCertificate;
	}

    /**
     * Sets the referee's certification status.
     * @param hasCertificate true if certified, false otherwise
     */
	public void setHasCertificate(boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}

    /**
     * Gets the list of match identifiers the referee is assigned to.
     * @return list of match IDs, or null if not set
     */
	public List<Long> getMatches() {
		return matches;
	}

    /**
     * Sets the list of match identifiers the referee is assigned to.
     * @param matches list of match IDs to set
     */
	public void setMatches(List<Long> matches) {
		this.matches = matches;
	}	
}
