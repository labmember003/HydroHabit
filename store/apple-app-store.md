# Apple App Store — Submission Package (Droply)

All fields validated against App Store Connect limits. Paste directly into App Store Connect.

## App Information

| Field | Value |
|---|---|
| App Name (title) | `Droply: Water Tracker Reminder` (30/30) |
| Subtitle | `Daily Hydration Habit Builder` (29/30) |
| Bundle ID | `com.falcon.hydrohabit.composeapp` (already registered in project) |
| Primary Category | Health & Fitness |
| Secondary Category | Lifestyle |
| Age Rating | 4+ (no objectionable content) |
| Price | Free (no in-app purchases) |
| Version | 1.0 (build 2) |

## Promotional Text (editable without app update)

```text
Build a hydration habit that sticks. Smart reminders from wake-up to bedtime, streaks and badges that keep you going — and a daily goal tailored to your body. Free.
```

## Keywords Field (100 chars, comma-separated, no spaces)

```text
drink,intake,log,goal,streak,h2o,health,wellness,fluid,thirst,aqua,alarm,cup,bottle,ml,oz,diet,sip
```

Notes: deliberately excludes `water`, `tracker`, `reminder`, `daily`, `hydration`, `habit` — those are already indexed from the title and subtitle (Apple combines them; repeating wastes characters).

## Description

```text
Meet Droply — the simple, beautiful way to drink more water every day.

Most of us don't drink enough. Droply fixes that with a daily goal calculated for your body, gentle reminders that respect your sleep schedule, and streaks that turn hydration into a habit you'll actually keep.

A GOAL MADE FOR YOUR BODY
Answer three quick questions — height, weight, and activity level — and Droply calculates your personal daily water goal. No guesswork, no one-size-fits-all targets.

REMINDERS THAT RESPECT YOUR DAY
• Choose your rhythm: every 30 minutes up to every 4 hours
• Set wake-up and bedtime — Droply stays silent while you sleep
• Six calming sounds (Droplet, Ripple, Stream, Cascade, Splash) or use your own
• Switch off anytime

LOG WATER IN ONE TAP
Quick-add buttons for 50–500 ml make tracking effortless. Watch the animated water level rise toward your goal — and enjoy a little celebration when you reach 100%.

BUILD A STREAK, EARN BADGES
Hit your goal daily to grow your streak and unlock 8 achievements, from First Drop on day one to 365-Day Champion. Small wins, every single day.

SEE YOUR PROGRESS
A monthly calendar shows every day you hit your goal, alongside your average intake and best streak.

SIMPLE BY DESIGN
• Clean, minimal interface with light & dark mode
• Kilograms or pounds — your choice
• Height, weight, and intake data stay on your device
• No account, no sign-up

COMPLETELY FREE
No subscription. No ads. No paywalls. Just water.

Start today — your future self will thank you.
```

## What's New (v1.0)

```text
Welcome to Droply 1.0 — your new hydration companion.

• Personalized daily water goal from your height, weight & activity level
• Smart reminders between wake-up and bedtime (every 30 min to 4 h)
• Six notification sounds, or bring your own
• One-tap logging from 50–500 ml
• Streaks with 8 unlockable achievement badges
• Monthly calendar report with average intake & best streak
• Light & dark mode — and completely free
```

## App Privacy (nutrition labels)

The app has no accounts and stores health inputs (height, weight, intake) on-device only. Firebase Analytics + Crashlytics are integrated, so declare:

| Data type | Collected? | Linked to identity? | Used for tracking? |
|---|---|---|---|
| Usage Data → Product Interaction | Yes (Firebase Analytics) | No | No |
| Diagnostics → Crash Data | Yes (Crashlytics) | No | No |
| Diagnostics → Performance Data | Yes (Crashlytics) | No | No |
| Health & Fitness | **No** — never leaves device | — | — |

No ad SDKs → no App Tracking Transparency prompt needed. Verify the current Firebase data-disclosure guidance before submitting (Firebase docs list exact label mappings per SDK version).

## URLs (required before submission)

- Privacy Policy URL: **TODO — blocker.** Required because Firebase collects usage/diagnostic data. A GitHub Pages page is sufficient.
- Support URL: **TODO — blocker.** Can be the repo, a landing page, or a mailto-backed page.
- Marketing URL: optional.

## Visual Assets

| Asset | Spec |
|---|---|
| App icon | 1024×1024 px, PNG, no alpha, no rounded corners |
| iPhone screenshots | 6.9" display set (1320×2868 portrait) — required; Apple scales for smaller devices |
| iPad screenshots | Not required — app is iPhone-only (`TARGETED_DEVICE_FAMILY = 1` in project.pbxproj) |
| App preview video | Optional; 15–30 s, portrait |

Suggested screenshot order + captions (first 2–3 matter most):
1. Home progress — "Hit your daily water goal"
2. Quick add sheet — "Log water in one tap"
3. Reminder settings — "Reminders that fit your day"
4. Achievements sheet — "Build a streak. Earn badges."
5. Calendar heatmap — "Your month at a glance"
6. Onboarding result — "A goal made for your body"

## App Review Notes (paste into "Notes" for the review team)

```text
Droply is a free water-intake tracker. No account or login is required — the reviewer can use every feature immediately after onboarding (enter any height/weight, pick an activity level). Notifications are a core feature: reminders fire between the configured wake/bed times at the chosen interval. All health inputs stay on-device; Firebase Analytics/Crashlytics provide anonymous usage and crash telemetry only. No in-app purchases, no ads, no third-party login.
```
