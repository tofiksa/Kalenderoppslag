package no.hioa.hvaskjer;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class AllCalendarActivity extends ListFragment {

        // Progress Dialog
        private ProgressDialog pDialog;
        
        
        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();
        Activity apa = new Activity();
        ArrayList<HashMap<String, String>> productsList;
        ArrayList<HashMap<String, String>> newsFeed;
        
        private final static String Host = "http://solr.hioa.no/solr";
        private final static String eZStudtag = "&fq=ezf_df_tags:KalenderStudentliv";
        private final static String eZTilsatttag = "&fq=ezf_df_tags:KalenderFortilsatte";
        private final static String eZLSBtag = "&fq=ezf_df_tags:KalenderLSB";
        private final static String Query = "meta_class_identifier_ms:event&fq=meta_main_parent_node_id_si:2682";
        private static String url_all_products = Host + "/select?q="+Query+eZTilsatttag+
        		"&fq=meta_is_invisible_b:false&fq=meta_language_code_ms:nor-NO&" +
        		"rows=20&wt=json&indent=on&omitHeader=true&sort=attr_from_time_dt+asc";
        		
        
        
        
        private static final String TAG_PRODUCTS = "docs";
        
        
        
        private static final String Author = "attr_text_t";
        private static final String Title = "attr_title_t";
        private static final String Displaydate = "attr_from_time_dt";
        private static final String Permalink = "meta_main_url_alias_ms";
        private static final String Sted = "attr_sted_s";

        // products JSONArray
        JSONArray products = null;
        
        //ListView lv;
        public ListView resultatList;
        public ListAdapter adapter;
        
        @Override
        public View onCreateView(LayoutInflater inflater, 
                ViewGroup container,
                Bundle savedInstanceState) {
        		super.onCreate(savedInstanceState);

        		System.out.println("Starter onCreateView");
        		View theLayout = inflater.inflate(R.layout.all_calendar, container, false);
        		resultatList = (ListView) theLayout.findViewById(android.R.id.list);

                // Hashmap for ListView
                productsList = new ArrayList<HashMap<String, String>>();
                newsFeed = new ArrayList<HashMap<String, String>>();
                
                boolean internett = haveNetworkConnection();
                
                // Loading products in Background Thread
                if (internett) {
                	
                	new LoadAllProducts().execute();
                }
                
                return theLayout;
        }

        // Response from Edit Product Activity
        @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
        	System.out.println("Starter onActivityResult");
                super.onActivityResult(requestCode, resultCode, data);

                if (resultCode == 100) {

                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                }

        }
        
        private boolean haveNetworkConnection() {
    	    boolean haveConnectedWifi = false;
    	    boolean haveConnectedMobile = false;

    	    ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
    	    for (NetworkInfo ni : netInfo) {
    	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
    	            if (ni.isConnected())
    	                haveConnectedWifi = true;
    	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
    	            if (ni.isConnected())
    	                haveConnectedMobile = true;
    	    }
    	    return haveConnectedWifi || haveConnectedMobile;
    	}

        /**
         * Background Async Task to Load all product by making HTTP Request
         * */
        class LoadAllProducts extends AsyncTask<String, String, String> implements OnItemClickListener {
        	
        	
        	final ProgressDialog pDialog = new ProgressDialog(getActivity());
                /**
                 * Before starting background thread Show Progress Dialog
                 * */
                @Override
                protected void onPreExecute() {
                        //super.onPreExecute();
                        pDialog.setMessage("Henter data fra Solr...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                }
                
        	    @Override
                protected String doInBackground(String... args) {
                	System.out.println("Starter doInBackground");
                                    // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        // getting JSON string from URL
                        JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

                        try {
                        
                                products = json.getJSONObject("response").getJSONArray(TAG_PRODUCTS);
                                if (products.length() > 0) {
                                	for (int i = 0; i < products.length(); i++) {
                                        JSONObject c = products.getJSONObject(i);

                                        // Storing each json item in variable
                                        String date = c.getString(Displaydate);
                                        String name = c.getString(Title);
                                        String id = c.getString(Author);
                                        String link = c.getString(Permalink);
                                        String sted = c.getString(Sted);
                                        
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                        Date day;
                                        int month;
                                        int year;
                                        String datoen = "";
                                        Date d;
										try {
											day = df.parse(date);
											String dagg = new SimpleDateFormat("dd.MM.yyyy").format(day);
											String tidd = new SimpleDateFormat("HH:mm:ss").format(day);
											datoen = tidd;
											date = dagg + " " + tidd;
											System.out.println(dagg + " " + datoen);
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
                                       
                                        
                                        // creating new HashMap
                                        HashMap<String, String> map = new HashMap<String, String>();

                                        // adding each child node to HashMap key => value
                                        map.put(Displaydate, date);
                                        map.put(Title, name);
                                        map.put(Author, id);
                                        map.put(Permalink, link);
                                        map.put(Sted, sted);

                                        // adding HashList to ArrayList
                                        productsList.add(map);
                                   }
                                
                                } else {
		                        	System.out.println("Oops...ingen doc!!!");
                                }
                        } catch (JSONException e) {
                                e.printStackTrace();
                        }

                        return null;
                }

                /**
                 * After completing background task Dismiss the progress dialog
                 * **/
                @Override
                protected void onPostExecute(String file_url) {
                        pDialog.dismiss();

                        getActivity().runOnUiThread(new Runnable() {
                                @Override
								public void run() {
                                        /**
                                         * Updating parsed JSON data into ListView
                                         * */
                                		
                                		System.out.println();
                                        adapter = new SimpleAdapter(
                                                        getActivity(), productsList,
                                                        R.layout.kalender_liste, new String[] { Displaydate,
                                                                        Title, Author, Sted, Permalink},
                                                        new int[] { R.id.date, R.id.title, R.id.author, R.id.sted, R.id.link });
                                		
                                        
                                        // updating listview
                                        resultatList = (ListView) getActivity().findViewById(android.R.id.list);
                                        resultatList.setAdapter(adapter);
                                        
                                        resultatList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                                        	@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                                                
                                        		String  U_id = ((TextView)view.findViewById(R.id.link)).getText().toString();
                                        		String  U_sted = ((TextView)view.findViewById(R.id.sted)).getText().toString();
                                        		String  U_tid = ((TextView)view.findViewById(R.id.date)).getText().toString();
                                        		
                                        		getPage(U_id, U_sted, U_tid);
	
                                        	}

                            				
                                        });
                                }
                        });
                        

                }
                
                public void getPage(final String urlid, final String sted, final String tid) {
                	Thread downloadThread = new Thread() {                     
                	    @Override
						public void run() {
                	    	
                	    	String host = "http://www.hioa.no";
                	    	
                	    	Intent i = new Intent(getActivity().getApplicationContext(), ShowItem.class);
            	    		i.putExtra("url", urlid);
            	    		i.putExtra("tid", tid);
            	    		i.putExtra("sted", sted);
            	    		
            	    		startActivity(i);
                	    	
                	    	
                	    }
                	};
            		downloadThread.start();
            		
                }
                
                @Override
        		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        				long arg3) {
        			// TODO Auto-generated method stub

        		}

        }

}
