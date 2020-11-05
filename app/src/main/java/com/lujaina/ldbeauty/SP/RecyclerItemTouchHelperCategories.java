package com.lujaina.ldbeauty.SP;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Adapters.CategoryAdapter;
import com.lujaina.ldbeauty.Interfaces.RecyclerItemTouchHelperListener;

public class RecyclerItemTouchHelperCategories extends ItemTouchHelper.SimpleCallback {


    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelperCategories(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener =listener;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if(listener != null){
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        View forgroundView = ((CategoryAdapter.MyViewHolder)viewHolder).viewForground;
        getDefaultUIUtil().clearView(forgroundView);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder !=null){
            View forgroundView = ((CategoryAdapter.MyViewHolder)viewHolder).viewForground;
            getDefaultUIUtil().onSelected(forgroundView);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((CategoryAdapter.MyViewHolder)viewHolder).viewForground;

        getDefaultUIUtil().onDraw(c,recyclerView,forgroundView, dX,dY,actionState,isCurrentlyActive);

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((CategoryAdapter.MyViewHolder)viewHolder).viewForground;

        getDefaultUIUtil().onDrawOver(c,recyclerView,forgroundView, dX,dY,actionState,isCurrentlyActive);


    }
}
