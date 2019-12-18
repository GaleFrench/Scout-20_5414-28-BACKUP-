package com.pearadox.scout_5414;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.PendingIntent.getActivity;
import static android.view.View.VISIBLE;

public class PitScoutActivity extends AppCompatActivity {

    String TAG = "PitScout_Activity";      // This CLASS name
    TextView txt_EventName, txt_dev, txt_stud, txt_TeamName, txt_NumWheels;
    EditText editTxt_Team, txtEd_Height, editText_Comments, txtEd_Speed;
    ImageView imgScoutLogo, img_Photo;
    Spinner spinner_Team, spinner_Traction, spinner_Omni, spinner_Mecanum, spinner_Pneumatic;
    Spinner spinner_numRobots, spinner_Motor, spinner_Lang, spinner_ssMode;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_Trac, adapter_Omni, adapter_Mac, adapter_Pneu ;
    ArrayAdapter<String> adapter_driveMotor, adapter_progLang,adapter_ssMode;
    CheckBox chkBox_Ramp, chkBox_CanLift, chkBox_Hook, chkBox_Vision, chkBox_Pneumatics, chkBox_Climb, chkBox_Belt, chkBox_Box, chkBox_Other;
    CheckBox chkBox_OffFloor, chkBox_PanelFloor, chkBox_Hab2, chkBox_HABLvl_2, chkBox_HABLvl_3;

    Button btn_Save;
    Uri currentImageUri;
    String currentImagePath;
    String picname;
    int REQUEST_IMAGE_CAPTURE = 2;
    public static String[] teams = new String[Pearadox.numTeams+1];  // Team list (array of just Team Names)
    public static String[] wheels = new String[]
            {"0","1","2","3","4","5","6", "7", "8"};
    public static String[] carry = new String[]             // Num. of robots this robot can lift
            {" ","1","2"};

    String team_num, team_name, team_loc;
    p_Firebase.teamsObj team_inst = new p_Firebase.teamsObj();
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfPitData_DBReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    String pitPlace = "";  Boolean pitSD = false;   Boolean pitFB = false;
    String URL = "";
    public static String timeStamp = " ";
    Boolean imageOnFB = false;      // Does image already exist in Firebase
    boolean dataSaved = false;      // Make sure they save before exiting
    public Boolean Wt_entered = false;      // Weight entered

