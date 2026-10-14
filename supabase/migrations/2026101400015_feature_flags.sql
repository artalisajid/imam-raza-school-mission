-- Feature gates must remain disabled until Google's merchant/OAuth configuration
-- and necessary legal approval are independently confirmed.
create table if not exists public.app_flags (
 singleton boolean primary key default true check(singleton),
 commerce_enabled boolean not null default false,
 google_signin_enabled boolean not null default false,
 updated_at timestamptz not null default now()
);
insert into public.app_flags(singleton,commerce_enabled,google_signin_enabled)
values(true,false,false) on conflict(singleton) do nothing;
alter table public.app_flags enable row level security;
create policy "Everyone sees feature availability" on public.app_flags
 for select to anon,authenticated using(true);
revoke insert,update,delete on public.app_flags from anon,authenticated;
grant select on public.app_flags to anon,authenticated;
create unique index if not exists purchases_order_reference_unique
 on public.purchases(order_reference) where order_reference is not null;
create index if not exists entitlements_purchase_idx on public.entitlements(purchase_id);
