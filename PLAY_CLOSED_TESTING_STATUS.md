# Google Play Closed Testing — Verified Handoff
Updated 14 October 2026.

## App / tester rollout
- App: **Imam Raza School Mission**
- Android package: `org.imamraza.schoolmission`
- Version code 4; version name `0.3.0-internal`
- Play Console: https://play.google.com/console/u/0/developers/5583249955126155149/app/4973072581418014938/tracks/4700434500703611814
- Track: **Closed testing — Alpha**
- Target region: Pakistan, saved.
- Tester configuration step: marked complete by Play Console; selected email lists should be reviewed before sending for review.
- Release: **draft**. Not submitted to Google for review, not published, not installable through Play yet.
- Billing remains disabled in Supabase; no real sticker payments can occur.

## The initial AAB upload failed; certificate mismatch was resolved locally
Play Console expected upload-signing SHA-1:
`EA:30:F5:AA:AC:BF:47:04:EE:CB:67:5A:2E:20:F0:6A:EA:A6:3C:30`.

The original school-upload and newly generated closed-test keys were not the correct registered key. The pre-existing `imam-raza-play-upload-2026.jks` **does match** the Play certificate. The Mac's script `scripts/sign_play_bundle_original_key.sh` checks the registered fingerprint, builds the release and verifies that its JAR signature is correct.

**VERIFIED:** `./gradlew :app:bundleRelease :app:testDebugUnitTest` succeeded using the registered Play key. `keytool -printcert -jarfile` returned the expected SHA-1.

Correct signed AAB (local Mac):
`~/Documents/Imam_Raza_a.a_school/app/build/outputs/bundle/release/app-release.aab`.

Keystore:
`~/.android/keystores/imam-raza-play-upload-2026.jks` (never commit or publish).

Password stored privately in macOS Keychain. Never paste or transmit signing credentials. Arrange an encrypted private backup of both the keystore and recovery instructions. **Do not regenerate another upload key for this app.**

## Play Store assets completed
- Listing text: `PLAY_STORE_LISTING.md`, already entered in default English (US) store listing.
- 512×512 app icon: `play-store-assets/play_icon_512.png`.
- 1024×500 feature graphic: `play-store-assets/feature_graphic_1024x500.png`.
- Seven 1080×1920 real Pixel screenshots: `play-store-assets/phone_screenshots/`.
- Play default listing previously displayed existing icon, feature graphic and 7 screenshots (7/8 slots used). Google Play indicated **Ready to send for review**, *not* approved.
- Listing imagery contains education, books, growth motifs. No holy figures, shrines or sacred depictions.
- App description states that the school is proposed, not operational; sticker purchases are disabled.

## Actions to complete in Play Console
1. Open the Alpha release's **Edit release** page using the authenticated owner Chrome profile.
2. Remove the rejected AAB row from the draft, if it remains attached.
3. Upload the **corrected AAB** from the path above; verify its version code 4 and ensure Google displays **no upload key mismatch**.
4. Review tester email lists and opt-in settings; confirm intended adult testers only. The app should remain restricted to testers.
5. Confirm release name and release notes; use the description in `PLAY_STORE_LISTING.md`.
6. Complete truthful app content declarations (Data safety, account access, ads, age target, content rating, financial features, government association), public support details and any required privacy information.
7. Preview the closed-test release; send **only the closed-test release** for Google's review once all checklist gates pass. Never start a production rollout.
8. Wait for Google approval, then verify an invited tester can install through Play. Only afterward create the three actual one-time products and run license-tester billing/entitlement/refund tests.

**Current external blocker:** Play Console browser profile requires Google account sign-in as of the latest check, so the replacement AAB was not submitted from the current browser session.

## Legal/financial gate
No charitable registration or school-fundraising authorization has been confirmed. A paid sticker sale and public claims that proceeds benefit a future school require proper merchant identity, legal advice, transparent accounting, Play merchant/product setup, and a live billing sandbox test before enabling commerce. None of those approvals should be inferred from a signed AAB or closed-test draft.