    // ===================  Data Elements for Pit Scout object ===================
    public String teamSelected = " ";               // Team #
    public int tall = 0;                        // Weight (lbs)
    public int totalWheels = 0;                 // Total # of wheels
    public int numTraction = 0;                 // Num. of Traction wheels
    public int numOmnis = 0;                    // Num. of Omni wheels
    public int numMecanums = 0;                 // Num. of Mecanum wheels
    public int numPneumatic = 0;                // Num. of Pneumatic wheels
    public boolean vision = false;              // presence of Vision Camera
    public boolean pneumatics = false;          // presence of Pneumatics
    public boolean cargoManip = false;          // presence of a way to pick up cargo from floor
    public boolean climb = false;               // presence of a Climbing mechanism
    public boolean floorPanel = false;          // can get Hatch Panel from floor
    public boolean canLift = false;             // Ability to lift other robots
    public int numLifted = 0;                   // Num. of robots can lift (1-2)
    public boolean liftRamp = false;            // lift type Ramp
    public boolean liftHook = false;            // lift type Hook
    public boolean leaveHAB2 = false;           // Can leave from HAB level 2
    public boolean endHAB2 = false;             // Can climb to HAB level 2
    public boolean endHAB3 = false;             // Can climb to HAB level 3
    public int speed = 0;                       // Speed (Ft. per Sec)
    public String motor;                        // Type of Motor
    public String lang;                         // Programming  Language
    public String ssMode;                       // Sandstorm Operatong Mode
    /* */
    public String comments;                     // Comment(s)
    public String scout = " ";                  // Student who collected the data
    private String  final_dateTime;             // Date & Time data was saved
    public String photoURL = "";                // URL of the robot photo in Firebase

// ===========================================================================
pitData Pit_Data = new pitData();
    pitData Pit_Load = new pitData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scout);
        Log.w(TAG, "<< Pit Scout >>");
        Bundle bundle = this.getIntent().getExtras();
        String param1 = bundle.getString("dev");
        String param2 = bundle.getString("stud");
        Log.w(TAG, param1 + " " + param2);     // ** DEBUG **
        scout = param2;                         // Scout of record

        txt_EventName = (TextView) findViewById(R.id.txt_EventName);
        txt_EventName.setText(Pearadox.FRC_EventName);          // Event Name
        pfDatabase = FirebaseDatabase.getInstance();
        pfPitData_DBReference = pfDatabase.getReference("pit-data/" + Pearadox.FRC_Event); // Pit Scout Data
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ImageView img_Photo = (ImageView) findViewById(R.id.img_Photo);
        txt_dev = (TextView) findViewById(R.id.txt_Dev);
        txt_stud = (TextView) findViewById(R.id.txt_stud);
        txt_TeamName = (TextView) findViewById(R.id.txt_TeamName);
        txt_NumWheels = (TextView) findViewById(R.id.txt_NumWheels);
        txt_dev.setText(param1);
        txt_stud.setText(param2);
        txt_TeamName.setText(" ");
        txtEd_Height = (EditText) findViewById(R.id.txtEd_Height);
        Spinner spinner_Team = (Spinner) findViewById(R.id.spinner_Team);
        editTxt_Team = (EditText) findViewById(R.id.editTxt_Team);
        if (Pearadox.is_Network && Pearadox.numTeams > 0) {      // is Internet available & Teams present?
            loadTeams();
            txtEd_Height.setEnabled(false);
            spinner_Team.setVisibility(View.VISIBLE);
            spinner_Team.setFocusable(true);
            spinner_Team.requestFocus();
            spinner_Team.requestFocusFromTouch();       // make team selection focus
            editTxt_Team.setVisibility(View.GONE);
            adapter = new ArrayAdapter<String>(this, R.layout.team_list_layout, teams);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Team.setAdapter(adapter);
            spinner_Team.setSelection(0, false);
            spinner_Team.setOnItemSelectedListener(new team_OnItemSelectedListener());
        } else {        // Have the user type in Team #
            editTxt_Team.setText("");
            editTxt_Team.setVisibility(View.VISIBLE);
            editTxt_Team.setEnabled(true);
            spinner_Team.setVisibility(View.GONE);
            editTxt_Team.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.w(TAG, " editTxt_Team listener; Team = " + editTxt_Team.getText());
                    if (editTxt_Team.getText().length() < 3 || editTxt_Team.getText().length() > 4) {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                        Toast.makeText(getBaseContext(),"*** Team number must be at least 3 characters and no more than 4  *** ", Toast.LENGTH_LONG).show();
                    } else {
                        teamSelected = (String.valueOf(editTxt_Team.getText()));
                        chkForPhoto(teamSelected);      // see if photo already exists
                        return true;
                    }
                }
                return false;
                }
            });
        }

        final Spinner spinner_numRobots = (Spinner) findViewById(R.id.spinner_numRobots);
        ArrayAdapter adapter_Robs = new ArrayAdapter<String>(this, R.layout.robonum_list_layout, carry);
        adapter_Robs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_numRobots.setAdapter(adapter_Robs);
        spinner_numRobots.setSelection(0, false);
        spinner_numRobots.setOnItemSelectedListener(new numRobots_OnItemSelectedListener());
        spinner_numRobots.setVisibility(View.GONE);
        Spinner spinner_Traction = (Spinner) findViewById(R.id.spinner_Traction);
        ArrayAdapter adapter_Trac = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wheels);
        adapter_Trac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Traction.setAdapter(adapter_Trac);
        spinner_Traction.setSelection(0, false);
        spinner_Traction.setOnItemSelectedListener(new Traction_OnItemSelectedListener());
        Spinner spinner_Omni = (Spinner) findViewById(R.id.spinner_Omni);
        ArrayAdapter adapter_Omni = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wheels);
        adapter_Omni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Omni.setAdapter(adapter_Trac);
        spinner_Omni.setSelection(0, false);
        spinner_Omni.setOnItemSelectedListener(new Omni_OnItemSelectedListener());
        Spinner spinner_Mecanum = (Spinner) findViewById(R.id.spinner_Mecanum);
        ArrayAdapter adapter_Mac = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wheels);
        adapter_Mac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Mecanum.setAdapter(adapter_Mac);
        spinner_Mecanum.setSelection(0, false);
        spinner_Mecanum.setOnItemSelectedListener(new Mecanum_OnItemSelectedListener());
        adapter_Mac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_Pneumatic = (Spinner) findViewById(R.id.spinner_Pneumatic);
        ArrayAdapter adapter_Pneu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wheels);
        adapter_Mac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Pneumatic.setAdapter(adapter_Pneu);
        spinner_Pneumatic.setSelection(0, false);
        spinner_Pneumatic.setOnItemSelectedListener(new Pneumatic_OnItemSelectedListener());
        spinner_Motor = (Spinner) findViewById(R.id.spinner_Motor);
        String[] driveMotor = getResources().getStringArray(R.array.drive_motor_array);
        adapter_driveMotor = new ArrayAdapter<String>(this, R.layout.dev_list_layout, driveMotor);
        adapter_driveMotor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Motor.setAdapter(adapter_driveMotor);
        spinner_Motor.setSelection(0, false);
        spinner_Motor.setOnItemSelectedListener(new driveMotorOnClickListener());
        spinner_Lang = (Spinner) findViewById(R.id.spinner_Lang);
        String[] progLang = getResources().getStringArray(R.array.prog_lang_array);
        adapter_progLang = new ArrayAdapter<String>(this, R.layout.dev_list_layout, progLang);
        adapter_progLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Lang.setAdapter(adapter_progLang);
        spinner_Lang.setSelection(0, false);
        spinner_Lang.setOnItemSelectedListener(new progLangOnClickListener());
        spinner_ssMode = (Spinner) findViewById(R.id.spinner_ssMode);
        String[] operMode = getResources().getStringArray(R.array.ss_Mode_array);
        adapter_ssMode = new ArrayAdapter<String>(this, R.layout.dev_list_layout, operMode);
        adapter_ssMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ssMode.setAdapter(adapter_ssMode);
        spinner_ssMode.setSelection(0, false);
        spinner_ssMode.setOnItemSelectedListener(new ssModeOnClickListener());
        chkBox_Ramp = (CheckBox) findViewById(R.id.chkBox_Ramp);
        chkBox_Hook = (CheckBox) findViewById(R.id.chkBox_Hook);
        chkBox_Ramp.setVisibility(View.GONE);
        chkBox_Hook.setVisibility(View.GONE);
        chkBox_Vision = (CheckBox) findViewById(R.id.chkBox_Vision);
        chkBox_Pneumatics = (CheckBox) findViewById(R.id.chkBox_Pneumatics);
        chkBox_CanLift = (CheckBox) findViewById(R.id.chkBox_CanLift);
        chkBox_OffFloor = (CheckBox) findViewById(R.id.chkBox_OffFloor);
        chkBox_Climb = (CheckBox) findViewById(R.id.chkBox_Climb);
