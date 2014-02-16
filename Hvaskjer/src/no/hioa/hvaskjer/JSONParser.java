package no.hioa.hvaskjer;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

        //static InputStream is = null;
		String is = null;
        static JSONObject jObj = null;
        static String json = "";

        // constructor
        public JSONParser() {

        }

        // function get json from url
        // by making HTTP POST or GET mehtod
        public JSONObject makeHttpRequest(String url, String method,
                        List<NameValuePair> params) {

                // Making HTTP request
                try {

                        // check for request method
                        if(method == "POST"){
                                // request method is POST
                                // defaultHttpClient
                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost(url);
                                httpPost.setEntity(new UrlEncodedFormEntity(params));

                                HttpResponse httpResponse = httpClient.execute(httpPost);
                                HttpEntity httpEntity = httpResponse.getEntity();
                                //is = httpEntity.getContent();
                                is = EntityUtils.toString(httpResponse.getEntity());

                        }else if(method == "GET"){
                                // request method is GET
                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                String paramString = URLEncodedUtils.format(params, "utf-8");
                                //url += "?" + paramString;
                                url += paramString;
                                System.out.println("url: " + url);
                                
                                HttpGet httpGet = new HttpGet(url);
                                
                                
                                HttpResponse httpResponse = httpClient.execute(httpGet);
                                //HttpEntity httpEntity = httpResponse.getEntity();
                                //is = httpEntity.getContent();
                                is = EntityUtils.toString(httpResponse.getEntity());
                                
                                
                        }                       

                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                } catch (ClientProtocolException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }

                try {
                        /*BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        is, "utf-8"), 8);
                        StringBuilder sb = new StringBuilder();*/
                        //char[] buffer = null;
						//int length = reader.read();
						//System.out.println("httpEntity: " + reader.read(buffer, offset, length));
                        //System.exit(0);
                        
                        /*String line = null;
                        int i = 0;
                        while ((line = reader.readLine()) != null) {
                        	//if (i > 0) {
                                sb.append(line + "\n");
                                i++;
                        	//}
                        }*/
                        //is.close();
                        //json = sb.toString();
                        //System.out.println("JSONPARSR " + i + " " + json);
                } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                		System.out.println("IS: " + is);
                        jObj = new JSONObject(is);
                } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                // return JSON String
                return jObj;

        }
}