package by.black_pearl.githubuserclient.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.RetrofitManager;
import by.black_pearl.githubuserclient.adapters.ReposRecyclerViewAdapter;
import by.black_pearl.githubuserclient.gsons.Repos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReposFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReposFragment extends Fragment {

    private static final String LOGIN = "login";
    private static final String LINK_HEADER = "Link";
    private static final String PAGE_PARAMETER = "page";
    private static final int START_PAGE = 1;
    private String mFirstLink = "";
    private String mPrevLink = "";
    private String mNextLink = "";
    private String mLastLink = "";
    private Button mFirstBtn;
    private Button mPrevBtn;
    private Button mNextBtn;
    private Button mLastBtn;
    private View mProgressView;
    private String mLogin;
    private ReposRecyclerViewAdapter mReposAdapter;
    private RetrofitManager.GitHubApiInterface mGitHubApi;
    private TextView mPageTv;

    public ReposFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * login login of user to get repos.
     *
     * @return A new instance of fragment ReposFragment.
     */
    public static ReposFragment newInstance(String login) {
        ReposFragment reposFragment = new ReposFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LOGIN, login);
        reposFragment.setArguments(bundle);
        return reposFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReposAdapter = new ReposRecyclerViewAdapter(getContext(), getCallback());
        mGitHubApi = RetrofitManager.getGsonRetrofit(RetrofitManager.GITHUB_API_ADDRESS)
                .create(RetrofitManager.GitHubApiInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() == null) {
            getActivity().onBackPressed();
            return null;
        }
        final View view = inflater.inflate(R.layout.fragment_repos, container, false);
        if(getArguments().getString(LOGIN) != null) {
            mLogin = getArguments().getString(LOGIN);
        }
        mFirstBtn = (Button) view.findViewById(R.id.btn_first);
        mPrevBtn = (Button) view.findViewById(R.id.btn_prev);
        mNextBtn = (Button) view.findViewById(R.id.btn_next);
        mLastBtn = (Button) view.findViewById(R.id.btn_last);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_repos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mReposAdapter);
        mProgressView = view.findViewById(R.id.fl_progress);
        mPageTv = ((TextView) view.findViewById(R.id.tv_page));
        mFirstBtn.setOnClickListener(getFirstBtnOnClick());
        mPrevBtn.setOnClickListener(getPrevBtnOnClick());
        mNextBtn.setOnClickListener(getNextBtnOnClick());
        mLastBtn.setOnClickListener(getLastBtnOnClick());
        getRepos();
        return view;
    }

    private void getRepos() {
        setBtnDisabled();
        Call<List<Repos>> call = mGitHubApi.getGitHubUserRepos(mLogin, START_PAGE, RetrofitManager.PER_PAGE_ITEMS);
        enqueue(call);
    }

    private void getRepos(String url) {
        setBtnDisabled();
        Call<List<Repos>> call = mGitHubApi.getGitHubUserRepos(url);
        enqueue(call);
    }
    
    private View.OnClickListener getFirstBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepos(mFirstLink);
            }
        };
    }
    
    private View.OnClickListener getPrevBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepos(mPrevLink);
            }
        };
    }
    
    private View.OnClickListener getNextBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepos(mNextLink);
            }
        };
    }
    
    private View.OnClickListener getLastBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepos(mLastLink);
            }
        };
    }

    private void enqueue(Call<List<Repos>> call) {
        call.enqueue(new Callback<List<Repos>>() {
            @Override
            public void onResponse(Call<List<Repos>> call, Response<List<Repos>> response) {
                String linksHeader = response.headers().get(LINK_HEADER);
                if(linksHeader != null) {
                    String[] headers = linksHeader.split(", ");
                    headerHandler(headers);
                }
                mPageTv.setText(response.raw().request().url().queryParameter(PAGE_PARAMETER));
                mReposAdapter.changeReposData(response.body());
                mProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Repos>> call, Throwable t) {
                Toast.makeText(getContext(), "User error! Try again!", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }

    private void setBtnDisabled() {
        mFirstBtn.setEnabled(false);
        mPrevBtn.setEnabled(false);
        mNextBtn.setEnabled(false);
        mLastBtn.setEnabled(false);
    }

    private void headerHandler(String[] headers) {
        for (String header : headers) {

            if (header.contains("first")) {
                mFirstLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mFirstBtn.setEnabled(true);
            }
            else if (header.contains("prev")) {
                mPrevLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mPrevBtn.setEnabled(true);
            }
            else if (header.contains("next")) {
                mNextLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mNextBtn.setEnabled(true);
            }
            else if (header.contains("last")){
                mLastLink = header.substring(header.indexOf('<') + 1, header.indexOf('>'));
                mLastBtn.setEnabled(true);
            }
        }
    }

    private ReposRecyclerViewAdapter.OnViewButtonClickListener getCallback() {
        return new ReposRecyclerViewAdapter.OnViewButtonClickListener() {
            @Override
            public void onButtonClick(String url) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        };
    }

}
