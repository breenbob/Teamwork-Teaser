package conorbreen.com.teamworkteaser.retrofit;

import conorbreen.com.teamworkteaser.enums.OrderBy;
import conorbreen.com.teamworkteaser.models.ProjectList;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Main Teamwork API Retrofit interface definition. I love how Retrofit does this with annotations!
 */
public interface ITeamworkApi {
    @GET("/projects.json")
    Observable<ProjectList> getAllProjects();

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
}
