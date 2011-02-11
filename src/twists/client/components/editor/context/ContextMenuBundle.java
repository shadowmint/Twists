package twists.client.components.editor.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/** Icons and assets for the editor. */
public interface ContextMenuBundle extends ClientBundle {

  /** Public instance. */
  public static final ContextMenuBundle instance = GWT.create(ContextMenuBundle.class);

  @Source("inc/ContextMenu.css")
  public TextResource contextMenuCss();

  @Source("inc/ContextMenuItem.html")
  public TextResource contextMenuItemHtml();

  @Source("inc/ContextMenuItemContainer.html")
  public TextResource contextMenuItemContainerHtml();
}
