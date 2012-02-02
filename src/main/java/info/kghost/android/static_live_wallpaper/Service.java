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
		private final SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
			public void surfaceCreated(SurfaceHolder holder) {
				draw();
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				draw();
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
			}
		};

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
			// setTouchEventsEnabled(true);
			draw();
			holder.addCallback(callback);

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
			this.getSurfaceHolder().removeCallback(callback);
			super.onDestroy();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				draw();
			}
		}

		private void draw() {
			if (!isVisible())
				return;
			Canvas c = null;
			try {
				c = getSurfaceHolder().lockCanvas();
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
					getSurfaceHolder().unlockCanvasAndPost(c);
			}
		}
	}
}
