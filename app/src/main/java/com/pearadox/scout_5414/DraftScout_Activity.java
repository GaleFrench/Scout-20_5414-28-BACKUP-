package com.pearadox.scout_5414;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.util.Log.e;
import static com.pearadox.scout_5414.R.id.progressBar1;

public class DraftScout_Activity extends AppCompatActivity {

    String TAG = "DraftScout_Activity";        // This CLASS name
    Boolean is_Resumed = false;
    int start = 0;          // Start Position for matches (0=ALL)
    int numObjects = 0; int numProcessed = 0;
    int minMatches = 99;         // minimum # of matches collected
    int numPicks = 24;              // # of picks to show for Alliance Picks (actually get from Preferences)
    /*Shared Prefs-Scored Cargo*/ public String cargo_L1  = ""; public String cargo_L2  = ""; public String cargo_L3  = "";
    /*Shared Prefs-Panels*/ public String panels_L1 = ""; public String panels_L2  = ""; public String panels_L3  = ""; public String panels_Drop  = "";
    /*Shared Prefs-Climbing*/  public String climbLift1 = ""; public String climbLift2 = "";  public String climbLifted = ""; public String climbHAB0 = ""; public String climbHAB1 = ""; public String climbHAB2 = ""; public String climbHAB3 = "";
    /*Weight factors*/ public String wtClimb = ""; public String wtCargo = ""; public String wtPanels = "";
    ImageView imgStat_Load;
    TextView txt_EventName, txt_NumTeams, txt_Formula, lbl_Formula, txt_LoadStatus, txt_SelNum;
    Spinner spinner_numMatches;
    ListView lstView_Teams;
    TextView TeamData, BA, Stats, Stats2;
    Button btn_Match, btn_Pit, btn_Default;
    ProgressBar pbSpinner;
    RadioGroup radgrp_Sort;
    RadioButton radio_Climb, radio_Cargo, radio_Weight, radio_Team, radio_pGt1, radio_cGt1, radio_Panels;
    //    Button btn_Up, btn_Down, btn_Delete;
    public ArrayAdapter<String> adaptTeams;
    public static String[] numMatch = new String[]             // Num. of Matches to process
            {"ALL","Last","Last 2","Last 3","Last 4"};
    //    ArrayList<String> draftList = new ArrayList<String>();
    static final ArrayList<HashMap<String, String>> draftList = new ArrayList<HashMap<String, String>>();
    public int teamSelected = -1;
    public static String sortType = "";
    private ProgressDialog progress;
    String tNumb = "";
    String tn = "";
    String Viz_URL = "";
    String teamNum=""; String teamName = "";
    String tmRank = "";
    String tmRScore = "";
    String tmWLT = "";
    String tmOPR = "";
    p_Firebase.teamsObj team_inst = new p_Firebase.teamsObj();
    //    Team[] teams;
    public static int BAnumTeams = 0;                                      // # of teams from Blue Alliance
    String cubeChk = "";
    String climb_HAB0 = ""; String climb_HAB1 = ""; String climb_HAB2 = ""; String climb_HAB3 = "";
    String sandCargL1 = "";
    String sandCargL2 = "";
    String sandCargL3 = "";
    String sandPanelL1 = "";
    String sandPanelL2 = "";
    String sandPanelL3 = "";
    String teleCargoL1 = "";
    String teleCargoL3 = "";
    String mdNumMatches = "";
    String telePanL1 = "";
    String teleCargoL2 = "";
    String telePanL2 = "";
    String telePanL3 = "";
    String panDropped = "";
    String liftOne = "";
    String liftTwo = "";
    String gotLifted = "";
    String onPlatform = "";
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfMatchData_DBReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    matchData match_inst = new matchData();
    // -----  Array of Match Data Objects for Draft Scout
    public static ArrayList<matchData> All_Matches = new ArrayList<matchData>();
    String load_team, load_name;

    //===========================   SCORES object that is used for Comparator SORT  ==================================
    public static class Scores {
        private String teamNum;
        private String teamName;
        private String scrRank;
        private String scrRScore;
        private String scrWLT;
        private String scrOPR;
        private float SCORE_cargoScore;
        private float SCORE_panelsScore;
        private float SCORE_climbScore;
        private float SCORE_combinedScore;

        public Scores() {
        }

// ** Constuctor **

        public Scores(String teamNum, String teamName, String scrRank, String scrRScore, String scrWLT, String scrOPR, float SCORE_cargoScore, float SCORE_panelsScore, float SCORE_climbScore, float SCORE_combinedScore) {
            this.teamNum = teamNum;
            this.teamName = teamName;
            this.scrRank = scrRank;
            this.scrRScore = scrRScore;
            this.scrWLT = scrWLT;
            this.scrOPR = scrOPR;
            this.SCORE_cargoScore = SCORE_cargoScore;
            this.SCORE_panelsScore = SCORE_panelsScore;
            this.SCORE_climbScore = SCORE_climbScore;
            this.SCORE_combinedScore = SCORE_combinedScore;
        }

// ** Getters/Setters **

        public String getTeamNum() {
            return teamNum;
        }

        public void setTeamNum(String teamNum) {
            this.teamNum = teamNum;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getScrRank() {
            return scrRank;
        }

        public void setScrRank(String scrRank) {
            this.scrRank = scrRank;
        }

        public String getScrRScore() {
            return scrRScore;
        }

        public void setScrRScore(String scrRScore) {
            this.scrRScore = scrRScore;
        }

        public String getScrWLT() {
            return scrWLT;
        }

        public void setScrWLT(String scrWLT) {
            this.scrWLT = scrWLT;
        }

        public String getScrOPR() {
            return scrOPR;
        }

        public void setScrOPR(String scrOPR) {
            this.scrOPR = scrOPR;
        }

        public float getSCORE_cargoScore() {
            return SCORE_cargoScore;
        }

        public void setSCORE_cargoScore(float SCORE_cargoScore) {
            this.SCORE_cargoScore = SCORE_cargoScore;
        }

        public float getSCORE_panelsScore() {
            return SCORE_panelsScore;
        }

        public void setSCORE_panelsScore(float SCORE_panelsScore) {
            this.SCORE_panelsScore = SCORE_panelsScore;
        }

        public float getSCORE_climbScore() {
            return SCORE_climbScore;
        }

        public void setSCORE_climbScore(float SCORE_climbScore) {
            this.SCORE_climbScore = SCORE_climbScore;
        }

        public float getSCORE_combinedScore() {
            return SCORE_combinedScore;
        }

        public void setSCORE_combinedScore(float SCORE_combinedScore) {
            this.SCORE_combinedScore = SCORE_combinedScore;
        }

        public static Comparator<Scores> getTeamComp() {
            return teamComp;
        }

        public static void setTeamComp(Comparator<Scores> teamComp) {
            Scores.teamComp = teamComp;
        }

        public static Comparator<Scores> getClimbComp() {
            return climbComp;
        }

        public static void setClimbComp(Comparator<Scores> climbComp) {
            Scores.climbComp = climbComp;
        }


// ** END - Getters/Setters **

        public static Comparator<Scores> teamComp = new Comparator<Scores>() {
            public int compare(Scores t1, Scores t2) {
                String TeamNum1 = t1.getTeamNum();
                String TeamNum2 = t2.getTeamNum();
                //ascending order
                return TeamNum1.compareTo(TeamNum2);
                //descending order
                //return TeamNum2.compareTo(TeamNum1);
            }
        };
        public static Comparator<Scores> climbComp = new Comparator<Scores>() {
            public int compare(Scores s1, Scores s2) {
                float climbNum1 = s1.getSCORE_climbScore();
                float climbNum2 = s2.getSCORE_climbScore();
	            /*For ascending order*/
                //return climbNum1-climbNum2;
	            /*For descending order*/
                return (int) (climbNum2 - climbNum1);
            }
        };

    }
    //==========================
    public ArrayList<Scores> team_Scores = new ArrayList<Scores>();
    Scores score_inst = new Scores();

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_draft_scout);
        Log.i(TAG, "@@@@@ DraftScout_Activity  @@@@@");
        Log.e(TAG, "B4 - " + sortType);
        if (savedInstanceState != null) {
            Log.e(TAG, "Are we ever getting called? " + is_Resumed);
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            String sortType = prefs.getString("Sort", "");
        } else {
    //            sortType = "Team#";
        }
        Log.e(TAG, "After - " + sortType);
        getprefs();         // Get multiplier values from Preferences

