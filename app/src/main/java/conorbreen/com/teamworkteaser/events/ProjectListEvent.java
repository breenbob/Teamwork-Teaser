package conorbreen.com.teamworkteaser.events;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * Arbitrary class to represent a "Projects list fetched successfully from API" event.
 */
public class ProjectListEvent {
    private boolean successful;

    public ProjectListEvent(boolean successful)
    {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
