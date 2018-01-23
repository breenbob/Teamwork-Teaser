package conorbreen.com.teamworkteaser.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * An abstract base class representing an API response with HTTP status code indicator
 */
public abstract class BaseApiResponse {
    @SerializedName("STATUS")
    private String status;

    public String getStatus() {
        return status;
    }
}