        txt_EventName = findViewById(R.id.txt_EventName);
        txt_NumTeams = findViewById(R.id.txt_NumTeams);
        txt_Formula = findViewById(R.id.txt_Formula);
        lbl_Formula = findViewById(R.id.lbl_Formula);
        txt_LoadStatus = findViewById(R.id.txt_LoadStatus);
        txt_SelNum = findViewById(R.id.txt_SelNum);
        txt_SelNum.setText("");
        lstView_Teams = findViewById(R.id.lstView_Teams);
        txt_EventName.setText(Pearadox.FRC_EventName);              // Event Name
        txt_NumTeams.setText(String.valueOf(Pearadox.numTeams));    // # of Teams
        txt_Formula.setText(" ");
        btn_Match = findViewById(R.id.btn_Match);
        btn_Pit = findViewById(R.id.btn_Pit);
        btn_Match.setEnabled(false);
        btn_Match.setVisibility(View.INVISIBLE);
        btn_Pit.setEnabled(false);
        btn_Pit.setVisibility(View.INVISIBLE);
        pbSpinner =  (ProgressBar) findViewById(progressBar1);
//        pbSpinner.setVisibility(View.INVISIBLE);
//        Spinner spinner_numMatches = (Spinner) findViewById(R.id.spinner_numMatches);
//        ArrayAdapter adapter_Matches = new ArrayAdapter<String>(this, R.layout.robonum_list_layout, numMatch);
//        adapter_Matches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_numMatches.setAdapter(adapter_Matches);
//        spinner_numMatches.setSelection(0, false);
//        spinner_numMatches.setOnItemSelectedListener(new numMatches_OnItemSelectedListener());

        pfDatabase = FirebaseDatabase.getInstance();
        pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data

        RadioGroup radgrp_Sort = findViewById(R.id.radgrp_Sort);
        for(int i = 0; i < radgrp_Sort.getChildCount(); i++){        // turn them all OFF
           radgrp_Sort.getChildAt(i).setEnabled(false);
        }
        radgrp_Sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "@@ RadioClick_Sort @@");
                minMatches = 99;    // Reset for new search
                txt_SelNum = findViewById(R.id.txt_SelNum);
                txt_SelNum.setText("");
                teamSelected = -1;
                radio_Team = findViewById(checkedId);
                String value = radio_Team.getText().toString();
//                Log.w(TAG, "RadioSort -  '" + value + "'");
                switch (value) {
                    case "Climb":
//                        Log.w(TAG, "Climb sort");
                        sortType = "Climb";
                        Collections.sort(team_Scores, new Comparator<Scores>() {
                            @Override
                            public int compare(Scores c1, Scores c2) {
                                return Float.compare(c1.getSCORE_climbScore(), c2.getSCORE_climbScore());
                            }
                        });
                        Collections.reverse(team_Scores);
                        showFormula(sortType);              // update the formula
                        loadTeams();
                        break;
                    case "Cargo":
                        sortType = "Cargo";
//                      Log.w(TAG, "Cube sort");
                        Collections.sort(team_Scores, new Comparator<Scores>() {
                            @Override
                            public int compare(Scores c1, Scores c2) {
                                return Float.compare(c1.getSCORE_cargoScore(), c2.getSCORE_cargoScore());
                            }
                        });
                        Collections.reverse(team_Scores);   // Descending
                        showFormula("Cargo");              // update the formula
                        loadTeams();
                        break;
                    case "Combined":
//                Log.w(TAG, "Combined sort");
                        sortType = "Combined";
                        Collections.sort(team_Scores, new Comparator<Scores>() {
                            @Override
                            public int compare(Scores c1, Scores c2) {
                                return Float.compare(c1.getSCORE_combinedScore(), c2.getSCORE_combinedScore());
                            }
                        });
                        Collections.reverse(team_Scores);   // Descending
                        showFormula(sortType);              // update the formula
                        loadTeams();
                        break;
                    case "Panels":
                        sortType = "Panels";
//                        Log.w(TAG, "Panels sort");
                        Collections.sort(team_Scores, new Comparator<Scores>() {
                            @Override
                            public int compare(Scores c1, Scores c2) {
                                return Float.compare(c1.getSCORE_panelsScore(), c2.getSCORE_panelsScore());
                            }
                        });
                        Collections.reverse(team_Scores);   // Descending
                        showFormula(sortType);              // update the formula
                        loadTeams();
                        break;
                    case "Team#":
//                Log.w(TAG, "Team# sort");
                        sortType = "Team#";
                        Collections.sort(team_Scores, Scores.teamComp);
                        lbl_Formula.setTextColor(Color.parseColor("#ffffff"));
                        txt_Formula.setText(" ");       // set formulat to blank
                        loadTeams();
                        break;
                    default:                //
                        Log.e(TAG, "*** Invalid Sort " + value);
                }
            }
        });



// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        lstView_Teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {
                Log.w(TAG, "*** lstView_Teams ***   Item Selected: " + pos);
                teamSelected = pos;
                lstView_Teams.setSelector(android.R.color.holo_blue_light);
        		/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
