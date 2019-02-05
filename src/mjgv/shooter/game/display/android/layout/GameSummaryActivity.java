package mjgv.shooter.game.display.android.layout;

import mjgv.shooter.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameSummaryActivity extends GameBaseActivity {
	public static final String SCORE_BUNDLE_KEY = "scoreBundleKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_summary);

		// Recuperamos la información pasada en el intent
		Bundle bundle = this.getIntent().getExtras();

		// Construimos el mensaje a mostrar
		((TextView) this.findViewById(R.id.lblSummaryScore)).setText("Tu puntuación: "
				+ bundle.getString(SCORE_BUNDLE_KEY));

		Button btnRePlay = (Button) this.findViewById(R.id.btnRePlay);
		btnRePlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rePlay();
			}
		});

		Button btnMenu = (Button) this.findViewById(R.id.btnNext);
		btnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GameSummaryActivity.this, MenuActivity.class);
				startActivity(intent);
			}
		});

	}

	private void rePlay() {
		Intent intent = new Intent(GameSummaryActivity.this, PlayActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		rePlay();
	}

}
