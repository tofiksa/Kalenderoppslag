package no.hioa.hvaskjer;


import java.util.ArrayList;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class AllProductsActivity extends ListFragment {

        // Progress Dialog
        private ProgressDialog pDialog;
        
        
        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();
        Activity apa = new Activity();
        ArrayList<HashMap<String, String>> productsList;
        ArrayList<HashMap<String, String>> newsFeed;

        // url to get all products list
        //private static String url_all_products = "http://statisk.hio.no/android/get_all_products.php";
        private static String url_all_products = 
        		"http://solr.hioa.no/solr/select?q=ezf_df_tags:IKT-driftsmeldinger&" +
        		"fq=meta_language_code_ms:nor-NO&fq=meta_class_name_ms:Artikkel&rows=100&wt=json&" +
        		"fq=meta_is_invisible_b:false&indent=on&mitHeader=true&sort=meta_published_dt+desc";
        // JSON Node names
        private static final String TAG_SUCCESS = "numFound";
        //private static final String TAG_PRODUCTS = "products";
        private static final String TAG_PRODUCTS = "docs";
        
        
        //private static final String TAG_PID = "pid";
        private static final String Author = "attr_ingress_t";
        //private static final String TAG_NAME = "name";
        private static final String Title = "meta_name_t";
        private static final String Displaydate = "meta_published_dt";
        private static final String Permalink = "meta_main_url_alias_ms";
        
        
        /*private static final String TAG_NEWS = "nyheter";
        private static final String Tittel = "tittel";
        private static final String Url = "url";*/
        
        
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
        		View theLayout = inflater.inflate(R.layout.all_products, container, false);
        		//resultatList =  inflater.inflate(R.layout.all_products, container, false);
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
                // if result code 100
                if (resultCode == 100) {
                        // if result code 100 is received
                        // means user edited/deleted product
                        // reload this screen again
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
                
        	
                
                /**
                 * getting All products from url
                 * */
                @Override
                protected String doInBackground(String... args) {
                	System.out.println("Starter doInBackground");
                	
					
                	
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        // getting JSON string from URL
                        JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
                        
                        
                        
                        System.out.println("=======decode=======");
                        
                        

                        // Check your log cat for JSON reponse
                        Log.d("All Products: ", json.toString());
                        
                        
                        try {
                                // Checking for SUCCESS TAG
                                //int success = json.getInt(TAG_SUCCESS);
                                products = json.getJSONObject("response").getJSONArray(TAG_PRODUCTS);
                                
                                //System.out.println("ROWS: " + success);
                                
                                if (products.length() > 0) {
                                        // products found
                                        // Getting Array of Products
                                        
                                        // looping through All Products
                                        for (int i = 0; i < products.length(); i++) {
                                                JSONObject c = products.getJSONObject(i);

                                                // Storing each json item in variable
                                                String date = c.getString(Displaydate);
                                                String name = c.getString(Title);
                                                String id = c.getString(Author);
                                                String link = c.getString(Permalink);

                                                // creating new HashMap
                                                HashMap<String, String> map = new HashMap<String, String>();

                                                // adding each child node to HashMap key => value
                                                map.put(Displaydate, date);
                                                map.put(Title, name);
                                                map.put(Author, id);
                                                map.put(Permalink, link);

                                                // adding HashList to ArrayList
                                                productsList.add(map);
                                        }
                                } else {
                                	
                                	pDialog.setMessage("Oops...trøbbel med å hente data fra sentralen.");
                                    pDialog.setIndeterminate(false);
                                    pDialog.setCancelable(true);
                                    pDialog.show();
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
                        // dismiss the dialog after getting all products
                		System.out.println("før pDialog");
                        pDialog.dismiss();
                        // updating UI from Background Thread
                		System.out.println("har kjørt pDialog");
                        getActivity().runOnUiThread(new Runnable() {
                                @Override
								public void run() {
                                        /**
                                         * Updating parsed JSON data into ListView
                                         * */
                                		
                                		System.out.println();
                                        adapter = new SimpleAdapter(
                                                        getActivity(), productsList,
                                                        R.layout.list_item, new String[] { Displaydate,
                                                                        Title, Author, Permalink},
                                                        new int[] { R.id.date, R.id.title, R.id.author, R.id.link });
                                		
                                        
                                        // updating listview
                                        resultatList = (ListView) getActivity().findViewById(android.R.id.list);
                                        resultatList.setAdapter(adapter);
                                        
                                        resultatList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                                        	@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                                                
                                        		String  U_id = ((TextView)view.findViewById(R.id.link)).getText().toString();
                                        		getPage(U_id);
                                        		
                                        		
                                        		
                                        		
                                        	}

                            				
                                        });
                                }
                        });
                        

                }
                
                public void getPage(final String urlid) {
                	Thread downloadThread = new Thread() {                     
                	    @Override
						public void run() {
                	    	
                	    	String host = "http://www.hioa.no";
                	    	
                	    	Intent i = new Intent(getActivity().getApplicationContext(), ShowItem.class);
            	    		i.putExtra("url", urlid);
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
