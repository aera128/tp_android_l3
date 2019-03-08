package fr.unicaen.aera128.immobilier.Activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.unicaen.aera128.immobilier.Fragments.DepotFragment;
import fr.unicaen.aera128.immobilier.Fragments.DetailFragment;
import fr.unicaen.aera128.immobilier.Fragments.ListFragment;
import fr.unicaen.aera128.immobilier.Fragments.MainFragment;
import fr.unicaen.aera128.immobilier.Fragments.SavedFragment;
import fr.unicaen.aera128.immobilier.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * fragments disponibles
     */
    private Fragment fragmentAccueil;
    private Fragment fragmentHasard;
    private Fragment fragmentListe;
    private Fragment fragmentDepot;
    private Fragment fragmentSaved;

    /**
     * ID des fragments
     */
    private static final int FRAGMENT_ACCUEIL = 0;
    private static final int FRAGMENT_HASARD = 1;
    private static final int FRAGMENT_LISTE = 2;
    private static final int FRAGMENT_DEPOT = 3;
    private static final int FRAGMENT_SAVED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Assignation de la vue
         */
        setContentView(R.layout.activity_main);

        /**
         * Déclaration de la Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        /**
         * Assignation de la toolbar comme action par défaut
         */
        setSupportActionBar(toolbar);

        /**
         * Initialisation
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        /**
         * Synchronisation
         */
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Affichage du frament accueil au démarrage de l'application
         */
        showAccueilFragment();
    }

    /**
     * Gestion du bouton retour (automatique avec le backstack des framents)
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Gestion de l'affichage du bon fragment en fonction de l'élement selectionné du navigation drawer
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        int id = item.getItemId();

        if (id == R.id.nav_accueil) {
            this.showFragment(FRAGMENT_ACCUEIL);
            getSupportActionBar().setTitle("Accueil");
        } else if (id == R.id.nav_hasard) {
            this.showFragment(FRAGMENT_HASARD);
            getSupportActionBar().setTitle("Annonce aléatoire");
        } else if (id == R.id.nav_liste) {
            this.showFragment(FRAGMENT_LISTE);
            getSupportActionBar().setTitle("Annonces");
        } else if (id == R.id.nav_depot) {
            this.showFragment(FRAGMENT_DEPOT);
            getSupportActionBar().setTitle("Déposer un bien");
        } else if (id == R.id.nav_saved) {
            this.showFragment(FRAGMENT_SAVED);
            getSupportActionBar().setTitle("Annonces sauvegardées");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Gestion de l'affichage
     */
    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case FRAGMENT_ACCUEIL:
                this.showAccueilFragment();
                break;
            case FRAGMENT_HASARD:
                this.showHasardFragment();
                break;
            case FRAGMENT_LISTE:
                this.showListFragment();
                break;
            case FRAGMENT_DEPOT:
                this.showDepotFragment();
                break;
            case FRAGMENT_SAVED:
                this.showSavedFragment();
                break;
            default:
                break;
        }
    }

    /**
     * Affichage
     */
    private void showAccueilFragment() {
        if (this.fragmentAccueil == null)
            this.fragmentAccueil = MainFragment.newInstance();
        this.startTransactionFragment(this.fragmentAccueil);
    }

    private void showHasardFragment() {
        if (this.fragmentHasard == null) this.fragmentHasard = DetailFragment.newInstance();
        this.startTransactionFragment(this.fragmentHasard);
    }

    private void showListFragment() {
        if (this.fragmentListe == null) this.fragmentListe = ListFragment.newInstance();
        this.startTransactionFragment(this.fragmentListe);
    }

    private void showDepotFragment() {
        if (this.fragmentDepot == null) this.fragmentDepot = DepotFragment.newInstance();
        this.startTransactionFragment(this.fragmentDepot);
    }

    private void showSavedFragment() {
        if (this.fragmentSaved == null) this.fragmentSaved = SavedFragment.newInstance();
        this.startTransactionFragment(this.fragmentSaved);
    }

    /**
     * Finalisation de l'affichage
     */
    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main, fragment).commit();
        }
    }
}
