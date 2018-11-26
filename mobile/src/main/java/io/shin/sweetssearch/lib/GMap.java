/*
 * google mapsの短縮URLから緯度経度を調べる
 * AndroidのIntentで得られるURLが対象
 * Web版mapから得られるURLにはcidが無いので無理
 */

package io.shin.sweetssearch.lib;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by shin on 2018/01/09.
 */

public class GMap {
    public static final String TAG = "GMap";

    public static class Location {
        public double latitude;
        public double longitude;
        public String name;
        public URL url;
        public String cid;

        public String toString() {
            return name + "  " + url.toString() + " [lat:" + latitude + ",lon:" + longitude + ",cid:" + cid + "]";
        }
    }

    public interface LocationCallback {
        void onSuccess(Location location);

        void onError(Exception ex);
    }

    public static void getLocation(Intent intent, final LocationCallback callback) {
        if (intent == null) {
            callback.onError(new Exception("intent is null"));
            return;
        }
        if (!intent.getAction().equals(Intent.ACTION_SEND)) {
            callback.onError(new Exception("invalid intent"));
            return;
        }
        final Location location = new Location();

        Bundle extras = intent.getExtras();
        location.name = extras.getString(Intent.EXTRA_SUBJECT);  // 地名
        String text = extras.getString(Intent.EXTRA_TEXT);
        getLocationFromIntentText(text, new LocationCallback() {
            @Override
            public void onSuccess(Location _location) {
                location.url = _location.url;
                location.latitude = _location.latitude;
                location.longitude = _location.longitude;
                location.cid = _location.cid;
                callback.onSuccess(location);
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });

    }

    public static void getLocationFromIntentText(String text, final LocationCallback callback) {
        final Location location = new Location();
        URL shortUrl;
        try {
            shortUrl = getUrlFromIntentText(text);
        } catch (Exception ex) {
            callback.onError(ex);
            return;
        }
        location.url = shortUrl;

        new GetCidFromShortUrlTask(new GetCidFromShortUrlTask.Callback() {
            @Override
            public void onSuccess(Location _location) {
                location.longitude = _location.longitude;
                location.latitude = _location.latitude;
                callback.onSuccess(location);
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        }).execute(shortUrl);
    }

    public static URL getUrlFromIntentText(String text) throws MalformedURLException {
        Matcher m = Pattern.compile("\\s+(https?://[^\\s]+)$").matcher(text);
        if (m.find()) return new URL(m.group(1));
        return null;
    }

    public static class GetCidFromShortUrlTask extends AsyncTask<URL, Void, Location> {

        public interface Callback {
            void onSuccess(Location location);

            void onError(Exception ex);
        }

        private Callback callback;

        public GetCidFromShortUrlTask(Callback callback) {
            super();
            this.callback = callback;
        }

        protected Location doInBackground(URL... args) {
            URL shortUrl = args[0];
            final Location location = new Location();
            //オレオレ証明書によるSSLサーバー接続でもエラーをスルーできるようにする
            SSLContext sslcontext = null;
            try {
                //証明書情報 全て空を返す
                //証明書情報　全て空を返す
                TrustManager[] tm = {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }//function

                            @Override
                            public void checkClientTrusted(X509Certificate[] chain,
                                                           String authType) throws CertificateException {
                            }//function

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain,
                                                           String authType) throws CertificateException {
                            }//function
                        }//class
                };
                sslcontext = SSLContext.getInstance("SSL");
                sslcontext.init(null, tm, null);
                //ホスト名の検証ルール　何が来てもtrueを返す
                HttpsURLConnection.setDefaultHostnameVerifier(
                        new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname,
                                                  SSLSession session) {
                                return true;
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) shortUrl.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //オレオレ証明書によるSSLサーバー接続でもエラーをスルーできるようにする
            connection.setSSLSocketFactory(sslcontext.getSocketFactory());
            try {
                connection.setRequestMethod("GET");
                connection.connect();

                int code = connection.getResponseCode();
                if (code != 200) {
                    if (callback != null)
                        callback.onError(new Exception("status code is " + code));
                    return null;
                }
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                StringBuilder builder = new StringBuilder();
                char[] buf = new char[1024];
                int numRead;
                while (0 <= (numRead = reader.read(buf))) {
                    builder.append(buf, 0, numRead);
                }
                Matcher m = Pattern.compile("cacheResponse\\(\\[\\[\\[[\\d\\.]+,(\\-?[\\d\\.]+),(\\-?[\\d\\.]+)").matcher(builder.toString());
                if (m.find()) {
                    location.longitude = Double.parseDouble(m.group(1));
                    location.latitude = Double.parseDouble(m.group(2));
                    return location;
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                if (callback != null) callback.onError(ex);
            } finally {
                if (connection != null) {
                    //ステップ7:コネクションを閉じる。
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Location location) {
            if (callback == null) return;
            if (location == null) {
                callback.onError(new Exception("cannot find location"));
                return;
            }
            callback.onSuccess(location);
        }
    }
}
