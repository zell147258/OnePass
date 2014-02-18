package com.sebi.onepass;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class SplashActivity extends Activity {


    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 3000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SplashFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SplashFragment extends Fragment {


        private static final int GO_AHEAD_WHAT = 1;
        private long mStartTime = -1L;
        private boolean mIsDone;

        private static final String IS_DONE_KEY = "com.sebi.onepass.key.IS_DONE_KEY";
        private static final String START_TIME_KEY = "com.sebi.onepass.key.START_TIME_KEY";

        private static class UiHandler extends Handler {
            private WeakReference<SplashFragment> mActivityRef;

            public UiHandler(final SplashFragment srcActivity) {
                this.mActivityRef = new WeakReference<SplashFragment>(srcActivity);
            }

            @Override
            public void handleMessage(Message msg) {
                final SplashFragment srcActivity = this.mActivityRef.get();
                if (srcActivity == null)
                    return;
                switch (msg.what) {
                    case GO_AHEAD_WHAT:
                        long elapsedTime = SystemClock.uptimeMillis() - srcActivity.mStartTime;
                        if (elapsedTime >= MIN_WAIT_INTERVAL && !srcActivity.mIsDone) {
                            srcActivity.mIsDone = true;
                            srcActivity.goAhead();
                        }
                        break;
                }
            }

        }

        public SplashFragment() {
        }


        private void goAhead() {
            final Intent intent = new Intent(this.getActivity(), FirstAccessActivity.class);
            startActivity(intent);
            getActivity().finish();

        }

        private UiHandler mHandler;


        @Override
        public void onStart() {
            super.onStart();
            if(mStartTime == -1L)
                mStartTime = SystemClock.uptimeMillis();
            final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
            mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                mStartTime = savedInstanceState.getLong(START_TIME_KEY);
                mIsDone = savedInstanceState.getBoolean(IS_DONE_KEY);
            }
            mHandler = new UiHandler(this);

            View rootView = inflater.inflate(R.layout.fragment_splash, container, false);


            final ImageView logo = (ImageView) rootView.findViewById(R.id.imgLogoView);
            logo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    long elapsedTime = SystemClock.uptimeMillis() - mStartTime;
                    if (elapsedTime >= MIN_WAIT_INTERVAL && !mIsDone) {
                        mIsDone = true;
                        goAhead();
                        return true;
                    } else
                        return false;
                }
            });


            return rootView;
        }

        @Override
        public void onSaveInstanceState(android.os.Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(IS_DONE_KEY, mIsDone);
            outState.putLong(START_TIME_KEY, mStartTime);
        }


    }

}

