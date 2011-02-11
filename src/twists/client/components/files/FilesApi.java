package twists.client.components.files;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.http.client.URL;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.utils.GenericCallback;

/** Api for interacting with local filesystem. */
public class FilesApi extends ComponentApi {

  public FilesApi(Component parent) {
    super(parent);
  }

  /**
   * Lets the user select a file to load.
   * @param preload If the content of the file should be loaded as a string automatically.
   */
  public InputElement createFileSelector(GenericCallback<File[]> complete, boolean preload) {
    InputElement ep = Document.get().createFileInputElement();
    ep.setPropertyBoolean("multiple", true);
    FileList handle = new FileList(complete, preload);
    selectFiles(this, handle, ep);
    return(ep);
  }

  private void selectFilesCallback(Object blob, FileList handle, int remaining) {
    if (blob != null) {
      File n = new File(blob);
      handle.files.add(n);
    }
    if (remaining == 0) {

      // Return array!
      final File[] rtn = new File[handle.files.size()];
      handle.files.toArray(rtn);

      if (!handle.preload) {
        handle.callback.onSuccess(rtn);
        resetContext(handle);
      }
      else {
        final FileList context = handle;
        for (File f : rtn) {
          f.readAsText(new GenericCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
              if (!context.failed) {
                context.failed = true;
                context.callback.onFailure(caught);
                resetContext(context);
              }
            }
            @Override
            public void onSuccess(String result) {
              ++context.loaded;
              if (context.loaded == rtn.length) {
                context.callback.onSuccess(rtn);
                resetContext(context);
              }
            }
          });
        }
      }
    }
  }
  
  /** Prepares the file list for the next invokation. */
  private void resetContext(FileList context) {
    context.failed = false;
    context.loaded = 0;
    context.files.clear();
  }

  private native void selectFiles(FilesApi self, FileList handle, InputElement e) /*-{
    function handleFileSelect(evt) {
      var files = evt.target.files; // native javascript FileList object
      var count = files.length - 1; // For the callback
      for (var i = 0; i < files.length; i++) {
        self.@twists.client.components.files.FilesApi::selectFilesCallback(Ljava/lang/Object;Ltwists/client/components/files/FilesApi$FileList;I)(files[i], handle, count - i);
      }
    }
    e.addEventListener('change', handleFileSelect, false);
  }-*/;

  /**
   * Binds an element as a drop zone for dragging files into.
   * @param preload If the content of the file should be loaded as a string automatically.
   */
  public void dropZone(Element e, GenericCallback<File[]> complete, boolean preload) {
    FileList handle = new FileList(complete, preload);
    createDropZone(this, handle, e);
  }
  
  private native void createDropZone(FilesApi self, FileList handle, Element e) /*-{
    
    function handleFileSelect(evt) {
      evt.stopPropagation();
      evt.preventDefault();
      var files = evt.dataTransfer.files; // native javascript FileList object
      var count = files.length - 1; // For the callback
      for (var i = 0; i < files.length; i++) {
        self.@twists.client.components.files.FilesApi::selectFilesCallback(Ljava/lang/Object;Ltwists/client/components/files/FilesApi$FileList;I)(files[i], handle, count - i);
      }
    }
    
    function handleDragOver(evt) {
      evt.stopPropagation();
      evt.preventDefault();
    }

    e.addEventListener('dragover', handleDragOver, false);
    e.addEventListener('drop', handleFileSelect, false);
  }-*/;

  /**
   * Returns a data uri for the content given.
   * <p>
   * It's not possible (cross platform) to trigger the save-as dialog on
   * browsers. The best we can do is set the href of an anchor to the data
   * uri for the content and make the user right-click and save-as.
   * */
  public String dataUrl(String content, String mimeType) {
    String encoded = URL.encode(content);
    encoded = "data:" + mimeType + "," + encoded;
    return(encoded);
  }

  /** Temporary class for storage values between native calls. */
  private class FileList {

    /** If any load failures had happened. */
    public boolean failed = false;

    /** Total count of items loaded so far. */
    public int loaded = 0;

    /** If the content for files should be loaded. */
    public boolean preload;

    /** Callback to invoke when we're done. */
    public GenericCallback<File[]> callback;

    /** Set of file objects so far. */
    public ArrayList<File> files = new ArrayList<File>();

    public FileList(GenericCallback<File[]> complete, boolean preload) {
      this.callback = complete;
      this.preload = preload;
    }
  }
}
