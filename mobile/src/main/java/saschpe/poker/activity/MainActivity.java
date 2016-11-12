package saschpe.poker.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import saschpe.poker.BuildConfig;
import saschpe.poker.R;
import saschpe.poker.adapter.CardArrayAdapter;
import saschpe.poker.util.PlanningPoker;
import saschpe.poker.widget.recycler.SpacesItemDecoration;
import saschpe.versioninfo.widget.VersionInfoDialogFragment;

import static saschpe.poker.util.PlanningPoker.DEFAULTS;
import static saschpe.poker.util.PlanningPoker.VALUES;

public final class MainActivity extends AppCompatActivity {
    private static final String PREFS_FLAVOR = "flavor2";
    private static final String STATE_FLAVOR = "flavor";

    private CardArrayAdapter arrayAdapter;
    private FloatingActionButton fab;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private LinearSnapHelper linearSnapHelper;
    private @PlanningPoker.Flavor int flavor;
    private RecyclerView recyclerView;
    private SpacesItemDecoration gridSpacesDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            //noinspection WrongConstant
            flavor = savedInstanceState.getInt(STATE_FLAVOR, PlanningPoker.FIBONACCI);
        } else {
            // Either load flavor from previous invocation or use default
            //noinspection WrongConstant
            flavor = PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt(PREFS_FLAVOR, PlanningPoker.FIBONACCI);
        }

        // Compute spacing between cards
        float marginDp = 8;
        int spacePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());

        // Setup recycler adapter
        arrayAdapter = new CardArrayAdapter(this, VALUES.get(flavor), CardArrayAdapter.BIG_CARD_VIEW_TYPE, DEFAULTS.get(flavor));
        arrayAdapter.setOnSmallCardClickListener(new CardArrayAdapter.OnSmallCardClickListener() {
            @Override
            public void onCardClick(int position) {
                displayBigCards();
                recyclerView.scrollToPosition(position);
            }
        });

        // Setup recycler layout managers
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearSnapHelper = new LinearSnapHelper();
        gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridSpacesDecoration = new SpacesItemDecoration(spacePx, SpacesItemDecoration.VERTICAL);

        // Setup recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacePx, SpacesItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(arrayAdapter);
        recyclerView.scrollToPosition(DEFAULTS.get(flavor));
        linearSnapHelper.attachToRecyclerView(recyclerView);

        // Setup floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getLayoutManager() == linearLayoutManager) {
                    displaySmallCards();
                } else {
                    displayBigCards();
                }
            }
        });
    }

    private void displayBigCards() {
        arrayAdapter.setViewType(CardArrayAdapter.BIG_CARD_VIEW_TYPE);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.removeItemDecoration(gridSpacesDecoration);
        linearSnapHelper.attachToRecyclerView(recyclerView);
        fab.setImageResource(R.drawable.ic_view_module);
    }

    private void displaySmallCards() {
        arrayAdapter.setViewType(CardArrayAdapter.SMALL_CARD_VIEW_TYPE);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(gridSpacesDecoration);
        linearSnapHelper.attachToRecyclerView(null);
        fab.setImageResource(R.drawable.ic_view_array);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Persist current flavor for next invocation
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(PREFS_FLAVOR, flavor)
                .apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current flavor over configuration change
        outState.putInt(STATE_FLAVOR, flavor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        switch (flavor) {
            case PlanningPoker.FIBONACCI:
                menu.findItem(R.id.fibonacci).setChecked(true);
                break;
            case PlanningPoker.T_SHIRT_SIZES:
                menu.findItem(R.id.t_shirt_sizes).setChecked(true);
                break;
            case PlanningPoker.IDEAL_DAYS:
                menu.findItem(R.id.ideal_days).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fibonacci:
                updateFlavor(PlanningPoker.FIBONACCI);
                item.setChecked(true);
                break;
            case R.id.t_shirt_sizes:
                updateFlavor(PlanningPoker.T_SHIRT_SIZES);
                item.setChecked(true);
                break;
            case R.id.ideal_days:
                updateFlavor(PlanningPoker.IDEAL_DAYS);
                item.setChecked(true);
                break;
            case R.id.version_info:
                VersionInfoDialogFragment
                        .newInstance(
                                getString(R.string.app_name),
                                BuildConfig.VERSION_NAME,
                                "Sascha Peilicke",
                                R.mipmap.ic_launcher)
                        .show(getFragmentManager(), "version_info");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFlavor(@PlanningPoker.Flavor int flavor) {
        this.flavor = flavor;
        arrayAdapter.replaceAll(VALUES.get(flavor));
        recyclerView.scrollToPosition(DEFAULTS.get(flavor));
    }
}
