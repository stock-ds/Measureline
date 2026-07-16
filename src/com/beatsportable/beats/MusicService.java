package com.beatsportable.beats;

import android.media.MediaPlayer;

public class MusicService {
	
	private MediaPlayer p;
	private String musicFilePath;
	
	private int pauseTime;
	private boolean isStarted;
	
	private void setupMusicPlayer() {
		try {
			if (musicFilePath != null && musicFilePath.length() < 2) {
				throw new Exception(
						Tools.getString(R.string.MusicService_invalid_path) + 
						musicFilePath
						);
			}
			if (p == null)
				p = new MediaPlayer();
			p.setDataSource(musicFilePath);
			p.setLooping(false);
			p.prepare();
		} catch (Exception e) {
			ToolsTracker.error("MusicService.setupMusicPlayer", e, musicFilePath);
			Tools.toast(
					Tools.getString(R.string.MusicService_unable_create_service) +
					Tools.getString(R.string.Tools_error_msg) +
					e.getMessage() + 
					Tools.getString(R.string.Tools_notify_msg)
					);
			p = null;
		} 
	}
	
	public MusicService(String musicFilePath) {
		this.musicFilePath = musicFilePath;
		this.isStarted = false;
		setupMusicPlayer();
	}
	
	public int getCurrentPosition() {
		if (p != null) {
			return p.getCurrentPosition();
		} else {
			return 0;
		}
	}
	
	public boolean isPlaying() {
		return p != null && p.isPlaying();
	}
	
	public boolean isStarted() {
		return p != null && isStarted;
	}
	
	private void startPlaying(boolean firstAttempt) {
		try {
			if (p == null)
				throw new IllegalStateException(
						Tools.getString(R.string.MusicService_not_initialized)
						);
			p.seekTo(0);
			p.start();
			isStarted = true;
		} catch (IllegalStateException e) {
			ToolsTracker.error("MusicService.startPlaying", e, musicFilePath);
			Tools.toast(
					Tools.getString(R.string.MusicService_unable_start_playback) +
					Tools.getString(R.string.Tools_error_msg) +
					e.getMessage() + 
					Tools.getString(R.string.Tools_notify_msg)
					);
			setupMusicPlayer();
			if (firstAttempt)
				startPlaying(false); // Try max twice
		}
	}
	public void startPlaying() {
		startPlaying(true);
	}
	
	public void pausePlaying() {
		try {
			if (p == null)
				throw new IllegalStateException(
						Tools.getString(R.string.MusicService_not_initialized)
						);
			if (p.isPlaying()) {
				p.pause();
				pauseTime = p.getCurrentPosition();
			}
		} catch (IllegalStateException e) {
			ToolsTracker.error("MusicService.pausePlaying", e, musicFilePath);
			Tools.toast(
					Tools.getString(R.string.MusicService_unable_pause_playback) +
					Tools.getString(R.string.Tools_error_msg) +
					e.getMessage() + 
					Tools.getString(R.string.Tools_notify_msg)
					);
		}
	}
	
	public void resumePlaying() {
		try {
			if (p == null)
				throw new IllegalStateException(
						Tools.getString(R.string.MusicService_not_initialized)
						);
			if (this.isStarted) {
				if (pauseTime > 20) // Delay 20ms
					p.seekTo(pauseTime - 20);
				p.start();
			}
		} catch (IllegalStateException e) {
			ToolsTracker.error("MusicService.resumePlaying", e, musicFilePath);
			Tools.toast(
					Tools.getString(R.string.MusicService_unable_resume_playback) +
					Tools.getString(R.string.Tools_error_msg) +
					e.getMessage() + 
					Tools.getString(R.string.Tools_notify_msg)
					);
		}
	}
	
	public void onDestroy() {
		if (p != null) {
			// MediaPlayer.stop()/release() can block the calling thread for a while on
			// real hardware while the codec tears down (this is a well-known Android
			// behavior; it's usually near-instant on emulators using software codecs,
			// which is why this wasn't noticed until testing on a real device). Since
			// onDestroy() runs on the UI thread and nothing touches the player after
			// this call, hand the actual teardown off to a background thread instead
			// of blocking the exit/back transition.
			final MediaPlayer playerToRelease = p;
			p = null;
			new Thread(new Runnable() {
				public void run() {
					try {
						playerToRelease.stop();
						playerToRelease.release();
					} catch (IllegalStateException e) {
						ToolsTracker.error("MusicService.onDestroy", e, musicFilePath);
					}
				}
			}).start();
		}
	}
	
}
