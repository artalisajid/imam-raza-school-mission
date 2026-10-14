# Google Cloud and Google Play setup status — 14 October 2026

## Google Cloud — CREATED
- Signed-in owner account: user's **Sajid** Chrome profile (account identity and recovery details remain with owner).
- Dedicated GCP project display name: **IMAM RAZA SCHOOL MISSION**.
- Google Cloud project ID: `imam-raza-school-mission`.
- Console: https://console.cloud.google.com/welcome?project=imam-raza-school-mission
- Google Auth overview: https://console.cloud.google.com/auth/overview?project=imam-raza-school-mission

### OAuth consent configuration — PREPARED BUT NOT SUBMITTED
Entered app name `Imam Raza (a.s.) School Mission`, selected the account's contact/support email, selected audience **External**, and entered the account notification email.
Google Auth Platform requires account holder acceptance of **Google API Services: User Data Policy** at the final Finish step. This checkbox remains **unchecked**. OAuth consent configuration has therefore **not** been created, and no client credentials exist.
Owner must read and personally accept the policy, then click Continue/Create; the app is not yet ready for testing until the Google OAuth web application client and Supabase provider/redirect are configured.

OAuth Web Client redirect to configure after owner policy acceptance:
`https://zvtvpldmapcelkizrxtg.supabase.co/auth/v1/callback`
Supabase Android redirect allowlist:
`org.imamraza.schoolmission://login-callback`
Supabase: https://supabase.com/dashboard/project/zvtvpldmapcelkizrxtg/auth/providers

## Google Play Console — EXISTING DEVELOPER ACCOUNT CONFIRMED
- Authenticated developer account: **Artistic Sajid**, personal account.
- Console displayed an existing portfolio with six apps. No additional developer registration is necessary.
- Play Console application create form has:
  - Name `Imam Raza School Mission` (24 characters; maximum 30)
  - Package `org.imamraza.schoolmission` (**Google Play verified available**)
  - Language `en-US`
  - Type **App** (not Game)
  - Price **Free** (the app itself is free; sticker collections use Google Play in-app purchases)
- Form is **NOT SUBMITTED** because Play requires the account holder's certifications:
  1. Confirm application meets **Developer Program Policies**.
  2. Accept/certify **US export-law obligations**.
- Both checkboxes remain **unchecked**. Only the legal account holder, after personally reviewing the declarations and confirming their truth, should select them and submit app creation.
- Developer console: https://play.google.com/console/u/0/

## Next technical actions after Google & Play acceptance
1. Finish Google Auth consent configuration, add only standard Google sign-in scopes, create Web OAuth client, place client ID and secret in Supabase Google provider settings. Never commit the client secret or service credentials.
2. Add OAuth redirect, add a test user if in testing mode, perform end-to-end sign-in on the connected Pixel.
3. Once Play app created, create three one-time nonconsumable Play products: `little_learners`, `seeds_of_knowledge`, `bright_minds`.
4. Verify Play merchant and developer API account rights; configure account secrets in Supabase protected functions, add authenticated Pub/Sub push subscription.
5. Produce properly signed internal-testing AAB and test pending/cancel/refund/restore using licensed testers.
6. Obtain written Sindh/Pakistan legal advice on fundraising/promoter status, taxes and proceeds allocation before enabling any live purchase or school-funding representation.

**No Google API policy, Play developer policy or export-law certification was accepted by the assistant.** Neither live Google OAuth nor commercial checkout is active.

## Payment principle
Only Google Play Billing inside the native Android app; never a web or alternative processor for digital stickers. `commerce_enabled=false` and `google_signin_enabled=false` in Supabase until the release gates pass.
