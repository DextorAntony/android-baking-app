

package com.example.android.bakingapp.ui.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.FragmentStepDetailBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

import static com.example.android.bakingapp.utilities.Constant.EXTRA_RECIPE;
import static com.example.android.bakingapp.utilities.Constant.FAST_FORWARD_INCREMENT;
import static com.example.android.bakingapp.utilities.Constant.PLAYER_PLAYBACK_SPEED;
import static com.example.android.bakingapp.utilities.Constant.REWIND_INCREMENT;
import static com.example.android.bakingapp.utilities.Constant.SAVE_STEP;
import static com.example.android.bakingapp.utilities.Constant.START_POSITION;
import static com.example.android.bakingapp.utilities.Constant.STATE_CURRENT_WINDOW;
import static com.example.android.bakingapp.utilities.Constant.STATE_PLAYBACK_POSITION;
import static com.example.android.bakingapp.utilities.Constant.STATE_PLAY_WHEN_READY;
import static com.example.android.bakingapp.utilities.Constant.STATE_STEP_INDEX;


public class StepDetailFragment extends Fragment implements Player.EventListener {

    
    private FragmentStepDetailBinding mStepDetailBinding;

    
    private Step mStep;
    
    private int mStepIndex;
    
    private Recipe mRecipe;

    
    private String mVideoUrl;
    
    private String mThumbnailUrl;
    
    private boolean mHasVideoUrl = false;

    
    private SimpleExoPlayer mExoPlayer;

    
    private long mPlaybackPosition;

    
    private int mCurrentWindow;

    private boolean mPlayWhenReady;

