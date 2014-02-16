package no.hioa.hvaskjer;



import android.support.v4.app.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class FragAllProd extends ListFragment {
		
	@Override
    public View onCreateView( LayoutInflater inflater, 
        ViewGroup container,
        Bundle savedInstanceState )
    {

		View view =  inflater.inflate(R.layout.all_products, container, false);
		
		
        return view;
        
    }
	
}