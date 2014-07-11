package omgandroid.morris.example.com.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by morris on 14-7-9.
 */
public class JSONAdapter extends BaseAdapter {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return mJsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.row_book, null);
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView)view.findViewById(R.id.img_thumbnail);
            holder.titleTextView = (TextView)view.findViewById(R.id.text_title);
            holder.authorTextView = (TextView)view.findViewById(R.id.text_author);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        JSONObject jsonObject = (JSONObject)getItem(i);
        if (jsonObject.has("cover_i")) {
            String imageID = jsonObject.optString("cover_i");
            String imageURL = IMAGE_URL_BASE + imageID + "-S.jpg";
            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }
        String bookTitle = "";
        String authorName = "";
        if (jsonObject.has("title")) {
            bookTitle = jsonObject.optString("title");
        }
        if (jsonObject.has("author_name")) {
            authorName = jsonObject.optJSONArray("author_name").optString(0);
        }
        holder.titleTextView.setText(bookTitle);
        holder.authorTextView.setText(authorName);


        return view;
    }

    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }

    public void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }
}
