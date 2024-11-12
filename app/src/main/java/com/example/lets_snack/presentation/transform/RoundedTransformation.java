package com.example.lets_snack.presentation.transform;

import com.squareup.picasso.Transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class RoundedTransformation implements Transformation {

    private final int radius;
    private final int margin;

    public RoundedTransformation(int radius, int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Path path = new Path();
        path.addRoundRect(
                new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin),
                radius,
                radius,
                Path.Direction.CW
        );

        canvas.clipPath(path);
        canvas.drawBitmap(source, 0, 0, paint);

        if (source != result) {
            source.recycle();
        }

        return result;
    }

    @Override
    public String key() {
        return "rounded(radius=" + radius + ", margin=" + margin + ")";
    }
}