package abhishek.dev.spa.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import abhishek.dev.spa.Model.Banner;
import abhishek.dev.spa.R;

public class LookBookAdapter extends RecyclerView.Adapter<LookBookAdapter.MyViewHolder> {

    Context context;
    List<Banner> lkBook;

    public LookBookAdapter(Context context, List<Banner> lkBook) {
        this.context = context;
        this.lkBook = lkBook;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.image_lookbook);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.layout_lookbook,viewGroup,false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(lkBook.get(i).getImage()).into(myViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return lkBook.size();
    }


}
