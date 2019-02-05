package mjgv.shooter.game.display.android.playview;

import mjgv.shooter.game.handler.GameHandler;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShootSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	public ShootSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		buildInstance(context);
	}

	public ShootSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		buildInstance(context);
	}

	public ShootSurfaceView(Context context) {
		super(context);
		buildInstance(context);
	}

	private void buildInstance(Context context) {
		this.getHolder().addCallback(this);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("ShootSurfaceView ", "Surface Creada");
		GameHandler gameHandler = GameHandler.getGameHandler();
		gameHandler.doLinkSurfaceView(ShootSurfaceView.this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Empty

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("ShootSurfaceView ", "Unlink surfaceView");
		GameHandler gameHandler = GameHandler.getGameHandler();
		gameHandler.doUnlinkSurfaceView();
		Log.e("ShootSurfaceView ", "Surface Destruida");
	}

}
