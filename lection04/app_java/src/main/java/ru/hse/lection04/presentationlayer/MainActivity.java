package ru.hse.lection04.presentationlayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.hse.lection04.R;
import ru.hse.lection04.businesslayer.LogProvider;
import ru.hse.lection04.businesslayer.ServiceLocator;
import ru.hse.lection04.businesslayer.connectivity.AbstractConnectivityProvider;
import ru.hse.lection04.objects.LogEntry;
import ru.hse.lection04.presentationlayer.adapter.LogAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";


    protected AbstractConnectivityProvider mConnectivityProvider = ServiceLocator.getConnectivityProvider();
    protected LogProvider mLogProvider = ServiceLocator.getLogProvider();

    protected final LogListener mLogListener = new LogListener();
    protected final LogAdapter mLogAdapter = new LogAdapter();

    protected SwitchCompat mTrackConnectivity;
    protected RecyclerView mRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTrackConnectivity = findViewById(R.id.connectivity_track);
        mTrackConnectivity.setChecked(mConnectivityProvider.isTrackEnabled());
        mTrackConnectivity.setOnCheckedChangeListener(new TrackerChangeListener());

        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mLogAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mLogProvider.register(mLogListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAndUpdateState();
    }

    @Override
    protected void onStop() {
        mLogProvider.unregister(mLogListener);

        super.onStop();
    }


    protected void checkAndUpdateState() {
        if (mTrackConnectivity.isChecked() != mConnectivityProvider.isTrackEnabled()) {
            mTrackConnectivity.setChecked(mConnectivityProvider.isTrackEnabled());
        } else {
            updateServiceState(mConnectivityProvider.isTrackEnabled());
        }
    }

    protected void updateServiceState(boolean isChecked) {
        final Intent intent = ConnectivityService.newInstance(getApplicationContext());

        if (isChecked) {
            ContextCompat.startForegroundService(getApplicationContext(), intent);
        } else {
            stopService(intent);
        }
    }

    protected void updateData(List<LogEntry> entries) {
        mLogAdapter.submitList(entries);
    }


    protected class LogListener implements LogProvider.IListener {
        @Override
        public void logUpdated(LogProvider provider, List<LogEntry> entries) {
            updateData(entries);
        }
    }

    protected class TrackerChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final boolean needServiceUpdate = mConnectivityProvider.setTrackEnabled(isChecked);

            if (needServiceUpdate) {
                updateServiceState(isChecked);
            }
        }
    }
}