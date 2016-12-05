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
import java.awt.image.BufferedImage;
import java.io.File;

import uk.co.caprica.vlcj.binding.internal.libvlc_logo_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.Equalizer;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcjplayer.event.StoppedEvent;

/**
 * A single player instance and associated video surface.
 */
public class MultiPlayerInstance extends MediaPlayerEventAdapter {

    private final EmbeddedMediaPlayer mediaPlayer;

    private final Canvas videoSurface;
    
    private String mediaPath;
	private String screenName;
	private int channel;

    public MultiPlayerInstance(EmbeddedMediaPlayer mediaPlayer, int channel) {
    	this.channel = channel;
    	this.screenName = "Channel #" + channel;
    	this.mediaPath = "";
        this.mediaPlayer = mediaPlayer;
        this.videoSurface = new Canvas();
        this.videoSurface.setBackground(Color.black);

        mediaPlayer.addMediaPlayerEventListener(this);
    }

    public EmbeddedMediaPlayer mediaPlayer() {
        return mediaPlayer;
    }

    public Canvas videoSurface() {
        return videoSurface;
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
        application().checkContinuousPlay(this.channel);
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
    	application().removeFromRunningItems(this.mediaPath);
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
}

