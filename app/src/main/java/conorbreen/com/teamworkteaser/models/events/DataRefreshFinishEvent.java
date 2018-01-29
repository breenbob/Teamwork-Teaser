package conorbreen.com.teamworkteaser.models.events;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * Arbitrary class to represent a "Projects list fetched successfully from API" event.
 */
public class DataRefreshFinishEvent extends EventBase {
    public DataRefreshFinishEvent(boolean successful)
    {
        this.successful = successful;
    }
}
