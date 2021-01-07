package com.example.exoplayer.presentation.player;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.exoplayer.R;
import com.example.exoplayer.databinding.FragmentDialogQualityBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;

import org.jetbrains.annotations.NotNull;

public class SettingDialogFragment extends DialogFragment implements  View.OnClickListener {

    private FragmentDialogQualityBinding binding ;

    private static final String TAG = "QualityDialogFragment";
    private LayoutInflater layoutInflater;
    private CheckedTextView defaultView;
    private CheckedTextView[][] trackViews;
    private boolean allowAdaptiveSelections;
    private ComponentListener componentListener;

    private DefaultTrackSelector trackSelector;
    private int rendererIndex;
    private final String playingString = "<font color=#13aab2> &nbsp;(در حال پخش) &nbsp; </font>";
    private int currentBitrate;
    private TrackGroupArray trackGroups;
    private boolean isDisabled;

    private @Nullable
    DefaultTrackSelector.SelectionOverride override;

    private static final int BITRATE_1080P = 2800000;
    private static final int BITRATE_720P = 1600000;
    private static final int BITRATE_480P = 700000;
    private static final int BITRATE_360P = 530000;
    private static final int BITRATE_240P = 400000;
    private static final int BITRATE_160P = 300000;

    private int selectedGroupIndex;
    private int selectedTrackIndex;
    private int selectedBitrate;

    private int lastChangedGroupIndex = -1;
    private int lastChangedTrackIndex = -1;
    private int lastChangedBitrate = -1;
    private Boolean isTrackChanged;


    public SettingDialogFragment(DefaultTrackSelector trackSelector,
                                 int rendererIndex,
                                 int currentBitrate,
                                 DialogInterface.OnDismissListener onDismissListener
    ) {
        this.trackSelector = trackSelector;
        this.rendererIndex = rendererIndex;
        this.currentBitrate = currentBitrate;




    }

