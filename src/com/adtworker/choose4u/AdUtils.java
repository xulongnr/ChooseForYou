package com.adtworker.choose4u;

import android.app.Activity;
import android.view.ViewGroup;
import cn.domob.android.ads.DomobAdView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdUtils {
	public static void setupDomobAdView(Activity context, ViewGroup parent) {
		DomobAdView mAdview = new DomobAdView(context, "56OJyNhYuMt0S8js8X",
				DomobAdView.INLINE_SIZE_320X50);
		mAdview.setKeyword("life luck choice");
		parent.addView(mAdview);
	}

	public static void setupAdmobAdView(Activity context, ViewGroup parent) {
		AdView adView = new AdView(context, AdSize.BANNER, "a14ff64fc94299e");
		parent.addView(adView);
		adView.loadAd(new AdRequest());
	}

	public static void setupSuizongAdView(Activity context, ViewGroup parent) {
		com.suizong.mobplate.ads.AdView adView = new com.suizong.mobplate.ads.AdView(
				context, com.suizong.mobplate.ads.AdSize.BANNER,
				"500b76497c6e5e36fcaa3509");
		parent.addView(adView);
		com.suizong.mobplate.ads.AdRequest adRequest = new com.suizong.mobplate.ads.AdRequest();
		adRequest.setTesting(false);
		adView.loadAd(adRequest);
	}

}
