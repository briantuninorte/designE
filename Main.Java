package com.example.gpssmstrck;


import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothSocket;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;


public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText IPdestino;
    private EditText IPdestino2;
    private EditText IPdestino3;
    private EditText IPdestino4;
    private EditText IPdestino5;
    private EditText Port1;
    private Button Inicio;
    private Button Pair;
    private String deviceAddress = "";
    private BluetoothSocket socket = null;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 1;


    Spinner Conductor;


    public void btbut(View view) {

        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                deviceAddress = (String) devices.get(position);

            }
        });

        alertDialog.setTitle("Selecciona el Dispositivo Bluetooth");
        alertDialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PackageManager.PERMISSION_GRANTED);
        IPdestino = (EditText) findViewById(R.id.IPdest);
        IPdestino2 = (EditText) findViewById(R.id.IPdest2);
        IPdestino3 = (EditText) findViewById(R.id.IPdest3);
        IPdestino4 = (EditText) findViewById(R.id.IPdest4);
        IPdestino5 = (EditText) findViewById(R.id.IPdest5);
        Port1 = (EditText) findViewById(R.id.Puerto2);
        Inicio = (Button) findViewById(R.id.InicioBt);


        Conductor = findViewById(R.id.Conductor);
        String[] Opciones = {"Conductor 1", "Conductor 2"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Opciones);
        Conductor.setAdapter(adapter1);


        Inicio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String seleccion = Conductor.getSelectedItem().toString();

                String Conduc = null;
                if (seleccion.equals("Conductor 1")) {
                    Conduc = "1";
                } else if (seleccion.equals("Conductor 2")) {
                    Conduc = "2";
                }

                Toast toast1 = Toast.makeText(getApplicationContext(), "La aplicación ha iniciado...", Toast.LENGTH_SHORT);
                toast1.show();

                String IPdest1 = null;
                String IPdest2 = null;
                String IPdest3 = null;
                String IPdest4 = null;
                String IPdest5 = null;

                IPdest1 = IPdestino.getText().toString();
                IPdest2 = IPdestino2.getText().toString();
                IPdest3 = IPdestino3.getText().toString();
                IPdest4 = IPdestino4.getText().toString();
                IPdest5 = IPdestino5.getText().toString();

                String Port2 = Port1.getText().toString();

                String finalIPdest1 = IPdest1;
                String finalIPdest2 = IPdest2;
                String finalIPdest3 = IPdest3;
                String finalIPdest4 = IPdest4;
                String finalIPdest5 = IPdest5;
                int finalPuerto2 = Integer.valueOf(Port2);

                if (!deviceAddress.equals("")){
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
                    Toast.makeText(getApplicationContext(), "OBDII emparejado", Toast.LENGTH_SHORT).show();
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                    try {
                        socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                        socket.connect();
                        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                        new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                        Toast.makeText(getApplicationContext(), "Entre siuuuu 1", Toast.LENGTH_SHORT);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "No entre 1", Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "No entre 2", Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }
                }

                RPMCommand engineRpmCommand = new RPMCommand();

                String Conducnum = Conduc;
                locationListener = new LocationListener() {

                    public void onLocationChanged(Location location) {
                        try {

                            String myLatidude = String.valueOf(location.getLatitude());
                            String myLongitude = String.valueOf(location.getLongitude());

                            String date = String.valueOf(location.getTime());
                            String rpm = "";

                            if (socket != null){
                                engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                                rpm = engineRpmCommand.getFormattedResult();
                                Toast.makeText(getApplicationContext(), "Midiendo rpm", Toast.LENGTH_SHORT).show();
                            }

                            String message = "Latitude=" + myLatidude + ";" + "Longitude=" + myLongitude + ";" + "Fecha=" + date + ";" + "Conductor=" + Conducnum + ";" + "RPM= " + rpm;

                            Message_sender2 ms2 = new Message_sender2(finalIPdest2, finalPuerto2);
                            Message_sender3 ms3 = new Message_sender3(finalIPdest1, finalPuerto2);
                            Message_sender4 ms4 = new Message_sender4(finalIPdest3, finalPuerto2);
                            Message_sender5 ms5 = new Message_sender5(finalIPdest4, finalPuerto2);
                            Message_sender6 ms6 = new Message_sender6(finalIPdest5, finalPuerto2);

                            ms2.execute(message);
                            ms3.execute(message);
                            ms4.execute(message);
                            ms5.execute(message);
                            ms6.execute(message);

                            Toast.makeText(getApplicationContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast toasteror2 = Toast.makeText(getApplicationContext(), "No entre 3", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                } catch (SecurityException e) {
                    Toast.makeText(getApplicationContext(), "No entre 4", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }


            }
        });

    }


}
