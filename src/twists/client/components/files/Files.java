package twists.client.components.files;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twisted.client.ComponentLog;

/** Provides a filesystem access api. */
public class Files extends Component {

  FilesApi api;

  public Files(ComponentContainer root) {
    super(root);
  }

  @Override
  public void init() {
  }

  @Override
  public void run() {
    if (isSupported()) {
      api = new FilesApi(this);
      complete();
    }
    else {
      ComponentLog.trace("The browser in used does not support the HTML5 files api.");
      failed();
    }
  }

  /** Check if we support the files api. */
  private native boolean isSupported() /*-{
    var rtn = false;
    if (window.File && window.FileReader && window.FileList)
      rtn = true;
    return(rtn);
  }-*/;

  @Override
  public ComponentApi api() {
    return(api);
  }
}
