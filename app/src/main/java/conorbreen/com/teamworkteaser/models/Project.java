package conorbreen.com.teamworkteaser.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Project details realm object, implements parcelable - just in case!
 */
public class Project extends RealmObject implements Parcelable {
    @SerializedName("id")
    @PrimaryKey
    private int id;

    @SerializedName("company")
    private Company company;

    @SerializedName("starred")
    private boolean starred;

    @SerializedName("name")
    private String name;

    @SerializedName("show-announcement")
    private boolean showAnnouncement;

    @SerializedName("announcement")
    private String announcement;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private String status;

    @SerializedName("subStatus")
    // not documented, but used to indicate if a project is completed, i.e. "subStatus":"completed"
    private String subStatus;

    @SerializedName("isProjectAdmin")
    private boolean isProjectAdmin;

    @SerializedName("created-on")
    private Date createdOn;

    @SerializedName("start-page")
    private String startPage;

    @SerializedName("startDate")
    private Date startDate;

    @SerializedName("logo")
    private String logo;

    @SerializedName("notifyeveryone")
    private boolean notifyEveryone;

    @SerializedName("last-changed-on")
    private Date lastChangedOn;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("harvest-timers-enabled")
    private boolean harvestTimersEnabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowAnnouncement() {
        return showAnnouncement;
    }

    public void setShowAnnouncement(boolean showAnnouncement) {
        this.showAnnouncement = showAnnouncement;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public boolean isProjectAdmin() {
        return isProjectAdmin;
    }

    public void setProjectAdmin(boolean projectAdmin) {
        isProjectAdmin = projectAdmin;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isNotifyEveryone() {
        return notifyEveryone;
    }

    public void setNotifyEveryone(boolean notifyEveryone) {
        this.notifyEveryone = notifyEveryone;
    }

    public Date getLastChangedOn() {
        return lastChangedOn;
    }

    public void setLastChangedOn(Date lastChangedOn) {
        this.lastChangedOn = lastChangedOn;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isHarvestTimersEnabled() {
        return harvestTimersEnabled;
    }

    public void setHarvestTimersEnabled(boolean harvestTimersEnabled) {
        this.harvestTimersEnabled = harvestTimersEnabled;
    }

    public Project() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.company, flags);
        dest.writeByte(this.starred ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeByte(this.showAnnouncement ? (byte) 1 : (byte) 0);
        dest.writeString(this.announcement);
        dest.writeString(this.description);
        dest.writeString(this.status);
        dest.writeString(this.subStatus);
        dest.writeByte(this.isProjectAdmin ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdOn != null ? this.createdOn.getTime() : -1);
        dest.writeString(this.startPage);
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeString(this.logo);
        dest.writeByte(this.notifyEveryone ? (byte) 1 : (byte) 0);
        dest.writeLong(this.lastChangedOn != null ? this.lastChangedOn.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeByte(this.harvestTimersEnabled ? (byte) 1 : (byte) 0);
    }

    protected Project(Parcel in) {
        this.id = in.readInt();
        this.company = in.readParcelable(Company.class.getClassLoader());
        this.starred = in.readByte() != 0;
        this.name = in.readString();
        this.showAnnouncement = in.readByte() != 0;
        this.announcement = in.readString();
        this.description = in.readString();
        this.status = in.readString();
        this.subStatus = in.readString();
        this.isProjectAdmin = in.readByte() != 0;
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        this.startPage = in.readString();
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        this.logo = in.readString();
        this.notifyEveryone = in.readByte() != 0;
        long tmpLastChangedOn = in.readLong();
        this.lastChangedOn = tmpLastChangedOn == -1 ? null : new Date(tmpLastChangedOn);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.harvestTimersEnabled = in.readByte() != 0;
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
