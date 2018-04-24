package com.yiche.autoeasy.html2local.popup;

import android.text.Layout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by hanbo1 on 2016/6/16.
 */
public class TextLocationHelper {

    /** 传进来的是屏幕坐标
     * @param x 相对于屏幕的坐标
     * @param y 相对于屏幕的坐标
     * @param lc
     */
    public static void showSelectionArea(TextView tv, float x, float y, int listIndex, FrameSelector lc) {
        if(tv.length()<=1||tv.getLayout()==null){
            return;
        }
        int[] tvCord = new int[2];
        tv.getLocationInWindow(tvCord);
        int lineHeight = tv.getLineHeight();

        //文本长度较短则直接全选
        if(tv.length()<=3){
            int[] start = getFixXYByTextviewOffset(tv,0);
            int[] end = getFixXYByTextviewOffset(tv,tv.length());
            CursorPoint sp = new CursorPoint(tvCord,start[0],start[1],listIndex,lineHeight,0);
            CursorPoint ep = new CursorPoint(tvCord,end[0],end[1],listIndex,lineHeight,tv.length());
            lc.showOnScreen(sp, ep);
            return;
        }

        //获取指定坐标对应的文字index
        int startOffset = tv.getOffsetForPosition(x-tvCord[0], y-tvCord[1]);
        if(startOffset<=0){
            startOffset= 0;
        }
        Layout layout = tv.getLayout();
        //假如选中行末尾的话start要往前推推
        int line = layout.getLineForOffset(startOffset);
        int lineLastOffest = layout.getLineEnd(line);
        int dis;
        if((dis = lineLastOffest-startOffset)<3){
            startOffset = startOffset+dis-3;
        }

        int endOffset = startOffset + 3;

        int[] start = getFixXYByTextviewOffset(tv,startOffset);
        int[] end = getFixXYByTextviewOffset(tv,endOffset);
        CursorPoint sp = new CursorPoint(tvCord,start[0],start[1],listIndex,lineHeight,startOffset);
        CursorPoint ep = new CursorPoint(tvCord,end[0],end[1],listIndex,lineHeight,endOffset);
        sp.mContent = ep.mContent = tv.getText().toString();
        lc.showOnScreen(sp, ep);

        /*int[] startFixXY = new int[2];
        int[] endFixXY = new int[2];

        getAdjusteStartXY(start,tv,startFixXY);
        getAdjustedEndXY(end,tv,endFixXY);

        CursorPoint sp = new CursorPoint(startFixXY[0]+tvCord[0],startFixXY[1]+tvCord[1]
                ,startFixXY[0],startFixXY[1],listIndex,lineHeight,start);

        CursorPoint ep = new CursorPoint(endFixXY[0]+tvCord[0],endFixXY[1]+tvCord[1]
                ,endFixXY[0],endFixXY[1],listIndex,lineHeight,end);
        sp.content = ep.content = tv.getText().toString();

        lc.showSelectionControls(sp, ep);*/
    }


    /**
     * 传进来及返回去的全是相对于屏幕的坐标
     */
    public static CursorPoint getFixedXY(CursorPoint old,int x, int y, TextView tv){
        int[] tvXY = new int[2];
        tv.getLocationInWindow(tvXY);

        if(old==null){
            old = new CursorPoint();
        }

        int offset = tv.getOffsetForPosition(x-tvXY[0], y-tvXY[1]);
        if(offset<0){
            offset = 0;
        }
        int[] fixedRelativeXX = getFixXYByTextviewOffset(tv,offset);
        if(fixedRelativeXX==null){
            return old;
        }
        old.mTextOffset = offset;
        old.mLineHeight = tv.getLineHeight();
        old.mRelativeX = fixedRelativeXX[0];
        old.mRelativeY = fixedRelativeXX[1];
        old.mWindowX =  old.mRelativeX +tvXY[0];
        old.mWindowY = old.mRelativeY +tvXY[1];
        old.mContent = tv.getText().toString();
        return old;
    }

