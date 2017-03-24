package by.black_pearl.githubuserclient.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.RetrofitManager;
import by.black_pearl.githubuserclient.activities.FindActivity;
import by.black_pearl.githubuserclient.adapters.UsersRecyclerViewAdapter;
import by.black_pearl.githubuserclient.gsons.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {
    private static final int START_PAGE = 1;
    private static final String SINCE_PARAMETER = "since";
    private static final String LINK_HEADER = "Link";
    private static final int FIRST_PAGE = 1;
    private static final int SINCE_USER = 0;
    private UsersRecyclerViewAdapter mUsersRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private RetrofitManager.GitHubApiInterface mGitHubApi;
    //private String mFirstLink = "";
    //private String mPrevLink = "";
    private String mNextLink = "";
    //private String mLastLink = "";
    private Button mFirstBtn;
    private Button mPrevBtn;
    private Button mNextBtn;
    //private Button mLastBtn;
    private View mProgressView;
    private TextView mPageTv;
    private int mCurrentPage = 1;

    public UsersFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersFragment.
     */
    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsersRecyclerViewAdapter = new UsersRecyclerViewAdapter(getContext(), getUsersCallBack());
        mGitHubApi = RetrofitManager.getGsonRetrofit(RetrofitManager.GITHUB_API_ADDRESS)
                .create(RetrofitManager.GitHubApiInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_scrollable, container, false);
        RecyclerView usersRecyclerView = (RecyclerView) view.findViewById(R.id.rv_users);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(mUsersRecyclerViewAdapter);
        mPageTv = (TextView) view.findViewById(R.id.tv_page);
        mFirstBtn = (Button) view.findViewById(R.id.btn_first);
        mPrevBtn = (Button) view.findViewById(R.id.btn_prev);
        mNextBtn = (Button) view.findViewById(R.id.btn_next);
        //mLastBtn = (Button) view.findViewById(R.id.btn_last);
        mFirstBtn.setOnClickListener(getFirstBtnOnClick());
        mPrevBtn.setOnClickListener(getPrevBtnOnClick());
        mNextBtn.setOnClickListener(getNextBtnOnClick());
        //mLastBtn.setOnClickListener(getLastBtnOnClick());
        mSwipeLayout = ((SwipeRefreshLayout) view.findViewById(R.id.swipe_container));
        mSwipeLayout.setOnRefreshListener(getOnRefreshListener());
        mProgressView = view.findViewById(R.id.fl_progress);
        FloatingActionButton fabFind = (FloatingActionButton) view.findViewById(R.id.fab_find);
        fabFind.setOnClickListener(getFindBtnOnClick());
        getUsersFromGitHub(mCurrentPage);
        return view;
    }

    private View.OnClickListener getFirstBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersFromGitHub(FIRST_PAGE);
                mCurrentPage = FIRST_PAGE;
            }
        };
    }

    private View.OnClickListener getPrevBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage--;
                getUsersFromGitHub(mCurrentPage);
            }
        };
    }

    private View.OnClickListener getNextBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage++;
                getUsersFromGitHub(mCurrentPage);
            }
        };
    }

    /*private View.OnClickListener getLastBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersFromGitHub(mLastLink);
            }
        };
    }*/

    private View.OnClickListener getFindBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FindActivity.class));
            }
        };
    }

    private SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = FIRST_PAGE;
                getUsersFromGitHub(FIRST_PAGE);
            }
        };
    }

    private void setBtnDisabled() {
        mFirstBtn.setEnabled(false);
        mPrevBtn.setEnabled(false);
        mNextBtn.setEnabled(false);
        //mLastBtn.setEnabled(false);
    }

    private void getUsersFromGitHub(int sincePage) {
        setBtnDisabled();
        int since_user = (sincePage - 1) * RetrofitManager.PER_PAGE_ITEMS;
        Call<List<User>> call = mGitHubApi.getGitHubUsers(since_user, RetrofitManager.PER_PAGE_ITEMS);
        enqueue(call);
    }

    private void getUsersFromGitHub(String url) {
        Call<List<User>> call = mGitHubApi.getGitHubUsers(url);
        enqueue(call);
    }

    private UsersRecyclerViewAdapter.OnItemClick getUsersCallBack() {
        return new UsersRecyclerViewAdapter.OnItemClick() {
            @Override
            public void onItemClick(String login) {
                getUserFromGitHub(login);
            }
        };
    }

    private void getUserFromGitHub(String login) {
        Call<User> call = mGitHubApi.getGitHubUser(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                getFragmentManager().beginTransaction()
                        .replace(R.id.activity_container, UserFragment.newInstance(user))
                        .addToBackStack(null).commit();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void enqueue(Call<List<User>> call) {
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    String[] headers = response.headers().get(LINK_HEADER).split(", ");
                    headerHandler(headers);
                    List<User> users = response.body();
                    mUsersRecyclerViewAdapter.changeUsersInfo(users);
                    int since = Integer.valueOf(response.raw().request().url().queryParameter(SINCE_PARAMETER));
                    int page = since / RetrofitManager.PER_PAGE_ITEMS + 1;
                    mPageTv.setText(String.valueOf(page));
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                if(mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);
                }
                else {
                    mProgressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
                User errorUser = new User();
                errorUser.login = "Error loading users!";
                List<User> errorUsers = new ArrayList<>();
                errorUsers.add(errorUser);
                mUsersRecyclerViewAdapter.changeUsersInfo(errorUsers);
                if(mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);
                }
                else {
                    mProgressView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void headerHandler(String[] headers) {
        for (String header : headers) {
            /*if (header.contains("first")) {
                mFirstLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mFirstBtn.setEnabled(true);
            }
            else if (header.contains("prev")) {
                mPrevLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mPrevBtn.setEnabled(true);
            }
            else*/ if (header.contains("next")) {
                //mNextLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mNextBtn.setEnabled(true);
                break;
            }/*
            else if (header.contains("last")){
                mLastLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mLastBtn.setEnabled(true);
            }*/
        }
        if(mCurrentPage > START_PAGE) {
            mFirstBtn.setEnabled(true);
            mPrevBtn.setEnabled(true);
        }
    }
}
