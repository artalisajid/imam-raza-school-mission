# Google Cloud, Play Console, Supabase OAuth — Operator Setup
Updated 14 October 2026 — Imam Raza (a.s.) School Mission

## Current browser state
Google Cloud redirected to a **Google account login**. Play Console redirects to Google's generic Play-for-business landing page / console, without authenticated developer account access. Supabase Studio also requested browser login, despite the separately connected Supabase app. No Google OAuth or developer credentials were created. The operator must log into the owned Google accounts directly; **never share passwords, MFA codes or service account JSON through chat or GitHub**.

## Account URLs (open in the Chrome profile on the connected Mac)
- Google Cloud: https://console.cloud.google.com/
- Google Auth Platform: https://console.cloud.google.com/auth/overview
- OAuth Clients: https://console.cloud.google.com/auth/clients
- Play Console: https://play.google.com/console/u/0/
- Supabase Google provider: https://supabase.com/dashboard/project/zvtvpldmapcelkizrxtg/auth/providers
- Supabase URL redirects: https://supabase.com/dashboard/project/zvtvpldmapcelkizrxtg/auth/url-configuration
- Supabase Functions: https://supabase.com/dashboard/project/zvtvpldmapcelkizrxtg/functions

## Google sign-in configuration
Create or select an appropriate Google Cloud project owned by the app operator.
Configure the Google consent screen's application name, domain and the exact published privacy policy:
https://artalisajid.github.io/imam-raza-school-mission/privacy.html
Scopes must be no broader than openid, email and profile.
Create a Web application OAuth client with this authorized callback:
https://zvtvpldmapcelkizrxtg.supabase.co/auth/v1/callback
Enter the new client ID and **client secret directly in Supabase**, not in code.
Add this exact Supabase allowed redirect (must match AndroidManifest):
org.imamraza.schoolmission://login-callback
Use a real Google tester on the Pixel 7 Pro to validate: browser launch, account selection, return URI, Supabase session, refresh, sign-out, deletion and profile-data privacy.
Only after successful testing, an authorized project administrator may enable `google_signin_enabled` in `public.app_flags`. It remains **false** today.

## Google Play developer and product setup
Account owner verifies legal identity, country, payments profile and banking/merchant requirements in Play Console.
App ID/package: `org.imamraza.schoolmission`. Do not create an app with a different package.
Current Android is a **debug APK**, not a signed release AAB. Create a protected release upload key and proper Play App Signing setup; do not publish debug keystores.
Configure one-time, non-consumable Play products exactly as:
- `little_learners` (12 original PNG stickers)
- `seeds_of_knowledge` (20 original PNG stickers)
- `bright_minds` (30 original PNG stickers)
Prices come from Google Play regional localized catalog, not constants in Android code. The product records in Supabase remain unpublished until complete.

Configure a Cloud service account authorized in Play Console (developer API application permissions), enable Android Publisher API, and set the service account JSON **only** in Supabase Edge Function secrets under:
`GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`
Do not put this key in local.properties, APK, GitHub or public Storage.

## Pub/Sub real-time purchase notifications
Configure a Google Cloud Pub/Sub topic, enable notifications under Play Console settings and create a **push subscription with OIDC authentication**.
Endpoint: https://zvtvpldmapcelkizrxtg.supabase.co/functions/v1/play-rtdn
Give Pub/Sub service-agent the required service-account-token-creator permission, and set subscription authentication to a narrowly scoped service account.
Set in Supabase secret manager:
- `PLAY_PUBSUB_PUSH_AUDIENCE` = **exact** audience configured for push subscription (prefer endpoint URL).
- `PLAY_PUBSUB_PUSH_EMAIL` = exact authenticated push service-account email.
- `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` = Play-approved publisher account JSON.
The deployed `play-rtdn` function cryptographically verifies Google's signed token, issuer, audience and service-account email before processing messages. It fails closed while secrets are absent.
The authenticated finance-admin `reconcile-play-voids` function queries Google's Voided Purchases API over the latest 29 days and revokes matching entitlements. It also requires the Play service account, and a verified finance admin membership. Schedule recurring reconciliation only after successful sandbox verification and appropriate secrets setup.

## Required final tests and operating gates
- Google login using the actual tester, account switch and OAuth cancellation.
- Internal testing and licensed Google Play testers, purchase/pending/canceled/refunded/voided/chargeback cases.
- Idempotent backend verification (duplicate token; wrong user; wrong product; API outages).
- Entitlement and ZIP delivery only to purchase owner.
- Account deletion, lawful retained accounting records, and privacy retention.
- Google Play declaration, audience, Data Safety, support contact, app signing, and release review.
- Written legal clearance on the public sale proceeds/school-fundraising wording and the proper financial/operator structure under Pakistani/Sindh requirements.
- Approved financial allocation and settlement reconciliation before publishing raised funds.
- An external reviewer must approve the release. Flip `commerce_enabled=true` only after everything above; it is still **false**.

**No official approvals have been granted by creating this checklist.**
