package fr.unicaen.aera128.immobilier.Fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import fr.unicaen.aera128.immobilier.DB.AnnonceDataSource;
import fr.unicaen.aera128.immobilier.Models.Propriete;
import fr.unicaen.aera128.immobilier.R;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    private Propriete propriete = null;
    private boolean isDB = false;
    private AnnonceDataSource annonceDB;

    public HasardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HasardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HasardFragment newInstance(Propriete p, boolean isDB) {
        HasardFragment fragment = new HasardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, p);
        fragment.setArguments(args);
        fragment.setDB(isDB);
        return fragment;
    }

    public static HasardFragment newInstance() {
        HasardFragment fragment = new HasardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            propriete = (Propriete) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hasard, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        annonceDB = new AnnonceDataSource(getActivity());
        annonceDB.open();
        FloatingActionButton btnSave = getActivity().findViewById(R.id.btnSaveAnnonce);
        FloatingActionButton btnDel = getActivity().findViewById(R.id.btnDelete);

        if (isDB) {
            btnSave.setVisibility(View.GONE);
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
        }
        else {
            btnDel.setVisibility(View.GONE);
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

        if (propriete != null) {
            miseEnPage();
        } else {
            String url = "https://ensweb.users.info.unicaen.fr/android-estate/mock-api/immobilier.json";
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

    public void setDB(boolean DB) {
        isDB = DB;
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

    private void miseEnPage() {
        TextView titre = getActivity().findViewById(R.id.titleVisio);
        titre.setText(propriete.getTitre());

        ViewPager viewPager = getActivity().findViewById(R.id.carousel);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(), propriete.getImages());
        viewPager.setAdapter(adapter);

        TextView prix = getActivity().findViewById(R.id.prixVisio);
        prix.setText(propriete.getPrix() + " €");

        TextView ville = getActivity().findViewById(R.id.villeVisio);
        ville.setText(propriete.getVille());

        TextView type = getActivity().findViewById(R.id.descriptionVisio);
        type.setText(propriete.getDescription());

        TextView dateView = getActivity().findViewById(R.id.dateVisio);
        Timestamp ts = new Timestamp(propriete.getDate());
        Date date = ts;
        dateView.setText(date.toString());

        TextView nomVendeur = getActivity().findViewById(R.id.nomVendeur);
        nomVendeur.setText(propriete.getVendeur().getPrenom() + " " + propriete.getVendeur().getNom());

        TextView mailVendeur = getActivity().findViewById(R.id.mailVendeur);
        mailVendeur.setText(propriete.getVendeur().getEmail());

        TextView telVendeur = getActivity().findViewById(R.id.telVendeur);
        telVendeur.setText(propriete.getVendeur().getTelephone());
    }

    private void convert(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected HTTP code " + response);
                    }
                    String source = responseBody.string();
                    JSONObject json = new JSONObject(source);
                    JSONObject jsonResponse = json.getJSONObject("response");
                    Moshi moshi = new Moshi.Builder()
                            .build();
                    JsonAdapter<Propriete> jsonAdapter = moshi.adapter(Propriete.class);
                    propriete = jsonAdapter.fromJson(jsonResponse.toString());
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
}
