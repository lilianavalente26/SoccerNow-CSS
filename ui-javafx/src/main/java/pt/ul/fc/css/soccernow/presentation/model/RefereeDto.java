package pt.ul.fc.css.soccernow.presentation.model;

import java.util.List;

public class RefereeDto {

    private String name;
    private boolean hasCertificate;
    private List<Long> matches;

    public RefereeDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHasCertificate() {
        return hasCertificate;
    }

    public void setHasCertificate(boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    public List<Long> getMatches() {
        return matches;
    }

    public void setMatches(List<Long> matches) {
        this.matches = matches;
    }
}
