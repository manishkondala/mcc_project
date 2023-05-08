package com.example.mcc_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mcc_project.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView text2,emergencyText;
    FirebaseDatabase db;
    DatabaseReference reference;
    Button reserveButton;
    List<CycleStand> cycleStandList = new ArrayList<>();
    HashMap<String, Integer> map = new HashMap<>();
    State state = new State();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text2 = findViewById(R.id.idTVtextTwo);
        emergencyText = findViewById(R.id.emergencyID);

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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Object markerData = marker.getTag();
                if (markerData != null) {
                    String cycleStandName = markerData.toString();
                    displayBottomSheet(cycleStandName);
                }
                return true;
            }
        });
    }

    private void displayBottomSheet(String cycleStandName) {

        // creating a variable for our bottom sheet dialog.
        final BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);


        // passing a layout file for our bottom sheet dialog.
        ImageView image1= bottomSheetDialog.findViewById(R.id.idIVimage);
        TextView text1= bottomSheetDialog.findViewById(R.id.idTVtext);
        TextView text2= bottomSheetDialog.findViewById(R.id.idTVtextTwo);

        Drawable res= getResources().getDrawable(R.drawable.cycleimage);
        image1.setImageDrawable(res);

        int availableQuantity = map.get(cycleStandName);

        text1.setText(cycleStandName + " Bike Stand");
        text2.setText(availableQuantity + " Cycles Available");

        reserveButton = bottomSheetDialog.findViewById(R.id.reserveBtn);
        if(!state.isCycleReserved) {
            reserveButton.setText("Reserve Cycle");
        } else {
            reserveButton.setText("Drop Off Cycle");
        }
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Step 4 : On Clicking Reserve Button :
                //i) Check count cycle count >0
                //ii) If true, then decrease count in DB by 1
                //iii) Maintain State that the cycle is reserved
                //iv) Show Timer
                //v) Change Bottom Sheet layout (Drop Cycle)
                System.out.println("Reserve Button is Clicked");
                System.out.println("Available Quantity : "+availableQuantity);
                if(!state.isCycleReserved) {
                    if (availableQuantity > 0) {
                        reserveCycle();
                        updateDb(cycleStandName, availableQuantity-1);
                        showTimer();
                        showCycleOnMap(); // get current location from user and update cycle marker on that map position
                    }
                } else {
                    dropCycle();
                    updateDb(cycleStandName, availableQuantity+1);
                }
            }
        });
        bottomSheetDialog.show();
    }

    private void dropCycle() {
        state.setCycleReserved(false);
    }

    private void showCycleOnMap() {
    }

    private void showTimer() {
    }

    private void emergencyBtn() {
        emergencyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Admins have been notified!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDb(String cycleStandName, int availableQuantity) {
        DatabaseReference cyclesRef = FirebaseDatabase.getInstance().getReference("cycles");
        Map<String, Object> updates = new HashMap<>();
        String path = cycleStandName + "/" + "quantity";
        System.out.println("The path " +path);
        updates.put(path, availableQuantity);

        cyclesRef.updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // The values were successfully updated
                        System.out.println("Value update is success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There was an error updating the values
                        System.out.println("Value update is Failed");
                    }
                });
    }

    private void reserveCycle() {
        state.setCycleReserved(true);
    }

    public void fetchData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("cycles");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

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
                        }
                    }
                    cycleStandList.add(cycleStand);
                    map.put(key, cycleStand.getQuantity());
                    createCycleMap(cycleStandList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void createCycleMap(List<CycleStand> cycleStandList) {
        for(CycleStand cycleStand : cycleStandList) {
            LatLng coordinates = new LatLng(Double.parseDouble(cycleStand.getLatitude()), Double.parseDouble(cycleStand.getLongitude()));
            String cycleStandName = cycleStand.getName();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(coordinates)
                    .title(cycleStandName);

            Marker marker = mMap.addMarker(markerOptions);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cycleimage));
            marker.setTag(cycleStandName);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17));
        }
    }
}