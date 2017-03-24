package by.black_pearl.githubuserclient.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.gsons.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    private static final String USER_LOGIN = "userLogin";
    private static final String USER_NAME = "userName";
    private static final String USER_AVATAR = "userAvatar";
    private static final String USER_COMPANY = "userCompany";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_REPOS = "userRepos";

    public UserFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     * @param user
     */
    public static UserFragment newInstance(User user) {
        UserFragment fragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_LOGIN, user.login);
        bundle.putString(USER_NAME, user.name);
        bundle.putString(USER_AVATAR, user.avatar_url);
        bundle.putString(USER_COMPANY, user.company);
        bundle.putString(USER_EMAIL, user.email);
        bundle.putString(USER_REPOS, user.repos_url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_scrollable, container, false);
        if(getArguments() != null) {
            ((TextView) view.findViewById(R.id.tv_login)).setText(getArguments().getString(USER_LOGIN));
            ((TextView) view.findViewById(R.id.tv_name)).setText(getArguments().getString(USER_NAME));
            ((TextView) view.findViewById(R.id.tv_email)).setText(getArguments().getString(USER_EMAIL));
            String company = getArguments().getString(USER_COMPANY);
            company = company == null || company.equals("") ? "no company." : company;
            ((TextView) view.findViewById(R.id.tv_company)).setText(company);
            ImageView imageView = (ImageView) view.findViewById(R.id.app_bar_image);
            Glide.with(getContext()).load(getArguments().getString(USER_AVATAR)).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .fitCenter().placeholder(android.R.drawable.ic_menu_camera)
                    .crossFade().into(imageView);
            Button toRepostBtn = (Button) view.findViewById(R.id.btn_repos);
            toRepostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toRepos();
                }
            });
            FloatingActionButton toReposFab = (FloatingActionButton) view.findViewById(R.id.fab_repos);
            toReposFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toRepos();
                }
            });
        }
        else {
            Toast.makeText(getContext(), "User error! Try again!", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
        return view;
    }

    private void toRepos() {
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_container, ReposFragment.newInstance(getArguments().getString(USER_LOGIN)))
                .addToBackStack(null)
                .commit();
    }
}
