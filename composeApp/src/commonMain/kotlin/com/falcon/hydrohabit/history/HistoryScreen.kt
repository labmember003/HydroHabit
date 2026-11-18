package com.falcon.hydrohabit.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.model.IntakeLog
import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.features.history.usecase.HistoryStats
import com.falcon.hydrohabit.tracking.HydrationTracker
import com.falcon.hydrohabit.tracking.displayName
import com.falcon.hydrohabit.tracking.formatClock
import com.falcon.hydrohabit.tracking.formatMl
import com.falcon.hydrohabit.tracking.groupThousands
import com.falcon.hydrohabit.tracking.longLabel
import com.falcon.hydrohabit.tracking.shortName
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

private val secondaryText = Color(0xFF8E8E93)
private val hairline = Color(0xFFE5E5EA)

/** Collects the intake log and wires delete + Undo through [tracker]. */
@Composable
fun HistoryScreenHost(
    intakeLogRepo: IntakeLogRepository,
    tracker: HydrationTracker,
    currentGoalMl: Int,
    onShowMessage: (message: String, actionLabel: String?, onAction: (() -> Unit)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val log by intakeLogRepo.log.collectAsState(initial = IntakeLog())
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    HistoryScreen(
        log = log,
        today = today,
        currentGoalMl = currentGoalMl,
        onDeleteEntry = { entry ->
            scope.launch {
                val removed = tracker.remove(entry.id) ?: return@launch
                onShowMessage("Deleted ${formatMl(removed.entry.amountMl)}", "Undo") {
                    scope.launch { tracker.restore(removed.entry) }
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
fun HistoryScreen(
    log: IntakeLog,
    today: LocalDate,
    currentGoalMl: Int,
    onDeleteEntry: (IntakeEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    var monthStart by remember { mutableStateOf(LocalDate(today.year, today.monthNumber, 1)) }
    var selectedDay by remember { mutableStateOf(today) }
    val isCurrentMonth = monthStart.year == today.year && monthStart.monthNumber == today.monthNumber

    val summary = HistoryStats.monthSummary(log, monthStart.year, monthStart.monthNumber, today, currentGoalMl)
    val currentStreak = HistoryStats.currentStreak(log, today, currentGoalMl)
    val bestStreak = HistoryStats.bestStreak(log, currentGoalMl)
    val totals = HistoryStats.dailyTotals(log)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor2)
            .testTag("history"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "History",
                style = TextStyle(fontSize = 28.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 4.dp),
            )
        }
        item {
            MonthHeader(
                label = "${monthStart.month.displayName()} ${monthStart.year}",
                canGoForward = !isCurrentMonth,
                onPrevious = { monthStart = monthStart.minus(1, DateTimeUnit.MONTH) },
                onNext = { monthStart = monthStart.plus(1, DateTimeUnit.MONTH) },
            )
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = "Goal met",
                        // Months before the user's first day have nothing to count.
                        value = if (summary.daysElapsed == 0) "–" else "${summary.daysGoalMet} of ${summary.daysElapsed}",
                        unit = when (summary.daysElapsed) { 0 -> ""; 1 -> "day"; else -> "days" },
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Daily average",
                        value = if (summary.daysLogged == 0) "–" else groupThousands(summary.averageMl),
                        unit = if (summary.daysLogged == 0) "" else "ml",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = "Current streak",
                        value = "$currentStreak",
                        unit = if (currentStreak == 1) "day" else "days",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Best streak",
                        value = "$bestStreak",
                        unit = if (bestStreak == 1) "day" else "days",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            Card {
                MonthCalendar(
                    monthStart = monthStart,
                    today = today,
                    selectedDay = selectedDay,
                    progressFor = { day ->
                        HistoryStats.progressPercent(totals[day] ?: 0, HistoryStats.goalOn(log, day, currentGoalMl))
                    },
                    onSelect = { selectedDay = it },
                )
                Spacer(Modifier.height(14.dp))
                Legend()
            }
        }
        item {
            Card {
                SectionTitle("Last 7 days")
                Spacer(Modifier.height(12.dp))
                WeekChart(
                    days = HistoryStats.lastDays(log, today, 7),
                    goalFor = { HistoryStats.goalOn(log, it, currentGoalMl) },
                    today = today,
                )
            }
        }
        item {
            DayDetail(
                day = selectedDay,
                today = today,
                totalMl = totals[selectedDay] ?: 0,
                goalMl = HistoryStats.goalOn(log, selectedDay, currentGoalMl),
                entries = HistoryStats.entriesOn(log, selectedDay),
                onDeleteEntry = onDeleteEntry,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor1)
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(fontSize = 17.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack),
    )
}

@Composable
private fun MonthHeader(label: String, canGoForward: Boolean, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = waterColor)
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f).testTag("monthLabel"),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 17.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack),
        )
        IconButton(onClick = onNext, enabled = canGoForward) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = if (canGoForward) waterColor else hairline,
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor1)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Text(label, style = TextStyle(fontSize = 13.sp, fontFamily = fontFamily, color = secondaryText), maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                maxLines = 1,
                style = TextStyle(fontSize = 22.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack),
            )
            if (unit.isNotEmpty()) {
                Spacer(Modifier.width(4.dp))
                Text(
                    unit,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 3.dp),
                    style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, color = secondaryText),
                )
            }
        }
    }
}

