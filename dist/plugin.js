var capacitorPlugin = (function (exports, core) {
    'use strict';

    class FileSelectorWeb extends core.WebPlugin {
        constructor() {
            super({
                name: 'FileSelector',
                platforms: ['web', 'android', 'ios'],
            });
        }
        fileSelector(options) {
            var x = document.createElement("INPUT");
            x.hidden = true;
            x.setAttribute("type", "file");
            x.setAttribute("id", "filePicker");
            var accept = "";
            options.ext.forEach(element => {
                if (element == 'images') {
                    accept = accept.concat("image/*,");
                }
                else if (element == 'videos') {
                    accept = accept.concat("video/*,");
                }
                else if (element == 'audios') {
                    accept = accept.concat("audio/*,");
                }
            });
            let index = options.ext.indexOf("images");
            if (index > -1) {
                options.ext.splice(index, 1);
            }
            index = options.ext.indexOf("videos");
            if (index > -1) {
                options.ext.splice(index, 1);
            }
            index = options.ext.indexOf("audios");
            if (index > -1) {
                options.ext.splice(index, 1);
            }
            options.ext = options.ext.map(v => "." + v);
            accept = accept.concat(options.ext.join(","));
            x.setAttribute("accept", accept);
            if (options.multiple_selection) {
                x.setAttribute("multiple", "true");
            }
            x.addEventListener("change", () => {
                if (document != null) {
                    this.notifyListeners("onFilesSelected", x.files);
                }
            });
            x.click();
            return Promise.resolve();
        }
    }
    const FileSelector = new FileSelectorWeb();
    core.registerWebPlugin(FileSelector);

    exports.FileSelector = FileSelector;
    exports.FileSelectorWeb = FileSelectorWeb;

    return exports;

}({}, capacitorExports));
//# sourceMappingURL=plugin.js.map
