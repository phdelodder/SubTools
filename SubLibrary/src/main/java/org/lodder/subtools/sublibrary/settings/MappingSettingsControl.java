/**
 *
 */
package org.lodder.subtools.sublibrary.settings;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.lodder.subtools.sublibrary.settings.model.MappingSettings;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.lodder.subtools.sublibrary.xml.XMLMappingTvdbScene;

/**
 * @author lodder
 *
 */
public class MappingSettingsControl {

    private MappingSettings mappingSettings;
    private Preferences preferences;

    public MappingSettingsControl(Preferences preferences) {
        setMappingSettings(new MappingSettings());
        this.preferences = preferences;
    }

    /**
     *
     */
    public void store() {
        int last = 0;
        for (int i = 0; i < mappingSettings.getMappingList().size(); i++) {
            preferences.put("Dictionary" + i, mappingSettings.getMappingList().get(i).getSceneName()
                    + "\\\\" + mappingSettings.getMappingList().get(i).getTvdbId());
            last++;
        }
        preferences.putInt("lastItemDictionary", last);
        preferences.putInt("mappingVersion", mappingSettings.getMappingVersion());
    }

    public void load() {
        int last = preferences.getInt("lastItemDictionary", 0);
        mappingSettings.setMappingVersion(preferences.getInt("mappingVersion", 0));
        for (int i = 0; i < last; i++) {
            int tvdbid = 0;
            String s = preferences.get("Dictionary" + i, "");
            String[] items = s.split("\\\\");
            if ((items.length == 3) && (items[2].length() != 0)) {
                tvdbid = Integer.parseInt(items[2]);
            }
            MappingTvdbScene item = new MappingTvdbScene(items[0], tvdbid);
            mappingSettings.getMappingList().add(item);
        }
    }

    public void updateMappingFromOnline() throws Throwable {
        /*
         * int mappingVersion = XMLMappingTvdbScene.getMappingsVersionNumber();
         * if (mappingVersion > mappingSettings.getMappingVersion()) {
         */
        ArrayList<MappingTvdbScene> onlineList = XMLMappingTvdbScene.getOnlineMappingCollection();
        if (mappingSettings.getMappingList().size() == 0) {
            mappingSettings.setMappingList(onlineList);
        } else {
            for (MappingTvdbScene onlineItem : onlineList) {
                boolean missing = true;
                for (int i = 0; i < mappingSettings.getMappingList().size(); i++) {
                    MappingTvdbScene localItem = mappingSettings.getMappingList().get(i);
                    if (onlineItem.getTvdbId() != localItem.getTvdbId() && onlineItem.getSceneName()
                            .equalsIgnoreCase(localItem.getSceneName())) {
                        localItem = onlineItem;
                        mappingSettings.getMappingList().set(i, onlineItem);
                        missing = false;
                        break;
                    } else if (onlineItem.getTvdbId() == localItem.getTvdbId() && onlineItem
                            .getSceneName().equalsIgnoreCase(localItem.getSceneName())) {
                        missing = false;
                        break;
                    }
                }
                if (missing) {
                    mappingSettings.getMappingList().add(onlineItem);
                }
            }
        }
        // }
    }

    /**
     * @return the mappingSettings
     */
    public MappingSettings getMappingSettings() {
        return mappingSettings;
    }

    /**
     * @param mappingSettings the mappingSettings to set
     */
    public void setMappingSettings(MappingSettings mappingSettings) {
        this.mappingSettings = mappingSettings;
    }
}
