import { WebPlugin } from '@capacitor/core';
import { FileSelectorPlugin } from './definitions';
export declare class FileSelectorWeb extends WebPlugin implements FileSelectorPlugin {
    private fileSelectorInput;
    private readonly ACCEPT_TYPES;
    constructor();
    fileSelector(options: {
        id?: string;
        multipleSelection: boolean;
        ext: string[];
    }): Promise<HTMLInputElement>;
}
declare const FileSelector: FileSelectorWeb;
export { FileSelector };
