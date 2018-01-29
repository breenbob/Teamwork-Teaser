package conorbreen.com.teamworkteaser.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class PostProject {
    @SerializedName("project")
    private Project project;

    public PostProject(Project project)
    {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
