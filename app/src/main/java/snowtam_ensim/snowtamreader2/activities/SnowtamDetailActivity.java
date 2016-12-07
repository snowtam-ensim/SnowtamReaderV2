package snowtam_ensim.snowtamreader2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import snowtam_ensim.snowtamreader2.R;
import snowtam_ensim.snowtamreader2.model.Snowtam;

public class SnowtamDetailActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ArrayList<Snowtam> snowtams;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snowtam_detail);

        // Get the Snowtams
        Intent intent = getIntent();
        snowtams = intent.getParcelableArrayListExtra("Snowtams");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.snowtam_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public void switchDisplayMode(View view) {
        // Change the label
        Button button = (Button) findViewById(R.id.left_button);
        if (button.getText().equals(getString(R.string.parsed_snowtam))) {
            button.setText(getString(R.string.raw_snowtam));
        }
        else {
            button.setText(getString(R.string.parsed_snowtam));
        }

        // Update the display mode
        PlaceholderFragment.switchDisplayMode();
        // Update the fragments'view
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SNOWTAM = "snowtam";
        private static boolean rawMode = true;

        public PlaceholderFragment() {}

        public static void switchDisplayMode() {
            PlaceholderFragment.rawMode = !PlaceholderFragment.rawMode;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Snowtam snowtam) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_SNOWTAM, snowtam);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_snowtam_detail, container, false);

            Snowtam snowtam = getArguments().getParcelable(ARG_SNOWTAM);
            ((TextView) rootView.findViewById(R.id.snowtam_title_textView)).setText(snowtam.getOaci());
            TextView snowtamTextView = (TextView) rootView.findViewById(R.id.snowtam_textView);

            if (rawMode) {
                snowtamTextView.setText(snowtam.getRawContent());
            }
            else {
                snowtamTextView.setText(snowtam.getParsedContent());
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(snowtams.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return snowtams.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return snowtams.get(position).getOaci();
        }

    }
}
