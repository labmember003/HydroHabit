package com.falcon.hydrohabit.features.onboarding.usecase

/**
 * Gender, used only to pick the hydration baseline.
 *
 * [UNSPECIFIED] is a first-class choice, not a fallback — users who skip the
 * question get the midpoint of the male and female baselines.
 */
enum class Gender {
    MALE,
    FEMALE,
    UNSPECIFIED;

    companion object {
        fun fromName(name: String): Gender =
            entries.firstOrNull { it.name == name } ?: UNSPECIFIED
    }
}
