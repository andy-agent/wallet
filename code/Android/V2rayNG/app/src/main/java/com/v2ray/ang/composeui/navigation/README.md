# Compose Navigation Assets

This directory retains the `vpnui/navigation` source-only assets inside the Android app module.

- `Routes.kt` holds the lightweight route constants used by the migrated page skeleton.
- `AppNavGraph.kt` and `NavGraph.kt` provide compileable placeholder graph wiring.
- `NavigationManager.kt`, `BackStackManager.kt`, and `DeepLinkHandler.kt` preserve the navigation support structure for later bridge tasks.

The real runtime integration belongs to `liaojiang-4j0.11`, `liaojiang-4j0.12`, and `liaojiang-4j0.13`.
