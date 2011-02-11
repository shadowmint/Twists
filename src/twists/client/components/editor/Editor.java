package twists.client.components.editor;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twisted.client.ComponentLog;
import twists.client.components.editor.context.ContextMenu;
import twisty.client.utils.Stylesheet;

/** Basic inline rich text editor. */
public class Editor extends Component {

  private EditorApi api;

  public Editor(ComponentContainer root) {
    super(root);
  }

  @Override
  public void init() {
  }

  @Override
  public void run() {
    api = new EditorApi(this);
    
    try {
      // Editor
      String css = EditorBundle.instance.styles().getText();
      Stylesheet.createStylesheet(css);
      
      // Context menu default
      ContextMenu.injectCss();
    }
    catch(Exception error) {
      ComponentLog.trace("Unable to load editor styles: " + error);
    }
    
    complete();
  }

  @Override
  public ComponentApi api() {
    return(api);
  }
}
