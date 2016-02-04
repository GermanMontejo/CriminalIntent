package com.bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager mCrimeViewPager;
    private List<Crime> mCrimeList;
    private Crime mCrime;
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.criminalintent.crime_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mCrimeViewPager = (ViewPager) findViewById(R.id.activity_crime_view_pager);
        mCrimeList = CrimeLab.getCrimeLab(this).getCrimeList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Toast.makeText(getApplicationContext(), "onCreate - crime pager", Toast.LENGTH_LONG).show();
        mCrimeViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                mCrime = mCrimeList.get(position);
                return CrimeFragment.newInstance(mCrime.getId());
            }

            @Override
            public int getCount() {
                return mCrimeList.size();
            }
        });
        getParentActivityIntent();
        for (int i = 0; i < mCrimeList.size(); i++) {
            if (mCrimeList.get(i).getId().equals(crimeId)) {
                mCrimeViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext,UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_single_crime, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_delete:
                CrimeLab.getCrimeLab(getApplicationContext()).getCrimeList().remove(mCrime);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
