    package com.example.myapplication;

    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.myapplication.databinding.ActivityMainBinding;
    import com.example.myapplication.ui.dashboard.DashboardFragment;

    public class MainActivity extends AppCompatActivity {

        private ActivityMainBinding binding;
        private Button buttonGetLocation;
        private EditText editTextLatitude;
        private EditText editTextLongitude;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getSupportActionBar().setTitle("Artillery Simulator");

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // EditText 인스턴스 찾기
            editTextLatitude = findViewById(R.id.editTextLatitude);
            editTextLongitude = findViewById(R.id.editTextLongitude);

            // 버튼 가져오기
            buttonGetLocation = findViewById(R.id.button_get_location);

            // 버튼 클릭 이벤트 리스너 설정
            buttonGetLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String latitudeString = editTextLatitude.getText().toString();
                    String longitudeString = editTextLongitude.getText().toString();

                    boolean isLatitudeEmpty = latitudeString.isEmpty();
                    boolean isLongitudeEmpty = longitudeString.isEmpty();

                    if (isLatitudeEmpty || isLongitudeEmpty) {
                        showEmptyFieldsAlert(isLatitudeEmpty, isLongitudeEmpty);
                    } else {
                        addDashboardFragment();
                    }
                }
            });

        }

        // EditText가 비어 있을 때 경고를 표시하는 메서드
        private void showEmptyFieldsAlert(boolean isLatitudeEmpty, boolean isLongitudeEmpty) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            if (isLatitudeEmpty && isLongitudeEmpty) {
                builder.setMessage("위도와 경도를 입력해주세요.");
            } else if (isLatitudeEmpty) {
                builder.setMessage("위도를 입력해주세요.");
            } else if (isLongitudeEmpty) {
                builder.setMessage("경도를 입력해주세요.");
            }

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }


        // addDashboardFragment() 메서드 정의
        private void addDashboardFragment() {
            String latitudeString = editTextLatitude.getText().toString();
            String longitudeString = editTextLongitude.getText().toString();

            // 위도와 경도 값을 번들에 담아 DashboardFragment에 전달
            Bundle args = new Bundle();
            args.putString("latitude", latitudeString);
            args.putString("longitude", longitudeString);

            // DashboardFragment 인스턴스 생성 및 번들 설정
            DashboardFragment fragment = new DashboardFragment();
            fragment.setArguments(args);

            // DashboardFragment를 화면에 추가
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null) // 백 스택에 추가하여 뒤로 가기를 지원합니다.
                    .commit();
        }

    }
