package com.lalitp.sampleapp.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lalitp.sampleapp.Activity.MainActivity;
import com.lalitp.sampleapp.Database.DatabaseHelper;
import com.lalitp.sampleapp.Database.LocationDetails;
import com.lalitp.sampleapp.Utils.PermissionDialogView;
import com.lalitp.sampleapp.R;
import com.lalitp.sampleapp.SampleApp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final int AMOUNT_OF_DATA = 50;
    private static final String TAG = MapFragment.class.getSimpleName();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SongListFragment.
     */

    static Context context;
    private GoogleMap googleMap;
    private SupportMapFragment map;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private Double currentLatitude = 0.0, currentLongitude = 0.0;
    private Dao<LocationDetails, Integer> locationDao;
    public static DatabaseHelper databaseHelper = null;
    private List<LocationDetails> locationDetailsList;
    private int i = 0;
    private LatLng storeLatlng;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public boolean hasCustomToolbar() {
        return false;
    }

    @Override
    protected int getLayout() {
        return R.layout.frag_map;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        init();
    }

    /*******************************************************************************************/

    private void init() {
        locationDetailsList = new ArrayList<>();
        getPermissionNearestArea();
    }


    public void getPermissionNearestArea() {

        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, new PermissionResult() {

            @Override
            public void permissionGranted() {
                //locationChecker();
                mapClientInit();
            }

            @Override
            public void permissionDenied() {
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.LOCATION_PERMISSION);
            }

            @Override
            public void permissionForeverDienid() {
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.LOCATION_PERMISSION);
            }
        });


    }

    private void mapClientInit() {
        map.getMapAsync(this);
    }


    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().isCompassEnabled();
        googleMap.getUiSettings().isMapToolbarEnabled();

        this.googleMap.setOnMapLongClickListener(this);
        myLocation();
        getLocationList();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("store location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        storeLatlng = latLng;

        getPermissionInfo();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.markerClickListener();

        return false;
    }


    private void myLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (getLocationPermissionInfo())
                getCurrentLocation();
        } else {
            getCurrentLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getCurrentLocation() {
        if (googleMap == null)
            return;
        googleMap.clear();
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Getting currentLongitude and currentLatitude
            currentLongitude = location.getLongitude();
            currentLatitude = location.getLatitude();

            getLocationList();
        }
    }


    private void getLocationList() {

        try {
            locationDao = SampleApp.getHelper().getLocationDao();
            // build your query
            QueryBuilder<LocationDetails, Integer> queryBuilder = locationDao.queryBuilder();
            // when you are done, prepare your query and build an iterator
            List<LocationDetails> notificationResults = queryBuilder.orderBy(LocationDetails.COL_TIMESTAMP, false).query();

            if (locationDetailsList != null && !locationDetailsList.isEmpty())
                locationDetailsList.clear();


            locationDetailsList.addAll(notificationResults);

            drawSellerMarkers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void drawSellerMarkers() {
        if (googleMap == null)
            return;
        googleMap.clear();

        if (locationDetailsList != null && !locationDetailsList.isEmpty()) {
            for (int i = 0; i < locationDetailsList.size(); i++) {

                setDetails(locationDetailsList.get(i));
            }
        }

        setZoomlevel();
    }

    private void setDetails(LocationDetails locationDetails) {
        double lat = Double.parseDouble(locationDetails.getLocLat());
        double longi = Double.parseDouble(locationDetails.getLocLong());
        String locName = locationDetails.getLocName();
        String imgUrl = locationDetails.getLocPic();

        LatLng latLng = new LatLng(lat, longi);

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(locName)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    public void setZoomlevel() {
        if (googleMap == null)
            return;
        List<LatLng> listLatLng = new ArrayList<>();


        if (currentLongitude != 0 && currentLatitude != 0) {
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            listLatLng.add(latLng);
        }

        if (locationDetailsList == null | listLatLng.isEmpty())
            return;

        for (LocationDetails locationDetails : locationDetailsList) {
            Double lat = Double.valueOf(locationDetails.getLocLat());
            Double longi = Double.valueOf(locationDetails.getLocLong());


            if (lat != 0 && longi != 0) {
                LatLng latLng = new LatLng(lat, longi);
                listLatLng.add(latLng);
            }
        }


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void storeLocation(String locationPic) {
        if (storeLatlng == null)
            return;

        LocationDetails locationDetails = new LocationDetails();
        locationDetails.setLocName("Store " + i);
        locationDetails.setLocLat(String.valueOf(storeLatlng.latitude));
        locationDetails.setLocLong(String.valueOf(storeLatlng.longitude));
        locationDetails.setLocPic(locationPic);


        try {
            Dao<LocationDetails, Integer> notificationDetailses = SampleApp.getHelper().getLocationDao();
            notificationDetailses.create(locationDetails);
            i++;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshList();

        Toast.makeText(getActivity(), "Store Location!!!", Toast.LENGTH_LONG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imgUrl;
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                try {
                    imgUrl = createImageFile().getAbsolutePath();
                    //setImage(imgUrl);
                    storeLocation(imgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == SELECT_FILE) {

                try {
                    Uri selectedImageUri = data == null ? null : data.getData();

                    copyStream(selectedImageUri);
                    imgUrl = createImageFile().getAbsolutePath();
                    System.out.println("gallary:" + imgUrl);
                    if (imgUrl.startsWith("/storage")) {
                        storeLocation(imgUrl);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            getLocationList();
        }
    }

    public void copyStream(Uri uri)
            throws IOException {

        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        FileOutputStream fileOutputStream = new FileOutputStream(createImageFile().getAbsolutePath());

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        inputStream.close();

    }


}
