package com.example.custom3dviewapp.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XHD on 2020/11/24
 */
public class Custom3DView2 extends ViewGroup {
    private Camera mCamera = new Camera();//摄像机
    private Matrix mMatrix = new Matrix();//矩阵

    public Custom3DView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int childViewMaxWidth = 0, childViewMaxHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewGroupWidth = 0, viewGroupHeight = 0;
        measureChildren(widthMeasureSpec, heightMeasureSpec);//测量子view
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childViewMaxWidth = childViewMaxWidth < childView.getMeasuredWidth() ? childView.getMeasuredWidth() : childViewMaxWidth;
            childViewMaxHeight = childViewMaxHeight < childView.getMeasuredHeight() ? childView.getMeasuredHeight() : childViewMaxHeight;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY://match_parent 100dp 确切值 父决定子的确切大小，子被限定在给定的边界里，忽略本身想要的大小
                viewGroupWidth = widthSize > childViewMaxWidth ? widthSize : childViewMaxWidth;//选择较大的值
                break;
            case MeasureSpec.AT_MOST://wrap_content  子最大可以达到的指定大小
                viewGroupWidth = childViewMaxWidth;
                break;
            case MeasureSpec.UNSPECIFIED:// 父容器不对子View的大小做限制.
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY://match_parent 100dp 确切值 父决定子的确切大小，子被限定在给定的边界里，忽略本身想要的大小
                viewGroupHeight = heightSize > childViewMaxHeight ? heightSize : childViewMaxHeight;//选择较大的值
                break;
            case MeasureSpec.AT_MOST://wrap_content  子最大可以达到的指定大小
                viewGroupHeight = childViewMaxHeight;
                break;
            case MeasureSpec.UNSPECIFIED:// 父容器不对子View的大小做限制.
                break;
        }
        setMeasuredDimension(viewGroupWidth, viewGroupHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left, right, top, bottom;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            left = (getMeasuredWidth() - childView.getMeasuredWidth()) / 2;
            right = (getMeasuredWidth() - childView.getMeasuredWidth()) / 2 + childView.getMeasuredWidth();
            top = (getMeasuredHeight() - childView.getMeasuredHeight()) / 2;
            bottom = (getMeasuredHeight() - childView.getMeasuredHeight()) / 2 + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);//所有childView居中叠加显示
            childView.setVisibility(GONE);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        List<Integer> pages = populateDrawOrder();
        pages.size();
        for (int i = 0; i < 6; i++) {
            boolean flag = false;
            for (int j = 0; j < pages.size(); j++) {
                Integer page = pages.get(j);
                if (page == i) {
                    flag = true;//找到相同的页码，不做处理，最后画
                }
            }
            if (!flag) {
                drawChildView(canvas, i);
            }
        }
        for (int i = 0; i < pages.size(); i++) {
            drawChildView(canvas, pages.get(i));//0背面 1左面 2上面 3右面 4下面 5正面
        }
    }

    private float rotateX, rotateY;

    private void drawChildView(Canvas canvas, int page) {
        View childView = getChildAt(page);
        int childViewWidth = childView.getMeasuredWidth();
        int childViewHeight = childView.getMeasuredHeight();
        //坐标轴中点在左上角
        mCamera.save();//保存原先状态
        switch (page) {
            case 0://背面
                mCamera.rotateX(rotateX + 180);
                mCamera.rotateY(-rotateY);//图像围绕Y轴旋转
                break;
            case 1://左面
                mCamera.rotateX(rotateX);
                mCamera.rotateY(rotateY - 90);
                break;
            case 2://上面
                mCamera.rotateX(rotateX + 90);
                mCamera.rotateZ(rotateY);//图像围绕Z轴旋转
                break;
            case 3://右面
                mCamera.rotateX(rotateX);
                mCamera.rotateY(rotateY + 90);
                break;
            case 4://下面
                mCamera.rotateX(rotateX + 270);
                mCamera.rotateZ(-rotateY);//图像围绕Z轴旋转
                break;
            case 5://正面
                mCamera.rotateX(rotateX);
                mCamera.rotateY(rotateY);//图像围绕Y轴旋转
                break;

        }
        mMatrix.reset();
        mCamera.setLocation(0, 0, -Integer.MAX_VALUE);//设置摄像机的位置。此处参数单位不是像素，而是 inch英寸/72px
        if (spreadOutValue != -1)
            mCamera.translate(0, 0, -childViewHeight / 2 - spreadOutValue);//实现展开效果
        else
            mCamera.translate(0, 0, -childViewHeight / 2);//在所有三个轴上应用平移变换。
        mCamera.getMatrix(mMatrix);//将内部的Matrix的值复制到matrix(注意必须在restore之前)
        mCamera.restore();//恢复保存的状态（如果有）
        mMatrix.preTranslate(-getMeasuredWidth() / 2, -getMeasuredHeight() / 2);//在队列头部添加Translate
        mMatrix.postTranslate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);//在队列尾部添加Translate
        //动画执行顺序,preTranslate让childView中心移动到坐标轴中点，rotateX，rotateY绕轴旋转，postTranslate让childView回到原来位置，实现对称旋转
        canvas.save();
        canvas.concat(mMatrix);
        drawChild(canvas, childView, getDrawingTime());
        canvas.restore();
    }

    private List<Integer> pageList = new ArrayList<>();

    private List<Integer> populateDrawOrder() {
        pageList.clear();
        //优先展示的面先追加,最后进行绘制
        for (int i = 0; i < 6; i++) {
            switch (i) {//rotateX,rotateY浮动90以内画出
                case 0://背面
                    //        背面 y=180 x=0,y=0,x=180
                    if ((rotateY <= 270 && rotateY >= 90) && (rotateX <= 90 || rotateX >= 270))
                        pageList.add(0);
                    else if ((rotateY <= 90 || rotateY >= 270) && (rotateX >= 90 && rotateX <= 270))
                        pageList.add(0);

                    break;
                case 1://左面
                    //        左面 y=90 x=0,y=270,x=180
                    if ((rotateY >= 0 && rotateY <= 180) && (rotateX <= 90 || rotateX >= 270))
                        pageList.add(1);
                    else if ((rotateY >= 180 && rotateY <= 360) && (rotateX >= 90 && rotateX <= 270))
                        pageList.add(1);


                    break;
                case 2://上面
                    //        上面 x=270
                    if (rotateX >= 180 && rotateX <= 360)
                        pageList.add(2);

                    break;
                case 3://右面
                    //        右面 y=270 x=0,y=90,x=180
                    if ((rotateY >= 180 && rotateY <= 360) && (rotateX <= 90 || rotateX >= 270))
                        pageList.add(3);
                    else if ((rotateY >= 0 && rotateY <= 180) && (rotateX >= 90 && rotateX <= 270))
                        pageList.add(3);
                    break;
                case 4://下面
                    //        下面 x=90
                    if (rotateX >= 0 && rotateX <= 180)
                        pageList.add(4);

                    break;
                case 5://正面
                    //        正面 y=0 x=0,y=180 x=180
                    if ((rotateY <= 90 || rotateY >= 270) && (rotateX <= 90 || rotateX >= 270))
                        pageList.add(5);
                    else if ((rotateY >= 90 && rotateY <= 270) && (rotateX >= 90 && rotateX <= 270))
                        pageList.add(5);
                    break;
            }
        }
        return pageList;

    }

    float downX, downY, moveX, moveY;

    //实现点击子view手指不移动，子view点击事件有效，其他情况子view点击事件无效
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();//相对父容器
        float rawY = event.getRawY();//相对父容器
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = rawX;
                downY = rawY;
                intercept = false;//按下不拦截
                break;
            case MotionEvent.ACTION_MOVE:
                intercept = true;//滑动自己处理
                moveX = rawX;
                moveY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean intercept = false;//默认不拦截

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (intercept) {//拦截
            return intercept;
        }
        return super.onInterceptTouchEvent(event);
    }

    private float dy, lastDy;
    private float dx, lastDx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();//相对父容器
        float rawY = event.getRawY();//相对父容器
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = rawX;
                downY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = rawX;
                moveY = rawY;
                dx = ((moveX - downX) + lastDx) % 360;
                dy = (-(moveY - downY) + lastDy) % 360;
                rotateX = dy < 0 ? dy % 360 + 360 : dy % 360;
                rotateY = dx < 0 ? dx % 360 + 360 : dx % 360;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                lastDx = dx;
                lastDy = dy;
                break;
        }
        return true;
    }

    public void stopAnimal() {
        exitAnimal = true;
        isAnimalRunning = false;
    }

    private boolean exitAnimal = true;
    private boolean isAnimalRunning = false;

    public void startAnimal() {
        if (isAnimalRunning) {
            return;
        }
        exitAnimal = false;
        isAnimalRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exitAnimal) {
                    try {
                        Thread.sleep(10);
                        lastDy += 1;
                        lastDx += 1;
                        dx = lastDx % 360;
                        dy = lastDy % 360;
                        rotateX = dy < 0 ? dy % 360 + 360 : dy % 360;
                        rotateY = dx < 0 ? dx % 360 + 360 : dx % 360;
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int spreadOutValue = -1;

    public void spreadOut(int value) {//展开
        spreadOutValue = value;
        invalidate();
    }

    public void shrink() {//收缩
        spreadOutValue = -1;
        invalidate();
    }
}
