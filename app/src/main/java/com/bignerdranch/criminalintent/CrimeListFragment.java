package com.bignerdranch.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    public static final String LIST_POSITION = "LIST_POSITION";
    private RecyclerView mRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private static final int CRIME_RESULT = 1;
    private int positionOfItemToUpdate = 0;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_crime);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setHasOptionsMenu(true);
        updateUI();
        return view;
    }

    private void updateUI() {
        List<Crime> crimeList;
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        crimeList = crimeLab.getCrimeList();
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimeList);
            mRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private class CrimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mCrimeSolvedCheckBox;

        public TextView getTitleTextView() {
            return mTitleTextView;
        }

        public TextView getDateTextView() {
            return mDateTextView;
        }

        public CheckBox getCrimeSolvedCheckBox() {
            return mCrimeSolvedCheckBox;
        }

        public CrimeViewHolder(View itemView) {
            super(itemView);
            this.mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_text_view_crime_title);
            this.mDateTextView = (TextView) itemView.findViewById(R.id.list_item_text_view_crime_date);
            this.mCrimeSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_check_box_crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            getTitleTextView().setText(mCrime.getTitle());
            getCrimeSolvedCheckBox().setChecked(mCrime.isSolved());
            getDateTextView().setText(mCrime.getDate().toString());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, CRIME_RESULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CRIME_RESULT) {
            if (data != null) {
                positionOfItemToUpdate = data.getIntExtra(LIST_POSITION, 0);
            } else {
                Log.d("CrimeListFragment", "data is null", new Exception());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeViewHolder> {
        List<Crime> mCrimeList;
        public CrimeAdapter(List<Crime> crimeList) {
            this.mCrimeList = crimeList;
        }

        @Override
        public CrimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeViewHolder holder, int position) {
            Crime crime = mCrimeList.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime(UUID.randomUUID());
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimeList().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