/** 0 = nothing logged, 1 = under half, 2 = half or more, 3 = goal met. */
private fun level(percent: Int): Int = when {
    percent <= 0 -> 0
    percent < 50 -> 1
    percent < 100 -> 2
    else -> 3
}

private fun levelColor(level: Int): Color = when (level) {
    1 -> waterColor.copy(alpha = 0.18f)
    2 -> waterColor.copy(alpha = 0.45f)
    3 -> waterColor
    else -> Color.Transparent
}

@Composable
private fun MonthCalendar(
    monthStart: LocalDate,
    today: LocalDate,
    selectedDay: LocalDate,
    progressFor: (LocalDate) -> Int,
    onSelect: (LocalDate) -> Unit,
) {
    val daysInMonth = HistoryStats.daysInMonth(monthStart.year, monthStart.monthNumber)
    // Sunday-first grid: Sunday = 0 ... Saturday = 6.
    val leadingBlanks = monthStart.dayOfWeek.isoDayNumber % 7
    val cells: List<LocalDate?> = List(leadingBlanks) { null } + (1..daysInMonth).map { LocalDate(monthStart.year, monthStart.monthNumber, it) }

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach {
            Text(
                it,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily, color = secondaryText),
            )
        }
    }
    Spacer(Modifier.height(6.dp))
    cells.chunked(7).forEach { week ->
        Row(modifier = Modifier.fillMaxWidth()) {
            (0 until 7).forEach { i ->
                val day = week.getOrNull(i)
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                    if (day != null) {
                        val future = day > today
                        val lvl = if (future) 0 else level(progressFor(day))
                        val selected = day == selectedDay
                        val percent = progressFor(day)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(levelColor(lvl))
                                .then(
                                    when {
                                        selected -> Modifier.border(2.dp, primaryBlack, CircleShape)
                                        day == today -> Modifier.border(1.5.dp, waterColor, CircleShape)
                                        else -> Modifier
                                    }
                                )
                                .clickable(enabled = !future) { onSelect(day) }
                                .semantics {
                                    contentDescription = "${day.longLabel()}, " +
                                        if (future) "upcoming" else "$percent percent of goal"
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = if (day == today) fontFamilyBold else fontFamily,
                                    fontWeight = if (day == today) FontWeight(600) else FontWeight(400),
                                    color = when {
                                        future -> Color(0xFFC7C7CC)
                                        lvl == 3 -> Color.White
                                        else -> primaryBlack
                                    },
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(0 to "None", 1 to "Under 50%", 2 to "50–99%", 3 to "Goal met").forEach { (lvl, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(levelColor(lvl))
                        .then(if (lvl == 0) Modifier.border(1.dp, hairline, CircleShape) else Modifier),
                )
                Spacer(Modifier.width(5.dp))
                Text(label, style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily, color = secondaryText), maxLines = 1)
            }
        }
    }
}

@Composable
private fun WeekChart(days: List<Pair<LocalDate, Int>>, goalFor: (LocalDate) -> Int, today: LocalDate) {
    val goals = days.map { goalFor(it.first) }
    val maxValue = maxOf(days.maxOfOrNull { it.second } ?: 0, goals.maxOrNull() ?: 0, 1)
    val referenceGoal = goals.lastOrNull() ?: 0
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Last 7 days: " + days.joinToString { (d, ml) -> "${d.dayOfWeek.shortName()} ${formatMl(ml)}" }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val slot = size.width / days.size
            val barWidth = slot * 0.5f
            days.forEachIndexed { i, (day, ml) ->
                val h = size.height * (ml.toFloat() / maxValue)
                val met = goals[i] > 0 && ml >= goals[i]
                drawRoundRect(
                    color = if (met) waterColor else waterColor.copy(alpha = if (ml > 0) 0.4f else 0.08f),
                    topLeft = Offset(slot * i + (slot - barWidth) / 2, size.height - h.coerceAtLeast(4f)),
                    size = Size(barWidth, h.coerceAtLeast(4f)),
                    cornerRadius = CornerRadius(6f, 6f),
                )
            }
            if (referenceGoal > 0) {
                val y = size.height * (1f - referenceGoal.toFloat() / maxValue)
                drawLine(
                    color = primaryBlack.copy(alpha = 0.35f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                )
            }
        }
    }
    Spacer(Modifier.height(6.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { (day, _) ->
            Text(
                text = if (day == today) "Today" else day.dayOfWeek.shortName(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = fontFamily,
                    color = if (day == today) waterColor else secondaryText,
                ),
            )
        }
    }
    if (referenceGoal > 0) {
        Spacer(Modifier.height(8.dp))
        Text(
            "Dashed line: daily goal (${formatMl(referenceGoal)})",
            style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily, color = secondaryText),
        )
    }
}

