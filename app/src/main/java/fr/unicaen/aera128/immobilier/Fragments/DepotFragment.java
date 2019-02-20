package fr.unicaen.aera128.immobilier.Fragments;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import fr.unicaen.aera128.immobilier.DB.AnnonceDataSource;
import fr.unicaen.aera128.immobilier.Models.Propriete;
import fr.unicaen.aera128.immobilier.Models.Vendeur;
import fr.unicaen.aera128.immobilier.R;
import fr.unicaen.aera128.immobilier.Utils.Tool;
import fr.unicaen.aera128.immobilier.Utils.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DepotFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DepotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepotFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<Vendeur> listVendeur;
    private ArrayAdapter<String> adapter;
    private ViewPagerAdapter adapterPhoto;

    //components
    private EditText editTitre;
    private EditText editDescription;
    private EditText editPiece;
    private EditText editCara;
    private ArrayList<String> listCaracteristiques;
    private EditText editPrix;
    private EditText editVille;
    private EditText editCodePostal;
    private ArrayList<String> listPhoto;


    private Button btnAdd;
    private Button btnPhoto;
    private Button btnVendeur;
    private FloatingActionButton btnPreview;
    private ListView listView;
    private ViewPager viewPager;
    private Spinner spinner;

    private AnnonceDataSource annonceDB;
    private int REQUEST_TAKE_PHOTO;

    public DepotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DepotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DepotFragment newInstance() {
        DepotFragment fragment = new DepotFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            DepotFragment fr = (DepotFragment) getFragmentManager().getFragment(savedInstanceState, "SaveDepot");
            listPhoto = fr.getListPhoto();
            listCaracteristiques = fr.getListCaracteristiques();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_depot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        annonceDB = new AnnonceDataSource(getActivity());
        annonceDB.open();

        editTitre = getActivity().findViewById(R.id.editTitre);
        editDescription = getActivity().findViewById(R.id.editDescription);
        editPiece = getActivity().findViewById(R.id.editPiece);
        editCara = getActivity().findViewById(R.id.editCara);
        editPrix = getActivity().findViewById(R.id.editPrix);
        editVille = getActivity().findViewById(R.id.editVille);
        editCodePostal = getActivity().findViewById(R.id.editCodePostal);

        if (listPhoto == null) {
            listPhoto = new ArrayList<String>();
        } else {
            if (listPhoto.size() > 0) {
                viewPager.setVisibility(View.VISIBLE);
            }
        }
        viewPager = getActivity().findViewById(R.id.carouselDepot);
        adapterPhoto = new ViewPagerAdapter(getContext(), listPhoto);
        viewPager.setAdapter(adapterPhoto);

        if (listCaracteristiques == null) {
            listCaracteristiques = new ArrayList<String>();
        }
        adapter = new ArrayAdapter<String>(getContext(), R.layout.item_list, R.id.item_list_cara, listCaracteristiques);
        listView = getActivity().findViewById(R.id.listCara);
        listView.setAdapter(adapter);
        btnAdd = getActivity().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editCara.getText().toString().equals("")) {
                    listCaracteristiques.add(editCara.getText().toString());
                    editCara.setText("");
                    adapter.notifyDataSetChanged();
                    Tool.setListViewHeightBasedOnChildren(listView);
                }
            }
        });

        listVendeur = annonceDB.getAllVendeur();
        ArrayAdapter<Vendeur> adapterVendeur = new ArrayAdapter<Vendeur>(getActivity(), R.layout.item_list, R.id.item_list_cara, listVendeur);

        spinner = getActivity().findViewById(R.id.spinnerVendeur);
        spinner.setAdapter(adapterVendeur);

        btnPhoto = getActivity().findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });

        btnPreview = getActivity().findViewById(R.id.btnPreview);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    Propriete propriete = new Propriete();
                    Random generator = new Random();
                    int n = 100000000;
                    propriete.setId(String.valueOf(generator.nextInt(n)));
                    propriete.setTitre(editTitre.getText().toString());
                    propriete.setDescription(editDescription.getText().toString());
                    propriete.setNbPieces(Integer.parseInt(editPiece.getText().toString()));
                    propriete.setCaracteristiques(listCaracteristiques);
                    propriete.setPrix(Integer.parseInt(editPrix.getText().toString()));
                    propriete.setVille(editVille.getText().toString());
                    propriete.setCodePostal(editCodePostal.getText().toString());
                    propriete.setVendeur(listVendeur.get(spinner.getSelectedItemPosition()));
                    propriete.setImages(listPhoto);
                    propriete.setDate(System.currentTimeMillis());

                    HasardFragment fr = HasardFragment.newInstance(propriete, false);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    fragmentTransaction.replace(R.id.frame_main, fr);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btnVendeur = getActivity().findViewById(R.id.btnAddVendeur);
        btnVendeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVendeurFragment fr = AddVendeurFragment.newInstance();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.frame_main, fr);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    private boolean isFormValid() {
        if (editTitre.getText().toString() == "") return false;
        if (editDescription.getText().toString() == "") return false;
        if (editPiece.getText().toString() == "") return false;
        if (listCaracteristiques.size() == 0) return false;
        if (editPrix.getText().toString() == "") return false;
        if (editVille.getText().toString() == "") return false;
        if (editCodePostal.getText().toString() == "") return false;
        if (listVendeur.size() == 0) return false;
        if (listPhoto.size() == 0) return false;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        try {
            Bitmap imgTmp = (Bitmap) extras.get("data");
            listPhoto.add(Tool.saveImage(imgTmp));
            if (listPhoto.size() > 0) {
                viewPager.setVisibility(View.VISIBLE);
            }
            adapterPhoto.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, "SaveDepot", this);
    }

    public ArrayList<String> getListCaracteristiques() {
        return listCaracteristiques;
    }

    public ArrayList<String> getListPhoto() {
        return listPhoto;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listPhoto != null) {
            if (listPhoto.size() > 0) {
                viewPager.setVisibility(View.VISIBLE);
            }
        }
//        if (listCaracteristiques != null){
//            if (listCaracteristiques.size() > 0){
//                adapter.notifyDataSetChanged();
//                setListViewHeightBasedOnChildren(listView);
//            }
//        }
    }


}