//                tnum = draftList.get(teamSelected).substring(0,4);
                txt_SelNum = findViewById(R.id.txt_SelNum);
                txt_SelNum.setText(String.valueOf(pos+1));      // Sort Position
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    private void getprefs() {
        Log.i(TAG, "** getprefs **");

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

        cargo_L1 = sharedPref.getString("prefCargo_L1", "1.0");
        cargo_L2 = sharedPref.getString("prefCargo_L2", "2.0");
        cargo_L3 = sharedPref.getString("prefCargo_L3", "3.0");
//        Log.e(TAG, "cargo_L1=" + cargo_L1);

        panels_L1 = sharedPref.getString("prefPanel_L1", "1.0");
        panels_L2 = sharedPref.getString("prefPanel_L2", "2.0");
        panels_L3 = sharedPref.getString("prefPanel_L3", "3.0");
        panels_Drop = sharedPref.getString("prefPanel_Drop", "-1.0");

        climbLift1 = sharedPref.getString("prefClimb_lift1", "1.5");
        climbLift2 = sharedPref.getString("prefClimb_lift2", "2.0");
        climbLifted = sharedPref.getString("prefClimb_lifted", "0.3");
        climbHAB0 = sharedPref.getString("prefClimb_HAB0", "-1.0");
        climbHAB1 = sharedPref.getString("prefClimb_HAB1", "1.0");
        climbHAB2 = sharedPref.getString("prefClimb_HAB2", "2.5");
        climbHAB3 = sharedPref.getString("prefClimb_HAB3", "5.0");

        wtCargo  = sharedPref.getString("prefWeight_cargo", "1.0");
        wtPanels = sharedPref.getString("prefWeight_panels", "1.0");
        wtClimb  = sharedPref.getString("prefWeight_climb", "1.5");

        numPicks = Integer.parseInt(sharedPref.getString("prefAlliance_num", "24"));


    }

    private String showFormula(String typ) {
        Log.i(TAG, "** showFormula **  " + typ);
        String form = "";
        getprefs();         // make sure Prefs are up to date
        switch (typ) {
            case "Climb":
                form = "((" + climbHAB1 + "*HAB1) + (" + climbHAB2 + "*HAB2) + (" + climbHAB3 + "*HAB3) + (" + climbHAB0 + "*HAB0)) ✚ " +"((Lift1*" + climbLift1 + ") + " +"(Lift2*" + climbLift2 + ") + (WasLifted*" + climbLifted + ")) / # matches";
                lbl_Formula.setTextColor(Color.parseColor("#4169e1"));      // blue
                txt_Formula.setText(form);
                break;
            case "Cargo":
                form = "((" + cargo_L1 +"* cargL1) + (" + cargo_L2 + "* cargL2) + (" + cargo_L3 + "* cargL3)) /# matches";
                lbl_Formula.setTextColor(Color.parseColor("#ee00ee"));      // magenta
                txt_Formula.setText(form);
                break;
            case "Combined":
                form = "((" + wtClimb + "*climbScore) + (" + wtCargo + "*cargoScore) + (" + wtPanels + "*panelScore)) / #matches";
                lbl_Formula.setTextColor(Color.parseColor("#ff0000"));      // red
                txt_Formula.setText(form);
                break;
            case "Panels":
                form = "((" + panels_L1 +"* panL1) + (" + panels_L2 + "* panL2) + (" + panels_L3 + "* panL3) + (" + panels_Drop +"* dropped)) " + "/# matches";
                lbl_Formula.setTextColor(Color.parseColor("#a8a8a8"));      /// grey
                txt_Formula.setText(form);
                break;
            default:                //
                Log.e(TAG, "*** Invalid Type " + typ);
        }
        return typ;
    }

    //===========================================================================================
    public class numMatches_OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Boolean goodToGo = false;
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            Log.w(TAG, ">>>>> NumMatches '" + num + "'  " + minMatches);
            switch (num) {
                case "Last":
                    if (minMatches >= 1) {
                        start = numObjects - 1;     //
                        numProcessed = 1;
                        goodToGo = true;
                    } else {
                        Toast_Msg(num, minMatches);
                        goodToGo = false;
                    }
                    break;
                case "Last 2":
                    if (minMatches >= 2) {
                        start = numObjects - 2;     //
                        numProcessed = 2;
                        goodToGo = true;
                    } else {
                        Toast_Msg(num, minMatches);
                        goodToGo = false;
                    }
                    break;
                case "Last 3":
//                    Log.d(TAG, "@@@ Last 3 @@@" );
                    if (minMatches >= 3) {
                        start = numObjects - 3;     //
                        numProcessed = 3;
                        goodToGo = true;
                    } else {
                        Toast_Msg(num, minMatches);
                        goodToGo = false;
                    }
                    break;
                case "Last 4":
//                    Log.d(TAG, "@@@ Last 4 @@@" );
                    if (minMatches >= 4) {
                        start = numObjects - 4;     //
                        numProcessed = 4;
                        goodToGo = true;
                    } else {
                        Toast_Msg(num, minMatches);
                        goodToGo = false;
                    }
                    break;
                case "ALL":
                    start = 0;                  // Start at beginning
                    numProcessed = numObjects;
                    goodToGo = true;
                    break;
                default:                //
                    Log.e(TAG, "Invalid number of matches - " + start);
            }
            Log.e(TAG, "*** Start=" + start + "  #Proc=" + numProcessed + "  " + goodToGo);
            if (goodToGo) {
                Log.e(TAG, "We are good To Go!! - Start=" + start + " #Proc=" + numProcessed);
//            init_Values();
//            getMatch_Data();
            }
        }
        public void onNothingSelected (AdapterView < ? > parent){
            // Do nothing.
        }
    }

