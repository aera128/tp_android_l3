package fr.unicaen.aera128.immobilier.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AnnonceSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_PROPRIETES = "propietes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_KEY = "cle";
    public static final String COLUMN_TITRE = "titre";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_NBPIECE = "nbPieces";
    public static final String COLUMN_CARAC = "caracteristiques";
    public static final String COLUMN_PRIX = "prix";
    public static final String COLUMN_VILLE = "ville";
    public static final String COLUMN_CP = "codePostal";
    public static final String COLUMN_VENDEUR = "vendeur";
    public static final String COLUMN_IMAGES = "images";
    public static final String COLUMN_DATE = "date";

    public static final String TABLE_VENDEURS = "vendeurs";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TEL = "telephone";


    private static final String DATABASE_NAME = "annonces.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String CREATE_PROPRIETE = "create table "
            + TABLE_PROPRIETES + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_KEY + " text not null," +
            COLUMN_TITRE + " text not null," +
            COLUMN_DESC + " text not null," +
            COLUMN_NBPIECE + " integer not null," +
            COLUMN_CARAC + " text not null," +
            COLUMN_PRIX + " integer not null," +
            COLUMN_VILLE + " text not null," +
            COLUMN_CP + " text not null," +
            COLUMN_VENDEUR + " text not null," +
            COLUMN_IMAGES + " text not null," +
            COLUMN_DATE + " integer not null," +
            "FOREIGN KEY(" + COLUMN_VENDEUR + ") REFERENCES " + TABLE_VENDEURS + "(" + COLUMN_ID + "));";

    private static final String CREATE_VENDEUR = "create table "
            + TABLE_VENDEURS + "(" +
            COLUMN_ID + " text primary key, " +
            COLUMN_NOM + " text not null," +
            COLUMN_PRENOM + " text not null," +
            COLUMN_EMAIL + " text not null," +
            COLUMN_TEL + " text not null);";

    public AnnonceSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_VENDEUR);
        database.execSQL(CREATE_PROPRIETE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AnnonceSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPRIETES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDEURS);
        onCreate(db);
    }
}
