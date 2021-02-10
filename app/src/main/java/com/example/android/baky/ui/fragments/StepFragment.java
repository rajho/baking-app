package com.example.android.baky.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.baky.R;
import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Step;
import com.example.android.baky.databinding.FragmentStepBinding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import timber.log.Timber;

public class StepFragment extends Fragment implements Player.EventListener {
  public static final String STEP_FRAGMENT_TAG      = "STEP_FRAGMENT_TAG";
  public static final  String STEP_ID_PARAM        = "STEP_ID_PARAM";
  public static final  String VIDEO_POSITION_PARAM = "VIDEO_POSITION_PARAM";
  private static final String TAG                  = StepFragment.class.getSimpleName();
  private static final int    DEFAULT_INVALID_ID   = -1;

  private StepFragment.OnButtonClickListener mCallback;
  private FragmentStepBinding                mBinding;
  private Step                               mStep;

  private int                         mStepId;
  private SimpleExoPlayer             mSimpleExoPlayer;
  private MediaSessionCompat          mMediaSession;
  private PlaybackStateCompat.Builder mStateBuilder;
  private AppDatabase                 mDb;
  // Current position
  private long                        mStartPosition;
  private int                         mStartWindow;
  private boolean                     mStartAutoplay = true;


  public StepFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try {
      mCallback = (StepFragment.OnButtonClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = FragmentStepBinding.inflate(inflater, container, false);

    mDb = AppDatabase.getInstance(getContext());

    if (savedInstanceState != null) {
      mStartPosition = savedInstanceState.getLong(VIDEO_POSITION_PARAM, 0);
      mStepId        = savedInstanceState.getInt(STEP_ID_PARAM, DEFAULT_INVALID_ID);
      Timber.i("Getting video current position: %s and step id: %s", mStartPosition, mStepId);
    }


    if (mBinding.getRoot().findViewById(R.id.next_button) != null) {
      mBinding.nextButton.setOnClickListener(v -> {
        mCallback.onNextOrPreviousSelected(mStep.getSortOrder() + 1);
      });

      mBinding.previousButton.setOnClickListener(v -> {
        mCallback.onNextOrPreviousSelected(mStep.getSortOrder() - 1);
      });
    }

    return mBinding.getRoot();
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      loadDBStep();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (Util.SDK_INT <= 23) {
      loadDBStep();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    updateStartPosition();
    Timber.i("Saving current position: %s", mStartPosition);

    if (Util.SDK_INT <= 23) {
      if (mSimpleExoPlayer != null) {
        releasePlayer();
      }

      if (mMediaSession != null) {
        mMediaSession.setActive(false);
      }
    }
  }

  private void updateStartPosition() {
    if (mSimpleExoPlayer != null) {
      mStartPosition = mSimpleExoPlayer.getCurrentPosition();
      mStartAutoplay = mSimpleExoPlayer.getPlayWhenReady();
      mStartWindow   = mSimpleExoPlayer.getCurrentWindowIndex();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      if (mSimpleExoPlayer != null) {
        releasePlayer();
      }

      if (mMediaSession != null) {
        mMediaSession.setActive(false);
      }
    }
  }

  private void loadDBStep() {
    mDb.mStepDao().getStepById(mStepId).observe(this, step -> {
      if (step != null) {
        mStep = step;
        setStepUI();
      }
    });
  }

  private void setStepUI() {
    String videoUrl = mStep.getVideoURL();
    Uri    videoUri = null;
    if (videoUrl != null && !videoUrl.isEmpty()) {
      mBinding.videoPlayer.setVisibility(View.VISIBLE);
      videoUri = Uri.parse(mStep.getVideoURL());
    } else {
      mBinding.videoPlayer.setVisibility(View.GONE);
      mBinding.stepDescription.setVisibility(View.VISIBLE);
    }
    mBinding.stepDescription.setText(mStep.getDescription());

    if (videoUri != null) {
      initializeMediaSession();
      initializePlayer(videoUri);
    }

    setDefaultImage(mStep.getThumbnailURL());
  }

  private void setDefaultImage(String imageUrl) {
    if (imageUrl != null && !imageUrl.isEmpty()) {
      try {
        URL url = new URL(imageUrl);

        new Thread() {
          @Override
          public void run() {
            try {
              Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection()
                                                                 .getInputStream());
              Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                mBinding.videoPlayer.setVisibility(View.VISIBLE);
                mBinding.videoPlayer.setDefaultArtwork(imageBitmap);
              });
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void initializeMediaSession() {
    mMediaSession = new MediaSessionCompat(Objects.requireNonNull(getContext()), TAG);

    mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                           MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

    mMediaSession.setMediaButtonReceiver(null);

    mStateBuilder = new PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
        PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SEEK_TO);

    mMediaSession.setPlaybackState(mStateBuilder.build());
    mMediaSession.setCallback(new MySessionCallback());
    mMediaSession.setActive(true);
  }

  private void initializePlayer(Uri videoUri) {
    if (mSimpleExoPlayer == null) {
      // Create and instance of the ExoPlayer
      mSimpleExoPlayer = new SimpleExoPlayer.Builder(Objects.requireNonNull(getContext())).build();
      mSimpleExoPlayer.setPlayWhenReady(mStartAutoplay);

      mBinding.videoPlayer.setPlayer(mSimpleExoPlayer);

      mSimpleExoPlayer.addListener(this);
      // Prepare the MediaSource.
      MediaItem mediaItem = MediaItem.fromUri(videoUri);
      if (mStartPosition != 0.0){
        Timber.i("Initializing player with position: %s", mStartPosition);
        mSimpleExoPlayer.seekTo(mStartWindow, mStartPosition);
      }

      mSimpleExoPlayer.setMediaItem(mediaItem, mStartPosition == 0.0);
      mSimpleExoPlayer.prepare();

    }
  }


  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    Timber.i("OnSaveInstanceState Position: %s , mStepId: %s", mStartPosition, mStepId);
    outState.putInt(STEP_ID_PARAM, mStepId);
    outState.putLong(VIDEO_POSITION_PARAM, mStartPosition);
    super.onSaveInstanceState(outState);
  }

  private void releasePlayer() {
    mSimpleExoPlayer.stop();
    mSimpleExoPlayer.release();
    mSimpleExoPlayer = null;
  }

  @Override
  public void onIsPlayingChanged(boolean isPlaying) {
    if (isPlaying) {
      mStateBuilder.setState(
          PlaybackStateCompat.STATE_PLAYING,
          mSimpleExoPlayer.getCurrentPosition(),
          1f
      );
    } else {
      mStateBuilder.setState(
          PlaybackStateCompat.STATE_PAUSED,
          mSimpleExoPlayer.getCurrentPosition(),
          1f
      );
    }

    mMediaSession.setPlaybackState(mStateBuilder.build());
  }

  public void setStepId(int stepId) {
    mStepId = stepId;
  }

  public interface OnButtonClickListener {
    void onNextOrPreviousSelected(int newStepNumber);
  }

  private class MySessionCallback extends MediaSessionCompat.Callback {
    @Override
    public void onPlay() {
      mSimpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
      super.onPause();
      mSimpleExoPlayer.setPlayWhenReady(false);
    }
  }
}