//        chkBox_Belt = (CheckBox) findViewById(R.id.chkBox_Belt);
//        chkBox_Box = (CheckBox) findViewById(R.id.chkBox_Box);
//        chkBox_Other = (CheckBox) findViewById(R.id.chkBox_Other);
        chkBox_PanelFloor = (CheckBox) findViewById(R.id.chkBox_PanelFloor);
        chkBox_Hab2 = (CheckBox) findViewById(R.id.chkBox_Hab2);
        chkBox_HABLvl_2 = (CheckBox) findViewById(R.id.chkBox_HABLvl_2);
        chkBox_HABLvl_3 = (CheckBox) findViewById(R.id.chkBox_HABLvl_3);
        editText_Comments = (EditText) findViewById(R.id.editText_Comments);
        editText_Comments.setClickable(true);
        txtEd_Speed = (EditText) findViewById(R.id.txtEd_Speed);


//        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 200);
//        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        Toast toast = Toast.makeText(getBaseContext(), " \n *** Select a TEAM first before entering data *** \n", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        timeStamp = new SimpleDateFormat("yyyy.MM.dd  hh:mm:ss a").format(new Date());

//===============================================================================================================
        chkBox_Ramp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_Ramp Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"Ramp is checked.");
                    liftRamp = true;
                } else {
                    Log.w(TAG,"Ramp is unchecked.");
                    liftRamp = false;
                }
            }
        });
        chkBox_CanLift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               Log.w(TAG, "chkBox_CanLift Listener");
               if (buttonView.isChecked()) {
                   Log.w(TAG,"Lift is checked.");
                   canLift = true;
                   chkBox_Ramp.setVisibility(VISIBLE);
                   chkBox_Hook.setVisibility(VISIBLE);
                   spinner_numRobots.setVisibility(VISIBLE);
               } else {
                   Log.w(TAG,"Lift is unchecked.");
                   canLift = false;
                   chkBox_Ramp.setVisibility(View.GONE);
                   chkBox_Hook.setVisibility(View.GONE);
                   spinner_numRobots.setVisibility(View.GONE);
               }
           }
        });
        chkBox_Hook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               Log.w(TAG, "chkBox_CanLift Listener");
               if (buttonView.isChecked()) {
                   Log.w(TAG,"Hook is checked.");
                   liftHook = true;
               } else {
                   Log.w(TAG,"Hook is unchecked.");
                   liftHook = false;
               }
           }
        });
        chkBox_Vision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               Log.w(TAG, "chkBox_Vision Listener");
               if (buttonView.isChecked()) {
                   Log.w(TAG,"Vision is checked.");
                   vision = true;
               } else {
                   Log.w(TAG,"Vision is unchecked.");
                   vision = false;
               }
           }
       });
        chkBox_Pneumatics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_Pneumatics Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"Pneumatics is checked.");
                    pneumatics = true;
                } else {
                    Log.w(TAG,"Pneumatics is unchecked.");
                    pneumatics = false;
                }
            }
        });


        chkBox_OffFloor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                            Log.w(TAG, "chkBox_OffFloor Listener");
                            if (buttonView.isChecked()) {
                                Log.w(TAG,"Off-floor is checked.");
                                cargoManip = true;
                            } else {
                                Log.w(TAG,"Off-floor is unchecked.");
                                cargoManip = false;
                }
            }
        });

        chkBox_PanelFloor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_PanelFloor Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"floorPanel is checked.");
                    floorPanel = true;
                } else {
                    Log.w(TAG,"floorPanel is unchecked.");
                    floorPanel = false;
                }
            }
        });

        chkBox_Hab2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_Hab2 Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"leave_Hab2 is checked.");
                    leaveHAB2 = true;
                } else {
                    Log.w(TAG,"leave_Hab2 is unchecked.");
                    leaveHAB2 = false;
                }
            }
        });

        chkBox_HABLvl_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_HABLvl_2 Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"end_HABLvl_2 is checked.");
                    endHAB2 = true;
                } else {
                    Log.w(TAG,"end_HABLvl_2 is unchecked.");
                    endHAB2 = false;
                }
            }
        });

        chkBox_HABLvl_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_HABLvl_3 Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"end_Hab3 is checked.");
                    endHAB3 = true;
                } else {
                    Log.w(TAG,"end_Hab3 is unchecked.");
                    endHAB3 = false;
                }
            }
        });

        chkBox_Climb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                Log.w(TAG, "chkBox_Climb Listener");
                if (buttonView.isChecked()) {
                    Log.w(TAG,"Climb is checked.");
                    climb = true;
                } else {
                    Log.w(TAG,"Climb is unchecked.");
                    climb = false;
                }
            }
        });




