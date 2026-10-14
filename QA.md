# QA — Android v0.2.0-dev

**Verified 14 October 2026, Asia/Karachi.**

## Build and static tests
- `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`: PASS.
- 4 out of 4 unit tests: PASS.
- Android Lint: zero errors. Non-blocking warnings remain.
- Target SDK 36, app ID `org.imamraza.schoolmission`, versionCode 2, versionName `0.2.0-dev`.

## Supabase production boundary checks
- Feature flags (anonymous GET): HTTP 200; `commerce_enabled=false`, `google_signin_enabled=false`.
- Anonymous purchase records: no records visible.
- Private financial ledger: HTTP 401.
- `verify-play-purchase` without JWT: HTTP 401.
- `delete-account` without JWT: HTTP 401.
- All three private pack downloads without JWT: HTTP 400 (not available).
- 3 private ZIP files uploaded: `little_learners` (12 PNG), `seeds_of_knowledge` (20 PNG), `bright_minds` (30 PNG).
- Temporary content upload endpoint disabled; one-time credential destroyed.
- Supabase Security Advisor: no warning/error findings; four informational notices for tables intentionally hidden from clients.

## Physical device smoke test
**Phone:** Pixel 7 Pro (`cheetah`), Android 17 reported by `adb`, connected wirelessly.

- Installed version 0.2.0-dev, versionCode 2: PASS.
- Launches MainActivity: PASS.
- Home displays school mission and primary Support CTA: PASS.
- Support tab displays sticker catalog: PASS.
- Little Learners product detail opens and shows 12 items: PASS.
- School tab displays Nursery–Class 7 educational information: PASS.
- Journey tab displays legal registration as first milestone with unverified funds state: PASS.
- Account tab displays guest supporter, Google-login-disabled state and privacy entry: PASS.
- MainActivity remains resumed and no FATAL EXCEPTION found in checked logcat window: PASS.

**Not covered:** Google sign-in end to end, purchase sandbox, refunds, accessibility with TalkBack, large-text/RTL layout, download as an authenticated purchaser, deletion as an authenticated user, exhaustive navigation, performance profiling or release signing.

## External release blockers
1. Create or connect an owned Google Cloud project; configure Google OAuth web client, secret and redirect.
2. Register/verify Play Console operator, app and one-time products; link Android Publisher API permissions and secret.
3. Configure Real-time Developer Notifications/voided purchase reconciliation, perform Play purchase/chargeback tests.
4. Obtain project-specific Pakistani legal advice and applicable organizational/fundraising/payment authorizations.
5. Establish public support contact and update Privacy Policy/Data Safety to match enabled features.
6. End-to-end QA with a real logged-in test supporter and licensed Play testing environment.

**Both feature flags remain OFF. No checkout or login is active and no charitable funds are collected.**
