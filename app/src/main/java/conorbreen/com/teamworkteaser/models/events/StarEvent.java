package conorbreen.com.teamworkteaser.models.events;

/**
 * Created by Conor Breen on 24/01/2018.
 */

public class StarEvent extends EventBase {
    private boolean isStarred;
    public StarEvent(boolean successful, boolean isStarred)
    {
        this.successful = successful;
        this.isStarred = isStarred;
    }

    public boolean isStarred() {
        return isStarred;
    }
}
