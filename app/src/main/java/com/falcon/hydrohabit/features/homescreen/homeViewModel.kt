package com.falcon.hydrohabit.features.homescreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.hydrohabit.R
import com.falcon.hydrohabit.alarmSchedular.AlarmScheduler
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.features.homescreen.usecase.getGreeting
import com.falcon.hydrohabit.features.homescreen.usecase.getStreakMessage
import com.falcon.hydrohabit.features.homescreen.usecase.calculateWaterPercent
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class HomeViewModel(private val onboardingRepo: OnboardingRepositoryContract, context: Context) : ViewModel() {

    var usedWaterAmount by mutableIntStateOf(0)
        private set

    var totalWaterAmount by mutableIntStateOf(0)
        private set

    var rewardDialog by mutableStateOf(false)
        private set

    var streakDays =  mutableStateListOf<Int>()
        private set
    var waterPercent by mutableIntStateOf(0)
        private set
    var _streak by mutableStateOf(StreakClass())
        private set
    var _waterAmount by mutableStateOf(WaterAmount())
        private set
    var perks = mutableStateListOf<Int>()
        private set
    var streakScore by mutableIntStateOf(0)
        private set
    var streakDay by mutableStateOf("")
        private set
    var waterTime by mutableStateOf(24)
        private set
    var onProgress by mutableStateOf("")
        private set
    var onTime by mutableStateOf("")
        private set

    fun DismissReward(reward: Boolean) {
        rewardDialog = reward
    }

    // Gets water amount, Gets streak score, Gets compliments for filling water,
    // Updates the water amount at end of the day

    init {

        viewModelScope.launch {
            getWaterAmount(context)
        }

        println("streakScore Onboarding totalWaterAmount $totalWaterAmount")
        viewModelScope.launch {
            getStreakScore()

        }

        waterStreak()
//        updateWaterAmountOnDayEnd()


    }


    // updates the total water amount from onboarding to Homescreen
    fun updateTotalWaterAmount(totalWater:Int){
        totalWaterAmount = totalWater
    }

    // Gets greetings according to the time of day
    fun getGreeting() {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        onTime = getGreeting(timeOfDay)
    }

    // Updates the perks in perk sheet
    fun updatePerks(){
        println("Streak Score $streakScore")
        when {
            streakScore>=1 -> {
                perks.add(R.drawable.day1)
            }
            streakScore>=7 -> {
                perks.add(R.drawable.day3)
            }
            streakScore>=14 -> {
                perks.add(R.drawable.day4)
            }
            streakScore>=30 -> {
                perks.add(R.drawable.day2)
            }
        }
    }



    // Updates the water amount and streak scores
    fun fillWaterUpdate(waterUpdate: Int) {

        val calendar = Calendar.getInstance()
        val currentTime = LocalTime.now()
        val date: LocalDate = LocalDate.now()
        println("currentTime $date")
        println("streakScore Onboarding date.dayOfMonth ${calendar.get(Calendar.DAY_OF_MONTH)}")

        println("WATER PERCENT: ${waterPercent}")
        if (waterPercent < 100) {
                println("REWARD: Water Percent less than 100")

                waterTime = currentTime.minute
                usedWaterAmount += waterUpdate
                waterPercent = calculateWaterPercent(usedWaterAmount, totalWaterAmount)

            } else if (waterPercent>=100) {
                rewardDialog = true
                println("REWARD: Water Percent 100")
                if (date.toString() != streakDay) {
                    streakScore++
                    streakDays.addAll(streakDays)
                    streakDays.add(calendar.get(Calendar.DAY_OF_MONTH) -1)
                    updatePerks()
                    viewModelScope.launch {
                        calculateStreakScore()
                    }
                }
                streakDay = date.toString()
            }else{
                println("REWARD: Water Percent equal to 100")
                waterPercent = 100
            }

            viewModelScope.launch {
                calculateWaterAmount()
            }
            println("streakScore Onboarding streakScore $streakScore")
            println("streakScore Onboarding waterTime $waterTime")
            println("streakScore Onboarding waterTime $streakDays")
            println("streakScore Onboarding currentTime hour ${currentTime.hour}")
            println("streakScore Onboarding streakDay $streakDay")
            println("streakScore Onboarding waterAmount $usedWaterAmount")
            println("streakScore Onboarding rewardDialog $rewardDialog")
            println("streakScore Onboarding streakDay $streakDay")
        waterStreak()

    }

    // Update the water Amount in database
    private suspend fun calculateWaterAmount() {
        val date = LocalDate.now()
        onboardingRepo.updateWaterAmount(
            onUsedWater = usedWaterAmount,
            onTotalWaterAmount = totalWaterAmount,
            onWaterDay = date.toString()
        )
        println("Date onWaterDay ${date.toString()}")

    }

    // Update the streak Scores in database
    private suspend fun calculateStreakScore() {
        onboardingRepo.updateStreak(
            streak = streakScore,
            streakDay = streakDay,
            streakDays = streakDays,
            waterTime = waterTime.toString(),
            perks =perks
        )
        println("streakScore Onboarding calculateStreakScore ")

    }

    // Gets the water amount filled in the glacier from the database
    // Tries to compare todays date versus previous water date. If the dates are different, the water amount becomes zero
    // It also calculates and updates the water percentage
    private suspend fun getWaterAmount(context: Context) {
        val date = LocalDate.now()
        onboardingRepo.getWaterAmount().collect {
            println("Get Water Amount Home Viewmodel Function ${it}")
            _waterAmount = it
            usedWaterAmount = it.onUsedWater
            totalWaterAmount = it.onTotalWater
            if(it.onUsedWater>it.onTotalWater){
                createWaterReminders(context =context, it.onTotalWater.toString())
            }else{
                createWaterReminders(context =context, it.onUsedWater.toString())
            }

            if (it.onUsedWater > 0) {
                waterPercent = calculateWaterPercent(it.onUsedWater, it.onTotalWater)
            }
            if(date.toString()!=it.onWaterDay){
                usedWaterAmount = 0
                waterPercent =0
                calculateWaterAmount()
            }
        }
    }

    private fun createWaterReminders(context: Context, waterReminderMessage:String){
        val alarmScheduler = AlarmScheduler(context)
        val reminder: WaterReminder =
            WaterReminder(
                time = Clock.System.now().plus(2, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()),
                message = "Your water intake for today is ${waterReminderMessage} ml"
            )
        reminder.let {
            alarmScheduler.schedule(reminder)
        }
    }

    // Gets the streak score and updates perks in perk sheet

    private suspend fun getStreakScore() {
        onboardingRepo.getStreak().collect { it ->
            _streak =it
            streakScore = it.streak
            if(it.perks.isNotEmpty()){
                perks.clear()
                println("perks ${it.perks}")

                it.perks.forEach{
                    if(!perks.contains(it)){
                        perks.add(it)
                    }
                }
               /*perks.addAll(it.perks)*/
            }
            if (it.waterTime != "") {
                waterTime = it.waterTime.toInt()
            }
            streakDay = it.streakDay
            if(it.streakDays.isNotEmpty()){
                streakDays.addAll(it.streakDays)
            }
            println("getStreakScore ${it}")
        }
    }

    // Gives compliments according to percentage of water filled in the glacier
    fun waterStreak() {
        onProgress = getStreakMessage(waterPercent)
    }
}