public void Toast_Msg(String choice, Integer minimum) {
    Toast toast = Toast.makeText(getBaseContext(), "\nCannot show '" + choice + "' some teams have " + minimum + " matches \n " , Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    toast.show();

}



    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void buttonDefault_Click(View view) {
        // Reload _ALL_ the Preference defaults
        Log.i(TAG, ">>>>> buttonDefault_Click" );
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        Toast toast = Toast.makeText(getBaseContext(), "Default Settings have been reset", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

        showFormula(sortType);              // update the formula
        loadTeams();                        // reload based on default
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void buttonMatch_Click(View view) {
        Log.i(TAG, ">>>>> buttonMatch_Click  " + teamSelected);
        HashMap<String, String> temp = new HashMap<String, String>();
        String teamHash;
        setProgressBarIndeterminateVisibility(true);
//        pbSpinner = (ProgressBar) findViewById(progressBar1);
//        pbSpinner.setVisibility(View.VISIBLE);
        if (teamSelected >= 0) {
            draftList.get(teamSelected);
            temp = draftList.get(teamSelected);
            teamHash = temp.get("team");
//        Log.w(TAG, "teamHash: '" + teamHash + "' \n ");
            load_team = teamHash.substring(0, 4);
            load_name = teamHash.substring(5, teamHash.indexOf("("));  // UP TO # MATCHES
//        Log.w(TAG, ">>>team & name: '" + load_team + "'  [" + load_name +"]");
            addMatchData_Team_Listener(pfMatchData_DBReference);        // Load Matches for _THIS_ selected team
        } else {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            Toast toast = Toast.makeText(getBaseContext(), "★★★★  There is _NO_ Team selected for Match Data ★★★★", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
        pbSpinner.setVisibility(View.INVISIBLE);
        setProgressBarIndeterminateVisibility(false);
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void buttonPit_Click(View view) {
        Log.i(TAG, ">>>>> buttonPit_Click  " + teamSelected);
        HashMap<String, String> temp = new HashMap<String, String>();
        String teamHash="";
        final String[] URL = {""};  Viz_URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (teamSelected >= 0) {
            draftList.get(teamSelected);
            temp = draftList.get(teamSelected);
            teamHash = temp.get("team");
            teamNum = teamHash.substring(0, 4);
            teamName = teamHash.substring(5, teamHash.indexOf("("));  // UP TO # MATCHES
            Log.e(TAG, "****** team  '" + teamName + "' ");
//            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2020.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + teamNum.trim() + ".png");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.w(TAG, "@@@@@@@@  Getting Photo!   URI=" + uri);
                    URL[0] = uri.toString();
                    Viz_URL = URL[0];
                    Log.w(TAG, "*GOOD* URL=" + Viz_URL);
                    launchVizPit(teamNum, teamName, Viz_URL);   // do it here to WAIT for FB retrieval
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "ERR= '" + exception + "'");
                    Viz_URL = "";
                    Log.w(TAG, "*BAD* URL=" + Viz_URL + "' ");
                    launchVizPit(teamNum, teamName, Viz_URL);
                }
            });
        } else {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            Toast toast = Toast.makeText(getBaseContext(), "★★★★  There is _NO_ Team selected for Pit Data ★★★★", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void launchVizPit(String team, String name, String imgURL) {
        Log.d(TAG,">>>>>>>>  launchVizPit " + team + " " + name + " " + imgURL);      // ** DEBUG **
        Intent pit_intent = new Intent(DraftScout_Activity.this, VisPit_Activity.class);
        Bundle VZbundle = new Bundle();
        VZbundle.putString("team", team);        // Pass data to activity
        VZbundle.putString("name", name);        // Pass data to activity
        VZbundle.putString("url", imgURL);       // Pass data to activity
        pit_intent.putExtras(VZbundle);
        startActivity(pit_intent);               // Start Visualizer for Pit Data

    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addMatchData_Team_Listener(final DatabaseReference pfMatchData_DBReference) {
        pfMatchData_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "<<<< addMatchData_Team_Listener >>>> Match Data for team " + load_team);
                Pearadox.Matches_Data.clear();
                matchData mdobj = new matchData();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    mdobj = iterator.next().getValue(matchData.class);
                    if (mdobj.getTeam_num().matches(load_team)) {
                        Pearadox.Matches_Data.add(mdobj);
                    }
                }
                Log.i(TAG, "***** Matches Loaded. # = "  + Pearadox.Matches_Data.size());
                if (Pearadox.Matches_Data.size() > 0) {
                    Intent match_intent = new Intent(DraftScout_Activity.this, VisMatch_Activity.class);
                    Bundle VZbundle = new Bundle();
                    VZbundle.putString("team", load_team);          // Pass data to activity
                    VZbundle.putString("name", load_name);          // Pass data to activity
                    match_intent.putExtras(VZbundle);
                    startActivity(match_intent);                    // Start Visualizer for Match Data
                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "★★★★  There is _NO_ Match Data for Team " + load_team + "  ★★★★", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }

    private void loadTeams() {
        Log.w(TAG, "@@@@  loadTeams started  @@@@  " + team_Scores.size() + " Type=" + sortType);
//        pbSpinner.setVisibility(View.VISIBLE);

        SimpleAdapter adaptTeams = new SimpleAdapter(
                this,
                draftList,
                R.layout.draft_list_layout,
                new String[] {"team","BA","Stats","Stats2"},
                new int[] {R.id.TeamData,R.id.BA, R.id.Stats, R.id.Stats2}
        );

        draftList.clear();
        String totalScore="";
        minMatches = 99;    // Reset for new search
        for (int i = 0; i < team_Scores.size(); i++) {    // load by sorted scores
            score_inst = team_Scores.get(i);
//            Log.w(TAG, i +" team=" + score_inst.getTeamNum());
            HashMap<String, String> temp = new HashMap<String, String>();
            tn = score_inst.getTeamNum();
            tmRank = score_inst.getScrRank();
            tmRScore = score_inst.getScrRScore();
            tmWLT = score_inst.scrWLT;
            tmOPR = score_inst.getScrOPR();

            // ToDo - use start & NumObjects
            teamData(tn);   // Get Team's Match Data
            switch (sortType) {
                case "Climb":
                    totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_climbScore()) + "]";
                    break;
                case "Cargo":
                    totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_cargoScore()) + "]";
                    break;
                case "Combined":
                    totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_combinedScore()) + "]";
                    break;
                case "Panels":
                    totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_panelsScore()) + "]";
                    break;
                case "Team#":
                    totalScore=" ";
                    break;
                default:                //
                    Log.e(TAG, "Invalid Sort - " + sortType);
            }

            temp.put("team", tn + "-" + score_inst.getTeamName() + "  (" + mdNumMatches + ")  " +  totalScore);
//            temp.put("BA", "Rank=" + teams[i].rank + "  " + teams[i].record + "   OPR=" + String.format("%3.1f", (teams[i].opr)) + "    ↑ " + String.format("%3.1f", (teams[i].touchpad)) + "   kPa=" + String.format("%3.1f", (teams[i].pressure)));
            temp.put("BA","Rank=" + tmRank + "   Score=" + tmRScore + "   WLT=" + tmWLT + "   OPR=" + tmOPR);
                    temp.put("Stats", "Sand ⚫ ¹" + sandCargL1 + " ²" + sandCargL2 + " ³" + sandCargL3 + "  ☢ ¹" + sandPanelL1 + " ²" + sandPanelL2 + " ³" + sandPanelL3 + "    Tele ⚫ ¹" + teleCargoL1 + " ²" + teleCargoL2 +  "  ³" + teleCargoL3 + "  ☢ ¹" + telePanL1 + " ²" + telePanL2 + " ³" + telePanL3  + "  ▼" +panDropped);
            temp.put("Stats2",  "Climb HAB ₀" + climb_HAB0 + " ₁" + climb_HAB1 + " ₂" + climb_HAB2 + " ₃" + climb_HAB3 + "    ↕One " + liftOne + "  ↕Two " + liftTwo + "    Was↑ " + gotLifted);
            draftList.add(temp);
        } // End For
        Log.w(TAG, "### Teams ###  : " + draftList.size());
        lstView_Teams.setAdapter(adaptTeams);
        adaptTeams.notifyDataSetChanged();
    }

    // ****************************************************************************************8
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draft, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.e(TAG, "@@@  Options  @@@ " + sortType);
        Log.w(TAG, " \n  \n");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, DraftSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_help) {
            Intent help_intent = new Intent(this, HelpActivity.class);
            startActivity(help_intent);  	// Show Help
            return true;
        }
        if (id == R.id.action_picks) {
            Log.e(TAG, "Picks");
            Toast toast = Toast.makeText(getBaseContext(), "\n Generating Alliance Picks file - Please wait ... \n ", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            txt_Formula.setText("Generating Alliance Picks file - Please wait ...");
            alliance_Picks();
            return true;
        }
        if (id == R.id.action_screen) {
            String filNam = Pearadox.FRC_Event.toUpperCase() + "-Draft"  + "_" + sortType + ".JPG";
            Log.w(TAG, "File='" + filNam + "'");
            try {
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + filNam);
                View v1 = getWindow().getDecorView().getRootView();             // **\
                v1.setDrawingCacheEnabled(true);                                // ** \Capture screen
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());      // ** /  as bitmap
                v1.setDrawingCacheEnabled(false);                               // **/
                FileOutputStream fos = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();           //release memory
                Toast toast = Toast.makeText(getBaseContext(), "☢☢  Screen captured in Download/FRC5414  ☢☢", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void alliance_Picks() {
        Log.e(TAG, "@@@  alliance_Picks  @@@ ");
        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
//        Toast toast = Toast.makeText(getBaseContext(), "\n Generating Alliance Picks file - Please wait ... \n ", Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.show();

        String tName = ""; String totalScore=""; String DS = "";
        String underScore = new String(new char[30]).replace("\0", "_");    // string of 'x' underscores
        String blanks = new String(new char[50]).replace("\0", " ");        // string of 'x' blanks
        String pound = new String(new char[50]).replace("\0", "#");        // string of 'x' Pound
// ======================================================================================
        sortType = "Combined";          // Attempt to "force" correct sort 1st time
        Collections.sort(team_Scores, new Comparator<Scores>() {
            @Override
            public int compare(Scores c1, Scores c2) {
                return Float.compare(c1.getSCORE_combinedScore(), c2.getSCORE_combinedScore());
            }
        });
        Collections.reverse(team_Scores);   // Descending
        loadTeams();
// ======================================================================================
        // ToDo - Convert to Async task
        if (numPicks > team_Scores.size()) {
//            Log.w(TAG, "******>> numPick changed to: " + team_Scores.size());
            numPicks = team_Scores.size();      // Use max (prevent Error when # teams < 'numPicks')
        }
        if (numPicks > 24) {
            DS = "";                    // Use Single Space
        }else {
            DS = "\n";                  // Use Double Space on anything less than 24
        }
        try {
            String destFile = Pearadox.FRC_ChampDiv + "_Alliance-Picks" + ".txt";
            File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
//            Log.e(TAG, " path = " + prt);
//            BufferedWriter bW;
//            bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
            BufferedWriter bW = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(prt), "UTF-8"
            ));
            bW.write(Pearadox.FRC_ChampDiv + "-" + Pearadox.FRC_EventName +  "\n");
            bW.write(underScore + "  COMBINED  " + underScore +  "\n" + DS);
            //  Combined sort
            Collections.sort(team_Scores, new Comparator<Scores>() {
                @Override
                public int compare(Scores c1, Scores c2) {
                    return Float.compare(c1.getSCORE_combinedScore(), c2.getSCORE_combinedScore());
                }
            });
            Collections.reverse(team_Scores);   // Descending
            loadTeams();
            for (int i = 0; i < numPicks; i++) {    // load by sorted scores
                score_inst = team_Scores.get(i);
                tNumb = score_inst.getTeamNum();
                tName = score_inst.getTeamName();
                tName = tName + blanks.substring(0, (36 - tName.length()));     // Pad the name to line up Tabs (\t)
                Log.e(TAG, ">>>>  teamName '" + tName + "' ");
                totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_combinedScore()) + "]";
                teamData(tNumb);   // Get Team's Match Data
                bW.write(String.format("%2d", i+1) +") " + tNumb + "-" + tName + "\t  (" + String.format("%2d",(Integer.parseInt(mdNumMatches))) + ")   " +  totalScore + " \t");
                bW.write( "\n" + DS);
            } // end For # teams
            bW.write(" \n" + "\n" + (char)12);        // NL & FF
            //=====================================================================

            bW.write(Pearadox.FRC_ChampDiv + " - " + Pearadox.FRC_EventName +  "\n");
            bW.write(underScore + "  CARGO  " + underScore +  "\n \n");
            //  Switch sort
            sortType = "Cargo";
            Collections.sort(team_Scores, new Comparator<Scores>() {
                @Override
                public int compare(Scores c1, Scores c2) {
                    return Float.compare(c1.getSCORE_cargoScore(), c2.getSCORE_cargoScore());
                }
            });
            Collections.reverse(team_Scores);   // Descending
            loadTeams();
            for (int i = 0; i < numPicks; i++) {    // load by sorted scores
                score_inst = team_Scores.get(i);
                tNumb = score_inst.getTeamNum();
                tName = score_inst.getTeamName();
                tName = tName + blanks.substring(0, (36 - tName.length()));
                totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_cargoScore()) + "]";
                teamData(tNumb);   // Get Team's Match Data
                bW.write(String.format("%2d", i+1) + ") " + tNumb + "-" + tName  + "\t (" + String.format("%2d",(Integer.parseInt(mdNumMatches))) + ") " +  totalScore + "\t");
                bW.write("⚫Sand ₁" + sandCargL1 + " ₂" + sandCargL2 + " ₃" + sandCargL3 + "  Tele ₁" + teleCargoL1 + " ₂" + teleCargoL2+ " ₃" + teleCargoL3 + "\n" + DS);
            } // end For # teams
            bW.write(" \n" + "\n" + (char)12);        // NL & FF
            //=====================================================================

            bW.write(Pearadox.FRC_ChampDiv + " - " + Pearadox.FRC_EventName +  "\n");
            bW.write(underScore + "  PANELS  " + underScore +  "\n" + DS);
            //  Panels sort
            sortType = "Panels";
            Collections.sort(team_Scores, new Comparator<Scores>() {
                @Override
                public int compare(Scores c1, Scores c2) {
                    return Float.compare(c1.getSCORE_panelsScore(), c2.getSCORE_panelsScore());
                }
            });
            Collections.reverse(team_Scores);   // Descending
            loadTeams();
            for (int i = 0; i < numPicks; i++) {    // load by sorted scores
                score_inst = team_Scores.get(i);
                tNumb = score_inst.getTeamNum();
                tName = score_inst.getTeamName();
                tName = tName + blanks.substring(0, (36 - tName.length()));
                totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_panelsScore()) + "]";
                teamData(tNumb);   // Get Team's Match Data
                bW.write(String.format("%2d", i+1) +") " + tNumb + "-" + tName + "\t  (" + String.format("%2d",(Integer.parseInt(mdNumMatches))) + ")  " +  totalScore);
                bW.write( "☢ Sand  ₁" + sandPanelL1 + " ₂" + sandPanelL2 + " ₃" + sandPanelL3 + "  Tele  ₁" + telePanL1 + " ₂" + telePanL2 + " ₃" + telePanL3 + "  ▼ " + panDropped + "\n" + DS);
            } // end For # teams
            bW.write(" \n" + "\n" + (char)12);        // NL & FF
            //=====================================================================

            bW.write(Pearadox.FRC_ChampDiv + " - " + Pearadox.FRC_EventName +  "\n");
            bW.write(underScore + "  CLIMB  " + underScore +  "\n \n");
            //  Scale sort
            sortType = "Climb";
            Collections.sort(team_Scores, new Comparator<Scores>() {
                @Override
                public int compare(Scores c1, Scores c2) {
                    return Float.compare(c1.getSCORE_climbScore(), c2.getSCORE_climbScore());
                }
            });
            Collections.reverse(team_Scores);   // Descending
            loadTeams();
            for (int i = 0; i < numPicks; i++) {    // load by sorted scores
                score_inst = team_Scores.get(i);
                tNumb = score_inst.getTeamNum();
                tName = score_inst.getTeamName();
                tName = tName + blanks.substring(0, (36 - tName.length()));
                totalScore = "[" + String.format("%3.2f", score_inst.getSCORE_climbScore()) + "]";
                teamData(tNumb);   // Get Team's Match Data
                bW.write(String.format("%2d", i+1) +") " + tNumb + " - " + tName + "\t  (" + String.format("%2d",(Integer.parseInt(mdNumMatches))) + ") " +  totalScore + " \t");
                bW.write(" HAB ₀" + climb_HAB0 + " ₁" + climb_HAB1 + " ₂" + climb_HAB2  + " ₃" + climb_HAB3 + "\n" + DS);
            } // end For # teams