//=================================================================
        editText_Comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.w(TAG, "******  onTextChanged TextWatcher  ******" + s);
                comments = String.valueOf(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.w(TAG, "******  beforeTextChanged TextWatcher  ******");
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.w(TAG, "******  onTextChanged TextWatcher  ******" + s );
                comments = String.valueOf(s);
            }
        });

        txtEd_Height.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.w(TAG, "******  txtEd_Height listener  ******  " + keyCode + "  " + event.getAction());

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.w(TAG, " txtEd_Weight = "  + txtEd_Height.getText());

                    if (txtEd_Height.getText().length() > 0) {
                        tall = Integer.valueOf(String.valueOf(txtEd_Height.getText()));     //REALLY Weight  GLF 3/2020
                        Wt_entered = true;
                        Log.w(TAG, "### Used the right key!!  ### " + Wt_entered);
                        return true;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                        Toast toast = Toast.makeText(getBaseContext(), " \n*****  Enter a valid Weight!  *****\n ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                }
                return false;
            }
        });

        txtEd_Height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.w(TAG, "@@@ Height - Lost Focus Listener @@@  '" + txtEd_Height.getText() +"' " + tall +"  " + Wt_entered);
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    if (!Wt_entered) {
                        Toast toast = Toast.makeText(getBaseContext(), "\n*** Please use the > key and NOT the ▽ key ***\n                Please re-enter Weight", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                }
            }
        });


        txtEd_Speed.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.w(TAG, "******  txtEd_Speed listener  ******  " + keyCode + "  " + event.getAction());

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.w(TAG, "Speed = " + txtEd_Speed.getText());
                    if (txtEd_Speed.getText().length() > 0) {
                        speed = Integer.valueOf(String.valueOf(txtEd_Speed.getText()));
                        return true;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                        Toast toast = Toast.makeText(getBaseContext(), " \n*****  Enter a valid Speed  *****\n ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                }
                return false;
            }
        });

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Save = (Button) findViewById(R.id.btn_Save);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "Save Button Listener");
                if ((txtEd_Height.length() > 0 && totalWheels >= 4) && (spinner_ssMode.getSelectedItemPosition() > 0) && (spinner_Lang.getSelectedItemPosition() > 0)) {        // required

                    Spinner spinner_Team = (Spinner) findViewById(R.id.spinner_Team);
                    storePitData();           // Put all the Pit data collected in Pit object
                    dataSaved = true;
                    if (Pearadox.is_Network) {      // is Internet available?
                        spinner_Team.setSelection(0);       //Reset to NO selection
                        txt_TeamName.setText(" ");
                    }
                    finish();       // Exit  <<<<<<<<
                } else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    Toast toast = Toast.makeText(getBaseContext(), "*** Enter _ALL_ data (Weight & Wheels) before saving ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                onLaunchCamera();       // Start Camera
            default:
                break;
        }
        return false;
    }

    private void onLaunchCamera() {
        Log.w(TAG, "►►►►►  LaunchCamera  ◄◄◄◄◄");
        if (teamSelected.length() < 3) {        /// Make sure a Team is selected 1st
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            Toast toast = Toast.makeText(getBaseContext(), "*** Select a TEAM first before taking photo ***", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else {
            File dirPhotos = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/images/" + Pearadox.FRC_Event + "/");
            currentImagePath = String.valueOf(dirPhotos);
            picname = "robot_" + teamSelected.trim() + ".png";
            File x = new File (dirPhotos, picname);
            currentImageUri = Uri.fromFile(x);
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri); // set the image file name
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            String picname = "robot_" + teamSelected.trim() + ".png";
//            File dirPhotos = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/pit/" + Pearadox.FRC_Event + "/");
//            Log.w(TAG, "SD card Path = " + dirPhotos);
//            dirPhotos = new File(dirPhotos, picname);
//            Log.w(TAG, "File = " + dirPhotos);
//            Uri outputFileUri = Uri.fromFile(dirPhotos);
//
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            takePictureIntent.PutExtra (MediaStore.ExtraOutput, outputFileUri);
//            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.w(TAG, "Photo taken");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "*****  onActivityResult " + requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == PitScoutActivity.RESULT_OK) {
            Log.w(TAG, "requestCode = '" + requestCode + "'");
            galleryAddPic();
            File savedFile;
            if(currentImagePath == null){
                savedFile= new File(currentImageUri.getPath());
            }else{
                savedFile = new File(currentImagePath);
            }

            String filename = "robot_" + teamSelected.trim() + ".png";
            File directPhotos = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/images/" + Pearadox.FRC_Event + "/" + filename);

            ImageView img_Photo = (ImageView) findViewById(R.id.img_Photo);
            Log.w(TAG, "@@@ PHOTO EXISTS LOCALLY @@@ ");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(directPhotos.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,img_Photo.getWidth(),img_Photo.getHeight(),true);
            img_Photo.setImageBitmap(bitmap);

            if (!imageOnFB) {
                SaveToFirebase(savedFile);
            }else{
                Log.w(TAG, "*** PHOTO EXISTS ON FIREBASE *** ");
            }
        }
    }

    private void SaveToFirebase(File savedFile) {
        Log.w(TAG, "$$$$$  SaveToFirebase  $$$$$" + savedFile);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2020.appspot.com/images/"+ Pearadox.FRC_Event).child(picname);

        UploadTask uploadTask = storageReference.putFile(currentImageUri);

        // Now get the URL
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
                Uri downloadURL = taskSnapshot.getUploadSessionUri();
                photoURL = downloadURL.toString();
                Log.e(TAG, "#####  URL=" + photoURL  + " \n");
            }
        });
    }

    private void galleryAddPic() {
        /**
         * copy current image to Gallery
         */
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(currentImageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        Log.w(TAG, "$$$$$  encodeBitmapAndSaveToFirebase  $$$$$");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String picname = "robot_" + teamSelected.trim() + ".png";
        Log.w(TAG, "Photo = '" + picname + "'");
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);     // ByteArrayOutputStream
        byte[] data = baos.toByteArray();
        //                  gs://paradox-2017.appspot.com/images/txZZ/
// ToDo - ????
//        StorageReference storageReference = storage.getReferenceFromUrl("gs://paradox-2017.appspot.com").child(picname);

//        UploadTask uploadTask = storageRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            }
//        });
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public class team_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Log.w(TAG, "*****  team_OnItemSelectedListener " + pos);
            teamSelected = parent.getItemAtPosition(pos).toString();
            Log.w(TAG, ">>>>>  '" + teamSelected + "'");
            txt_TeamName = (TextView) findViewById(R.id.txt_TeamName);
            findTeam(teamSelected);
            txt_TeamName.setText(team_inst.getTeam_name());
            txtEd_Height.setEnabled(true);

            chkForPhoto(teamSelected);              // see if photo already exists (SD card or Firebase)

            chkForData(teamSelected);               // see if data already exists (SD card or Firebase)

            // Check Firebase
            getTeam_Pit(teamSelected);

        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }


    // =============================================================================
    private void chkForData(String team) {
        Log.w(TAG, "*****  chkForData - team = " + team);
        pitPlace = ""; pitSD = false;  pitFB = false;
        // First check SD card
        String filename = team.trim() + ".dat";
        File directData = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/pit/" + Pearadox.FRC_Event + "/" + filename);
        Log.w(TAG, "SD card Path = " + directData);
        if(directData.exists())  {
            if (pitPlace == "") {           // let Firebase take precedent
                pitPlace = "SD card";
            }
            pitSD = true;
            Log.w(TAG, "**** in 'chkForData'.   Place = '" + pitPlace + "'  " + pitSD + " " + pitFB + " \n");
        }
    }

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void getTeam_Pit(String team) {
        Log.i(TAG, "$$$$$  getTeam_Pit  $$$$$  " + team);

        String child = "pit_team";
        String key = team;      // Removed .trim()       GLF 3/31/2017
        Log.w(TAG, "   Q U E R Y  " + child + "  '" + key + "' \n ");
        Query query = pfPitData_DBReference.orderByChild(child).equalTo(key);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.w(TAG, "%%%%%%%%%%%%  ChildAdded");
                pitPlace = "Firebase";
                pitFB = true;
                Log.w(TAG, "**** in 'getTeam_Pit'.   Place = '" + pitPlace + "'  " + pitSD + " " + pitFB + " \n");
                System.out.println(dataSnapshot.getValue());
                System.out.println("\n \n ");
                Pit_Load = dataSnapshot.getValue(pitData.class);
                System.out.println("Team: " + Pit_Load.getPit_team());
                System.out.println("Comment: " + Pit_Load.getPit_comment());
                System.out.println("\n \n ");

                Log.w(TAG, "Place = '" + pitPlace + "'  " + pitSD + " " + pitFB + " \n");
                if (pitSD || pitFB) {
                    diag();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.w(TAG, "%%%  ChildChanged");
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.w(TAG, "%%%  ChildRemoved");
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.w(TAG, "%%%  ChildMoved");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "%%%  DatabaseError");
            }
        });
        Log.w(TAG, "Place = '" + pitPlace + "'  " + pitSD + " " + pitFB + " \n");
        if (pitSD || pitFB) {
            diag();
        }
    }

    private void diag() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("★★★  Pit Data exists on " + pitPlace + ".  ★★★  \n  Do you want to use that data and make changes (or add photo)? ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        Spinner spinner_Traction = (Spinner) findViewById(R.id.spinner_Traction);
                        Spinner spinner_Omni = (Spinner) findViewById(R.id.spinner_Omni);
                        Spinner spinner_Mecanum = (Spinner) findViewById(R.id.spinner_Mecanum);
                        Spinner spinner_Pneumatic = (Spinner) findViewById(R.id.spinner_Pneumatic);
                        Spinner spinner_numRobots = (Spinner) findViewById(R.id.spinner_numRobots);

                        if (pitFB) {
                            // Already loaded data from Firebase in Pit_Load
                        } else {
                            File direct_pit = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/pit/" + Pearadox.FRC_Event);
                            try {
                                Log.w(TAG, "   Dir:" + direct_pit + "/" + teamSelected.trim() + ".dat");
                                InputStream file = new FileInputStream(direct_pit + "/" + teamSelected.trim() + ".dat");
                                InputStream buffer = new BufferedInputStream(file);
                                ObjectInput input = new ObjectInputStream(buffer);
                                Pit_Load = (pitData) input.readObject();
                                Log.w(TAG, "#### Obect '" + Pit_Load.getPit_team() + "'  " + Pit_Load.getPit_scout());
                            } catch (FileNotFoundException e) {
                                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        // Now load the screen & variables
                        teamSelected = Pit_Load.getPit_team();
                        //  Height _NOT_ coming back?  Scouters _MUST_ use > keyboard key and NOT Exit
                        txtEd_Height.setText(String.valueOf(Pit_Load.getPit_tall()));
                        tall = Integer.valueOf(String.valueOf(txtEd_Height.getText()));     // REALLY Weight  GLF 3/2020
                        txt_NumWheels.setText(String.valueOf(Pit_Load.getPit_totWheels()));
                        totalWheels = Pit_Load.getPit_totWheels();
                        spinner_Traction.setSelection((Pit_Load.getPit_numTrac()));
                        spinner_Omni.setSelection((Pit_Load.getPit_numOmni()));
                        spinner_Mecanum.setSelection((Pit_Load.getPit_numMecanum()));
                        spinner_Pneumatic.setSelection((Pit_Load.getPit_numPneumatic()));


                        chkBox_Climb.setChecked(Pit_Load.isPit_climb());
                        chkBox_Vision.setChecked(Pit_Load.isPit_vision());
                        chkBox_Pneumatics.setChecked(Pit_Load.isPit_pneumatics());
                        chkBox_OffFloor.setChecked(Pit_Load.isPit_cargoManip());
                        chkBox_PanelFloor.setChecked(Pit_Load.isPit_floorPanel());
                        chkBox_Hab2.setChecked(Pit_Load.isPit_leaveHAB2());
                        chkBox_HABLvl_2.setChecked(Pit_Load.isPit_endHAB2());
                        chkBox_HABLvl_3.setChecked(Pit_Load.isPit_endHAB3());

                        chkBox_OffFloor.setChecked(Pit_Load.isPit_floorCargo());
                        chkBox_PanelFloor.setChecked(Pit_Load.isPit_floorPanel());

                        chkBox_CanLift.setChecked(Pit_Load.isPit_canLift());
                        if (Pit_Load.isPit_canLift()) {
                            spinner_numRobots.setVisibility(View.VISIBLE);
                            spinner_numRobots.setSelection((Pit_Load.getPit_numLifted()));
                            chkBox_Hook.setVisibility(View.VISIBLE);
                            chkBox_Hook.setChecked(Pit_Load.isPit_liftHook());
                            chkBox_Ramp.setVisibility(View.VISIBLE);
                            chkBox_Ramp.setChecked(Pit_Load.isPit_liftRamp());
                        } else {
                            spinner_numRobots.setVisibility(View.INVISIBLE);
                            chkBox_Ramp.setVisibility(View.INVISIBLE);
                            chkBox_Hook.setVisibility(View.INVISIBLE);
                        }

                        String motr = Pit_Load.getPit_motor();
                        Log.w(TAG, "Motor = '" + motr + "'");
                        switch (motr) {
                            case ("CIM"):
                                spinner_Motor.setSelection(1);
                                break;
                            case ("Mini-CIM"):
                                spinner_Motor.setSelection(2);
                                break;
                            case ("775pro"):
                                spinner_Motor.setSelection(3);
                                break;
                            default:
                                Log.w(TAG, "►►►►►  E R R O R  ◄◄◄◄◄");
                                break;
                        }
                        String pLang = Pit_Load.getPit_lang();
                        Log.w(TAG, "Lauguage = '" + pLang + "'");
                        switch (pLang) {
                            case ("JAVA"):
                                spinner_Lang.setSelection(1);
                                break;
                            case ("C++"):
                                spinner_Lang.setSelection(2);
                                break;
                            case ("LabView"):
                                spinner_Lang.setSelection(3);
                                break;
                            default:
                                Log.w(TAG, "►►►►►  E R R O R  ◄◄◄◄◄");
                                break;
                        }
                        String mode = Pit_Load.getPit_ssMode();
                        Log.w(TAG, "Mode = '" + mode + "'");
                        switch (mode) {
                            case ("Program Only"):
                                spinner_ssMode.setSelection(1);
                                break;
                            case ("Vision Only"):
                                spinner_ssMode.setSelection(2);
                                break;
                            case ("Hybrid (P+V)"):
                                spinner_ssMode.setSelection(3);
                                break;
                            default:
                                Log.w(TAG, "►►►►►  E R R O R  ◄◄◄◄◄");
                                break;
                        }

                        //  Speed _NOT_ coming back?   Scouters _MUST_ use > keyboard key and NOT Exit
                        txtEd_Speed.setText(String.valueOf(Pit_Load.getPit_speed()));
                        // Finally ...
                        scout = scout + " & " + Pit_Load.getPit_scout();    // Append new scout name
                        editText_Comments.setText(Pit_Load.getPit_comment());
                        photoURL = Pit_Load.pit_photoURL;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // nothing
                    }
                })
                .show();

    }

