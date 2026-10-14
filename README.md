# Imam Raza (a.s.) School Mission

A native Android application for adult supporters of a **proposed** affordable Shia school in Karachi (Nursery–Class 7). The project is starting from zero and has **not** been represented as an operating school, registered charity, or active fundraising entity.

## Status

**Development preview; not yet a production release.** Home, School, Support, Journey and Account UI are implemented in Jetpack Compose. Legal and payment features are intentionally disabled until required authorizations and external configuration exist.

### Priority experience
Home → **Explore Stickers & Support** → Sticker Collections → Product Details → [Google Play purchases after setup] → Owned Stickers → Verified Journey.

### Tech stack
- Android: Kotlin, Jetpack Compose, Material 3, target SDK 36.
- Backend: Supabase Auth/Postgres/Storage/Edge Functions (migration prepared, hosted project not connected).
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
1. Authenticate Supabase CLI and create a project under the correct organization.
2. Run `supabase link --project-ref YOUR_PROJECT_REF` then `supabase db push`; review the SQL before applying.
3. Add the following to untracked `local.properties`:
```properties
supabase.url=https://YOUR_PROJECT.supabase.co
supabase.publishable_key=YOUR_PUBLIC_PUBLISHABLE_KEY
```
4. Configure Google OAuth with a dedicated Android/client configuration and redirect allowlist. Build a secure account-deletion flow before enabling accounts.
5. Build and deploy verified purchasing only after legal, Play, and merchant readiness. Never embed service credentials in the Android app.

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

## Release blockers
- Supabase hosted project and authorization.
- Verified organizational/merchant eligibility and any relevant fundraising permissions.
- Play Console registered in-app products and developer API service account.
- Google OAuth configuration, account deletion and secure support channel.
- Real sticker asset packs + proper license/fulfillment (current UI uses previews).
- Real device QA, Play Billing sandbox, full accessibility, Play Console privacy and Data Safety.

See `ARCHITECTURE.md` and `QA.md`.
