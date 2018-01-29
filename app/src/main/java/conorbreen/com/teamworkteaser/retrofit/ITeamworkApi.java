package conorbreen.com.teamworkteaser.retrofit;

import conorbreen.com.teamworkteaser.models.enums.OrderBy;
import conorbreen.com.teamworkteaser.models.ProjectList;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Main Teamwork API Retrofit interface definition. I love how Retrofit does this with annotations!
 */
public interface ITeamworkApi {

    //region Get Projects methods

    @GET("/projects.json")
    Observable<ProjectList> getAllProjects(@Query("status") ProjectStatus projectStatus);

    @GET("/projects.json")
    // Enum parameter, to differentiate from overload with String parameter for updatedAfterDate below
    Observable<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy);

    @GET("/projects.json")
    Observable<ProjectList> getAllProjects(@Query("updatedAfterDate") String updatedAfterDate);

    @GET("/projects.json")
    Observable<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy, @Query("updatedAfterDate") String updatedAfterDate);

    @GET("/projects.json")
    Observable<ProjectList> getAllProjects(@Query("updatedAfterDate") String updatedAfterDate, @Query("updatedAfterTime") String updatedAfterTime);

    @GET("/projects.json")
    Observable<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy, @Query("updatedAfterDate") String updatedAfterDate, @Query("updatedAfterTime") String updatedAfterTime);

    //endregion

    //region Edit individual Project methods

    @PUT("/projects/{project_id}/star.json")
    Observable<Response<ResponseBody>> starProject(@Path("project_id") int projectId);

    @PUT("/projects/{project_id}/unstar.json")
    Observable<Response<ResponseBody>> unstarProject(@Path("project_id") int projectId);

    //endregion
}
