package info.kghost.android.static_live_wallpaper;

import java.io.FileNotFoundException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class Service extends WallpaperService {
	static final String SHARED_PREFS_NAME = "settings";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new WallEngine();
	}

	class WallEngine extends Engine {
		private BroadcastReceiver receiver;

		WallEngine() {
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			// setTouchEventsEnabled(true);

			IntentFilter filter = new IntentFilter(
					"info.kghost.android.static_live_wallpaper.REFRESH");
			receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					draw();
				}
			};
			registerReceiver(receiver, filter);
		}

		@Override
		public void onDestroy() {
			unregisterReceiver(receiver);
			super.onDestroy();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				draw();
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			draw();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void draw() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					int portrait = c.getHeight() > c.getWidth() ? Settings.PORTRAIT
							: Settings.LANDSCAPE;
					Bitmap bm = BitmapFactory
							.decodeStream(openFileInput(Settings.FILE_USE[portrait]));
					if (bm != null)
						c.drawBitmap(bm, new Matrix(), null);
				}
			} catch (FileNotFoundException e) {
				Log.e("StaticLiveWallpaperEngine", "Error load bitmap");
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}
		}
	}
}
