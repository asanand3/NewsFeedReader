package net.anandsingh.dailynews;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.anandsingh.dailynews.db.DatabaseHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by Anand Singh.
 */
public class FetchDataFromAPI {
    /**
     * An array of (news) items.
     */
    public static ArrayList<NewsItem> ITEMS = new ArrayList<NewsItem>();

    private EventBus bus = EventBus.getDefault();

    private DatabaseHelper databaseHelper;

    public void fetchData(final String url, final String type, Context context) {
        databaseHelper = new DatabaseHelper(context);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                fetchXML(response, type);
            }
        });

    }

    private void addItem(NewsItem item) {
        ITEMS.add(item);
    }

    void fetchXML(Response response, String type) throws IOException {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = new ByteArrayInputStream(response.body().string().getBytes());
            xpp.setInput(is, null);

            factory.setNamespaceAware(false);
            boolean insideItem = false;
            ITEMS.clear();
            NewsItem item = null;
            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                        item = new NewsItem();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            item.setContent(xpp.nextText().trim());
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                            item.setDetails(xpp.nextText().trim());
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (insideItem)
                            item.setDescription(xpp.nextText().trim());
                    } else if (xpp.getName().equalsIgnoreCase("comments")) {
                        if (insideItem)
                            Log.i("comments: ", xpp.nextText().trim());
                    } else if (xpp.getName().equalsIgnoreCase("pubdate")) {
                        if (insideItem)
                            item.setDate(xpp.nextText().trim());
                    } else if (insideItem) {
                        addItem(item);
                        insideItem = false;
                    }

                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }

                eventType = xpp.next(); /// move to next element
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        Set<String> titles = new HashSet<String>();
        for (NewsItem item : ITEMS) {
            if (titles.add(item.content)) {
                databaseHelper.insertData(type, item.getContent(), item.getDate(), item.getDescription(), item.getDetails());
            }
        }
        bus.post("DONE");
    }
}
