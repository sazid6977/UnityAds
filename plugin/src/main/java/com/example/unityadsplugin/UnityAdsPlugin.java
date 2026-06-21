package com.example.unityadsplugin;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;
import java.util.Set;
import java.util.HashSet;

public class UnityAdsPlugin extends GodotPlugin {

    private static final String TAG = "UnityAdsPlugin";
    private Activity activity;

    public UnityAdsPlugin(Godot godot) {
        super(godot);
        activity = godot.getActivity();
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "UnityAdsPlugin";
    }

    @UsedByGodot
    public void initialize(String gameId, boolean testMode) {
        activity.runOnUiThread(() -> {
            UnityAds.initialize(activity, gameId, testMode, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    emitSignal("on_initialization_complete");
                }
                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    emitSignal("on_initialization_failed", message);
                }
            });
        });
    }

    @UsedByGodot
    public void loadRewarded(String placementId) {
        UnityAds.load(placementId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                emitSignal("on_rewarded_loaded", placementId);
            }
            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                emitSignal("on_rewarded_failed", placementId, message);
            }
        });
    }

    @UsedByGodot
    public void showRewarded(String placementId) {
        activity.runOnUiThread(() -> {
            UnityAds.show(activity, placementId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    emitSignal("on_rewarded_show_failed", placementId, message);
                }
                @Override
                public void onUnityAdsShowStart(String placementId) {
                    emitSignal("on_rewarded_show_start", placementId);
                }
                @Override
                public void onUnityAdsShowClick(String placementId) {
                    emitSignal("on_rewarded_show_click", placementId);
                }
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    boolean rewarded = state == UnityAds.UnityAdsShowCompletionState.COMPLETED;
                    emitSignal("on_rewarded_show_complete", placementId, rewarded);
                }
            });
        });
    }

    @UsedByGodot
    public void loadInterstitial(String placementId) {
        UnityAds.load(placementId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                emitSignal("on_interstitial_loaded", placementId);
            }
            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                emitSignal("on_interstitial_failed", placementId, message);
            }
        });
    }

    @UsedByGodot
    public void showInterstitial(String placementId) {
        activity.runOnUiThread(() -> {
            UnityAds.show(activity, placementId, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                    emitSignal("on_interstitial_show_failed", placementId, message);
                }
                @Override
                public void onUnityAdsShowStart(String placementId) {
                    emitSignal("on_interstitial_show_start", placementId);
                }
                @Override
                public void onUnityAdsShowClick(String placementId) {
                    emitSignal("on_interstitial_show_click", placementId);
                }
                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    emitSignal("on_interstitial_show_complete", placementId);
                }
            });
        });
    }

    @NonNull
    @Override
    public Set<String> getPluginSignals() {
        Set<String> signals = new HashSet<>();
        signals.add("on_initialization_complete");
        signals.add("on_initialization_failed");
        signals.add("on_rewarded_loaded");
        signals.add("on_rewarded_failed");
        signals.add("on_rewarded_show_start");
        signals.add("on_rewarded_show_click");
        signals.add("on_rewarded_show_complete");
        signals.add("on_rewarded_show_failed");
        signals.add("on_interstitial_loaded");
        signals.add("on_interstitial_failed");
        signals.add("on_interstitial_show_start");
        signals.add("on_interstitial_show_click");
        signals.add("on_interstitial_show_complete");
        signals.add("on_interstitial_show_failed");
        return signals;
    }
}
