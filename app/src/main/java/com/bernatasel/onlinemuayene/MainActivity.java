package com.bernatasel.onlinemuayene;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.FragmentDashboard;
import com.bernatasel.onlinemuayene.fragment.FragmentTest;
import com.bernatasel.onlinemuayene.fragment.admin.FragmentDashboardAdmin;
import com.bernatasel.onlinemuayene.fragment.doctor.FragmentDoctorDashboard;
import com.bernatasel.onlinemuayene.fragment.doctor.FragmentDoctorProfile;
import com.bernatasel.onlinemuayene.fragment.general.FragmentAboutUs;
import com.bernatasel.onlinemuayene.fragment.general.FragmentLogin;
import com.bernatasel.onlinemuayene.fragment.general.FragmentMakeSuggestion;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientListMessages;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientProfile;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.MyFCM;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    private MyFCM myFCM;
    public MyFM<FragmentBase> myFM;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle abdt;

    private MenuItem miProfile, miQuit;

    private ImageView ivMenuAvatar;
    private TextView tvHeaderTitle, tvMenuUsername, tvMenuEmail;
    private View vLoading;

    private FragmentBase fragmentDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApp.getInstance().ma = this;

        myFCM = new MyFCM(this, new MyFCM.IMyFCM() {
            @Override
            public void onMessageReceived(@NonNull Bundle bundle) {
                String myAction = bundle.getString("my_action");
                if (myAction != null) {
                    if (myAction.equals("show_message")) {
                        String myUserType = bundle.getString("user_type");
                        String myTitle = bundle.getString("my_title");
                        String myBody = bundle.getString("my_body");

                        if (getApp().isLoggedIn()) {
                            FSUser loggedInPerson = getApp().getLoggedInPerson();

                            if (loggedInPerson.getType().equals(myUserType)) {
                                UtilsAndroid.showAlertDialog(MainActivity.this,
                                        myTitle, myBody,
                                        false,
                                        R.string.tamam, null, null,
                                        (dialog, which) -> dialog.dismiss(), null, null);
                            }
                        }
                    }
                }
            }

            @Override
            public void onToken(String token) {
                System.out.println("token: " + token);
                FSOps.getInstance().updateCurrentUserFCMToken(token);
            }
        });

        myFM = new MyFM<>(getSupportFragmentManager(),
                findViewById(R.id.rl_click_prevent),
                new ArrayList<>(Collections.singletonList(R.id.fragment_container)));

        drawerLayout = findViewById(R.id.dl);
        NavigationView navigationView = findViewById(R.id.navigation);
        abdt = new ActionBarDrawerToggle(this, drawerLayout, R.string.yes, R.string.no);
        vLoading = findViewById(R.id.rl_loading);

        MenuItem miTest = navigationView.getMenu().findItem(R.id.btn_menu_test);
        miProfile = navigationView.getMenu().findItem(R.id.btn_menu_profile);
        miQuit = navigationView.getMenu().findItem(R.id.btn_menu_out);
        ivMenuAvatar = navigationView.getHeaderView(0).findViewById(R.id.iv_menu_avatar);
        tvMenuUsername = navigationView.getHeaderView(0).findViewById(R.id.tv_menu_username);
        tvMenuEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_menu_email);

        miTest.setVisible(Constants.IS_DEBUG);

        myFM.getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (myFM.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                abdt.setDrawerIndicatorEnabled(true);
            } else {
                abdt.setDrawerIndicatorEnabled(false);
            }
            try {
                UtilsAndroid.hideKeyboard(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.titlebar);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvHeaderTitle = actionBar.getCustomView().findViewById(R.id.tv_header_title);

        drawerLayout.addDrawerListener(abdt);
        abdt.setDrawerIndicatorEnabled(true);
        abdt.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.btn_menu_test:
                    addFragmentSafe(FragmentTest.newInstance(), MyFM.ANIM.RIGHT);
                    break;
                case R.id.btn_menu_profile:
                    myFM.clearAll();
                    if (getApp().isLoggedIn()) {
                        FSUser loggedInPerson = getApp().getLoggedInPerson();
                        if (loggedInPerson.isDoctor()) {
                            addFragmentSafe(FragmentDoctorProfile.newInstance(), MyFM.ANIM.RIGHT);
                        } else if (loggedInPerson.isPatient()) {
                            addFragmentSafe(FragmentPatientProfile.newInstance(), MyFM.ANIM.RIGHT);
                        }
                    } else {
                        addFragmentSafe(FragmentLogin.newInstance(), MyFM.ANIM.RIGHT);
                    }
                    break;
                case R.id.btn_menu_about_us:
                    addFragmentSafe(FragmentAboutUs.newInstance(), MyFM.ANIM.RIGHT);
                    break;
                case R.id.btn_menu_suggestion:
                    myFM.clearAll();
                    addFragmentSafe(FragmentMakeSuggestion.newInstance(), MyFM.ANIM.RIGHT);
                    break;
                case R.id.btn_menu_out:
                    UtilsAndroid.showAlertDialog(this, "Çıkış yapmak üzeresiniz", null, true,
                            R.string.cikis_yap, null, R.string.iptal, (dialog, which) -> {
                                getApp().signOut();
                                getApp().showToast("Sağlıklı kalın!");
                                closeDrawerLayoutIfOpen();
                                updateDashBoard();
                            }, null, null);
                    break;
                default:
                    return true;
            }
            return true;
        });

        setHeaderTitle("Online Muayene");

        updateDashBoard();
        myFCM.requestInstanceId();