//            bW.write(" \n" + "\n" + (char)12);        // NL & FF
            //=====================================================================

            bW.write(" \n" + "\n");        // NL
            //=====================================================================

            bW.flush();
            bW.close();
            Toast toast2 = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Alliance Picks file (" + numPicks + " teams) written to SD card [Download/FRC5414] ***", Toast.LENGTH_LONG);
            toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast2.show();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " not found in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        pickList();     // generate Picklist for app
    }

    private void pickList() {
        Log.w(TAG, "$$$$  pickList  $$$$ ");
// ======================================================================================
        sortType = "Combined";          //
        Collections.sort(team_Scores, new Comparator<Scores>() {
            @Override
            public int compare(Scores c1, Scores c2) {
                return Float.compare(c1.getSCORE_combinedScore(), c2.getSCORE_combinedScore());
            }
        });
        Collections.reverse(team_Scores);   // Descending
        loadTeams();
//        Log.w(TAG,"Size=" + draftList.size());
// ======================================================================================
        HashMap<String, String> temp = new HashMap<String, String>();
        try {
            String destFile = Pearadox.FRC_ChampDiv.toUpperCase() + "_Pick-List" + ".txt";
            File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
            BufferedWriter bW = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(prt), "UTF-8"
            ));

            for (int i = 0; i < draftList.size(); i++) {    // load by sorted scores
                temp = draftList.get(i);
                bW.write(temp +  "\n");

            } //end FOR
                //**********************************************
            bW.write(" \n" + "\n");        // NL
            bW.flush();
            bW.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " not found in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    private void teamData(String team) {
