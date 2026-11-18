import AppIntents
import ComposeApp

/// 2150 -> "2,150 ml", using the user's locale.
private func ml(_ value: Int32) -> String { "\(Int(value).formatted()) ml" }
private func ml(_ value: Int) -> String { "\(value.formatted()) ml" }

/// "Log water in Droply" — adds a drink without opening the app (Siri, Shortcuts, Spotlight, Action button).
@available(iOS 16.0, *)
struct LogWaterIntent: AppIntent {
    static var title: LocalizedStringResource = "Log Water"
    static var description = IntentDescription("Adds a drink to today's water in Droply.")

    @Parameter(title: "Amount (ml)", default: 250, inclusiveRange: (1, 5000))
    var amount: Int

    static var parameterSummary: some ParameterSummary {
        Summary("Log \(\.$amount) ml of water")
    }

    func perform() async throws -> some IntentResult & ProvidesDialog {
        let result = try await ShortcutBridge.shared.logWater(amountMl: Int32(amount))
        guard result.isSetUp else {
            return .result(dialog: "Open Droply once to set your daily goal, then try again.")
        }
        let message: String
        if result.reachedGoal {
            message = "Logged \(ml(amount)). You've reached today's goal of \(ml(result.goalMl))!"
        } else if result.totalMl >= result.goalMl {
            message = "Logged \(ml(amount)). That's \(ml(result.totalMl)) today, past your \(ml(result.goalMl)) goal."
        } else {
            message = "Logged \(ml(amount)). \(ml(result.totalMl)) of \(ml(result.goalMl)) today, \(ml(result.goalMl - result.totalMl)) to go."
        }
        return .result(dialog: IntentDialog(stringLiteral: message))
    }
}

/// "How much water have I had in Droply?"
@available(iOS 16.0, *)
struct TodayWaterIntent: AppIntent {
    static var title: LocalizedStringResource = "Today's Water"
    static var description = IntentDescription("Tells you how much water you've logged today in Droply.")

    func perform() async throws -> some IntentResult & ProvidesDialog {
        let result = try await ShortcutBridge.shared.today()
        guard result.isSetUp else {
            return .result(dialog: "Open Droply once to set your daily goal first.")
        }
        let message: String
        if result.totalMl >= result.goalMl {
            message = "You've had \(ml(result.totalMl)) today and reached your \(ml(result.goalMl)) goal."
        } else {
            message = "You've had \(ml(result.totalMl)) of \(ml(result.goalMl)) today. \(ml(result.goalMl - result.totalMl)) to go."
        }
        return .result(dialog: IntentDialog(stringLiteral: message))
    }
}

@available(iOS 16.0, *)
struct DroplyShortcuts: AppShortcutsProvider {
    static var appShortcuts: [AppShortcut] {
        AppShortcut(
            intent: LogWaterIntent(),
            phrases: [
                "Log water in \(.applicationName)",
                "Add water in \(.applicationName)",
                "Log a glass of water in \(.applicationName)",
            ],
            shortTitle: "Log Water",
            systemImageName: "drop.fill"
        )
        AppShortcut(
            intent: TodayWaterIntent(),
            phrases: [
                "How much water have I had in \(.applicationName)",
                "Check my water in \(.applicationName)",
            ],
            shortTitle: "Today's Water",
            systemImageName: "chart.bar.fill"
        )
    }
}
