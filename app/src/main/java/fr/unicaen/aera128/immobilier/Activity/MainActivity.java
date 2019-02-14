package fr.unicaen.aera128.immobilier.Activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.unicaen.aera128.immobilier.Fragments.DepotFragment;
import fr.unicaen.aera128.immobilier.Fragments.HasardFragment;
import fr.unicaen.aera128.immobilier.Fragments.ListFragment;
import fr.unicaen.aera128.immobilier.Fragments.MainFragment;
import fr.unicaen.aera128.immobilier.Fragments.SavedFragment;
import fr.unicaen.aera128.immobilier.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragmentAccueil;
    private Fragment fragmentHasard;
    private Fragment fragmentListe;
    private Fragment fragmentDepot;
    private Fragment fragmentSaved;

    private static final int FRAGMENT_ACCUEIL = 0;
    private static final int FRAGMENT_HASARD = 1;
    private static final int FRAGMENT_LISTE = 2;
    private static final int FRAGMENT_DEPOT = 3;
    private static final int FRAGMENT_SAVED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showAccueilFragment();
    }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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

    private void showAccueilFragment() {
        if (this.fragmentAccueil == null)
            this.fragmentAccueil = MainFragment.newInstance();
        this.startTransactionFragment(this.fragmentAccueil);
    }

    private void showHasardFragment() {
        if (this.fragmentHasard == null) this.fragmentHasard = HasardFragment.newInstance();
        this.startTransactionFragment(this.fragmentHasard);
    }

    private void showListFragment() {
        if (this.fragmentListe == null) this.fragmentListe = ListFragment.newInstance();
        this.startTransactionFragment(this.fragmentListe);
    }

    private void showDepotFragment() {
        if (this.fragmentDepot == null) this.fragmentDepot = DepotFragment.newInstance(null, null);
        this.startTransactionFragment(this.fragmentDepot);
    }

    private void showSavedFragment() {
        if (this.fragmentSaved == null) this.fragmentSaved = SavedFragment.newInstance();
        this.startTransactionFragment(this.fragmentSaved);
    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main, fragment).commit();
        }
    }
}
