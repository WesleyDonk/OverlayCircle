package com.transparentcircle.app;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wesleyd on 14/04/14.
 */
public class TransparentCircle extends View {

    private float mPositionX;
    private float mPositionY;

    private float mShakeX;

    private int mRadius;
    private int mMaxRadius;
    private int mFullscreenRadius;

    private int mBackgroundResourceId;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mEraser;

    public TransparentCircle(Context context) {
        super(context);
        init(null);
    }

    public TransparentCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TransparentCircle(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        mEraser = new Paint();
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setAntiAlias(true);

        if (attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.TransparentCircle);

            mBackgroundResourceId = typedArray.getColor(R.styleable.TransparentCircle_overlay,
                    getResources().getColor(R.color.semi_transparent));

            mMaxRadius = typedArray.getResourceId(R.styleable.TransparentCircle_maxRadius,
                    getResources().getDimensionPixelSize(R.dimen.circle_radius));

            mShakeX = typedArray.getResourceId(R.styleable.TransparentCircle_shakeX,
                    getResources().getDimensionPixelSize(R.dimen.circle_shake_x));

            mPositionX = typedArray.getResourceId(R.styleable.TransparentCircle_positionX,
                    getResources().getDimensionPixelSize(R.dimen.circle_position_x));

            mPositionY = typedArray.getResourceId(R.styleable.TransparentCircle_positionY,
                    getResources().getDimensionPixelSize(R.dimen.circle_position_y));

            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (w != oldw || h != oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            mPositionX = w / 2;
            mPositionY = h / 2;

            mFullscreenRadius = h;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mBitmap.eraseColor(Color.TRANSPARENT);

        mCanvas.drawColor(mBackgroundResourceId);
        mCanvas.drawCircle(mPositionX, mPositionY, mRadius, mEraser);

        canvas.drawBitmap(mBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    ////////////////////////////////
    /// SETTERS
    ////////////////////////////////

    public void setMRadius(int radius) {

        this.mRadius = radius;
        invalidate();
    }

    public void setMPositionX(float positionX) {

        this.mPositionX = positionX;
        invalidate();
    }

    public void setMPositionY(float positionY) {

        this.mPositionY = positionY;
        invalidate();
    }

    ////////////////////////////////
    /// ANIMATIONS
    ////////////////////////////////

    private void animateToPosition(TimeInterpolator interpolator, float positionX, float positionY, Animator.AnimatorListener listener) {

        ObjectAnimator animationX = ObjectAnimator.ofFloat(this, "mPositionX", mPositionX, positionX);
        animationX.setDuration(1000);
        animationX.setInterpolator(interpolator);

        ObjectAnimator animationY = ObjectAnimator.ofFloat(this, "mPositionY", mPositionY, positionY);
        animationY.setDuration(1000);
        animationY.setInterpolator(interpolator);

        AnimatorSet animatorSet = new AnimatorSet();

        if (listener != null) animatorSet.addListener(listener);

        animatorSet.playTogether(animationX, animationY);
        animatorSet.start();
    }

    private void animateToNavigationDrawer(TimeInterpolator interpolator, Animator.AnimatorListener listener) {

        animateToPosition(interpolator, 10, 10, listener);
    }

    private void wiggle(TimeInterpolator interpolator, Animator.AnimatorListener listener) {

        ObjectAnimator beginAnimation = ObjectAnimator.ofFloat(this, "mPositionX", mPositionX, mShakeX);
        beginAnimation.setDuration(1000);

        ObjectAnimator animateToLeft = ObjectAnimator.ofFloat(this, "mPositionX", mPositionX, -(mShakeX * 2));
        animateToLeft.setDuration(1000);

        ObjectAnimator animateToRight = ObjectAnimator.ofFloat(this, "mPositionX", mPositionX, (mShakeX * 2));
        animateToRight.setDuration(1000);

        ObjectAnimator endAnimation = ObjectAnimator.ofFloat(this, "mPositionX", mPositionX, -mShakeX);
        beginAnimation.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(interpolator);

        if(listener != null) animatorSet.addListener(listener);

        animatorSet.playSequentially(beginAnimation, animateToLeft, animateToRight, animateToLeft,
                animateToRight, animateToLeft, animateToRight, endAnimation);
    }

    public void show(TimeInterpolator interpolator, Animator.AnimatorListener listener) {

        ObjectAnimator radiusAnimation = ObjectAnimator.ofInt(this, "mRadius", mRadius, mMaxRadius);
        radiusAnimation.setDuration(1000);
        radiusAnimation.setInterpolator(interpolator);

        if (listener != null) radiusAnimation.addListener(listener);

        radiusAnimation.start();
    }

    public void fillScreen(TimeInterpolator interpolator, boolean removeAfter, Animator.AnimatorListener listener) {

        ObjectAnimator radiusAnimation = ObjectAnimator.ofInt(this, "mRadius", mRadius, mFullscreenRadius);
        radiusAnimation.setDuration(1000);
        radiusAnimation.setInterpolator(interpolator);

        if (listener != null) radiusAnimation.addListener(listener);

        radiusAnimation.start();
    }
}
