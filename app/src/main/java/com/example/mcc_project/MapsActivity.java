package com.example.mcc_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mcc_project.databinding.ActivityMapsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView text2;
    FirebaseDatabase db;
    DatabaseReference reference;
    List<CycleStand> cycleStandList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text2 = findViewById(R.id.idTVtextTwo);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchData();
        System.out.println("CycleStand Size :"+cycleStandList.size());
        for(CycleStand cycleStand : cycleStandList) {
            System.out.println("The Cycle Details : ");
            System.out.println(cycleStand.getName());
            System.out.println(cycleStand.getLatitude());
            System.out.println(cycleStand.getLongitude());
            System.out.println(cycleStand.getQuantity());

        }
       // createInitialMap();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Object markerData = marker.getTag();
                System.out.println("Entering markerData");
                if (markerData != null) {
                    String dataString = markerData.toString();
                    System.out.println("The Marker dataString : "+dataString);
                }
                displayBottomSheet();
                return true;
            }
        });
    }

    private void createInitialMap() {
        LatLng chapinStand = new LatLng(40.90804153702675, -73.11056348133616);
        LatLng recStand = new LatLng(40.91685359483822, -73.12389716535296);
        LatLng rothStand = new LatLng(40.91066812941763, -73.12385078024428);
        LatLng sacStand = new LatLng(40.914690268275045, -73.12418302640131);

        mMap.addMarker(new MarkerOptions().position(chapinStand).title("Chapin Cycle Stand")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chapinStand, 17));

        mMap.addMarker(new MarkerOptions().position(recStand).title("REC Cycle Stand")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(recStand, 17));

        mMap.addMarker(new MarkerOptions().position(rothStand).title("Roth Cycle Stand")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rothStand, 17));

        mMap.addMarker(new MarkerOptions().position(sacStand).title("SAC Cycle Stand")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sacStand, 17));

    }

    private void displayBottomSheet() {

        // creating a variable for our bottom sheet dialog.
        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);


        // passing a layout file for our bottom sheet dialog.
        ImageView image1= bottomSheetDialog.findViewById(R.id.idIVimage);
        TextView text1= bottomSheetDialog.findViewById(R.id.idTVtext);
        TextView text2= bottomSheetDialog.findViewById(R.id.idTVtextTwo);

        Drawable res= getResources().getDrawable(R.drawable.cycleimage);
        image1.setImageDrawable(res);



        text1.setText("Wolfie's Bike Stand");
        //text2.setText("10 Cycles Available");

        bottomSheetDialog.show();
    }

    public void fetchData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("cycles");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println("Cycle key : "+snapshot.getKey());
                    System.out.println("Cycle quantity : "+snapshot.getValue());

                    CycleStand cycleStand = new CycleStand();
                    String key = snapshot.getKey();
                    cycleStand.setName(key);
                    Object value = snapshot.getValue();

                    if (value instanceof Map) {
                        Map<String, Object> mapValue = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                            String childKey = entry.getKey();
                            Object childValue = entry.getValue();
                            if(childKey.equals("quantity")) {
                                cycleStand.setQuantity(Integer.parseInt(childValue.toString()));
                            }
                            if(childKey.equals("latitude")) {
                                cycleStand.setLatitude(childValue.toString());
                            }
                            if(childKey.equals("longitude")) {
                                cycleStand.setLongitude(childValue.toString());
                            }

                            System.out.println("Child1 key: " + childKey);
                            System.out.println("Child1 value: " + childValue);
                        }
                    }
                    cycleStandList.add(cycleStand);
                    System.out.println("CycleStand Size after adding :"+cycleStandList.size());
                    updateCycleMap(cycleStandList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCycleMap(List<CycleStand> cycleStandList) {
        for(CycleStand cycleStand : cycleStandList) {
            LatLng coordinates = new LatLng(Double.parseDouble(cycleStand.getLatitude()), Double.parseDouble(cycleStand.getLongitude()));
            String name = cycleStand.getName();
            Marker marker;
            mMap.addMarker(new MarkerOptions().position(coordinates).title(name)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17));

            //text2.setText(cycleStand.getQuantity());

        }


    }
}