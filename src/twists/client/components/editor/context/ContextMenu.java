package twists.client.components.editor.context;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;

import twisted.client.ComponentLog;
import twisted.client.utils.GenericCallback;
import twisty.client.utils.Stylesheet;

public class ContextMenu {
  
  /** If this menu is open or closed. */
  private boolean closed = true;
  
  /** The parent for the context menu. */
  private Element parent = null;
  
  /** The menu elements; probably not attached to the dom. */
  private ContextMenuItem menuRoot = null;
  
  /** Global context marker list. */
  private static ArrayList<ContextMenu> openContexts = new ArrayList<ContextMenu>();
  
  /**
   * Content for this menu.
   * <p>
   * Only one menu for any particular context can be open at a time,
   * so if another menu is open when @see #open() is called, it is
   * closed first. 
   */
  private String context = null;
  
  public ContextMenu(String context) {
    this.context = context;
    menuRoot = new ContextMenuItem(context, null, null, null, null, null); 
  }
  
  /** Closes any open items with the same context. */
  private void closeSiblings() {
    ArrayList<ContextMenu> pruned = new ArrayList<ContextMenu>();
    for (ContextMenu c : openContexts) {
      if (c.getContext().equals(context)) {
        c.close();
        pruned.add(c);
      }
    }
    openContexts.removeAll(pruned);
  }
  
  /** Sets the element this menu is attached to. */
  public void setElement(Element e) {
    parent = e;
  }
  
  /** Returns the current context. */
  public String getContext() {
    return(context);
  }
  
  /** Closes the current object. */
  public void close() {
    // TODO
    if (!closed) {
      Element menu = menuRoot.getSubElement();
      parent.removeChild(menu);
      closed = true;
    }
  }
  
  /** Opens the current object. */
  public void open() {
    closeSiblings();
    menuRoot.update();
    ComponentLog.trace("Update completed.");
    Element menu = menuRoot.getSubElement();
    parent.appendChild(menu);
    closed = false;
    ComponentLog.trace("Added child");
    // TODO
  }
  
  /** Adds a sub-menu with a name. */
  public int addMenu(String name) {
    // TODO
    return(0);
  }
  
  /** 
   * Adds an item to the menu. 
   * <p>
   * blah blah
   * <p>
   * The callback object is invoked when a menu item is selected;
   * if the item is a checkbox, the returned object can be used to
   * check the state.
   * <p>
   * When a menu is displayed, the update objects are all invoked
   * first; this allows objects to populate checked states, and 
   * other information.
   * <p>
   * Use addMenuShortcut() to actually bind a keyboard shortcut.
   * */
  public int addMenuItem(String id, String name, String iconUrl, String shortcut, 
    GenericCallback<ContextMenuItem> callback, 
    GenericCallback<ContextMenuItem> update) {
    ContextMenuItem item = new ContextMenuItem(id, name, iconUrl, shortcut, callback, update);
    menuRoot.attachMenuItem(item);
    // TODO
    return(0);
  }
  
  /** Adds a separator on the menu. */
  public void addMenuSpacer() {
    // TODO
  }
  
  /** Adds a keyboard shortcut. */
  public void addMenuShortcut(int id, String shortcut, int keycode, int modifiers) {
    // TODO
  }
  
  /** Creates a menu from the open menu items. */
  private Element createMenuInstance() {
    // TODO
    return(null);
  }
  
  /** Returns true if the menu is currently open. */
  public boolean isOpen() {
    return(!closed);
  }
  
  /** Injects the standard css files for the context menu. */
  public static void injectCss() throws Exception {
    String css = ContextMenuBundle.instance.contextMenuCss().getText();
    Stylesheet.createStylesheet(css);
  }
}
