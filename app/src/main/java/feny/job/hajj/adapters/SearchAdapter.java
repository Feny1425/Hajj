package feny.job.hajj.adapters;
import static feny.job.hajj.activities.HajjiListActivity.getSearchList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import feny.job.hajj.R;
import feny.job.hajj.activities.HajjiListActivity;

// The adapter class which
// extends RecyclerView Adapter
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    // List with String type
    private HajjiListActivity hajjiListActivity;

    // Constructor for adapter class
    // which takes a list of String type
    public SearchAdapter(HajjiListActivity hajjiListActivity)
    {
        this.hajjiListActivity = hajjiListActivity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = getSearchList().get(position);
        holder.bind(s);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView filter;
        // Add more TextViews for other fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            filter = itemView.findViewById(R.id.filter);
            // Initialize other TextViews
        }

        public void bind(String s) {
            filter.setText(s);
            // Bind other fields as needed
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                hajjiListActivity.removeSearch(getSearchList().get(position));
                notifyItemRemoved(position);
            } else {
                // Handle the case where the position is invalid
            }
        }

    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return getSearchList().size();
    }
}
