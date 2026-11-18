package com.falcon.hydrohabit.tracking

/**
 * Mirrors logged drinks into the platform's health store (Apple Health on iOS).
 * Platforms without one use [NoHealthSync], and the app hides the setting there.
 */
interface HealthSync {
    /** True when this device has a health store the app can write to. */
    val isAvailable: Boolean

    /** Shown in Settings, e.g. "Apple Health". */
    val providerName: String

    /** Asks for permission to save water. Returns true if saving is allowed. */
    suspend fun requestAccess(): Boolean

    /** Saves a drink and returns the health store's ID for it, or null if it failed. */
    suspend fun saveWater(amountMl: Int, epochMillis: Long): String?

    /** Deletes a drink previously saved with [saveWater]. */
    suspend fun deleteWater(healthId: String)

    /** Opens the health app, where the user can change Droply's access. */
    fun openHealthApp() {}
}

object NoHealthSync : HealthSync {
    override val isAvailable = false
    override val providerName = ""
    override suspend fun requestAccess() = false
    override suspend fun saveWater(amountMl: Int, epochMillis: Long): String? = null
    override suspend fun deleteWater(healthId: String) = Unit
}
