package net.anandsingh.dailynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import net.anandsingh.dailynews.db.DatabaseHelper;
import net.anandsingh.dailynews.text.ColorGenerator;
import net.anandsingh.dailynews.text.TextDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RecyclerView recyclerView;

    private EditText searchText;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    private DatabaseHelper databaseHelper;

    public static ArrayList<NewsItem> ITEMS = new ArrayList<NewsItem>();

    private EventBus bus = EventBus.getDefault();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView rightText = (TextView) toolbar.findViewById(R.id.logo);
        rightText.setText("ZN");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bus.register(this);

        searchText = (EditText) findViewById(R.id.editText);

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        databaseHelper = new DatabaseHelper(this);

        Cursor cursor = databaseHelper.getData(getIntent().getExtras().getString("TYPE"));

        Log.i("Log ", String.valueOf(cursor.getCount()));
        if (cursor.getCount() == 0) {

            //Fetch news from news api if there is not news in database
            FetchDataFromAPI fetchDataFromAPI = new FetchDataFromAPI();
            fetchDataFromAPI.fetchData(getIntent().getExtras().getString("URL"), getIntent().getExtras().getString("TYPE"), ItemListActivity.this);
        } else {
            ITEMS.clear();

            if (cursor.moveToFirst()) {
                do {
                    NewsItem item = new NewsItem();
                    item.setContent(cursor.getString(2));
                    item.setDate(cursor.getString(3));
                    item.setDescription(cursor.getString(4));
                    item.setDetails(cursor.getString(5));
                    ITEMS.add(item);
                } while (cursor.moveToNext());
            }
            databaseHelper.close();

            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));

            //add latest news if any
            FetchDataFromAPI fetchDataFromAPI = new FetchDataFromAPI();
            fetchDataFromAPI.fetchData(getIntent().getExtras().getString("URL"), getIntent().getExtras().getString("TYPE"), ItemListActivity.this);

        }


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if (s.length() > 0) {

                    final Collection<NewsItem> newData = Collections2.filter(ITEMS, new Predicate<NewsItem>() {
                        @Override
                        public boolean apply(NewsItem feedItem) {
                            return (feedItem.getContent().toUpperCase().startsWith(s.toString().toUpperCase()));
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new ArrayList<>(newData)));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
                        }
                    });
                }
            }
        });
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<NewsItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<NewsItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).content);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.valueOf(mValues.get(position).content.charAt(0)), mColorGenerator.getRandomColor());
            holder.textImage.setImageDrawable(drawable);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy, HH:mm ");
            Date firstParsedDate = null;
            Calendar c = Calendar.getInstance();
            try {
                firstParsedDate = dateFormat.parse(mValues.get(position).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = c.getTimeInMillis() - firstParsedDate.getTime();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            if (hours > 0)
                holder.textDate.setText(hours + " h " + (minutes - hours * 60) + " m ago");
            else
                holder.textDate.setText((minutes - hours * 60) + " m ago");


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        arguments.putString("LINK", mValues.get(position).details);
                        arguments.putString("CONTENT", mValues.get(position).description);
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("LINK", mValues.get(position).details);
                        intent.putExtra("CONTENT", mValues.get(position).description);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public NewsItem mItem;
            public final ImageView textImage;
            public final TextView textDate;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                textImage = (ImageView) view.findViewById(R.id.textImage);
                textDate = (TextView) view.findViewById(R.id.textDate);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public void onEvent(String done) {
        Cursor cursor = databaseHelper.getData(getIntent().getExtras().getString("TYPE"));
        ITEMS.clear();

        if (cursor.moveToFirst()) {
            do {
                NewsItem item = new NewsItem();
                item.setContent(cursor.getString(2));
                item.setDate(cursor.getString(3));
                item.setDescription(cursor.getString(4));
                item.setDetails(cursor.getString(5));
                ITEMS.add(item);
            } while (cursor.moveToNext());
        }
        databaseHelper.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
            }
        });


    }
}