    private static MediaSessionCompat sMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    // Tag for a MediaSessionCompat
    private static final String TAG = StepDetailFragment.class.getSimpleName();

    
    public StepDetailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate FragmentStepDetailBinding using DataBindingUtil
        mStepDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step_detail, container, false);
        View rootView = mStepDetailBinding.getRoot();

        // Load the saved state (the step, step index, playback position, current window,
        // play when ready) if there is one
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(SAVE_STEP);
            mStepIndex = savedInstanceState.getInt(STATE_STEP_INDEX);
            mPlaybackPosition = savedInstanceState.getLong(STATE_PLAYBACK_POSITION);
            mCurrentWindow = savedInstanceState.getInt(STATE_CURRENT_WINDOW);
            mPlayWhenReady = savedInstanceState.getBoolean(STATE_PLAY_WHEN_READY);
        } else {
            // Clear the start position
            mCurrentWindow = C.INDEX_UNSET;
            mPlaybackPosition = C.TIME_UNSET;
            mPlayWhenReady = true;
        }

        // If the Step exists, set the description to the TextView and handle media URL.
        // Otherwise, create a Log statement that indicates that the step was not found
        if(mStep != null) {

            // Set correct description
            setCorrectDescription(mStep);

            // Handles video URL and thumbnail URL
            handleMediaUrl();

        } else {
            Timber.v("This fragment has a null step");
        }

        // Initialize the Media Session
        initializeMediaSession();

        // Get the recipe data from the intent
        getRecipeData();

        // When the user clicks the next button, it navigates to the next step
        onNextButtonClick();
        // When the user clicks the previous button, it navigates to the previous step
        onPreviousButtonClick();
        // Display the current step in a single-pane phone case (e.g. "Step 5 of 12")
        setStepId();

        // Hide next button at the end of the step and the previous button at the beginning
        hideButtonAtBeginningEnd();

        // Return the rootView
        return rootView;
    }

    
    private String getDescriptionWithCorrectStepId(Step step) {
        // Extract step ID and description
        int stepId = step.getStepId();
        String description = step.getDescription();

        if (stepId != mStepIndex) {
            // If the step ID does not correspond to the step index, replace step ID with step index
            stepId = mStepIndex;

            // Find the position of the first occurrence of the period
            int periodIndex = description.indexOf(getString(R.string.period));
            // Concatenate correct step ID and the substring
            description = stepId + description.substring(periodIndex);

            // Set a new description of the step
            step.setDescription(description);
        }
        return description;
    }

    
    private void setCorrectDescription(Step step) {
        // Replace the number in the description which represents step ID with the correct ID.
        String descriptionWithCorrectStepId = getDescriptionWithCorrectStepId(step);

        // Replace a question mark "�" with the degree "°"(e.g. step 1 description of Brownies)
        String replacedDescription = replaceString(descriptionWithCorrectStepId);

        // Display the correct description
        mStepDetailBinding.tvDescription.setText(replacedDescription);
    }

    
    private void onNextButtonClick() {
        // Set a click listener on the next button
        mStepDetailBinding.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                // Increment position as long as the index remains <= the size of the step list
                if (mStepIndex < mRecipe.getSteps().size() - 1) {
                    mStepIndex++;
                    stepDetailFragment.setStep(mRecipe.getSteps().get(mStepIndex));
                    stepDetailFragment.setStepIndex(mStepIndex);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, stepDetailFragment)
                            .commit();
                }
            }
        });
    }

    
    private void onPreviousButtonClick() {
        // Set a click listener on the previous button
        mStepDetailBinding.btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                // Decrement position by one if the index is greater than zero
                if (mStepIndex > 0) {
                    mStepIndex--;
                    stepDetailFragment.setStep(mRecipe.getSteps().get(mStepIndex));
                    stepDetailFragment.setStepIndex(mStepIndex);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, stepDetailFragment)
                            .commit();
                }
            }
        });
    }

    
    private void hideButtonAtBeginningEnd() {
        int lastStep = mRecipe.getSteps().size() -1;
        if (mStepIndex == lastStep) {
            mStepDetailBinding.btNext.setVisibility(View.INVISIBLE);
        } else if (mStepIndex == 0) {
            mStepDetailBinding.btPrevious.setVisibility(View.INVISIBLE);
        }
    }

    
    private void setStepId() {
        if (!isTwoPane()) {
            int lastStep = mRecipe.getSteps().size() - 1;
            String currentStepOfLast = getString(R.string.step) + String.valueOf(mStepIndex)
                    + getString(R.string.space) + getString(R.string.of)
                    + getString(R.string.space) + String.valueOf(lastStep);
            mStepDetailBinding.tvStepId.setText(currentStepOfLast);
        }
    }

    
    private void getRecipeData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_RECIPE)) {
                // Get the recipe from the intent
                Bundle b = intent.getBundleExtra(EXTRA_RECIPE);
                mRecipe = b.getParcelable(EXTRA_RECIPE);
            }
        }
    }

    
    private void initializeMediaSession() {

        // Create a MediaSessionCompat
        sMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls
        sMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible
        sMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_REWIND |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        sMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller
        sMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the fragment is active
        sMediaSession.setActive(true);
    }

    
    private void handleMediaUrl() {
        // Get video URL and thumbnail URL from the step of the recipe
        mVideoUrl = mStep.getVideoUrl();
        mThumbnailUrl = mStep.getThumbnailUrl();

        // Check if the thumbnail URL contains an "mp4" file
        // Step 5 of the Nutella Pie has an mp4 file in the thumbnail URL
        if (mThumbnailUrl.contains(getResources().getString(R.string.mp4))) {
            mVideoUrl = mThumbnailUrl;
        }

        if (!mVideoUrl.isEmpty()) {
            // If the video URL exists, set the boolean variable to true
            mHasVideoUrl = true;
        } else if (!mThumbnailUrl.isEmpty()) {
            // If the thumbnail URL exists, load thumbnail with Picasso
            mStepDetailBinding.playerView.setVisibility(View.GONE);
            Picasso.with(getContext())
                    .load(mThumbnailUrl)
                    .error(R.drawable.woman_with_dish)
                    .placeholder(R.drawable.woman_with_dish)
                    .into(mStepDetailBinding.ivEmpty);
        } else {
            // If the step of the recipe has no visual media, load chef image
            mStepDetailBinding.playerView.setVisibility(View.GONE);
            mStepDetailBinding.ivEmpty.setImageResource(R.drawable.woman_with_dish);
        }
    }

    
    public void setStep(Step step) {
        mStep = step;
    }

    
    public void setStepIndex(int stepIndex) {
        mStepIndex = stepIndex;
    }

    
    private void initializePlayer(boolean hasVideoUrl) {
        // Check if the step of the recipe has a video
        if (hasVideoUrl) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer
                DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(getContext());
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        defaultRenderersFactory, trackSelector, loadControl);

                // Set the ExoPlayer to the playerView
                mStepDetailBinding.playerView.setPlayer(mExoPlayer);

                mExoPlayer.setPlayWhenReady(mPlayWhenReady);
            }

            // Set the Player.EventListener to this fragment
            mExoPlayer.addListener(this);

            // Prepare the MediaSource
            Uri mediaUri = Uri.parse(mVideoUrl);
            MediaSource mediaSource = buildMediaSource(mediaUri);

            // Restore the playback position
            boolean haveStartPosition = mCurrentWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                mExoPlayer.seekTo(mCurrentWindow, mPlaybackPosition);
            }
            // The boolean flags indicate whether to reset position and state of the player
            mExoPlayer.prepare(mediaSource, !haveStartPosition, false);
        }
    }

    
    private MediaSource buildMediaSource(Uri mediaUri) {
        String userAgent = Util.getUserAgent(this.getContext(), getString(R.string.app_name));
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(mediaUri);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Starting with API level 24 Android supports multiple windows. As our app can be visible
        // but not active in split window mode, we need to initialize the player in onStart().
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            // Initialize the player if the step of the recipe has a video URL
            initializePlayer(mHasVideoUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        // Before API level 24 we wait as long as possible until we grab resources, so we wait until
        // onResume() before initializing the player.
        if (Util.SDK_INT <= Build.VERSION_CODES.M || mExoPlayer == null) {
            // Initialize the player if the step of the recipe has a video URL
            initializePlayer(mHasVideoUrl);
        }
    }

    
    private boolean isTwoPane() {
        // The TextView that displays the step ID will only initially exist in a single-pane phone case.
       return mStepDetailBinding.tvStepId == null;
    }

    
    private boolean isSinglePaneLand() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !isTwoPane();
    }

    
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (isSinglePaneLand()) {
            int flagFullScreen = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            // Enable full-screen mode on PlayerView, empty ImageView
            mStepDetailBinding.playerView.setSystemUiVisibility(flagFullScreen);
            mStepDetailBinding.ivEmpty.setSystemUiVisibility(flagFullScreen);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Before API level 24 there is no guarantee of onStop() being called. So we have to release
        // the player as early as possible in onPause().
        if (Util.SDK_INT <= Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Starting with API level 24 onStop() is guaranteed to be called and in the paused mode
        // our activity is eventually still visible. Hence we need to wait releasing until onStop().
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // End the Media session when it is no longer needed
        sMediaSession.setActive(false);
    }

    
    private void releasePlayer() {
        if (mExoPlayer != null) {
            updateCurrentPosition();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    
    private void updateCurrentPosition() {
        if (mExoPlayer != null) {
            mPlaybackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }
    }

    
    private String replaceString(String target) {
        if (target.contains(getString(R.string.question_mark))) {
            target = target.replace(getString(R.string.question_mark), getString(R.string.degree));
        }
        return target;
    }

    
    private void hideButtonWhenPlaying() {
        if (isTwoPane() | isSinglePaneLand()){
            mStepDetailBinding.btPrevious.setVisibility(View.GONE);
            mStepDetailBinding.btNext.setVisibility(View.GONE);
        }
    }

    
    private void showButtonWhenPausedEnded() {
        if (isTwoPane() | isSinglePaneLand()) {
            mStepDetailBinding.btPrevious.setVisibility(View.VISIBLE);
            mStepDetailBinding.btNext.setVisibility(View.VISIBLE);
        }

        // Hide next button at the end of the step and the previous button at the beginning.
        hideButtonAtBeginningEnd();
    }

    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the step and step index to our bundle
        outState.putParcelable(SAVE_STEP, mStep);
        outState.putInt(STATE_STEP_INDEX, mStepIndex);

        // Update the current state of the player
        updateCurrentPosition();
        // Store the playback position to our bundle
        outState.putLong(STATE_PLAYBACK_POSITION, mPlaybackPosition);
        outState.putInt(STATE_CURRENT_WINDOW, mCurrentWindow);
        outState.putBoolean(STATE_PLAY_WHEN_READY, mPlayWhenReady);
    }

    // Player Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            // When ExoPlayer is playing, update the PlayBackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), PLAYER_PLAYBACK_SPEED);

            // When ExoPlayer is playing, hide the previous and next button
            hideButtonWhenPlaying();
        } else if (playbackState == Player.STATE_READY) {
            // When ExoPlayer is paused, update the PlayBackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), PLAYER_PLAYBACK_SPEED);

            // When ExoPlayer is paused, show the previous and next button
            showButtonWhenPausedEnded();
        } else if (playbackState == Player.STATE_ENDED) {
            // When ExoPlayer is ended, show the previous and next button
            showButtonWhenPausedEnded();
        }
        sMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(Math.max(mExoPlayer.getCurrentPosition()
                    - REWIND_INCREMENT, START_POSITION));
        }

        @Override
        public void onFastForward() {
            long duration = mExoPlayer.getDuration();
            mExoPlayer.seekTo(Math.min(mExoPlayer.getCurrentPosition()
                    + FAST_FORWARD_INCREMENT, duration));
        }
    }
}
