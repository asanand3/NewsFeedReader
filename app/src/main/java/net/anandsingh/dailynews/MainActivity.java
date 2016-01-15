package net.anandsingh.dailynews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnNation, buttonSiTech, btnSports, btnWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnNation = (Button) findViewById(R.id.buttonNation);
        buttonSiTech = (Button) findViewById(R.id.buttonSiTech);
        btnSports = (Button) findViewById(R.id.buttonSports);
        btnWorld = (Button) findViewById(R.id.buttonWorld);

        btnNation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                String url = "http://zeenews.india.com/rss/india-national-news.xml";
                intent.putExtra("URL", url);
                intent.putExtra("TYPE", "INN");
                startActivity(intent);
            }
        });
        buttonSiTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                String url = "http://zeenews.india.com/rss/science-technology-news.xml";
                intent.putExtra("URL", url);
                intent.putExtra("TYPE", "STN");
                startActivity(intent);
            }
        });
        btnSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                String url = "http://zeenews.india.com/rss/sports-news.xml";
                intent.putExtra("URL", url);
                intent.putExtra("TYPE", "SN");
                startActivity(intent);
            }
        });
        btnWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                String url = "http://zeenews.india.com/rss/world-news.xml";
                intent.putExtra("URL", url);
                intent.putExtra("TYPE", "WN");
                startActivity(intent);
            }
        });
    }
}
