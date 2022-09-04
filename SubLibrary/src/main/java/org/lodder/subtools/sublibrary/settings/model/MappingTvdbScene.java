package org.lodder.subtools.sublibrary.settings.model;

import java.io.Serializable;

public class MappingTvdbScene implements Serializable {

    private static final long serialVersionUID = 3125949308808140323L;
    private String sceneName;
    private int tvdbId;

    public MappingTvdbScene(String sceneName, int tvdbId) {
        this.setSceneName(sceneName);
        this.setTvdbId(tvdbId);
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public int getTvdbId() {
        return tvdbId;
    }

    public void setTvdbId(int tvdbId) {
        this.tvdbId = tvdbId;
    }

}
