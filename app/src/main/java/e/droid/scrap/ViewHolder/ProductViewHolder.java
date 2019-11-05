package e.droid.scrap.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import e.droid.scrap.ItemClickListener;
import e.droid.scrap.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductname, txtProductdesc,txtProductprice;
    public ImageView imageView;
    public ItemClickListener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.product_image);
        txtProductname=itemView.findViewById(R.id.product_name);
        txtProductdesc=itemView.findViewById(R.id.product_description);
        txtProductprice=itemView.findViewById(R.id.product_price);
    }
public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;

}
    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);

    }
}
