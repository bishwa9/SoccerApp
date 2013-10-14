package com.example.footballapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;

// -------------------------------------------------------------------------
/**
 * This task contacts the appropriate server to fetch the stats of the player or
 * team
 *
 * NOTE: very similar to statsGetterTask
 *
 * @author Bishwamoy Sinha Roy
 * @version Oct 9, 2013
 */
public class statsGetterTask
    extends AsyncTask<String, Void, String>
{

    private MainActivity guiThread_;


    // ----------------------------------------------------------
    /**
     * Create a new statsGetterTask object.
     *
     * @param thread
     *            : class where the inputted string is stored
     */
    public statsGetterTask(MainActivity thread)
    {
        guiThread_ = thread;
    }


    // happens in a different thread
    protected String doInBackground(String... params)
    {
        String response = "";
        String url = "";
        if (params[1] == "player")
        {
            response = "s.p."; // prepend s for stats and p for player
            url = "http://www.manutd.com" + params[0];
        }
        else if (params[1] == "team")
        {
            response = "s.t."; // prepend s for stats and p for player
            url = "http://www.uefa.com" + params[0];
        }
        // make request to appropiate server
        response += makeHTTPRequest(url);
        return response;
    }


    // happend on the UI Thread
    protected void onPostExecute(String result)
    {
        // parser taks to extract the stats for the player or the team
        parserTask task = new parserTask(guiThread_);
        task.execute(result);
        guiThread_.display("s.Parsing..." + "\n");
    }


    // ----------------------------------------------------------
    /**
     * called to make a HttpRequest to the apprpriate server
     *
     * @param url
     *            : url to request
     * @return : response from the server
     */
    public String makeHTTPRequest(String url)
    {
        String response = "";
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response1 = client.execute(request);
            InputStream inFromServer = response1.getEntity().getContent();
            BufferedReader bin =
                new BufferedReader(new InputStreamReader(inFromServer));
            String responseLine = "";
            while ((responseLine = bin.readLine()) != null)
            {
                response += responseLine;
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }

}
