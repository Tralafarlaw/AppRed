package tralafarlaw.com.appred;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {
    DatabaseReference mRef,bRef;
    final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    final String user_name = user.substring(0,user.length()-10);
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        init();
        marcadores();
    }
    public void init (){
        mRef = FirebaseDatabase.getInstance().getReference("/red/usuarios/");
        bRef = FirebaseDatabase.getInstance().getReference("/bue/conductores/");
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        map.getController().setZoom(9.2);
    }
    public void marcadores (){
        final List<String> Nombres = new ArrayList<>();
        mRef.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data :
                        dataSnapshot.getChildren()) {
                    Nombres.add(data.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for (final String nombre :
             Nombres) {
            bRef.child(nombre).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double lat = dataSnapshot.child("Lat").getValue(Double.class);
                    double lon = dataSnapshot.child("Lon").getValue(Double.class);
                    GeoPoint loc = new GeoPoint(lat, lon);
                    boolean sw = false;
                    for (Overlay mark :
                            map.getOverlays()) {
                        Marker aux = (Marker) mark;
                        if (aux.getTitle().equals(nombre)) {
                            aux.setPosition(loc);
                            sw = true;
                        }
                        if (sw) {
                            Marker mk = new Marker(map);
                            mk.setTitle(nombre);
                            mk.setPosition(loc);
                            mk.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(mk);
                            map.invalidate();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



}
