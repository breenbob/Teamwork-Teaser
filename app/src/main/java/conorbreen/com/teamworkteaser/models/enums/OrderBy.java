package conorbreen.com.teamworkteaser.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * An enum to represent the Sorting options available
 */
public enum OrderBy {
    @SerializedName("name")
    Name,
    @SerializedName("companyName")
    CompanyName,
    @SerializedName("lastActivityDate")
    LastActivityDate;
}
