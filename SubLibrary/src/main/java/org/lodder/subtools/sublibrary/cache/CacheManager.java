package org.lodder.subtools.sublibrary.cache;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.http.HttpClient;


public class CacheManager {
	private static CacheManager uc = null;
	private final HashMap<String, CacheEntry> ucList;
	private String UserAgent;
	private static String DEFAULTUSERAGENT = "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)";
	private int ratelimit = 0; // unlimited per minute
	private int dayRateLimit = 0; // unlimited per day
	private Date oldDate = new Date();
	private long RateDuration;
	private static long DEFAULTRATEDURATION = MILLISECONDS.convert(1, MINUTES);
	private HashMap<String, Integer> internalCounter;
	private Preferences preferences;

	CacheManager() {
		Logger.instance.trace("CacheManager", "CacheManager", "");
		ucList = new HashMap<String, CacheEntry>();
		UserAgent = DEFAULTUSERAGENT;
		RateDuration = DEFAULTRATEDURATION;
		preferences = Preferences.userRoot();
		internalCounter = new HashMap<String, Integer>();
	}

	public static CacheManager getURLCache() {
		Logger.instance.trace("CacheManager", "getURLCache", "");
		if (uc == null)
			uc = new CacheManager();
		return uc;
	}

	public String fetchAsString(URL url, long timeout) throws Exception {
		Logger.instance.trace("CacheManager", "fetchAsString", "");

		/*
		 * if (getCacheEntry(url, timeout).getContent() == null){ return ""; }
		 */
		return getCacheEntry(url, timeout).getContent();
	}

	public InputStream fetchAsInputStream(URL url, long timeout)
			throws Exception {
		Logger.instance.trace("CacheManager", "fetchAsInputStream", "");

		String content = fetchAsString(url, timeout);
		return new ByteArrayInputStream(content.getBytes("UTF-8"));
	}

	/*
	 * timeout in seconds
	 */
	synchronized CacheEntry get(URL url, long timeout) throws Exception {
		Logger.instance.trace("CacheManager", "get", "");
		if (getDayRateLimit() > 0) {
			int propDayCounter = preferences
					.getInt("CacheManagerDayCounter", 0);
			Long propDayCounterDate = preferences.getLong(
					"CacheManagerDayCounterDate", new Date().getTime());

			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(new Date(propDayCounterDate));
			cal2.setTime(new Date());
			boolean sameDay = cal1.get(Calendar.YEAR) == cal2
					.get(Calendar.YEAR)
					&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
							.get(Calendar.DAY_OF_YEAR);

			if (!sameDay) {
				preferences.putLong("CacheManagerDayCounter",
						new Date().getTime());
				propDayCounter = 0;
			}

			if (propDayCounter > getDayRateLimit()) {
				throw new Exception("Day limit reached");
			} else {
				propDayCounter++;
				preferences.putInt("CacheManagerDayCounter", propDayCounter);
			}
		}

		if (ratelimit > 0 && !preferences.getBoolean("speedy", false)) {
			long duration = new Date().getTime() - oldDate.getTime();
			Logger.instance.debug("internalCounter for " + url.getHost()
					+ " : " + internalCounter.get(url.getHost()));
			Logger.instance.debug("ratelimit: " + ratelimit);
			Logger.instance.debug("duration: " + duration);
			Logger.instance.debug("RATEDURATION: " + RateDuration);
			if (internalCounter.containsKey(url.getHost())) {
				if (internalCounter.get(url.getHost()) >= ratelimit
						&& duration <= RateDuration) {
					String time = String.format(
							"%d min en %d sec",
							TimeUnit.MILLISECONDS.toMinutes(RateDuration),
							TimeUnit.MILLISECONDS.toSeconds(RateDuration)
									- TimeUnit.MINUTES
											.toSeconds(TimeUnit.MILLISECONDS
													.toMinutes(RateDuration)));
					Logger.instance.log("RateLimiet is bereikt voor '"
							+ url.getHost() + "', gelieve " + time
							+ " te wachten");
					while ((new Date().getTime() - oldDate.getTime()) <= RateDuration) {
						try {
							// Pause for 5 seconds
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt(); // restore
																// interrupted
																// status
						}

					}
					internalCounter.put(url.getHost(), 0);
				} else {
					if (duration > RateDuration) {
						internalCounter.put(url.getHost(), 0);
					}
				}
			} else {
				internalCounter.put(url.getHost(), 0);
			}
		}

		return doGet(url, timeout);
	}

	private CacheEntry doGet(URL url, long timeout) {
		Logger.instance.trace("CacheManager", "doGet", "");
		CacheEntry uce = new CacheEntry();

		Logger.instance.debug("CacheManager doGet: " + url.toString());
		uce.setContent(HttpClient.getHttpClient().doGet(url, UserAgent));

		Long maxAge = timeout * 1000;
		uce.setExpiresDate(new Date(System.currentTimeMillis() + maxAge));
		restoreDefaultUserAgent();
		restoreDefaultRateLimit();
		restoreDefaultRateDuration();
		oldDate = new Date();

		if (internalCounter.containsKey(url.getHost())) {
			internalCounter.put(url.getHost(),
					internalCounter.get(url.getHost()) + 1);
		} else {
			internalCounter.put(url.getHost(), 1);
		}

		Logger.instance.trace("CacheManager", "doGet",
				"uce content: '" + uce.getContent() + "'");
		return uce;
	}

	synchronized CacheEntry getCacheEntry(URL url, long timeout)
			throws Exception {
		Logger.instance.trace("CacheManager", "getCacheEntry", "");
		if (!ucList.containsKey(url.toString())) {
			synchronized (ucList) {
				ucList.put(url.toString(), get(url, timeout));
				Logger.instance.debug("Added cached element: " + url);
			}
		} else {
			Date current = new Date();
			if (ucList.get(url.toString()).getExpiresDate().compareTo(current) < 0) {
				synchronized (ucList) {
					ucList.put(url.toString(), get(url, timeout));
					Logger.instance.debug("Added cached element: " + url);
				}
			} else {
				Logger.instance.debug("Found cached element: " + url);
			}
		}
		return ucList.get(url.toString());
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return UserAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		UserAgent = userAgent;
	}

	private void restoreDefaultUserAgent() {
		this.setUserAgent(DEFAULTUSERAGENT);
	}

	private void restoreDefaultRateLimit() {
		this.setRatelimit(0);
		this.setDayRateLimit(0);
	}

	private void restoreDefaultRateDuration() {
		this.setRateDuration(DEFAULTRATEDURATION);
	}

	public void removeEntry(String url) {
		synchronized (ucList) {
			ucList.remove(url);
		}
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
	 * @return the dayRateLimit
	 */
	public int getDayRateLimit() {
		return dayRateLimit;
	}

	/**
	 * @param dayRateLimit
	 *            the dayRateLimit to set
	 */
	public void setDayRateLimit(int dayRateLimit) {
		this.dayRateLimit = dayRateLimit;
	}

	/**
	 * @return the rateDuration
	 */
	public long getRateDuration() {
		return RateDuration;
	}

	/**
	 * @param rateDuration
	 *            the rateDuration to set
	 */
	public void setRateDuration(long rateDuration) {
		RateDuration = rateDuration;
	}
}
