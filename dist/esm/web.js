import { WebPlugin } from '@capacitor/core';
export class FileSelectorWeb extends WebPlugin {
    constructor() {
        super({
            name: 'FileSelector',
            platforms: ['web', 'android', 'ios'],
        });
        this.ACCEPT_TYPES = new Map([['*', 'image/*,video/*,audio/*'], ['images', 'image/*,'],
            ['videos', 'video/*,'], ['audios', 'audio/*,']]);
    }
    fileSelector(options) {
        if (typeof this.fileSelectorInput === 'undefined') {
            this.fileSelectorInput = document.createElement('INPUT');
            this.fileSelectorInput.hidden = true;
            this.fileSelectorInput.setAttribute('type', 'file');
            this.fileSelectorInput.setAttribute('id', (options === null || options === void 0 ? void 0 : options.id) ? options === null || options === void 0 ? void 0 : options.id : 'filePicker');
            let accept = '';
            options.ext.forEach(element => {
                if (this.ACCEPT_TYPES.has(element)) {
                    accept = accept.concat(this.ACCEPT_TYPES.get(element));
                    options.ext = options.ext.filter(option => option !== element);
                }
            });
            options.ext = options.ext.map(v => '.' + v);
            accept = accept.concat(options.ext.join(','));
            this.fileSelectorInput.setAttribute('accept', accept);
            if (options.multipleSelection) {
                this.fileSelectorInput.setAttribute('multiple', 'true');
            }
        }
        this.fileSelectorInput.addEventListener('change', (event) => {
            if ((document != null) && (typeof this.fileSelectorInput !== 'undefined')) {
                event.stopPropagation();
                this.notifyListeners('onFilesSelected', this.fileSelectorInput.files);
                this.removeAllListeners();
            }
        });
        this.fileSelectorInput.value = '';
        this.fileSelectorInput.click();
        return Promise.resolve(this.fileSelectorInput);
    }
}
const FileSelector = new FileSelectorWeb();
export { FileSelector };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(FileSelector);
//# sourceMappingURL=web.js.map