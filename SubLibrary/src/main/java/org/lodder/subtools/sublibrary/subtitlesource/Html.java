package org.lodder.subtools.sublibrary.subtitlesource;

import java.net.MalformedURLException;
import java.net.URL;

import org.lodder.subtools.sublibrary.cache.CacheManager;
import org.lodder.subtools.sublibrary.cache.DiskCacheManager;
import org.lodder.subtools.sublibrary.logging.Logger;

public class Html {

	private final CacheManager ucm;
	private final DiskCacheManager dcm;
	private static long cacheTimeout = 900;
	private static long diskTimeout = 24 * 60 * 60 * 5; // 5 day cache
	private String userAgent;
	private int ratelimit;
	private int dayRatelimit;
	private long rateDuration;

	public Html() {
		ucm = CacheManager.getURLCache();
		dcm = DiskCacheManager.getDiskCache();
		this.setRatelimit(0);
		this.setDayRatelimit(0);
		this.setRateDuration(0);
	}

	public String getHtml(String url) {
		return getHtml(url, cacheTimeout);
	}

	public String getHtml(String url, long timeout) {
		if (userAgent != null) {
			ucm.setUserAgent(userAgent);
		}
		ucm.setRatelimit(ratelimit);
		ucm.setDayRateLimit(dayRatelimit);
		ucm.setRateDuration(rateDuration);
		try {
			return ucm.fetchAsString(new URL(url), timeout);
		} catch (MalformedURLException e) {
			Logger.instance.error(Logger.stack2String(e));
		} catch (Exception e) {
			Logger.instance.error(Logger.stack2String(e));
		}
		return "";
	}

	public String getHtmlDisk(String url) {
		return getHtmlDisk(url, diskTimeout);
	}

	public String getHtmlDisk(String url, long timeout) {
		if (userAgent != null) {
			dcm.setUserAgent(userAgent);
		}
		dcm.setRatelimit(ratelimit);
		dcm.setDayRateLimit(dayRatelimit);
		dcm.setRateDuration(rateDuration);
		try {
			return dcm.fetchAsString(new URL(url), timeout);
		} catch (MalformedURLException e) {
			Logger.instance.error(Logger.stack2String(e));
		} catch (Exception e) {
			Logger.instance.error(Logger.stack2String(e));
		}
		return "";
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @return the ratelimit
	 */
	public int getRatelimit() {
		return ratelimit;
	}

	/**
	 * @param ratelimit
	 *            the ratelimit to set
	 */
	public void setRatelimit(int ratelimit) {
		this.ratelimit = ratelimit;
	}

	/**
	 * @return the dayRatelimit
	 */
	public int getDayRatelimit() {
		return dayRatelimit;
	}

	/**
	 * @param dayRatelimit
	 *            the dayRatelimit to set
	 */
	public void setDayRatelimit(int dayRatelimit) {
		this.dayRatelimit = dayRatelimit;
	}

	/**
	 * @return the rateDuration
	 */
	public long getRateDuration() {
		return rateDuration;
	}

	/**
	 * @param rateDuration the rateDuration to set
	 */
	public void setRateDuration(long rateDuration) {
		this.rateDuration = rateDuration;
	}
}
