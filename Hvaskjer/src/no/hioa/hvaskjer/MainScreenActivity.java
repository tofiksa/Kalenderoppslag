package no.hioa.hvaskjer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;


public class MainScreenActivity extends Activity{

        
        protected boolean _active = true;
        
     // Splash screen timer
        private static int SPLASH_TIME_OUT = 3000;
     
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
     
            new Handler().postDelayed(new Runnable() {
     
                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
     
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(MainScreenActivity.this, HvaSkjer.class);
                    startActivity(i);
     
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
        
        
        public void minStop() {
            _active = false;
        }
        
      //Function that will handle the touch
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                _active = false;
            }
            return true;
        }
}
