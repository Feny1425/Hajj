package feny.job.hajj.adapters;

import static feny.job.hajj.custom.Data.hajjis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import feny.job.hajj.activities.HajjiListActivity;
import feny.job.hajj.custom.Hajji;
import feny.job.hajj.R;
import feny.job.hajj.activities.HajjiDetailActivity;

public class HajjiAdapter extends RecyclerView.Adapter<HajjiAdapter.ViewHolder> {

    private List<Hajji> hajjiList;
    private HajjiListActivity context;
    public HajjiAdapter(HajjiListActivity context) {
        this.context = context;
        this.hajjiList = hajjis.getHajjis();
        resetHighlight();
    }

    public void highlightHajji(Hajji hajji){
        hajji.setChecked(true);
        notifyDataSetChanged();
    }
    public void resetHighlight(){
        hajjis.resetCheck();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hajji, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hajji hajji = hajjis.getHajjiByPassport(hajjiList.get(position).getPassport());
        holder.bind(hajji);
    }
    @Override
    public int getItemCount() {
        return hajjiList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Hajji> filteredList) {
        this.hajjiList = new ArrayList<>(filteredList);

        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTextView;
        private final TextView passportTextView;
        private final LinearLayout linearLayout;
        // Add more TextViews for other fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            passportTextView = itemView.findViewById(R.id.passportTextView);
            linearLayout = itemView.findViewById(R.id.card);
            // Initialize other TextViews
        }

        public void bind(Hajji hajji) {
            nameTextView.setText(hajji.getName());
            passportTextView.setText(hajji.getPassport());
            if(hajji.isChecked()){
                linearLayout.setBackgroundColor(Color.GREEN);
                nameTextView.setTextColor(Color.BLACK);
                passportTextView.setTextColor(Color.BLACK);
            }
            else {
                linearLayout.setBackgroundColor(Color.DKGRAY);
                nameTextView.setTextColor(Color.WHITE);
                passportTextView.setTextColor(Color.WHITE);
            }

            // Bind other fields as needed
        }


        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Hajji hajji = hajjiList.get(position);
                if(context.isAutoCheck()){
                    hajji.setChecked(!hajji.isChecked());
                    notifyDataSetChanged();
                    return;
                }
                Gson gson = new Gson();
                // Launch detail activity with Hajji data
                Intent intent = new Intent(context, HajjiDetailActivity.class);
                intent.putExtra("hajji", gson.toJson(hajji));
                context.startActivity(intent);

            }
        }
    }
}
