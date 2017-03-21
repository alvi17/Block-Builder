package alvi17.klooni1010;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import alvi17.klooni1010.Klooni;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Globals.context=AndroidLauncher.this;
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Klooni(), config);
	}

}
