# Registration Client UI refresh (UX4G foundations)

A redesign of the JavaFX registration client on the foundations of **UX4G 3.0**, the Government of
India design system ([ux4g.gov.in](https://ux4g.gov.in); token values taken from the MIT-licensed
`ux4g-web-components` package). It also includes the fix needed to run the Java 21 client on macOS
(Apple Silicon). Based on `develop` (Java 21, 1.3.0-SNAPSHOT).

**Contract and upgrade safety:** layouts were rebuilt where needed, but every `fx:id`, `onAction`/handler
name, controller field type and public method is unchanged. The work is in FXML and CSS plus a few
isolated UI-construction tweaks, so later MOSIP upgrades merge cleanly. The only nodes removed are two
decorative separator lines in the app bar and the static, unpopulated "News and Updates" placeholder on
Home. Nothing in the code referenced either.

## Design system

All visual values are tokens on `.root` in
[`application.css`](../../registration/registration-client/src/main/resources/application.css).

| Foundation | UX4G value used |
| --- | --- |
| Font | Noto Sans Regular / Medium / SemiBold / Bold, bundled (`resources/fonts`, SIL OFL 1.1) |
| Brand | Primary 600 `#4A2BC2`; hover 700 `#3D239F`; pressed 800 `#301C7D`; tint 50 `#F2EFFF` |
| Chrome | Primary 900 `#24145C` app bar |
| Neutrals | Text `#171717` / `#404040` / `#737373`; borders `#D9D9D9` / `#E5E5E5`; canvas `#F5F5F5` |
| Status | Success `#128937`, error `#DB372D`, warning `#D46B08`, info `#08979C`, each with a soft tint |
| Spacing | 4px grid (8 / 12 / 16 / 24 / 32); 32px page margins, 24px gutters |
| Radius | 8 for controls, 12 for cards, 999 for chips |
| Elevation | Soft key + ambient shadows (levels 1-2) |
| Controls | 40px inputs and buttons; uniform 360px inputs and dropdowns; 96px date parts; fixed-width chips |

## Screens redesigned

| Screen | What changed |
| --- | --- |
| App bar (`Header.fxml`) | One 56px flow: logo, home, and pill chips for operator / centre / machine on the left; status, settings and menu on the right |
| Home (`RegistrationOfficerPacketLayout.fxml`) | Registration tasks first, as a row of four action cards; operational and end-of-day tasks as two even columns of list cards |
| Registration flow (`GenericRegistrationLayout.fxml`) | Page title above the steps, the steps in one full-width card with underline tabs, an action bar with the status message and primary action |
| Form fields (code-built) | Labels above inputs (6px gap); hidden validation and transliteration helpers no longer reserve space; chip groups labelled like other fields; 24/16px gaps |
| Pending approval | Breadcrumb, title and Approve/Reject header; list and preview as matching cards; action bar with guidance and Authenticate |
| Login | Card laid out as a vertical flow; the language picker collapses when unused |
| Everything else | Picks up the tokens: tables, dialogs, menus, settings, biometrics, onboarding, upload, re-registration |

### Code touched (UI construction only)

- `PacketHandlerController`: process cards in a 4-column grid.
- `GenericController`: form margins and gaps.
- `ButtonFxControl`: label above the chips.
- `FxControl.collapseWhenHidden` (new helper), used by `TextFieldFxControl` and `DropDownFxControl`.
- `DOB*FxControl`: `dobFieldGroup` style hook.
- `BiometricFxControl` / `BiometricsController`: tile spacing.
- `ClientPreLoader`: themed startup window.

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
  inner styling (bright tiles, pink borders, serif text) comes from the template data, not from client CSS.
- **Pre-existing on `develop`, not changed here:** `UpdateUINController` wires a hover listener to
  `backBtn`, but `UpdateUIN.fxml` has no such element. The NPE is caught and logged, and the screen still
  works.
- **Verification:** the full flow was replayed on macOS with offline mock data after every change. That
  covers login, home, new registration (consent, demographics, documents, scan, biometrics, preview,
  operator auth, packet created, acknowledgement), approval with supervisor auth, re-registration, upload,
  dashboard, update UIN, lost UIN, biometric correction, settings and onboarding.
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

### Before (original `develop`, for reference)

| | |
| --- | --- |
| ![](before/01-startup-preloader.png) Startup | ![](before/02-login.png) Login |