    /**
     * 根据字符所在位置的偏移量计算该字符左下角的坐标，坐标是相对于该TextView的
     * @param tv
     * @param offset
     * @return
     */
    private static int[] getFixXYByTextviewOffset(TextView tv, int offset) {
        Layout layout = tv.getLayout();
        if (layout != null) {
            int line = layout.getLineForOffset(offset);
            int base = layout.getLineBottom(line);
            //修正纵坐标用，暂未使用
            int lineYxiuZheng = layout.getLineDescent(line);

            int relativeX = (int) layout.getPrimaryHorizontal(offset) - tv.getScrollX(); // x
            int relativeY  = base - tv.getScrollY(); // y
            return new int[]{relativeX,relativeY};
        }
        return null;
    }


    /** 根据位置返回listview对应的子view */
    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if(pos<firstListItemPosition||pos>lastListItemPosition){
            //无意义，返回null
            return null;
        }
        int childIndex = pos - firstListItemPosition;
        return listView.getChildAt(childIndex);

    }

    /**返回该offset是否在行的结尾*/
    private static boolean isEndOfLineOffset(TextView tv,int offset) {
        if (offset > 0) {
            return tv.getLayout().getLineForOffset(offset) ==
                    tv.getLayout().getLineForOffset(offset - 1) + 1;
        }
        return false;
    }






    /* ***************************以下是参考代码********************** */

    /**
     * Get the (x,y) screen coordinate from the specified offset. If the specified offset is beyond the
     * end of the line, move the offset to the beginning of the next line.
     *
     * @param offset   the offset
     * @param coords   the returned x, y coordinate array, muust have a length of 2
     */
    private static void getAdjusteStartXY(int offset, TextView tv, int[] coords) {
        if (offset < tv.getText().length()) {
            final Layout layout = tv.getLayout();
            if (layout != null) {
                if (isEndOfLineOffset(tv,offset + 1)) {
                    float a = layout.getPrimaryHorizontal(offset);
                    float b = layout.getLineRight(layout.getLineForOffset(offset));
                    if (a == b) {
                        // this means the we encounter a new line character, i think.
                        offset += 1;
                    }
                }
            }
        }
        getXY(tv,offset,coords);
    }
    /**
     * Get the (x,y) screen coordinate from the specified offset. If the offset is the at the end of a
     * wrapped line, return the (x,y) at the end of that line instead of the (x, y) at the beginning of
     * the next line (which is the default behaviour for Android)
     *
     * @param offset   the offset
     * @param coords   the returned x, y coordinate array, must have a length of 2
     */
    private static void getAdjustedEndXY(int offset,TextView tv, int[] coords) {
        if (offset > 0) {
            final Layout layout = tv.getLayout();
            if (layout != null) {
                //if (this_line > prev_line) {
                if (isEndOfLineOffset(tv,offset)) {
                    // if we are at the end of a line, calculate the X using getLineRight instead of
                    // getPrimaryHorizontal.
                    // (Because getPrimaryHorizontal returns 0 for offset sitting at the end of a line.
                    // getPrimaryHorizontal returns the next insertion point, which will be the next line)
                    int prev_line = layout.getLineForOffset(offset - 1);
                    float right = layout.getLineRight(prev_line);
                    int y = layout.getLineBottom(prev_line);
                    coords[0] = (int) right - tv.getScrollX();
                    coords[1] = y - tv.getScrollY();
                    return;
                }
            }
        }
        getXY(tv,offset, coords);
    }
    /**
     * get the (x,y) screen coordinates from the specified offset.
     * @param offset   the offset
     * @param coords   the returned x, y coordinate array, must have a length of 2
     */
    private static void getXY(TextView tv,int offset, int[] coords) {
        assert (coords.length >= 2);

        //scroll_x the horizontal scroll distance to take away
        //the horizontal scroll distance to take away
        int scroll_x = tv.getScrollX();
        int scroll_y = tv.getScrollY();
        coords[0] = coords[1] = -1;
        Layout layout = tv.getLayout();

        if (layout != null) {
            int line = layout.getLineForOffset(offset);
            int base = layout.getLineBottom(line);

            coords[0] = (int) layout.getPrimaryHorizontal(offset) - scroll_x; // x
            coords[1] = base - scroll_y; // y
        }
    }


}
