package fr.unicaen.aera128.immobilier.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tool {

    /**
     * Singleton donc le constructeur est privé
     */
    private Tool() {
    }

    /**
     * Hauteur dynamique d'une listview en fonction de son nombre d'éléments
     */
    public static boolean setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            /**
             * Récupération de la hauteur de tous les items
             */
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            /**
             * Récupération de la hauteur de tous les séparateurs/diviseurs des items
             */
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            /**
             * Récupération de la marge
             */
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            /**
             * Définition de la hauteur
             */
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sauvegarde d'une image
     *
     * @return le chemin de l'image sauvegardée
     */
    public static String saveImage(Bitmap finalBitmap, Context context) {
        /**
         * Dossier de l'application
         */
        String dir = "/TPImmobilier";

        /**
         * Récupération du path du cache directory
         */
        String root = context.getCacheDir().toString();
        File myDir = new File(root + dir);
        myDir.mkdirs();

        /**
         * Génération de l'image
         */
        Random generator = new Random();
        int n = 100000;
        n = generator.nextInt(n);
        String fname = "image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return root + dir + "/" + fname;
    }

    /**
     * Conversion d'une liste en chaine de caractère
     * @return une liste
     */
    public static String convertArrayToString(List<String> array) {
        String strSeparator = "__,__";
        String str = "";
        for (int i = 0; i < array.size(); i++) {
            str = str + array.get(i);
            if (i < array.size() - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    /**
     * Conversion d'une chaine de caractère en liste
     * @return un String
     */
    public static List<String> convertStringToArray(String str) {
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        return Arrays.asList(arr);
    }
}