@Composable
private fun DayDetail(
    day: LocalDate,
    today: LocalDate,
    totalMl: Int,
    goalMl: Int,
    entries: List<IntakeEntry>,
    onDeleteEntry: (IntakeEntry) -> Unit,
) {
    Card {
        SectionTitle(if (day == today) "Today" else day.longLabel())
        Spacer(Modifier.height(4.dp))
        val percent = HistoryStats.progressPercent(totalMl, goalMl)
        Text(
            text = if (goalMl > 0) "${formatMl(totalMl)} of ${formatMl(goalMl)} · $percent%" else formatMl(totalMl),
            modifier = Modifier.testTag("dayTotal"),
            style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, color = secondaryText),
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(waterColor.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((percent.coerceIn(0, 100)) / 100f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(waterColor),
            )
        }
        Spacer(Modifier.height(8.dp))
        if (entries.isEmpty()) {
            Text(
                text = if (day == today) "No drinks logged yet today. Tap Add on the Home tab." else "No drinks logged on this day.",
                modifier = Modifier.padding(vertical = 10.dp),
                style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, color = secondaryText),
            )
        } else {
            entries.forEachIndexed { index, entry ->
                if (index > 0) Box(Modifier.fillMaxWidth().height(0.5.dp).background(hairline))
                EntryRow(entry, onDelete = { onDeleteEntry(entry) })
            }
        }
    }
}

@Composable
private fun EntryRow(entry: IntakeEntry, onDelete: () -> Unit) {
    val time = Instant.fromEpochMilliseconds(entry.epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val clock = formatClock(time.hour, time.minute)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            clock,
            modifier = Modifier.weight(1f),
            style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, color = primaryBlack),
        )
        Text(
            formatMl(entry.amountMl),
            style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, color = primaryBlack),
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete ${formatMl(entry.amountMl)} logged at $clock",
                tint = secondaryText,
            )
        }
    }
}
