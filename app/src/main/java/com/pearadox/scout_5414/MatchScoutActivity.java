package com.pearadox.scout_5414;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;


public class MatchScoutActivity extends AppCompatActivity {

    String TAG = "MatchScout_Activity";      // This CLASS name
    boolean onStart = false;
    /* Header Sect. */  TextView txt_EventName, txt_dev, txt_stud, txt_Match, txt_MyTeam, txt_TeamName, txt_NextMatch;
                        EditText editTxt_Team, editTxt_Match;
    /* Pre-Match */     RadioGroup radgrp_startPiece; RadioButton radio_startNone, radio_startHatch, radio_startCargo, radio_Pick;
                        Spinner spinner_startPos;
    /* After Start */   CheckBox checkbox_leftHAB, checkbox_noSS, checkbox_leftHAB2;
    /* L Rocket */      CheckBox chk_LeftRocket_LPan1,chk_LeftRocket_LPan2,chk_LeftRocket_LPan3, chk_LeftRocket_LCarg1,chk_LeftRocket_LCarg2,chk_LeftRocket_LCarg3;
                        CheckBox chk_LeftRocket_RPan1,chk_LeftRocket_RPan2,chk_LeftRocket_RPan3, chk_LeftRocket_RCarg1,chk_LeftRocket_RCarg2,chk_LeftRocket_RCarg3;
    /* CargoShip */     CheckBox chk_CargoLPan1,chk_CargoLPan2,chk_CargoLPan3, chk_CargoLCarg1,chk_CargoLCarg2,chk_CargoLCarg3;
                        CheckBox chk_CargoRPan1,chk_CargoRPan2,chk_CargoRPan3, chk_CargoRCarg1,chk_CargoRCarg2,chk_CargoRCarg3;
                        CheckBox chk_CargoEndLPanel,chk_CargoEndRPanel,chk_CargoEndLCargo,chk_CargoEndRCargo;
    /* R Rocket */      CheckBox chk_RghtRocket_LPan1,chk_RghtRocket_LPan2,chk_RghtRocket_LPan3, chk_RghtRocket_LCarg1,chk_RghtRocket_LCarg2,chk_RghtRocket_LCarg3;
                        CheckBox chk_RghtRocket_RPan1,chk_RghtRocket_RPan2,chk_RghtRocket_RPan3, chk_RghtRocket_RCarg1,chk_RghtRocket_RCarg2,chk_RghtRocket_RCarg3;
    /* 2nd & 3rd */     RadioGroup radgrp_secondPiece; RadioButton radio_none2, radio_hatch2, radio_cargo2, radio_2nd;
                        RadioGroup radgrp_secondPieceLocation; RadioButton radio_playerStation2, radio_corral2, radio_floor2, radio_2ndLoc;
                        RadioGroup radgrp_thirdPiece; RadioButton radio_none3, radio_hatch3, radio_cargo3, radio_3rd;
                        RadioGroup radgrp_thirdPieceLocation; RadioButton radio_playerStation3, radio_corral3, radio_floor3, radio_3rdLoc;
    /* Last Sect. */    EditText editText_autoComment;
                        Button btn_DropPlus, btn_DropMinus;  TextView  txt_Num_Dropped;
    protected Vibrator vibrate;
    long[] once = { 0, 100 };
    long[] twice = { 0, 100, 400, 100 };
    long[] thrice = { 0, 100, 400, 100, 400, 100 };
    public static String device = " ";
    private Button button_GoToTeleopActivity, button_GoToArenaLayoutActivity, button_dropMinus, button_dropPlus;
    String team_num, team_name, team_loc;
    p_Firebase.teamsObj team_inst = new p_Firebase.teamsObj();
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfTeam_DBReference;
    private DatabaseReference pfMatch_DBReference;
    private DatabaseReference pfDevice_DBReference;
    private DatabaseReference pfCur_Match_DBReference;
    String key = null;      // key for Devices Firebase
    ArrayAdapter<String> adapter_autostartpos;

    // ===================  Autonomous Elements for Match Scout Data object ===================
    // Declare & initialize
    public String matchID             = "T00";  // Type + #
    public String tn                  = "";     // Team #
    public boolean carry_none         = false;  // Do they carry nothing?
    public boolean carry_cargo        = false;  // Do they carry cargo
    public boolean carry_panel        = false;  // Do they carry panel
    public String  startPos           = " ";    // Start Position
    // ---- AFTER Start ----
    public boolean auto               = false;  // Do they have Autonomous mode?
    public boolean leftHAB            = false;  // Did they leave HAB
    public boolean leftHAB2           = false;  // Did they start from Hab level 2

    public boolean LeftRocket_LPan1   = false;  // L-Rocket L-Panel#1
    public boolean LeftRocket_LPan2   = false;  // L-Rocket L-Panel#2
    public boolean LeftRocket_LPan3   = false;  // L-Rocket L-Panel#3
    public boolean LeftRocket_RPan1   = false;  // L-Rocket R-Panel#1
    public boolean LeftRocket_RPan2   = false;  // L-Rocket R-Panel#2
    public boolean LeftRocket_RPan3   = false;  // L-Rocket R-Panel#3
    public boolean LeftRocket_LCarg1  = false; // L-Rocket L-Cargo#1
    public boolean LeftRocket_LCarg2  = false; // L-Rocket L-Cargo#2
    public boolean LeftRocket_LCarg3  = false; // L-Rocket L-Cargo#3
    public boolean LeftRocket_RCarg1  = false; // L-Rocket R-Cargo#1
    public boolean LeftRocket_RCarg2  = false; // L-Rocket R-Cargo#2
    public boolean LeftRocket_RCarg3  = false; // L-Rocket R-Cargo#3

    public boolean CargoLPan1         = false; // Cargo L-Panel#1
    public boolean CargoLPan2         = false; // Cargo L-Panel#2
    public boolean CargoLPan3         = false; // Cargo L-Panel#3
    public boolean CargoRPan1         = false; // Cargo R-Panel#1
    public boolean CargoRPan2         = false; // Cargo R-Panel#2
    public boolean CargoRPan3         = false; // Cargo R-Panel#3
    public boolean CargoLCarg1        = false; // Cargo L-Cargo#1
    public boolean CargoLCarg2        = false; // Cargo L-Cargo#2
    public boolean CargoLCarg3        = false; // Cargo L-Cargo#3
    public boolean CargoRCarg1        = false; // Cargo R-Cargo#1
    public boolean CargoRCarg2        = false; // Cargo R-Cargo#2
    public boolean CargoRCarg3        = false; // Cargo R-Cargo#3
    public boolean CargoEndLPanel     = false; // Cargo End L-Panel#1
    public boolean CargoEndLCargo     = false; // Cargo End L-Cargo#1
    public boolean CargoEndRPanel     = false; // Cargo End R-Panel#1
    public boolean CargoEndRCargo     = false; // Cargo End R-Cargo#1

