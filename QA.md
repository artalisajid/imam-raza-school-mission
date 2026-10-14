# Quality Assurance & Release Status

**Audit date:** 2026-10-14
**Repository:** https://github.com/artalisajid/imam-raza-school-mission
**APK:** GitHub pre-release v0.1.0-preview

## Verified on Sajids-MacBook-Air
| Verification | Result |
|---|---|
| Kotlin / Android Gradle assembleDebug | PASS |
| Four JUnit tests | PASS (4/4) |
| Android Lint | PASS (0 errors, 11 warnings) |
| Target SDK configuration | 36 |
| App ID | org.imamraza.schoolmission |
| GitHub repository push | PASS |
| GitHub Pages privacy URL | PASS — HTTP 200 |
| Google Play payments | NOT ENABLED; checkout is explicitly blocked |
| Google sign-in | NOT ENABLED; provider credentials absent |
| Hosted Supabase project | CREATED — ap-south-1, connected to Android build |
| Public Supabase API milestone query | PASS — HTTP 200 |
| Hosted database security advisors | No WARN/ERROR; four INFO findings for intentionally private tables without client policies |
| Physical Pixel install, accessibility and gesture QA | NOT RUN — ADB found no connected Pixel |
| Emulator visual QA | NOT RUN — emulator installation requires a new Android SDK license acceptance |
| Play Console production approval | NOT SUBMITTED |
| Legal fundraising authorization | NOT VERIFIED |

## Checklist before publication
- Configure and test Supabase ownership, Auth, Postgres RLS and file access.
- Set up private account deletion request route and privacy contact before accounts.
- Complete original sticker content and licensing; current artwork is only an in-app preview.
- Create Play Console one-time products and enable Billing with server verification.
- Validate refund, pending-payment and purchase-restoration behavior end to end.
- Verify legal structure, business/fundraising claims, Google merchant eligibility, finance allocation policy and independent controls.
- Get actual Pixel/emulator access. Test font scaling, TalkBack, small screens, network failures, RTL, navigation, purchase edge cases, and screen orientation.
- Remove or address 11 lint warnings where feasible.
- Configure an organization-owned upload key; do not use the debug keystore for production.
- Update Play Store Data Safety, privacy, disclosures and published policy after any new processing goes live.

## Safety defaults
The preview does not collect donations or process Play payments, does not create supporter accounts, and does not declare fictional milestones or financial balances.
