package twists.client.components.editor;

import twisted.client.utils.GenericCallback;
import twists.client.components.editor.context.ContextMenuItem;

import com.google.gwt.dom.client.Element;

public class EditorStyle {

  /** Global instance. */
  private static EditorStyle instance = null;
  
  /** Returns the global instance. */
  public static EditorStyle get() {
    if (instance == null)
      instance = new EditorStyle();
    return(instance);
  }
  
  /** Sets the root element context for style events. */
  private void setContext(Element c) {
    // TODO
  }
  
  /** Gets the bold state. */
  public boolean boldGet() {
    // TODO
    return(false);
  }
  
  /** Sets the bold state. */
  public void boldSet(boolean bold) {
    // TODO
  }
  
  /** Fetches the bold state and returns it for the menu. */
  public GenericCallback<ContextMenuItem> boldUpdate = new GenericCallback<ContextMenuItem>() {
    @Override
    public void onFailure(Throwable caught) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onSuccess(ContextMenuItem result) {
      // TODO Auto-generated method stub
    }
  };
  
  /** Run when the bold state trigger or shortcut is invoked. */
  public GenericCallback<ContextMenuItem> boldCallback = new GenericCallback<ContextMenuItem>() {
    @Override
    public void onFailure(Throwable caught) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onSuccess(ContextMenuItem result) {
      // TODO Auto-generated method stub
    }
  };
}
