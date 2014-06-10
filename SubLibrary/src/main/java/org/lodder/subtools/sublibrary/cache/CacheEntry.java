package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;
import java.util.Date;

public class CacheEntry implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6259654067628069940L;
    private String content;
    private Date expiresDate;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    public Date getExpiresDate() {
        return expiresDate;
    }

}
