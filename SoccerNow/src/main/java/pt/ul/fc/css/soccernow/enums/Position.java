package pt.ul.fc.css.soccernow.enums;

public enum Position {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD;

    public static boolean isValidPosition(String pos) {
        try {
            Position.valueOf(pos);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
