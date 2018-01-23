package conorbreen.com.teamworkteaser.retrofit;

import conorbreen.com.teamworkteaser.enums.OrderBy;
import conorbreen.com.teamworkteaser.models.ProjectList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Main Teamwork API Retrofit interface definition. I love how this works! Big time saver, too.
 */
public interface ITeamworkApi {
    @GET("/projects.json")
    Call<ProjectList> getAllProjects();

    @GET("/projects.json")
    // Enum parameter, to differentiate from overload with String parameter for updatedAfterDate below
    Call<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy);

    @GET("/projects.json")
    Call<ProjectList> getAllProjects(@Query("updatedAfterDate") String updatedAfterDate);

    @GET("/projects.json")
    Call<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy, @Query("updatedAfterDate") String updatedAfterDate);

    @GET("/projects.json")
    Call<ProjectList> getAllProjects(@Query("updatedAfterDate") String updatedAfterDate, @Query("updatedAfterTime") String updatedAfterTime);

    @GET("/projects.json")
    Call<ProjectList> getAllProjects(@Query("orderby") OrderBy orderBy, @Query("updatedAfterDate") String updatedAfterDate, @Query("updatedAfterTime") String updatedAfterTime);
}
