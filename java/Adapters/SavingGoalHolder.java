package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.louisa.savemore.R;

import Models.SharedCost;

/**
 * Created by Louisa on 02/02/2017.
 */
public class SavingGoalHolder extends RecyclerView.ViewHolder
{
    View mView;
    Context mContext;


    public SavingGoalHolder(View itemView)
    {
        super(itemView);

        mView = itemView;
        mContext = mView.getContext();
    }



    public void setContent(SharedCost sharedCost)
    {
        TextView productName = (TextView) mView.findViewById(R.id.product_name);
        TextView amountToShare = (TextView) mView.findViewById(R.id.product_amount);
        TextView receiverEmail = (TextView) mView.findViewById(R.id.receiver);

        productName.setText(sharedCost.getName());
        amountToShare.setText(String.valueOf(sharedCost.getPrice()));
        receiverEmail.setText(sharedCost.getEmail());


    }

}