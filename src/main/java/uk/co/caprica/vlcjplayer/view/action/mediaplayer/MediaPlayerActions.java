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
 * Copyright 2015 Caprica Software Limited.
 */

package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import static uk.co.caprica.vlcjplayer.view.action.Resource.resource;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.google.common.collect.ImmutableList;

import multiplayer.MultiScreensHandler;
import uk.co.caprica.vlcj.player.MediaPlayer;

// FIXME i think none of these actions need be public now?
//       the dynamic ones currently are unfortunately... for now... (e.g. videotrack)

// FIXME the play action here could listen to the mediaplayer and change its icon accordingly

public final class MediaPlayerActions {

    private final List<Action> playbackSpeedActions;
    private final List<Action> playbackSkipActions;
    private final List<Action> playbackChapterActions;
    private final List<Action> playbackControlActions;

    private final List<Action> audioStereoModeActions;
    private final List<Action> audioControlActions;

    private final List<Action> videoQuantityActions;
    private final List<Action> videoZoomActions;
    private final List<Action> videoAspectRatioActions;
    private final List<Action> videoCropActions;

    private final Action       playbackPlayAction;
    private final Action       playbackStopAction;

    private final Action       videoSnapshotAction;

    public MediaPlayerActions(MultiScreensHandler mediaPlayer) {
        playbackSpeedActions    = newPlaybackSpeedActions   (mediaPlayer.getHeadPlayer());
        playbackSkipActions     = newPlaybackSkipActions    (mediaPlayer.getHeadPlayer());
        playbackChapterActions  = newPlaybackChapterActions (mediaPlayer.getHeadPlayer());
        playbackControlActions  = newPlaybackControlActions (mediaPlayer.getHeadPlayer());
        audioStereoModeActions  = newAudioStereoModeActions (mediaPlayer.getHeadPlayer());
        audioControlActions     = newAudioControlActions    (mediaPlayer.getHeadPlayer());
        videoQuantityActions    = newVideoQuantityActions   (mediaPlayer.getHeadPlayer());
        videoZoomActions        = newVideoZoomActions       (mediaPlayer.getHeadPlayer());
        videoAspectRatioActions = newVideoAspectRatioActions(mediaPlayer.getHeadPlayer());
        videoCropActions        = newVideoCropActions       (mediaPlayer.getHeadPlayer());

        playbackPlayAction      = new PlayAction    (resource("menu.playback.item.play" ), mediaPlayer.getHeadPlayer());
        playbackStopAction      = new StopAction    (resource("menu.playback.item.stop" ), mediaPlayer.getHeadPlayer());
        videoSnapshotAction     = new SnapshotAction(resource("menu.video.item.snapshot"), mediaPlayer.getHeadPlayer());
    }

    private List<Action> newVideoQuantityActions(MediaPlayer mediaPlayer) {
		List<Action> actions = new ArrayList<>();
        actions.add(new ScreenQuantityAction(resource("menu.video.item.quantity.item.1"    ), mediaPlayer, 1));
        actions.add(new ScreenQuantityAction(resource("menu.video.item.quantity.item.2"    ), mediaPlayer, 2));
        actions.add(new ScreenQuantityAction(resource("menu.video.item.quantity.item.4"    ), mediaPlayer, 4));
        actions.add(new ScreenQuantityAction(resource("menu.video.item.quantity.item.6"    ), mediaPlayer, 6));
        actions.add(new ScreenQuantityAction(resource("menu.video.item.quantity.item.8"    ), mediaPlayer, 8));
        return ImmutableList.copyOf(actions);
	}

