package com.example.myapplication.ui.dashboard;

import android.animation.TypeEvaluator;

import com.naver.maps.geometry.LatLng;

public class LatLngEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
        double lat = (endValue.latitude - startValue.latitude) * fraction + startValue.latitude;
        double lng = (endValue.longitude - startValue.longitude) * fraction + startValue.longitude;
        return new LatLng(lat, lng);
    }
}
