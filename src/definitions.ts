import { PluginListenerHandle } from "@capacitor/core";

declare module '@capacitor/core' {
  interface PluginRegistry {
    FileSelector: FileSelectorPlugin;
  }
}

export interface FileSelectorPlugin {
  fileSelector(options:{multiple_selection:boolean,ext:string[]}):Promise<any>;
  addListener(
    eventName: 'onFilesSelected',
    listenerFunc: (state: any) => void,
  ): PluginListenerHandle;
}
