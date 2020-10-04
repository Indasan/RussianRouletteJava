package ru.indasan.russianroulette;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {
    private SoundPool sounds;
    private int sound_shot, sound_shot_false, sound_roll;
    private ImageView blood_image;
    private Button buttonRoll, buttonShot;

    private int lucky_number = 3;
    private int max_bullets = 6;
    private int random = 0;

    //Для сохранения настроек(в моём случае количество удач)
    private TextView lucky_view, max_lucky;
    private int lucky_shots = 0;
    private int statistic = 0;
    private boolean isRoll = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createSoundPool();
        loadSounds();
        init();
        if(isRoll){
            buttonShot.setEnabled(true);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }



    @Override
    protected void onResume() {
        super.onResume();
        lucky_shots = sharedPreferences.getInt("shots", 0);
        statistic = sharedPreferences.getInt("statistic",0);
        isRoll = sharedPreferences.getBoolean("roll",false);
        if(isRoll){
            buttonShot.setEnabled(true);
        }else buttonShot.setEnabled(false);
        max_lucky.setText("Your lucky: "+statistic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    protected void createSoundPool(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            createNewSoundPool();
        }
        else {
            createOldSoundPool();
        }
    }

    private void saveData() {
        editor.putInt("shots", lucky_shots).commit();
        editor.putInt("statistic", statistic).commit();
        editor.putBoolean("roll", isRoll);
    }

    //Пишем код для разных версий Андрода
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }
    protected void createOldSoundPool(){
        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
    }

    private void loadSounds(){ 
        sound_shot = sounds.load(this,R.raw.revolver_shot,1);
        sound_shot_false = sounds.load(this,R.raw.gun_false,1);
        sound_roll = sounds.load(this,R.raw.revolver_baraban,1);
    }

    public void onShot(View view) {
        if(isRoll) {
            isRoll = false;
            buttonShot.setEnabled(false);
            if (random == lucky_number) {
                sounds.play(sound_shot, 1.0f, 1.0f, 1, 0, 1);
                blood_image.setVisibility(View.VISIBLE);

                int max_stat = sharedPreferences.getInt("statistic", 0);
                if (lucky_shots > max_stat) {
                    statistic = lucky_shots;
                    editor.putInt("statistic", lucky_shots).commit();
                    max_lucky.setText("Your lucky: " + lucky_shots);
                    lucky_view.setText("Good Bye Champion!");
                } else lucky_view.setText("Good Bye Loser!");
                lucky_shots = 0;


            } else {
                lucky_shots++;
                sounds.play(sound_shot_false, 1.0f, 1.0f, 1, 0, 1);
                lucky_view.setText("Lucky shots: " + lucky_shots);
            }
        }

    }


    public void onRoll(View view) {
        isRoll = true;
        buttonShot.setEnabled(true);
        lucky_view.setText("Lucky shots: " + lucky_shots);
        sounds.play(sound_roll,1.0f,1.0f,1,0,1);
        blood_image.setVisibility(View.GONE);

        random = new Random().nextInt(max_bullets-1);

        //Log.d("RANDOM","Random number is: "+random);
    }

    @SuppressLint("CommitPrefEdits")
    private void init(){
        buttonRoll = findViewById(R.id.buttonRoll);
        buttonShot = findViewById(R.id.buttonShot);

        blood_image = findViewById(R.id.image_blood);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = sharedPreferences.edit();

        lucky_view = findViewById(R.id.lucky_view);
        max_lucky = findViewById(R.id.max_lucky);

        int shots = sharedPreferences.getInt("shots",0);

        lucky_view.setText("Lucky shots: "+shots);

    }
}