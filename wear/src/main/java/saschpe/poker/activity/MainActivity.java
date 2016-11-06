package saschpe.poker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import saschpe.poker.R;
import saschpe.poker.adapter.PokerCardArrayAdapter;
import saschpe.poker.util.PlanningPoker;

public class MainActivity extends WearableActivity implements
        WearableActionDrawer.OnMenuItemClickListener {
    private static final String PREFS_FLAVOR = "flavor";
    private static final String STATE_FLAVOR = "flavor";

    private PlanningPoker.Flavor flavor;
    private PokerCardArrayAdapter arrayAdapter;
    private WearableActionDrawer actionDrawer;
    private WearableDrawerLayout drawerLayout;
    private WearableListView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        if (savedInstanceState != null) {
            flavor = (PlanningPoker.Flavor) savedInstanceState.getSerializable(STATE_FLAVOR);
        } else {
            // Either load flavor from previous invocation or use default
            String flavorString = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(PREFS_FLAVOR, PlanningPoker.Flavor.FIBONACCI.toString());

            flavor = PlanningPoker.Flavor.fromString(flavorString);
        }

        recyclerView = (WearableListView) findViewById(R.id.wearable_list);
        updateFlavor();
        recyclerView.setAdapter(arrayAdapter);

        // Main Wearable Drawer Layout that wraps all content
        drawerLayout = (WearableDrawerLayout) findViewById(R.id.drawer_layout);

        // Bottom Action Drawer
        actionDrawer = (WearableActionDrawer) findViewById(R.id.bottom_action_drawer);
        // Populate Action Drawer Menu
        Menu menu = actionDrawer.getMenu();
        getMenuInflater().inflate(R.menu.action_drawer_menu, menu);
        switch (flavor) {
            case FIBONACCI:
                menu.findItem(R.id.fibonacci).setChecked(true);
                break;
            case T_SHIRT_SIZES:
                menu.findItem(R.id.t_shirt_sizes).setChecked(true);
                break;
        }
        actionDrawer.setOnMenuItemClickListener(this);
        // Peeks action drawer on the bottom.
        drawerLayout.peekDrawer(Gravity.BOTTOM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Persist current flavor for next invocation
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(PREFS_FLAVOR, flavor.toString())
                .apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_FLAVOR, flavor);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fibonacci:
                flavor = PlanningPoker.Flavor.FIBONACCI;
                updateFlavor();
                item.setChecked(true);
                break;
            case R.id.t_shirt_sizes:
                flavor = PlanningPoker.Flavor.T_SHIRT_SIZES;
                updateFlavor();
                item.setChecked(true);
                break;
            case R.id.version_info:
                startActivity(new Intent(this, InfoActivity.class));
                break;
        }
        actionDrawer.closeDrawer();
        return super.onOptionsItemSelected(item);
    }

    private void updateDisplay() {
        if (isAmbient()) {
            //boxInsetLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
           /* mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);*/
        } else {
            //boxInsetLayout.setBackground(null);
            /*mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);*/
        }
    }

    private void updateFlavor() {
        switch (flavor) {
            case FIBONACCI:
                if (arrayAdapter == null) {
                    arrayAdapter = new PokerCardArrayAdapter(this, PlanningPoker.FIBONACCI_LIST);
                } else {
                    arrayAdapter.replace(PlanningPoker.FIBONACCI_LIST);
                }
                recyclerView.scrollToPosition(PlanningPoker.FIBONACCI_POSITION);
                break;
            case T_SHIRT_SIZES:
                if (arrayAdapter == null) {
                    arrayAdapter = new PokerCardArrayAdapter(this, PlanningPoker.T_SHIRT_SIZE_LIST);
                } else {
                    arrayAdapter.replace(PlanningPoker.T_SHIRT_SIZE_LIST);
                }
                recyclerView.scrollToPosition(PlanningPoker.T_SHIRT_SIZE_POSITION);
                break;
        }
    }
}
