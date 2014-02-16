package no.hioa.hvaskjer;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class FragmentWeb extends Fragment {

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.webview, container, false);
	    WebView webView = (WebView) v.findViewById(R.id.webView);
	    
	    webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	    webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
	    
	    webView.setWebViewClient(new MyWebViewClient());
	    //webView.getSettings().setPluginsEnabled(true);
	    webView.getSettings().setBuiltInZoomControls(false); 
	    webView.getSettings().setSupportZoom(false);
	    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);   
	    webView.getSettings().setAllowFileAccess(true); 
	    webView.getSettings().setDomStorageEnabled(true);
	    webView.loadUrl("http://rif.hioa.no/Solr");
	
	    return v;
    }
    
    
    
public class MyWebViewClient extends WebViewClient {        
    /* (non-Java doc)
     * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
     */


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.endsWith(".mp4")) 
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");

            view.getContext().startActivity(intent);
            return true;
        } 
        else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
}