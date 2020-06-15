/*
 * Copyright (C) 2018-2020 Raf
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.teithe.it.it_app.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import gr.teithe.it.it_app.R;

public class BadgeDrawable extends Drawable
{
    private Paint mBadgePaint;
    private Paint mBadgePaint1;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();

    private String mCount = "";
    private boolean mWillDraw;

    public BadgeDrawable(Context context)
    {
        float mTextSize = context.getResources().getDimension(R.dimen.badge_text_size);

        mBadgePaint = new Paint();
        mBadgePaint.setColor(Color.RED);
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mBadgePaint1 = new Paint();
        mBadgePaint1.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.design_default_color_error));
        mBadgePaint1.setAntiAlias(true);
        mBadgePaint1.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        if(!mWillDraw)
        {
            return;
        }

        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) + 5;
        float centerY = radius - 5;

        if(mCount.length() <= 2)
        {
            canvas.drawCircle(centerX, centerY, (int) (radius + 7.5), mBadgePaint1);
            canvas.drawCircle(centerX, centerY, (int) (radius + 5.5), mBadgePaint);
        }
        else
        {
            canvas.drawCircle(centerX, centerY, (int) (radius + 8.5), mBadgePaint1);
            canvas.drawCircle(centerX, centerY, (int) (radius + 6.5), mBadgePaint);
        }

        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);

        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);

        if(mCount.length() > 2)
        {
            canvas.drawText("99+", centerX, textY, mTextPaint);
        }
        else
        {
            canvas.drawText(mCount, centerX, textY, mTextPaint);
        }
    }

    public void setCount(String count)
    {
        mCount = count;

        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha)
    {
        //Do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        //Do nothing
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.UNKNOWN;
    }
}
