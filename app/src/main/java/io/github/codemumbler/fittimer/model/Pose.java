package io.github.codemumbler.fittimer.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Pose implements Parcelable {

    private String name;
    private long duration;

    public Pose(String name, long duration) {
        this.name = name;
        this.duration = duration;
    }

    public Pose(String name) {
        this(name, 45000);
    }

    protected Pose(Parcel in) {
        this.name = in.readString();
        this.duration = in.readLong();
    }

    public static final Creator<Pose> CREATOR = new Creator<Pose>() {
        @Override
        public Pose createFromParcel(Parcel in) {
            return new Pose(in);
        }

        @Override
        public Pose[] newArray(int size) {
            return new Pose[size];
        }
    };

    public String getName() {
        return name;
    }

    public long getDuration() { return duration; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return name + " for " + duration/1000 + " seconds";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(duration);
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("duration", duration);
        return jsonObject;
    }
}
