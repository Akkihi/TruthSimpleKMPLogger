package dev.inmo.kslog.common

import dev.inmo.kslog.common.filter.filtered

/**
 * Logger based on [defaultLogging] or [logging] parameter
 *
 * @param defaultTag will be used in case when `tag` parameter in [performLog] omitted
 * @param messageFormatter special formatter which creating [String] inside of [performLog] for each call. Defaults to
 * [defaultMessageFormatter]
 * @param logging target lambda which will be called with a result of [messageFormatter] logs formatting as a message
 * and tag as tag if not `null` and [defaultTag] otherwise
 */
class DefaultKSLog(
    private val defaultTag: String,
    private val messageFormatter: MessageFormatter = defaultMessageFormatter,
    private val logging: (level: LogLevel, tag: String, message: Any, throwable: Throwable?) -> Unit = defaultLogging
) : KSLog {
    override fun performLog(level: LogLevel, tag: String?, message: Any, throwable: Throwable?) {
        val tag = tag ?: defaultTag

        val text = messageFormatter(level, tag, message, throwable)
        logging(level, tag, text, throwable)
    }

    override fun performLog(level: LogLevel, tag: String?, throwable: Throwable?, messageBuilder: () -> Any) {
        val tag = tag ?: defaultTag

        val text = messageFormatter(level, tag, messageBuilder(), throwable)
        logging(level, tag, text, throwable)
    }

    override suspend fun performLogS(
        level: LogLevel,
        tag: String?,
        throwable: Throwable?,
        messageBuilder: suspend () -> Any
    ) {
        val tag = tag ?: defaultTag

        val text = messageFormatter(level, tag, messageBuilder(), throwable)
        logging(level, tag, text, throwable)
    }
}

@Deprecated("Filtering should be replaced with FilterKSLog")
fun DefaultKSLog(
    defaultTag: String,
    filter: MessageFilter = { _, _, _ -> true },
    messageFormatter: MessageFormatter = defaultMessageFormatter,
    logging: (level: LogLevel, tag: String, message: Any, throwable: Throwable?) -> Unit = defaultLogging
) = DefaultKSLog(
    defaultTag, messageFormatter, logging
).filtered(filter)
