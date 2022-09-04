/**
 *
 */
package org.lodder.subtools.sublibrary.settings.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lodderp
 *
 */
public class MappingSettings {
    private List<MappingTvdbScene> mappingList;
    private int mappingVersion;

    public MappingSettings() {
        mappingList = new ArrayList<>();
        mappingVersion = 0;
    }

    public List<MappingTvdbScene> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<MappingTvdbScene> mappingList) {
        this.mappingList = mappingList;
    }

    public int getMappingVersion() {
        return mappingVersion;
    }

    public void setMappingVersion(int mappingVersion) {
        this.mappingVersion = mappingVersion;
    }
}
