package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;


public class MainActivity extends AppCompatActivity {

    Button btn_busTracking;
    ProgressDialog progressDialog;
    SplitInstallManager splitInstallManager;
    private int mySessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        splitInstallManager = SplitInstallManagerFactory.create(this);
        progressDialog = new ProgressDialog(this);

        btn_busTracking = findViewById(R.id.btn_bustrackingmodule);

        btn_busTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloading();
            }
        });

    }

    private void startDownloading() {
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule("busTrackingModule").build();
        SplitInstallStateUpdatedListener listener = splitInstallState -> {
            if (splitInstallState.sessionId() == mySessionId) {
                switch (splitInstallState.status()) {
                    case SplitInstallSessionStatus.DOWNLOADING:
                        progressDialog.setMessage("Downloading");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.INSTALLED:
                        Intent intent = new Intent();
                        intent.setClassName(BuildConfig.APPLICATION_ID, "com.example.bustrackingmodule.BusTrackingMainActivity");
                        startActivity(intent);
                    case SplitInstallSessionStatus.CANCELED:
                        Toast.makeText(this,"Canceled",Toast.LENGTH_SHORT).show();
                        break;
                    case SplitInstallSessionStatus.CANCELING:
                        progressDialog.setMessage("Cancelling");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.DOWNLOADED:
                        break;
                    case SplitInstallSessionStatus.FAILED:
                        break;
                    case SplitInstallSessionStatus.INSTALLING:
                        progressDialog.setMessage("Installing");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.PENDING:
                        progressDialog.setMessage("Pending");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                        break;
                    case SplitInstallSessionStatus.UNKNOWN:
                        break;
                }

            }
        };

        splitInstallManager.registerListener(listener);

        splitInstallManager.startInstall(request)
                .addOnFailureListener(e ->
                        Log.e("MainActivity", "Error installing module", e)
                )
                .addOnSuccessListener(sessionId -> mySessionId = sessionId);

    }

}