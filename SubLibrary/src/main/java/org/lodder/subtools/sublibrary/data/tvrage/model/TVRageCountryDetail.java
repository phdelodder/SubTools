package org.lodder.subtools.sublibrary.data.tvrage.model;

import static org.lodder.subtools.sublibrary.data.tvrage.TVRageApi.*;

/*
 *      Copyright (c) 2004-2013 Stuart Boston
 *
 *      This file is part of the TVRage API.
 *
 *      TVRage API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TVRage API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TVRage API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;

import lombok.Getter;

/**
 * Class to hold country along with a generic detail string
 *
 * @author Stuart.Boston
 *
 */
@Getter
public class TVRageCountryDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String country;
    private final String detail;

    public TVRageCountryDetail(String country, String detail) {
        this.country = isValidString(country) ? country.trim() : TVRageApi.UNKNOWN;
        this.detail = isValidString(detail) ? detail.trim() : TVRageApi.UNKNOWN;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean isValid() {
        return isValidString(country) && isValidString(detail);
    }
}
