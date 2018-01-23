package conorbreen.com.teamworkteaser.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Company details realm object, implements parcelable - just in case!
 */
public class Company extends RealmObject implements Parcelable {
    @SerializedName("id")
    @PrimaryKey
    private int id;

    @SerializedName("is-owner")
    private int isOwner;

    @SerializedName("name")
    private String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.isOwner);
        dest.writeString(this.name);
    }

    public Company() {
    }

    protected Company(Parcel in) {
        this.id = in.readInt();
        this.isOwner = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel source) {
            return new Company(source);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
