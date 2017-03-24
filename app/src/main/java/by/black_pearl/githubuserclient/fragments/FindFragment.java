package by.black_pearl.githubuserclient.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.RetrofitManager;
import by.black_pearl.githubuserclient.adapters.SearchRecyclerViewAdapter;
import by.black_pearl.githubuserclient.gsons.Search;
import by.black_pearl.githubuserclient.gsons.SearchItem;
import by.black_pearl.githubuserclient.gsons.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment {

    private SearchRecyclerViewAdapter mSearchRvAdapter;
    private RetrofitManager.GitHubApiInterface mGitHubApi;
    private EditText mEtLogin;
    private boolean mKeyboardOpened = false;

    public FindFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSearchRvAdapter = new SearchRecyclerViewAdapter(getContext(), getOnClickCallback());
        this.mGitHubApi = RetrofitManager.getGsonRetrofit(RetrofitManager.GITHUB_API_ADDRESS)
                .create(RetrofitManager.GitHubApiInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        mEtLogin = (EditText) view.findViewById(R.id.et_login);
        mEtLogin.setOnTouchListener(getOnEtTouchListener());
        view.findViewById(R.id.fab_find).setOnClickListener(getOnFindClick(mEtLogin));
        RecyclerView searchRv = (RecyclerView) view.findViewById(R.id.rv_find);
        searchRv.setOnTouchListener(getOnTouchListener());
        searchRv.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRv.setAdapter(mSearchRvAdapter);
        view.findViewById(R.id.appbar).setOnTouchListener(getOnTouchListener());
        view.findViewById(R.id.ns_find).setOnTouchListener(getOnTouchListener());
        return view;
    }

    private SearchRecyclerViewAdapter.OnItemClick getOnClickCallback() {
        return new SearchRecyclerViewAdapter.OnItemClick() {
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

    private View.OnClickListener getOnFindClick(final EditText et_login) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!correctLogin(et_login.getText())) {
                    Toast.makeText(getContext(), "Incorrect input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<Search> call = mGitHubApi.getSearchResponse(
                        RetrofitManager.getSearchByLoginQuery(et_login.getText().toString()));
                enqueue(call);
                hideKeyboard(et_login);
            }
        };
    }

    private boolean correctLogin(Editable login) {
        return !(login.toString().equals(" ") || login.toString().equals(""));
    }

    private void enqueue(Call<Search> call) {
        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                if (response.isSuccessful()) {
                    if (response.body().items.size() == 0) {
                        SearchItem noItem = new SearchItem();
                        noItem.avatar_url = "R.drawable.no_logo";
                        noItem.login = "Nothing not found.";
                        ArrayList<SearchItem> noItems = new ArrayList<SearchItem>();
                        noItems.add(noItem);
                        mSearchRvAdapter.changeData(noItems);
                    }
                    else {
                        mSearchRvAdapter.changeData(response.body().items);
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private View.OnTouchListener getOnEtTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mKeyboardOpened = true;
                return false;
            }
        };
    }

    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtLogin.clearFocus();
                hideKeyboard(v);
                return false;
            }
        };
    }

    private void hideKeyboard(View v) {
        if (mKeyboardOpened) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            mKeyboardOpened = false;
        }
    }
}
