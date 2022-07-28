package br.com.desing;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import br.com.pdfreport.PdfReport;
import br.com.pdfreport.entidades.ItemHeader;
import br.com.pdfreport.entidades.ItemTable;
import br.com.pdfreport.entidades.ItemTotalizer;
import br.com.pdfreport.enuns.Border;
import br.com.pdfreport.enuns.Location;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkExternalStorageManager();

    }

    public void checkExternalStorageManager() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            validaPermisoes();
            return;
        }

        if (Environment.isExternalStorageManager()) {
            validaPermisoes();
            return;
        }

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Atenção")
                .setMessage("Dar permissão de acesso aos arquivos!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                            checkExternalStorageManagerLaunch.launch(intent);
                        } catch (Exception ex) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            checkExternalStorageManagerLaunch.launch(intent);
                        }

                    }
                })
                .setIcon(R.drawable.round_folder_open_24
                )
                .show();
    }

    ActivityResultLauncher<Intent> checkExternalStorageManagerLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                validaPermisoes();
            } else {
                checkExternalStorageManager();
            }
        }

    });

    private void validaPermisoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        setListeners();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setListeners();
                } else {
                    validaPermisoes();
                }
                return;
            }
        }
    }

    private void setListeners(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute("VER");
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute("ENVIAR");
            }
        });
    }

    ProgressDialog progressDialog;

    public class Task extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Aguarde.", "Processando..!", true);
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {

                int linha = 2;
                int coluna = 3;

                ItemHeader[][] matrizHeader = new ItemHeader[linha][coluna];
                matrizHeader[0][0] = new ItemHeader("Razão social", "Siac Sistemas LTDA");
                matrizHeader[0][1] = new ItemHeader("Nome Fantasia", "Siac Sistemas");
                matrizHeader[0][2] = new ItemHeader("Simples nacional", "S");

                matrizHeader[1][0] = new ItemHeader("CNPJ", "01.000.258/0001-89");
                matrizHeader[1][1] = new ItemHeader("Insc Estadual", "00.526-89");
                matrizHeader[1][2] = new ItemHeader("NIT", "00.52-0002-8574");

                float[] columnWidths = {0.2f, 1f, 1f};
                String[] arraySubtitle = new String[]{"ID", "NOME", "SOBRENOME"};

                ItemTable[][] arrayTabela = new ItemTable[2][3];
                arrayTabela[0][0] = new ItemTable("1", Location.CENTER);
                arrayTabela[0][1] = new ItemTable("Aureo");
                arrayTabela[0][2] = new ItemTable("Jose");

                arrayTabela[1][0] = new ItemTable("2", Location.CENTER);
                arrayTabela[1][1] = new ItemTable("Davi");
                arrayTabela[1][2] = new ItemTable("Martins");

                ItemTotalizer[] arrayTotalizer = new ItemTotalizer[2];
                arrayTotalizer[0] = new ItemTotalizer("Toal de itens", "3");
                arrayTotalizer[1] = new ItemTotalizer("Toal de valor", "5000,00");

                PdfReport report = PdfReport.init(MainActivity.this, BuildConfig.APPLICATION_ID, "android", Color.BLACK)
                        .lineSeparator()
                        .imageIn("icon.jpg", Location.CENTER)
                        .lineSeparator(2)
                        .headerImage("icon.jpg", Location.LEFT, Border.YES, matrizHeader) // ONLY LEFT OR RIGTH
                        .lineSeparator(3)
                        .header(matrizHeader)
                        .table(columnWidths, arraySubtitle, arrayTabela)
                        .totalizer(arrayTotalizer)
                        .create();

                if(strings.length > 0 && strings[0].equals("VER")){
                    report.previewPdf();
                }else{
                    report.sendPdf();
                }


            } catch (Exception e) {
                e.printStackTrace();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Atenção")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.round_folder_open_24
                        )
                        .show();
            } finally {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

}