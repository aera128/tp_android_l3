package fr.unicaen.aera128.immobilier.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.unicaen.aera128.immobilier.DB.AnnonceDataSource;
import fr.unicaen.aera128.immobilier.Models.Propriete;
import fr.unicaen.aera128.immobilier.R;
import fr.unicaen.aera128.immobilier.Utils.Tool;
import fr.unicaen.aera128.immobilier.Utils.ViewPagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HasardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HasardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HasardFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    private String url = "https://ensweb.users.info.unicaen.fr/android-estate/mock-api/liste.json";

    private Propriete propriete = null;

    /**
     * Mode du fragment :
     * 0 = Propriété aléatoire
     * 1 = Propriété sauvegardée dans la base de données
     * 2 = autre propriété
     */
    private int modeFragmentHasard = 0;

    private AnnonceDataSource annonceDB;

    /**
     * Composants de la vue
     */
    private EditText editComment;
    private ListView listComment;
    private FloatingActionButton btnSave;
    private FloatingActionButton btnDel;
    private LinearLayout commentContainer;
    private Button btnAddComment;
    private Button btnPhotoSaved;

    private List<Propriete> proprietes;
    /**
     * Stockage pour un affichage dynamique en fonction des adapters
     */
    private ArrayList<String> localListPhoto;
    private ArrayList<String> localListComment;

    /**
     * Adapteurs
     */
    private ViewPagerAdapter adapterImage;
    private JsonAdapter<List<Propriete>> jsonAdapter;
    private ArrayAdapter<String> adapterComment;

    private int REQUEST_TAKE_PHOTO;

    public HasardFragment() {
    }


    /**
     * @return une nouvelle instance d'un fragment avec une propriété
     */
    public static HasardFragment newInstance(Propriete p, int modeFragmentHasard) {
        HasardFragment fragment = new HasardFragment();
        /**
         * Gestion des paramètres
         */
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, p);
        fragment.setArguments(args);
        fragment.setDB(modeFragmentHasard);
        return fragment;
    }

    /**
     * @return une nouvelle instance d'un fragment
     */
    public static HasardFragment newInstance() {
        HasardFragment fragment = new HasardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /**
             * récupération de la propriété
             */
            propriete = (Propriete) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hasard, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Déclaration de la base de données
         */
        annonceDB = new AnnonceDataSource(getActivity());
        annonceDB.open();

        /**
         * Gestion des boutons sauvegarder et suppression
         */
        btnSave = getActivity().findViewById(R.id.btnSaveAnnonce);
        btnDel = getActivity().findViewById(R.id.btnDelete);

        if (modeFragmentHasard == 1) {
            /**
             * on retire le bouton sauvegarder
             */
            btnSave.setVisibility(View.GONE);

            /**
             * Gestion du bouton suppression
             */
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (annonceDB.deletePropriete(propriete)) {
                        Toast toast = Toast.makeText(getContext(), "Annonce supprimée", Toast.LENGTH_SHORT);
                        toast.show();
                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Annonce non supprimée", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            /**
             * Affichage de la partie commentaire
             */
            commentContainer = getActivity().findViewById(R.id.commentVisio);
            commentContainer.setVisibility(View.VISIBLE);

            /**
             * Gestion de la liste des commentaires
             */
            listComment = getActivity().findViewById(R.id.listComment);
            listComment.setVisibility(View.VISIBLE);
            localListComment = new ArrayList<>(propriete.getComment());
            adapterComment = new ArrayAdapter<String>(getContext(), R.layout.item_list, R.id.item_list_cara, localListComment);
            listComment.setAdapter(adapterComment);
            adapterComment.notifyDataSetChanged();
            Tool.setListViewHeightBasedOnChildren(listComment);

            /**
             * Gestion de l'ajout d'un commentaire
             */
            editComment = getActivity().findViewById(R.id.editComment);
            btnAddComment = getActivity().findViewById(R.id.btnAddComment);
            btnAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editComment.getText().toString() != "") {
                        localListComment.add(editComment.getText().toString());
                        propriete.setComment(localListComment);
                        annonceDB.updatePropriete(propriete);
                        adapterComment.notifyDataSetChanged();
                        editComment.setText("");
                        Tool.setListViewHeightBasedOnChildren(listComment);
                    }
                }
            });

            /**
             * Gestion de l'ajout d'une photo
             */
            btnPhotoSaved = getActivity().findViewById(R.id.btnPhotoSaved);
            btnPhotoSaved.setVisibility(View.VISIBLE);
            btnPhotoSaved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (photoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            });
        } else {
            /**
             * On retire le bouton de suppression
             */
            btnDel.setVisibility(View.GONE);
            /**
             * Gestion de la sauvegarde d'une propriété
             */
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (annonceDB.insertPropriete(propriete)) {
                        Toast toast = Toast.makeText(getContext(), "Annonce sauvegardée", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Annonce non sauvegardée", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }

        /**
         * si une propriété existe on l'affiche directement
         * sinon on en affiche une aléatorement
         */
        if (propriete != null) {
            miseEnPage();
        } else {
            convert(url);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDB(int DB) {
        modeFragmentHasard = DB;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Gestion de l'affichage de la propriété
     */
    private void miseEnPage() {
        TextView titre = getActivity().findViewById(R.id.titleVisio);
        titre.setText(propriete.getTitre());

        /**
         * Affichage du carousel d'images
         */
        ViewPager viewPager = getActivity().findViewById(R.id.carousel);
        localListPhoto = new ArrayList<String>(propriete.getImages());
        adapterImage = new ViewPagerAdapter(getContext(), localListPhoto);
        viewPager.setAdapter(adapterImage);

        TextView prix = getActivity().findViewById(R.id.prixVisio);
        prix.setText(propriete.getPrix() + " €");

        TextView ville = getActivity().findViewById(R.id.villeVisio);
        ville.setText(propriete.getVille());

        TextView codePostal = getActivity().findViewById(R.id.cpVisio);
        codePostal.setText(propriete.getCodePostal());

        TextView type = getActivity().findViewById(R.id.descriptionVisio);
        type.setText(propriete.getDescription());

        TextView dateView = getActivity().findViewById(R.id.dateVisio);
        Timestamp ts = new Timestamp(propriete.getDate());
        Date date = ts;
        dateView.setText(date.toString());

        TextView nomVendeur = getActivity().findViewById(R.id.nomVendeur);
        nomVendeur.setText(propriete.getVendeur().toString());

        TextView mailVendeur = getActivity().findViewById(R.id.mailVendeur);
        mailVendeur.setText(propriete.getVendeur().getEmail());

        TextView telVendeur = getActivity().findViewById(R.id.telVendeur);
        telVendeur.setText(propriete.getVendeur().getTelephone());

        /**
         * Affichage des la liste des caractéristiques
         */
        ArrayAdapter<String> adapterCara = new ArrayAdapter<String>(getContext(), R.layout.item_list, R.id.item_list_cara, propriete.getCaracteristiques());
        ListView listView = getActivity().findViewById(R.id.listCaraVisio);
        listView.setAdapter(adapterCara);
        Tool.setListViewHeightBasedOnChildren(listView);
    }

    /**
     * Conversion d'une réponse okHttp en propriété
     */
    private void convert(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                /**
                 * Affichage d'une fenêtre de dialogue lors d'une erreur de requete okHttp
                 */
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                        alertDialog.setTitle("Info");
                        alertDialog.setMessage("Une erreur s'est produite");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainFragment fr = MainFragment.newInstance();
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_main, fr);
                                fragmentTransaction.commit();
                            }
                        });

                        alertDialog.show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected HTTP code " + response);
                    }
                    final String source = responseBody.string();
                    JSONObject json = new JSONObject(source);
                    JSONArray jsonResponse = json.getJSONArray("response");
                    Moshi moshi = new Moshi.Builder()
                            .build();
                    /**
                     * Récupération de la liste des propriétés
                     */
                    Type type = Types.newParameterizedType(List.class, Propriete.class);
                    jsonAdapter = moshi.adapter(type);
                    proprietes = jsonAdapter.fromJson(jsonResponse.toString());
                    /**
                     * Choix au hasard parmi cette liste
                     */
                    Random random = new Random();
                    propriete = proprietes.get(random.nextInt(proprietes.size()));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            miseEnPage();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Gestion de l'ajout d'une photo lors du retour sur l'application
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        try {
            Bitmap imgTmp = (Bitmap) extras.get("data");
            localListPhoto.add(Tool.saveImage(imgTmp, getContext()));
            propriete.setImages(localListPhoto);
            annonceDB.updatePropriete(propriete);
            adapterImage.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    /**
     * Affichage d'une autre propriété aléatoire lors du retour sur le fragment en mode 0
     */
    @Override
    public void onResume() {
        super.onResume();
        if (modeFragmentHasard == 0) {
            convert(url);
        }
    }
}
