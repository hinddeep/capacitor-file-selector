import Foundation
import Capacitor
import CoreServices

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FileSelector)
public class FileSelector: CAPPlugin {
// https://developer.apple.com/library/archive/documentation/Miscellaneous/Reference/UTIRef/Articles/System-DeclaredUniformTypeIdentifiers.html
    
    var savedCall: CAPPluginCall? = nil

    @objc func fileSelector(_ call: CAPPluginCall) {
        savedCall = call
        let multiple_selection = call.getBool("multiple_selection") ?? true
        let exts: [String] = call.getArray("ext", String.self) ?? ["*"]
        var extUTIs: [String] = []
        for element in exts
        {
            let fileExtension: CFString = element as CFString
            var extUTI:CFString?
            if(element == "images")
            {
                extUTI = kUTTypeImage
            }
            else if(element == "videos")
            {
                extUTI = kUTTypeVideo
            }
            else if(element == "audios")
            {
                extUTI = kUTTypeAudio
            }
            else if(element == "*")
            {
                extUTI = kUTTypeData
            }
            else
            {
               extUTI  = UTTypeCreatePreferredIdentifierForTag(
                    kUTTagClassFilenameExtension,
                    fileExtension,
                    nil
                )?.takeUnretainedValue()
            }
            extUTIs.append(extUTI! as String)
        }
        
        DispatchQueue.main.async {
            let types: [String] = extUTIs
            let documentPicker = UIDocumentPickerViewController(documentTypes: types, in: .import)
            documentPicker.delegate = self
            documentPicker.modalPresentationStyle = .formSheet
            documentPicker.allowsMultipleSelection = multiple_selection
            self.bridge.viewController.present(documentPicker, animated: true, completion: nil)
            
        }
    }
}

extension FileSelector: UIDocumentPickerDelegate {
    public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
       var paths:[String] = []
       var original_names:[String] = []
       var extensions:[String] = []
       for value in urls
       {
           paths.append(value.absoluteString.replacingOccurrences(of: "file:///", with: "capacitor://localhost/_capacitor_file_/"))
          // paths.append(value.absoluteString)
           original_names.append(value.lastPathComponent)
           extensions.append(value.pathExtension)
       }
       savedCall!.success([
           "paths" : paths,
           "original_names": original_names,
           "extensions" : extensions
       ])
    }
}
    
   
