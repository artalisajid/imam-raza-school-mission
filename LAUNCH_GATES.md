# Launch Gates, Setup, and Legal Clearance — Imam Raza (a.s.) School Mission
Updated 2026-10-14. **This is a development handoff, not legal approval or Google Play approval.**

## A. Google sign-in: implemented client, external setup outstanding
- Google Auth Platform: https://console.cloud.google.com/auth/overview
- Create a Google Cloud project owned by the app operator and configure the OAuth consent screen (appropriate adult audience, required openid/email/profile scopes).
- Create a **Web application OAuth client** and add Google redirect URI:
  `https://zvtvpldmapcelkizrxtg.supabase.co/auth/v1/callback`
- Configure client ID/secret directly in Supabase Authentication → Providers → Google:
  https://supabase.com/dashboard/project/zvtvpldmapcelkizrxtg/auth/providers
- Supabase Authentication → URL Configuration → Redirect allow list:
  `org.imamraza.schoolmission://login-callback`
- Android client uses system-browser Google OAuth with PKCE, a short-lived authorization verifier and Android Keystore-encrypted local session. Refresh tokens remain encrypted on device.
- After a real test succeeds and the privacy policy is reviewed, use an administrator-approved SQL update:
  `update public.app_flags set google_signin_enabled=true where singleton=true;`
- Current status: OFF. No credentials have been configured. Never paste OAuth secrets in GitHub.

## B. Play purchases: client and server verification implemented; commerce remains OFF
- Play Console: https://play.google.com/console/
- App ID must be exactly `org.imamraza.schoolmission`; production must be properly signed and distributed via Play.
- Configure three **one-time non-consumable** product IDs:
  `little_learners`, `seeds_of_knowledge`, `bright_minds`.
- Google Play Billing client queries actual regional prices, uses a hashed logged-in Supabase user ID to bind the transaction, and only attempts a purchase after all gates pass.
- Backend Edge Function `verify-play-purchase` checks the Supabase JWT, live flags, Google Android Publisher purchases.productsv2, product, single quantity, order, account binding and purchased state. It writes an idempotent verified purchase and entitlement through a service-role-only Postgres function, then acknowledges with Google.
- Connect Play Console to Google Cloud, enable Android Publisher API, and grant the least privileged account rights needed for purchase verification and acknowledgements.
- Configure the service-account credential as **Supabase Edge Function secret**, never in a phone app or repository:
  `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`
- Upload full, unique, genuinely usable sticker assets to private `paid-sticker-packs` bucket at `PRODUCT_ID/pack.zip`. Only verified owners should be able to download.
- Configure Real-time Developer Notifications / Voided Purchases API and test refund revocations: the protected `revoke_verified_google_purchase` database operation exists, but event delivery is **not yet wired**.
- Testing required: purchased/pending/canceled/refunded, restore after reinstall, duplicate callbacks, repurchase, network failure, account mismatch, price display and account deletion.
- Do not enable checkout until legal and Play Console compliance is reviewed and every digital collection is deliverable.
- Final controlled activation: publish products in DB and set `commerce_enabled=true` only after all tests pass. This is NOT done now.

## C. Personal data and compliance
- Public project information requires no account.
- Google OAuth collects provider user ID and email for sign-in; purchase data includes Google order reference, token SHA-256 (not raw tokens), purchase state and entitlements.
- No card details, CVV, children or religious affiliation data should be requested.
- Account deletion via a JWT-protected Supabase Edge Function removes the account and entitlements, detaches retained transaction records from the account and calls Supabase Auth admin deletion. Account deletion path is a live server route but requires real-user testing.
- Privacy: https://artalisajid.github.io/imam-raza-school-mission/privacy.html
- Deletion: https://artalisajid.github.io/imam-raza-school-mission/deletion.html
- Review Google Play Data Safety and privacy text for actual live processing before switching OAuth and payments on.

## D. Legal/payment approval: not obtainable through software
- Sindh Charities Registration and Regulation Act, 2019: https://charitycommission.sindh.gov.pk/system/files/Sindh%20Act%20No.XVI%20of%202019.pdf
- Sindh Charity Commission: https://charitycommission.sindh.gov.pk/
- The school is presently a proposed project, not a registered operating school or charity.
- Before advertising sticker proceeds as a school fund or accepting charitable collections, obtain **written, project-specific local legal advice** on whether commercial product sales promoted for the mission create charitable-collection or promoter-authorization obligations. Clarify taxes, registration and money custody.
- Determine lawful operator identity (individual business vs eligible registered charitable structure) and tax reporting. Open appropriate segregated banking and implement fund allocation/financial reconciliation.
- Google Play payments policy: https://support.google.com/googleplay/android-developer/answer/9858738
- Google Play generally uses Play Billing for digital products; qualifying tax-exempt donations follow different rules and require eligibility. Sticker purchases must remain real digital-product purchases, not disguised donations.
- Legal requirements, registration certificates and merchant approval **cannot be fabricated** or marked complete by engineering.

## E. Pixel QA: blocked by device not being connected
On the Pixel:
1. Settings → About Phone → tap Build number seven times to enable Developer options.
2. Settings → System → Developer options → enable USB debugging.
3. Connect via a data-capable USB cable to the connected Mac; approve the phone's fingerprint authorization.
4. On the Mac run `/opt/homebrew/share/android-commandlinetools/platform-tools/adb devices -l`.
5. When listed, install the pre-release APK using `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
6. Exercise onboarding, five tabs, sticker preview, Google OAuth redirect, account deletion, large text, TalkBack and offline behavior.
7. Play Billing sandbox requires Play Console installation/licensed tester, not just a locally installed debug APK.

## Current enforced live safety
`google_signin_enabled=false` and `commerce_enabled=false` in Supabase.
No live payment collection; no invented school funds, invoices, tax status or authorization.
