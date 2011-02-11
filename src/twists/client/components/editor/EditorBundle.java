package twists.client.components.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/** Icons and assets for the editor. */
public interface EditorBundle extends ClientBundle {
  
  /** Public instance. */
  public static final EditorBundle instance = GWT.create(EditorBundle.class);

  @Source("inc/iconBold.png")
  public ImageResource iconBold();
  
  /** All common editor styles. */
  @Source("inc/Editor.css")
  public TextResource styles();
}
