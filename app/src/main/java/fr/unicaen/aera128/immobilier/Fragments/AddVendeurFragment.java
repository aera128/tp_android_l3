package fr.unicaen.aera128.immobilier.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import fr.unicaen.aera128.immobilier.DB.AnnonceDataSource;
import fr.unicaen.aera128.immobilier.Models.Vendeur;
import fr.unicaen.aera128.immobilier.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddVendeurFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddVendeurFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVendeurFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AnnonceDataSource annonceDB;
    /**
     * Composants de la vue
     */
    private EditText editPrenom;
    private EditText editNom;
    private EditText editMail;
    private EditText editTel;
    private FloatingActionButton btnSave;

    public AddVendeurFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddVendeurFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddVendeurFragment newInstance() {
        AddVendeurFragment fragment = new AddVendeurFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_vendeur, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Déclaration de la base de données
         */
        annonceDB = new AnnonceDataSource(getActivity());
        annonceDB.open();

        /**
         * Assignation des composants de la vue
         */
        editPrenom = getActivity().findViewById(R.id.editPrenom);
        editNom = getActivity().findViewById(R.id.editNom);
        editMail = getActivity().findViewById(R.id.editMail);
        editTel = getActivity().findViewById(R.id.editTel);

        /**
         * Gestion du bouton pour sauvegarder le vendeur
         */
        btnSave = getActivity().findViewById(R.id.btnSaveVendeur);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Gestion du formulaire
                 */
                if (isFormValid()) {
                    Vendeur vendeur = new Vendeur();
                    Random generator = new Random();
                    int n = 100000000;
                    vendeur.setId(String.valueOf(generator.nextInt(n)));
                    vendeur.setPrenom(editPrenom.getText().toString());
                    vendeur.setNom(editNom.getText().toString());
                    vendeur.setEmail(editMail.getText().toString());
                    vendeur.setTelephone(editTel.getText().toString());
                    /**
                     * Insertion du vendeur dans la base de données
                     */
                    if (annonceDB.insertVendeur(vendeur)) {
                        Toast toast = Toast.makeText(getContext(), "Vendeur inséré", Toast.LENGTH_SHORT);
                        toast.show();
                        FragmentManager fm = getFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            /**
                             * Retour vers le formulaire de dépot d'une propriété
                             */
                            fm.popBackStack();
                        } else {
                            /**
                             * Retour vers le menu principal s'il n'y a pas de retour entre fragments
                             */
                            MainFragment fr = MainFragment.newInstance();
                            FragmentTransaction fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                            fragmentTransaction.replace(R.id.frame_main, fr);
                            fragmentTransaction.commit();
                        }
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Erreur veuillez réessayer", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    /**
     * Vérification du formulaire
     */
    private boolean isFormValid() {
        if (editPrenom.getText().toString() == "") return false;
        if (editNom.getText().toString() == "") return false;
        if (editMail.getText().toString() == "") return false;
        if (editTel.getText().toString() == "") return false;
        return true;
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
}
