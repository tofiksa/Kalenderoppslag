package no.hioa.hvaskjer;


import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ScanScreenActivity extends Activity{

	String txtResult;
	
	public void setBarcode (String txtResult) {
	 	   this.txtResult = txtResult;
	    }
	 
	public String getBarcode () {
	   	return this.txtResult;
	}
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
			Intent i = new Intent(this,HvaSkjer.class);
			startActivity(i);
		} else if (item.getItemId() == R.id.menu_search) {
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.menu_share) {
			Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
		}
        return super.onOptionsItemSelected(item);
    }
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentIntegrator.initiateScan(this, R.layout.capture,
        R.id.viewfinder_view, R.id.preview_view, true);
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                if (result != null) {

                	System.out.println("debugmelding fra zxingjar: " + result);
                	this.txtResult = result;
                	
                	Intent i = new Intent(getApplicationContext(), HvaSkjer.class);
                	i.putExtra(result, true);
                	i.putExtra("barcode", result);
                	startActivity(i);

                }
                break;
            default:
        }
    }
}