	private List<Action> newPlaybackSpeedActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new RateAction(resource("menu.playback.item.speed.item.x4"    ), mediaPlayer, 5));
        actions.add(new RateAction(resource("menu.playback.item.speed.item.x2"    ), mediaPlayer, 4));
        actions.add(new RateAction(resource("menu.playback.item.speed.item.normal"), mediaPlayer, 3));
        actions.add(new RateAction(resource("menu.playback.item.speed.item./2"    ), mediaPlayer, 2));
        actions.add(new RateAction(resource("menu.playback.item.speed.item./4"    ), mediaPlayer, 1));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newPlaybackSkipActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new SkipAction(resource("menu.playback.item.skipForward" ), mediaPlayer,  10000));
        actions.add(new SkipAction(resource("menu.playback.item.skipBackward"), mediaPlayer, -10000));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newPlaybackChapterActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new PreviousChapterAction(resource("menu.playback.item.previousChapter"), mediaPlayer));
        actions.add(new NextChapterAction    (resource("menu.playback.item.nextChapter"    ), mediaPlayer));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newPlaybackControlActions(MediaPlayer embeddedMediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new PlayAction(resource("menu.playback.item.play"), embeddedMediaPlayer));
        actions.add(new StopAction(resource("menu.playback.item.stop"), embeddedMediaPlayer));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newAudioStereoModeActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new StereoModeAction(resource("menu.audio.item.stereoMode.item.stereo" ), mediaPlayer, null));
        actions.add(new StereoModeAction(resource("menu.audio.item.stereoMode.item.left"   ), mediaPlayer, null));
        actions.add(new StereoModeAction(resource("menu.audio.item.stereoMode.item.right"  ), mediaPlayer, null));
        actions.add(new StereoModeAction(resource("menu.audio.item.stereoMode.item.reverse"), mediaPlayer, null));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newAudioControlActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new VolumeAction(resource("menu.audio.item.increaseVolume"), mediaPlayer,  10));
        actions.add(new VolumeAction(resource("menu.audio.item.decreaseVolume"), mediaPlayer, -10));
        actions.add(new MuteAction  (resource("menu.audio.item.mute"          ), mediaPlayer     ));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newVideoZoomActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ZoomAction(resource("menu.video.item.zoom.item.quarter" ), mediaPlayer, 0.25f));
        actions.add(new ZoomAction(resource("menu.video.item.zoom.item.half"    ), mediaPlayer, 0.50f));
        actions.add(new ZoomAction(resource("menu.video.item.zoom.item.original"), mediaPlayer, 1.00f));
        actions.add(new ZoomAction(resource("menu.video.item.zoom.item.double"  ), mediaPlayer, 2.00f));
        // FIXME maybe need a zoom default of 0.0 (or is this just fit window?)
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newVideoAspectRatioActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new AspectRatioAction(resource("menu.video.item.aspectRatio.item.default"), mediaPlayer, null));
        actions.add(new AspectRatioAction("16:9"  , mediaPlayer, "16:9"   ));
        actions.add(new AspectRatioAction("4:3"   , mediaPlayer, "4:3"    ));
        actions.add(new AspectRatioAction("1:1"   , mediaPlayer, "1:1"    ));
        actions.add(new AspectRatioAction("16:10" , mediaPlayer, "16:10"  ));
        actions.add(new AspectRatioAction("2.21:1", mediaPlayer, "221:100"));
        actions.add(new AspectRatioAction("2.35:1", mediaPlayer, "235:100"));
        actions.add(new AspectRatioAction("2.39:1", mediaPlayer, "239:100"));
        actions.add(new AspectRatioAction("5:4"   , mediaPlayer, "5:4"    ));
        return ImmutableList.copyOf(actions);
    }

    private List<Action> newVideoCropActions(MediaPlayer mediaPlayer) {
        List<Action> actions = new ArrayList<>();
        actions.add(new CropAction(resource("menu.video.item.crop.item.default"), mediaPlayer, null));
        actions.add(new CropAction("16:10" , mediaPlayer, "16:10"  ));
        actions.add(new CropAction("16:9"  , mediaPlayer, "16:9"   ));
        actions.add(new CropAction("4:3"   , mediaPlayer, "4:3"    ));
        actions.add(new CropAction("1.85:1", mediaPlayer, "185:100"));
        actions.add(new CropAction("2.21:1", mediaPlayer, "221:100"));
        actions.add(new CropAction("2.35:1", mediaPlayer, "235:100"));
        actions.add(new CropAction("2.39:1", mediaPlayer, "239:100"));
        actions.add(new CropAction("5:3"   , mediaPlayer, "5:3"    ));
        actions.add(new CropAction("5:4"   , mediaPlayer, "5:4"    ));
        actions.add(new CropAction("1:1"   , mediaPlayer, "1:1"    ));
        return ImmutableList.copyOf(actions);
    }

    public List<Action> playbackSpeedActions() {
        return playbackSpeedActions;
    }

    public List<Action> playbackSkipActions() {
        return playbackSkipActions;
    }

    public List<Action> playbackChapterActions() {
        return playbackChapterActions;
    }

    public List<Action> playbackControlActions() {
        return playbackControlActions;
    }

    public List<Action> audioStereoModeActions() {
        return audioStereoModeActions;
    }

    public List<Action> audioControlActions() {
        return audioControlActions;
    }

    public List<Action> videoZoomActions() {
        return videoZoomActions;
    }
    
    public List<Action> videoQuantityActions() {
        return videoQuantityActions;
    }

    public List<Action> videoAspectRatioActions() {
        return videoAspectRatioActions;
    }

    public List<Action> videoCropActions() {
        return videoCropActions;
    }

    public Action playbackPlayAction() {
        return playbackPlayAction;
    }

    public Action playbackStopAction() {
        return playbackStopAction;
    }

    public Action videoSnapshotAction() {
        return videoSnapshotAction;
    }
}
