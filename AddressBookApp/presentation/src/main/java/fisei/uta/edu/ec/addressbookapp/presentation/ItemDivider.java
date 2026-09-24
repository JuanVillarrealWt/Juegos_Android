package fisei.uta.edu.ec.addressbookapp.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;
    public ItemDivider(Context context) {
        TypedArray attrs = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        divider = attrs.getDrawable(0); attrs.recycle();
    }
    @Override public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (divider == null) return;
        int left = parent.getPaddingLeft(), right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View item = parent.getChildAt(i);
            int top = item.getBottom() + ((RecyclerView.LayoutParams) item.getLayoutParams()).bottomMargin;
            divider.setBounds(left, top, right, top + divider.getIntrinsicHeight()); divider.draw(canvas);
        }
    }
}
