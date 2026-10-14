-- Imam Raza (a.s.) School Mission: preview schema. Apply only to an authorized Supabase project.
create extension if not exists pgcrypto;
create table if not exists public.profiles (
 id uuid primary key references auth.users(id) on delete cascade,
 display_name text check(char_length(display_name)<=100),
 preferred_language text not null default 'en',
 created_at timestamptz not null default now()
);
create table if not exists public.sticker_products (
 id text primary key, title text not null, description text not null,
 play_product_id text unique not null, preview_path text,
 sticker_count int not null check(sticker_count>0),
 published boolean not null default false,
 created_at timestamptz not null default now()
);
create table if not exists public.purchases (
 id uuid primary key default gen_random_uuid(),
 user_id uuid not null references auth.users(id),
 product_id text not null references public.sticker_products(id),
 order_reference text, currency text, price_minor bigint,
 status text not null check(status in('pending','verified','refunded','voided')),
 purchased_at timestamptz not null default now(),
 verified_at timestamptz, created_at timestamptz not null default now()
);
create table if not exists public.entitlements (
 user_id uuid not null references auth.users(id),
 product_id text not null references public.sticker_products(id),
 purchase_id uuid not null references public.purchases(id),
 granted_at timestamptz not null default now(),
 revoked_at timestamptz,
 primary key(user_id,product_id)
);
create table if not exists public.purchase_tokens (
 purchase_id uuid primary key references public.purchases(id) on delete cascade,
 token_sha256 text not null unique,
 created_at timestamptz not null default now()
);
create table if not exists public.project_milestones (
 id bigserial primary key, sort_order int not null unique,
 title text not null, summary text not null,
 status text not null default 'not_started' check(status in('not_started','planning','in_progress','verified_complete')),
 evidence_url text, verified_at timestamptz,
 created_at timestamptz not null default now()
);
create table if not exists public.project_updates (
 id uuid primary key default gen_random_uuid(),
 title text not null, summary text not null, body text not null default '',
 published boolean not null default false, created_at timestamptz not null default now()
);
create table if not exists public.public_financial_reports (
 id uuid primary key default gen_random_uuid(),
 period_start date not null, period_end date not null, currency text not null,
 gross_sales_minor bigint, deductions_minor bigint, net_settled_minor bigint,
 school_allocated_minor bigint, documented_expense_minor bigint,
 verified boolean not null default false, approved_at timestamptz,
 published_at timestamptz, created_at timestamptz not null default now(),
 check(period_end>=period_start)
);
create table if not exists public.financial_ledger (
 id uuid primary key default gen_random_uuid(), account_code text not null,
 debit_minor bigint not null default 0, credit_minor bigint not null default 0,
 currency text not null, source_type text not null, source_id text,
 evidence_path text, recorded_by uuid, approved_by uuid,
 created_at timestamptz not null default now(),
 check(debit_minor>=0 and credit_minor>=0)
);
create table if not exists public.admin_memberships (
 user_id uuid primary key references auth.users(id),
 role text not null check(role in('content_editor','finance_officer','finance_approver','owner'))
);
create table if not exists public.audit_events (
 id uuid primary key default gen_random_uuid(),
 actor_id uuid, event_name text not null, entity_type text not null,
 entity_id text, details jsonb not null default '{}'::jsonb,
 created_at timestamptz not null default now()
);

-- Access control: no client-side inserts to financial or commerce tables.
alter table public.profiles enable row level security;
alter table public.sticker_products enable row level security;
alter table public.purchases enable row level security;
alter table public.purchase_tokens enable row level security;
alter table public.entitlements enable row level security;
alter table public.project_milestones enable row level security;
alter table public.project_updates enable row level security;
alter table public.public_financial_reports enable row level security;
alter table public.financial_ledger enable row level security;
alter table public.admin_memberships enable row level security;
alter table public.audit_events enable row level security;

create policy "Profile owner reads" on public.profiles for select to authenticated using (id=auth.uid());
create policy "Profile owner updates" on public.profiles for update to authenticated using (id=auth.uid()) with check (id=auth.uid());
create policy "Published products" on public.sticker_products for select to anon,authenticated using (published=true);
create policy "Own purchases" on public.purchases for select to authenticated using (user_id=auth.uid());
create policy "Own entitlements" on public.entitlements for select to authenticated using (user_id=auth.uid());
create policy "Public milestones" on public.project_milestones for select to anon,authenticated using (true);
create policy "Approved updates" on public.project_updates for select to anon,authenticated using (published=true);
create policy "Published reconciled reports" on public.public_financial_reports for select to anon,authenticated using (verified=true and published_at is not null);

revoke insert,update,delete on public.sticker_products,public.purchases,public.purchase_tokens,public.entitlements,public.project_milestones,public.project_updates,public.public_financial_reports,public.financial_ledger,public.admin_memberships,public.audit_events from anon,authenticated;
revoke all on public.purchase_tokens,public.financial_ledger,public.admin_memberships,public.audit_events from anon,authenticated;
create index if not exists idx_purchases_user_date on public.purchases(user_id,purchased_at desc);
create index if not exists idx_updates_published_date on public.project_updates(published,created_at desc);

-- Automatic profile after OAuth sign-up, without allowing users to create others' profiles.
create or replace function public.create_new_profile()
returns trigger language plpgsql security definer set search_path=''
as $$
begin
 insert into public.profiles(id) values(new.id) on conflict do nothing;
 return new;
end;
$$;
drop trigger if exists on_auth_user_created_mission on auth.users;
create trigger on_auth_user_created_mission after insert on auth.users
for each row execute function public.create_new_profile();

insert into public.sticker_products(id,title,description,play_product_id,sticker_count,published) values
 ('little_learners','Little Learners','Educational illustrations','little_learners',12,false),
 ('seeds_of_knowledge','Seeds of Knowledge','Nature and learning','seeds_of_knowledge',20,false),
 ('bright_minds','Bright Minds','Science and creativity','bright_minds',30,false)
on conflict (id) do nothing;

insert into public.project_milestones(sort_order,title,summary,status) values
 (1,'Legal registration','Founding organization, governance and permissions','not_started'),
 (2,'Land acquisition','Karachi site feasibility, land use and registered purchase','not_started'),
 (3,'Design and approvals','Professional plans and regulatory permissions','not_started'),
 (4,'Construction','Safe and accessible educational facilities','not_started'),
 (5,'School readiness','Teaching staff, curriculum, equipment and registration','not_started'),
 (6,'School opening','Begin Nursery through Class 7 once approved','not_started')
on conflict (sort_order) do nothing;
