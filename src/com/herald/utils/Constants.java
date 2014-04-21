package com.herald.utils;


public final class Constants {

	/** Error dialog codes */
	public static final class HeraldDialogCodes {
		public static final int ACTIVITYSUCCESS = 201;
		public static final int SUCCESS = 200;
		public static final int TIMEOUT = 1;
		public static final int NETWORK_ERROR = 2;
		public static final int BAD_REQUEST = 400;
		public static final int NOT_FOUND = 404;
		public static final int DATA_NOT_FOUND = 0;
		public static final int INTERNAL_SERVER_ERROR = 500;
	}

	/** Error dialog Messages */
	public static final class HeraldDialogMessages {
		public static final String TIMEOUT = "Timeout occurred. Please try again...";
		public static final String NETWORK_ERROR = "Network error. Please try again...";
		public static final String BAD_REQUEST = "Bad request. Please try again...";
		public static final String NOT_FOUND = "Data not found. Please try again...";
		public static final String INTERNAL_SERVER_ERROR = "Internal Server error. Pleas try again...";
	}

	/**
	 * 
	 * @param temp
	 * @return temperature in degree Fahrenheit
	 */
	public static int convertKelvinToDegreeFahrenheit(double temp) {

		double celcTemp = 0;
		celcTemp = ((temp - 273) * 1.8) + 32;
		return (int) Math.round(celcTemp);
	}

}
