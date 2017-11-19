/*
 * Copyright 2016 Sascha Peilicke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package saschpe.poker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import saschpe.android.socialfragment.app.SocialFragment;
import saschpe.android.versioninfo.widget.VersionInfoDialogFragment;
import saschpe.poker.BuildConfig;
import saschpe.poker.R;

public final class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up nested scrollview
        NestedScrollView scrollView = findViewById(R.id.nested_scroll);
        scrollView.setFillViewport(true);

        // Set up view pager
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(this, getSupportFragmentManager()));

        // Set up  tab layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.open_source_licenses:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
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

    private static final class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final String[] pageTitles;
        private final String applicationName;

        MyFragmentPagerAdapter(final Context context, final FragmentManager fm) {
            super(fm);
            applicationName = context.getString(R.string.app_name);
            pageTitles = new String[] {
                    context.getString(R.string.social)
            };
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    return new SocialFragment.Builder()
                            // Mandatory
                            .setApplicationId(BuildConfig.APPLICATION_ID)
                            // Optional
                            .setApplicationName(applicationName)
                            .setContactEmailAddress("sascha+gp@peilicke.de")
                            .setGithubProject("saschpe/PlanningPoker")
                            .setTwitterProfile("saschpe")
                            // Visual customization
                            .setHeaderTextColor(R.color.accent)
                            .setIconTint(android.R.color.white)
                            .build();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
