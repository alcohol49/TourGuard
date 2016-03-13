package com.hackathon.tourguard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbySituation extends ListActivity {

    public String[] mHeads = {
            "附近有龍捲風",
            "附近有淹水情形",
            "預定搭程班機因龍捲風取消"
    };
    public String[] mDescriptions = {
            "台北 101 附近出現四級龍捲風，有乳牛落下可能，請注意安全。",
            "台北車站附近有淹水情況，請勿靠近。",
            "班機因天候異常取消。"

    };
    public String[] mSources = {
            "中央氣象局",
            "交通部",
            "中正國際機場"
    };
    public double[] mLats = {
            25.0335,
            25.0463,
            25.0763
    };
    public double[] mLngs = {
            121.5641,
            121.5175,
            121.2238
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_situation);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mHeads);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, Detail.class);
        intent.putExtra(Detail.KEY_HEAD, mHeads[position])
                .putExtra(Detail.KEY_DISCRIPTION, mDescriptions[position])
                .putExtra(Detail.KEY_SOURCE, mSources[position])
                .putExtra(Detail.KEY_LAT, mLats[position])
                .putExtra(Detail.KEY_LNG, mLngs[position]);

        startActivity(intent);
    }
}
