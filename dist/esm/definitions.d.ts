import { PluginListenerHandle } from "@capacitor/core";
declare module '@capacitor/core' {
    interface PluginRegistry {
        FileSelector: FileSelectorPlugin;
    }
}
export interface FileSelectorPlugin {
    fileSelector(options: {
        id?: string;
        multipleSelection: boolean;
        ext: string[];
    }): Promise<HTMLInputElement>;
    addListener(eventName: 'onFilesSelected', listenerFunc: (state: any) => void): PluginListenerHandle;
}
