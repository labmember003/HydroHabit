package com.falcon.hydrohabit.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.model.IntakeLog
import com.falcon.hydrohabit.features.onboarding.usecase.Gender
import com.falcon.hydrohabit.features.profilescreen.utils.ProfileData
import com.falcon.hydrohabit.history.HistoryScreen
import com.falcon.hydrohabit.settings.SettingsScreen
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class NewScreensUiTest {

    private val today = LocalDate(2026, 11, 18)
    private fun at(day: Int, hour: Int, minute: Int) =
        LocalDateTime(2026, 11, day, hour, minute).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

    private val log = IntakeLog(
        entries = listOf(
            IntakeEntry("a", at(18, 9, 30), "2026-11-18", 250),
            IntakeEntry("b", at(18, 13, 5), "2026-11-18", 500),
            IntakeEntry("c", at(17, 10, 0), "2026-11-17", 2000),
        ),
        goals = mapOf("2026-11-17" to 2000, "2026-11-18" to 2000),
    )

    @Test
    fun historyShowsStatsAndTodaysDrinks() = runComposeUiTest {
        setContent { HistoryScreen(log = log, today = today, currentGoalMl = 2000, onDeleteEntry = {}) }
        onNodeWithTag("monthLabel").assertTextEquals("November 2026")
        onNodeWithText("1 of 2").assertExists() // first drink was on the 17th
        onNodeWithTag("history").performScrollToNode(hasTestTag("dayTotal"))
        onNodeWithTag("dayTotal").assertTextEquals("750 ml of 2,000 ml · 37%")
        onNodeWithText("1:05 PM").assertExists()
        onNodeWithText("9:30 AM").assertExists()
    }

    @Test
    fun deleteButtonReportsTheRightDrink() = runComposeUiTest {
        var deleted: IntakeEntry? = null
        setContent { HistoryScreen(log = log, today = today, currentGoalMl = 2000, onDeleteEntry = { deleted = it }) }
        assertNull(deleted)
        onNodeWithTag("history").performScrollToNode(hasTestTag("dayTotal"))
        onNodeWithContentDescription("Delete 500 ml logged at 1:05 PM").performClick()
        assertEquals("b", deleted?.id)
    }

    @Test
    fun tappingAPastDayShowsThatDay() = runComposeUiTest {
        setContent { HistoryScreen(log = log, today = today, currentGoalMl = 2000, onDeleteEntry = {}) }
        onNodeWithContentDescription("Tuesday, Nov 17, 100 percent of goal").performClick()
        onNodeWithTag("history").performScrollToNode(hasTestTag("dayTotal"))
        onNodeWithText("Tuesday, Nov 17").assertExists()
        onNodeWithTag("dayTotal").assertTextEquals("2,000 ml of 2,000 ml · 100%")

        onNodeWithTag("history").performScrollToNode(hasText("November 2026"))
        onNodeWithContentDescription("Monday, Nov 2, 0 percent of goal").performClick()
        onNodeWithTag("history").performScrollToNode(hasText("No drinks logged on this day."))
        onNodeWithText("No drinks logged on this day.").assertExists()
    }

    @Test
    fun monthNavigationStopsAtTheCurrentMonth() = runComposeUiTest {
        setContent { HistoryScreen(log = log, today = today, currentGoalMl = 2000, onDeleteEntry = {}) }
        onNodeWithContentDescription("Next month").assertIsNotEnabled()
        onNodeWithContentDescription("Previous month").performClick()
        onNodeWithTag("monthLabel").assertTextEquals("October 2026")
        // October is before this user's first day, so nothing counts as missed.
        onAllNodesWithText("–").assertCountEquals(2)
        onNodeWithContentDescription("Next month").assertIsEnabled().performClick()
        onNodeWithTag("monthLabel").assertTextEquals("November 2026")
    }

    @Test
    fun emptyHistoryExplainsWhatToDo() = runComposeUiTest {
        setContent { HistoryScreen(log = IntakeLog(), today = today, currentGoalMl = 2000, onDeleteEntry = {}) }
        onNodeWithText("0 of 1").assertExists()
        onNodeWithText("–").assertExists()
        onNodeWithTag("history").performScrollToNode(hasText("No drinks logged yet today. Tap Add on the Home tab."))
        onNodeWithText("No drinks logged yet today. Tap Add on the Home tab.").assertExists()
    }

    private fun settings(provider: String?, onHealth: (Boolean) -> Unit = {}) = @androidx.compose.runtime.Composable {
        SettingsScreen(
            profileData = ProfileData(
                onNotificationChange = true,
                selectedIntervalIndex = 1,
                wakeUpHour = 7,
                wakeUpMinute = 0,
                bedHour = 23,
                bedMinute = 0,
                selectedSoundIndex = 0,
                customSoundUri = null,
            ),
            heightCm = 170,
            weightKg = 65,
            gender = Gender.FEMALE,
            getGenderChange = {},
            getNotificationChange = {},
            getIntervalChange = {},
            getWakeUpHourChange = { _, _ -> },
            getBedHourChange = { _, _ -> },
            getSoundChange = {},
            onCustomSoundPicked = {},
            getHeightChange = {},
            getWeightChange = {},
            healthProviderName = provider,
            healthSyncEnabled = false,
            onHealthSyncChange = onHealth,
        )
    }

    @Test
    fun healthToggleAppearsAndReportsChanges() = runComposeUiTest {
        var requested: Boolean? = null
        setContent { settings("Apple Health") { requested = it }() }
        onNodeWithContentDescription("Save drinks to Apple Health").performScrollTo().assertIsOff().performClick()
        assertEquals(true, requested)
    }

    @Test
    fun healthSectionIsHiddenWithoutAHealthApp() = runComposeUiTest {
        setContent { settings(null)() }
        onNodeWithText("Save drinks to Health").assertDoesNotExist()
        onNodeWithText("Report a Bug").assertExists()
    }
}
