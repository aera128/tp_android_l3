package fr.unicaen.aera128.immobilier.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import fr.unicaen.aera128.immobilier.Models.Propriete;
import fr.unicaen.aera128.immobilier.Models.Vendeur;
import fr.unicaen.aera128.immobilier.Utils.Tool;

public class AnnonceDataSource {

    private SQLiteDatabase database;
    private AnnonceSQLiteHelper dbHelper;
    /**
     * Champs de la base de données
     */
    private String[] Colums_propriete =
            {
                    AnnonceSQLiteHelper.COLUMN_ID,
                    AnnonceSQLiteHelper.COLUMN_KEY,
                    AnnonceSQLiteHelper.COLUMN_TITRE,
                    AnnonceSQLiteHelper.COLUMN_DESC,
                    AnnonceSQLiteHelper.COLUMN_NBPIECE,
                    AnnonceSQLiteHelper.COLUMN_CARAC,
                    AnnonceSQLiteHelper.COLUMN_PRIX,
                    AnnonceSQLiteHelper.COLUMN_VILLE,
                    AnnonceSQLiteHelper.COLUMN_CP,
                    AnnonceSQLiteHelper.COLUMN_VENDEUR,
                    AnnonceSQLiteHelper.COLUMN_IMAGES,
                    AnnonceSQLiteHelper.COLUMN_DATE
            };

    private String[] Colums_vendeur =
            {
                    AnnonceSQLiteHelper.COLUMN_ID,
                    AnnonceSQLiteHelper.COLUMN_NOM,
                    AnnonceSQLiteHelper.COLUMN_PRENOM,
                    AnnonceSQLiteHelper.COLUMN_EMAIL,
                    AnnonceSQLiteHelper.COLUMN_TEL
            };


