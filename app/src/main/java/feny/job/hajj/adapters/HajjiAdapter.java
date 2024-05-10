package feny.job.hajj.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import feny.job.hajj.Hajji;
import feny.job.hajj.R;
import feny.job.hajj.activities.HajjiDetailActivity;

public class HajjiAdapter extends RecyclerView.Adapter<HajjiAdapter.ViewHolder> {

    private List<Hajji> hajjiList;
    private Context context;
    public HajjiAdapter(Context context, List<Hajji> hajjiList) {
        this.context = context;
        this.hajjiList = hajjiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hajji, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hajji hajji = hajjiList.get(position);
        holder.bind(hajji);
    }

    @Override
    public int getItemCount() {
        return hajjiList.size();
    }
    public void filterList(List<Hajji> filteredList) {
        this.hajjiList = new ArrayList<>(filteredList);
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView passportTextView;
        // Add more TextViews for other fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            passportTextView = itemView.findViewById(R.id.passportTextView);
            // Initialize other TextViews
        }

        public void bind(Hajji hajji) {
            nameTextView.setText(hajji.getName());
            passportTextView.setText(hajji.getPassport());
            // Bind other fields as needed
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Hajji hajji = hajjiList.get(position);
                Gson gson = new Gson();
                // Launch detail activity with Hajji data
                Intent intent = new Intent(context, HajjiDetailActivity.class);
                intent.putExtra("hajji", gson.toJson(hajji));
                context.startActivity(intent);

            }
        }
    }
}
