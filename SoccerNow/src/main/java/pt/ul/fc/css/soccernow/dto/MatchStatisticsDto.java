package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class MatchStatisticsDto {

    private Long matchStatisticsId;
    private int team1_Score;
    private int team2_Score;
    private List<Long> team1_Cards;
    private List<Long> team2_Cards;
    private List<Long> goals;

    private Integer winnerTeam;

    private boolean isOver;

    /**
     * Constructs a new empty MatchStatisticsDto.
     */
    public MatchStatisticsDto() {}

    /**
     * Gets the match statistics identifier.
     * @return the statistics record ID, or null if not persisted
     */
    public Long getMatchStatisticsId() {
        return matchStatisticsId;
    }

    /**
     * Sets the match statistics identifier.
     * @param matchStatisticsId the statistics record ID to set
     */
    public void setMatchStatisticsId(Long matchStatisticsId) {
        this.matchStatisticsId = matchStatisticsId;
    }

    /**
     * Gets team 1's current score.
     * @return the score for team 1
     */
    public int getTeam1_Score() {
        return team1_Score;
    }

    /**
     * Sets team 1's current score.
     * @param team1_Score the score to set for team 1
     */
    public void setTeam1_Score(int team1_Score) {
        this.team1_Score = team1_Score;
    }

    /**
     * Gets team 2's current score.
     * @return the score for team 2
     */
    public int getTeam2_Score() {
        return team2_Score;
    }

    /**
     * Sets team 2's current score.
     * @param team2_Score the score to set for team 2
     */
    public void setTeam2_Score(int team2_Score) {
        this.team2_Score = team2_Score;
    }

    /**
     * Gets the winning team designation.
     * @return 1 for team 1, 2 for team 2, or null for draw
     */
    public Integer getWinnerTeam() {
        return winnerTeam;
    }

    /**
     * Sets the winning team designation.
     * @param winnerTeam 1 for team 1, 2 for team 2, null for draw
     */
    public void setWinnerTeam(Integer winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    /**
     * Checks if the match is concluded.
     * @return true if match is over, false if still in progress
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * Sets the match conclusion status.
     * @param isOver true to mark as concluded, false as in progress
     */
    public void setOver(boolean isOver) {
        this.isOver = isOver;
    }

    /**
     * Gets the list of card identifiers for team 1.
     * @return list of card IDs for team 1
     */
    public List<Long> getTeam1_Cards() {
        return team1_Cards;
    }

    /**
     * Sets the list of card identifiers for team 1.
     * @param team1_Cards list of card IDs to set for team 1
     */
    public void setTeam1_Cards(List<Long> team1_Cards) {
        this.team1_Cards = team1_Cards;
    }

    /**
     * Gets the list of card identifiers for team 2.
     * @return list of card IDs for team 2
     */
    public List<Long> getTeam2_Cards() {
        return team2_Cards;
    }

    /**
     * Sets the list of card identifiers for team 2.
     * @param team2_Cards list of card IDs to set for team 2
     */
    public void setTeam2_Cards(List<Long> team2_Cards) {
        this.team2_Cards = team2_Cards;
    }

    /**
     * Gets the list of goal identifiers for this match.
     * @return list of goal IDs
     */
    public List<Long> getGoals() {
        return goals;
    }

    /**
     * Sets the list of goal identifiers for this match.
     * @param goals list of goal IDs to set
     */
    public void setGoals(List<Long> goals) {
        this.goals = goals;
    }
}
