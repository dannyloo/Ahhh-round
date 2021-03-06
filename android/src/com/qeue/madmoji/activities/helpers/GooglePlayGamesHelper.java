package com.qeue.madmoji.activities.helpers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.qeue.madmoji.R;
import com.qeue.madmoji.stores.CharacterSkinStore;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.qeue.madmoji.events.UpdateGooglePlayGamesEvent;
import com.qeue.madmoji.stores.GameActivityStore;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * This class allows easy integration with Google Play Games.
 * It takes care of connecting to Google Play Games and sending scores.
 * If the user has previously installed the app and integrated Google Play Games and they reinstall
 * the app, it will load their old scores.
 */
public class GooglePlayGamesHelper implements GameHelper.GameHelperListener {
    private Activity activity;
    private Bus bus;
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private GameHelper gameHelper;
    private boolean showLeaderboardAfterSignIn;

    public GooglePlayGamesHelper(final Activity activity, Bus bus, final GameActivityStore gameActivityStore, CharacterSkinStore characterSkinStore) {
        this.activity = activity;
        this.bus = bus;
        this.gameActivityStore = gameActivityStore;
        this.characterSkinStore = characterSkinStore;

        bus.register(this);

        gameHelper = new GameHelper(activity, GameHelper.CLIENT_GAMES);
        gameHelper.setConnectOnStart(!gameActivityStore.hasFailedGamesSignInOnce());
        gameHelper.setup(this);
    }


    public void onStart() {
        gameHelper.onStart(activity);
        int tempScore = 0;

    }

    @Override
    public void onSignInFailed() {
        gameActivityStore.setHasFailedGamesSignInOnce(true);
        showLeaderboardAfterSignIn = false;


    }

    @Override
    public void onSignInSucceeded() {
        gameActivityStore.setHasFailedGamesSignInOnce(false);
        updateHighscoreBasedOnLeaderboard();
        updateTotalJumpsBasedOnLeaderboard();
        updateTotalPlaysBasedOnLeaderboard();
        if (showLeaderboardAfterSignIn) {
            showLeaderboard();
        }
    }

    private void updateHighscoreBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.highscore_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                System.out.println("score is ");
                int score = 0;
                if(loadPlayerScoreResult.getScore() == null){
                    //do nothing
                }
                else
                    score = (int) loadPlayerScoreResult.getScore().getRawScore();

                System.out.println("score1 is " + score);
                if (score > gameActivityStore.getHighScore()) {
                    gameActivityStore.setHighScore(score);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }
            }
        });
    }

    private void updateTotalJumpsBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_jumps_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                int totalJumps = 0;
                if(loadPlayerScoreResult.getScore() == null){
                    //do nothing
                }
                else
                    totalJumps = (int) loadPlayerScoreResult.getScore().getRawScore();

                if (totalJumps > gameActivityStore.getTotalJumps()) {
                    gameActivityStore.setTotalJumps(totalJumps);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }


            }
        });
    }

    private void updateTotalPlaysBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_plays_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                int totalPlays = 0;
                if(loadPlayerScoreResult.getScore() == null){
                    //do nothing
                }
                else
                    totalPlays = (int) loadPlayerScoreResult.getScore().getRawScore();

                if (totalPlays > gameActivityStore.getTotalPlays()) {
                    gameActivityStore.setTotalPlays(totalPlays);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }
            }
        });
    }

    @Subscribe
    public void updateGooglePlayGames(final UpdateGooglePlayGamesEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameHelper.isSignedIn()) {
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.highscore_leaderboard_id), event.score);
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_jumps_leaderboard_id), event.totalJumps);
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_plays_leaderboard_id), event.totalPlays);
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_days_plays_leaderboard_id), event.daysPlayedInARow);

                    if (event.score >= 1) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_1_id));
                    }
                    if (event.score >= 10) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_10_id));
                    }
                    if (event.score >= 25) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_25_id));
                    }
                    if (event.score >= 50) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_50_id));
                    }
                    if (event.totalJumps >= 1000) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_1000_jumps_id));
                    }
                    if (event.totalJumps >= 2500) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_2500_jumps_id));
                    }
                    if (event.totalJumps >= 5000) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_5000_jumps_id));
                    }
                    if (event.totalPlays >= 1000) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_1000_plays_id));
                    }
                    if (event.daysPlayedInARow >= 2) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_2_days_id));
                    }
                    if (event.daysPlayedInARow >= 7) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_7_days_id));
                    }
                }
            }
        });
    }

    public void attemptToShowLeaderboard() {
        if (gameHelper.isSignedIn()) {
            showLeaderboard();
        } else {
            showLeaderboardAfterSignIn = true;
            gameHelper.reconnectClient();
        }
    }

    private void showLeaderboard() {
        if (gameHelper.isSignedIn()) {
            activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), 0);
        }
        showLeaderboardAfterSignIn = false;
    }


    public void onStop() {
        gameHelper.onStop();
    }

    public void onActivityResult(int request, int response, Intent data) {
        gameHelper.onActivityResult(request, response, data);
    }
}
