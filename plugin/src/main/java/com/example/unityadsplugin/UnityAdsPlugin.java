package com.example.unityadsplugin;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;
import java.util.Set;
import java.util.HashSet;

public class UnityAdsPlugin extends GodotPlugin {

    private static final String TAG = "UnityAdsPlugin";
    private Activity activity;
    private BannerView bannerView;

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

    // ─── Rewarded ───────────────────────────────────────────
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

    // ─── Interstitial ────────────────────────────────────────
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

    // ─── Banner ──────────────────────────────────────────────
    @UsedByGodot
    public void showBanner(String placementId, boolean showOnTop) {
        activity.runOnUiThread(() -> {
            bannerView = new BannerView(activity, placementId, UnityBannerSize.getDynamicSize(activity));
            bannerView.setListener(new BannerView.IListener() {
                @Override
                public void onBannerLoaded(BannerView bannerAdView) {
                    emitSignal("on_banner_loaded", placementId);
                }
                @Override
                public void onBannerShown(BannerView bannerAdView) {}
                @Override
                public void onBannerClick(BannerView bannerAdView) {
                    emitSignal("on_banner_clicked", placementId);
                }
                @Override
                public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                    emitSignal("on_banner_failed", placementId, errorInfo.errorMessage);
                }
                @Override
                public void onBannerLeftApplication(BannerView bannerAdView) {}
            });

            FrameLayout layout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                showOnTop ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
            );
            layout.addView(bannerView, params);
            bannerView.load();
        });
    }

    @UsedByGodot
    public void hideBanner() {
        activity.runOnUiThread(() -> {
            if (bannerView != null) {
                FrameLayout layout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                layout.removeView(bannerView);
                bannerView.destroy();
                bannerView = null;
                emitSignal("on_banner_hidden");
            }
        });
    }

    // ─── Signals ─────────────────────────────────────────────
    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("on_initialization_complete"));
        signals.add(new SignalInfo("on_initialization_failed", String.class));
        signals.add(new SignalInfo("on_rewarded_loaded", String.class));
        signals.add(new SignalInfo("on_rewarded_failed", String.class, String.class));
        signals.add(new SignalInfo("on_rewarded_show_start", String.class));
        signals.add(new SignalInfo("on_rewarded_show_click", String.class));
        signals.add(new SignalInfo("on_rewarded_show_complete", String.class, Boolean.class));
        signals.add(new SignalInfo("on_rewarded_show_failed", String.class, String.class));
        signals.add(new SignalInfo("on_interstitial_loaded", String.class));
        signals.add(new SignalInfo("on_interstitial_failed", String.class, String.class));
        signals.add(new SignalInfo("on_interstitial_show_start", String.class));
        signals.add(new SignalInfo("on_interstitial_show_click", String.class));
        signals.add(new SignalInfo("on_interstitial_show_complete", String.class));
        signals.add(new SignalInfo("on_interstitial_show_failed", String.class, String.class));
        signals.add(new SignalInfo("on_banner_loaded", String.class));
        signals.add(new SignalInfo("on_banner_clicked", String.class));
        signals.add(new SignalInfo("on_banner_failed", String.class, String.class));
        signals.add(new SignalInfo("on_banner_hidden"));
        return signals;
    }
}