//        Log.i(TAG, "$$$$  teamData  $$$$ " + team);
        int autoCubeSw = 0; int autoCubeSwAtt = 0; int autoCubeSc = 0; int autoCubeScAtt = 0; int autoSwXnum = 0;  int autoScXnum = 0; int autoCubeSwExtra = 0; int autoCubeScExtra = 0;
        int teleCubeSw = 0; int teleCubeSwAtt = 0; int teleCubeSc = 0; int teleCubeScAtt = 0;
        int teleCubeExch = 0; int teleOthrNUM = 0;  int teleOthrATT = 0; int telePanL2alNUM = 0; int telePanL3NUM = 0; int teleFloorNUM = 0; int teleTheirNUM = 0; int teleRandomNUM = 0;
        int climbH0= 0; int climbH1 = 0; int climbH2 = 0; int climbH3 = 0; int lift1Num = 0; int lift2Num = 0; int liftedNum = 0;
        int cargL1 = 0; int cargL2 = 0; int cargL3 =0; int TcargL1 = 0; int TcargL2 = 0; int TcargL3 = 0; int TpanL1 = 0; int TpanL2 = 0; int TpanL3 = 0;
        int numMatches = 0; int panL1 = 0; int panL2 = 0; int panL3 = 0; int dropped=0;
        boolean cube_pu =false;

//        Log.d(TAG, ">>>>>>> All_Matches " + All_Matches.size());
        for (int i = 0; i < All_Matches.size(); i++) {
            match_inst = All_Matches.get(i);      // Get instance of Match Data
//            Log.e(TAG, i + " ##### FOR   Q" + match_inst.getMatch() + "  Team=" + team);
            if (match_inst.getTeam_num().matches(team)) {
                numMatches++;
//                Log.w(TAG, "Team Match " + team);
                // New Match Data Object *** GLF 1/20/19
                dropped = dropped + match_inst.getSand_num_Dropped();
                // =================== Cargo ============
                if (match_inst.isSand_LeftRocket_LCarg1()) {
                    cargL1++;
                }
                if (match_inst.isSand_LeftRocket_LCarg2()) {
                    cargL2++;
                }
                if (match_inst.isSand_LeftRocket_LCarg3()) {
                    cargL3++;
                }
                if (match_inst.isSand_LeftRocket_RCarg1()) {
                    cargL1++;
                }
                if (match_inst.isSand_LeftRocket_RCarg2()) {
                    cargL2++;
                }
                if (match_inst.isSand_LeftRocket_RCarg3()) {
                    cargL3++;
                }
                if (match_inst.isSand_RghtRocket_LCarg1()) {
                    cargL1++;
                }
                if (match_inst.isSand_RghtRocket_LCarg2()) {
                    cargL2++;
                }
                if (match_inst.isSand_RghtRocket_LCarg3()) {
                    cargL3++;
                }
                if (match_inst.isSand_RghtRocket_RCarg1()) {
                    cargL1++;
                }
                if (match_inst.isSand_RghtRocket_RCarg2()) {
                    cargL2++;
                }
                if (match_inst.isSand_RghtRocket_RCarg3()) {
                    cargL3++;
                }
                if (match_inst.isSand_CargoLCarg1()) {              // Cargo Ship
                    cargL1++;
                }
                if (match_inst.isSand_CargoLCarg2()) {
                    cargL1++;
                }
                if (match_inst.isSand_CargoLCarg3()) {
                    cargL1++;
                }
                if (match_inst.isSand_CargoRCarg1()) {
                    cargL1++;
                }
                if (match_inst.isSand_CargoRCarg2()) {
                    cargL1++;
                }
                if (match_inst.isSand_CargoRCarg3()) {
                    cargL1++;
                }
                if (match_inst.isSand_CargoEndLCargo()) {      // End
                    cargL1++;
                }
                if (match_inst.isSand_CargoEndRCargo()) {      // End
                    cargL1++;
                }
                // =================== Panels ============
                if (match_inst.isSand_LeftRocket_LPan1()) {
                    panL1++;
                }
                if (match_inst.isSand_LeftRocket_LPan2()) {
                    panL2++;
                }
                if (match_inst.isSand_LeftRocket_LPan3()) {
                    panL3++;
                }
                if (match_inst.isSand_LeftRocket_RPan1()) {
                    panL1++;
                }
                if (match_inst.isSand_LeftRocket_RPan2()) {
                    panL2++;
                }
                if (match_inst.isSand_LeftRocket_RPan3()) {
                    panL3++;
                }
                if (match_inst.isSand_RghtRocket_LPan1()) {
                    panL1++;
                }
                if (match_inst.isSand_RghtRocket_LPan2()) {
                    panL2++;
                }
                if (match_inst.isSand_RghtRocket_LPan3()) {
                    panL3++;
                }
                if (match_inst.isSand_RghtRocket_RPan1()) {
                    panL1++;
                }
                if (match_inst.isSand_RghtRocket_RPan2()) {
                    panL2++;
                }
                if (match_inst.isSand_RghtRocket_RPan3()) {
                    panL3++;
                }
                if (match_inst.isSand_CargoLPan1()) {              // Cargo Ship
                    panL1++;
                }
                if (match_inst.isSand_CargoLPan2()) {
                    panL1++;
                }
                if (match_inst.isSand_CargoLPan3()) {
                    panL1++;
                }
                if (match_inst.isSand_CargoRPan1()) {
                    panL1++;
                }
                if (match_inst.isSand_CargoRPan2()) {
                    panL1++;
                }
                if (match_inst.isSand_CargoRPan3()) {
                    panL1++;
                }
                if (match_inst.isSand_CargoEndLPanel()) {      // End
                    panL1++;
                }
                if (match_inst.isSand_CargoEndRPanel()) {      // End
                    panL1++;
                }


                // *************************************************
                // ******************** TeleOps ********************
                // *************************************************
                dropped = dropped + match_inst.getTele_num_Dropped();
                // =================== Cargo ============
                if (match_inst.isTele_LeftRocket_LCarg1()) {
                    TcargL1++;
                }
                if (match_inst.isTele_LeftRocket_LCarg2()) {
                    TcargL2++;
                }
                if (match_inst.isTele_LeftRocket_LCarg3()) {
                    TcargL3++;
                }
                if (match_inst.isTele_LeftRocket_RCarg1()) {
                    TcargL1++;
                }
                if (match_inst.isTele_LeftRocket_RCarg2()) {
                    TcargL2++;
                }
                if (match_inst.isTele_LeftRocket_RCarg3()) {
                    TcargL3++;
                }
                if (match_inst.isTele_RghtRocket_LCarg1()) {
                    TcargL1++;
                }
                if (match_inst.isTele_RghtRocket_LCarg2()) {
                    TcargL2++;
                }
                if (match_inst.isTele_RghtRocket_LCarg3()) {
                    TcargL3++;
                }
                if (match_inst.isTele_RghtRocket_RCarg1()) {
                    TcargL1++;
                }
                if (match_inst.isTele_RghtRocket_RCarg2()) {
                    TcargL2++;
                }
                if (match_inst.isTele_RghtRocket_RCarg3()) {
                    TcargL3++;
                }
                if (match_inst.isTele_CargoLCarg1()) {              // Cargo Ship
                    TcargL1++;
                }
                if (match_inst.isTele_CargoLCarg2()) {
                    TcargL1++;
                }
                if (match_inst.isTele_CargoLCarg3()) {
                    TcargL1++;
                }
                if (match_inst.isTele_CargoRCarg1()) {
                    TcargL1++;
                }
                if (match_inst.isTele_CargoRCarg2()) {
                    TcargL1++;
                }
                if (match_inst.isTele_CargoRCarg3()) {
                    TcargL1++;
                }
                if (match_inst.isTele_CargoEndLCargo()) {      // End
                    TcargL1++;
                }
                if (match_inst.isTele_CargoEndRCargo()) {      // End
                    TcargL1++;
                }
                // =================== Panels ============
                if (match_inst.isTele_LeftRocket_LPan1()) {
                    TpanL1++;
                }
                if (match_inst.isTele_LeftRocket_LPan2()) {
                    TpanL2++;
                }
                if (match_inst.isTele_LeftRocket_LPan3()) {
                    TpanL3++;
                }
                if (match_inst.isTele_LeftRocket_RPan1()) {
                    TpanL1++;
                }
                if (match_inst.isTele_LeftRocket_RPan2()) {
                    TpanL2++;
                }
                if (match_inst.isTele_LeftRocket_RPan3()) {
                    TpanL3++;
                }
                if (match_inst.isTele_RghtRocket_LPan1()) {
                    TpanL1++;
                }
                if (match_inst.isTele_RghtRocket_LPan2()) {
                    TpanL2++;
                }
                if (match_inst.isTele_RghtRocket_LPan3()) {
                    TpanL3++;
                }
                if (match_inst.isTele_RghtRocket_RPan1()) {
                    TpanL1++;
                }
                if (match_inst.isTele_RghtRocket_RPan2()) {
                    TpanL2++;
                }
                if (match_inst.isTele_RghtRocket_RPan3()) {
                    TpanL3++;
                }
                if (match_inst.isTele_CargoLPan1()) {              // Cargo Ship
                    TpanL1++;
                }
                if (match_inst.isTele_CargoLPan2()) {
                    TpanL1++;
                }
                if (match_inst.isTele_CargoLPan3()) {
                    TpanL1++;
                }
                if (match_inst.isTele_CargoRPan1()) {
                    TpanL1++;
                }
                if (match_inst.isTele_CargoRPan2()) {
                    TpanL1++;
                }
                if (match_inst.isTele_CargoRPan3()) {
                    TpanL1++;
                }
                if (match_inst.isTele_CargoEndLPanel()) {      // End
                    TpanL1++;
                }
                if (match_inst.isTele_CargoEndRPanel()) {      // End
                    TpanL1++;
                }

                int endHAB = match_inst.getTele_level_num();        // end HAB Level
                switch (endHAB) {
                    case 0:         // Not On
                        climbH0++;
                        break;
                    case 1:         // Level 1
                        climbH1++;
                        break;
                    case 2:         // Level 2
                        climbH2++;
                        break;
                    case 3:         // Level 3
                        climbH3++;
                        break;
                    default:                // ????
                        e(TAG, "*** Error - bad HAB Level indicator  ***");
                }

                if (match_inst.isTele_lifted()) {
                    lift1Num++;
                }
// ToDo - Lift 2
                //                if (match_inst.isTele_lift_two()) {
//                    lift2Num++;
//                }
                if (match_inst.isTele_got_lift()) {
                    liftedNum++;
                }
//                Log.w(TAG, "Accum. matches = " + numMatches);
            } //End if teams equal
        } // End For _ALL_ matches
