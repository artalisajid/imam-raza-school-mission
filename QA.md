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

## Subsequent work — 14 October 2026, v0.2.1-dev
- Implemented finance-admin-only `reconcile-play-voids` Edge Function, with Google Play API token and 29-day reconciliation window. Deployed; cannot test real refunds without Google Play publisher credentials.
- Implemented authenticated Pub/Sub push endpoint `play-rtdn`: verifies Google OIDC signature, issuer, audience and configured service-account identity; fail-closed until configuration. Deployed but no live push subscription linked.
- Clearing local downloaded sticker ZIP cache on supporter sign-out or successful account deletion.
- Opened Google Cloud and Play Console websites on the connected Mac. Google Cloud presented an account sign-in page; no accessible authorized developer account. No login information was supplied or collected.
- Google Cloud, Supabase Studio browser, and Play Console authentication/merchant setup remain external blockers. The Supabase connector works independently and the database remains deployed.
- Both live flags (Google login and commerce) MUST remain disabled until legal and sandbox acceptance gates are satisfied.
- See `GOOGLE_ACCOUNT_SETUP.md` for exact account and authorized redirect configuration.

## Google Play API validation — 14 October 2026
- Cross-checked server purchase payload with official Google Android Publisher ProductPurchaseV2 reference.
- Enforced quantity == 1 using `productLineItem[0].productOfferDetails.quantity` (correct nested field); deployed verify-play-purchase v2 to Supabase.
- Security Advisor: no ERROR/WARN findings; INFO for four deliberately private tables.
- Live configuration flags remain false; real checkout and Google OAuth still require signed-in Google Cloud / Play developer console and account approval.
- Checkout architecture remains exclusively native Google Play Billing; no external checkout.

## 14 October 2026 — v0.3.0 internal Google OAuth acceptance
- Google Cloud OAuth Web client created for the dedicated `imam-raza-school-mission` project, callback `https://zvtvpldmapcelkizrxtg.supabase.co/auth/v1/callback`.
- Google provider enabled in Supabase Studio; callback and `org.imamraza.schoolmission://login-callback` Android deep link allowed.
- Google Auth Platform audience: External / Testing. One authorized adult test account added (personal email redacted here).
- Supabase operator flag: `google_signin_enabled=true`; `commerce_enabled=false`.
- Pixel 7 Pro: debug build versionCode 4 / v0.3.0-internal installed. Google sign-in selected an allowed test Google account, consent returned to native app, and Account screen displayed **Signed in** with Sign out. No fatal crash in checked log window.
- Supabase verification: `auth.users=1`, `profiles=1`, `purchases=0`, `entitlements=0`.
- Build and 4 unit tests passed. Supabase Google endpoint returned HTTP 302 to Google sign-in.
- Play Console: the app exists under Artistic Sajid, but **one-time product setup is blocked until a billing-enabled signed Play upload**.
- Release AAB generated locally (11 MB), but jarsigner reports `jar is unsigned`. Do **not** upload this artifact as a Play release until secured signing has been completed and verified.
- Signing key was prepared outside the Git repo. Its password must not be exposed in chat/GitHub. Sign the bundle through a locally secured build environment, verify the signed AAB with jarsigner, then upload to Play's Internal testing track.
- Outstanding release gating: signed Play AAB / uploaded billing-enabled version, three one-time Play products/prices, Play publisher API service account, sandbox purchase/refund QA, legal fundraising/merchant clearance, public Data Safety and support disclosures.
