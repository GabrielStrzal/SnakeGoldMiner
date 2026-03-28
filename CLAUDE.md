# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Snake Gold Miner is a libGDX snake game where the player controls a mining truck collecting gold. It targets desktop (LWJGL3) and HTML (GWT) platforms.

## Build & Run Commands

```bash
# Run on desktop
./gradlew desktop:run

# Build desktop JAR
./gradlew desktop:dist

# Run HTML in superdev (hot-reload) mode
./gradlew html:superDev

# Build HTML for distribution
./gradlew html:dist
```

There are no automated tests in this project.

## Architecture

### Module Structure
- **`core/`** — all game logic and screens; shared across platforms
- **`desktop/`** — desktop launcher (`DesktopLauncher.java`), depends on core
- **`html/`** — GWT-based web launcher (`HtmlLauncher.java`), depends on core

### Entry Point & Lifecycle
`SnakeGoldMiner` (extends `BasicGame` from `gdxUtilLib`) is the main application class. On `create()` it:
1. Initializes the `ScreenManager` singleton
2. Shows a `LoadingScreen` that preloads assets defined in `LoadingPathsImpl`
3. Transitions to `MenuScreen` once loading completes

### Screen System
Screens are created via the `ScreenEnum` factory and switched through `ScreenManager.getInstance().showScreen(ScreenEnum.X, ...)`. The enum variants are:
- `LOADING_SCREEN` — asset preloader, shown once at startup
- `MENU_SCREEN` / `MenuScreen` — main menu with Play, Game Stats, Trophies buttons
- `GAME_SCREEN` / `GameScreen` — core snake gameplay
- `TEXT_SCREEN` / `TextScreen` — reusable info screen driven by `GameModeEnum` (GAME_STATS, TROPHIES, STORY_MODE, CLASSIC_MODE, LEVEL_COMPLETED, etc.)

`BasicMenuScreen` is the base class for all UI screens — it sets up a `Stage`, `Viewport`, `Skin`, and `AssetManager` reference. `GameScreen` extends `ScreenAdapter` directly and manages its own camera/batch.

### Key Classes
| Class | Purpose |
|---|---|
| `GameConfig` | Screen dimensions (640x480), version string, debug flag |
| `ImagesPaths` | All asset path constants |
| `GameTexts` | TypingLabel-formatted strings for the TextScreen (supports `{VAR=...}` substitution) |
| `GamePositions` | UI element position constants |
| `GameStatsHandler` | Persists high score and total plays via libGDX `Preferences` (key: `snake_gold_miner_v1_0`) |
| `LevelStats` | Value object returned by `GameStatsHandler.getSavedData()` |
| `Hud` | Overlay drawn on top of `GameScreen` using its own Stage |
| `LoadingPathsImpl` | Lists all textures/sounds to preload before gameplay |

### Asset Loading Pattern
Assets used in menu screens are preloaded via `LoadingPathsImpl` and accessed through `game.getAssetManager()`. Assets used only in `GameScreen` (truck, gold, cart textures) are loaded directly with `new Texture(...)` in `show()` and should be disposed in `hide()`/`dispose()`.

### Custom Library (`gdxUtilLib`)
The project depends on a local Maven snapshot `com.strzal:gdxUtilLib:1.0-SNAPSHOT` that provides:
- `BasicGame` — base `Game` class with `AssetManager` and `GameStatsHandler` access
- `ScreenManager` — singleton screen switcher
- `ScreenEnumInterface` — interface implemented by `ScreenEnum`
- `LoadingScreen` / `LoadingPaths` — generic asset preloading screen
- `GdxUtils.clearScreen()` — clears the screen each frame

### HTML / GWT Considerations
- `GdxDefinition.gwt.xml` and `SnakeGoldMiner.gwt.xml` must list any new source packages for GWT compilation
- The `regexodus` and `typing-label` dependencies require `:sources` artifacts for HTML
- `GameConfig.SCREEN_HTML_DISPLAY_WIDTH/HEIGHT` controls the canvas size in `HtmlLauncher`