//        Log.w(TAG, "####### Total Matches/Team = " + numMatches);
        mdNumMatches = String.valueOf(numMatches);
        if (numMatches < minMatches) {
            minMatches = numMatches;
            Log.e(TAG, team + " >>>>>>>>>>  Min. matches changed = " + minMatches);
        }
        if (numMatches > 0) {
            if (cube_pu) {
                cubeChk = "❎";
            } else {
                cubeChk = "⎕";
            }
            sandCargL1 = String.valueOf(cargL1);
            sandCargL2 = String.valueOf(cargL2);
            sandCargL3 = String.valueOf(cargL3);
            sandPanelL1 = String.valueOf(panL1);
            sandPanelL2 = String.valueOf(panL2); 
            sandPanelL3 = String.valueOf(panL3);
            teleCargoL1 = String.valueOf(TcargL1);
            teleCargoL2 = String.valueOf(TcargL2);
            teleCargoL3 = String.valueOf(TcargL3);
            telePanL1 = String.valueOf(TpanL1);
            telePanL2 = String.valueOf(TpanL2);
            telePanL3 = String.valueOf(TpanL3);
            panDropped = String.valueOf(dropped);
            climb_HAB0 = String.valueOf(climbH0);
            climb_HAB1 = String.valueOf(climbH1);
            climb_HAB2 = String.valueOf(climbH2);
            climb_HAB3 = String.valueOf(climbH3);
            liftOne = String.valueOf(lift1Num);
            liftTwo = String.valueOf(lift2Num);
            climb_HAB0 = String.valueOf(climbH0);
            gotLifted = String.valueOf(liftedNum);
        } else {
            cubeChk = "⎕";
            sandCargL1 = "0";
            sandCargL2 = "0";
            sandCargL3 = "0";
            sandPanelL1 = "0";
            sandPanelL2 = "0";
            sandPanelL3 = "0";
            teleCargoL1 = "0";
            teleCargoL2 = "0";
            teleCargoL3 = "0";
            telePanL1 = "0";
            telePanL2 = "0";
            telePanL3  = "0";
            panDropped = "0";
            climb_HAB0 = "0"; climb_HAB1 = "0"; climb_HAB2 = "0"; climb_HAB3 = "0";
            liftOne = "0";
            liftTwo = "0";
//            onPlatform = "0";
            gotLifted = "0";
        }
        //============================
        float climbScore = 0; float cubeScored = 0; float cubeCollect = 0; float cargoScore = 0; float combinedScore = 0; float switchScore = 0; float scaleScore = 0; float panelsScore = 0;
