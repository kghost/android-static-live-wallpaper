package info.kghost.android.static_live_wallpaper;

import java.util.Observable;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;

public class LinearLayout extends android.widget.LinearLayout {
	public LinearLayout(Context context) {
		super(context);
	}

	public LinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private class MyObservable extends Observable {
		public void set() {
			setChanged();
		}
	}

	private MyObservable observable = new MyObservable();

	public Observable getSizeObserver() {
		return observable;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		observable.set();
		observable.notifyObservers(new Point(w, h));
	}
}
