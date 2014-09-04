package co.patt0.gosfdeals;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.patt0.gosfdeals.api.dealEndpoint.model.CollectionResponseDeal;
import co.patt0.gosfdeals.api.dealEndpoint.model.Deal;


public class MyDeals extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            List<Deal> deals = new GetDealsTask().execute().get();
            List<String> dealStrings = new ArrayList<String>();
            for (Deal deal : deals) {
                dealStrings.add(deal.getTitle());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, dealStrings);
            setListAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_deals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * An 'AsyncTask' is something which runs on a separate thread and connects to the AppEngine endpoint we just created
     */
    public class GetDealsTask extends AsyncTask<Void, Void, List<Deal>> {
        /**
         * The main method that creates an API call to our endpoint
         *
         * @param params
         * @return
         */
        @Override
        protected List<Deal> doInBackground(Void... params) {
            List<Deal> deals = new ArrayList<Deal>();

            try {

                co.patt0.gosfdeals.api.dealEndpoint.DealEndpoint api = new co.patt0.gosfdeals.api.dealEndpoint.DealEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null).setRootUrl("https://iamreallykwel.appspot.com/_ah/api")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        }).build();
                CollectionResponseDeal responseDeal = api.listDeal().execute();
                deals = responseDeal.getItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return deals;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Deal> dealBean) {
            super.onPostExecute(dealBean);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
