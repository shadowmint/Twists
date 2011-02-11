package twists.client.components.editor;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.events.ComponentClickListener;
import twists.client.components.editor.context.ContextMenu;

/** Api for interacting with the editor. */
public class EditorApi extends ComponentApi {

  /** The context menu for this item. */
  private ContextMenu menu = null;

  /** Any attached events. */
  private HashMap<Element,ArrayList<HandlerRegistration>> registers = new HashMap<Element,ArrayList<HandlerRegistration>>();

  public EditorApi(Component parent) {
    super(parent);
  }

  /** Returns the registers array for an element. */
  private ArrayList<HandlerRegistration> getRegister(Element target) {
    ArrayList<HandlerRegistration> rtn = registers.get(target);
    if (rtn == null) {
      rtn = new ArrayList<HandlerRegistration>();
      registers.put(target, rtn);
    }
    return(rtn);
  }

  /** Returns true if an element exists in the registers set. */
  private boolean hasRegister(Element target) {
    if (registers.keySet().contains(target))
      return(true);
    else
      return(false);
  }

  /** Removes an element from the registers set. */
  private void removeRegister(Element target) {
    ArrayList<HandlerRegistration> set = registers.get(target);
    if (set != null) {
      registers.remove(target);
      for (HandlerRegistration r : set) {
        r.removeHandler();
      }
    }
  }

  /** Starts editing a target element. */
  public void edit(Element target) {
    // TODO
    if (!hasRegister(target)) {
      if (menu == null)
        menu = createDefaultMenu();
      menu.setElement(target);
      attachMenuHandler(target);
      target.addClassName("ComponentEditor-Open");
    }
  }

  /** Stops an editing activity. */
  public void done(Element target) {
    // TODO
    if (menu != null)
      menu.close();
    removeRegister(target);
    target.removeClassName("ComponentEditor-Open");
  }

  /**
   * Opens the editor formatting menu.
   * <p>
   * The context menu which is opened is opened at the potion of the target
   * element passed in.
   * <p>
   * Use this function to bind the format menu to a useful event, like a right
   * click or a format menu button.
   */
  public void format(Element target) {
    if (menu != null) {
      if (!menu.isOpen())
        menu.open();
    }
  }

  /** Returns the content of the current target. */
  public String getInnerHTML() {
    // TODO
    return("");
  }

  /**
   * Sets the context menu for the editor
   * <p>
   * Otherwise a default rich-edit menu is used.
   */
  public void setContextMenu(ContextMenu menu) {
  }

  /** Returns the style object, for things like setting, bold, etc. */
  public EditorStyle getStyle() {
    return(EditorStyle.get());
  }

  /** Creates the default context menu. */
  private ContextMenu createDefaultMenu() {

    ContextMenu rtn = new ContextMenu("Editor");
    EditorStyle style = getStyle();

    // Bold
    String iconUrl = EditorBundle.instance.iconBold().getURL();
    rtn.addMenuItem("Bold", "Bold", iconUrl, "Control-B", style.boldCallback, style.boldUpdate);
    rtn.addMenuItem("Bold", "Bold", iconUrl, "Control-B", style.boldCallback, style.boldUpdate);
    rtn.addMenuItem("Bold", "Bold", iconUrl, "Control-B", style.boldCallback, style.boldUpdate);
    rtn.addMenuItem("Bold", "Bold", iconUrl, "Control-B", style.boldCallback, style.boldUpdate);
    rtn.addMenuItem("Bold", "Bold", iconUrl, "Control-B", style.boldCallback, style.boldUpdate);
    //rtn.addMenuShortcut();

    return(rtn);
  }

  /** Attaches event listeners when an edit target is selected. */
  private void attachMenuHandler(Element e) {
    if (!hasRegister(e)) {
      ArrayList<HandlerRegistration> registers = getRegister(e);
      ComponentClickListener cl = ComponentClickListener.get(e);
      registers.add(cl.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          menu.close();
        }
      }));
    }
  }

  /**

Notes:
If you use a custom context menu, you may still want to use style
controls. You can use the ContextStyle object for this.

eg.
ContextStyle style = api.getStyle();
ContextMenu c = new contextMenu();
int id = c.addMenuItem("Bold", "Bold", null, style.boldDefaultCallback(), style.boldDefaultUpdate());
c.addMenuShortcut(id, ...);

Style object to have a bunch of named methods like that:
eg.

boldGet
boldSet
boldCallback
boldUpdate

indentStart
indentEnd
indentCallback

listStart
listEnd
listCallback

imageInsert
imageCallback

linkInsert
linkCallback

   */

}
