package info.kghost.android.static_live_wallpaper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class Settings extends Activity {
	private static final String FILE_TMP = "static-live-wallpaper-temp.png";
	static final String FILE_USE = "static-live-wallpaper.png";
	private static final int REQ_CODE_PICK_IMAGE = 1;
	private FileOutputStream output;

	private void closeOutput() {
		if (output != null)
			try {
				output.close();
			} catch (IOException e) {
			}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.settings);

		try {
			Bitmap bm = BitmapFactory.decodeStream(openFileInput(FILE_USE));
			((ImageView) findViewById(R.id.setting_preview)).setImageBitmap(bm);
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "Please pick background image",
					Toast.LENGTH_LONG).show();
		}

		findViewById(R.id.setting_pick).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						closeOutput();
						try {
							output = openFileOutput(FILE_TMP,
									MODE_WORLD_WRITEABLE);
						} catch (FileNotFoundException e1) {
							Toast.makeText(
									Settings.this,
									"Open file error: "
											+ e1.getLocalizedMessage(),
									Toast.LENGTH_LONG).show();
						}
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(
								metrics);

						Intent i = new Intent(Intent.ACTION_PICK, null);
						i.setType("image/*");
						i.putExtra("crop", "true");
						i.putExtra("scale", true);
						i.putExtra("outputX", metrics.widthPixels);
						i.putExtra("outputY", metrics.heightPixels);
						i.putExtra("aspectX", metrics.widthPixels);
						i.putExtra("aspectY", metrics.heightPixels);
						i.putExtra("noFaceDetection", true);
						i.putExtra("output",
								Uri.fromFile(getFileStreamPath(FILE_TMP)));
						i.putExtra("return-data", false);

						try {
							startActivityForResult(i, REQ_CODE_PICK_IMAGE);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(Settings.this,
									"Can't not find photo picker",
									Toast.LENGTH_LONG).show();
						}
					}
				});

		findViewById(R.id.setting_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					FileInputStream in = openFileInput(FILE_TMP);
					FileOutputStream out = openFileOutput(FILE_USE,
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
				} catch (IOException e) {
					Toast.makeText(Settings.this,
							"Save image error: " + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
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
		closeOutput();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_PICK_IMAGE:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bm = BitmapFactory
							.decodeStream(openFileInput(FILE_TMP));
					((ImageView) findViewById(R.id.setting_preview))
							.setImageBitmap(bm);
					findViewById(R.id.setting_ok).setVisibility(View.VISIBLE);
				} catch (FileNotFoundException e) {
					Toast.makeText(Settings.this,
							"Open file error: " + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}
}
