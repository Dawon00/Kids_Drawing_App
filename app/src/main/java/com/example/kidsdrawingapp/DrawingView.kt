package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// 클래스로 DrawingView 생성
// 무언가를 그리고 싶다면 활용도가 높은 뷰 타입을 사용해야하기 때문
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs){

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    // Paint 클래스는 geometry, text, bitmap 등을 그릴 때 사용하는 스타일이나 색상의 정보값을 포함한 클래스입니다.
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    // 변수들을 설정
    init{
        setUpDrawing()
    }

    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND //브러쉬의 끝부분 모양
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND // Cap 은 선 끝의 위치
        mCanvasPaint = Paint(Paint.DITHER_FLAG) // DITHER_FLAG 는 사용불가 색상을 실험할 때 렌더린을 제어한다.
        mBrushSize = 20.toFloat()
    }

    // DrawingView 가 View 를 상속받기 때문에 하위 요소를 사용하거나 재정의 할 수 있다.
    // 화면의 크기가 바뀔 때마다 불러오게 만든다
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // ARGB_8888 사용할 수 있는 색상의 분량
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // 비트맵을 캔버스 비트맵으로 사용
        canvas = Canvas(mCanvasBitmap!!)
    }

    // 그림을 그리고 싶을 때 실행될 코드
    //onDraw 메서드는 뭔가를 적거나 그릴 캔버스를 포함
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //시작 위치를 0f 0f 로 지정(왼쪽 위)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        //path를 그리기
        //mDrawPath가 비어있는지 확인
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)

        }
    }

    //화면을 터치할 때 그려지므로
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            //action done 손가락을 화면에 댔을 때
            //action move 화면을 손가락으로 드래그 했을 때
            //action up 손가락을 들었을 때
            MotionEvent.ACTION_DOWN -> {
                //화면을 눌렀을 때 실행할 코드
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)

            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mDrawPath = CustomPath(color, mBrushSize)

            }
            else -> return false

        }
        //뷰가 화면에 보일 때 전체 뷰를 무효화 한다. 그리고 onDraw 가 그 후에 실행됨
        invalidate()
        return true
    }

    // Drawing View 내부에서만 사용
    // Path 타입. Path 클래스는 직선, 2차 곡선 및 3차 곡선으로 구성된 복합 기하학적 path를 압축함.
    internal inner class CustomPath(var color : Int, var brushThickness : Float) : Path(){ //Drawing View 내부에서만 사용

    }
}