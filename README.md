# Capacitor File Selector

For detailed tutorial on how to enable dark mode using this plugin visit:
https://medium.com/@hinddeep.purohit007/picking-files-in-capacitor-apps-a82c67384496 <br/>

Demo Application: https://github.com/hinddeep/demo-capacitor-file-picker <br/>

Platforms Supported: Android, iOS, and Web/PWA

This plugin can be used to conditionally select files form Android/iOS devices and the web.

# Installation <br/>

```
npm install capacitor-file-selector
```

# Android Configuration: <br/>
Open MainActivity.java and add the following code inside this.init() <br/>
```
add(FileSelector.class);
Adding the above mentioned line will add the following import statement: 
import com.bkon.capacitor.fileselector.FileSelector;
```
If you encounter errors, please add both the lines manually to MainActivity.java <br/>

To view all the supported Extensions please visit: <br/>
https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types <br/>


# iOS Configuration: <br/>
To view all the supported extensions please visit: <br/>
https://developer.apple.com/library/archive/documentation/Miscellaneous/Reference/UTIRef/Articles/System-DeclaredUniformTypeIdentifiers.html <br/>

# Web Configuration <br/>
```
import { Plugins } from '@capacitor/core'; 
const { FileSelector } = Plugins; 
import ‘capacitor-file-selector’ // Comment out this line when building android/iOS app<br/>
```


<b> SPECIAL NOTE: </b> When building the app for Android/iOS please do not forget to comment out “import ‘capacitor-file-selector’ ”. The import statement is used to register the web implementation of the plugin. If you register the web implementation of the plugin on native android/iOS app, the code that gets invoked is the web implementation instead of the native Android/iOS implementation. <br/>


# Select Files:

Supported extensions:- All extensions are supported. Please refer https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types   <br/>

Supported Broad Categories: “images”, “videos” and “audios”  <br/>

To allow the selection of all file types use “*” <br/>

If you wish to allow the user to select more than file, set the multiple_selection variable to true, else set it to false. Use the ext array to list out all the permitted extensions / broader file categories. The user will be able to select only the files with extensions / category outlined in this ext array. <br/>

The ext array IS case sensitive. All characters entered must be in lowercase. If not use Typescript’s map function to convert them into lowercase.  <br/> 
```  
ext = ext.map(v => v.toLowerCase());
```

Data returned by the file picker contains: <br/>
1. “paths” array - an array of web accessible URL(s) <br/>
2. “original_names” - an array of the name(s) of the file(s) <br/>
3. “extensions” - an array of extension(s) corresponding to the files selected <br/>

2 and 3 can be combined to rebuild the original file name. The following function illustrates how to upload files fetched from Android/iOS/Web to any server. <br/>

```
async select() { 
    let multiple_selection = true 
    //let ext = [".jpg",".png",".pdf",".jpeg"] // list of extensions
    let ext = ["MP3", "ImaGes"] // combination of extensions or category 
    //let ext = ["videos", "audios", "images"] // list of all category
    //let ext = ["*"] // Allow any file type
    ext = ext.map(v => v.toLowerCase()); 
    let formData = new FormData(); 
    let selectedFile = await FileSelector.fileSelector({ 
      multiple_selection: multiple_selection, 
      ext: ext 
    }) 

    if(this.platform.is("android")) 
    { 
      let paths = JSON.parse(selectedFile.paths) 
      let original_names = JSON.parse(selectedFile.original_names) 
      let extensions = JSON.parse(selectedFile.extensions) 
      for (let index = 0; index < paths.length; index++) { 
          const file = await fetch(paths[index]).then((r) => r.blob()); 
          formData.append( 
            "myfile[]", 
            file, 
            original_names[index] + extensions[index] 
          ); 
        }
    } 
    else if(this.platform.is("ios")) 
    { 
      for (let index = 0; index < selectedFile.paths.length; index++) { 
        const file = await fetch(selectedFile.paths[index]).then((r) => r.blob()); 
        formData.append( 
          "myfile[]", 
          file, 
          selectedFile.original_names[index] + selectedFile.extensions[index] 
        ); 
      } 
    } 
    else 
    { 
      FileSelector.addListener("onFilesSelected", (data:FileList) => { 
            for(var i = 0; i < data.length; i++) 
            { 
              formData.append( 
                "myfile[]", 
                data.item(i), 
                data.item(i).name + data.item(i).type  
              );
            }
        }); 
    } 
}
```
