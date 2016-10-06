package com.dmplayer.uicomponent;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.ExpandableLayoutItem;

import java.util.List;

public class ExpandableLayout extends LinearLayout implements View.OnClickListener {
    private RecyclerView innerList;

    private boolean isVisible = false;

    public ExpandableLayout(Context context) {
        super(context);

        init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init()
    {
        innerList = (RecyclerView) findViewById(R.id.list);
        isVisible = (innerList.getVisibility() == VISIBLE);
    }

    @Override
    public void onClick(View v) {
        innerList.setVisibility((isVisible)? VISIBLE : INVISIBLE);

    }

    protected class InnerRecycleAdapter extends RecyclerView.Adapter<InnerRecycleAdapter.ViewHolder> {
        private Context context;
        private List<ExpandableLayoutItem> items;

        public InnerRecycleAdapter(Context context,List<ExpandableLayoutItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InnerRecycleAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String itemName = items.get(position).getName();
            String itemDetails = items.get(position).getDetails();

            holder.name.setText(itemName);
            holder.details.setText(itemDetails);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView name;
            private TextView details;

            public ViewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                details = (TextView) itemView.findViewById(R.id.details);
            }

            @Override
            public void onClick(View v) {
                Fragment f = items.get(getAdapterPosition()).getFragment();
            }
        }
    }
}
