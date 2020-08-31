import { WebPlugin } from '@capacitor/core';
import { FileSelectorPlugin } from './definitions';
export declare class FileSelectorWeb extends WebPlugin implements FileSelectorPlugin {
    constructor();
    fileSelector(options: {
        multiple_selection: boolean;
        ext: string[];
    }): Promise<any>;
}
declare const FileSelector: FileSelectorWeb;
export { FileSelector };
