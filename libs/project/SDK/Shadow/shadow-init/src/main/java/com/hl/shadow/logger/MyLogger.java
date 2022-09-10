package com.hl.shadow.logger;

import android.util.Log;

import com.tencent.shadow.core.common.Logger;

import org.jetbrains.annotations.Nullable;

/**
 * @author 张磊  on  2021/05/22 at 16:22
 * Email: 913305160@qq.com
 */
abstract class MyLogger implements Logger {

    private static final int LOG_LEVEL_TRACE = 1;
    private static final int LOG_LEVEL_DEBUG = 2;
    private static final int LOG_LEVEL_INFO = 3;
    private static final int LOG_LEVEL_WARN = 4;
    private static final int LOG_LEVEL_ERROR = 5;

    private String name;

    MyLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 打印相关日志
     */
    abstract void log(LogLevel logLevel, String message, @Nullable Throwable t);

    /**
     * 设置 logger 等级， 设置高级别后低级别的日志将不再打印
     */
    abstract LogLevel initLogLevel();


    /**
     * 是否可打印日志
     */
    private boolean isLogEnabled(int level) {
        return level >= initLogLevel().getLevel();
    }

    private void log(int level, String message, Throwable t) {
        final String tag = String.valueOf(name);

        switch (level) {
            case LOG_LEVEL_TRACE:
            case LOG_LEVEL_DEBUG:
                if (t == null) {
                    Log.d(tag, message);
                } else {
                    Log.d(tag, message, t);
                }
                break;
            case LOG_LEVEL_INFO:
                if (t == null) {
                    Log.i(tag, message);
                } else {
                    Log.i(tag, message, t);
                }
                break;
            case LOG_LEVEL_WARN:
                if (t == null) {
                    Log.w(tag, message);
                } else {
                    Log.w(tag, message, t);
                }
                break;
            case LOG_LEVEL_ERROR:
                if (t == null) {
                    Log.e(tag, message);
                } else {
                    Log.e(tag, message, t);
                }
                break;
            default:
                break;
        }

        if (isLogEnabled(level)) {
            log(LogLevel.getLogLevelByLevel(level), message, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return isLogEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public void trace(String msg) {
        log(LOG_LEVEL_TRACE, msg, null);
    }

    @Override
    public void trace(String format, Object o) {
        FormattingTuple tuple = MessageFormatter.format(format, o);
        log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
    }

    @Override
    public void trace(String format, Object o, Object o1) {
        FormattingTuple tuple = MessageFormatter.format(format, o, o1);
        log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
    }

    @Override
    public void trace(String format, Object... objects) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
        log(LOG_LEVEL_TRACE, tuple.getMessage(), null);
    }

    @Override
    public void trace(String msg, Throwable throwable) {
        log(LOG_LEVEL_TRACE, msg, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLogEnabled(LOG_LEVEL_DEBUG);
    }

    @Override
    public void debug(String msg) {
        log(LOG_LEVEL_DEBUG, msg, null);
    }

    @Override
    public void debug(String format, Object o) {
        FormattingTuple tuple = MessageFormatter.format(format, o);
        log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
    }

    @Override
    public void debug(String format, Object o, Object o1) {
        FormattingTuple tuple = MessageFormatter.format(format, o, o1);
        log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
    }

    @Override
    public void debug(String format, Object... objects) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
        log(LOG_LEVEL_DEBUG, tuple.getMessage(), null);
    }

    @Override
    public void debug(String msg, Throwable throwable) {
        log(LOG_LEVEL_DEBUG, msg, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLogEnabled(LOG_LEVEL_INFO);
    }

    @Override
    public void info(String msg) {
        log(LOG_LEVEL_INFO, msg, null);
    }

    @Override
    public void info(String format, Object o) {
        FormattingTuple tuple = MessageFormatter.format(format, o);
        log(LOG_LEVEL_INFO, tuple.getMessage(), null);
    }

    @Override
    public void info(String format, Object o, Object o1) {
        FormattingTuple tuple = MessageFormatter.format(format, o, o1);
        log(LOG_LEVEL_INFO, tuple.getMessage(), null);
    }

    @Override
    public void info(String format, Object... objects) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
        log(LOG_LEVEL_INFO, tuple.getMessage(), null);
    }

    @Override
    public void info(String msg, Throwable throwable) {
        log(LOG_LEVEL_INFO, msg, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLogEnabled(LOG_LEVEL_WARN);
    }

    @Override
    public void warn(String msg) {
        log(LOG_LEVEL_WARN, msg, null);
    }

    @Override
    public void warn(String format, Object o) {
        FormattingTuple tuple = MessageFormatter.format(format, o);
        log(LOG_LEVEL_WARN, tuple.getMessage(), null);
    }

    @Override
    public void warn(String format, Object o, Object o1) {
        FormattingTuple tuple = MessageFormatter.format(format, o, o1);
        log(LOG_LEVEL_WARN, tuple.getMessage(), null);
    }

    @Override
    public void warn(String format, Object... objects) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
        log(LOG_LEVEL_WARN, tuple.getMessage(), null);
    }

    @Override
    public void warn(String msg, Throwable throwable) {
        log(LOG_LEVEL_WARN, msg, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLogEnabled(LOG_LEVEL_ERROR);
    }

    @Override
    public void error(String msg) {
        log(LOG_LEVEL_ERROR, msg, null);
    }

    @Override
    public void error(String format, Object o) {
        FormattingTuple tuple = MessageFormatter.format(format, o);
        log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
    }

    @Override
    public void error(String format, Object o, Object o1) {
        FormattingTuple tuple = MessageFormatter.format(format, o, o1);
        log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
    }

    @Override
    public void error(String format, Object... objects) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, objects);
        log(LOG_LEVEL_ERROR, tuple.getMessage(), null);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        log(LOG_LEVEL_ERROR, msg, throwable);
    }
}