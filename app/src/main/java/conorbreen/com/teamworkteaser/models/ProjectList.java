package conorbreen.com.teamworkteaser.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * API response wrapper class for Project Listing
 */
public class ProjectList extends BaseApiResponse {
    @SerializedName("projects")
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
