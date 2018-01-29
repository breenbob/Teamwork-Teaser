package conorbreen.com.teamworkteaser.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Conor Breen on 26/01/2018.
 */

public enum ProjectStatus {
    @SerializedName("ALL")
    All,
    @SerializedName("ACTIVE")
    Active,
    @SerializedName("ARCHIVED")
    Archived,
    @SerializedName("CURRENT")
    Current,
    @SerializedName("LATE")
    Late,
    @SerializedName("COMPLETED")
    Completed
}
