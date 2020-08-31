# Ionic Capacitor Dark Mode

For detailed tutorial on how to enable dark mode using this plugin visit:


Platforms Supported: Android, iOS, and Web/PWA

This plugin can be used to conditionally select files form Android/iOS devices and the web.

# Installation <br/>

<i> npm install capacitor-file-selector </i>

# Android Configuration: <br/>
Open MainActivity.java and add the following code inside this.init() <br/>
<i> add(FileSelector.class); </i> <br/>
Adding the above mentioned line will add the following import statement: <br/>
<i> import com.bkon.capacitor.fileselector.FileSelector; </i> <br/>
If you encounter errors, please add both the lines manually to MainActivity.java <br/>

To view all the supported Extensions please visit: <br/>
https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types <br/>


# iOS Configuration: <br/>
To view all the supported extensions please visit: <br/>
https://developer.apple.com/library/archive/documentation/Miscellaneous/Reference/UTIRef/Articles/System-DeclaredUniformTypeIdentifiers.html <br/>

# Web Configuration <br/>
<i> import { Plugins } from '@capacitor/core'; </i> <br/>
<i> const { FileSelector } = Plugins; </i> <br/>
<i>import ‘capacitor-file-selector’ // Comment out this line when building android/iOS app</i> <br/>


<b> SPECIAL NOTE: </b> When building the app for Android/iOS please do not forget to comment out “import ‘capacitor-file-selector’ ”. The import statement is used to register the web implementation of the plugin. If you register the web implementation of the plugin on native android/iOS app, the code that gets invoked is the web implementation instead of the native Android/iOS implementation. <br/>


# Select Files:

Supported extensions:- All extensions are supported. Please refer https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types   <br/>

Supported Broad Categories: “images”, “videos” and “audios”  <br/>

To allow the selection of all file types use “*” <br/>

If you wish to allow the user to select more than file, set the multiple_selection variable to true, else set it to false. Use the ext array to list out all the permitted extensions / broader file categories. The user will be able to select only the files with extensions / category outlined in this ext array. <br/>

The ext array IS case sensitive. All characters entered must be in lowercase. If not use Typescript’s map function to convert them into lowercase.  <br/>   
ext = ext.map(v => v.toLowerCase()); <br/>

Data returned by the file picker contains: <br/>
1. “paths” array - an array of web accessible URL(s) <br/>
2. “original_names” - an array of the name(s) of the file(s) <br/>
3. “extensions” - an array of extension(s) corresponding to the files selected <br/>

2 and 3 can be combined to rebuild the original file name. The following function illustrates how to upload files fetched from Android/iOS/Web to any server. <br/>

async select() <br/>
  { <br/>
    let multiple_selection = true <br/>
    //let ext = [".jpg",".png",".pdf",".jpeg"] // list of extensions <br/>
    let ext = ["MP3", "ImaGes"] // combination of extensions or category <br/> 
    //let ext = ["videos", "audios", "images"] // list of all category <br/>
    //let ext = ["*"] // Allow any file type <br/>
    ext = ext.map(v => v.toLowerCase()); <br/>
    let formData = new FormData(); <br/>
    let selectedFile = await FileSelector.fileSelector({ <br/>
      multiple_selection: multiple_selection, <br/>
      ext: ext <br/>
    }) <br/> 

    if(this.platform.is("android")) <br/>
    { <br/>
      let paths = JSON.parse(selectedFile.paths) <br/>
      let original_names = JSON.parse(selectedFile.original_names) <br/>
      let extensions = JSON.parse(selectedFile.extensions) <br/>
      for (let index = 0; index < paths.length; index++) { <br/>
          const file = await fetch(paths[index]).then((r) => r.blob()); <br/>
          formData.append( <br/>
            "myfile[]", <br/>
            file, <br/>
            original_names[index] + extensions[index] <br/>
          ); <br/>
        } <br/>
    } <br/>
    else if(this.platform.is("ios")) <br/>
    { <br/>
      for (let index = 0; index < selectedFile.paths.length; index++) { <br/>
        const file = await fetch(selectedFile.paths[index]).then((r) => r.blob()); <br/>
        formData.append( <br/>
          "myfile[]", <br/>
          file, <br/>
          selectedFile.original_names[index] + selectedFile.extensions[index] <br/>
        ); <br/>
      } <br/>
    } <br/>
    else <br/>
    { <br/>
      FileSelector.addListener("onFilesSelected", (data:FileList) => { <br/>
            for(var i = 0; i < data.length; i++) <br/>
            { <br/>
              formData.append( <br/>
                "myfile[]", <br/>
                data.item(i), <br/>
                data.item(i).name + data.item(i).type  <br/>
              ); <br/>
            } <br/>
        });  <br/>
    } <br/>
  } <br/>

