package com.lujaina.ldbeauty.SP;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Adapters.ServiceAdapter;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperListener;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclerItemTouchHelperService extends ItemTouchHelper.SimpleCallback {


    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelperService(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        View forgroundView = ((ServiceAdapter.MyViewHolder) viewHolder).viewForground;
        getDefaultUIUtil().clearView(forgroundView);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View forgroundView = ((ServiceAdapter.MyViewHolder) viewHolder).viewForground;
            getDefaultUIUtil().onSelected(forgroundView);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((ServiceAdapter.MyViewHolder) viewHolder).viewForground;
/*
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(R.color.red)
                .addSwipeLeftLabel("Delete")
                .addSwipeLeftActionIcon(R.drawable.delete)
                .addSwipeLeftBackgroundColor(R.color.lightPink)
                .addSwipeRightActionIcon(R.drawable.ic_event)
                .addSwipeRightLabel("Add Appointment")
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);*/
        getDefaultUIUtil().onDraw(c, recyclerView, forgroundView, dX*3/8, dY, actionState, isCurrentlyActive);

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((ServiceAdapter.MyViewHolder) viewHolder).viewForground;

        getDefaultUIUtil().onDrawOver(c, recyclerView, forgroundView, dX*3/4, dY, actionState, isCurrentlyActive);


    }
}