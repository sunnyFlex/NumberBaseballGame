package com.example.numberbaseballgame;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ScrollView scrollView;
    TextView resultTextView;
    TextView limitBallTextView;
    TextView[] inputTextView = new TextView[3];
    ImageButton backButton;
    ImageButton playButton;
    ImageView gameStartImage, gameOverImage, gameWinImage, pitcherView, pitchingView;
    ImageView[] outCountView = new ImageView[3];
    Button[] numButton = new Button[10];
    Button restartButton;
    Button exitButton;

    FrameLayout startView, resultView;
    Button startButton;
    Animation startAnim;
    AnimationDrawable pitcherAnim, pitchingAnim;

    int inputTextCount = 0;

    int[] comNumber = new int[3];
    boolean isFirstInput = true;

    int playCount = 1;
    int limitCount = 30;

    String getResultText = "";
    String getInputString = "";

    int outCount = 0;

    int[] sound = new int[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //***변수 xml 연결***
        startView = findViewById(R.id.start_view);
        resultView = findViewById(R.id.result_view);
        scrollView = findViewById(R.id.scroll_view);
        resultTextView = findViewById(R.id.result_text_view);
        limitBallTextView = findViewById(R.id.limitBall_text_view);
        pitcherView = findViewById(R.id.pitcher_view);
        pitchingView = findViewById(R.id.pitching_view);
        gameStartImage = findViewById(R.id.game_start_view);
        gameWinImage = findViewById(R.id.game_win_view);
        gameOverImage = findViewById(R.id.game_over_view);

        for (int i = 0; i < outCountView.length; i++) {
            outCountView[i] = findViewById(R.id.outCount_view_0 + i);
        }
        for (int i = 0; i < inputTextView.length ; i++) {
            inputTextView[i] = findViewById(R.id.input_text_view_0 + i);
        }
        for (int i = 0; i < numButton.length; i++) {
            numButton[i] = findViewById(R.id.button_0 + i);
        }

        backButton = findViewById(R.id.back_button);
        playButton = findViewById(R.id.play_button);
        startButton = findViewById(R.id.start_button);
        restartButton = findViewById(R.id.restart_button);
        exitButton = findViewById(R.id.exit_button);

        //앱 시작시 Visibility 정의
        StartPageView();
        getComNumber();

        //***start Text anim***
        startAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.start_text_anim);
        startButton.startAnimation(startAnim);

        pitcherAnim = (AnimationDrawable) pitcherView.getDrawable();
        pitcherAnim.start();

        pitchingAnim = (AnimationDrawable) pitchingView.getDrawable();

        //Button onClickLisener를 통한 콜백 이벤트 정의
        for (Button button: numButton){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNumButtonClick(view);
                    Log.e("inputTextView","입력창: " + inputTextCount );
                }
            });
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBackButtonClick();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButtonClick();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startView.setVisibility(View.GONE);
                startView.clearAnimation();
                startAnim.reset();
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartButtonClick();
                numButtonReset();

                Log.e("limitcount","남은투구수: " + limitCount);
            }
        });
        //**게임앱나가기**
        exitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);// 태스크를 백그라운드로 이동
                finishAndRemoveTask();// 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        limitBallTextView.setText("남은 투구수:" + limitCount + "구");
    }

    private void restartButtonClick() {
        resultView.setVisibility(View.GONE);
        outCount = 0;
        for (ImageView imageView: outCountView){
            imageView.setImageResource(R.drawable.out_count_off);
        }
        for (TextView textView: inputTextView){
            textView.setText("");
        }
        pitcherView();
        pitcherAnim.start();

        playCount = 1;
        limitCount = 30;
        isFirstInput = true;
        resultTextView.setText("*숫자를 눌러주세요*");
        limitBallTextView.setText("남은 투구수:" + limitCount + "구");
    }

    private void pitcherView() {
        pitcherView.setVisibility(View.VISIBLE);
        pitchingView.setVisibility(View.GONE);
    }

    private void playButtonClick() {
        limitCount--;

        if (limitCount > 0){
            //입력하는 myNumber 정의
            int[] myNumber = new int[3];
            for (int i = 0; i < myNumber.length ; i++) {
                myNumber[i] = Integer.parseInt(inputTextView[i].getText().toString());
            }

            //비교 결과(resultText) 정의
            getCountCheck(comNumber, myNumber);

            //모든 숫자 버튼 활성화
            numButtonReset();

            //모든 inputTextView 초기화
            for (TextView textView: inputTextView){
                textView.setText("");
            }
        }else{
            //***투구수 초과 GameOver시
            gameOverPage();
        }
        //pitcherView 에니메이션 정의
        pitchingView.setVisibility(View.VISIBLE);
        pitchingAnim.start();
        pitcherView.setVisibility(View.GONE);


        inputTextCount = 0;
        limitBallTextView.setText("남은 투구수:" + limitCount + "구");
    }

    private void numButtonReset() {
        for (Button button: numButton){
            button.setEnabled(true);
        }
        playButton.setEnabled(false);
    }


    //comNumber와 myNumber 비교 결과 도출 메소드
    private void getCountCheck(int[] comNumber, int[] myNumber) {
        //count[0]은 strike, count[1]은 ball
        int[] count = new int[2];

        //myNumber,comNumber 숫자 비교 S,B 판정
        for (int i = 0; i < comNumber.length; i++) {
            for (int j = 0; j < myNumber.length; j++) {
                if (comNumber[i] == myNumber[j] && i == j) {
                    count[0]++;
                } else if (comNumber[i] == myNumber[j]) {
                    count[1]++;
                }
            }
        }
        //비교 결과값 string 변수 저장
        getResultText = " S:" + count[0] + " B:" + count[1];

        //player입력값 String 변수 저장
        getInputString = "번 [ " + myNumber[0] + "," + myNumber[1] + "," + myNumber[2] + " ] ";

        //strike 3개 일때 출력 및 게임 초기화 정의
        if (count[0] == 3){
            isFirstInput = true;
            getComNumber();
            outCountView[outCount].setImageResource(R.drawable.out_count_on);
            outCount++;
            resultTextView.append(playCount + getInputString + "\n" + "    *** Strike out ***" + "\n" + outCount + "out 다시 입력하세요" + "\n");
            playCount = 1;

            //*** 3아웃 경기 종료 및 경기 초기화 정의 ***
            if (outCount == 3){
                winPageView();
                pitcherAnim.stop();
                pitchingAnim.stop();
                pitcherView.clearAnimation();
                pitchingView.clearAnimation();
            }
        //strike out 이 아닌 경우 정의
        }else{
            if (isFirstInput){
                resultTextView.setText(playCount + getInputString + getResultText + "\n");
                isFirstInput = false;
            }else{
                resultTextView.append(playCount + getInputString + getResultText + "\n");
            }
            playCount++;
        }
    }

    private void StartPageView() {
        startView.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.GONE);
        playButton.setEnabled(false);
    }

    private void winPageView() {
        resultView.setVisibility(View.VISIBLE);
        gameWinImage.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
        gameOverImage.setVisibility(View.GONE);
        startView.setVisibility(View.GONE);
    }

    private void gameOverPage() {
        resultView.setVisibility(View.VISIBLE);
        gameOverImage.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
        gameWinImage.setVisibility(View.GONE);
        startView.setVisibility(View.GONE);
    }

    //backButton 클릭시 상세정의의
   private void getBackButtonClick() {
        if (inputTextCount > 0) {
            Button button = numButton[Integer.parseInt(inputTextView[inputTextCount -1].getText().toString())];

            button.setEnabled(true);
            inputTextView[inputTextCount -1].setText(" ");
            inputTextCount--;
        } else {
            Toast.makeText(getApplicationContext(), "삭제할수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //숫자버튼 numButton 클릭시 상세정의
    private void getNumButtonClick(View view) {

        //pitcherView 에니메이션 정의
        pitcherView();

        if (inputTextCount < 3){
                Button button = findViewById(view.getId());
                inputTextView[inputTextCount].setText(view.getTag().toString());
                inputTextCount++;
                button.setEnabled(false);
                if (inputTextCount == 3){
                    playButton.setEnabled(true);
                }
            }else{
                Toast.makeText(getApplicationContext(), "입력초과", Toast.LENGTH_SHORT).show();
            }
    }

    //랜덤 comNumber 정의
    private void getComNumber(){

        for (int i = 0; i < 3 ; i++) {
            int randomNumber = new Random().nextInt(10);
            comNumber[i] = randomNumber;

            for (int j = 0; j < i ; j++) {
                if (comNumber[i] == comNumber[j]) {
                    i--;
                }
            }
            Log.e("comNumber","com랜덤값 : " + comNumber[0] + "," + comNumber[1] + "," + comNumber[2]);
        }
    }

}