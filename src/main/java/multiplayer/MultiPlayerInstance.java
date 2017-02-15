/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009, 2010, 2011, 2012, 2013, 2014, 2015 Caprica Software Limited.
 */
package multiplayer;

import static uk.co.caprica.vlcjplayer.Application.application;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.w3c.dom.events.MouseEvent;

import uk.co.caprica.vlcj.binding.internal.libvlc_logo_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.Equalizer;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcjplayer.event.StoppedEvent;

/**
 * A single player instance and associated video surface.
 */
public class MultiPlayerInstance extends MediaPlayerEventAdapter implements Runnable, MouseListener {


    private final Canvas videoSurface;
    private JPanel playerPanel;
    
    private String mediaPath;
	private String screenName;

	private LineBorder border;
	
	private EmbeddedMediaPlayer mediaPlayer;
	private MediaPlayerFactory mediaFactory;
	
	private MediaPlayer recordMediaPlayer;

	private long elapsedTime;
	private boolean recording;


    public MultiPlayerInstance(String screenName, MediaPlayerFactory factory, FullScreenStrategy fullScreenStrategy) {
    	this.screenName = screenName;
    	this.mediaPath = "";
    	this.mediaFactory = factory;
        this.mediaPlayer = this.mediaFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
        this.videoSurface = new Canvas();
        this.videoSurface.setBackground(Color.black);
        this.recording = false;
        elapsedTime = 0;

        mediaPlayer.addMediaPlayerEventListener(this);
    }

	public EmbeddedMediaPlayer mediaPlayer() {
        return mediaPlayer;
    }

    public Canvas videoSurface() {
        return videoSurface;
    }
    
    public void setPlayerPanel(JPanel playerPanel) {
    	this.playerPanel = playerPanel;
    }