    public AnnonceDataSource(Context context) {
        dbHelper = new AnnonceSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Insertion du propriété
     */
    public boolean insertPropriete(Propriete propriete) {
        insertVendeur(propriete.getVendeur());
        /**
         * On vérifie si cette propriété existe déjà ou non
         */
        Cursor c = database.rawQuery("SELECT * FROM " + AnnonceSQLiteHelper.TABLE_PROPRIETES + " WHERE " + AnnonceSQLiteHelper.COLUMN_KEY + "=\"" + propriete.getId() + "\"", null);
        if (c.getCount() == 0) {
            /**
             * Remplissage des données
             */
            ContentValues values = new ContentValues();
            values.put(AnnonceSQLiteHelper.COLUMN_KEY, propriete.getId());
            values.put(AnnonceSQLiteHelper.COLUMN_TITRE, propriete.getTitre());
            values.put(AnnonceSQLiteHelper.COLUMN_DESC, propriete.getDescription());
            values.put(AnnonceSQLiteHelper.COLUMN_NBPIECE, propriete.getNbPieces());
            String caraTmp = Tool.convertArrayToString(propriete.getCaracteristiques());
            values.put(AnnonceSQLiteHelper.COLUMN_CARAC, caraTmp);
            values.put(AnnonceSQLiteHelper.COLUMN_PRIX, propriete.getPrix());
            values.put(AnnonceSQLiteHelper.COLUMN_VILLE, propriete.getVille());
            values.put(AnnonceSQLiteHelper.COLUMN_CP, propriete.getCodePostal());
            values.put(AnnonceSQLiteHelper.COLUMN_VENDEUR, propriete.getVendeur().getId());
            String imgTmp = Tool.convertArrayToString(propriete.getImages());
            values.put(AnnonceSQLiteHelper.COLUMN_IMAGES, imgTmp);
            values.put(AnnonceSQLiteHelper.COLUMN_DATE, propriete.getDate());
            /**
             * Insertion dans la base de données
             */
            database.insert(AnnonceSQLiteHelper.TABLE_PROPRIETES, null, values);
            Log.w(AnnonceDataSource.class.getName(), "Propriete inséré");
            return true;
        } else {
            Log.w(AnnonceDataSource.class.getName(), "Propriete déjà existante");
            return false;
        }
    }

    /**
     * Mis à jour d'une propriété lors d'un ajout d'un commentaire ou d'une image
     */
    public boolean updatePropriete(Propriete propriete) {
        /**
         * On vérifie si cette propriété existe
         */
        Cursor c = database.rawQuery("SELECT * FROM " + AnnonceSQLiteHelper.TABLE_PROPRIETES + " WHERE " + AnnonceSQLiteHelper.COLUMN_ID + "=\"" + propriete.getId() + "\"", null);
        if (c.getCount() != 0) {
            /**
             * Remplissage des données
             */
            ContentValues values = new ContentValues();
            values.put(AnnonceSQLiteHelper.COLUMN_IMAGES, Tool.convertArrayToString(propriete.getImages()));
            values.put(AnnonceSQLiteHelper.COLUMN_COMMENT, Tool.convertArrayToString(propriete.getComment()));

            /**
             * Mis à jour dans la base de données
             */
            database.update(AnnonceSQLiteHelper.TABLE_PROPRIETES, values, AnnonceSQLiteHelper.COLUMN_ID + " = " + propriete.getId(), null);
            Log.w(AnnonceDataSource.class.getName(), "Propriete mis à jour");
            return true;
        } else {
            Log.w(AnnonceDataSource.class.getName(), "Propriete non mis à jour");
            return false;
        }
    }

    /**
     * Insertion d'un vendeur
     */
    public boolean insertVendeur(Vendeur vendeur) {
        /**
         * On vérifie si ce vendeur existe ou non
         */
        Cursor c = database.rawQuery("SELECT * FROM " + AnnonceSQLiteHelper.TABLE_VENDEURS + " WHERE " + AnnonceSQLiteHelper.COLUMN_ID + "=\"" + vendeur.getId() + "\"", null);
        if (c.getCount() == 0) {
            /**
             * Remplissage des données
             */
            ContentValues values = new ContentValues();
            values.put(AnnonceSQLiteHelper.COLUMN_ID, vendeur.getId());
            values.put(AnnonceSQLiteHelper.COLUMN_NOM, vendeur.getNom());
            values.put(AnnonceSQLiteHelper.COLUMN_PRENOM, vendeur.getPrenom());
            values.put(AnnonceSQLiteHelper.COLUMN_EMAIL, vendeur.getEmail());
            values.put(AnnonceSQLiteHelper.COLUMN_TEL, vendeur.getTelephone());

            /**
             * Insertion dans la base de données
             */
            database.insert(AnnonceSQLiteHelper.TABLE_VENDEURS, null, values);
            Log.w(AnnonceDataSource.class.getName(), "Vendeur inséré");
            return true;
        } else {
            Log.w(AnnonceDataSource.class.getName(), "Vendeur déja existant");
            return false;
        }
    }


    /**
     * Selection de toutes les propriétes sauvegardées dans la base de données
     */
    public ArrayList<Propriete> getAll() {
        ArrayList<Propriete> proprietes = new ArrayList<Propriete>();

        String query = "SELECT  * FROM " + AnnonceSQLiteHelper.TABLE_PROPRIETES;

        Cursor cursor = database.rawQuery(query, null);

        Propriete propriete = null;
        /**
         * tant que l'on ne tombe pas sur une propriété nulle on l'ajoute à notre liste
         */
        if (cursor.moveToFirst()) {
            do {

                /**
                 * Partie propriété
                 */
                propriete = new Propriete();
                propriete.setId(cursor.getString(0));
                propriete.setTitre(cursor.getString(2));
                propriete.setDescription(cursor.getString(3));
                propriete.setNbPieces(cursor.getInt(4));
                propriete.setCaracteristiques(Tool.convertStringToArray(cursor.getString(5)));
                propriete.setPrix(cursor.getInt(6));
                propriete.setVille(cursor.getString(7));
                propriete.setCodePostal(cursor.getString(8));
                propriete.setImages(Tool.convertStringToArray(cursor.getString(10)));
                propriete.setDate(cursor.getInt(11));
                if (cursor.getString(12) == null) {
                    propriete.setComment(new ArrayList<String>());
                } else {
                    propriete.setComment(Tool.convertStringToArray(cursor.getString(12)));
                }

                /**
                 * Partie vendeur
                 */
                Vendeur vendeur = new Vendeur();
                String q = "SELECT  * FROM " + AnnonceSQLiteHelper.TABLE_VENDEURS + " WHERE " + AnnonceSQLiteHelper.COLUMN_ID + "=\"" + cursor.getString(9) + "\"";

                Cursor c = database.rawQuery(q, null);

                if (c.moveToFirst()) {
                    do {
                        vendeur.setId(c.getString(0));
                        vendeur.setNom(c.getString(1));
                        vendeur.setPrenom(c.getString(2));
                        vendeur.setEmail(c.getString(3));
                        vendeur.setTelephone(c.getString(4));
                    } while (c.moveToNext());
                }
                propriete.setVendeur(vendeur);
                proprietes.add(propriete);
            } while (cursor.moveToNext());
        }
        return proprietes;
    }

    /**
     * Selection de tous les vendeurs sauvegardés dans la base de données
     */
    public ArrayList<Vendeur> getAllVendeur() {
        ArrayList<Vendeur> vendeurs = new ArrayList<Vendeur>();

        String q = "SELECT  * FROM " + AnnonceSQLiteHelper.TABLE_VENDEURS;

        Cursor c = database.rawQuery(q, null);
        if (c.moveToFirst()) {
            do {
                Vendeur vendeur = new Vendeur();
                vendeur.setId(c.getString(0));
                vendeur.setNom(c.getString(1));
                vendeur.setPrenom(c.getString(2));
                vendeur.setEmail(c.getString(3));
                vendeur.setTelephone(c.getString(4));
                vendeurs.add(vendeur);
            } while (c.moveToNext());
        }
        return vendeurs;
    }

    /**
     * Suppression d'une propriété
     */
    public boolean deletePropriete(Propriete propriete) {
        String id = propriete.getId();
        System.out.println("Propriete deleted with id: " + id);
        database.delete(AnnonceSQLiteHelper.TABLE_PROPRIETES, AnnonceSQLiteHelper.COLUMN_ID
                + " = " + id, null);

        Cursor c = database.rawQuery("SELECT * FROM " + AnnonceSQLiteHelper.TABLE_PROPRIETES + " WHERE " + AnnonceSQLiteHelper.COLUMN_KEY + "=\"" + propriete.getId() + "\"", null);
        if (c.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Suppression d'un vendeur
     */
    public void deleteVendeur(Vendeur vendeur) {
        String id = vendeur.getId();
        System.out.println("Vendeur deleted with id: " + id);
        database.delete(AnnonceSQLiteHelper.TABLE_VENDEURS, AnnonceSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

}