//        Log.e(TAG, team + " "+ climbs + " "+ lift1Num + " "+ lift2Num + " " + platNum +  " " + liftedNum + " / " + numMatches);
        if (numMatches > 0) {
            climbScore = ((climbH1 * Float.parseFloat(climbHAB1) + climbH2 * Float.parseFloat(climbHAB2) + climbH3 * Float.parseFloat(climbHAB3)  + climbH0 * Float.parseFloat(climbHAB0)) + (lift1Num * Float.parseFloat(climbLift1)) + (lift2Num * Float.parseFloat(climbLift2)) + (liftedNum * Float.parseFloat(climbLifted))) / numMatches;
            cargoScore = (((cargL1+TcargL1) * Float.parseFloat(cargo_L1)) + ((cargL2+TcargL2) * Float.parseFloat(cargo_L2)) + ((cargL3+TcargL3) * Float.parseFloat(cargo_L3)))  / numMatches;
//            Log.e(TAG, "@@@" + team + "  1="+ (cargL1+TcargL1) + " 2="+ (cargL2+TcargL2) + " 3="+ (cargL3+TcargL3) + " TOTAL=" + cargoScore);
            panelsScore = (((panL1+TpanL1) * Float.parseFloat(panels_L1)) + ((panL2+TpanL2) * Float.parseFloat(panels_L2)) + ((panL3+TpanL3) * Float.parseFloat(panels_L3)) + (dropped * Float.parseFloat(panels_Drop))) / numMatches;
            combinedScore = (((climbScore * Float.parseFloat(wtClimb) + (cargoScore * Float.parseFloat(wtCargo)) + (panelsScore * Float.parseFloat(wtPanels)))) / numMatches);
        } else {
            climbScore = 0;
            cargoScore = 0;
            combinedScore = 0;
            panelsScore = 0;
        }
        String tNumber="";
        for (int i = 0; i < team_Scores.size(); i++) {    // load by sorted scores
            Scores score_data = new Scores();
            score_data = team_Scores.get(i);
            tNumber = score_data.getTeamNum();
            if (score_data.getTeamNum().matches(team)) {
//                Log.w(TAG, "score team=" + score_data.getTeamNum());
                score_data.setSCORE_climbScore(climbScore);           // Save
                score_data.setSCORE_cargoScore(cargoScore);           //  all
                score_data.setSCORE_combinedScore(combinedScore);     //   scores
                score_data.setSCORE_panelsScore(panelsScore);         //
            }
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@ @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addMD_VE_Listener(final DatabaseReference pfMatchData_DBReference) {
        pfMatchData_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "<<<< getFB_Data >>>> _ALL_ Match Data ");
                ImageView imgStat_Load = findViewById(R.id.imgStat_Load);
                RadioGroup radgrp_Sort = findViewById(R.id.radgrp_Sort);
                txt_LoadStatus = findViewById(R.id.txt_LoadStatus);
                All_Matches.clear();
                matchData mdobj = new matchData();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    mdobj = iterator.next().getValue(matchData.class);
                    All_Matches.add(mdobj);
                }
                Log.e(TAG, "addMD_VE *****  Matches Loaded. # = "  + All_Matches.size());
                Button btn_Match = findViewById(R.id.btn_Match);
                Button btn_Pit = findViewById(R.id.btn_Pit);
                imgStat_Load.setImageDrawable(getResources().getDrawable(R.drawable.traffic_light_green));
                txt_LoadStatus.setText(All_Matches.size() + " Matches");
                radio_Team = findViewById(R.id.radio_Team);
                radio_Team.performClick();         // "force" radio button click  (so it works 1st time)
                for(int i = 0; i < radgrp_Sort.getChildCount(); i++){        // turn them all ON
                    radgrp_Sort.getChildAt(i).setEnabled(true);
                }
                btn_Match.setEnabled(true);
                btn_Match.setVisibility(View.VISIBLE);
                btn_Pit.setEnabled(true);
                btn_Pit.setVisibility(View.VISIBLE);
                pbSpinner.setVisibility(View.INVISIBLE);
//                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });

        // ======================================================================================
        sortType = "Team#";          // Attempt to "force" correct sort 1st time
        Collections.sort(team_Scores, Scores.teamComp);
        loadTeams();    // load for 1st time in Team# order
    }

    private void initScores() {
        Log.i(TAG, " ## initScores ##  " + is_Resumed);
//        Log.d(TAG, "Start to Load team scores '"  + sortType + "'");
        team_Scores.clear();
        for (int i = 0; i < Pearadox.numTeams; i++) {
            Scores curScrTeam = new Scores();       // instance of Scores object
            team_inst = Pearadox.team_List.get(i);
            curScrTeam.setTeamNum(team_inst.getTeam_num());
            curScrTeam.setTeamName(team_inst.getTeam_name());
            curScrTeam.setScrRank(team_inst.getTeam_rank());
            curScrTeam.setScrRScore(team_inst.getTeam_rScore());
            curScrTeam.setScrWLT(team_inst.getTeam_WLT());
            curScrTeam.setScrOPR(team_inst.getTeam_OPR());
//            Log.w(TAG, curScrTeam.getTeamNum() + "  " + curScrTeam.getTeamName());
            curScrTeam.setSCORE_climbScore((float) 0);        //Climb
            curScrTeam.setSCORE_cargoScore((float) 0);        // Cargo
            curScrTeam.setSCORE_combinedScore((float) 0);     // Combined
            curScrTeam.setSCORE_panelsScore((float) 0);       // Panels
            team_Scores.add(i, curScrTeam);
        } // end For
        Log.w(TAG, "#Scores = " + team_Scores.size());
        if (sortType.matches("") || sortType.matches("Team#")) {       // if 1st time
            sortType = "Team#";
        } else {
//            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//            String sortType = prefs.getString("Sort", "");
//
            // ToDONE - Load teams according to Radio Button (VisMatch return messes it up)
            Log.e(TAG, "Leave scores alone '"  + sortType + "'");
            radgrp_Sort = findViewById(R.id.radgrp_Sort);
            radgrp_Sort.setActivated(true);
            radgrp_Sort.setSelected(true);
            switch (sortType) {
                case "Climb":
                    radio_Climb = findViewById(R.id.radio_Climb);
                    radio_Climb.performClick();         // "force" radio button click
                    break;
                case "Cargo":
                    radio_Cargo = findViewById(R.id.radio_Cargo);
                    radio_Cargo.performClick();         // "force" radio button click
                    break;
                case "Combined":
                    radio_Weight = findViewById(R.id.radio_Weight);
                    radio_Weight.performClick();         // "force" radio button click
                    break;
                case "Panels":
                    radio_Panels = findViewById(R.id.radio_Panels);
                    radio_Panels.performClick();         // "force" radio button click
                    break;
                default:                //
                    Log.e(TAG, "*** Invalid Type " + sortType);
            }
        }
    }

//###################################################################
//###################################################################
//###################################################################
@Override
public void onStart() {
    super.onStart();
    Log.v(TAG, "onStart");
    initScores();
    addMD_VE_Listener(pfMatchData_DBReference);        // Load _ALL_ Matches
    }


@Override
public void onResume() {
    super.onResume();
    Log.v(TAG, "****> onResume <**** " + sortType);
    is_Resumed = true;
    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
    String sortType = prefs.getString("Sort", "");
    Log.d(TAG, "Resume Prefs >> " + sortType);
//    initScores();           // make sure it sorts by _LAST_ radio button
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause  "  + sortType);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("Sort", sortType);
        editor.commit();        // keep sort type
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

//