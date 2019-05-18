package com.machado.mystock.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.machado.mystock.R;
import com.machado.mystock.classes.Pessoa;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class CodeDetectorActivity extends AppCompatActivity {

    private static final String INTENT_USER = "pessoa";

    private String codeInfo;
    private Pessoa pessoa;

    private SurfaceView camView;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_detector);

        Intent intentRecive = getIntent();
        pessoa = (Pessoa) intentRecive.getSerializableExtra(INTENT_USER);

        /*Associa ao SurfaceView do activity_code_detector.xml*/
        camView = findViewById(R.id.camera_view);

        /*Inicia o barcodeDetector setando os codigos que ele conseguirá ler ## API da Google ## */
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.CODE_39 | Barcode.EAN_13 | Barcode.QR_CODE | Barcode.EAN_8)
                .build();

        /*Inicia o cameraSource que é responsavel pelo funcionamento da camera ## API da Google ##
         * .Builder(this, barcodeDetector) controi setando o detector de codigos
         * .setAutoFocusEnable(true) ativa o foco automatico da cemra
         * .setRequestedFps(15.0f) nao sei direito mas setado em (15.0f) ajuda a ler os codigos*/
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedFps(15.f)
                .build();

        /*Recebe um retorno da camera para esibir na tela*/
        camView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    cameraSource.start(camView.getHolder());        /*Inicia o funcionamento da camera*/
                } catch (IOException e){
                    Log.e("CAMERA SOURCE", e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();         /*Para a camera*/
            }
        });

        /* Seta o processador do sensor de codigos no caso como vai funcionar e o que vai fazer depois de detectar */
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                /*Detecta o codigo e inicia uma atividade passando o resultado*/
                if (barcodes.size()!=0){
                    codeInfo = barcodes.valueAt(0).displayValue;
                    Intent intent = new Intent(CodeDetectorActivity.this, CadastroProdutoActivity.class);
                    intent.putExtra("code",codeInfo);
                    intent.putExtra(INTENT_USER, pessoa);
                    startActivity(intent);
                }
            }
        });
    }
}