# Imam Raza (a.s.) School Mission

A native Android application for adult supporters of a **proposed** affordable Shia school in Karachi (Nursery–Class 7). The project is starting from zero and has **not** been represented as an operating school, registered charity, or active fundraising entity.

## Status

**Development preview; not yet a production release.** Home, School, Support, Journey and Account UI are implemented in Jetpack Compose. Legal and payment features are intentionally disabled until required authorizations and external configuration exist.

### Priority experience
Home → **Explore Stickers & Support** → Sticker Collections → Product Details → [Google Play purchases after setup] → Owned Stickers → Verified Journey.

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
