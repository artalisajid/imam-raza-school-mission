# Imam Raza (a.s.) School Mission

A native Android application for adult supporters of a **proposed** affordable Shia school in Karachi (Nursery–Class 7). The project is starting from zero and has **not** been represented as an operating school, registered charity, or active fundraising entity.

## Status

**Development preview; not yet a production release.** Home, School, Support, Journey and Account UI are implemented in Jetpack Compose. Legal and payment features are intentionally disabled until required authorizations and external configuration exist.

### Priority experience
Home → **Explore Stickers & Support** → Sticker Collections → Product Details → **Google Play Billing in-app purchase only** (after account and legal approvals) → Verified Owned Stickers → Verified Journey.

### Mandatory checkout policy
**The sole monetary checkout in the Android app is Google Play Billing for genuine digital sticker collections.** The app must not redirect a purchase to the website, bank transfer, Stripe, PayPal, WhatsApp, or any other payment processor. Google Play provides the live local price and displays its native purchase sheet; the secure Supabase verification function grants ownership only after Google's purchase confirmation. The public GitHub Pages website hosts privacy, account deletion, and mission information, and must not collect payments. There is no direct-donation checkout. A published funding mission does not waive legal, merchant or Google Play review requirements.

### Tech stack
- Android: Kotlin, Jetpack Compose, Material 3, target SDK 36.
- Backend: Supabase Auth/Postgres/Storage/Edge Functions (project created and read-only public API connected; OAuth and payments not enabled).
- Payments: Google Play Billing (production integration gated by Play merchant approval, purchase verification and legal review).
- Public website / privacy: GitHub Pages in `docs/`.

## Local build (macOS)
Requires JDK 17, Android SDK platform 36, Gradle wrapper.
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
./gradlew :app:assembleDebug
```
App ID: `org.imamraza.schoolmission`. The debug APK will be in `app/build/outputs/apk/debug/`.

## Connect Supabase
1. Existing project: zvtvpldmapcelkizrxtg (Fatima Corp, ap-south-1). Public API configured in ignored local.properties.
2. Initial SQL migration has been applied to the hosted database; for future migrations use the authenticated Supabase project and review SQL before applying.
3. Add the following to untracked `local.properties`:
```properties
supabase.url=https://YOUR_PROJECT.supabase.co
supabase.publishable_key=YOUR_PUBLIC_PUBLISHABLE_KEY
```
4. Configure Google Cloud OAuth credentials and Supabase redirect allowlist. The Android PKCE OAuth client and account deletion Edge Function are implemented but awaiting account configuration and live-user tests.
5. Protected Play purchase verification, private sticker delivery, RTDN and finance-admin voided-purchase reconciliation are deployed but cannot process actual Play orders without Play merchant/API setup and legal approval. Never embed service credentials in the Android app.

## School scope
- Karachi; Nursery, KG, Preparatory, Classes 1–7.
- Shia knowledge, Quranic knowledge, world knowledge.
- Proposed monthly tuition: standard Rs. 3,000; subsidized Rs. 1,500; full scholarship Rs. 0.
- Uniform: black; girls fully cover their hair, with humane age-appropriate implementation.
- No shrine or sacred figure imagery in app and stickers.
- First milestone: organizational registration; target and funds remain unverified until documented.

## Compliance rules
Do not process charity donations without the required legal and payment permissions. Permanent sticker packs are genuine one-time digital purchases, not repeat donation tokens. Publish only independently reconciled financial records. No children's personal records are stored in this supporter application.

## Privacy policy
- https://artalisajid.github.io/imam-raza-school-mission/privacy.html
- https://artalisajid.github.io/imam-raza-school-mission/deletion.html

## Release blockers and status
- Supabase project and migrations: **complete**, Fatima Corp / ap-south-1. Login and commerce both OFF.
- Original art: **62 sticker PNGs complete**, three licensed ZIP archives stored in private Supabase Storage (not in GitHub).
- Android app build: **passed**; smoke tested on a physical Pixel 7 Pro. Version 0.2.1-dev is installed.
- Google OAuth: native browser/PKCE client implemented, but browser Google account sign-in and Google Cloud OAuth credentials missing.
- Google Play: store client and protected backend implemented, but Play Console developer/merchant/products and approved service account absent; no real purchase tested.
- Refunds: secured RTDN and finance reconciliation functions deployed, but Google Pub/Sub subscription, credentials and sandbox testing pending.
- Legal fundraising/charity registration and payment claims: authorization or counsel review outstanding; not a software task.
- Additional tests: end-to-end login/billing/refund, accessibility, finance audits and real release signing.

See `ARCHITECTURE.md`, `GOOGLE_ACCOUNT_SETUP.md`, `LAUNCH_GATES.md` and `QA.md`.


### Google Play closed-testing handoff (14 October 2026)
The Play Console app and **Closed testing – Alpha** track exist. Pakistan is selected; a closed-test release draft (version 4 / 0.3.0-internal) exists. The native app has been tested with Google sign-in on Pixel 7 Pro. The English Store listing, icon, feature graphic and genuine Pixel screenshots are prepared; Google Play previously indicated the listing was ready to send for review.

The signed AAB in `app/build/outputs/bundle/release/app-release.aab` has been rebuilt using the **exact Play-registered original upload certificate** (SHA-1 `EA:30:F5:AA:AC:BF:47:04:EE:CB:67:5A:2E:20:F0:6A:EA:A6:3C:30`). It passed Gradle and signature verification. This corrected AAB has **not yet been uploaded**: Google Play Console currently requests a fresh owner login. The earlier rejected AAB was signed with a different key. See `PLAY_CLOSED_TESTING_STATUS.md` for precise next steps, `play-store-assets/` for the artwork, and `release-assets/` for the ZIP. No public production rollout or live payments have been enabled.
