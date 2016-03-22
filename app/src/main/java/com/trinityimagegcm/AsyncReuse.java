package com.trinityimagegcm;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by aneh on 2/23/2016.
 */
public class AsyncReuse extends AsyncTask<Void, Void, Void> {
    public GetResponse getResponse = null;
    String response = "{\"status\":\"0\",\"msg\":\"Sorry something went wrong try again\",\"type\":\"asyncError\"}";
    JSONObject jsonObject;
    String URLs;
    boolean dialogE = true;
    Activity activity;
    // Dialog builder
    private ProgressDialog Dialog;

    public AsyncReuse(String url, boolean dialog, Activity activity1) {
        URLs = url;
        dialogE = dialog;
        activity = activity1;
    }

    public AsyncReuse(String url, boolean dialog) {
        URLs = url;
        dialogE = dialog;
    }

    public void getObjectQ(JSONObject object) {
        jsonObject = object;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialogE) {
            if (Dialog == null) {
                Dialog = new ProgressDialog(activity);
                Dialog.setMessage("Please Wait..");
                Dialog.setCancelable(false);
                Dialog.show();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String urlParameters = "r=" + jsonObject;
        byte[] postData = new byte[0];
        try {
            postData = urlParameters.toString().getBytes("UTF-8");
            int postDataLength = postData.length;
            URL url = new URL(URLs);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postData);
            conn.connect();

            int statusCode = conn.getResponseCode();
            Log.e("Status", "" + statusCode);
            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    // throw some exception
                    InputStream is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer responseData = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        responseData.append(line);
                        responseData.append('\r');
                    }
                    rd.close();
                    response = responseData.toString();
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    response = "{\"status\":\"0\",\"msg\":\"Connection timeout.\"}";
                    break;
            }
            closeDialog(dialogE);
        } catch (UnsupportedEncodingException e) {
            closeDialog(dialogE);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            closeDialog(dialogE);
            e.printStackTrace();
        } catch (ProtocolException e) {
            closeDialog(dialogE);
            e.printStackTrace();
        } catch (IOException e) {
            closeDialog(dialogE);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (Dialog != null) {
            Dialog.dismiss();
        }
        Log.e("Response", "response" + response);
        if (response.equals("null")) {
            response = "{\"status\":\"0\",\"msg\":\"No data to show.\"}";
            getResponse.getData(response);
        } else
            getResponse.getData(response);
    }

    private void closeDialog(boolean flag) {
        if (flag) {
            Dialog.dismiss();
        } else {
            /* Nothing to do*/
        }
    }
}
