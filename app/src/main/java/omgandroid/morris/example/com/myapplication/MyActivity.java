package omgandroid.morris.example.com.myapplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    JSONAdapter mJSONAdapter;
    ArrayList mNameList = new ArrayList();
    ShareActionProvider mShareActionProvider;

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

    private void queryBooks(String searchString) {
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        setProgressBarIndeterminateVisibility(true);
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        mJSONAdapter.updateData(jsonObject.optJSONArray("docs"));
                        setProgressBarIndeterminateVisibility(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject errer) {
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("omg android", statusCode + " " + throwable.getMessage());
                        setProgressBarIndeterminateVisibility(false);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.activity_my);
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);
        mainEditText = (EditText) findViewById(R.id.main_edittext);
        mainListView = (ListView) findViewById(R.id.main_listview);
        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());
        mainListView.setAdapter(mJSONAdapter);
        mainListView.setOnItemClickListener(this);
        displayWelcome();


    }

    public void displayWelcome() {
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name = mSharedPreferences.getString(PREF_NAME, "");
        if (name.length() > 0) {
            Toast.makeText(this, "welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What's your name?");

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String inputName = input.getText().toString();
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alert.show();
        }
    }

    @Override
    public void onClick(View view) {
        queryBooks(mainEditText.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long l) {
        JSONObject jsonObject = (JSONObject)mJSONAdapter.getItem(i);
        String coverID = jsonObject.optString("cover_i", "");

        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("coverID", coverID);

        startActivity(detailIntent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
        }

        setShareIntent();
        return true;
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

}
