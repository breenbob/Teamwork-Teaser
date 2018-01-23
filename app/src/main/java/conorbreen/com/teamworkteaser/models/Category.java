package conorbreen.com.teamworkteaser.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Category!
 */
public class Category extends RealmObject {
    @SerializedName("id")
    @PrimaryKey
    private int id;

    @SerializedName("name")
    private String name;
}
