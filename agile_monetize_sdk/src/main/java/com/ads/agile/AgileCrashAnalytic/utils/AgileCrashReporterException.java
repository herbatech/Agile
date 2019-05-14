package com.ads.agile.AgileCrashAnalytic.utils;



/**
 * Represents an error condition specific to the Crash Reporter for Android.
 */
public class AgileCrashReporterException extends RuntimeException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a new AgileCrashReporterException.
     */
    public AgileCrashReporterException() {
        super();
    }

    /**
     * Constructs a new AgileCrashReporterException.
     *
     * @param message the detail message of this exception
     */
    public AgileCrashReporterException(String message) {
        super(message);
    }

    /**
     * Constructs a new AgileCrashReporterException.
     *
     * @param message   the detail message of this exception
     * @param throwable the cause of this exception
     */
    public AgileCrashReporterException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new AgileCrashReporterException.
     *
     * @param throwable the cause of this exception
     */
    public AgileCrashReporterException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString() {
        // Throwable.toString() returns "AgileCrashReporterException:{message}". Returning just "{message}"
        // should be fine here.
        return getMessage();
    }
}
