package com.example.smartresourceallocation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun parseReservationDateTime(
        date: String,
        time: String
    ): Date {

        val utcParser =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )

        utcParser.timeZone =
            TimeZone.getTimeZone("UTC")

        val localDate =
            utcParser.parse(date)!!

        val localDateFormatter =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        localDateFormatter.timeZone =
            TimeZone.getDefault()

        val actualDate =
            localDateFormatter.format(localDate)

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
            )

        formatter.timeZone =
            TimeZone.getDefault()

        return formatter.parse("$actualDate $time")!!
    }

    fun formatReservationDateForEdit(
        date: String
    ): String {

        return try {

            val parser =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.getDefault()
                )

            parser.timeZone =
                TimeZone.getTimeZone("UTC")

            val formatter =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            formatter.timeZone =
                TimeZone.getDefault()

            formatter.format(parser.parse(date)!!)

        } catch (e: Exception) {

            date

        }

    }

    fun format(date: String): String {

        return try {

            val parser = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )

            parser.timeZone = TimeZone.getTimeZone("UTC")

            val formatter = SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

            formatter.timeZone = java.util.TimeZone.getDefault()

            formatter.format(parser.parse(date)!!)

        } catch (e: Exception) {

            date

        }

    }
    fun formatCreatedDate(date: String): String {

        return try {

            val parser = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )

            parser.timeZone = TimeZone.getTimeZone("UTC")

            val formatter = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

            formatter.timeZone = TimeZone.getDefault()

            formatter.format(parser.parse(date)!!)

        } catch (e: Exception) {

            date

        }

    }
    fun formatReservationDate(date: String): String {

        return try {

            val parser = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            )

            parser.timeZone = java.util.TimeZone.getTimeZone("UTC")

            val formatter = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

            formatter.timeZone = TimeZone.getDefault()

            formatter.format(parser.parse(date)!!)

        } catch (e: Exception) {

            date

        }


    }
    fun formatShortDate(

        date: String

    ): String {

        return try {

            val parser =
                SimpleDateFormat(

                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",

                    Locale.getDefault()

                )

            parser.timeZone =
                TimeZone.getTimeZone("UTC")

            val formatter =
                SimpleDateFormat(

                    "dd MMM",

                    Locale.getDefault()

                )

            formatter.format(
                parser.parse(date)!!
            )

        }

        catch (e: Exception) {

            date

        }

    }

}