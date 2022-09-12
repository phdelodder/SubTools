/**
 *
 */
package org.lodder.subtools.sublibrary.settings.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lodderp
 *
 */
@Getter
@Setter
public class MappingSettings {
    private List<MappingTvdbScene> mappingList= new ArrayList<>();
    private int mappingVersion = 0;
}