    public static boolean willHaveContent(DefaultTrackSelector trackSelector) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        return mappedTrackInfo != null && willHaveContent(mappedTrackInfo);
    }
    public static boolean willHaveContent(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            if (showTabForRenderer(mappedTrackInfo, i)) {
                return true;
            }
        }
        return false;
    }

    private static boolean showTabForRenderer(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int rendererIndex) {
        TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
        if (trackGroupArray.length == 0) {
            return false;
        }
        int trackType = mappedTrackInfo.getRendererType(rendererIndex);
        return isSupportedTrackType(trackType);
    }

    private static boolean isSupportedTrackType(int trackType) {
        switch (trackType) {
            case C.TRACK_TYPE_VIDEO:
            case C.TRACK_TYPE_AUDIO:
            case C.TRACK_TYPE_TEXT:
                return true;
            default:
                return false;
        }
    }

    public static SettingDialogFragment newInstance(DefaultTrackSelector trackSelector,
                                                    int rendererIndex,
                                                    int currentBitrate,
                                                    DialogInterface.OnDismissListener onDismissListener

    ) {

        return new SettingDialogFragment(trackSelector, rendererIndex, currentBitrate,onDismissListener);
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @org.jetbrains.annotations.Nullable ViewGroup container, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        layoutInflater = inflater;

        componentListener = new ComponentListener();
        binding = FragmentDialogQualityBinding.inflate(inflater);

        defaultView = (CheckedTextView) layoutInflater.inflate(android.R.layout.simple_list_item_single_choice, binding.root, false);
        defaultView.setText(R.string.exo_auto_quality);
        defaultView.setTextSize(13);
        defaultView.setEnabled(false);
        defaultView.setFocusable(true);
        defaultView.setOnClickListener(componentListener);
        binding.exoTrackSelectionView.addView(defaultView);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        viewModelFactory = new PlayerFactory(Injection.INSTANCE.providePlayerRepository(),
//                Injection.INSTANCE.provideStoreOperatorRepository(),
//                Injection.INSTANCE.provideUserRepository());
//        playerViewModel = getViewModel();

        binding.confirmPlayerSettingTxv.setOnClickListener(this);
        binding.cancelPlayerSettingTxv.setOnClickListener(this);

        isTrackChanged = false;
        getSelectedInfo();
        updateViews();
        manageCheckBox();




    }


    @NotNull
    @Override
    public Dialog onCreateDialog(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext()) {
            @Override
            public void onBackPressed() {
                //DO Nothing
            }
        };

    }

    private void manageCheckBox(){


        if (selectedBitrate != -1 ) {

            binding.forAlwaysCheckbox.setChecked(true);

        }

    }
    private void updateViews() {


        MappingTrackSelector.MappedTrackInfo trackInfo =
                trackSelector == null ? null : trackSelector.getCurrentMappedTrackInfo();

        if (trackSelector == null || trackInfo == null) {
            // The view is not initialized.
            defaultView.setEnabled(false);
            return;
        }

        defaultView.setEnabled(true);

        trackGroups = trackInfo.getTrackGroups(rendererIndex);

        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        isDisabled = parameters.getRendererDisabled(rendererIndex);
        override = parameters.getSelectionOverride(rendererIndex, trackGroups);

        // Add per-track views.
        trackViews = new CheckedTextView[trackGroups.length][];
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            boolean enableAdaptiveSelections =
                    allowAdaptiveSelections
                            && trackGroups.get(groupIndex).length > 1
                            && trackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false)
                            != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED;
            trackViews[groupIndex] = new CheckedTextView[group.length];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                if (trackIndex == 0) {
                    binding.exoTrackSelectionView.addView(layoutInflater.inflate(R.layout.exo_list_divider, binding.root, false));
                }
                int trackViewLayoutId = android.R.layout.simple_list_item_single_choice;

                CheckedTextView checkedTextView =
                        (CheckedTextView) layoutInflater.inflate(trackViewLayoutId, binding.root, false);
                checkedTextView.setText(Html.fromHtml(buildBitrateString(group.getFormat(trackIndex))));
                checkedTextView.setTextSize(13);

                if (trackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                        == RendererCapabilities.FORMAT_HANDLED) {
                    checkedTextView.setFocusable(true);
                    checkedTextView.setTag(Pair.create(groupIndex, trackIndex));
                    checkedTextView.setOnClickListener(componentListener);
                } else {
                    checkedTextView.setFocusable(false);
                    checkedTextView.setEnabled(false);
                }
                trackViews[groupIndex][trackIndex] = checkedTextView;
                binding.exoTrackSelectionView.addView(checkedTextView);

            }
        }


        updateViewStates();
    }

    private void updateViewStates() {
        boolean isChecked;
        defaultView.setChecked(!isDisabled && override == null);
        for (int i = 0; i < trackViews.length; i++) {
            for (int j = 0; j < trackViews[i].length; j++) {
                isChecked = override != null && override.groupIndex == i && override.containsTrack(j);
                trackViews[i][j].setChecked(isChecked);

                if (isChecked) {

                    lastChangedGroupIndex = i;
                    lastChangedTrackIndex = j;
                    TrackGroup trackGroup = trackGroups.get(i);
                    lastChangedBitrate = trackGroup.getFormat(j).bitrate;
                    binding.forAlwaysCheckbox.setVisibility(View.VISIBLE);

                }

            }

        }

    }


    private String buildBitrateString(Format format) {
        int bitrate = format.bitrate;
        boolean isPlaying = currentBitrate == bitrate;

        if (bitrate == Format.NO_VALUE) {
            return updateText(isPlaying, "کیفیت نامشخص");
        }
        if (bitrate <= BITRATE_160P) {
            return updateText(isPlaying, "کیفیت ضعیف" + " 160P");
        }
        if (bitrate <= BITRATE_240P) {
            return updateText(isPlaying, "کیفیت متوسط" + " 240P");
        }
        if (bitrate <= BITRATE_360P) {
            return updateText(isPlaying, "کیفیت معمولی" + " 360P");
        }
        if (bitrate <= BITRATE_480P) {
            return updateText(isPlaying, "کیفیت خوب" + " 480P");
        }
        if (bitrate <= BITRATE_720P) {
            return updateText(isPlaying, "کیفیت خیلی‌خوب" + " 720P");
        }
        if (bitrate <= BITRATE_1080P) {
            return updateText(isPlaying, "کیفیت عالی" + " 1080P");
        }
        return "کیفیت عالی" + " 1080P";
    }

    private String updateText(boolean isPlaying, String quality) {
        if (isPlaying) {
            if (!quality.contains(playingString))
                return quality + playingString;
            return quality;
        }

        return quality.replace(playingString, "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_player_setting_txv: {
//                playerController.disableBtnOnShowDialog(false);
                break;
            }
            case R.id.confirm_player_setting_txv: {
                applySelection();
//                playerController.disableBtnOnShowDialog(false);
                break;
            }
        }

        dismiss();

    }


    private void applySelection() {


        DefaultTrackSelector.ParametersBuilder parametersBuilder = trackSelector.buildUponParameters();
        parametersBuilder.setRendererDisabled(rendererIndex, isDisabled);
        if (override != null) {

            parametersBuilder.setSelectionOverride(rendererIndex, trackGroups, override);

        } else {

            parametersBuilder.clearSelectionOverrides(rendererIndex);
        }
        trackSelector.setParameters(parametersBuilder);

        checkboxStatus();
    }

    private void checkboxStatus() {


        if (override == null || !binding.forAlwaysCheckbox.isChecked()) {

            //video player handle the quality
            saveOnPreference(-1, -1, -1);

        } else {

            //user handle the quality

            if (!isTrackChanged) {
                //for when user change checkbox state without changing the selected track
                updateWithLastChange();
            }

            saveOnPreference(selectedGroupIndex, selectedTrackIndex, selectedBitrate);
        }


    }

    private void saveOnPreference(int groupIndex, int trackIndex, int bitrate) {

        if (getContext() == null)
            return;
//        ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .putValue(PlayerPreference.KEY_GROUP_INDEX, groupIndex);
//        ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .putValue(PlayerPreference.KEY_TRACK_INDEX, trackIndex);
//        ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .putValue(PlayerPreference.KEY_SELECTED_BITRATE, bitrate);

    }

    private void getSelectedInfo() {
        if (getContext() == null)
            return;
//        selectedGroupIndex = (int) ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .getValue(PlayerPreference.KEY_GROUP_INDEX, PlayerPreference.DEFAULT_GROUP_INDEX);
//        selectedTrackIndex = (int) ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .getValue(PlayerPreference.KEY_TRACK_INDEX, PlayerPreference.DEFAULT_TRACK_INDEX);
//        selectedBitrate = (int) ExtenstionsKt.getAppPref(getContext()).getPlayerPreference()
//                .getValue(PlayerPreference.KEY_SELECTED_BITRATE, PlayerPreference.DEFAULT__SELECTED_BITRATE);

    }

    private void updateWithLastChange() {
        if (getContext() == null)
            return;
        selectedGroupIndex = lastChangedGroupIndex;
        selectedTrackIndex = lastChangedTrackIndex;
        selectedBitrate = lastChangedBitrate;

    }


    private class ComponentListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            onTrackSelectionClick(view);
        }

    }

    private void onTrackSelectionClick(View view) {

        if (view == defaultView) {

            onDefaultViewClicked();
        } else {

            onTrackViewClicked(view);
        }
        updateViewStates();
    }


    private void onDefaultViewClicked() {

        binding.forAlwaysCheckbox.setVisibility(View.GONE);
        isDisabled = false;
        override = null;

    }

    private void onTrackViewClicked(View view) {

        binding.forAlwaysCheckbox.setVisibility(View.VISIBLE);

        isDisabled = false;
        @SuppressWarnings("unchecked")
        Pair<Integer, Integer> tag = (Pair<Integer, Integer>) view.getTag();
        int groupIndex = tag.first;
        int trackIndex = tag.second;

        isTrackChanged = true;
        selectedGroupIndex = groupIndex;
        selectedTrackIndex = trackIndex;
        TrackGroup trackGroup = trackGroups.get(selectedGroupIndex);
        selectedBitrate = trackGroup.getFormat(selectedTrackIndex).bitrate;


        if (override == null) {
            override = new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex);

        } else {
            int[] overrideTracks = override.tracks;
            int[] tracks = getTracksRemoving(overrideTracks, override.tracks[0]);
            override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);
            override = new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex);
        }
    }

    private static int[] getTracksRemoving(int[] tracks, int removedTrack) {
        int[] newTracks = new int[tracks.length - 1];
        int trackCount = 0;
        for (int track : tracks) {
            if (track != removedTrack) {
                newTracks[trackCount++] = track;
            }
        }
        return newTracks;
    }


    @Override
    public void onResume() {
        super.onResume();

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout((width * 60 / 100),
                (height * 85) / 100);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(false);

    }
}
