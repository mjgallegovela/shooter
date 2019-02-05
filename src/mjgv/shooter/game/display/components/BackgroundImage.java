package mjgv.shooter.game.display.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BackgroundImage extends DrawableItem {

	private Bitmap backgroundBitmap;

	public BackgroundImage(Bitmap drawableItem) {
		super(null, 0, 0);
		this.backgroundBitmap = drawableItem;
	}

	@Override
	public Long getIdentifier() {
		return 10L;
	}

	@Override
	public void applyTime() {
		/* Empty */
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(backgroundBitmap, 0, 0, null);
	}

}
