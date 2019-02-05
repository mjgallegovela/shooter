package mjgv.shooter.game.data.android.impl;

import java.util.ArrayList;
import java.util.List;

import mjgv.shooter.game.data.TopScoreDAO;
import mjgv.shooter.game.data.TopScoreDTO;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TopScoreDAOSQLiteImpl extends SQLiteOpenHelper implements TopScoreDAO {

	private static final String TABLE_NAME = "top_scores";

	private static final String TABLE_SQL_CREATION = "CREATE TABLE " + TABLE_NAME
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER, name TEXT, date LONG)";

	private static final String TABLE_SQL_INSERT = "INSERT INTO " + TABLE_NAME + " VALUES ( null, ";

	private static final String TABLE_SQL_SELECT = "SELECT name, score, date FROM " + TABLE_NAME
			+ " ORDER BY score DESC, date ASC LIMIT ";

	private static final String DB_NAME = "shooterDB";

	// Métodos de SQLiteOpenHelper
	public TopScoreDAOSQLiteImpl(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_SQL_CREATION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// En caso de una nueva versión habría que actualizar las tablas
	}

	// Métodos de AlmacenPuntuaciones
	public void addNewSocore(TopScoreDTO newScoreDTO) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(TABLE_SQL_INSERT + newScoreDTO.getScore() + ", '" + newScoreDTO.getName() + "', "
				+ newScoreDTO.getDateInMillis() + ")");
		db.close();
	}

	// Métodos de AlmacenPuntuaciones
	public void deleteLowerScores(TopScoreDTO lastScoreDTO) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE score <= " + lastScoreDTO.getScore() + " AND date > '"
				+ lastScoreDTO.getDateInMillis());
		db.close();
	}

	public List<TopScoreDTO> getTopScores(int size) {
		List<TopScoreDTO> result = new ArrayList<TopScoreDTO>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(TABLE_SQL_SELECT + size, null);
		while (cursor.moveToNext()) {
			result.add(new TopScoreDTO(cursor.getString(0), cursor.getInt(1), cursor.getLong(2)));
		}
		cursor.close();
		db.close();
		return result;
	}
}