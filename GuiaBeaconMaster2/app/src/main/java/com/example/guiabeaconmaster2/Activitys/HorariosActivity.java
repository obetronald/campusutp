package com.example.guiabeaconmaster2.Activitys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.example.guiabeaconmaster2.Entitys.Horario;
import com.example.guiabeaconmaster2.R;
import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;


public class HorariosActivity extends AppCompatActivity implements View.OnClickListener, BeaconConsumer,
        RangeNotifier{

    private AsyncHttpClient horario;
    EditText txtcod,txtnombre,txtdia;
    Button btnbuscar;
    ListView lvDatos;
    SmartImageView Simageview;

    protected final String TAG = HorariosActivity.this.getClass().getSimpleName();;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final long DEFAULT_SCAN_PERIOD_MS = 3000;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    RequestQueue rq;

    RequestQueue jrq;

    // Para interactuar con los beacons desde una actividad
    private BeaconManager mBeaconManager;
    Identifier cajaUser;
    // Representa el criterio de campos con los que buscar beacons
    private Region mRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        horario = new AsyncHttpClient();

        txtcod= findViewById(R.id.edtCodbeacon);
        txtnombre= findViewById(R.id.edtnombre);
        txtdia = findViewById(R.id.edtdia);
        lvDatos= findViewById(R.id.lvDatos);
        Simageview= findViewById(R.id.image);

        getStartButton().setOnClickListener(this);
        getStopButton().setOnClickListener(this);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        // Fijar un protocolo beacon, Eddystone en este caso
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        ArrayList<Identifier> identifiers = new ArrayList<>();

        mRegion = new Region(ALL_BEACONS_REGION, identifiers);

        //Button btnBuscar = (Button) findViewById(R.id.startReadingBeaconsButton);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(findViewById(R.id.startReadingBeaconsButton))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Si los permisos de localización todavía no se han concedido, solicitarlos
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    askForLocationPermissions();
                } else { // Permisos de localización concedidos

                    prepareDetection();
                }

            } else { // Versiones de Android < 6

                prepareDetection();
            }

        } else if (view.equals(findViewById(R.id.stopReadingBeaconsButton))) {

            stopDetectingBeacons();

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Desactivar bluetooth
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }
    private void prepareDetection() {

        if (!isLocationEnabled()) {

            askToTurnOnLocation();

        } else { // Localización activada, comprobemos el bluetooth

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {

                showToastMessage(getString(R.string.not_support_bluetooth_msg));

            } else if (mBluetoothAdapter.isEnabled()) {

                startDetectingBeacons();

            } else {

                // Pedir al usuario que active el bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            // Usuario ha activado el bluetooth
            if (resultCode == RESULT_OK) {

                startDetectingBeacons();

            } else if (resultCode == RESULT_CANCELED) { // User refuses to enable bluetooth

                showToastMessage(getString(R.string.no_bluetooth_msg));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startDetectingBeacons() {

        // Fijar un periodo de escaneo
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);

        // Enlazar al servicio de beacons. Obtiene un callback cuando esté listo para ser usado
        mBeaconManager.bind(this);

        // Desactivar botón de comenzar
        getStartButton().setEnabled(false);
        getStartButton().setAlpha(.5f);

        // Activar botón de parar
        getStopButton().setEnabled(true);
        getStopButton().setAlpha(1);
    }


    @Override
    public void onBeaconServiceConnect() {

        try {
            // Empezar a buscar los beacons que encajen con el el objeto Región pasado, incluyendo
            // actualizaciones en la distancia estimada
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            showToastMessage(getString(R.string.start_looking_for_beacons));

        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al empezar a buscar beacons " + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() == 0) {
            showToastMessage(getString(R.string.no_beacons_detected));
        }
        for (Beacon beacon : beacons) {
            String guuid = String.valueOf(beacon.getId1());
            //String guuid2 = String.valueOf(beacon.getId2());
            buscarBeacon(guuid);
        }
    }

    private void buscarimagen(){

        String url="http://134.209.237.96/WebService/buscar2.php?cod_beacon="+txtcod.getText();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                if (statusCode == 200) {
                    try {
                        JSONArray obj = new JSONArray(new String(response));
                        String urlimage = obj.getJSONObject(0).getString("imagen");
                        Rect rect = new Rect(Simageview.getLeft(), Simageview.getTop(), Simageview.getRight(), Simageview.getBottom());
                        Simageview.setImageUrl(urlimage, rect);
                        txtnombre.setText(obj.getJSONObject(0).getString("nombre_beacon"));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });

    }


    private void buscarBeacon(final String guuid){
        String url="http://134.209.237.96/WebService/identificar.php?beacon="+guuid;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                Toast.makeText(getApplicationContext(), "Encontrado..", Toast.LENGTH_SHORT).show();
                txtcod.setText((guuid));
                txtdia.setText(obtenerFecha());
                //lvDatos.setAdapter(null);
                buscarimagen();
                ObtenerDatos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
            }
        });

        rq = Volley.newRequestQueue(this);
        rq.add(jsonArrayRequest);
    }
    private String obtenerFecha(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    private void ObtenerDatos(){
        String url="http://134.209.237.96/WebService/buscar.php?cod_beacon="+txtcod.getText()+"&dia="+txtdia.getText();
        horario.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode == 200){

                    listar(new String (responseBody));
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    private void listar(String respuesta){
        ArrayList<Horario> lista = new ArrayList<>();
        try{
            JSONArray jsonarreglo = new JSONArray(respuesta);
            for(int i=0;i<jsonarreglo.length();i++){
                Horario h = new Horario();
                h.setId_horario(jsonarreglo.getJSONObject(i).getInt("id_horario"));
                h.setCod_beacon(jsonarreglo.getJSONObject(i).getString("cod_beacon"));
                h.setNombre_beacon(jsonarreglo.getJSONObject(i).getString("nombre_beacon"));
                h.setHora_inicio(jsonarreglo.getJSONObject(i).getString("hora_inicio"));
                h.setHora_fin(jsonarreglo.getJSONObject(i).getString("hora_fin"));
                h.setNombre_curso(jsonarreglo.getJSONObject(i).getString("nombre_curso"));
                h.setNombre_docente(jsonarreglo.getJSONObject(i).getString("nombre_docente"));
                h.setImagen(jsonarreglo.getJSONObject(i).getString("imagen"));
                lista.add(h);
            }
            ArrayAdapter<Horario> a = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lista);
            lvDatos.setAdapter(a);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            showToastMessage(getString(R.string.stop_looking_for_beacons));
        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al parar de buscar beacons " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();

        // Desenlazar servicio de beacons
        mBeaconManager.unbind(this);

        // Activar botón de comenzar
        getStartButton().setEnabled(true);
        getStartButton().setAlpha(1);

        // Desactivar botón de parar
        getStopButton().setEnabled(false);
        getStopButton().setAlpha(.5f);
    }

    private void askForLocationPermissions() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_access_needed);
        builder.setMessage(R.string.grant_location_access);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareDetection();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_not_granted) +
                            getString(R.string.cannot_discover_beacons));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
    private boolean isLocationEnabled() {

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean networkLocationEnabled = false;

        boolean gpsLocationEnabled = false;

        try {
            networkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gpsLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
            Log.d(TAG, "Excepción al obtener información de localización");
        }

        return networkLocationEnabled || gpsLocationEnabled;
    }

    /**
     * Abrir ajustes de localización para que el usuario pueda activar los servicios de localización
     */
    private void askToTurnOnLocation() {

        // Notificar al usuario
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.location_disabled);
        dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.show();
    }

    private Button getStartButton() {
        return (Button) findViewById(R.id.startReadingBeaconsButton);
    }

    private Button getStopButton() {
        return (Button) findViewById(R.id.stopReadingBeaconsButton);
    }

    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}