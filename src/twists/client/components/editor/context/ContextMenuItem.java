package twists.client.components.editor.context;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import twisted.client.utils.CommonEvents;
import twisted.client.utils.GenericCallback;

/** A single item from the context menu. */
public class ContextMenuItem {

  /** All menus have to use this, but only one at a time. */
  private static HashMap<String,Element> _template = null;

  /** Set of child objects. */
  private ArrayList<ContextMenuItem> children = new ArrayList<ContextMenuItem>();

  /** The root element. */
  private Element eSelf = null;

  /** The children element. */
  private Element eChildren = null;

  /** The parent. */
  private ContextMenuItem parent = null;

  /** Callback when clicked. */
  private GenericCallback<ContextMenuItem> callback;

  /** Update callback. */
  private GenericCallback<ContextMenuItem> update;

  /** Private id. */
  private String id;

  /** Public name. */
  private String name;

  /** Public icon. */
  private String icon;

  /** Public shortcut. */
  private String shortcut;

  /** The state, if any, for this menu. */
  private Element state = null;

  public ContextMenuItem(String id, String name, String iconUrl,
      String shortcut, GenericCallback<ContextMenuItem> callback,
      GenericCallback<ContextMenuItem> update) {
    this.id = id;
    this.name = name;
    this.icon = iconUrl;
    this.shortcut = shortcut;
    this.callback = callback;
    this.update = update;
  }

  /** Set the parent. */
  public void setParent(ContextMenuItem parent) {
    this.parent = parent;
  }

  /** Adds a child menu item. */
  public void attachMenuItem(ContextMenuItem child) {
    children.add(child);
    child.setParent(this);
    eChildren = null;
  }

  /** Gets the root element for this menu. */
  public Element getElement() {
    if (eSelf == null)
      eSelf = buildRootElement();
    return(eSelf);
  }

  /** Gets the children root element for this menu item. */
  public Element getSubElement() {
    if (eChildren == null)
      eChildren = buildChildrenElement();
    return(eChildren);
  }

  /** Builds the root element. */
  private Element buildRootElement() {

    HashMap<String,Element> template = getTemplate();

    // Get template text
    Element t = Document.get().createDivElement();
    t.setInnerHTML(ContextMenuBundle.instance.contextMenuItemHtml().getText());

    // Includes
    String t_name = name != null ? name : "";
    String t_shortcut = shortcut != null ? shortcut : "";
    String t_state = state != null ? state.getInnerHTML() : "";

    // Must remove the image if we don't have one to include.
    ImageElement img = (ImageElement) template.get("template-icon");
    if (icon != null)
      img.setSrc(icon);
    else
      template.remove("template-icon");

    // Bind includes
    template.get("template-name").setInnerHTML(t_name);
    template.get("template-shortcut").setInnerHTML(t_shortcut);
    template.get("template-state").setInnerHTML(t_state);
    CommonEvents.cssTemplate(t, template);

    // Rebind icon
    if (icon == null)
      template.put("template-icon", img);

    // Attach click handler
    final ContextMenuItem self = this;
    CommonEvents.attachClickListener(t, new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        callback.onSuccess(self);
      }
    });

    return(t);
  }

  /** Builds the children object. */
  private Element buildChildrenElement() {

    HashMap<String,Element> template = getTemplate();

    // Get template text
    Element t = Document.get().createDivElement();
    t.setInnerHTML(ContextMenuBundle.instance.contextMenuItemContainerHtml().getText());

    // Add children to include
    Element container = template.get("template-children");
    container.setInnerHTML("");
    for (ContextMenuItem i : children) {
      container.appendChild(i.getElement());
    }

    // Insert includes
    CommonEvents.cssTemplate(t, template);

    return(t);
  }

  /** Returns the template. */
  private static HashMap<String,Element> getTemplate() {
    if (_template == null) {
      _template = new HashMap<String,Element>();
      _template.put("template-name", Document.get().createSpanElement());
      _template.put("template-icon", Document.get().createImageElement());
      _template.put("template-shortcut", Document.get().createSpanElement());
      _template.put("template-state", Document.get().createSpanElement());
      _template.put("template-children", Document.get().createDivElement());
    }
    return(_template);
  }

  /** Updates the state of this, and all children. */
  public void update() {
    if (update != null)
      update.onSuccess(this);
    for (ContextMenuItem i : children) {
      i.update();
    }
  }

  /** Set the state for this menu; for the update callback. */
  public void setState(Element state) {
  }
}