    public boolean RghtRocket_LPan1   = false;  // R-Rocket L-Panel#1
    public boolean RghtRocket_LPan2   = false;  // R-Rocket L-Panel#2
    public boolean RghtRocket_LPan3   = false;  // R-Rocket L-Panel#3
    public boolean RghtRocket_RPan1   = false;  // R-Rocket R-Panel#1
    public boolean RghtRocket_RPan2   = false;  // R-Rocket R-Panel#2
    public boolean RghtRocket_RPan3   = false;  // R-Rocket R-Panel#3
    public boolean RghtRocket_LCarg1  = false;  // R-Rocket L-Cargo#1
    public boolean RghtRocket_LCarg2  = false;  // R-Rocket L-Cargo#2
    public boolean RghtRocket_LCarg3  = false;  // R-Rocket L-Cargo#3
    public boolean RghtRocket_RCarg1  = false;  // R-Rocket R-Cargo#1
    public boolean RghtRocket_RCarg2  = false;  // R-Rocket R-Cargo#2
    public boolean RghtRocket_RCarg3  = false;  // R-Rocket R-Cargo#3

    public boolean PU2ndPanel         = false;  // Second game piece - Panel
    public boolean PU2ndCargo         = false;  // Second game piece - Cargo
    public boolean PU2ndPlSta         = false;  // 2nd from Player Station
    public boolean PU2ndCorral        = false;  // 2nd from Corral
    public boolean PU2ndFloor         = false;  // 2nd from Floor
    public boolean PU3rdPanel         = false;  // Second game piece - Panel
    public boolean PU3rdCargo         = false;  // Second game piece - Cargo
    public boolean PU3rdPlSta         = false;  // 3rd from Player Station
    public boolean PU3rdCorral        = false;  // 3rd from Corral
    public boolean PU3rdFloor         = false;  // 3rd from Floor
    public int num_Dropped            = 0;      // How many Panels dropped?

    /* */
    public String autoComment = " ";        // Comment
    public static String studID = " ";

// ===========================================================================



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "<< Match Scout >>");
        onStart = false;
        setContentView(R.layout.activity_match_scout);
        Bundle bundle = this.getIntent().getExtras();
        String device = bundle.getString("dev");
        studID = bundle.getString("stud");
        Log.w(TAG, device + " " + studID);      // ** DEBUG **
        String ps = device.substring(device.length() - 1);
        int p = Integer.valueOf(ps);
        Pearadox.Match_Data.setPre_PlayerSta(p);
//
        tn = bundle.getString("tnum");
        Pearadox.MatchData_Saved = false;    // Set flag to show need to saved
        txt_EventName = (TextView) findViewById(R.id.txt_EventName);
        txt_EventName.setText(Pearadox.FRC_EventName);          // Event Name

        pfDatabase = FirebaseDatabase.getInstance();
        pfTeam_DBReference = pfDatabase.getReference("teams");              // Tteam data from Firebase D/B
