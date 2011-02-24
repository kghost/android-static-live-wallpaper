package info.kghost.android.static_live_wallpaper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class Settings extends Activity {
	static final int PORTRAIT = 0;
	static final int LANDSCAPE = 1;

	private static final String[] FILE_TMP = {
			"static-live-wallpaper-portrait-temp.png",
			"static-live-wallpaper-landscape-temp.png" };
	static final String[] FILE_USE = { "static-live-wallpaper-portrait.png",
			"static-live-wallpaper-landscape.png" };
	static final int[] LAYOUT_ID = { R.id.setting_portrait_preview,
			R.id.setting_landscape_preview };
	private DisplayMetrics metrics = new DisplayMetrics();;
	private boolean set[] = { false, false };

	private class PreviewClickListener implements OnClickListener {
		private int orientation;

		public PreviewClickListener(int o) {
			orientation = o;
		}

		public void onClick(View v) {
			try {
				FileOutputStream output = openFileOutput(FILE_TMP[orientation],
						MODE_WORLD_WRITEABLE);
				if (output != null)
					try {
						output.close();
					} catch (IOException e) {
					}
			} catch (FileNotFoundException e1) {
				Toast.makeText(Settings.this,
						"Open file error: " + e1.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
			}

			Intent i = new Intent(Intent.ACTION_PICK, null);
			i.setType("image/*");
			i.putExtra("crop", "true");
			i.putExtra("scale", true);
			if (orientation == PORTRAIT) {
				i.putExtra("outputX",
						Math.min(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("outputY",
						Math.max(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("aspectX",
						Math.min(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("aspectY",
						Math.max(metrics.widthPixels, metrics.heightPixels));
			} else {
				i.putExtra("outputX",
						Math.max(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("outputY",
						Math.min(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("aspectX",
						Math.max(metrics.widthPixels, metrics.heightPixels));
				i.putExtra("aspectY",
						Math.min(metrics.widthPixels, metrics.heightPixels));
			}
			i.putExtra("noFaceDetection", true);
			i.putExtra("output",
					Uri.fromFile(getFileStreamPath(FILE_TMP[orientation])));
			i.putExtra("return-data", false);

			try {
				startActivityForResult(i, orientation);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(Settings.this, "Can't not find photo picker",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void calculateLayout(Point point) {
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		boolean portrait = metrics.heightPixels > metrics.widthPixels;
		LinearLayout layout = ((LinearLayout) findViewById(R.id.setting_preview));
		layout.setOrientation(portrait ? LinearLayout.VERTICAL
				: LinearLayout.HORIZONTAL);
		int height, width;
		if (portrait) {
			height = metrics.widthPixels + metrics.heightPixels;
			width = Math.max(metrics.widthPixels, metrics.heightPixels);
		} else {
			width = metrics.widthPixels + metrics.heightPixels;
			height = Math.max(metrics.widthPixels, metrics.heightPixels);
		}
		double scale_rate = Math.min(((double) point.x) / width,
				((double) point.y) / height);
		height = (int) Math.floor(scale_rate
				* Math.max(metrics.widthPixels, metrics.heightPixels));
		width = (int) Math.floor(scale_rate
				* Math.min(metrics.widthPixels, metrics.heightPixels));
		findViewById(R.id.setting_portrait_preview).setLayoutParams(
				new LinearLayout.LayoutParams(width, height));
		findViewById(R.id.setting_landscape_preview).setLayoutParams(
				new LinearLayout.LayoutParams(height, width));

		for (int i = 0; i < 2; ++i) {
			try {
				Bitmap bm = BitmapFactory
						.decodeStream(openFileInput(FILE_TMP[i]));
				((ImageView) findViewById(LAYOUT_ID[i])).setImageBitmap(bm);
				set[i] = true;
			} catch (FileNotFoundException e) {
				((ImageView) findViewById(LAYOUT_ID[i]))
						.setImageResource(R.drawable.ic_menu_add_picture);
			}
		}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.settings);
		for (int i = 0; i < 2; ++i) {
			try {
				Bitmap bm = BitmapFactory
						.decodeStream(openFileInput(FILE_USE[i]));
				((ImageView) findViewById(LAYOUT_ID[i])).setImageBitmap(bm);
				set[i] = true;
			} catch (FileNotFoundException e) {
			}
			findViewById(LAYOUT_ID[i]).setOnClickListener(
					new PreviewClickListener(i));
		}

		if (set[PORTRAIT] == true && set[LANDSCAPE] == true)
			findViewById(R.id.setting_ok).setClickable(true);
		else
			findViewById(R.id.setting_ok).setClickable(false);

		((LinearLayout) findViewById(R.id.setting_preview)).getSizeObserver()
				.addObserver(new Observer() {
					public void update(Observable arg0, Object arg1) {
						calculateLayout((Point) arg1);
					}
				});

		findViewById(R.id.setting_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!(set[PORTRAIT] == true && set[LANDSCAPE] == true)) {
					Toast.makeText(Settings.this, "Please set all image",
							Toast.LENGTH_LONG).show();
					return;
				}
				for (int i = 0; i < 2; ++i) {
					try {
						FileInputStream in = openFileInput(FILE_TMP[i]);
						FileOutputStream out = openFileOutput(FILE_USE[i],
								MODE_PRIVATE);

						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						in.close();
						out.close();
					} catch (FileNotFoundException e) {
						Toast.makeText(Settings.this,
								"Save image error: " + e.getLocalizedMessage(),
								Toast.LENGTH_LONG).show();
						return;
					} catch (IOException e) {
						Toast.makeText(Settings.this,
								"Save image error: " + e.getLocalizedMessage(),
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				sendBroadcast(new Intent(
						"info.kghost.android.static_live_wallpaper.REFRESH"));
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PORTRAIT:
		case LANDSCAPE:
			if (resultCode == RESULT_OK) {
				try {
					((ImageView) findViewById(LAYOUT_ID[requestCode]))
							.setImageBitmap(BitmapFactory
									.decodeStream(openFileInput(FILE_TMP[requestCode])));
					set[requestCode] = true;
				} catch (FileNotFoundException e) {
					Toast.makeText(Settings.this,
							"Open file error: " + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
				}
				if (set[PORTRAIT] == true && set[LANDSCAPE] == true)
					findViewById(R.id.setting_ok).setClickable(true);
			}
			break;
		}
	}
}
