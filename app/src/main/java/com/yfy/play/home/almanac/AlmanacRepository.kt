package com.yfy.play.home.almanac

import android.app.Application
import android.net.Uri
import com.yfy.core.almanac.CalendarUtils
import com.yfy.model.room.PlayDatabase
import com.yfy.model.room.entity.Almanac
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.*
import javax.inject.Inject

/**
 * 日历
 * 描述：PlayAndroid
 *
 */
@ActivityRetainedScoped
class AlmanacRepository @Inject constructor(application: Application) {

    private val almanacDao = PlayDatabase.getDatabase(application).almanacDao()

    suspend fun getAlmanacUri(calendar: Calendar): Uri? {
        val julianDayFromCalendar =
            CalendarUtils.getJulianDayFromCalendar(calendar)
        val almanac = almanacDao.getAlmanac(julianDayFromCalendar)
        return if (almanac?.imgUri != null) {
            Uri.parse(almanac.imgUri)
        } else {
            null
        }
    }

    suspend fun addAlmanac(calendar: Calendar, imgUri: String) {
        almanacDao.insert(
            Almanac(
                julianDay = CalendarUtils.getJulianDayFromCalendar(calendar),
                imgUri = imgUri
            )
        )
    }

}