//        pfStudent_DBReference = pfDatabase.getReference("students");        // List of Students
        pfMatch_DBReference = pfDatabase.getReference("matches");           // List of Matches
        pfCur_Match_DBReference = pfDatabase.getReference("current-match"); // _THE_ current Match
        pfDevice_DBReference = pfDatabase.getReference("devices");          // List of Devices
        updateDev("Auto");      // Update 'Phase' for stoplight indicator in ScoutMaster

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        int pct = (int) (batteryPct * 100);
        String batpct = String.valueOf(pct);
        Log.w(TAG, "Battery=" + batteryPct + "  " + batpct);      // ** DEBUG **
        switch (Pearadox.FRC514_Device) {
            case ("Red-1"):             //#Red or Blue Scout
                key = "1";
                break;
            case ("Red-2"):             //#
                key = "2";
                break;
            case ("Red-3"):             //#
                key = "3";
                break;
            case ("Blue-1"):            //#
                key = "4";
                break;
            case ("Blue-2"):            //#
                key = "5";
                break;
            case ("Blue-3"):            //#####
                key = "6";
                break;
            default:                //
                Log.d(TAG, "DEV = NULL");
        }
        Log.w(TAG, "batt_stat=" + key + "  " + batpct);      // ** DEBUG **
        if (Pearadox.is_Network) {
            pfDevice_DBReference.child(key).child("batt_stat").setValue(batpct);
        }

        txt_dev = (TextView) findViewById(R.id.txt_Dev);
        txt_stud = (TextView) findViewById(R.id.txt_stud);
        txt_Match = (TextView) findViewById(R.id.txt_Match);
        txt_MyTeam = (TextView) findViewById(R.id.txt_MyTeam);
        txt_TeamName = (TextView) findViewById(R.id.txt_TeamName);
        editTxt_Match = (EditText) findViewById(R.id.editTxt_Match);
        editTxt_Team = (EditText) findViewById(R.id.editTxt_Team);
        ImageView imgScoutLogo = (ImageView) findViewById(R.id.imageView_MS);
        txt_dev.setText(device);
        txt_stud.setText(studID);
        txt_Match.setText("");
        if (Pearadox.is_Network && Pearadox.numTeams > 0) {      // is Internet available and Teams there?
            txt_MyTeam.setText("");
            editTxt_Match.setVisibility(View.INVISIBLE);
            editTxt_Match.setEnabled(false);
            editTxt_Team.setVisibility(View.INVISIBLE);
            editTxt_Team.setEnabled(false);
        } else {
            Log.d(TAG, "No Internet for Match & Team  " + Pearadox.numTeams);
            editTxt_Match.setVisibility(View.VISIBLE);
            editTxt_Match.setEnabled(true);
            editTxt_Match.requestFocus();        // Don't let EditText mess up layout!!
            editTxt_Match.setFocusable(true);
            editTxt_Match.setFocusableInTouchMode(true);
            editTxt_Match.requestFocus();        // Don't let EditText mess up layout!!
            txt_Match.setText("Q");         // Default to qualifying
            editTxt_Team.setVisibility(View.VISIBLE);
            editTxt_Team.setEnabled(true);
            editTxt_Team.setFocusable(true);
            editTxt_Team.setFocusableInTouchMode(true);
            txt_MyTeam.setVisibility(View.GONE);
            txt_TeamName.setVisibility(View.GONE);


// ******************
            editTxt_Match.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d(TAG, " editTxt_Match listener; Match = " + editTxt_Match.getText());
                    if (editTxt_Match.getText().length() < 3) {
                        vibrate.vibrate(twice, -1);
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                        Toast.makeText(getBaseContext(), "*** Match number must be at least 3 characters  *** ", Toast.LENGTH_LONG).show();
                    } else {
                        matchID = "Q" + (String.valueOf(editTxt_Match.getText()));
                        editTxt_Team.requestFocus();    // Go to Team# next
                    }
                    Log.e(TAG, " Match ID = " + matchID);
                    editTxt_Match.setNextFocusDownId(R.id.editTxt_Team);
                    editTxt_Team.requestFocus();    // Go to Team# next
                    return true;
                }
                return false;
            }
            });

            editTxt_Team.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Log.d(TAG, " editTxt_Team listener; Team = " + editTxt_Team.getText());
                        if (editTxt_Team.getText().length() < 2 || editTxt_Team.getText().length() > 4) {
                            vibrate.vibrate(twice, -1);
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                            Toast.makeText(getBaseContext(), "*** Team number must be at least 2 characters and no more than 4  *** ", Toast.LENGTH_LONG).show();
                        } else {
                            tn = (String.valueOf(editTxt_Team.getText()));
                        }
                        Log.e(TAG, " Team # = " + tn);
                        return true;
                    }
                    return false;
                }
            });
        } //End if

        txt_TeamName.setText("");
        String devcol = device.substring(0, 3);
        Log.d(TAG, "color=" + devcol);
        if (devcol.equals("Red")) {
            imgScoutLogo.setImageDrawable(getResources().getDrawable(R.drawable.red_scout));
        } else {
            imgScoutLogo.setImageDrawable(getResources().getDrawable(R.drawable.blue_scout));
        }

        checkbox_noSS           = (CheckBox) findViewById(R.id.checkbox_noSS);
        checkbox_leftHAB        = (CheckBox) findViewById(R.id.checkbox_leftHAB);
        checkbox_leftHAB2       = (CheckBox) findViewById(R.id.checkbox_leftHAB2);
        editText_autoComment    = (EditText) findViewById(R.id.editText_autoComment);
        btn_DropPlus            = (Button) findViewById(R.id.btn_DropPlus);
        btn_DropMinus           = (Button) findViewById(R.id.btn_DropMinus);
        txt_Num_Dropped         = (TextView) findViewById(R.id.txt_Num_Dropped);
        button_GoToTeleopActivity = (Button) findViewById(R.id.button_GoToTeleopActivity);
        button_GoToArenaLayoutActivity = (Button) findViewById(R.id.button_GoToArenaLayoutActivity);
        final Spinner spinner_startPos = (Spinner) findViewById(R.id.spinner_startPos);
        String[] autostartPos = getResources().getStringArray(R.array.auto_start_array);
        adapter_autostartpos = new ArrayAdapter<String>(this, R.layout.dev_list_layout, autostartPos);
        adapter_autostartpos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_startPos.setAdapter(adapter_autostartpos);
        spinner_startPos.setSelection(0, false);
        spinner_startPos.setOnItemSelectedListener(new startPosOnClickListener());

        // Left Rocket
        chk_LeftRocket_LPan1 = (CheckBox) findViewById(R.id.chk_LeftRocket_LPan1);
        chk_LeftRocket_LPan2 = (CheckBox) findViewById(R.id.chk_LeftRocket_LPan2);
        chk_LeftRocket_LPan3 = (CheckBox) findViewById(R.id.chk_LeftRocket_LPan3);
        chk_LeftRocket_RPan1 = (CheckBox) findViewById(R.id.chk_LeftRocket_RPan1);
        chk_LeftRocket_RPan2 = (CheckBox) findViewById(R.id.chk_LeftRocket_RPan2);
        chk_LeftRocket_RPan3 = (CheckBox) findViewById(R.id.chk_LeftRocket_RPan3);
        chk_LeftRocket_LCarg1 = (CheckBox) findViewById(R.id.chk_LeftRocket_LCarg1);
        chk_LeftRocket_LCarg2 = (CheckBox) findViewById(R.id.chk_LeftRocket_LCarg2);
        chk_LeftRocket_LCarg3 = (CheckBox) findViewById(R.id.chk_LeftRocket_LCarg3);
        chk_LeftRocket_RCarg1 = (CheckBox) findViewById(R.id.chk_LeftRocket_RCarg1);
        chk_LeftRocket_RCarg2 = (CheckBox) findViewById(R.id.chk_LeftRocket_RCarg2);
        chk_LeftRocket_RCarg3 = (CheckBox) findViewById(R.id.chk_LeftRocket_RCarg3);
        // Cargo Ship
        chk_CargoLPan1 = (CheckBox) findViewById(R.id.chk_CargoLPan1);
        chk_CargoLPan2 = (CheckBox) findViewById(R.id.chk_CargoLPan2);
        chk_CargoLPan3 = (CheckBox) findViewById(R.id.chk_CargoLPan3);
        chk_CargoRPan1 = (CheckBox) findViewById(R.id.chk_CargoRPan1);
        chk_CargoRPan2 = (CheckBox) findViewById(R.id.chk_CargoRPan2);
        chk_CargoRPan3 = (CheckBox) findViewById(R.id.chk_CargoRPan3);
        chk_CargoLCarg1 = (CheckBox) findViewById(R.id.chk_CargoLCarg1);
        chk_CargoLCarg2 = (CheckBox) findViewById(R.id.chk_CargoLCarg2);
        chk_CargoLCarg3 = (CheckBox) findViewById(R.id.chk_CargoLCarg3);
        chk_CargoRCarg1 = (CheckBox) findViewById(R.id.chk_CargoRCarg1);
        chk_CargoRCarg2 = (CheckBox) findViewById(R.id.chk_CargoRCarg2);
        chk_CargoRCarg3 = (CheckBox) findViewById(R.id.chk_CargoRCarg3);
        chk_CargoEndLPanel = (CheckBox) findViewById(R.id.chk_CargoEndLPanel);
        chk_CargoEndRPanel = (CheckBox) findViewById(R.id.chk_CargoEndRPanel);
        chk_CargoEndLCargo = (CheckBox) findViewById(R.id.chk_CargoEndLCargo);
        chk_CargoEndRCargo = (CheckBox) findViewById(R.id.chk_CargoEndRCargo);
        // Right Rocket
        chk_RghtRocket_LPan1 = (CheckBox) findViewById(R.id.chk_RghtRocket_LPan1);
        chk_RghtRocket_LPan2 = (CheckBox) findViewById(R.id.chk_RghtRocket_LPan2);
        chk_RghtRocket_LPan3 = (CheckBox) findViewById(R.id.chk_RghtRocket_LPan3);
        chk_RghtRocket_RPan1 = (CheckBox) findViewById(R.id.chk_RghtRocket_RPan1);
        chk_RghtRocket_RPan2 = (CheckBox) findViewById(R.id.chk_RghtRocket_RPan2);
        chk_RghtRocket_RPan3 = (CheckBox) findViewById(R.id.chk_RghtRocket_RPan3);
        chk_RghtRocket_LCarg1 = (CheckBox) findViewById(R.id.chk_RghtRocket_LCarg1);
        chk_RghtRocket_LCarg2 = (CheckBox) findViewById(R.id.chk_RghtRocket_LCarg2);
        chk_RghtRocket_LCarg3 = (CheckBox) findViewById(R.id.chk_RghtRocket_LCarg3);
        chk_RghtRocket_RCarg1 = (CheckBox) findViewById(R.id.chk_RghtRocket_RCarg1);
        chk_RghtRocket_RCarg2 = (CheckBox) findViewById(R.id.chk_RghtRocket_RCarg2);
        chk_RghtRocket_RCarg3 = (CheckBox) findViewById(R.id.chk_RghtRocket_RCarg3);
        final RadioGroup radgrp_secondPieceLocation = (RadioGroup)findViewById(R.id.radgrp_secondPieceLocation);
        final RadioGroup radgrp_thirdPieceLocation  = (RadioGroup)findViewById(R.id.radgrp_thirdPieceLocation);

        // ToDo - all references to new Widgets
