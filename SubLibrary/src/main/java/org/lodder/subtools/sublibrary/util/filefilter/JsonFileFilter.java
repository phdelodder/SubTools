package org.lodder.subtools.sublibrary.util.filefilter;

public class JsonFileFilter extends ExtensionFileFilter {

    @Override
    public String getDescription() {
        return "json files";
    }

    @Override
    public String getExtension() {
        return "json";
    }
}
