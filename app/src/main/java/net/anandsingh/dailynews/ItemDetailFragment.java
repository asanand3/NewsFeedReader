package net.anandsingh.dailynews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class ItemDetailFragment extends Fragment {

    String link;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        link = getLink();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        if (link != null) {
            TextView text = (TextView) rootView.findViewById(R.id.item_link);
            text.setText(getContent());
        }

        return rootView;
    }

    public String getLink() {
        return getArguments().getString("LINK", " ");
    }

    public String getContent() {
        return getArguments().getString("CONTENT", " ");
    }
}
