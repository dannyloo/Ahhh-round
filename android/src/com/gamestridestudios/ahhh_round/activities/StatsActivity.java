package com.gamestridestudios.ahhh_round.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.gamestridestudios.ahhh_round.AhhhRound;
import com.gamestridestudios.ahhh_round.MainApplication;
import com.gamestridestudios.ahhh_round.R;
import com.gamestridestudios.ahhh_round.activities.helpers.GooglePlayGamesHelper;
import com.gamestridestudios.ahhh_round.components.Color;
import com.gamestridestudios.ahhh_round.components.RoundedButton;
import com.gamestridestudios.ahhh_round.components.TextView;
import com.gamestridestudios.ahhh_round.stores.CharacterSkinStore;
import com.gamestridestudios.ahhh_round.stores.GameActivityStore;
import com.gamestridestudios.ahhh_round.utils.AssetSizeUtil;
import com.gamestridestudios.ahhh_round.utils.ThousandsFormatter;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends Activity {
    private List<TextView> textViews = new ArrayList<>();
    private TextView highScoreValueTextView;
    private TextView totalPlaysValueTextView;
    private TextView totalJumpsValueTextView;
    private RoundedButton leaderboardsButton;
    private RoundedButton doneButton;
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private GooglePlayGamesHelper googlePlayGamesHelper;
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityStore = ((MainApplication) getApplication()).getGameActivityStore();
        characterSkinStore = ((MainApplication) getApplication()).getCharacterSkinStore();
        bus = ((MainApplication) getApplication()).getBus();
        googlePlayGamesHelper = new GooglePlayGamesHelper(this, bus, gameActivityStore, characterSkinStore);
        setContentView(R.layout.stats_activity);
        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(Color.OFF_WHITE.argb);
        setupHeader();
        setupLabels();
        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googlePlayGamesHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googlePlayGamesHelper.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googlePlayGamesHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void setupHeader() {
        TextView header = (TextView) findViewById(R.id.stats_activity_header);
        header.initialize(AssetSizeUtil.outOfGameFontSize(26), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK);
    }

    public void setupLabels() {
        highScoreValueTextView = (TextView) findViewById(R.id.stats_activity_high_score_value);
        textViews.add(highScoreValueTextView);
        totalPlaysValueTextView = (TextView) findViewById(R.id.stats_activity_total_plays_value);
        textViews.add(totalPlaysValueTextView);
        totalJumpsValueTextView = (TextView) findViewById(R.id.stats_activity_total_jumps_value);
        textViews.add(totalJumpsValueTextView);
        textViews.add((TextView) findViewById(R.id.stats_activity_high_score_label));
        textViews.add((TextView) findViewById(R.id.stats_activity_total_jumps_label));
        textViews.add((TextView) findViewById(R.id.stats_activity_total_plays_label));

        for (TextView tv : textViews) {
            tv.initialize(AssetSizeUtil.outOfGameFontSize(20), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK);
        }

        highScoreValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(18));
        highScoreValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getHighScore()));
        totalPlaysValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(18));
        totalPlaysValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalPlays()));
        totalJumpsValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(18));
        totalJumpsValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalJumps()));
    }

    public void setupButtons() {
        doneButton = (RoundedButton) findViewById(R.id.stats_activity_done_button);
        doneButton.initialize(AssetSizeUtil.outOfGameFontSize(19), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK, Color.OFF_WHITE);
        doneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
        leaderboardsButton = (RoundedButton) findViewById(R.id.stats_activity_leaderboards_button);
        leaderboardsButton.initialize(AssetSizeUtil.outOfGameFontSize(19), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK, Color.OFF_WHITE);
        leaderboardsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                googlePlayGamesHelper.attemptToShowLeaderboard();
                return false;
            }
        });
    }
}
