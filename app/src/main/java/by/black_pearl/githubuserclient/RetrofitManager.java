package by.black_pearl.githubuserclient;

import java.util.List;

import by.black_pearl.githubuserclient.gsons.Repos;
import by.black_pearl.githubuserclient.gsons.Search;
import by.black_pearl.githubuserclient.gsons.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by BLACK_Pearl.
 */

public class RetrofitManager {
    public static final String GITHUB_API_ADDRESS = "https://api.github.com/";
    public static final String GITHUB_GET_USERS_ADDRESS = "users";
    public static final String GITHUB_GET_SEARCH_USERS_ADDRESS = "search/users";
    public static final int PER_PAGE_ITEMS = 17;
    public static final String SEARCH_IN_LOGIN = "in:login";

    public static Retrofit getGsonRetrofit(String address) {
        return new Retrofit.Builder().baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static String getSearchByLoginQuery(String login) {
        return login + SEARCH_IN_LOGIN;
    }

    public interface GitHubApiInterface {

        /**
         * Call to get users by URL
         * @param sinceUser from which user is counting per page
         * @param perPage items per page
         * @return get list of users
         */
        @GET(GITHUB_GET_USERS_ADDRESS)
        Call<List<User>> getGitHubUsers(@Query("since") int sinceUser, @Query("per_page") int perPage);

        /**
         * Call to get users by URL
         * @param url link to response
         * @return get list of users
         */
        @GET
        Call<List<User>> getGitHubUsers(@Url String url);

        /**
         * Call to get user info
         * @param login user login
         * @return User class that contains user info
         */
        @GET(GITHUB_GET_USERS_ADDRESS + "/{login}")
        Call<User> getGitHubUser(@Path("login") String login);

        /**
         * Call to get user repositories by login
         * @param login user login
         * @param page current page
         * @param perPage items per page
         * @return list of repositories
         */
        @GET(GITHUB_GET_USERS_ADDRESS + "/{login}/repos")
        Call<List<Repos>> getGitHubUserRepos(@Path("login") String login,
                                             @Query("page") int page,
                                             @Query("per_page") int perPage);

        /**
         * Call to get user's repositories info by link.
         * @param url link to repositories
         * @return list of repositories
         */
        @GET
        Call<List<Repos>> getGitHubUserRepos(@Url String url);

        /**
         * Call for search something from github api.
         * @param search parameter for search
         * @return Search class
         */
        @GET(GITHUB_GET_SEARCH_USERS_ADDRESS)
        Call<Search> getSearchResponse(@Query("q") String search);
    }
}
