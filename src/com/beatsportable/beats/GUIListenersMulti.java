package com.beatsportable.beats;

import java.util.HashMap;
import java.util.Map;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GUIListenersMulti extends GUIListeners {

	public GUIListenersMulti(GUIHandler handler) {
		super(handler);
	}
	
	public OnTouchListener getOnTouchListener() {
		return new OnTouchListener() {
			private Map<Integer, Integer> finger2pitch = new HashMap<Integer, Integer>();
			public boolean onTouch(View v, MotionEvent e) {
				if (!v.hasFocus()) v.requestFocus();
				if (autoPlay || h.done || h.score.gameOver) return false;
				int pitch;
				
			// Use pointer IDs (stable across the gesture), not pointer indexes.
			// The old code stored ACTION_POINTER_ID_SHIFT as if it were an id, then
			// never handled ACTION_MOVE, so a dragged finger kept the original column.
			int actionmask = e.getAction() & MotionEvent.ACTION_MASK;
			int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			switch (actionmask) {
			case MotionEvent.ACTION_DOWN:
				pointerIndex = 0;
				//fallthru
			case MotionEvent.ACTION_POINTER_DOWN: {
				int pointerId = e.getPointerId(pointerIndex);
				pitch = h.onTouch_Down(e.getX(pointerIndex), e.getY(pointerIndex));
				if (pitch > 0) finger2pitch.put(pointerId, pitch);
				return pitch > 0;
			}
			case MotionEvent.ACTION_MOVE: {
				boolean handled = false;
				for (int i = 0; i < e.getPointerCount(); i++) {
					int pointerId = e.getPointerId(i);
					int previous = finger2pitch.containsKey(pointerId) ? finger2pitch.get(pointerId) : 0;
					int next = h.onTouch_Move(e.getX(i), e.getY(i), previous);
					if (next > 0) {
						finger2pitch.put(pointerId, next);
					} else {
						finger2pitch.remove(pointerId);
					}
					handled |= (next != previous);
				}
				return handled;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				int pointerId = e.getPointerId(pointerIndex);
				h.onTouch_Up(e.getX(pointerIndex), e.getY(pointerIndex));
				if (finger2pitch.containsKey(pointerId)) {
					int released = finger2pitch.remove(pointerId);
					return h.onTouch_Up(released);
				} else {
					return h.onTouch_Up(0xF);
				}
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				h.onTouch_Up(e.getX(pointerIndex), e.getY(pointerIndex));
				finger2pitch.clear();
				return h.onTouch_Up(0xF);
			default:
				return false;
			}
			}
		};
	}	
}
