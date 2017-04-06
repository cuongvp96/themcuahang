package c.top3haui.themcuahang;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    EditText edTen, edMotagia, edLoaiGas, edSdt, edChuch;
    Button btn_next;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int check = 0;
    LocationManager locationManager;
    String vitri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edTen = (EditText) findViewById(R.id.edTen);
        edMotagia = (EditText) findViewById(R.id.edMota);
        btn_next = (Button) findViewById(R.id.btn_next);

        edLoaiGas = (EditText) findViewById(R.id.edLoaiGas);
        edSdt = (EditText) findViewById(R.id.edSdt);
        edChuch = (EditText) findViewById(R.id.edChucuahang);
        buildGoogleApiClient();
        if (Build.VERSION.SDK_INT >= 23) {
            askpmis();
        }
getLocation();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRun();
            }
        });


        edTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (check==0)
                    getLocation();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void mainRun() {
        if (check==0)
            getLocation();
        if (edTen.getText().toString().isEmpty())
            edTen.setError("Not null");
        else if (edSdt.getText().toString().isEmpty()) {
            edSdt.setError("Not null");
            getLocation();
        } else if (edSdt.getText().toString().trim().length() < 10 || edSdt.getText().toString().trim().length() > 11)
            edSdt.setError("Sdt từ 10-11 số");
        else if (edMotagia.getText().toString().isEmpty())
            edMotagia.setError("Not null");
        else if (edLoaiGas.getText().toString().isEmpty())
            edLoaiGas.setError("Not null");
        else if (edChuch.getText().toString().isEmpty())
            edChuch.setError("Not null");
        else {
            String user_id = "0982429650";
            Items items = new Items(edTen.getText().toString().trim(), edLoaiGas.getText().toString().trim(),
                    edMotagia.getText().toString().trim(),
                    edSdt.getText().toString().trim(), edChuch.getText().toString().trim(), vitri, user_id);
            Intent i = new Intent(this, ActivityNext.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", items);
            i.putExtra("data", bundle);
            startActivity(i);
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askpmis() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("You need to allow access to GPS",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        int hasWriteContactsPermission1 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission1 != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showMessageOKCancel("You need to allow access to GPS",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Get location Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Location location = mLastLocation;

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String lat_lon = latitude + "," + longitude;
            vitri = lat_lon;
            check=1;
        } else {
            check=0;
            vitri = "Erro gps";

           // Toast.makeText(getApplicationContext(), "Lỗi GPS - Bật tắt lại gps rồi chờ 1-2 phút.Rồi chạy lại app", Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    public void onLocationChanged(Location location) {

    }


}