// =============================================================================
    private void chkForPhoto(String team) {
        Log.w(TAG, "*****  chkForPhoto - team = " + team);

        // First check SD card
        String filename = "robot_" + team.trim() + ".png";
        File directPhotos = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/images/" + Pearadox.FRC_Event + "/" + filename);
        Log.w(TAG, "SD card Path = " + directPhotos);
        ImageView img_Photo = (ImageView) findViewById(R.id.img_Photo);
        if(directPhotos.exists())  {
            Log.w(TAG, "@@@ PHOTO EXISTS LOCALLY @@@ ");
//            Bitmap imageBitmap = BitmapFactory.decodeFile(directPhotos.getAbsolutePath());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(directPhotos.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,img_Photo.getWidth(),img_Photo.getHeight(),true);
            img_Photo.setImageBitmap(bitmap);

        } else {
//            if (Pearadox.is_Network) {      // is Internet available?   Commented out because 'tethered' show No internet
            Log.w(TAG, "### Checking on Firebase Images ### ");
//            }
            URL = "";
            img_Photo.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
            imageOnFB = false;

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2020.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
            Log.e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e(TAG, "  uri: " + uri.toString());
                    ImageView img_Photo = (ImageView) findViewById(R.id.img_Photo);
                    URL = uri.toString();
                    if (URL.length() > 0) {
                        Picasso.with(PitScoutActivity.this).load(URL).into(img_Photo);
                        photoURL = URL;     // save URL in Pit object
                        imageOnFB = true;
                    } else {
                        img_Photo.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
                        imageOnFB = false;
                    }
                }
            });
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    private class progLangOnClickListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            lang = parent.getItemAtPosition(pos).toString();
            Log.d(TAG, ">>>>> Language  '" + lang + "' " + pos);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
    private class ssModeOnClickListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            ssMode = parent.getItemAtPosition(pos).toString();
            Log.d(TAG, ">>>>> Oper.Mode  '" + ssMode + "' " + pos);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    private class driveMotorOnClickListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            motor = parent.getItemAtPosition(pos).toString();
            Log.d(TAG, ">>>>> Motor  '" + motor + "' " + pos);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    public class Traction_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            numTraction = Integer.parseInt(num);
            Log.w(TAG, ">>>>> Traction '" + numTraction + "'");
            updateNumWhls();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
    public class Omni_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            numOmnis = Integer.parseInt(num);
            Log.w(TAG, ">>>>> Omni '" + numOmnis + "'");
            updateNumWhls();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
    public class Mecanum_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            numMecanums = Integer.parseInt(num);
            Log.w(TAG, ">>>>> Mecanum '" + numMecanums + "'");
            updateNumWhls();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
    public class Pneumatic_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            numPneumatic = Integer.parseInt(num);
            Log.w(TAG, ">>>>> Pneumatic '" + numPneumatic + "'");
            updateNumWhls();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    private void updateNumWhls() {
        Log.w(TAG, "######  updateNumWhls ###### T-O-M = " + numTraction + numOmnis + numMecanums);
        int x = numTraction + numOmnis + numMecanums + numPneumatic;
        txt_NumWheels.setText(String.valueOf(x));      // Total # of wheels
        totalWheels = x;
        if (x < 4){
            Toast.makeText(getBaseContext(), "Robot should have at least 4 wheels", Toast.LENGTH_LONG).show();
        }
    }
    public class numRobots_OnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            numLifted = Integer.parseInt(num);
            Log.w(TAG, ">>>>> NumRobots '" + numLifted + "'");
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    private void findTeam(String tnum) {
        Log.w(TAG, "$$$$$  findTeam " + tnum);
        boolean found = false;
        for (int i = 0; i < Pearadox.numTeams; i++) {        // check each team entry
            if (Pearadox.team_List.get(i).getTeam_num().equals(tnum)) {
                team_inst = Pearadox.team_List.get(i);
//                Log.w(TAG, "===  Team " + team_inst.getTeam_num() + " " + team_inst.getTeam_name() + " " + team_inst.getTeam_loc());
                found = true;
                break;  // found it!
            }
        }  // end For
        if (!found) {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            Log.e(TAG, "****** ERROR - Team _NOT_ found!! = " + tnum);
            txt_TeamName.setText(" ");
        }
    }

    private void loadTeams() {
        Log.w(TAG, "$$$$$  loadTeams $$$$$");
        int tNum = 0;
        teams[0] = " ";     // Make the 1st one BLANK for dropdown
        tNum ++;
        for (int i = 0; i < Pearadox.numTeams; i++) {        // get each team entry
            team_inst = Pearadox.team_List.get(i);
            teams[i+1] = team_inst.getTeam_num();
            tNum ++;
        }  // end For
        Log.w(TAG, "# of teams = " + tNum);

    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void storePitData() {
        Log.w(TAG, ">>>>  storePitData  <<<< " + teamSelected );

        Pit_Data.setPit_team(teamSelected);
        Pit_Data.setPit_tall(tall);     // REALLY Weight  GLF 3/2020
        Pit_Data.setPit_totWheels(totalWheels);
        Pit_Data.setPit_numTrac(numTraction);
        Pit_Data.setPit_numOmni(numOmnis);
        Pit_Data.setPit_numMecanum(numMecanums);
        Pit_Data.setPit_numPneumatic(numPneumatic);
        Pit_Data.setPit_cargoManip(cargoManip);
        Pit_Data.setPit_floorPanel(floorPanel);
        Pit_Data.setPit_vision(vision);
        Pit_Data.setPit_pneumatics(pneumatics);
        Pit_Data.setPit_climb(climb);
        Pit_Data.setPit_canLift(canLift);
        Pit_Data.setPit_numLifted (numLifted );
        Pit_Data.setPit_liftRamp(liftRamp);
        Pit_Data.setPit_liftHook(liftHook);
        Pit_Data.setPit_leaveHAB2(leaveHAB2);
        Pit_Data.setPit_endHAB2(endHAB2);
        Pit_Data.setPit_endHAB3(endHAB3);
        Pit_Data.setPit_motor(motor);
        Pit_Data.setPit_speed(speed);
        Pit_Data.setPit_lang(lang);
        Pit_Data.setPit_ssMode(ssMode);
         /* */
        Pit_Data.setPit_comment(comments);
        Pit_Data.setPit_dateTime(timeStamp);
        Pit_Data.setPit_scout(scout);
        Pit_Data.setPit_photoURL(photoURL);
// -----------------------------------------------
        saveDatatoSDcard();                 //Save locally
        if (Pearadox.is_Network) {          // is Internet available?         Commented out because 'tethered' show No internet
            String keyID = teamSelected;
            pfPitData_DBReference.child(keyID).setValue(Pit_Data);      // Store it to Firebase
            Log.e(TAG, ">>>>>  Pit data saved to Firebase <<<<<");
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "*** Data _NOT_ stored to Firebase (only SD)!!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        private void saveDatatoSDcard() {
        Log.w(TAG, "@@@@  saveDatatoSDcard  @@@@");
        String filename = Pit_Data.getPit_team().trim() + ".dat";
        ObjectOutput out = null;
        File directMatch = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/pit/" + Pearadox.FRC_Event + "/" + filename);
        Log.w(TAG, "SD card Path = " + directMatch);
        if(directMatch.exists())  {
            // Todo - Replace TOAST with Dialog Box  - "Do you really ..."   _LOW_ priority
            Toast toast = Toast.makeText(getBaseContext(), "Data for " + filename + " already exists!!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }

        try {
            out = new ObjectOutputStream(new FileOutputStream(directMatch));
            out.writeObject(Pit_Data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// ################################################################
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 200);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);

            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit without saving?  All data will be lost!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        dataSaved = false;
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

//###################################################################
//###################################################################
//###################################################################
    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume  \n");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        if (!dataSaved) {
            Log.w(TAG, "** Data _NOT_ saved!!  **");
            // Handled with Dialog box
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "OnDestroy");
    }

}
