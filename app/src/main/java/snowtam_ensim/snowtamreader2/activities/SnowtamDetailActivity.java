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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
        if (button.getText().toString().equals(getString(R.string.parsed_snowtam))) {
            button.setText(getString(R.string.raw_snowtam));
            PlaceholderFragment.changeDisplayMode(PlaceholderFragment.DISPLAY_PARSED_SNOWTAM);
        }
        else {
            button.setText(getString(R.string.parsed_snowtam));
            PlaceholderFragment.changeDisplayMode(PlaceholderFragment.DISPLAY_RAW_SNOWTAM);
        }

        // Update the fragments'view
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public void showMap(View view) {
        if (PlaceholderFragment.getDisplayMode() != PlaceholderFragment.DISPLAY_MAP) {
            PlaceholderFragment.changeDisplayMode(PlaceholderFragment.DISPLAY_MAP);
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static int DISPLAY_RAW_SNOWTAM = 0;
        public static int DISPLAY_PARSED_SNOWTAM = 1;
        public static int DISPLAY_MAP = 2;

        private static final String ARG_SNOWTAM = "snowtam";
        private static int displayMode = DISPLAY_PARSED_SNOWTAM;
        private GoogleMap googleMap;
        private MapView mapView;

        public PlaceholderFragment() {}

        public static int getDisplayMode() {
            return displayMode;
        }

        public static void changeDisplayMode(int displayMode) {
            PlaceholderFragment.displayMode = displayMode;
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

            final Snowtam snowtam = getArguments().getParcelable(ARG_SNOWTAM);
            ((TextView) rootView.findViewById(R.id.snowtam_title_textView)).setText(snowtam.getOaci());

            TextView snowtamTextView = (TextView) rootView.findViewById(R.id.snowtam_textView);
            mapView = (MapView) rootView.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    //googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng airfield = new LatLng(snowtam.getLatitude(), snowtam.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(airfield));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(airfield).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

            if (displayMode == DISPLAY_RAW_SNOWTAM) {
                mapView.setVisibility(View.GONE);
                snowtamTextView.setText(snowtam.getRawContent());
            }
            else if (displayMode == DISPLAY_PARSED_SNOWTAM){
                mapView.setVisibility(View.GONE);
                snowtamTextView.setText(snowtam.getParsedContent());
            }
            else {
                snowtamTextView.setText("");
                mapView.setVisibility(View.VISIBLE);
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            mapView.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
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