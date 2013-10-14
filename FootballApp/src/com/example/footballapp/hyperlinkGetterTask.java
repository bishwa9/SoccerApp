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
 * This task contacts the appropriate server to fetch the html that has the
 * required hyperlink of the player or team
 *
 * NOTE: very similar to statsGetterTask
 *
 * @author Bishwamoy Sinha Roy
 * @version Oct 9, 2013
 */
public class hyperlinkGetterTask
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
    public hyperlinkGetterTask(MainActivity thread)
    {
        guiThread_ = thread;
    }


    protected String doInBackground(String... params)
    {
        String response = "";
        String resource = "";
        String server = "";
        String url = "";
        if (params[1] == "player")
        {
            String player = params[0];
            response = "p.p."; // prepend p for hyperlink and p for player
            server = "www.manutd.com";
            // URL for the server from which player info is being garnered
            // the search will equal to the string inputted by the user
            resource =
                "/en/Players-And-Staff/Players-And-Staff"
                    + "-Search-Results.aspx?search=" + player
                    + "&teamid=first+team";
            url = "http://" + server + resource;

        }
        else if (params[1] == "team")
        {
            response = "p.t."; // prepend p for hyperlink and t for team
            server = "www.uefa.com";
            // get resource by letter then the actual team
            // (teams are reported by number, can't be known without getting
            // the URL)
            resource =
                "/teamsandplayers/teams/atoz/letter="
                    + params[0].substring(0, 1) + "/index.html";
            url = "http://" + server + resource;
        }

        response += makeHTTPRequest(url);
        return response;
    }


    // happens on the UI thread
    protected void onPostExecute(String result)
    {
        // start another task to parse the HTML for the hyperlink
        parserTask task = new parserTask(guiThread_);
        task.execute(result);
        guiThread_.display("h.Getting stats... Brace Yourselves!" + "\n");
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
