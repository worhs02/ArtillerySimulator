package com.example.myapplication.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button resetButton;

    private LatLng White_Skull_Boot_Camp;
    private LatLng receivedLatLng;

    private Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 1000; // 1초

    @SuppressLint({"MissingInflatedId", "ResourceType", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // MapView 가져오기
        mapView = root.findViewById(R.id.mapView);

        // TextView 생성 및 설정
        textView = root.findViewById(R.id.time_layout);

        // ImageView 생성 및 설정
        bombImageView = root.findViewById(R.id.bomb_image_view);

        // 리셋 버튼 생성 및 설정
        resetButton = root.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리셋 버튼 클릭 시 위도와 경도 입력 화면으로 이동
                goToInputFragment();
            }
        });

        // 네이버 지도 설정
        NaverMapSdk.getInstance(requireContext()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("y0wyj0cbep"));

        // MapView에 비동기 콜백 등록
        mapView.onCreate(savedInstanceState);
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
        White_Skull_Boot_Camp = new LatLng(38.196781, 127.292882);
        naverMap.moveCamera(CameraUpdate.scrollTo(White_Skull_Boot_Camp));

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
                polyline.setCoords(Arrays.asList(White_Skull_Boot_Camp, receivedLatLng));
                polyline.setWidth(10);
                polyline.setColor(0xFF0000FF);
                polyline.setMap(naverMap);

                double distanceInMeters = White_Skull_Boot_Camp.distanceTo(receivedLatLng);
                int timeInSeconds = (int) (distanceInMeters / 980); // 거리/속도

                // 폭탄 위치를 주기적으로 업데이트하기 위한 Runnable 객체 생성
                Runnable updateBombPositionRunnable = new Runnable() {
                    private final long startTime = System.currentTimeMillis();
                    private final long duration = timeInSeconds * 1000; // (단위: 밀리초)
                    private LatLng previousLatLng = White_Skull_Boot_Camp;
                    private Projection projection = naverMap.getProjection();

                    @Override
                    public void run() {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        if (elapsedTime > duration) {
                            // 시간이 다 되었을 때 초기화
                            resetBomb();
                            textView.setText("폭파 되었습니다");
                        } else {
                            // 현재까지의 경과 시간에 따라 폭탄의 위치를 업데이트
                            double ratio = (double) elapsedTime / duration;
                            LatLng currentLatLng = new LatLng(
                                    White_Skull_Boot_Camp.latitude + (receivedLatLng.latitude - White_Skull_Boot_Camp.latitude) * ratio,
                                    White_Skull_Boot_Camp.longitude + (receivedLatLng.longitude - White_Skull_Boot_Camp.longitude) * ratio
                            );

                            // 남은 거리 계산
                            double remainingDistance = currentLatLng.distanceTo(receivedLatLng);
                            // 예상 속도 계산
                            double currentSpeed = remainingDistance / ((double) (duration - elapsedTime) / 1000); // (단위: m/s)
                            // 예상 도착 시간 계산
                            int estimatedTimeToArrive = (int) (remainingDistance / currentSpeed); // (단위: 초)

                            // 남은 시간 표시
                            textView.setText("폭파까지 남은 시간: 약 " + estimatedTimeToArrive + " 초");

                            // 폭탄 이미지의 좌표를 지도 상의 좌표로 변환
                            PointF newPosition = projection.toScreenLocation(currentLatLng);

                            // 폭탄 이미지의 크기 고려하여 위치 설정
                            float bombImageCenterX = newPosition.x;
                            float bombImageBottomY = newPosition.y;
                            float offsetX = bombImageView.getWidth() / 2;
                            float offsetY = bombImageView.getHeight();

                            // 새로운 위치 설정
                            bombImageView.setX(bombImageCenterX - offsetX);
                            bombImageView.setY(bombImageBottomY -offsetY);

                            // 이미지 회전
                            double angle = Math.toDegrees(Math.atan2(currentLatLng.latitude - previousLatLng.latitude, currentLatLng.longitude - previousLatLng.longitude));
                            bombImageView.setRotation((float) -angle + 40); // 회전 적용

                            // 1초 뒤에 다시 호출하여 위치 업데이트
                            handler.postDelayed(this, UPDATE_INTERVAL);

                            // 이전 위치 업데이트
                            previousLatLng = currentLatLng;
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

    private void goToInputFragment() {
        // 현재 띄워져 있는 Fragment를 제거하는 함수
        requireActivity().getSupportFragmentManager().popBackStack();
        resetBomb();
    }
}

