# App & Supabase Architecture — Development handoff

## Product
Adult-facing supporter app for a proposed school in Karachi. School model: Nursery–Class 7; Shia, Quranic and academic education; fees Rs. 3000/1500/0 per month. First milestone is registration, not land or construction. No registration, fund balance or launch date may be fabricated.

## Navigation
- Home: mission, primary sticker CTA, registration milestone, knowledge pillars, verified updates.
- School: vision, three pillars, Nursery–Class 7 curriculum, campus, tuition, admissions (future), safeguarding.
- Support: original collections, preview and purchase details, actual Google Play price, entitlement information.
- Journey: milestones, status, reconciled reports, documents and updates.
- Account: Google sign-in when configured, purchase history, sticker entitlements, privacy and deletion.

## Integration boundaries
- Kotlin + Jetpack Compose: user interface and local preferences.
- Supabase Auth: optionally create authenticated supporter account with Google OAuth.
- Postgres: profiles, products, purchases, entitlements, milestones, public updates, financial reports and access logs.
- Storage: public sticker previews; private paid sticker originals and unredacted source documents.
- Edge Functions: Google Play receipt verification, purchase acknowledgements, secure fulfillment, refund processing, financial publishing.
- Google Play Billing: purchases of genuine digital sticker products. Permanent collections are non-consumable; one purchase per SKU per account.
- Github Pages: public school project overview, privacy policy, deletion information.

## Security
- Public Supabase publishable key may be embedded in Android; never store secret/service-role credentials in the APK.
- Profiles and order histories require user-specific Row Level Security.
- Clients cannot INSERT/UPDATE purchases, entitlements, financial ledger or audit log.
- Edge Functions must authenticate caller JWT and verify purchases against Google Play Developer API.
- Enforce uniqueness by token hash and provider order, handle idempotency and refunds.
- Do not classify gross sales as school cash; reconcile actual settlements before publishing approved allocations.
- Administrative users must have restricted roles and independent financial approvals.
- No children's records in this supporter database.
- Secure deletion and retention procedures must be available before account creation is enabled.

## Backend activation checklist
1. Authenticated Supabase project under the correct account and organization.
2. Apply and review migration `supabase/migrations/202610140001_initial_schema.sql`.
3. Create RLS-tested public/private storage buckets.
4. Configure Google's OAuth provider and redirect URI.
5. Configure Play Console developer/merchant, non-consumable product IDs and API permissions.
6. Implement and deploy trusted purchase verification and refund webhooks.
7. Establish legitimate registration, merchant and fundraising positioning for public money-related claims.
8. Update privacy policy, Data Safety form and account deletion method for actual production behavior.
9. Complete live device, billing sandbox, accessibility, security and reconciliation QA.

## Not-live safeguards
The current debug app deliberately does not collect money, create accounts, or present fabricated financial metrics. All buy/login prompts state that external setup is required. This makes the build suitable for review, not a public commercial launch.
