package alvi17.klooni1010;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import alvi17.klooni1010.Klooni;

public class AndroidLauncher extends AndroidApplication {

	Handler handler;
	int adshown=0;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Globals.context=AndroidLauncher.this;
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Klooni(), config);
		handler=new Handler();
		initFullScreenAd();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("MainActivity","OnResume");

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("MainActivity","OnPause");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.e("MainActivity","Onbackpressed");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public void initFullScreenAd()
	{

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final InterstitialAd interstitialAd=new  InterstitialAd(AndroidLauncher.this);
				interstitialAd.setAdUnitId("ca-app-pub-6508526601344465/5592170834");
				AdRequest aRequest = new AdRequest.Builder().addTestDevice("858A1315E0C450B387A59834A836CF5D").build();

				// Begin loading your interstitial.  0754C239B1E2E19421FDE46BCEFB8855
				interstitialAd.loadAd(aRequest);
				interstitialAd.setAdListener(
						new AdListener() {
							@Override
							public void onAdLoaded() {
								super.onAdLoaded();
								interstitialAd.show();
							}
						}
				);
			}
		},30000);

	}


}