    public JPanel getPlayerPanel(){
    	return this.playerPanel;
    }
    
    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - mediaChanged{" + mrl + "} with media_t{" + media.toString() + "}");
        this.mediaPath = mrl;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Playing");
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Paused");
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Stopped");
        clearScreen();
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Finished playback");
        clearScreen();
        application().checkContinuousPlay();
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Error");
        clearScreen();
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {
        System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] Opening");
    }
    
    public void clearScreen() {
    	System.out.println("@Timestamp=" + mediaPlayer.getTime() + " - Screen[" + this.screenName + "] TODO Clear Screen");
    	this.mediaPlayer.enableLogo(true);
    	application().post(StoppedEvent.INSTANCE);
    }
    
    public boolean isMute() {
    	return this.mediaPlayer.isMute();
    }

	public void mute() {
		this.mediaPlayer.mute();
	}
	
	public void start() {
		
		// Check if mediaFilePAth is valid to avoid starting null objects
		if(!this.mediaPath.equals("")) this.mediaPlayer.start();
	}

	public void play() {
		
		// Checkup to avoid play on not playable files or null objects
		MediaDetails data = this.mediaPlayer.getMediaDetails();
		if(data != null && !this.mediaPath.equals("")) this.mediaPlayer.play();
	}
	
	public void pause() {
		this.mediaPlayer.pause();
	}
	
	public void stop() {
		this.mediaPlayer.stop();
		this.enableLogo(true);
	}

	public boolean isPlaying() {
		return this.mediaPlayer.isPlaying();
	}

	public boolean isPlayable() {
		return this.mediaPlayer.isPlayable();
	}

	public void setPosition(float position) {
		this.mediaPlayer.setPosition(position);
	}

	public boolean isSeekable() {
		return this.mediaPlayer.isSeekable();
	}

	public long getLength() {
		return this.mediaPlayer.getLength();
	}

	public void setRate(float rate) {
		this.mediaPlayer.setRate(rate);
	}

	public libvlc_state_t getMediaState() {
		return this.mediaPlayer.getMediaPlayerState();
	}

	public void setLogoImage(BufferedImage logoImage) {
		this.mediaPlayer.setLogoImage(logoImage);
		this.mediaPlayer.setLogoPosition(libvlc_logo_position_e.centre);
		this.mediaPlayer.setLogoOpacity(100);
	}

	public void enableLogo(boolean enable) {
		this.mediaPlayer.enableLogo(enable);
	}

	public void release() {
		this.mediaPlayer.release();
	}

	public void prepareMedia(String mrl) {
		this.stop();
		this.mediaPlayer.prepareMedia(mrl);
		this.mediaPath = mrl;
	}

	public void setVideoSurface(CanvasVideoSurface newVideoSurface) {
		this.mediaPlayer.setVideoSurface(newVideoSurface);
	}

	public void setContrast(float contrast) {
		this.mediaPlayer.setContrast(contrast);
	}

	public void setVolume(int value) {
		this.mediaPlayer.setVolume(value);
	}

	public void setBrightness(float brightness) {
		this.mediaPlayer.setBrightness(brightness);
	}

	public void setSaturation(float saturation) {
		this.mediaPlayer.setSaturation(saturation);
	}

	public void setAdjustVideo(boolean adjustVideo) {
		this.mediaPlayer.setAdjustVideo(adjustVideo);
	}

	public void setGamma(float gamma) {
		this.mediaPlayer.setGamma(gamma);
	}

	public void setHue(int hue) {
		this.mediaPlayer.setHue(hue);
	}

	public void setEqualizer(Equalizer equalizer) {
		this.mediaPlayer.setEqualizer(equalizer);
	}

	public float getBrightness() {
		return this.mediaPlayer.getBrightness();
	}

	public float getContrast() {
		return this.mediaPlayer.getContrast();
	}

	public int getHue() {
		return this.mediaPlayer.getHue();
	}

	public float getSaturation() {
		return this.mediaPlayer.getSaturation();
	}

	public float getGamma() {
		return this.mediaPlayer.getGamma();
	}

	public int getVolume() {
		return this.mediaPlayer.getVolume();
	}

	public String getMediaPath() {
		return this.mediaPath;
	}

	public float getRate() {
		return this.mediaPlayer.getRate();
	}

	public float getScale() {
		return this.mediaPlayer.getScale();
	}
	
	public void setScale(float zoom) {
		this.mediaPlayer.setScale(zoom);
	}

	public void setAspectRatio(String aspectRatio) {
		this.mediaPlayer.setAspectRatio(aspectRatio);
	}
	
	public String getAspectRatio() {
		return this.mediaPlayer.getAspectRatio();
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		System.out.println("[Mouse] Clicked " + this.screenName);
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		System.out.println("[Mouse] Pressed " + this.screenName);
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		System.out.println("[Mouse] Released " + this.screenName);		
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		System.out.println("[Mouse] Entered " + this.screenName);
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
		System.out.println("[Mouse] Leaved " + this.screenName);
	}

	public void setBorder(LineBorder lineBorder) {
		this.border = lineBorder;
	}
	
	public LineBorder getBorder() {
		return this.border;
	}
	
	public boolean isRunningState() {
		libvlc_state_t state = this.getMediaState();
		return state.toString().equalsIgnoreCase("libvlc_Playing") ||
				state.toString().equalsIgnoreCase("libvlc_Paused");
	}

	public void record() {

		if(!this.recording && this.isPlaying() && this.isRunningState()) {
			
			// Set starting of record point
			System.out.println(this.screenName + " - Started record");
		
			this.elapsedTime = this.mediaPlayer.getTime();
			System.out.println(this.screenName + " - ElapsedTime=" + elapsedTime);

			this.recording = true;
			
			// FIXME from here the file opens but it is out of sync and records much more than needed
			
			String MRL  = this.mediaPlayer.mrl();
			int bits    = 2048;
			String destination = "";
			for(int i = 0; i >= 0; i++) {
				destination = this.mediaPath.split(".avi")[0] + "-record" + i + ".avi";
				File testFile = new File(destination);
				if(!testFile.exists()) break;
			};
			//String SOUT = ":sout=#transcode{vcodec=mp4v,acodec=mpga,vb=%d,start-time=%f,stop-time=%f,run-time=%f}:file{dst=%s}";
			String SOUT = ":sout=#transcode{vcodec=mp4v,acodec=mpga,vb=%d}:file{dst=%s}";
			
			float  fps = 20;
			String FPS = ":screen-fps=%f";

			int    caching = 500;
			String CACHING = ":screen-caching=%d";

			System.out.println(this.screenName + " - MediaPath=" + this.mediaPath);
			System.out.println(this.screenName + " - Destination=" + destination);
			

			float startTime = (float) ((this.elapsedTime)*(0.001));
			System.out.println(this.screenName + " - startTime="  + startTime);
			
			String[] mediaRecordOptions = new String[] {
					String.format(SOUT, bits, destination),
					String.format(FPS, fps),
					String.format(CACHING, caching)
			};

			// Create the record player
			recordMediaPlayer = this.mediaFactory.newHeadlessMediaPlayer();
			recordMediaPlayer.prepareMedia(MRL, mediaRecordOptions);
			recordMediaPlayer.play();
			recordMediaPlayer.setPosition(this.mediaPlayer.getPosition());
		}
		else stopRecord();
	}

	public void stopRecord() {
		System.out.println(this.screenName + " - Stoping Record");
		System.out.println(this.screenName + " - ElapsedTime="  + this.elapsedTime);
		
		float endTime = (float) (this.mediaPlayer.getTime()*(0.001));
		System.out.println(this.screenName + " - endTime="  + endTime);
		
		float duration   = (this.mediaPlayer.getTime() - this.elapsedTime);
		System.out.println(this.screenName + " - End point of recording=" + this.mediaPlayer.getTime());
		System.out.println(this.screenName + " - Duration=" + duration);
		
		// A delay of 1000 represents a duration of 48s
		// then 1000 / 48 = 21
//		int delay = 50;
//		for(int i = 1; i <= (duration/1000); i++) {
//		int i = 1;
//			System.out.println(this.screenName + " - Giving a litle time #" + (i*delay) + "/" + (duration/1000));
//			try {
//				Thread.sleep(delay);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		recordMediaPlayer.stop();
		recordMediaPlayer.release();
		this.elapsedTime = 0;
		this.recording = false;
		
		System.out.println(this.screenName + " - Finished Record");
	}

	public void updateVideoSurface() {
		this.setVideoSurface(this.mediaFactory.newVideoSurface(this.videoSurface()));
	}

	public boolean isRecording() {
		return this.recording;
	}

	@Override
	public void run() {
		System.out.println(this.screenName + " - Started run");
		this.record();
	}
}

