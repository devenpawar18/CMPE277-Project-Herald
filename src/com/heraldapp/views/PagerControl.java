package com.heraldapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.heraldapp.R;

/**
 * 
 * @author DEVEN Custom Control View Pager
 */
public class PagerControl extends View {
	private static final String TAG = "DeezApps.Widget.PagerControl";

	private static final int DEFAULT_BAR_COLOR = 0xaa777777;
	private static final int DEFAULT_HIGHLIGHT_COLOR = 0xaa999999;
	private static final int DEFAULT_FADE_DELAY = 2000;
	private static final int DEFAULT_FADE_DURATION = 500;

	private int numPages, currentPage, position;
	private Paint barPaint, highlightPaint;
	private int fadeDelay, fadeDuration;
	private float ovalRadius;

	// private Animation fadeOutAnimation;
	private Bitmap blueDot, greenDot;

	public PagerControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.com_deezapps_widget_PagerControl);
		int barColor = a.getColor(
				R.styleable.com_deezapps_widget_PagerControl_barColor,
				DEFAULT_BAR_COLOR);
		int highlightColor = a.getColor(
				R.styleable.com_deezapps_widget_PagerControl_highlightColor,
				DEFAULT_HIGHLIGHT_COLOR);
		fadeDelay = a.getInteger(
				R.styleable.com_deezapps_widget_PagerControl_fadeDelay,
				DEFAULT_FADE_DELAY);
		fadeDuration = a.getInteger(
				R.styleable.com_deezapps_widget_PagerControl_fadeDuration,
				DEFAULT_FADE_DURATION);
		ovalRadius = a.getDimension(
				R.styleable.com_deezapps_widget_PagerControl_roundRectRadius,
				0f);
		a.recycle();

		barPaint = new Paint();
		barPaint.setColor(barColor);

		highlightPaint = new Paint();
		highlightPaint.setColor(highlightColor);

		blueDot = BitmapFactory.decodeResource(getResources(),
				R.drawable.dot_blauw);
		greenDot = BitmapFactory.decodeResource(getResources(),
				R.drawable.dot_wit);
	}

	/**
	 * 
	 * @return current number of pages
	 */
	public int getNumPages() {
		return numPages;
	}

	/**
	 * 
	 * @param numPages
	 *            must be positive number
	 */
	public void setNumPages(int numPages) {
		if (numPages <= 0) {
			throw new IllegalArgumentException("numPages must be positive");
		}
		this.numPages = numPages;
		invalidate();
	}

	/**
	 * 0 to numPages-1
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * 
	 * @param currentPage
	 *            0 to numPages-1
	 */
	public void setCurrentPage(int currentPage) {
		if (currentPage < 0 || currentPage >= numPages) {
			throw new IllegalArgumentException(
					"currentPage parameter out of bounds");
		}
		if (this.currentPage != currentPage) {
			this.currentPage = currentPage;
			this.position = currentPage * getPageWidth();
			invalidate();
		}
	}

	/**
	 * Equivalent to the width of the view divided by the current number of
	 * pages.
	 * 
	 * @return page width, in pixels
	 */
	public int getPageWidth() {
		return getWidth() / numPages;
	}

	/**
	 * 
	 * @param position
	 *            can be -pageWidth to pageWidth*(numPages+1)
	 */
	public void setPosition(int position) {
		if (this.position != position) {
			this.position = position;
			invalidate();
		}
	}

	/**
	 * 
	 * @param canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		int margin = 4;
		int offsetX = (getWidth() - (numPages * greenDot.getWidth()) - (margin * numPages)) / 2;
		for (int i = 0; i < numPages; i++) {

			if (i == currentPage) {
				canvas.drawBitmap(blueDot, offsetX + (i * greenDot.getWidth())
						+ (margin * i), 0, barPaint);
			} else {
				canvas.drawBitmap(greenDot, offsetX + (i * greenDot.getWidth())
						+ (margin * i), 0, barPaint);
			}
		}
	}
}
