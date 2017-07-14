package com.pdh.lenovo.gubhang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class mapFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "mapFragment";

    GoogleMap mMap;
    View view;
    Double x_position ;
    Double y_position ;
    String destination;
    String type;

/*
    String search_url = "https://apis.daum.net/local/geo/coord2detailaddr?";
    String api_key = "apikey=e6d05978048f1a56eab446819ee060b8";
    String x_point = "&x=126.993019";
    String y_point = "&y=37.49091190000001";
    String inputCoordSystem = "&inputCoordSystem=WGS84";
    String output = "&output=json";

    String request = search_url + api_key + x_point + y_point + inputCoordSystem + output;
*/

    MarkerOptions marker;
    LatLng latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_maps, container, false);
        Log.d("TAG", "onCreateView()");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // 애드몹
        AdView mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.d("TAG", "onMapReady()");
        mMap = googleMap;
        // 위도 경도 뿌려줌
        // activity에서 보낸 값 (위도 , 경도)
        Bundle extra = getArguments();
        Log.d("extra", String.valueOf(extra));
        // 위도 경도, 장소명, 구분
        x_position = extra.getDouble("X");
        y_position = extra.getDouble("Y");
        destination = extra.getString("Location");
        type = extra.getString("Type");

        latLng = new LatLng(y_position, x_position);
        //Log.d("latLng", String.valueOf(latLng));

        //현재위치
        mMap.setMyLocationEnabled(true);



        // 현재 위치 누르면
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //현재 위도,경도
                Double Latitude = mMap.getMyLocation().getLatitude();
                Double Longitude = mMap.getMyLocation().getLongitude();

                Log.d("현재위도", String.valueOf(Latitude));
                Log.d("현재경도", String.valueOf(Longitude));

                latLng = new LatLng(Latitude, Longitude);

                // 폴리라인 (내위치, 선택위치)
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(Latitude, Longitude), new LatLng(y_position, x_position))
                        .width(5)
                        .color(Color.RED));

                //거리 구하는 메소드 부름
                int between_distance = (int) calDistance(Latitude,Longitude,y_position,x_position);
                //사람 걷는 속도 시속 4km(4000m)/1h(60분)  => 1m 0.015분 1000m 15분
                int time = between_distance/60;
                int minute;
                int hour;

                Log.d("시간",time+"분");

                // 거리측정후 시간표시
                TextView txt = (TextView)view.findViewById(R.id.text);
                if(time>60)
                {
                    hour = (int) (time/60);
                    minute = (int) (time%60);
                    txt.setText("예상시간 : "+hour+"시간"+minute+"분");
                }
                else{
                    txt.setText("예상시간 : "+time+"분");
                }
                txt.setBackgroundColor(Color.WHITE);

                // 위치 표시
                TextView txt_location = (TextView)view.findViewById(R.id.text_location);
                txt_location.setText("목적지 :" + destination);
                txt_location.setBackgroundColor(Color.WHITE);
                // 카메라 다시 약간 멀어지게
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

     /*           //http://map.daum.net/link/to/카카오판교오피스,37.402056,127.108212
                // get방식 호출
                OkHttpClient client = new OkHttpClient();
                HttpUrl httpUrl = new HttpUrl.Builder()
                        .scheme("https")
                        .host("apis.daum.net")
                        .addPathSegment("local/geo/coord2detailaddr")
                        .addQueryParameter("apikey", "e6d05978048f1a56eab446819ee060b8")
                        .addQueryParameter("x", "126.993019")
                        .addQueryParameter("y", "37.49091190000001")
                        .addQueryParameter("inputCoordSystem", "WGS84")
                        .addQueryParameter("output", "json")
                        .build();

                Request request = new Request.Builder()
                        .url(httpUrl)
                        .build();

                // 클라이언트 요청
                client.newCall(request).enqueue(callbackAfterGettingMessage);
*/
                return false;
            }
        });

        // 마커 설정
        marker = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromAsset("icon_to.png"))
                .title(destination)
                .snippet(type);

        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        // 마커 누르면 나오는 이벤트
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 다이얼로그 창 띄워서 쾌변송 서비스 안내
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                alert_confirm.setMessage("쾌변송을 서비스를 시작합니다").setCancelable(false).setPositiveButton("재생",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                // 서비스 시작할것!!
                                getActivity().startService(new Intent(getActivity(),StartStickService.class));
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 취소
                                return;
                            }
                        }).setNeutralButton("정지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().stopService(new Intent(getActivity(),StartStickService.class));
                    }
                });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                return false;
            }
        });
    }


    //주소 (구글은 지원안되서 다음사용)
    //https://apis.daum.net/local/geo/coord2detailaddr?apikey=e6d05978048f1a56eab446819ee060b8&x=126.993019&y=37.49091190000001&inputCoordSystem=WGS84&output=json
/*
    private Callback callbackAfterGettingMessage = new Callback() {
        @Override
        // 실패후 콜백
        public void onFailure(Request request, IOException e) {
            Log.d("fail", "실패");
        }

        @Override

        // 성공후 콜백
        public void onResponse(Response response) throws IOException {
            final String strJsonOutput = response.body().string();
            //final JSONObject jsonOutput = JsonUtil.getJSONObjectFrom(strJsonOutput);
            Log.d("sucess", "성공" + strJsonOutput);
        }

    };*/

    // 위도 경도로 거리구함
    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }


}

