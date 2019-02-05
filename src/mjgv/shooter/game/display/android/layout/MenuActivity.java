package mjgv.shooter.game.display.android.layout;

import mjgv.shooter.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends GameBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.menu);
		Button btnMenu = (Button) this.findViewById(R.id.btnCreateNewGame);
		btnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, PlayActivity.class);
				startActivity(intent);
			}
		});

		// Gallery galleryInstructions = (Gallery)
		// this.findViewById(R.id.galleryInstructions);
		// gallery
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
