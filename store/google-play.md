# Google Play — Submission Package (Droply)

All fields validated against current Play Console limits. Note: Play titles are limited to **30 characters** (Google reduced this from 50 in late 2021).

## Store Listing

| Field | Value |
|---|---|
| App name (title) | `Droply: Water Tracker Reminder` (30/30) |
| Application ID | `com.falcon.hydrohabit` (`app` module) — confirm this is the release artifact vs `composeApp`'s `com.falcon.hydrohabit.composeapp` |
| Category | Health & Fitness |
| Tags | Health, Lifestyle |
| Price | Free (no in-app products) |
| Contains ads | No |
| Target SDK | 36 · Min SDK 26 (Android 8.0+) |
| Version | versionCode 1 · versionName 1.0 |

## Short Description (80 chars)

```text
Drink more water with smart reminders, personal goals, streaks & daily tracking.
```

## Full Description

Play indexes keywords from this text — primary phrases ("water tracker", "water reminder", "drink water", "water intake", "hydration") are woven in naturally, no stuffing.

```text
Drink more water, feel better every day. Droply is a beautifully simple water tracker and drink water reminder that turns hydration into a habit — with a daily goal calculated for your body, smart reminders that respect your sleep, and streaks that keep you motivated.

A WATER GOAL MADE FOR YOU
Answer three quick questions — height, weight, and activity level — and Droply calculates your personalized daily water intake goal. No generic 8-glasses rule: your body, your goal.

WATER REMINDERS THAT FIT YOUR DAY
• Reminder interval you control: every 30 minutes, 1, 2, 3, or 4 hours
• Set wake-up and bedtime — no reminders while you sleep
• 6 calming notification sounds (Droplet, Ripple, Stream, Cascade, Splash) or pick your own
• Switch off anytime

TRACK WATER IN ONE TAP
Log 50–500 ml with quick-add buttons and watch the animated water level rise toward your daily goal. Hit 100% and enjoy a delightful celebration animation.

STREAKS & ACHIEVEMENTS
Meet your goal every day to build your streak and unlock 8 badges — from First Drop (day 1) all the way to 365-Day Champion. Hydration tracking that feels like a game, not a chore.

YOUR MONTH AT A GLANCE
The calendar report shows every successful day, your average water intake, and your best streak — so you can watch your hydration habit grow.

SIMPLE BY DESIGN
• Clean, minimal interface with light & dark mode
• Kilograms or pounds — your choice
• Your height, weight, and intake data stay on your device
• No account or sign-up required

COMPLETELY FREE
Droply is 100% free. No subscription, no ads, no locked features.

Whether you want a daily water tracker, a drink water reminder, or a simple way to build a healthy hydration habit — Droply keeps it effortless.

Download Droply and start your streak today.
```

## Data Safety Form

No accounts; health inputs stay on-device. Firebase Analytics + Crashlytics require declaring:

| Question | Answer |
|---|---|
| Does your app collect or share user data? | Collects: yes. Shares: no |
| App interactions (Analytics events) | Collected, not shared, not linked to identity |
| Crash logs | Collected, not shared |
| Diagnostics | Collected, not shared |
| Device or other IDs (Firebase installation ID) | Collected, not shared |
| Data encrypted in transit | Yes |
| Data deletion mechanism | No account; on-device data removed on uninstall. Provide a contact email for telemetry-deletion requests |

## Content Rating & Audience

- IARC questionnaire → expect **Everyone / PEGI 3** (no violence, no user content, no data sharing).
- Target audience: **13+** (avoids Families-policy obligations; the app is not child-directed).
- Health apps declaration: Play Console asks whether the app is a "health app" — it is (hydration). No medical-device claims are made in the listing (keep it that way).

## Policy Risks Found in the Manifest (all fixed in code)

1. ~~`USE_EXACT_ALARM`~~ — **removed** from both module manifests. Play policy restricts it to alarm-clock/calendar apps. The app now uses `SCHEDULE_EXACT_ALARM` (user-grantable "Alarms & reminders" access, requested after notification permission) and falls back to inexact allow-while-idle alarms when not granted — reminders always fire either way.
2. ~~`SYSTEM_ALERT_WINDOW`~~ — **removed** from the `app` module manifest (was unused).
3. `POST_NOTIFICATIONS` + `SCHEDULE_EXACT_ALARM` + `RECEIVE_BOOT_COMPLETED` are fine (boot receiver reschedules reminders after restart — standard, unrestricted).

## Visual Assets

| Asset | Spec |
|---|---|
| App icon | 512×512 px, 32-bit PNG |
| Feature graphic | 1024×500 px — **required** |
| Phone screenshots | 2–8 (use 6), 1080×2400 recommended, 16:9–9:16 |
| 7"/10" tablet screenshots | Optional, needed for tablet-optimized badge |

Use the same 6 screenshots + captions as the Apple package (see `apple-app-store.md`).

## Release Plan

1. Internal testing track → verify install, notifications, reboot-rescheduling on a physical device.
2. Closed testing (optional; Play requires 12 testers/14 days only for *personal* dev accounts — check your account type).
3. Production with staged rollout (20% → 50% → 100%).
