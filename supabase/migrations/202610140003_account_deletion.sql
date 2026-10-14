alter table public.purchases alter column user_id drop not null;
alter table public.purchases drop constraint if exists purchases_user_id_fkey;
alter table public.purchases add constraint purchases_user_id_fkey
foreign key (user_id) references auth.users(id) on delete set null;
create index if not exists idx_purchases_user_id on public.purchases(user_id);
