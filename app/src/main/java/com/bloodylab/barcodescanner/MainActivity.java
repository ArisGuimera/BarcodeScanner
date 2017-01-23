package com.bloodylab.barcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class MainActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{

    private ZBarScannerView mScannerView;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_BARCODE_RESULT = TAG + "EXTRA_BARCODE_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
        validateCameraPermissions();
    }

    @Override
    public void handleResult(Result result) {
        //Compruebo si se trata de un código QR, sino muestro un toast al usuario.
        if(result.getBarcodeFormat().equals(BarcodeFormat.QRCODE)){
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(EXTRA_BARCODE_RESULT, result.getContents());
            startActivity(intent);
        }else{
            showError("Solo se permiten códigos QR");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public void validateCameraPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        ZBAR_CAMERA_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION: {
                //Si la petición es cancelada el tamaño de la array será 0
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Ha aceptado los permisos, la app puedo correr con normalidad.
                    Log.i("ZBAR_CAMERA_PERMISSION","Permisos aceptados");
                } else {
                    //Permisos rechazados, mostraremos un mensaje avisando al usuario.
                    Log.i("ZBAR_CAMERA_PERMISSION","Permisos rechazados");
                    showError("Permisos de cámara denegados.");
                }
                return;
            }
        }
    }

    public void showError(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
