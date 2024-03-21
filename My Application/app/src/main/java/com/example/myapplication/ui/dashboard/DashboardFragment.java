package com.example.myapplication.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.Arrays;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private TextView textView;
    private ImageView bombImageView;

    private LatLng seoulCityHall;
    private LatLng receivedLatLng;

    private Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 1000; // 1초

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // MapView 가져오기
        mapView = root.findViewById(R.id.mapView);

        // TextView 생성 및 설정
        textView = root.findViewById(R.id.time_layout);

        // ImageView 생성 및 설정
        bombImageView = root.findViewById(R.id.bomb_image_view);

        // 네이버 지도 설정
        NaverMapSdk.getInstance(requireContext()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("y0wyj0cbep"));

        // MapView에 비동기 콜백 등록
        mapView.getMapAsync(this);

        // TextView 설정
        textView.setText("폭파까지 남은 시간: ???");

        return root;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 네이버 지도가 준비되면 호출됩니다.
        // 이곳에서 지도 초기화 및 설정 작업을 수행할 수 있습니다.

        // 서울 시청의 좌표를 설정하여 지도를 이동시킴
        seoulCityHall = new LatLng(37.5662952, 126.9779451);
        naverMap.moveCamera(CameraUpdate.scrollTo(seoulCityHall));

        // 마커 추가 및 선 그리기
        Bundle args = getArguments();
        if (args != null) {
            String latitudeString = args.getString("latitude");
            String longitudeString = args.getString("longitude");

            if (latitudeString != null && longitudeString != null) {
                double latitude = Double.parseDouble(latitudeString.trim());
                double longitude = Double.parseDouble(longitudeString.trim());

                receivedLatLng = new LatLng(latitude, longitude);
                Marker receivedMarker = new Marker();
                receivedMarker.setPosition(receivedLatLng);
                receivedMarker.setMap(naverMap);

                PolylineOverlay polyline = new PolylineOverlay();
                polyline.setCoords(Arrays.asList(seoulCityHall, receivedLatLng));
                polyline.setWidth(10);
                polyline.setColor(0xFF0000FF);
                polyline.setMap(naverMap);

                double distanceInMeters = seoulCityHall.distanceTo(receivedLatLng);
                int timeInSeconds = (int) (distanceInMeters / 1000); // 소수점 제거 후 정수로 변환
                textView.setText("폭파까지 남은 시간: 약 " + timeInSeconds + " 초");

                // 회전 각도 계산
                double angle = Math.toDegrees(Math.atan2(receivedLatLng.longitude - seoulCityHall.longitude,
                        receivedLatLng.latitude - seoulCityHall.latitude));

                // 미사일 이미지 회전
                bombImageView.setRotation((float) angle - 40);


                // 이동 거리 계산
                double stepX = (receivedLatLng.latitude - seoulCityHall.latitude) / timeInSeconds;
                double stepY = (receivedLatLng.longitude - seoulCityHall.longitude) / timeInSeconds;

                // 폭탄 위치를 주기적으로 업데이트하기 위한 Runnable 객체 생성
                Runnable updateBombPositionRunnable = new Runnable() {
                    private final long startTime = System.currentTimeMillis();
                    private final long duration = 300000; // 5분 (단위: 밀리초)
                    private LatLng previousLatLng = seoulCityHall;

                    @Override
                    public void run() {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        if (                        elapsedTime >= duration) {
                            // 시간이 다 되었을 때 초기화
                            resetBomb();
                        } else {
                            // 현재까지의 경과 시간에 따라 폭탄의 위치를 업데이트
                            double ratio = (double) elapsedTime / duration;
                            LatLng currentLatLng = new LatLng(
                                    previousLatLng.latitude + stepX * ratio,
                                    previousLatLng.longitude + stepY * ratio
                            );

                            // 폭탄 이미지의 좌표를 지도 상의 좌표로 변환
                            Projection projection = naverMap.getProjection();
                            PointF newPosition = projection.toScreenLocation(currentLatLng);

                            // 새로운 위치 설정
                            bombImageView.setX(newPosition.x);
                            bombImageView.setY(newPosition.y);

                            // 이전 위치 업데이트
                            previousLatLng = currentLatLng;

                            // 1초 뒤에 다시 호출하여 위치 업데이트
                            handler.postDelayed(this, UPDATE_INTERVAL);
                        }
                    }
                };

                // 핸들러를 사용하여 폭탄 위치 업데이트를 1초마다 호출
                handler.postDelayed(updateBombPositionRunnable, UPDATE_INTERVAL);
            }
        }
    }

    private void resetBomb() {
        // 폭탄 초기화 작업 수행
        bombImageView.setVisibility(View.INVISIBLE); // 폭탄 이미지를 안 보이게 함
        // 시점을 도착지의 좌표에 고정
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                naverMap.moveCamera(CameraUpdate.scrollTo(receivedLatLng));
            }
        });
        // 핸들러 중지
        handler.removeCallbacksAndMessages(null);
    }
}