//        handler.postDelayed(() -> {
//        Intent i = new Intent(MainActivity.this, DoctorMainActivity.class);
//        startActivity(i);
//        finish();
//        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myFCM.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myFCM.onStop(this);
    }

    public void updateDashBoard() {
        if (fragmentDashboard != null) myFM.remove(fragmentDashboard);

        if (getApp().isLoggedIn()) {
            myFM.clearAll();

            FSUser loggedInPerson = getApp().getLoggedInPerson();
            if (loggedInPerson.isAdmin()) {
                fragmentDashboard = FragmentDashboardAdmin.newInstance();
            } else if (loggedInPerson.isDoctor()) {
                fragmentDashboard = FragmentDoctorDashboard.newInstance();
            } else if (loggedInPerson.isPatient()) {
                fragmentDashboard = FragmentPatientListMessages.newInstance();
            }

            myFM.addFragment(fragmentDashboard, null, false);
            onDashBoard();

            if (loggedInPerson.getProfilePhoto() != null && !loggedInPerson.getProfilePhoto().isEmpty() && !loggedInPerson.getProfilePhoto().equals("default"))
                ivMenuAvatar.setImageBitmap(UtilsAndroid.base64ToBitmap(loggedInPerson.getProfilePhoto()));

            tvMenuUsername.setText("Merhaba " + loggedInPerson.getName() + "!"
                    + (!loggedInPerson.isPatient() ? " (" + Constants.USER_TYPE.getByName(loggedInPerson.getType()).getDescription() + ")" : ""));
            tvMenuEmail.setText(loggedInPerson.getEmail());
            miQuit.setVisible(true);

            myFCM.requestInstanceId();
        } else {
            fragmentDashboard = FragmentDashboard.newInstance();
            myFM.addFragment(fragmentDashboard, null, false);
            miQuit.setVisible(false);

            tvMenuUsername.setText("(Ziyaretçi)");
            tvMenuEmail.setText(null);
            ivMenuAvatar.setImageResource(R.drawable.profile);
        }
        miProfile.setVisible(true);
        if (getApp().isLoggedIn()) miProfile.setVisible(!getApp().getLoggedInPerson().isAdmin());
    }

    protected MyApp getApp() {
        return MyApp.getInstance();
    }

    public void addFragmentSafe(FragmentBase fragmentBase, MyFM.ANIM anim) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        myFM.addFragment(fragmentBase, anim, true);
    }

    public void setHeaderTitle(String title) {
        tvHeaderTitle.setText(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FragmentBase fragmentBase = myFM.getCurrentFragment();
        if (fragmentBase != null)
            fragmentBase.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//Home ise yada Back ise
            if (!abdt.onOptionsItemSelected(item)) onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDashBoard() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        setTitle(null);
    }

    private boolean closeDrawerLayoutIfOpen() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    public void showHideLoading(boolean isShow) {
        vLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public boolean isLoading() {
        return vLoading.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBackPressed() {
        if (closeDrawerLayoutIfOpen()) return;
        if (myFM.getBackStackEntryCount() > 0) {
            if (myFM.getBackStackEntryCount() == 1 && myFM.onBackPressed()) {//MainMenu
                onDashBoard();
            } else {
                myFM.onBackPressed();
            }
            setHeaderTitle(getString(R.string.app_name));
            fragmentDashboard.updateContent();
            return;
        }

        UtilsAndroid.showAlertDialog(MainActivity.this,
                null, "Çıkmak istediğinize emin misiniz?",
                true,
                R.string.evet, null, R.string.hayir,
                (dialog, which) -> {
                    finish();
                }, null, null);
    }
}
