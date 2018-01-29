package conorbreen.com.teamworkteaser.models;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import conorbreen.com.teamworkteaser.MainApplication;
import conorbreen.com.teamworkteaser.R;
import io.realm.RealmObject;
import timber.log.Timber;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class Tag extends RealmObject implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ColorInt
    public int getColor() {
        if (!TextUtils.isEmpty(this.color)) {
            try {
                return Color.parseColor(this.color);
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Error parsing color with value '%s'", this.color);
            }
        }

        // Return a default tag color, I'm using the accent color
        return ContextCompat.getColor(MainApplication.getContext(), R.color.colorAccent);
    }

    public void setColor(String color) {
        this.color = color;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.color);
    }

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
