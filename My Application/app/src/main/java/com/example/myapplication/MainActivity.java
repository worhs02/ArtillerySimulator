package com.example.myapplication;

import android.os.Bundle;
import android.util.Log; // Log를 사용하기 위해 추가
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // EditText를 사용하기 위해 추가
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button buttonGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // 버튼 가져오기
        buttonGetLocation = findViewById(R.id.button_get_location);

        // 버튼 클릭 이벤트 리스너 설정
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭되었을 때 실행되는 코드 작성
                getLocation();
            }
        });
    }

    // 버튼 클릭 시 실행될 메서드
    private void getLocation() {
        // 버튼이 클릭되었을 때 실행되는 작업을 여기에 구현
        // EditText에서 위도와 경도 값 가져오기
        EditText editTextLatitude = findViewById(R.id.editTextLatitude);
        EditText editTextLongitude = findViewById(R.id.editTextLongitude);

        String latitudeString = editTextLatitude.getText().toString();
        String longitudeString = editTextLongitude.getText().toString();

        // 사용자가 입력한 위도와 경도 출력
        Log.d("MainActivity", "사용자가 입력한 위도: " + latitudeString);
        Log.d("MainActivity", "사용자가 입력한 경도: " + longitudeString);
    }
}
