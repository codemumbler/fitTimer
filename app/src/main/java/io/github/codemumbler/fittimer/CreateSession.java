package io.github.codemumbler.fittimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.github.codemumbler.fittimer.model.Pose;
import io.github.codemumbler.fittimer.model.Session;

public class CreateSession extends AppCompatActivity {

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setAdapter(new SessionCreatorListAdapter(this, this));
        setListViewHeightBasedOnItems(getListView());

        FitTimerApplication application = (FitTimerApplication) getApplication();
        tracker = application.getDefaultTracker();
        tracker.setScreenName(CreateSession.class.getName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void addPose(View view) {

        ((SessionCreatorListAdapter) getListView().getAdapter()).addNewItem();
        setListViewHeightBasedOnItems(getListView());
    }

    public void createNewSession(View view) {
        List<Pose> queue = ((SessionCreatorListAdapter) getListView().getAdapter()).getPoseQueue();
        Session session = new Session(queue);
        EditText sessionName = (EditText) findViewById(R.id.sessionName);
        session.setName(sessionName.getText().toString());
        Intent data = new Intent();
        data.putExtra("newCustomSession", session);
        setResult(RESULT_OK, data);

        String filename = "customsession" + System.currentTimeMillis() + ".json";
        File file = new File(getFilesDir(), filename);
        String output = "{}";
        try {
            output = session.toJsonObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(output.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    public ListView getListView() {
        return (ListView) findViewById(R.id.newSessionPose);
    }

    private void setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
}
