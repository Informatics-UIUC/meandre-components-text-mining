/*
Script.aculo.us Effects integration component for GWT
Copyright (C) 2006 Alexei Sokolov http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/

/*
Adapted for use in the Meandre Workbench.
D. Searsmith
*/


package com.gwt.components.client;

//==============
// Java Imports
//==============

import java.util.Iterator;
import java.util.Vector;

//===============
// Other Imports
//===============

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

public class Effects {

  public interface EffectListener {
    void onBeforeUpdate(Effect sender);
    void onAfterUpdate(Effect sender);
    void onAfterFinish(Effect sender);
  }

  public static class EffectListenerAdapter implements EffectListener {

    public void onBeforeUpdate(Effect sender) {
    }

    public void onAfterUpdate(Effect sender) {
    }

    public void onAfterFinish(Effect sender) {
    }
  }

  public static class EffectListenerCollection extends Vector {
    public void fireBeforeUpdate(Effect sender) {
      for (Iterator it = iterator(); it.hasNext();) {
        EffectListener listener = (EffectListener) it.next();
        listener.onBeforeUpdate(sender);
      }
    }
    public void fireAfterUpdate(Effect sender) {
      for (Iterator it = iterator(); it.hasNext();) {
        EffectListener listener = (EffectListener) it.next();
        listener.onAfterUpdate(sender);
      }
    }
    public void fireAfterFinish(Effect sender) {
      for (Iterator it = iterator(); it.hasNext();) {
        EffectListener listener = (EffectListener) it.next();
        listener.onAfterFinish(sender);
      }
    }
  }

  public static class Effect {

    private JavaScriptObject delegate;
    private EffectListenerCollection effectListeners;

    private Effect(JavaScriptObject delegate) {
      this.delegate = delegate;
      setupCallbacks(this);
    }

    public Effect addEffectListener(EffectListener listener) {
      if (effectListeners == null)
        effectListeners = new EffectListenerCollection();
      effectListeners.add(listener);
      return this;
    }

    public Effect removeEffectListener(EffectListener listener) {
      if (effectListeners != null)
        effectListeners.remove(listener);
      return this;
    }

    protected void beforeUpdate() {
      if (effectListeners != null) {
        effectListeners.fireBeforeUpdate(this);
      }
    }

    protected void afterUpdate() {
      if (effectListeners != null) {
        effectListeners.fireAfterUpdate(this);
      }
    }

    protected void afterFinish() {
      if (effectListeners != null) {
        effectListeners.fireAfterFinish(this);
      }
    }

    private native void setupCallbacks(Effect obj) /*-{
      var val = this.@com.gwt.components.client.Effects$Effect::delegate;

      val.options = $wnd.Object.extend({
        beforeUpdate: function(effect) {
          obj.@com.gwt.components.client.Effects$Effect::beforeUpdate()();
        },
        afterUpdate: function(effect) {
          obj.@com.gwt.components.client.Effects$Effect::afterUpdate()();
        },
        afterFinish: function(effect) {
          obj.@com.gwt.components.client.Effects$Effect::afterFinish()();
        }
      }, val.options);

    }-*/;

    public native void go() /*-{
      var val = this.@com.gwt.components.client.Effects$Effect::delegate;

      if (!val.options || !val.options.sync) {
        return;
      }

      $wnd.Effect.Queues.get(typeof val.options.queue == 'string' ?
        'global' : val.options.queue.scope).add(val);
    }-*/;
  }

  private static class JSArray {

    public JavaScriptObject[] objs;

    public JSArray(int size) {
      objs = new JavaScriptObject[size];
    }

    public JavaScriptObject get(int index) {
      return objs[index];
    }

    public void set(int index, JavaScriptObject obj) {
      objs[index] = obj;
    }

    public int size() {
      return objs.length;
    }

    public native JavaScriptObject toObj() /*-{
      var size = this.@com.gwt.components.client.Effects$JSArray::size()();
      var arr=new $wnd.Array(size);
      var i=0;
      for (; i < size; i++) {
        arr[i] = this.@com.gwt.components.client.Effects$JSArray::get(I)(i);
      }
      return $wnd.Array.from(arr);
    }-*/;
  }

  private static native JavaScriptObject evaluate(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;

  private static native JavaScriptObject effect(String name, Element elem,
      JavaScriptObject options) /*-{

    return new $wnd.Effect[name](elem, options);
  }-*/;

  private static native JavaScriptObject parallelEffect(JavaScriptObject effects,
                        JavaScriptObject options) /*-{

    return new $wnd.Effect.Parallel(effects, options);

  }-*/;

  public static Effect Parallel(Effect[] effects, String options) {
    if (effects == null) {
      return null;
    }
    JSArray objs = new JSArray(effects.length);
    for (int i = 0; i < effects.length; i++) {
      objs.set(i, effects[i].delegate);
    }
    return new Effect(parallelEffect(objs.toObj(), evaluate(options)));
  }

  public static Effect Effect(String name, UIObject uiObj) {
    return Effect(name, uiObj, "{}");
  }

  public static Effect Effect(String name, UIObject uiObj, String options) {
    return new Effect(effect(name, uiObj.getElement(), evaluate(options)));
  }

}
