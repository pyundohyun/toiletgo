package com.pdh.lenovo.gubhang;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchFragmentActivity extends FragmentActivity {

    String TAG = "SearchFragmentActivity";
    AssetManager assetManager;
    EditText keyword;
    String search_keyword;
    JSONObject jsonObject;
    JSONArray jArray;
    ListView list;
    mapFragment mapFragment_map;
    Bundle args;
    //동적 배열
    List<Integer> data_array = new ArrayList<Integer>();

    // 프로그래스
    ProgressDialog dialog;
    int pos_dilaog=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_toilet);
        Log.d("TAG", "onCreate()");

        assetManager = getResources().getAssets();

        //검색어
        keyword = (EditText) findViewById(R.id.search_keyword);
        list = (ListView) findViewById(R.id.list);


        Button bnt_toilet_search = (Button) findViewById(R.id.btn_search);
        bnt_toilet_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 검색어 받음
                    search_keyword = String.valueOf(keyword.getText());
                    Log.d("===============keyword", String.valueOf(search_keyword));

                    // 검색어가 입력안했을때
                    if (search_keyword.length() == 0) {
                        Toast.makeText(SearchFragmentActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //검색어가 숫자일때
                    else if(Pattern.matches("^[0-9]*$", search_keyword))
                    {
                        Toast.makeText(SearchFragmentActivity.this, "검색어를 다시 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //검색어가 영어일때
                    else if(Pattern.matches("^[a-zA-Z]*$", search_keyword))
                    {
                        Toast.makeText(SearchFragmentActivity.this, "검색어를 다시 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 검색어가 있을때
                    else {
                        Log.d("tag","검색중입니다");
                        Log.d("data_array", String.valueOf(data_array));
                        data_array.clear();

                        // 멀티쓰레드 프로그래스다이얼로그
                        new ProgressDialogAction().execute(70); // 다이얼로그의 max 값으로 100 전달

                        //Toast.makeText(SearchFragmentActivity.this, "검색중 입니다", Toast.LENGTH_SHORT).show();
                        // asset에서 json 읽고
                        AssetManager.AssetInputStream ais = (AssetManager.AssetInputStream) assetManager.open("Location.json");
                        BufferedReader br = new BufferedReader(new InputStreamReader(ais, "utf-8"));

                        StringBuffer sb = new StringBuffer();
                        String result = null;

                        while ((result = br.readLine()) != null) {
                            sb.append(result);
                        }

                        jsonObject = new JSONObject(sb.toString());

                        // DATA 를 JSONARRAY 넣고
                        jArray = new JSONArray(jsonObject.getString("DATA"));
                        // 리스트로 뿌리기 위함
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchFragmentActivity.this, R.layout.my_text_view);
                        //simple_list_item_1
                        // josn 뽑아서 list에 넣는데 해당하는 값만 넣기
                        for (int i = 0; i < jArray.length(); i++) {
                            //벨류값이랑 매칭
                            if (jArray.getJSONObject(i).getString("FNAME").contains(search_keyword)) {

                                final int number = i;
                                Log.d("i", String.valueOf(number));


                                // index들 배열에 추가
                                data_array.add(number);
                                adapter.add(jArray.getJSONObject(number).getString("FNAME").toString());
                                // 리스트에 쏨
                                list.setAdapter(adapter);
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // 배열선언해서 버튼별로 이벤트 뿌리면됨
                                        // 나중에 postion이 해당하는 i값이 되면 밑에 넘버를 다 i로 바꿈
                                        // position은 인덱스가 될것, i[1] = 753, i[2] = 1420,...

                                        //위도,경도값 넘김
                                        try {
                                            Log.d("position", String.valueOf(position));
                                            Log.d("Y_WGS84", jArray.getJSONObject(data_array.get(position)).getString("Y_WGS84").toString());
                                            Log.d("X_WGS84", jArray.getJSONObject(data_array.get(position)).getString("X_WGS84").toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            args = new Bundle();
                                            args.putDouble("Y", Double.parseDouble(jArray.getJSONObject(data_array.get(position)).getString("Y_WGS84").toString()));
                                            args.putDouble("X", Double.parseDouble(jArray.getJSONObject(data_array.get(position)).getString("X_WGS84").toString()));
                                            args.putString("Location", jArray.getJSONObject(data_array.get(position)).getString("FNAME").toString());
                                            args.putString("Type", jArray.getJSONObject(data_array.get(position)).getString("ANAME").toString());

                                            Log.d("args===============", String.valueOf(args));
                                            mapFragment_map = new mapFragment();
                                            mapFragment_map.setArguments(args);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // 프레그먼트 트랜젝션위함
                                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, mapFragment_map).addToBackStack(null).commit();
                                    }
                                });

                            }

                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult()");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d("TAG", "onAttachFragment()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("TAG", "onDetachedFromWindow()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "onPause()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "onStop()");
    }

    private class ProgressDialogAction  extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog= new ProgressDialog(SearchFragmentActivity.this); //ProgressDialog 객체 생성
            dialog.setMessage("검색중.....");             //ProgressDialog 메세지
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //막대형태의 ProgressDialog 스타일 설정
            dialog.setCanceledOnTouchOutside(false); //ProgressDialog가 진행되는 동안 dialog의 바깥쪽을 눌러 종료하는 것을 금지
            dialog.show(); //ProgressDialog 보여주기
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]); //전달받은 pos_dialog값으로 ProgressDialog에 변경된 위치 적용
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss(); //ProgressDialog 보이지 않게 하기
            dialog=null;      //참조변수 초기화
        }

        @Override
        protected String doInBackground(Integer... params) {
            while( pos_dilaog < params[0]){ //현재 ProgessDialog의 위치가 100보다 작은가? 작으면 계속 Progress

                pos_dilaog++;

                //ProgressDialog에 변경된 위치 적용 ..
                publishProgress(pos_dilaog);  //onProgressUpdate()메소드 호출.

                try {
                    Thread.sleep(50); //0.1초간 스레드 대기 ..예외처리 필수
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }//while

            //while을 끝마치고 여기까지 오면 Progress가 종료되었다는 것을 의미함.
            pos_dilaog=0; //다음 프로세스를 위해 위치 초기화

            return "Complete Load"; // AsyncTask 의 작덥종료 후 "Complete Load" String 결과 리턴
        }
    }
}