//        radgrp_secondPieceLocation.setEnabled(false);
        for(int i = 0; i < radgrp_secondPieceLocation.getChildCount(); i++){        // Can't pick until piece selected
            ((RadioButton)radgrp_secondPieceLocation.getChildAt(i)).setEnabled(false);
        }
//        radgrp_thirdPieceLocation.setEnabled(false);
        for(int i = 0; i < radgrp_thirdPieceLocation.getChildCount(); i++){        // Can't pick until piece selected
            ((RadioButton)radgrp_thirdPieceLocation.getChildAt(i)).setEnabled(false);
        }

// Start Listeners
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑  Process _ALL_ the CheckBoxes  ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LPan1.isChecked()) {     //checked
                    LeftRocket_LPan1 = true;
                }
                else {          //not checked
                    LeftRocket_LPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LPan2.isChecked()) {     //checked
                    LeftRocket_LPan2 = true;
                }
                else {          //not checked
                    LeftRocket_LPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LPan3.isChecked()) {     //checked
                    LeftRocket_LPan3 = true;
                }
                else {          //not checked
                    LeftRocket_LPan3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RPan1.isChecked()) {     //checked
                    LeftRocket_RPan1 = true;
                }
                else {          //not checked
                    LeftRocket_RPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RPan2.isChecked()) {     //checked
                    LeftRocket_RPan2 = true;
                }
                else {          //not checked
                    LeftRocket_RPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RPan3.isChecked()) {     //checked
                    LeftRocket_RPan3 = true;
                }
                else {          //not checked
                    LeftRocket_RPan3 = false;
                }
            }
        });

        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LCarg1.isChecked()) {     //checked
                    LeftRocket_LCarg1 = true;
                }
                else {          //not checked
                    LeftRocket_LCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LCarg2.isChecked()) {     //checked
                    LeftRocket_LCarg2 = true;
                }
                else {          //not checked
                    LeftRocket_LCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_LCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_LCarg3.isChecked()) {     //checked
                    LeftRocket_LCarg3 = true;
                }
                else {          //not checked
                    LeftRocket_LCarg3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RCarg1.isChecked()) {     //checked
                    LeftRocket_RCarg1 = true;
                }
                else {          //not checked
                    LeftRocket_RCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RCarg2.isChecked()) {     //checked
                    LeftRocket_RCarg2 = true;
                }
                else {          //not checked
                    LeftRocket_RCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_LeftRocket_RCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_LeftRocket_RCarg3.isChecked()) {     //checked
                    LeftRocket_RCarg3 = true;
                }
                else {          //not checked
                    LeftRocket_RCarg3 = false;
                }
            }
        });

        // RIGHT Rocket
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LPan1.isChecked()) {     //checked
                    RghtRocket_LPan1 = true;
                }
                else {          //not checked
                    RghtRocket_LPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LPan2.isChecked()) {     //checked
                    RghtRocket_LPan2 = true;
                }
                else {          //not checked
                    RghtRocket_LPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LPan3.isChecked()) {     //checked
                    RghtRocket_LPan3 = true;
                }
                else {          //not checked
                    RghtRocket_LPan3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RPan1.isChecked()) {     //checked
                    RghtRocket_RPan1 = true;
                }
                else {          //not checked
                    RghtRocket_RPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RPan2.isChecked()) {     //checked
                    RghtRocket_RPan2 = true;
                }
                else {          //not checked
                    RghtRocket_RPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RPan3.isChecked()) {     //checked
                    RghtRocket_RPan3 = true;
                }
                else {          //not checked
                    RghtRocket_RPan3 = false;
                }
            }
        });

        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LCarg1.isChecked()) {     //checked
                    RghtRocket_LCarg1 = true;
                }
                else {          //not checked
                    RghtRocket_LCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LCarg2.isChecked()) {     //checked
                    RghtRocket_LCarg2 = true;
                }
                else {          //not checked
                    RghtRocket_LCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_LCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_LCarg3.isChecked()) {     //checked
                    RghtRocket_LCarg3 = true;
                }
                else {          //not checked
                    RghtRocket_LCarg3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RCarg1.isChecked()) {     //checked
                    RghtRocket_RCarg1 = true;
                }
                else {          //not checked
                    RghtRocket_RCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RCarg2.isChecked()) {     //checked
                    RghtRocket_RCarg2 = true;
                }
                else {          //not checked
                    RghtRocket_RCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_RghtRocket_RCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_RghtRocket_RCarg3.isChecked()) {     //checked
                    RghtRocket_RCarg3 = true;
                }
                else {          //not checked
                    RghtRocket_RCarg3 = false;
                }
            }
        });

        // Cargo Ship
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLPan1.isChecked()) {     //checked
                    CargoLPan1 = true;
                }
                else {          //not checked
                    CargoLPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLPan2.isChecked()) {     //checked
                    CargoLPan2 = true;
                }
                else {          //not checked
                    CargoLPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLPan3.isChecked()) {     //checked
                    CargoLPan3 = true;
                }
                else {          //not checked
                    CargoLPan3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRPan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRPan1.isChecked()) {     //checked
                    CargoRPan1 = true;
                }
                else {          //not checked
                    CargoRPan1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRPan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRPan2.isChecked()) {     //checked
                    CargoRPan2 = true;
                }
                else {          //not checked
                    CargoRPan2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRPan3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRPan3.isChecked()) {     //checked
                    CargoRPan3 = true;
                }
                else {          //not checked
                    CargoRPan3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLCarg1.isChecked()) {     //checked
                    CargoLCarg1 = true;
                }
                else {          //not checked
                    CargoLCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLCarg2.isChecked()) {     //checked
                    CargoLCarg2 = true;
                }
                else {          //not checked
                    CargoLCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoLCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoLCarg3.isChecked()) {     //checked
                    CargoLCarg3 = true;
                }
                else {          //not checked
                    CargoLCarg3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRCarg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRCarg1.isChecked()) {     //checked
                    CargoRCarg1 = true;
                }
                else {          //not checked
                    CargoRCarg1 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRCarg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRCarg2.isChecked()) {     //checked
                    CargoRCarg2 = true;
                }
                else {          //not checked
                    CargoRCarg2 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoRCarg3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoRCarg3.isChecked()) {     //checked
                    CargoRCarg3 = true;
                }
                else {          //not checked
                    CargoRCarg3 = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoEndLPanel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoEndLPanel.isChecked()) {     //checked
                    CargoEndLPanel = true;
//                    chk_CargoEndLCargo.setChecked(true);    // Score the already set Cargo Ball
                }
                else {          //not checked
                    CargoEndLPanel = false;
//                    chk_CargoEndLCargo.setChecked(false);
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoEndRPanel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoEndRPanel.isChecked()) {     //checked
                    CargoEndRPanel = true;
//                    chk_CargoEndRCargo.setChecked(true);    // Score the already set Cargo Ball
                }
                else {          //not checked
                    CargoEndRPanel = false;
//                    chk_CargoEndRCargo.setChecked(false);
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoEndLCargo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoEndLCargo.isChecked()) {     //checked
                    CargoEndLCargo = true;
                }
                else {          //not checked
                    CargoEndLCargo = false;
                }
            }
        });
        // ☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑☑
        chk_CargoEndRCargo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chk_CargoEndRCargo.isChecked()) {     //checked
                    CargoEndRCargo = true;
                }
                else {          //not checked
                    CargoEndRCargo = false;
                }
            }
        });


// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        checkbox_noSS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             Log.w(TAG, "checkbox_noSS Listener");
                 if (buttonView.isChecked()) {
                     //checked
                     Log.w(TAG, "No SS is checked.");
                     auto = true;
                    // ToDo - turn ON/OFF correct widgets
                     checkbox_leftHAB.setChecked(false);
                     checkbox_leftHAB.setEnabled(false);
                     checkbox_leftHAB2.setChecked(false);
                     checkbox_leftHAB2.setEnabled(false);
                     editText_autoComment.setText("No Sandstorm activity - didn't move");
                     autoComment = "No Sandstorm activity - didn't move";
//                     chk_cubeSwitch.setEnabled(false);
//                     chk_attemptSwitch.setEnabled(false);
//                     chk_XoverSwitch.setEnabled(false);
//                     chk_WrongSwitch.setEnabled(false);       Remove when new ones added
//                     chk_ExtraSwitch.setEnabled(false);
//                     chk_cubeScale.setEnabled(false);
//                     chk_attemptScale.setEnabled(false);
//                     chk_XoverScale.setEnabled(false);
//                     chk_WrongScale.setEnabled(false);
//                     chk_ExtraScale.setEnabled(false);


                 } else {
                     //not checked
                     Log.w(TAG, "No SS is unchecked.");
                     auto = false;

                     checkbox_leftHAB.setEnabled(true);
                     checkbox_leftHAB2.setEnabled(true);
                     editText_autoComment.setText(" ");
                     autoComment = " ";

//                     chk_cubeSwitch.setEnabled(true);
//                     chk_attemptSwitch.setEnabled(true);
//                     chk_XoverSwitch.setEnabled(true);
//                     chk_WrongSwitch.setEnabled(true);
//                     chk_ExtraSwitch.setEnabled(true);        Remove when new ones added
//                     chk_cubeScale.setEnabled(true);
//                     chk_attemptScale.setEnabled(true);
//                     chk_XoverScale.setEnabled(true);
//                     chk_WrongScale.setEnabled(true);
//                     chk_ExtraScale.setEnabled(true);
                 }
             }
         }
        );

        checkbox_leftHAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.w(TAG, "checkbox_leftHAB Listener");
                if (buttonView.isChecked()) {
                    leftHAB = true;
                } else {
                    leftHAB = false;
                }
            }
        }
        );

        checkbox_leftHAB2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.w(TAG, "checkbox_leftHAB2 Listener");
            if (buttonView.isChecked()) {
                leftHAB2 = true;
                checkbox_leftHAB.setChecked(true);         // Force the other one
            } else {
                leftHAB2 = false;
            }
        }
    }
    );

        button_GoToTeleopActivity.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            Log.w(TAG, "Clicked 'NEXT/TeleOps' Button  match=" + matchID);
            if (matchID.length() < 2) {     // Between matches??
                Toast.makeText(getBaseContext(), "*** Match has _NOT_ started; wait until you have a Team #  *** ", Toast.LENGTH_LONG).show();

            } else {        // It's OK - Match has started

                    if ((auto==false) &&
                            ((carry_none==false) && (carry_cargo==false) && (carry_panel==false)) ||
                            (PU2ndPanel) && ((!PU2ndPlSta)&&(!PU2ndFloor)) ||
                            (PU3rdPanel) && ((!PU3rdPlSta)&&(!PU3rdFloor)) ||
                            (PU2ndCargo) && ((!PU2ndPlSta)&&(!PU2ndFloor)&&(!PU2ndCorral)) ||
                            (PU3rdCargo) && ((!PU3rdPlSta)&&(!PU3rdFloor)&&(!PU3rdCorral)) ||
                            (spinner_startPos.getSelectedItemPosition() == 0) ) {  //Required fields
                        // ToDo - check to see if ALL required fields entered (Start-pos, stop, gear, ....)

                        Toast.makeText(getBaseContext(), "\t*** Select _ALL_ required fields!  ***\n Starting Position, Gamepiece, 2nd & 3rd Location ", Toast.LENGTH_LONG).show();
                        if (spinner_startPos.getSelectedItemPosition() == 0) {
                            spinner_startPos.performClick();
                        }
                    } else {

                        if (tn != null) {
                            updateDev("Tele");      // Update 'Phase' for stoplight indicator in ScoutM aster
                            storeAutoData();        // Put all the Autonomous data collected in Match object

                            Intent smast_intent = new Intent(MatchScoutActivity.this, TeleopScoutActivity.class);
                            Bundle SMbundle = new Bundle();
                            SMbundle.putString("tnum", tn);
                            smast_intent.putExtras(SMbundle);
                            startActivity(smast_intent);
                        } else {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                            Toast.makeText(getBaseContext(), "*** Team # not entered  *** ", Toast.LENGTH_LONG).show();
                        }
                    }
            }
        }
    });

        button_GoToArenaLayoutActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked Sidebar");
                Intent smast_intent = new Intent(MatchScoutActivity.this, ArenaLayoutActivity.class);
                Bundle SMbundle = new Bundle();
                smast_intent.putExtras(SMbundle);
                startActivity(smast_intent);
        }
        });

        // *******************************************************************

        btn_DropPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                num_Dropped++;
                Log.w(TAG, "Dropped = " + Integer.toString(num_Dropped));      // ** DEBUG **
                txt_Num_Dropped.setText(Integer.toString(num_Dropped));
            }
        });
        btn_DropMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (num_Dropped >= 1) {     // Don't go below zero
                    num_Dropped--;
                }
                Log.w(TAG, "Dropped = " + Integer.toString(num_Dropped));      // ** DEBUG **
                txt_Num_Dropped.setText(Integer.toString(num_Dropped));
            }
        });


        // === End of OnCreate ===
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public void RadioClick_Piece(View view) {
        Log.w(TAG, "@@ RadioClick_Piece @@");
        radgrp_startPiece = (RadioGroup) findViewById(R.id.radgrp_startPiece);
        int selectedId = radgrp_startPiece.getCheckedRadioButtonId();
//        Log.w(TAG, "*** Selected=" + selectedId);
        radio_Pick = (RadioButton) findViewById(selectedId);
        String value = radio_Pick.getText().toString();
        if (value.equals("None")) {        // None
            Log.w(TAG, "None");
            carry_none = true;
            carry_panel = false;
            carry_cargo = false;
        }
        if (value.equals("Panel")) {        // Panel
            Log.w(TAG, "Panel");
            carry_panel = true;
            carry_cargo = false;
        }
        if (value.equals("Cargo")) {        // Panel
            Log.w(TAG, "Cargo");
            carry_cargo = true;
            carry_panel = false;
        }
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public void RadioClick_2nd(View view) {
        Log.w(TAG, "@@ RadioClick_2nd @@");
        radgrp_secondPiece = (RadioGroup) findViewById(R.id.radgrp_secondPiece);
        int selectedId = radgrp_secondPiece.getCheckedRadioButtonId();
//        Log.w(TAG, "*** Selected=" + selectedId);
        radio_2nd = (RadioButton) findViewById(selectedId);
        String value = radio_2nd.getText().toString();
        final RadioGroup radgrp_secondPieceLocation = (RadioGroup)findViewById(R.id.radgrp_secondPieceLocation);
        radio_playerStation2 = (RadioButton) findViewById(R.id.radio_playerStation2);
        radio_floor2 = (RadioButton) findViewById(R.id.radio_floor2);
        radio_corral2 = (RadioButton) findViewById(R.id.radio_corral2);
        if (value.equals("None")) {        // None
            Log.w(TAG, "2nd None");
            PU2ndPanel = false;
            PU2ndCargo = false;
            radio_playerStation2.setEnabled(false);
            radio_floor2.setEnabled(false);
            radio_corral2.setEnabled(false);
            radio_corral2.setVisibility(View.VISIBLE);
            for(int i = 0; i < radgrp_secondPieceLocation.getChildCount(); i++){        // turn them all OFF
                ((RadioButton)radgrp_secondPieceLocation.getChildAt(i)).setChecked(false);
                ((RadioButton)radgrp_secondPieceLocation.getChildAt(i)).setEnabled(false);
            }
        }
        if (value.equals("Panel")) {        // Panel
            Log.w(TAG, "2nd Panel");
            PU2ndPanel = true;
            PU2ndCargo = false;
            radio_playerStation2.setEnabled(true);
            radio_floor2.setEnabled(true);
            radio_corral2.setVisibility(View.INVISIBLE);
        }
        if (value.equals("Cargo")) {        // Panel
            Log.w(TAG, "2nd Cargo");
            PU2ndCargo = true;
            PU2ndPanel = false;
            for(int i = 0; i < radgrp_secondPieceLocation.getChildCount(); i++){        // turn them all ON
                ((RadioButton)radgrp_secondPieceLocation.getChildAt(i)).setEnabled(true);
            }
            radio_corral2.setVisibility(View.VISIBLE);
        }
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public void RadioClick_3rd(View view) {
        Log.w(TAG, "@@ RadioClick_3rd @@");
        radgrp_thirdPiece = (RadioGroup) findViewById(R.id.radgrp_thirdPiece);
        int selectedId = radgrp_thirdPiece.getCheckedRadioButtonId();
        radio_3rd = (RadioButton) findViewById(selectedId);
        String value = radio_3rd.getText().toString();
        final RadioGroup radgrp_thirdPieceLocation = (RadioGroup)findViewById(R.id.radgrp_thirdPieceLocation);
        radio_playerStation3 = (RadioButton) findViewById(R.id.radio_playerStation3);
        radio_floor3 = (RadioButton) findViewById(R.id.radio_floor3);
        radio_corral3 = (RadioButton) findViewById(R.id.radio_corral3);
        if (value.equals("None")) {        // None
            Log.w(TAG, "2nd None");
            PU3rdPanel = false;
            PU3rdCargo = false;
            radio_playerStation3.setEnabled(false);
            radio_floor3.setEnabled(false);
            radio_corral3.setEnabled(false);
            radio_corral3.setVisibility(View.VISIBLE);
            for(int i = 0; i < radgrp_thirdPieceLocation.getChildCount(); i++){        // turn them all OFF
                ((RadioButton)radgrp_thirdPieceLocation.getChildAt(i)).setChecked(false);
                ((RadioButton)radgrp_thirdPieceLocation.getChildAt(i)).setEnabled(false);
            }
        }
        if (value.equals("Panel")) {        // Panel
            Log.w(TAG, "3rd Panel");
            PU3rdPanel = true;
            PU3rdCargo = false;
            radio_playerStation3.setEnabled(true);
            radio_floor3.setEnabled(true);
            radio_corral3.setVisibility(View.INVISIBLE);
        }
        if (value.equals("Cargo")) {        // Panel
            Log.w(TAG, "3rd Cargo");
            PU3rdCargo = true;
            PU3rdPanel = false;
            for(int i = 0; i < radgrp_thirdPieceLocation.getChildCount(); i++){        // turn them all ON
                ((RadioButton)radgrp_thirdPieceLocation.getChildAt(i)).setEnabled(true);
            }
            radio_corral3.setVisibility(View.VISIBLE);
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public void RadioClick_2ndLoc(View view) {
        Log.w(TAG, "@@ RadioClick_2ndLoc @@");
        radgrp_secondPieceLocation = (RadioGroup) findViewById(R.id.radgrp_secondPieceLocation);
        int selectedId = radgrp_secondPieceLocation.getCheckedRadioButtonId();
        radio_2ndLoc = (RadioButton) findViewById(selectedId);
        String value = radio_2ndLoc.getText().toString();
        radio_playerStation2 = (RadioButton) findViewById(R.id.radio_playerStation2);
        radio_floor2 = (RadioButton) findViewById(R.id.radio_floor2);
        radio_corral2 = (RadioButton) findViewById(R.id.radio_corral2);
        if (value.equals("Loading Sta.")) {        // Loading Sta
            Log.w(TAG, "2nd Loading Sta.");
            PU2ndPlSta = true;
            PU2ndCorral = false;
            PU2ndFloor = false;
        }
        if (value.equals("Corral")) {        // Loading Sta
            Log.w(TAG, "2nd Corral");
            PU2ndCorral = true;
            PU2ndPlSta = false;
            PU2ndFloor = false;
        }
        if (value.equals("Floor")) {        // Loading Sta
            Log.w(TAG, "2nd Floor");
            PU2ndFloor = true;
            PU2ndCorral = false;
            PU2ndPlSta = false;
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public void RadioClick_3rdLoc(View view) {
        Log.w(TAG, "@@ RadioClick_3rdLoc @@");
        radgrp_thirdPieceLocation = (RadioGroup) findViewById(R.id.radgrp_thirdPieceLocation);
        int selectedId = radgrp_thirdPieceLocation.getCheckedRadioButtonId();
        radio_3rdLoc = (RadioButton) findViewById(selectedId);
        String value = radio_3rdLoc.getText().toString();
        radio_playerStation3 = (RadioButton) findViewById(R.id.radio_playerStation3);
        radio_floor3 = (RadioButton) findViewById(R.id.radio_floor3);
        radio_corral3 = (RadioButton) findViewById(R.id.radio_corral3);
        if (value.equals("Loading Sta.")) {        // Loading Sta
            Log.w(TAG, "3rd Loading Sta.");
            PU3rdPlSta = true;
            PU3rdCorral = false;
            PU3rdFloor = false;
        }
        if (value.equals("Corral")) {        // Loading Sta
            Log.w(TAG, "3rd Corral");
            PU3rdCorral = true;
            PU3rdPlSta = false;
            PU3rdFloor = false;
        }
        if (value.equals("Floor")) {        // Loading Sta
            Log.w(TAG, "3rd Floor");
            PU3rdFloor = true;
            PU3rdCorral = false;
            PU3rdPlSta = false;
        }
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void storeAutoData() {
        Log.i(TAG, ">>>>  storeAutoData  <<<< " + studID);
        Pearadox.Match_Data.setMatch(matchID);
        Pearadox.Match_Data.setTeam_num(tn);

//        Pearadox.Match_Data.setPre_PlayerSta(p);      // Set at start-up
        Pearadox.Match_Data.setPre_startPos(startPos);
        Pearadox.Match_Data.setSand_mode(auto);
        Pearadox.Match_Data.setPre_cargo(carry_cargo);
        Pearadox.Match_Data.setPre_panel(carry_panel);

        Pearadox.Match_Data.setSand_leftHAB(leftHAB);
        Pearadox.Match_Data.setSand_leftHAB2(leftHAB2);
        Pearadox.Match_Data.setSand_LeftRocket_LPan1(LeftRocket_LPan1);
        Pearadox.Match_Data.setSand_LeftRocket_LPan2(LeftRocket_LPan2);
        Pearadox.Match_Data.setSand_LeftRocket_LPan3(LeftRocket_LPan3);
        Pearadox.Match_Data.setSand_LeftRocket_RPan1(LeftRocket_RPan1);
        Pearadox.Match_Data.setSand_LeftRocket_RPan2(LeftRocket_RPan2);
        Pearadox.Match_Data.setSand_LeftRocket_RPan3(LeftRocket_RPan3);
        Pearadox.Match_Data.setSand_LeftRocket_LCarg1(LeftRocket_LCarg1);
        Pearadox.Match_Data.setSand_LeftRocket_LCarg2(LeftRocket_LCarg2);
        Pearadox.Match_Data.setSand_LeftRocket_LCarg3(LeftRocket_LCarg3);
        Pearadox.Match_Data.setSand_LeftRocket_RCarg1(LeftRocket_RCarg1);
        Pearadox.Match_Data.setSand_LeftRocket_RCarg2(LeftRocket_RCarg2);
        Pearadox.Match_Data.setSand_LeftRocket_RCarg3(LeftRocket_RCarg3);

        Pearadox.Match_Data.setSand_CargoLPan1(CargoLPan1);
        Pearadox.Match_Data.setSand_CargoLPan2(CargoLPan2);
        Pearadox.Match_Data.setSand_CargoLPan3(CargoLPan3);
        Pearadox.Match_Data.setSand_CargoRPan1(CargoRPan1);
        Pearadox.Match_Data.setSand_CargoRPan2(CargoRPan2);
        Pearadox.Match_Data.setSand_CargoRPan3(CargoRPan3);
        Pearadox.Match_Data.setSand_CargoLCarg1(CargoLCarg1);
        Pearadox.Match_Data.setSand_CargoLCarg2(CargoLCarg2);
        Pearadox.Match_Data.setSand_CargoLCarg3(CargoLCarg3);
        Pearadox.Match_Data.setSand_CargoRCarg1(CargoRCarg1);
        Pearadox.Match_Data.setSand_CargoRCarg2(CargoRCarg2);
        Pearadox.Match_Data.setSand_CargoRCarg3(CargoRCarg3);
        Pearadox.Match_Data.setSand_CargoEndLPanel(CargoEndLPanel);
        Pearadox.Match_Data.setSand_CargoEndRPanel(CargoEndRPanel);
        Pearadox.Match_Data.setSand_CargoEndLCargo(CargoEndLCargo);
        Pearadox.Match_Data.setSand_CargoEndRCargo(CargoEndRCargo);

        Pearadox.Match_Data.setSand_RghtRocket_LPan1(RghtRocket_LPan1);
        Pearadox.Match_Data.setSand_RghtRocket_LPan2(RghtRocket_LPan2);
        Pearadox.Match_Data.setSand_RghtRocket_LPan3(RghtRocket_LPan3);
        Pearadox.Match_Data.setSand_RghtRocket_RPan1(RghtRocket_RPan1);
        Pearadox.Match_Data.setSand_RghtRocket_RPan2(RghtRocket_RPan2);
        Pearadox.Match_Data.setSand_RghtRocket_RPan3(RghtRocket_RPan3);
        Pearadox.Match_Data.setSand_RghtRocket_LCarg1(RghtRocket_LCarg1);
        Pearadox.Match_Data.setSand_RghtRocket_LCarg2(RghtRocket_LCarg2);
        Pearadox.Match_Data.setSand_RghtRocket_LCarg3(RghtRocket_LCarg3);
        Pearadox.Match_Data.setSand_RghtRocket_RCarg1(RghtRocket_RCarg1);
        Pearadox.Match_Data.setSand_RghtRocket_RCarg2(RghtRocket_RCarg2);
        Pearadox.Match_Data.setSand_RghtRocket_RCarg3(RghtRocket_RCarg3);
        Pearadox.Match_Data.setSand_CargoEndLPanel(CargoEndLPanel);
        Pearadox.Match_Data.setSand_CargoEndRPanel(CargoEndRPanel);
        Pearadox.Match_Data.setSand_CargoEndLCargo(CargoEndLCargo);
        Pearadox.Match_Data.setSand_CargoEndRCargo(CargoEndRCargo);
        
        // ToDo - set all variables to object
        Pearadox.Match_Data.setSand_PU2ndPanel(PU2ndPanel);
        Pearadox.Match_Data.setSand_PU2ndCargo(PU2ndCargo);
        Pearadox.Match_Data.setSand_PU2ndFloor(PU2ndFloor);
        Pearadox.Match_Data.setSand_PU2ndCorral(PU2ndCorral);
        Pearadox.Match_Data.setSand_PU2ndPlSta(PU2ndPlSta);

        Pearadox.Match_Data.setSand_PU3rdPanel(PU3rdPanel);
        Pearadox.Match_Data.setSand_PU3rdCargo(PU3rdCargo);
        Pearadox.Match_Data.setSand_PU3rdFloor(PU3rdFloor);
        Pearadox.Match_Data.setSand_PU3rdCorral(PU3rdCorral);
        Pearadox.Match_Data.setSand_PU3rdPlSta(PU3rdPlSta);

        Pearadox.Match_Data.setSand_num_Dropped(num_Dropped);

        // --------------
        Pearadox.Match_Data.setSand_comment(autoComment);
        Pearadox.Match_Data.setFinal_studID(studID);
        Log.w(TAG, "*******  All done with AUTO setters!!");
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    private void getMatch() {
        Log.d(TAG, "%%%%  getMatch  %%%%");
        txt_NextMatch = (TextView) findViewById(R.id.txt_NextMatch);
        txt_NextMatch.setText("");
        if (Pearadox.is_Network) {
            pfCur_Match_DBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Current Match - onDataChange  %%%%");
                    txt_Match = (TextView) findViewById(R.id.txt_Match);
                    txt_MyTeam = (TextView) findViewById(R.id.txt_MyTeam);
                    txt_TeamName = (TextView) findViewById(R.id.txt_TeamName);
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    while (iterator.hasNext()) {
                        p_Firebase.curMatch match_Obj = iterator.next().getValue(p_Firebase.curMatch.class);
                        matchID = match_Obj.getCur_match();
                        Log.d(TAG, "***>  Current Match = " + matchID + " " + match_Obj.getR1() + " " + match_Obj.getB3());
                        if (matchID.length() < 3) {
//                        Log.d(TAG, "MatchID NULL");
                            txt_Match.setText(" ");
                            txt_MyTeam.setText(" ");
                            txt_TeamName.setText(" ");
                            updateDev("Auto");      // Update 'Phase' for stoplight indicator in ScoutMaster
                        } else {        // OK!!  Match has started
//                        Log.d(TAG, "Match started " + matchID);
                            txt_Match.setText(matchID);
//                        Log.d(TAG, "Device = " + Pearadox.FRC514_Device + " ->" + onStart);
                            txt_NextMatch.setText(match_Obj.getOur_matches());
                            switch (Pearadox.FRC514_Device) {          // Who am I?!?
                                case ("Red-1"):             //#Red or Blue Scout
                                    txt_MyTeam.setText(match_Obj.getR1());
                                    break;
                                case ("Red-2"):             //#
                                    txt_MyTeam.setText(match_Obj.getR2());
                                    break;
                                case ("Red-3"):             //#
                                    txt_MyTeam.setText(match_Obj.getR3());
                                    break;
                                case ("Blue-1"):            //#
                                    txt_MyTeam.setText(match_Obj.getB1());
                                    break;
                                case ("Blue-2"):            //#
                                    txt_MyTeam.setText(match_Obj.getB2());
                                    break;
                                case ("Blue-3"):            //#####
                                    txt_MyTeam.setText(match_Obj.getB3());
                                    break;
                                default:                //
                                    Log.d(TAG, "device is _NOT_ a Scout ->" + device);
                            }
                            tn = (String) txt_MyTeam.getText();
                            findTeam(tn);   // Find Team info
                            txt_TeamName.setText(team_inst.getTeam_name());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    /*listener failed or was removed for security reasons*/
                }
            });
        } else {
            Log.e(TAG, "***  No Internet for Matches  ***");

        }
    }

    private void findTeam(String tnum) {
        Log.i(TAG, "$$$$$  findTeam " + tnum);
        boolean found = false;
        for (int i = 0; i < Pearadox.numTeams; i++) {        // check each team entry
            if (Pearadox.team_List.get(i).getTeam_num().equals(tnum)) {
                team_inst = Pearadox.team_List.get(i);
//                Log.d(TAG, "===  Team " + team_inst.getTeam_num() + " " + team_inst.getTeam_name() + " " + team_inst.getTeam_loc());
                found = true;
                break;  // found it!
            }
        }  // end For
        if (!found) {
            Log.e(TAG, "****** ERROR - Team _NOT_ found!! = " + tnum);
            txt_TeamName.setText("");
        }
    }
    private void updateDev(String phase) {     //
        Log.i(TAG, "#### updateDev #### " + phase);
        switch (Pearadox.FRC514_Device) {
            case "Scout Master":         // Scout Master
                key = "0";
                break;
            case ("Red-1"):             //#Red or Blue Scout
                key = "1";
                break;
            case ("Red-2"):             //#
                key = "2";
                break;
            case ("Red-3"):             //#
                key = "3";
                break;
            case ("Blue-1"):            //#
                key = "4";
                break;
            case ("Blue-2"):            //#
                key = "5";
                break;
            case ("Blue-3"):            //#####
                key = "6";
                break;
            case "Visualizer":          // Visualizer
                key = "7";
                break;
            default:                //
                Log.d(TAG, "DEV = NULL" );
        }
             pfDevice_DBReference.child(key).child("phase").setValue(phase);
    }

    private class startPosOnClickListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            startPos = parent.getItemAtPosition(pos).toString();
            Log.d(TAG, ">>>>>  '" + startPos + "'");
            final Spinner spinner_startPos = (Spinner) findViewById(R.id.spinner_startPos);
            if (spinner_startPos.getSelectedItemPosition() == 3) {  //  No Show?
                Log.e(TAG, "### Team/robot is a No Show ###" );
                editText_autoComment.setText(R.string.NoShowMsg);
                checkbox_noSS.setChecked(true);
                // ????? - Do we want to turn off all other widgets?
            }
            if (spinner_startPos.getSelectedItemPosition() == 1 || spinner_startPos.getSelectedItemPosition() == 2 ) {
                checkbox_noSS.setChecked(false);                            // un-check if old value was NoShow
            }
            if (spinner_startPos.getSelectedItemPosition() == 0) {          // reset to start
                checkbox_noSS.setChecked(false);                            // un-check if old value was NoShow
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit without saving? All of your data will be lost!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }


//    private TextWatcher tw = new TextWatcher() {
//        public void afterTextChanged(Editable s){
//
//        }
//        public void  beforeTextChanged(CharSequence s, int start, int count, int after){
//            // you can check for enter key here
//        }
//        public void  onTextChanged (CharSequence s, int start, int before,int count) {
//        }
//    };
//
//    EditText et = (EditText) findViewById(R.id.editText_Fuel);
//    et.addTextChangedListener(tw)



//###################################################################
//###################################################################
//###################################################################
@Override
public void onStart() {
    super.onStart();
    Log.v(TAG, "onStart");

    onStart = true;
    getMatch();      // Get current match
    Log.d(TAG, "*** onStart  ->" + onStart);

    vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    if (vibrate == null) {
        Log.e(TAG, "No vibration service exists.");
    }
}

    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        onStart = false;
        if (Pearadox.MatchData_Saved) {
            // ???? - Clear all data back to original settings
            Log.d(TAG, "#### Data was saved in Final #### ");
            //Toast.makeText(getBaseContext(), "Data was saved in Final - probably should clear data and wait for next match", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "OnDestroy");

     }

}
