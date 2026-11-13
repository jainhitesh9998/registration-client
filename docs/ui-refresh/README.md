# Registration Client UI refresh

A modern, consistent visual system for the JavaFX registration client, plus the fixes needed to run the
Java 21 client on macOS (Apple Silicon) for development. Based on `develop` (Java 21, 1.3.0-SNAPSHOT).

Screens keep their structure (same sections, same controls, same flows); what changed is the visual
system and spacing.

## Design system

Everything is driven from tokens declared on `.root` in
[`application.css`](../../registration/registration-client/src/main/resources/application.css), so the
theme can be changed in one place.

| Area | Before | After |
| --- | --- | --- |
| Colour | ~45 hard-coded colours: MOSIP blue plus 3 teals, 4 cyans, pure red/green, black borders | One palette: brand blue `#005BAA`, deep navy chrome, slate neutrals, semantic success/danger/warning |
| Font | `Roboto` declared 45 times but never shipped, so each OS fell back to its own system font | Roboto Regular/Medium/Bold bundled (`resources/fonts`, Apache-2.0) |
| Page surface | Mix of white pages and grey pages | One light canvas (`#F2F5F9`) with white cards (10-12px radius, soft shadow) |
| Buttons | Different sizes per screen; hover inverted to dark blue | Primary (filled) / secondary (outlined) / compact, 40px and 32px heights, soft-tint hover, proper disabled state |
| Inputs | Underline-only fields, 18px label gap, combo values rendered as solid blue blocks | Outlined 40px inputs and dropdowns, 6px label gap, blue focus, red error state |
| Tabs | Fixed 250px grey tabs that overflowed the registration flow | Content-width underline tabs; every step is visible |
| Home | Tiles touching each other, mixed heights, misaligned headings, column divider | 80px tiles with a 12px gap, a single icon column, headings aligned to the cards |
| Tables | Black row rules, blue header rule | Subtle row dividers, muted header, blank placeholder rows |
| Dialogs, menus, popups | Grey Modena defaults, heavy shadows | Rounded, soft-elevation surfaces |
| Startup window | Unstyled Modena window | Branded card on a gradient |
| Login | Fixed-position card with a large empty gap | Card laid out as a vertical flow (the language picker collapses when unused), 16px rhythm |

## Code changes

- `application.css`: token layer, component system and per-screen rules. Layout-defining sizes are
  preserved unless noted below.
- `fonts/`: bundled Roboto (Apache-2.0 licence included).
- `RegistrationLogin.fxml`: each login card's controls now sit in a `VBox` flow inside the existing
  `AnchorPane`s. Same `fx:id`s and handlers; `LoginController` is unchanged.
- `RegistrationOfficerPacketLayout.fxml` and `PacketHandlerController`: one `homeTile` style, fixed
  80px tile rows with a 12px gap, a fixed 72px icon column, aligned headings, smaller top band.
- `TextFieldFxControl` / `DropDownFxControl`: label-to-input spacing 18px to 6px.
- `BiometricFxControl` / `BiometricsController`: 10px spacing between modality tiles.
- `ClientPreLoader`: the startup window uses the theme.

## macOS / Apple Silicon fix

`RegistrationAppHealthCheckUtil` used OSHI 3.6 only to check free disk space, and its lookup only
matched Windows drive letters (`user.dir.substring(0, 3)`). OSHI pulled in JNA 4.5.1, which has no
`darwin-aarch64` native library, so the Spring context failed to start on Apple Silicon. The check now
uses `java.nio.file.Files.getFileStore`, and OSHI is removed from `registration-services`.

## Running locally on macOS (dev)

```bash
cd registration
mvn clean install -Dgpg.skip -DskipTests -Dmaven.javadoc.skip=true
```

The Windows `run.bat` assumes a JRE that bundles JavaFX (Zulu FX). On a standard JDK 21, put the
`*-mac-aarch64` JavaFX jars on the module path:

```bash
java --module-path <javafx-mac-aarch64-jars> \
     --add-modules=javafx.controls,javafx.fxml,javafx.swing,javafx.web,javafx.media \
     --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     -cp "lib/*" io.mosip.registration.controller.Initialization
```

Also add `registration-api-stub-impl` to `lib/`; production adds the geo-position and scanner
implementations during packaging.

`Initialization` (in `_launcher.jar`) checks for a signed `MANIFEST.MF` / `lib/MANIFEST.MF` before it
starts the client. Production packaging (`configure.sh`) creates those files, but a plain dev build has
none, so the launcher stops at that check. For local UI work, call
`io.mosip.registration.launcher.NormalStartup.launch(args)` from a small dev main instead; it is the same
Java 21 start path the launcher uses after verification.

The screenshots were taken from this build with offline mock data: the official MOSIP 1.2.0 sample ID
schema, UI specs (`newProcess`, `updateProcess`, `lostProcess`, `bioCorrectionProcess`) and master data
from `mosip/mosip-data`. A dev-only harness, kept outside this repo, seeded that data and drove the UI.

## Notes

- **Dashboard** and the **preview/acknowledgement** pages are server-synced HTML templates, so their
  inner styling (bright tiles, pink borders) comes from the template data, not from client CSS.
- `application-pink.css` (the alternate theme) is untouched.

## Screenshots

| | |
| --- | --- |
| ![](screenshots/01-startup-preloader.png) Startup | ![](screenshots/02-login.png) Login |
| ![](screenshots/03-login-password.png) Login: password | ![](screenshots/04-home.png) Home |
| ![](screenshots/05-new-registration-consent.png) New registration: consent | ![](screenshots/06-new-registration-demographics.png) Demographics |
| ![](screenshots/07-new-registration-documents.png) Documents | ![](screenshots/08-document-scan-popup.png) Document scan |
| ![](screenshots/09-new-registration-biometrics.png) Biometrics | ![](screenshots/10-new-registration-preview.png) Preview |
| ![](screenshots/11-operator-authentication.png) Operator authentication | ![](screenshots/12-acknowledgement.png) Acknowledgement |
| ![](screenshots/13-pending-approval.png) Pending approval | ![](screenshots/14-reject-reason-popup.png) Reject reason |
| ![](screenshots/15-supervisor-authentication.png) Supervisor authentication | ![](screenshots/16-re-registration.png) Re-registration |
| ![](screenshots/17-application-upload.png) Application upload | ![](screenshots/18-dashboard.png) Dashboard |
| ![](screenshots/19-update-uin.png) Update UIN | ![](screenshots/20-update-uin-demographics.png) Update UIN: demographics |
| ![](screenshots/21-lost-uin.png) Lost UIN | ![](screenshots/22-biometric-correction.png) Biometric correction |
| ![](screenshots/23-settings.png) Settings | ![](screenshots/24-settings-scheduled-jobs.png) Scheduled jobs |
| ![](screenshots/25-settings-global-config.png) Global config | ![](screenshots/26-settings-devices.png) Devices |
| ![](screenshots/27-operator-onboarding.png) Operator onboarding | |

### Before (for reference)

| | |
| --- | --- |
| ![](before/01-startup-preloader.png) Startup | ![](before/02-login.png) Login |
