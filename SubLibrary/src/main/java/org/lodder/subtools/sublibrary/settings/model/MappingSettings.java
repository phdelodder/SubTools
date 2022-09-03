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

    /**
     * @return the mappingVersion
     */
    public int getMappingVersion() {
        return mappingVersion;
    }

    /**
     * @param mappingVersion the mappingVersion to set
     */
    public void setMappingVersion(int mappingVersion) {
        this.mappingVersion = mappingVersion;
    }
}
