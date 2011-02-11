package twists.client.components.files;

import twisted.client.utils.GenericCallback;

/** Abstract file representation */
public class File {

  /** Handle for the native implementation. */
  private Object blob = null;

  /** The callback we're currently hanging on to. */
  private GenericCallback<String> callback = null;

  /** The content of the file, if it has been loaded. */
  public String content;

  public String name;

  public String type;

  public String lastModified;

  public int size;

  public File(Object blob) {
    this.blob = blob;
    readBlobProperties(this, blob);
  }

  /** Reads the content of the file as a binary string. */
  public void readAsBinaryString(GenericCallback<String> callback) {
    this.callback = callback;
    readFileImpl(this, "readAsBinaryString", blob);
  }

  /** Reads the content of the file as normal text. */
  public void readAsText(GenericCallback<String> callback) {
    this.callback = callback;
    readFileImpl(this, "readAsText", blob);
  }

  /** Reads the content of the file as a data url. */
  public void readAsDataURL(GenericCallback<String> callback) {
    this.callback = callback;
    readFileImpl(this, "readAsDataURL", blob);
  }

  /** Runs the native reader. */
  public native void readFileImpl(File self, String request, Object blob) /*-{
    try {

      // Create the reader.
      var reader = new FileReader();

      reader.onload = function(e) {
        var result = '' + e.target.result;
        self.@twists.client.components.files.File::readFileCallback(ZLjava/lang/String;)(true, result);
      };

      reader.onerror = function(e) {
        var msg = "An error occurred reading the file.";
        switch(e.target.error.code) {
          case e.target.error.NOT_FOUND_ERR:
            msg = 'File Not Found!';
            break;
          case e.target.error.NOT_READABLE_ERR:
            msg = 'File is not readable';
            break;
        }
        self.@twists.client.components.files.File::readFileCallback(ZLjava/lang/String;)(false, message);
      }

      reader.onabort = function(e) {
        var msg = "File read aborted.";
        self.@twists.client.components.files.File::readFileCallback(ZLjava/lang/String;)(false, message);
      };

      reader[request](blob);
    }
    catch(error) {
      var msg = '' + error;
      if (error.message)
        msg = error.message;
      self.@twists.client.components.files.File::readFileCallback(ZLjava/lang/String;)(false, message);
    }
  }-*/;

  /** Invoked by the native reader as a callback. */
  private void readFileCallback(boolean success, String data) {
    if (success) {
      content = data;
      callback.onSuccess(data);
    }
    else
      callback.onFailure(new Exception(data));
  }

  /** Invoked as a callback to set all properties at once. */
  private void readBlobPropertiesCallback(String name, int size, String type, String lastModified) {
    this.name = name;
    this.type = type;
    this.size = size;
    this.lastModified = lastModified;
  }

  /** Native property extraction. */
  private native String readBlobProperties(File self, Object blob) /*-{
    var name = blob.name;
    var size = blob.size;
    var type = blob.type;
    var modified = "N/A";
    if (blob.lastModifiedDate)
      modified = blob.lastModifiedDate.toLocaleDateString();
    self.@twists.client.components.files.File::readBlobPropertiesCallback(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)(name, size, type, modified);
  }-*/;

}
